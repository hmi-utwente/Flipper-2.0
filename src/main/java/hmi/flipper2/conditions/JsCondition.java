package hmi.flipper2.conditions;

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
		boolean res =  is.condition(this.condition);
		if ( is.tc.fd != null )
			is.tc.fd.precondition(id, this.condition, res);
		return res;
	}

}
