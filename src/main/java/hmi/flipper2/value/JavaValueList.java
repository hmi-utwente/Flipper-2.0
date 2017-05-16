package hmi.flipper2.value;

import java.util.ArrayList;

import hmi.flipper2.FlipperException;

public class JavaValueList extends ArrayList<JavaValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object[] objectArray() throws FlipperException {
		Object res[] = new Object[this.size()];
		
		for(int i=0; i<this.size(); i++) {
			res[i] = this.get(i).getObject();
		}
		return res;
	}
	
	public Class<?>[] classArray() throws FlipperException {
		Class<?> res[] = new Class<?>[this.size()];
		
		for(int i=0; i<this.size(); i++) {
			res[i] = this.get(i).objectClass();
		}
		return res;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (JavaValue v : this) {
			if ( sb.length() > 0 )
				sb.append(',');
			try {
				sb.append( v.objectClass().getName());
			} catch (Exception e) {
				sb.append( e.toString());
			}
		}
		sb.append("]");
		return "[" + sb + "]";
	}
	
}
