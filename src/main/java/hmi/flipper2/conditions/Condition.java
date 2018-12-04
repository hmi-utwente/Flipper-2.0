package hmi.flipper2.conditions;

import hmi.flipper2.FlipperException;
import hmi.flipper2.FlipperObject;
import hmi.flipper2.Is;

public abstract class Condition extends FlipperObject {

	Condition(String id) {
		super(id);
	}
	
	public abstract boolean checkIt(Is is) throws FlipperException;
	
}
