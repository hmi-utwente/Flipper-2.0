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

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import hmi.flipper2.FlipperException;

public class SimpleSAXParser {
	
	public static SimpleElement parseString(String path, String xml_str) throws FlipperException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			SimpleHandler handler = new SimpleHandler();
			saxParser.parse(new InputSource(new StringReader(xml_str)), handler);
			return handler.root;
		} catch (SAXException | ParserConfigurationException e) {
			throw new FlipperException(e, "# Parsing XML File: "+path);
		} catch(IOException e) {
			throw new FlipperException(e);
		}
	}

	public static SimpleElement parseFile(String path) throws FlipperException {
		try {
			return parseString(path, readFile(path));
		} catch (IOException e) {
			throw new FlipperException(e);
		}
	}
	
	public final static String readFile(String path) throws IOException {
		return readFile(path, StandardCharsets.UTF_8);
	}

	public final static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
