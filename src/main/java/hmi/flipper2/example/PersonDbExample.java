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
package hmi.flipper2.example;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import hmi.flipper2.FlipperException;

public class PersonDbExample {

	private Connection connection;

	public PersonDbExample() {
		this.connection = null;
	}

	public PersonDbExample(Connection connection) {
		this.connection = connection;
	}

	public static String initPersonTable(Connection conn) throws FlipperException {
		JsonArrayBuilder ab = Json.createArrayBuilder();
		ab.add(addPerson(conn, "Jan", "Flokstra", "Software Engineer", 54));
		ab.add(addPerson(conn, "Peter", "Apers", "Boss", 63));
		ab.add(addPerson(conn, "Jelte", "van Waterschoot", "AIO", 21));
		return ab.build().toString();
	}

	public static int addPerson(Connection conn, String firstname, String lastname, String occupation, int age)
			throws FlipperException {
		try {
			String insertTableSQL = "INSERT INTO person"
					+ "(firstname,lastname,occupation,age) VALUES(?,?,?,?) RETURNING id";
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, firstname);
			preparedStatement.setString(2, lastname);
			preparedStatement.setString(3, occupation);
			preparedStatement.setInt(4, age);
			ResultSet rs = preparedStatement.executeQuery();
			rs.next();
			return rs.getInt("id");

		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}

	public String getPerson(Integer id) throws FlipperException {
		try {
			String selectSQL = "SELECT * FROM person WHERE id = ?;";
			PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, id.intValue());
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				JsonObject resultPerson = Json.createObjectBuilder().add("firstname", rs.getString("firstname"))
						.add("lastname", rs.getString("lastname")).add("occupation", rs.getString("occupation"))
						.add("age", rs.getInt("age")).build();
				return resultPerson.toString();
			}
			throw new FlipperException("Person with id \"" + id + "\" not found.");

		} catch (SQLException e) {
			System.out.println("CAUGHT: " + e);
			throw new FlipperException(e);
		}
	}

	public void greetingsToMa(String json) {
		JsonObject jperson = string2json(json);
		String fn = jperson.getString("firstname");
		String ln = jperson.getString("lastname");
		System.out.println("!@! Person \"" + fn + " " + ln + "\" sends greetings to his Mother");
	}

	/*
	 * 
	 */
	public static JsonObject string2json(String s) {
		JsonReader reader = Json.createReader(new StringReader(s));
		JsonObject result = reader.readObject();
		reader.close();
		return result;
	}

}