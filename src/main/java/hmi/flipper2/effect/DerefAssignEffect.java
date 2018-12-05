package hmi.flipper2.effect;

import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class DerefAssignEffect extends Effect {

	private String var;
	private String expr;
	
	public DerefAssignEffect(String id, String var, String expr) {
		super(id);
		this.var = var;
		this.expr = expr;
		
	}
	
	public Object doIt(Is is) throws FlipperException {
		is.assignDerefJavascript(var, expr);
		return null;
	}
	
}
