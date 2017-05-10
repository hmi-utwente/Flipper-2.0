package hmi.flipper2.value;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.Is.ValueTransferType;

public class IsJavaValue extends JavaValue {

	private Is is;
	private String path;	
	private ValueTransferType vt_type;
	private Class<?> cl;
	
	public IsJavaValue(Is is, String path, String type, String class_str) throws FlipperException {
		this.is = is;
		this.path = path;
		this.vt_type = Is.transferType(type);
		this.cl = (class_str == null)? null : name2class(class_str);
	}
	
	@Override
	public Object getObject() throws FlipperException {
		if ( this.vt_type == ValueTransferType.TYPE_OBJECT ) {
			Object res = is.eval(path);
			// System.out.println("RETCLASS="+res.getClass().getName());
			return res;
		} else if (this.vt_type == ValueTransferType.TYPE_JSONSTRING )
			return is.getJSONfromJs(path);
		else 
			throw new RuntimeException("UNEXPECTED");
	}

	@Override
	public Class<?> objectClass() throws FlipperException {
		if (this.vt_type == ValueTransferType.TYPE_JSONSTRING)
			return String.class;
		else if (this.cl != null ) {
			// System.out.println("DEFCLASS="+cl.getName());
			return this.cl;
		} else
			throw new RuntimeException("Should define class for is="+path+", is_type: Object. Dynamic calls implemented in future");
	}
}
