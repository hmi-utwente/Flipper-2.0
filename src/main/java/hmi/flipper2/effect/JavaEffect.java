package hmi.flipper2.effect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.Is.ValueTransferType;
import hmi.flipper2.value.JavaValueList;

public class JavaEffect extends Effect {

	public enum CallMode { CALL_METHOD, CALL_FUNCTION };
	
	public enum ObjectMode { OBJECT_SINGLE, OBJECT_MULTI };
	
	protected static final Object[] emptyArgs = {};
	protected static final Class<?> emptyClassArgs[] = {};
	
	protected String	is_assign;
	protected String 	className;
	protected JavaValueList 	constructors;
	protected String 	callName;
	protected JavaValueList 	arguments;
	protected CallMode 	callmode;
	protected ObjectMode objectmode;
	
	protected ValueTransferType vt_type;
	
	protected Class<?>		classObject = null;
	protected Class<?>[]	paramTypes = null;
	protected Method		callMethod = null;
	protected Object		callObject = null;
	
	
	public JavaEffect(String is_assign, String is_type, String className, JavaValueList constructors, String callName, JavaValueList arguments, CallMode callMode,
			ObjectMode objectMode) throws FlipperException {
		try {
			this.is_assign = is_assign; 		
			this.vt_type = Is.transferType(is_type);
			this.className = className;
			this.constructors = (constructors != null) ? constructors : new JavaValueList();
			this.callName = callName;
			this.arguments = (arguments != null) ? arguments : new JavaValueList();
			this.callmode = callMode;
			this.objectmode = objectMode;
			//
			this.classObject = Class.forName(this.className);
			this.paramTypes = this.arguments.classArray();
			this.callMethod = this.classObject.getMethod(this.callName, this.paramTypes);
			if (this.callmode == CallMode.CALL_METHOD) {
				if (this.objectmode == ObjectMode.OBJECT_SINGLE)
					this.callObject = this.createObject();
			} else {
				this.callObject = this.classObject;
			}
		} catch (ClassNotFoundException | IllegalArgumentException | InvocationTargetException | InstantiationException
				| IllegalAccessException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isAssign() {
		return this.is_assign != null;
	}
	
	protected Object createObject() throws FlipperException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if (this.constructors.size() == 0) {
			return this.classObject.newInstance();
		} else {
			Class<?> ctypes[] = this.constructors.classArray();
			Constructor<?> dynConstructor = this.classObject.getConstructor(ctypes);
			return dynConstructor.newInstance(this.constructors.objectArray());
		}
	}
	
	@Override
	public Object doIt(Is is) throws FlipperException {
		Object return_obj =  executeCall(this.arguments.objectArray());
		if ( is_assign != null ) {
			if (this.vt_type == ValueTransferType.TYPE_OBJECT ) {
				is.assignObject2Js(is_assign, return_obj);
			} else if (this.vt_type == ValueTransferType.TYPE_JSONSTRING ) {
				is.assignJSONString(is_assign, (String)return_obj);
			} else {
				throw new RuntimeException("UNEXPECTED");
			}
		}
		return return_obj;
	}
	
	protected Object executeCall(Object[] method_args) throws FlipperException {
		try {
			if (this.objectmode == ObjectMode.OBJECT_MULTI)
				this.callObject = this.createObject();
			return this.callMethod.invoke(this.callObject, method_args);
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			StringBuilder sb=new StringBuilder();
			sb.append("[");
			for(Object o : method_args) {
				sb.append(o.getClass().getName());
				sb.append(" ");
			}
			sb.append("]");
			throw new FlipperException(e,
					"JavaCall failed:JavaEffect:executeCall: " + this.toString() + "\n" + "Arguments=" + sb);
		}
	}
	
	public String toString() {
		return "JavaEffect["+"name="+this.callName+", className="+this.className+"]";
	}
	
}
