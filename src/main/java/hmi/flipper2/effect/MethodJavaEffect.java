package hmi.flipper2.effect;

import hmi.flipper2.FlipperException;
import hmi.flipper2.value.JavaValueList;

public class MethodJavaEffect extends JavaEffect {
	
	public MethodJavaEffect(String is_assign, String is_type, String className, JavaValueList constructors, String functionName, JavaValueList arguments, String objectMode)
			throws FlipperException {
		super(is_assign, is_type, className, constructors, functionName, arguments, CallMode.CALL_METHOD, decode_mode(objectMode));
	}
	
	public static final ObjectMode decode_mode(String mode) throws FlipperException {
		if ( mode == null )
			return ObjectMode.OBJECT_SINGLE;
		else if (mode.equals("single"))
			return ObjectMode.OBJECT_SINGLE;
		else if (mode.equals("multi"))
			return ObjectMode.OBJECT_MULTI;
		else
			throw new FlipperException("Unknown call mode: "+mode);
	}
	
}