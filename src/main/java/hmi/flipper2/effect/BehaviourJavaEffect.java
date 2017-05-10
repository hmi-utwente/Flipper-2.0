package hmi.flipper2.effect;

import hmi.flipper2.FlipperException;
import hmi.flipper2.value.JavaValueList;

public class BehaviourJavaEffect extends MethodJavaEffect {
	
	public BehaviourJavaEffect(String is_assign, String is_type, String className, JavaValueList constructors, String functionName, JavaValueList arguments, String objectMode)
			throws FlipperException {
		super(is_assign, is_type, className, constructors, functionName, arguments, objectMode);
		
		if ( is_assign!=null || is_type!=null ) {
			throw new FlipperException("Behaviour Effects cannot change the Information State");
		}
	}
	
}
