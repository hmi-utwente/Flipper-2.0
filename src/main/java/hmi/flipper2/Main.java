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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hmi.flipper2.debugger.FlipperDebugger;
import hmi.flipper2.postgres.Database;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		try {
			Database db = null;
			// Database db = Database.openDatabaseFromConfig();
			if ( db != null )
				db.clearAll(); // do a complete new start
			TemplateController tc = TemplateController.create("Test", "A test setup", db);
			//TemplateController tc = TemplateController.create("Test", "A test setup", db, new String[] {
			//	"jslibs/underscore-min.js"
			//});
				
			if (Config.debugging ) {
				tc.setDebugger(new FlipperDebugger(tc));
			}
			tc.addTemplateFile( tc.resourcePath("example/Flipper2Count.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/Try.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/ConditionalTemplates.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/Underscore.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/ChoiceExample.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/PersonDbExample.xml") );
			// tc.dataflow.analyze();
			if ( db != null )
				db.commit(); // addTemplatefile does not automatically commit()
			
			int maxcount = 5;
			int count = 0;
			boolean changed = true;
			while( changed && (count < maxcount) ) {
				logger.debug("\nIS:\n---\n"+tc.getIs("is")+"\n");
				changed = tc.checkTemplates();
				count++;
			}
			TemplateController.destroy("Test", db);
		} catch (FlipperException e) {
			FlipperException.handle(e);
		}
	}
	
}
