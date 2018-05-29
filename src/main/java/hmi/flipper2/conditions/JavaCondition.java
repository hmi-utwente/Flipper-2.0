package hmi.flipper2.conditions;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.effect.JavaEffect;

public class JavaCondition extends Condition {

	private JavaEffect booleanEffect;
	
	public JavaCondition(String id, JavaEffect booleanEffect) throws FlipperException {
		super(id);
		if ( booleanEffect.isAssign() )
			throw new FlipperException("JavaCondition:effect cannot be is_assign: "+booleanEffect);
		this.booleanEffect = booleanEffect;
		
	}
	
	@Override
	public boolean checkIt(Is is) throws FlipperException {
		Object b = booleanEffect.doIt(is);
		
		try {
			boolean res = ((Boolean)b).booleanValue();
			if ( is.tc.fd != null )
				is.tc.fd.precondition(id, this.booleanEffect.toString(), res);
			return res;
		} catch (ClassCastException e) {
			throw new FlipperException("JavaCondition: condition must be boolean: "+booleanEffect.toString());
		}
		
	}

}