package hmi.flipper2.effect;

import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.javascript.JsExpression;

public class AssignEffect extends Effect {

	private String var;
	private String expr;
	private JsExpression js_expr = null;
	
	public AssignEffect(String id, String var, String expr) {
		super(id);
		this.var = var;
		this.expr = expr;		
	}
	
	public Object doIt(Is is) throws FlipperException {
		if ( this.js_expr == null )
			this.js_expr = new JsExpression(is, this.var + "=" + expr, false);
		is.assignJsExpression(this.var, this.js_expr);
		return null;
	}
	
}
