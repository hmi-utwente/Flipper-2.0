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

import java.util.ArrayList;
import java.util.Set;

import hmi.flipper2.FlipperException;
import hmi.flipper2.dataflow.DataFlow;
import hmi.flipper2.effect.Effect;

public class JavaValueList extends ArrayList<JavaValue> implements DataFlow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object[] objectArray() throws FlipperException {
		Object res[] = new Object[this.size()];
        for(int i=0; i<this.size(); i++) {
            switch(this.get(i).objectClass().getName()){
                case "java.lang.Double" :
                    res[i] = Double.parseDouble(this.get(i).getObject().toString());
                    break;
                case "java.lang.Integer" :
                    res[i] = Integer.parseInt(this.get(i).getObject().toString());
                    break;
                default :
                    res[i] = this.get(i).getObject();
                    break;
            }
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
	
	public Set<String> flowIn() {
		Set<String> res = DataFlow.EMPTY;
		for(JavaValue c: this)
			res = DataFlow.union(res, c.flowIn());
		return res;
	}
	
	public Set<String> flowOut() {
		Set<String> res = DataFlow.EMPTY;
		for(JavaValue c: this)
			res = DataFlow.union(res, c.flowOut());
		return res;
	}
}
