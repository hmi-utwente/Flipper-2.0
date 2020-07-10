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

import java.util.HashSet;
import java.util.Set;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;
import hmi.flipper2.Is.ValueTransferType;
import hmi.flipper2.dataflow.DataFlow;
import hmi.flipper2.javascript.JsExpression;

import static hmi.flipper2.TemplateController.logger;

public class IsJavaValue extends JavaValue {

	private Is is;
	private String path;	
	private JsExpression js_expr;
	private ValueTransferType vt_type;
	private Class<?> cl;
	
	public IsJavaValue(Is is, String path, String type, String class_str) throws FlipperException {
		this.is = is;
		this.path = path;
		this.js_expr = new JsExpression(is,"",path,"return %s");
		this.vt_type = Is.transferType(type);
		this.cl = (class_str == null)? null : name2class(class_str);
	}
	
	@Override
	public Object getObject() throws FlipperException {
		// INCOMPLETE, should be implemented with JsExpressions
		if ( this.vt_type == ValueTransferType.TYPE_OBJECT ) {
			// Object res = is.eval(path);
			return js_expr.eval_object();
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
			logger.debug("DEFCLASS="+cl.getName());
			return this.cl;
		} else
			throw new RuntimeException("Should define class for is="+path+", is_type: Object. Dynamic calls implemented in future");
	}
	
	public Set<String> flowIn() {
		HashSet<String> res = new HashSet<String>();
		res.add(this.path);
		return res;

	}
}
