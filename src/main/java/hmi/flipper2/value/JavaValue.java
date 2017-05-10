package hmi.flipper2.value;

import java.util.HashMap;
import java.util.Map;

import hmi.flipper2.FlipperException;

public abstract class JavaValue {

		public abstract Object getObject() throws FlipperException;
		
		public abstract Class<?> objectClass() throws FlipperException;
		
		protected static Map<String,String> aliases = class_aliases();
			
		private static final Map<String,String> class_aliases() {
			Map<String, String> aliases = new HashMap<String,String>();
			
			aliases.put("String", "java.lang.String");
			aliases.put("Double", "java.lang.Double");
			aliases.put("Boolean", "java.lang.Boolean");
			aliases.put("Integer", "java.lang.Integer");
			return aliases;
		}
				
		protected static final Class<?> name2class(String name) throws FlipperException {
				try {
					String alias = aliases.get(name);
					
					return Class.forName((alias==null)?name:alias);
				} catch (ClassNotFoundException e) {
					throw new FlipperException(e);
				}
		}
		
}
