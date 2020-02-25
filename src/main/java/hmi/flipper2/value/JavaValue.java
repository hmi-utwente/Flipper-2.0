/*******************************************************************************
 * Copyright (C) 2017-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Flipper-2.0 Dialogue Control project.
 *
 * Flipper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flipper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flipper.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.flipper2.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import hmi.flipper2.FlipperException;
import hmi.flipper2.dataflow.DataFlow;

public abstract class JavaValue implements DataFlow {

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
		
		public Set<String> flowIn() {
			return DataFlow.EMPTY;
		}
		
		public Set<String> flowOut() {
			return DataFlow.EMPTY;
		}
		
}
