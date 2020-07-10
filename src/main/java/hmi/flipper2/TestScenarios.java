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

import hmi.flipper2.postgres.Database;

import static hmi.flipper2.TemplateController.logger;

public class TestScenarios {

	public static void main(String[] args) {
		openCheckXCloseDestroy();
	}

	public static void openCheckXCloseDestroy() {
		try {
			String scenario = "TestMultiDB";
			Database db = null;
			//Database db = Database.openDatabaseFromConfig();
			//db.clearAll();; // do a complete new start
			TemplateController tc = TemplateController.create(scenario, "An iterative open/close setup", db);	
			tc.addTemplateFile( tc.resourcePath("example/Flipper2Count.xml") );
			tc.close();
			//
			for(int i=0; i<5; i++) {
				tc = new TemplateController(scenario, db);
				logger.debug("\nBEFORE IS:\n---\n"+tc.getIs("is")+"\n");
				tc.checkTemplates();
				logger.debug("\nIS:\n---\n"+tc.getIs("is")+"\n");
				tc.close();
			}
			// TemplateController.destroy(scenario, db);
		} catch (FlipperException e) {
			FlipperException.handle(e);
		}
	}
	
}
