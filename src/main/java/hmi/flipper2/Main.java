package hmi.flipper2;

import hmi.flipper2.postgres.Database;

public class Main {

	public static void main(String[] args) {
		try {
			Database db = null;
			// Database db = Database.openDatabaseFromConfig();
			if ( db != null )
				db.reset(); // do a complete new start
			TemplateController tc = TemplateController.create("Test", "A test setup", db);
			//
			tc.addTemplateFile( tc.resourcePath("example/Flipper2Count.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/ChoiceExample.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/PersonDbExample.xml") );
			
			int maxcount = 5;
			int count = 0;
			boolean changed = true;
			while( changed && (count < maxcount) ) {
				System.out.println("\nIS:\n---\n"+tc.getIs("is")+"\n");
				changed = tc.checkTemplates();
				count++;
			}
			TemplateController.destroy("Test", db);
		} catch (FlipperException e) {
			FlipperException.handle(e);
		}
	}
	
}
