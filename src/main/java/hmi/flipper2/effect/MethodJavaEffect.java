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
package hmi.flipper2.effect;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Template;
import hmi.flipper2.value.JavaValueList;

public class MethodJavaEffect extends JavaEffect {
	
	public MethodJavaEffect(String id, Template template, String is_assign, String is_type, String className, String persistent, JavaValueList constructors, String functionName, JavaValueList arguments, String objectMode)
			throws FlipperException {
		super(id, template, is_assign, is_type, className, persistent, constructors, functionName, arguments, CallMode.CALL_METHOD, decode_mode(objectMode));
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