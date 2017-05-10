package hmi.flipper2.effect;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class AssignEffect extends Effect {

	private String var;
	private String expr;
	
	public AssignEffect(String var, String expr) {
		this.var = var;
		this.expr = expr;
		
	}
	
	public Object doIt(Is is) throws FlipperException {
		is.assignJavascript(var, expr);
		return null;
	}
	
}
