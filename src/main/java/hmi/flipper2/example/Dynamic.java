/*******************************************************************************
 * Copyright (C) 2017-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Flipper-2.0 Dialogue Control project.
 *
 * Flipper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flipper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flipper.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.flipper2.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hmi.flipper2.environment.BaseFlipperEnvironment;
import hmi.flipper2.environment.FlipperEnvironmentMessageJSON;
import hmi.flipper2.environment.IFlipperEnvironment;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class Dynamic extends BaseFlipperEnvironment {

	private final static boolean verbose = false;

	private ObjectMapper mapper;

	private Double base;

	public Dynamic() {
		if (verbose)
			logger.info("Dynamic() called");
		this.base = 0.0;
	}

	@Override
	public FlipperEnvironmentMessageJSON onMessage(FlipperEnvironmentMessageJSON fenvmsg) throws Exception {
		switch(fenvmsg.cmd){
			case "Double":
				Double one = fenvmsg.params.get("content").get("one").asDouble();
				Double two = fenvmsg.params.get("content").get("two").asDouble();
				JsonNode res = mapper.createObjectNode().put("f",this.fplus(one,two));
				enqueueMessage(res,"f",fenvmsg.msgId);
				break;
			default:
				logger.error("No appropriate CMD was given: {}",fenvmsg.cmd);
		}
		return null;
	}

	@Override
	public void setRequiredEnvironments(IFlipperEnvironment[] envs) throws Exception {

	}

	@Override
	public void init(JsonNode params) {
		this.mapper = new ObjectMapper();
	}

	public Dynamic(Double base) {
		if (verbose)
			logger.info("Dynamic(Double(" + base + ")) called");
		this.base = base;
	}

	public static Boolean alwaysTrue() {
		if (verbose)
			logger.info("Dynamic:method:alwaysTrue(" + "" + ") called.");
		return Boolean.TRUE;
	}

	public static void f(String s) {
		if (verbose)
			logger.info("Dynamic:static method:f(String(" + s + ")) called.");
	}

	public static void f(Double d) {
		if (verbose)
			logger.info("Dynamic:static method:f(Double(" + d + ")) called.");
	}

	public Double fplus(Double dl, Double dr) {
		if (verbose)
			logger.info("Dynamic:static method:f(Double(" + dl + "," + dr + ")) called.");
		return base + dl + dr;
	}
	
	public Double recur(Double dl, Dynamic dyn) {
		if (verbose)
			logger.info("Dynamic: method:recur(" + dl + ", obj.base=" + dyn.base  + ")) called.");
		return base + dl;
	}

	public String fjson(String json) {
		if (verbose)
			logger.info("Dynamic:static method:fjson(json=" + json + ") called.");
		JsonObject jso = string2json(json);
		JsonObject jso_counter = jso.getJsonObject("counter");
		JsonValue jso_value = jso_counter.get("value");

		JsonObject returnObject = Json.createObjectBuilder().add("extraction", "example").add("value", jso_value)
				.build();
		return returnObject.toString();
	}

	public static JsonObject string2json(String s) {
		JsonReader reader = Json.createReader(new StringReader(s));
		JsonObject result = reader.readObject();
		reader.close();
		return result;
	}

}
