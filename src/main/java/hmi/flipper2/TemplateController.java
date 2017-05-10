package hmi.flipper2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hmi.flipper2.postgres.Database;
import hmi.flipper2.sax.SimpleSAXParser;

public class TemplateController {
		
	public static void main(String[] args) {
		try {
			// Database db = null;
			Database db = Database.openDatabaseFromConfig();
			TemplateController tc = create("Test", "A test setup", db);			
			tc.addTemplateFile( tc.resourcePath("example/Flipper2Count.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/ChoiceExample.xml") );
			// tc.addTemplateFile( tc.resourcePath("example/PersonDbExample.xml") );
			
			int maxcount = 10;
			int count = 0;
			boolean changed = true;
			while( changed && (count < maxcount) ) {
				System.out.println("\nIS:\n---\n"+tc.getIs("is")+"\n");
				changed = tc.checkTemplates();
				count++;
			}
			
			tc.destroy();
		} catch (FlipperException e) {
			FlipperException.handle(e);
		}
	}
	
	public static TemplateController create(String name, String descr, Database db) throws FlipperException {
		if ( db != null ) {
				db.reset();
				db.createController(name, descr);
		}
		return new TemplateController(name, db);
	}
	
	/*
	 * 
	 */
	
	private String name;
	private Database db;
	public int	cid; // controller id in Database
	private List<TemplateFile> tf_list;
	public  Is is;
	
	public TemplateController(String name, Database db) throws FlipperException {
		this.name = name;
		this.db = db;
		this.is = new Is(this.db);
		if ( this.db != null ) {
			this.cid = db.getControllerID(name);
			this.tf_list = db.getTemplateFiles(this);
		} else {
			this.cid = -1;
			this.tf_list = new ArrayList<TemplateFile>();
		}
	}
	
	public void addTemplateFile(String path) throws FlipperException {
		try {
			String xml_str = SimpleSAXParser.readFile(path);
			TemplateFile tf = new TemplateFile(this, path, xml_str, null);
			if ( this.db != null )
				db.addTemplateFile(this, tf);
			this.tf_list.add(tf);
		} catch (IOException e) {
			throw new FlipperException(e);
		}
	}
	
	public boolean checkTemplates() throws FlipperException {
		boolean changed = false;

		for (TemplateFile tf : this.tf_list) {
			changed = changed || tf.check(this.is);
		}
		if ( changed ) {
			is.commit(); // commit the information state
			if (this.db != null)
				this.db.commit();
		}
		return changed;
	}
	
	public String getIs(String path) throws FlipperException {
		return is.getIs(path);
	}
	
	public void close() {
	}
	
	public void destroy() throws FlipperException {
		this.close();
		if (this.db != null) {
			db.destroyController(this);
		}
	}
	
	String resourcePath(String r) throws FlipperException {
		URL url = this.getClass().getClassLoader().getResource(r);
		if ( url == null )
			throw new FlipperException("Resource file: " + r + " not found");
		return url.getPath();
	}
	
}
