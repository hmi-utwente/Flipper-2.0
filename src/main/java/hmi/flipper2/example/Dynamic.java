package hmi.flipper2.example;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class Dynamic {

	private Double base;
	
	public Dynamic() {
		System.out.println("Dynamic() called");
		this.base = 0.0;
	}
	
	public Dynamic(Double base) {
		System.out.println("Dynamic(Double("+base+")) called");
		this.base = base;
	}
	
	public static Boolean alwaysTrue() {
		System.out.println("Dynamic:method:alwaysTrue("+""+") called.");
		return Boolean.TRUE;
	}
	
	public static void f(String s) {
		System.out.println("Dynamic:static method:f(String("+s+")) called.");
	}
	
	public static void f(Double d) {
		System.out.println("Dynamic:static method:f(Double("+d+")) called.");
	}
	
	public Double fplus(Double dl, Double dr) {
		System.out.println("Dynamic:static method:f(Double("+dl+","+dr+")) called.");
		return base + dl + dr;
	}
	
	public String fjson(String json) {
		System.out.println("Dynamic:static method:fjson(json="+json+") called.");
		JsonObject jso = string2json(json);
		JsonObject jso_counter = jso.getJsonObject("counter");
		JsonValue jso_value = jso_counter.get("value");
		
		JsonObject returnObject = Json.createObjectBuilder().add("extraction", "example").add("value", jso_value).build();
		return returnObject.toString();
	}
	
	public static JsonObject string2json(String s) {
		JsonReader reader = Json.createReader(new StringReader(s));
		JsonObject result = reader.readObject();
		reader.close();
		return result;
	}
	
}
