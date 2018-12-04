package hmi.flipper2.javascript;

import hmi.flipper2.TemplateController;
import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;

public class JsExpression {

	private static long fcnt = 10000;
	
	private String fid = null;
	private JsEngine jse;
	public String expr;
	public  TemplateController tc;
	
	public JsExpression(JsEngine jse, String expr) throws FlipperException {
		this.jse = jse;
		this.expr = expr;
		//
		if ( Config.JS_ENCAPSULATE_EXPR ) {
			this.fid = "_f"+fcnt++;
			String fdef = "var "+this.fid+" = function() { return "+expr+"; };";
			jse.eval(fdef);
		}
	}
	
	private Object _eval() throws FlipperException {
		if ( Config.JS_ENCAPSULATE_EXPR ) {
			try {
				return jse.invocable.invokeFunction(this.fid);
			} catch (Exception e) {
				throw new FlipperException(e);
			}
		} else {
			return jse.eval(this.expr);
		}
	}
	
	public boolean eval_boolean() throws FlipperException {
		Object retval = _eval();
		if (retval != null) {
			try {
				return ((Boolean) retval).booleanValue();
			} catch (ClassCastException e) {
			}				
		}
		throw new FlipperException("Condition not Boolean: " + this.expr);
	}
	
}
