package hmi.flipper2.conditions;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class JsCondition extends Condition {

	private String condition;
	
	public JsCondition(String condition) {
		this.condition = condition;
	}
	
	@Override
	public boolean checkIt(Is is) throws FlipperException {
		// TODO Auto-generated method stub
		return is.condition(this.condition);
	}

}
