package hmi.flipper2.conditions;

import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class JsCondition extends Condition {

	private String condition;
	
	public JsCondition(String id, String condition) {
		super(id);
		this.condition = condition;
	}
	
	@Override
	public boolean checkIt(Is is) throws FlipperException {
		return is.condition(this.condition);
	}

}
