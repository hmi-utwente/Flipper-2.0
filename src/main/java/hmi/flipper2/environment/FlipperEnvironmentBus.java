package hmi.flipper2.environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/* Example environmentSpec:
{
	"environments": [
		   { 	
			"id":"bml", "loader":"eu.couch.hmi.environments.BMLEnvironment", "requiredloaders": [],
			"params": {
				"publishBmlFeedback": true,
				"middleware": {
					"loaderClass": "nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader",
					"properties": {
						"iTopic": "COUCH/BML/FEEDBACK/ASAP",
						"oTopic": "COUCH/BML/REQUEST/ASAP",
						"amqBrokerURI": "tcp://localhost:61616"
					}
				}
			}
		}, {
			"id":"dgep", "loader":"eu.couch.hmi.environments.DGEPEnvironment", "requiredloaders": [],
			"params": {
				"dgepCtrlMiddleware": {
					"loaderClass": "nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader",
					"properties": {
						"iTopic": "DGEP/response",
						"oTopic": "DGEP/requests",
						"amqBrokerURI": "tcp://localhost:61616"
					}
				},
				"dgepMovesMiddleware": {
					"loaderClass": "nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader",
					"properties": {
						"iTopic": "DGEP/dialogue_moves",
						"oTopic": "DGEP/requests",
						"amqBrokerURI": "tcp://localhost:61616"
					}
				}
			}
		}, {
			"id":"flipperintents", "loader":"eu.couch.hmi.environments.FlipperIntentPlannerEnvironment", "requiredloaders": [ "bml" ],
			"params": {}
		}, {
			"id":"ui", "loader":"eu.couch.hmi.environments.UIEnvironment", "requiredloaders": [ "dgep" ],
			"params": {
				"middleware": {
					"loaderClass": "nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader",
					"properties": {
						"iTopic": "COUCH/UI/REQUESTS",
						"oTopic": "COUCH/UI/STATE",
						"amqBrokerURI": "tcp://localhost:61616"
					}
				}
			}
		}, {
			"id":"dialogueloader", "loader":"eu.couch.hmi.environments.DialogueLoaderEnvironment", "requiredloaders": [ "dgep", "flipperintents", "ui", "bml" ],
			"params": {}
		}
	]
}
*/

class EnvironmentSpec {
	public EnvironmentLoaderSpec[] environments;
}

class EnvironmentLoaderSpec {
	public String id;
	public String loader;
	public String[] requiredloaders;
	public JsonNode params;
}

public class FlipperEnvironmentBus {
	
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(FlipperEnvironmentBus.class.getName());
	
	private Map<String, IFlipperEnvironment> environments;
	private ObjectMapper om;
	private Connection connection;
	
	public FlipperEnvironmentBus() {
		environments = new HashMap<>();
		om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public FlipperEnvironmentBus(Connection connection){
		this();
		this.connection = connection;
	}
	
	
	public boolean init(String specString) {
		try {
			JsonNode jn = om.readTree(specString);
			EnvironmentSpec spec = om.treeToValue(jn, EnvironmentSpec.class);
			loadSpec(spec);
		} catch (IOException | EnvironmentLoadException e) {
			logger.error("Failed to init FlipperEnvironmentBus, invalid spec? ", e);
			return false;
		}
		logger.info("Initialized with "+environments.size()+" environments.");
		return true;
	}
	
	public boolean hasMessages() {
		for (IFlipperEnvironment env : environments.values()) {
			if (env.hasMessage()) return true;
		}
		return false;
	}
	
	public String getMessages() {
		List<FlipperEnvironmentMessageJSON> res = new ArrayList<FlipperEnvironmentMessageJSON>();
		for (IFlipperEnvironment env : environments.values()) {
			while (env.hasMessage()) {
				try {
					res.add(env.getMessage());
				} catch (Exception e) {
					logger.warn("Failed to get message from environment: "+env.getId(), e);
				}
			}
		}

		try {
			return om.writeValueAsString(res.toArray(new FlipperEnvironmentMessageJSON[0]));
		} catch (JsonProcessingException e) {
			logger.warn("Failed to return queue messages from environments ", e);
		}
		return "[]";
	}
	
	public String sendMessages(String msgsJson) {
		logger.debug("received messages:\n "+msgsJson);
		List<FlipperEnvironmentMessageJSON> res = new ArrayList<FlipperEnvironmentMessageJSON>();
		try {
			JsonNode jn = om.readTree(msgsJson);
			FlipperEnvironmentMessageJSON[] msgs = om.treeToValue(jn, FlipperEnvironmentMessageJSON[].class);
			for (FlipperEnvironmentMessageJSON msg : msgs) {
				FlipperEnvironmentMessageJSON _res = sendMessage(msg);
				if (_res != null) res.add(_res);
			}
		} catch (IOException e) {
			logger.error("Failed to process messages "+msgsJson, e);
		}

		try {
			return om.writeValueAsString(res.toArray(new FlipperEnvironmentMessageJSON[0]));
		} catch (Exception e) {
			logger.warn("Failed to return responses from environments ", e);
		}

		return "[]";
	}
	
	public FlipperEnvironmentMessageJSON sendMessage(FlipperEnvironmentMessageJSON msg) {
		FlipperEnvironmentMessageJSON _result = null;
		String targetEnv = "";
		if (environments.containsKey(msg.environment)) {
			targetEnv = msg.environment;
			try {
				logger.debug("Forwarding message of type {} to environment {}.",msg.cmd,msg.environment);
				_result = environments.get(targetEnv).onMessage(msg);
			} catch (Exception e) {
				logger.error("Environment "+targetEnv+" failed (error: {}) to handle message: {}", e, msg);
				e.printStackTrace();
			}
		} else {
			logger.warn("Environment ID unknown: {}", msg.environment);
		}
		return _result;
	}
	
	public String sendMessage(String msgJson) {
		String result = "{}";
		try {
			JsonNode msgNode = om.readTree(msgJson);
			FlipperEnvironmentMessageJSON msg = om.treeToValue(msgNode,FlipperEnvironmentMessageJSON.class);
			FlipperEnvironmentMessageJSON _result = sendMessage(msg);
			if (_result != null) {
				result = om.writeValueAsString(result);
			}
		} catch (IOException e) {
			logger.error("Failed to process message request: ", e, msgJson);
			result = "{}";
		}
		
		return result;
	}
	
	public void loadSpec(EnvironmentSpec spec) throws EnvironmentLoadException {
		for (EnvironmentLoaderSpec lspec : spec.environments) {
			try {
                            if (environments.containsKey(lspec.id)) {
                                throw new EnvironmentLoadException("Duplicate environment id: "+lspec.id);
                            }                            
                            IFlipperEnvironment env = instantiateEnvironment(lspec.loader);
                            env.setId(lspec.id);
                            List<IFlipperEnvironment> requiredEnvs = new ArrayList<IFlipperEnvironment>();
                            for (String envId : lspec.requiredloaders) {
                                if (!environments.containsKey(envId))
                                    throw new EnvironmentLoadException("Required Loader ID unknown: "+envId);
                                requiredEnvs.add(environments.get(envId));
                            }
                            try {
                                env.setRequiredEnvironments(requiredEnvs.toArray(new IFlipperEnvironment[0]));
								if(this.connection != null){
									env.init(lspec.params,this.connection);
								}
								else{
									env.init(lspec.params);
								}
                            } catch (Exception e) {
                                System.out.println("E: " + e.getMessage());
                                throw new EnvironmentLoadException("Failed to init environment "+lspec.id+": "+e.getMessage());
                            }
                            logger.debug("Initialized environment: "+lspec.loader+" ("+env.getId()+")");
                            environments.put(lspec.id, env);
                        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
				Logger.getLogger(FlipperEnvironmentBus.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public IFlipperEnvironment instantiateEnvironment(final String className) throws EnvironmentLoadException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
	    try{                
	        return IFlipperEnvironment.class.cast(Class.forName(className).getDeclaredConstructor().newInstance());
	    } catch(InstantiationException
	          | IllegalAccessException
	          | ClassNotFoundException e) {
	        throw new EnvironmentLoadException("Failed to instantiate FlipperEnvironment with classname "+className+": "+e.getMessage());
	    }
	}
	
}

class EnvironmentLoadException extends Exception {
	private static final long serialVersionUID = 8290847513890592695L;

	public EnvironmentLoadException(String errorMessage) {
        super(errorMessage);
    }
}