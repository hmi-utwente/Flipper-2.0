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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import hmi.flipper2.FlipperException;

public class ConstantJavaValue extends JavaValue {

	private String		class_str; // constructor is String Object, class must have String Constructor
	private Class<?>	class_obj;
	private String 		str_value;
	private Object		obj_value;
	
	public ConstantJavaValue(String class_str, String str_value) throws FlipperException {
		this.class_str = class_str;
		this.str_value = str_value;
		if ( this.class_str == null )
			this.class_obj = this.str_value.getClass();
		else
			this.class_obj = name2class(this.class_str);	
		obj_value = convertString2Object(this.class_obj, this.str_value);
	}
	
	private static final Class<?> stringClassList[] = {String.class};
	
	private static Object convertString2Object(Class<?> c, String s) throws FlipperException {
		try {
			Constructor<?> dynConstructor = c.getConstructor(stringClassList);
			return dynConstructor.newInstance(s);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new FlipperException(e);
		}
	}
	
	
	@Override
	public Object getObject() throws FlipperException {
		return obj_value;
	}
	
	@Override
	public Class<?> objectClass() throws FlipperException {
		return this.class_obj;
	}

}
