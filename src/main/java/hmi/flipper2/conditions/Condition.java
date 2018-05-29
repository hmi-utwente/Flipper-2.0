package hmi.flipper2.conditions;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public abstract class Condition {

	public String id;
	
	Condition(String id) {
		this.id = id;
	}
	
	public abstract boolean checkIt(Is is) throws FlipperException;
	
}
