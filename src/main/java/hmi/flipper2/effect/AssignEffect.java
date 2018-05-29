package hmi.flipper2.effect;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class AssignEffect extends Effect {

	private String var;
	private String expr;
	
	public AssignEffect(String id, String var, String expr) {
		super(id);
		this.var = var;
		this.expr = expr;
		
	}
	
	public Object doIt(Is is) throws FlipperException {
		if ( is.tc.fd != null )
			is.tc.fd.effect(id, var + " = " + expr);
		is.assignJavascript(var, expr);
		return null;
	}
	
}
