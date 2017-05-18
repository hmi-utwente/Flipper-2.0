package hmi.flipper2;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hmi.flipper2.postgres.Database;
import hmi.flipper2.sax.SimpleSAXParser;

/**
 * This class is the main Flipper interface class. You can create/modify/destroy Flipper Templatecontollers using this class. 
 * An example of how to use this class can be found in the Main class 
 * 
 * @author Jan Flokstra
 * @version 1.0
 * 
 */

public class TemplateController {
		
	/**
	 * This method creates a new TemplateController.
	 * 
	 * @param name
	 *            The name of the TemplateController. When db != null this name
	 *            has to be unique .
	 * @param descr
	 *            A description what this Controller does and is used for.
	 * @param db
	 *            The database used by this TemplateController, may be null for
	 *            non-persistent Controller.
	 * @return A running TemplateController. This TemplateController is
	 *         persistent in the database when db != null
	 * @exception FlipperException
	 *                On all errors.
	 */
	public static TemplateController create(String name, String descr, Database db) throws FlipperException {
		if ( db != null ) {
				db.createController(name, descr);
				db.commit();
		}
		return new TemplateController(name, db);
	}
	
	/**
	 * This method destroys a persistent TemplateController.
	 * 
	 * @param name
	 *            The name of the existing TemplateController in the Database.
	 * @param db
	 *            The Database the TemplateController is stored. When db == null the method does nothing.
	 * @exception FlipperException
	 *                On all errors.
	 */
	public static void destroy(String name, Database db) throws FlipperException {
		if ( db != null ) {
				db.destroyController(name);
				db.commit();
		}
	}
	
	/*
	 * 
	 */
	
	private String name;
	private Database db;
	public int	cid; // controller id in Database
	private List<TemplateFile> tf_list;
	public  Is is;
	
	/**
	 * This method constructs a running TemplateController.
	 * 
	 * @param name
	 *            The name of the TemplateController. When db != null the
	 *            TemplateController name has to exist in the Database.
	 *            Otherwise use TemplateController.create().
	 * @param db
	 *            The database used by this TemplateController, may be null for
	 *            a non-persistent Controller.
	 * @exception FlipperException
	 *                On all errors.
	 */
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
	
	/**
	 * This method adds an Xml template file to a running TemplateController.
	 * When the TemplateController is persistent in a Database this template
	 * file is persistently added to the controller.
	 * 
	 * @param path
	 *            The path of the XML template file
	 * @exception FlipperException
	 *                On all errors.
	 */
	public void addTemplateFile(String path) throws FlipperException {
		try {
			try {

				String xml_str = SimpleSAXParser.readFile(path);
				TemplateFile tf = new TemplateFile(this, path, xml_str, null, 0);
				if (this.db != null)
					db.addTemplateFile(this, tf);
				this.tf_list.add(tf);
			} catch (IOException e) {
				throw new FlipperException(e);
			}
		} catch (FlipperException e) {
			e.registerCurrentTemplate(this.current_tf, this.current_id, this.current_name);
			throw e;
		}
	}
	
	/**
	 * This method checks all Templates if the preconditions are true and fires
	 * the Effects and Behaviours when necessary. When the controller is
	 * persistent the Information State is saved in the Datababase and the
	 * Database state is committed.
	 * 
	 * @return a boolean value indicating that some Template preconditions were
	 *         true.
	 * @exception FlipperException
	 *                On all errors.
	 */
	public boolean checkTemplates() throws FlipperException {
		try {
			boolean changed = false;

			for (TemplateFile tf : this.tf_list) {
				changed = changed || tf.check(this.is);
			}
			if (changed) {
				is.commit(); // commit the information state
				if (this.db != null)
					this.db.commit();
			}
			return changed;
		} catch (FlipperException e) {
			e.registerCurrentTemplate(this.current_tf, this.current_id, this.current_name);
			throw e;
		}
	}
	
	private String current_tf = null;
	private String current_id = null;
	private String current_name = null;
	
	public void registerCurrentTemplate(String current_tf, String current_id, String current_name) {
		this.current_tf = current_tf;
		this.current_id = current_id;
		this.current_name = current_name;
	}
	
	/**
	 * This method returns the String JSON value of the Information State path variable.
	 * 
	 * @param path The Information state variable path as used by Javascript
	 * @return The String JSON Value of the JavaScript Is variable.
	 * @exception FlipperException
	 *                On all errors.
	 */
	public String getIs(String path) throws FlipperException {
		return is.getIs(path);
	}
	
	/**
	 * This method closes a TemplateController. In case of a non-persistent
	 * controller all data will be lost otherwise the controller is persistent
	 * in the Database and can be reopened in the future.
	 * 
	 * @exception FlipperException
	 *                On all errors.
	 */
	public void close() throws FlipperException {
		this.name = null;
		this.db = null;
		this.is = null;
	}
	
	/**
	 * This method returns the absolute filename of a project resource on the host filesystem.
	 * 
	 * @param rpath
	 *            The relative Path of the resource in the project tree.
	 * @return The absolute path of the file in the host filesystem.
	 * @exception FlipperException
	 *                On all errors.
	 */
	public String resourcePath(String rpath) throws FlipperException {
		URL url = this.getClass().getClassLoader().getResource(rpath);
		if ( url == null )
			throw new FlipperException("Resource file: " + rpath + " not found");
                return url.getPath().replaceFirst("^/(.:/)", "$1");
	}
	
}
