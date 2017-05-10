package hmi.flipper2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hmi.flipper2.effect.EffectList;
import hmi.flipper2.effect.JavaEffect;
import hmi.flipper2.javascript.JsStringValue;
import hmi.flipper2.javascript.JsValue;
import hmi.flipper2.postgres.Database;
import hmi.flipper2.sax.SimpleElement;
import hmi.flipper2.sax.SimpleSAXParser;

public class TemplateFile {
	
	public int    tfid = -1;
	public String name;
	public String path;
	public String xml_str;
	public TemplateController tc;
	private SimpleElement xml_root;
	
	public TemplateFile(TemplateController tc, String path, String xml_str, String db_is_value) throws FlipperException  {
		this.tc = tc;
		this.name = (new File(path)).getName();
		this.path = path;
		this.xml_str = xml_str;
		this.xml_root = SimpleSAXParser.parseString(this.path, xml_str);
		for (int i = 0; i < this.xml_root.children.size(); i++) {
			handle_section(this.xml_root.children.get(i));
		}
		init(db_is_value);
	}
	
	private void handle_section(SimpleElement el) throws FlipperException {
		if (el.tag.equals("is")) {
			handle_is(el);
		} else if (el.tag.equals("database")) {
			handle_database(el);
		} else if (el.tag.equals("javascript")) {
			this.tc.is.execute(el.characters.toString());
		} else if (el.tag.equals("template")) {
			handle_template(el);
		} else
			throw new RuntimeException("INCOMPLETE: "+el.tag);
	}
	
	public String is_name = null;
	public boolean is_updated = false;
	public String is_json_value = null;
	
	private void handle_is(SimpleElement el) {
		if ( is_name != null ) 
			throw new RuntimeException("INCOMPLETE: multiple is");
		this.is_name = el.attr.get("name");
		this.is_json_value = el.characters.toString();
	}
	
	public List<String> db_init_sql = new ArrayList<String>();
	public EffectList db_init_java = new EffectList();
	
	public List<String> db_cleanup_sql = new ArrayList<String>();
	public EffectList db_cleanup_java = new EffectList();
	
	private void handle_database(SimpleElement el) throws FlipperException {
		for (SimpleElement db_el : el.children) {
			if ( db_el.tag.equals("init")) {
				for (SimpleElement db_iel : db_el.children) {
					if (db_iel.tag.equals("function") || db_iel.tag.equals("method")) {
						this.db_init_java.add(Template.handle_effect(tc.is, db_iel));
					} else if (db_iel.tag.equals("sql")) {
						this.db_init_sql.add(db_iel.characters.toString());
					} else
						throw new FlipperException("UNEXPECTED db:init tag: "+db_iel.tag);
				}
			} else if ( db_el.tag.equals("cleanup")) {
					// INCOMPLETE
			} else
				throw new RuntimeException("INCOMPLETE: bad database");
		}
	}
	
	List<Template> templates = new ArrayList<Template>();
	
	private void handle_template(SimpleElement el) throws FlipperException {
		templates.add( new Template(this, el)	);
	}
	
	/*
	 * 
	 */
	
	private void init(String db_is_value) throws FlipperException {
		// first declate the is
		tc.is.declare_tf(this, (db_is_value==null?this.is_json_value:db_is_value));	
		if ( this.db_init_sql != null || this.db_init_java != null ) {
			Database db = tc.is.getDatabase();
			if ( db == null )
				throw new FlipperException("<db> section in database without default Database");
			if ( this.db_init_sql != null ) {
				for(String sql : this.db_init_sql)
					db.executeScript(sql);
			} if ( this.db_init_java != null )
				this.db_init_java.doIt(tc.is);
		}
	}
	
	/*
	 * 
	 * 
	 */
	
	public boolean check(Is is) throws FlipperException {	
		boolean res = false;
		
		for (Template template : this.templates ) {
			res = res || template.check(is);
		}
		return res;
	}
	
	/*
	 * 
	 * 
	 */
	
	public JsValue isValue() {
		return new JsStringValue("INCOMPLETE");
	}
}
