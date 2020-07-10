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
package hmi.flipper2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.script.ScriptException;

import org.xml.sax.SAXException;

import static hmi.flipper2.TemplateController.logger;

public class FlipperException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Exception ex = null;
	public String text = null;
	public String stack = null;
	public String extra = null;
	
	protected void _init(Exception ex) {
		this.ex = ex;
		this.text = ex.toString();
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bs);
		ex.printStackTrace(ps);
		try {
			this.stack = bs.toString("UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public FlipperException(String text) {
		this.text = text;
	}
	
	public FlipperException(Exception ex) {
		_init(ex);
	}
	
	public FlipperException(Exception ex, String extra) {
		_init(ex);
		this.extra = extra;
		logger.error(this.extra);
	}
	
	public FlipperException(SAXException ex) {
		_init(ex);
	}
	
	public FlipperException(IOException ex) {
		_init(ex);
	}
	
	public FlipperException(SQLException ex) {
		_init(ex);
	}
	
	public FlipperException(SQLException ex, String extra) {
		_init(ex);
		this.extra = extra;
	}
	
	public FlipperException(ScriptException ex, String script) {
		_init(ex);
		this.extra = "JAVASCRIPT ERROR:\n=================\n"+script+"\n";
	}
	
	private String currentInfo = null;
	
	public void registerCurrentTemplate(String current_tf, String current_id, String current_name) {
		this.currentInfo = "!TemplateFile: " + current_tf + " Tid: " + current_id + " Tname: "  + current_name;
	}
	
	public static void handle(FlipperException e) {
		if ( e.extra != null )
			logger.error(e.extra);
		if ( e.currentInfo != null )
			logger.error(e.currentInfo);
		logger.error("!Caught Exception: "+e.text);
		if (e.stack != null )
			logger.error("!Stack: \n"+e.stack);
	}
	
}
