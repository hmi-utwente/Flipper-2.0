package hmi.flipper2.environment;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;

public interface IFlipperEnvironment {

	public void setRequiredEnvironments(IFlipperEnvironment[] envs) throws Exception;
	public void init(JsonNode params) throws Exception;
	public void init(JsonNode params, Connection connection) throws Exception;
	public FlipperEnvironmentMessageJSON onMessage(FlipperEnvironmentMessageJSON fenvmsg) throws Exception;
	
	public boolean hasMessage();
	public FlipperEnvironmentMessageJSON getMessage() throws Exception;
	
	public String getId();
	public void setId(String id);
}

