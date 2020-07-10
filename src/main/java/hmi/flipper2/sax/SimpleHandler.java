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

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import static hmi.flipper2.TemplateController.logger;

public class SimpleHandler extends DefaultHandler {

	private Vector<SimpleElement> stack = null;
	
	private void push(SimpleElement el) {
		if ( stack.size() > 0 )
			top().addChild(el);		
		stack.addElement(el);
	}
	
	private SimpleElement top() {
		return stack.lastElement();
	}
	
	private SimpleElement pop() {
		SimpleElement top = top();
		stack.remove(top);
		return top;
	}
	
	public  SimpleElement root = null;
	
    public void startDocument() throws SAXException {
        stack =  new Vector<SimpleElement>();
        push(new SimpleElement("ROOT",null));
    }

    public void endDocument() throws SAXException {
    	if ( top().children.size() != 1 )
    		throw new RuntimeException("UNEXPECTED");
    	this.root = top().children.firstElement();
    	stack = null;
    	
    }

    public void startElement(String uri, String localName,
        String qName, Attributes attributes)
    throws SAXException {
    	push(new SimpleElement(qName, attributes));
    }

    public void endElement(String uri, String localName, String qName)
    throws SAXException {
    	pop();
    }

    public void characters(char ch[], int start, int length)
    throws SAXException {
    	top().addCharacters(ch, start, length);
    }

    //
    private String getParseExceptionInfo(SAXParseException spe) {
        String systemId = spe.getSystemId();

        if (systemId == null) {
            systemId = "null";
        }

        String info = "URI=" + systemId + " Line=" 
            + spe.getLineNumber() + ": " + spe.getMessage();

        return info;
    }
    
    public void warning(SAXParseException spe) throws SAXException {
        logger.warn("Warning: " + getParseExceptionInfo(spe));
    }
        
    public void error(SAXParseException spe) throws SAXException {
        String message = "Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    public void fatalError(SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }
}
