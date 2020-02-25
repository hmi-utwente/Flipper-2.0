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

import java.sql.Connection;

import hmi.flipper2.FlipperException;
import hmi.flipper2.Is;

public class DbJavaValue extends JavaValue {

	private Connection db_connection;
	
	public DbJavaValue(Is is, String name) throws FlipperException {
		if ( name != null && !name.equals("default"))
				throw new FlipperException("DbJavaValue: only default db can be used at the moment");
		this.db_connection = is.getDbConnection();
		if ( this.db_connection == null)
			throw new FlipperException("DbJavaValue: no db connection");
	}
	
	@Override
	public Object getObject() throws FlipperException {
		return db_connection;
	}

	@Override
	public Class<?> objectClass() throws FlipperException {
		return Connection.class;
	}
}