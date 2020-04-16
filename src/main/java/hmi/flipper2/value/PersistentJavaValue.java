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

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.Template;

public class PersistentJavaValue extends JavaValue {

	private Is	is;
	private Class<?> classObject;
	private String name;
	
	public PersistentJavaValue(Template template, String className, String name) throws FlipperException {
		this.is = template.tf.tc.is;
		this.name = name;
		//
		if ( className == null )
			throw new FlipperException("PersistentJavaValue: no \"class\" for persistent: "+name);
		try {
			this.classObject = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new FlipperException(e);
		}
	}
	
	private Object tableObject() throws FlipperException {
		Object res = is.getPersistent(this.name);
		
		if ( res == null)
			throw new RuntimeException("PersistentJavaValue: unknown persistent object: "+this.name);
		return res;
	}
	
	@Override
	public Object getObject() throws FlipperException {
		return tableObject();
	}

	@Override
	public Class<?> objectClass() throws FlipperException {
		// return tableObject().getClass();
		return classObject;
	}
}