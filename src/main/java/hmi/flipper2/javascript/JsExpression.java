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
package hmi.flipper2.javascript;

import hmi.flipper2.TemplateController;
import hmi.flipper2.TemplateFile;
import hmi.flipper2.dataflow.DataFlow;

import java.util.HashSet;
import java.util.Set;

import hmi.flipper2.Config;
import hmi.flipper2.FlipperException;

public class JsExpression {

	private static long fcnt = 10000;
	
	private String fid = null;
	private JsEngine jse;
	public  String expr;
	public  TemplateController tc;
	
	public JsExpression(JsEngine jse, String args, String expr, String format) throws FlipperException {
		this.jse = jse;
		this.expr = expr;
		this.fid = "_f" + fcnt++;
		String fundef = "var " + this.fid + " = function(" + args + ") { " + String.format(format, expr) + "; };";
		// System.out.println("FUNDEF: "+fundef);
		jse.eval(fundef);

	}
	
	private Object _eval() throws FlipperException {
		try {
			return jse.invocable.invokeFunction(this.fid);
		} catch (Exception e) {
			throw new FlipperException(e);
		}

	}
	
	private Object _eval(String arg) throws FlipperException {
		try {
			return jse.invocable.invokeFunction(this.fid,arg);
		} catch (Exception e) {
			throw new FlipperException(e);
		}

	}
	
	private Object _eval(String arg1, String arg2) throws FlipperException {
		try {
			return jse.invocable.invokeFunction(this.fid,arg1,arg2);
		} catch (Exception e) {
			throw new FlipperException(e);
		}

	}

	public void eval_void() throws FlipperException {
		_eval();
	}
	
	public Object eval_object() throws FlipperException {
		return _eval();
	}
	
	public void eval_void(String arg1, String arg2) throws FlipperException {
		_eval(arg1,arg2);
	}
	
	public boolean eval_boolean() throws FlipperException {
		Object retval = _eval();
		if (retval != null) {
			try {
				return ((Boolean) retval).booleanValue();
			} catch (ClassCastException e) {
			}				
		}
		throw new FlipperException("Condition not Boolean: " + this.expr);
	}
	
	public String eval_string(String arg) throws FlipperException {
		Object retval = _eval(arg);
		if (retval != null)
			return retval.toString();
		else
			return null;
	}
	
	public Set<String> extractRefs() {
		return DataFlow.extractRefs(this.expr);
	}
	
}
