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
package hmi.flipper2.sax;

import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;

public class SimpleElement {

	public String tag;
	public HashMap<String,String> attr;
	public Vector<SimpleElement> children;
	public StringBuffer characters;
	
	
    public SimpleElement(String tag, Attributes attr) {
    	this.tag = tag;
    	this.attr = new HashMap<String, String>();
    	if ( attr != null )
    		for (int i = 0; i < attr.getLength(); i++) {
    			this.attr.put(attr.getQName(i), attr.getValue(i));
    	}
    	this.children = new Vector<SimpleElement>();
    	this.characters =  new StringBuffer();
    }
    
    public void addChild(SimpleElement ch) {
    	this.children.addElement(ch);
    }
    
    public void addCharacters(char ch[], int start, int length) {
    	this.characters.append(ch, start, length);
    }
    
	public String toString() {
		StringBuffer b = new StringBuffer();

		b.append("<" + tag);
		for (String key : this.attr.keySet()) {
		    b.append(',');
		    b.append(key);
		    b.append('=');
		    b.append('"');
		    b.append(attr.get(key));
		    b.append('"');
		}
		b.append(",children{");
		for (int j = 0; j < this.children.size(); j++) {
			b.append(" " + this.children.get(j).tag);
		}
		b.append(" }");
		b.append(">");
		return b.toString();
	}
    
}
