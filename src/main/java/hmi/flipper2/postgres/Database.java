package hmi.flipper2.postgres;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import hmi.flipper2.FlipperException;
import hmi.flipper2.TemplateController;
import hmi.flipper2.TemplateFile;

public class Database {
	
	private Connection conn = null;
	private Properties props = null;
	
	public void commit() throws FlipperException {
		try {
			conn.commit();
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public void rollback() throws FlipperException {
		try {
			conn.rollback();
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public Database() {
	}

	public Database(String url, String user, String password) throws FlipperException {
		try {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				throw new FlipperException("Class org.postgresql.Driver not found");
			}
			this.props = new Properties();
			props.setProperty("url", url);
			props.setProperty("user", user);
			props.setProperty("password", password);
			this.conn = DriverManager.getConnection(url, props);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new FlipperException(e,"Connection Details: "+props);
		}
	}
	
	public Connection getConnection() {
		return this.conn;
	}
	
	public void reset() throws FlipperException {
		try {
			Statement st = conn.createStatement();
			st.execute("DROP TABLE IF EXISTS flipper;");
			st.execute("DROP TABLE IF EXISTS flipper_tf;");
			st.execute("DROP SEQUENCE IF EXISTS flipper_global_id;");
			st.execute("CREATE SEQUENCE flipper_global_id;");
			st.execute("CREATE TABLE flipper("+
						"cid INT PRIMARY KEY DEFAULT nextval('flipper_global_id')," +
						"name TEXT," +
					    "description TEXT," +
						"created TIMESTAMP" +
					    ");");
			st.execute("CREATE TABLE flipper_tf("+
					"tfid INT DEFAULT nextval('flipper_global_id')," +
					"cid INT," +
					"name TEXT PRIMARY KEY," +
					"path TEXT," +
					"xml TEXT," +
					"json_is JSONB," +
					"created TIMESTAMP," +
					"updated TIMESTAMP" +
				    ");");
			conn.commit();
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public void executeScript(String script) throws FlipperException {
		try {
			Statement st = conn.createStatement();
			st.addBatch(script);
			st.executeBatch();
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public void close() throws SQLException {
		conn.close();
	}
	
	public void createController(String name, String descr) throws FlipperException {
		try {
			String insertTableSQL = "INSERT INTO flipper" + "(name,description,created) VALUES" + "(?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, descr);
			preparedStatement.setTimestamp(3, getCurrentTimeStamp());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public void destroyController(TemplateController tc) throws FlipperException {
		try {
			String delSQL = "DELETE FROM flipper WHERE cid = ?;";
			PreparedStatement preparedStatement = conn.prepareStatement(delSQL);
			preparedStatement.setInt(1, tc.cid);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}

	public void addTemplateFile(TemplateController tc, TemplateFile tf) throws FlipperException {
		try {
			String insertTableSQL = "INSERT INTO flipper_tf" + "(cid,name,path,xml,json_is,created) VALUES" + "(?,?,?,?,to_json(?::json),?) RETURNING tfid";
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setInt(1, tc.cid);
			preparedStatement.setString(2, tf.name);
			preparedStatement.setString(3, tf.path);
			preparedStatement.setString(4, tf.xml_str);
			preparedStatement.setString(5, tf.is_json_value);
			preparedStatement.setTimestamp(6, getCurrentTimeStamp());
			ResultSet rs = preparedStatement.executeQuery();
			if ( rs.next() )
	            tf.tfid =  rs.getInt("tfid");
			else
				throw new FlipperException("addTemplateFile: UNEXPECTED RESULT");     
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public void updateTemplateFileIs(TemplateFile tf, String is_value) throws FlipperException {
		try {
			String updateTableSQL = "UPDATE flipper_tf SET json_is = to_json(?::json), updated = ? WHERE tfid = ?;";
			PreparedStatement preparedStatement = conn.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, is_value);
			preparedStatement.setTimestamp(2, getCurrentTimeStamp());
			preparedStatement.setInt(3, tf.tfid);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public List<TemplateFile> getTemplateFiles(TemplateController tc) throws FlipperException {
		List<TemplateFile> res = new ArrayList<TemplateFile>();
		
		// System.out.println("INCOMPLETE:Database:getTemplateFiles");
		try {
			String selectSQL = "SELECT path,xml,json_is#>>'{}' AS json_is FROM flipper_tf WHERE cid = ?;";
			PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setInt(1, tc.cid);
			ResultSet rs = preparedStatement.executeQuery();
			while ( rs.next() ) {
	            String path = rs.getString("path");
	            String xml_str = rs.getString("xml_str");
	            String json_is = rs.getString("json_is");
	            //
	            TemplateFile tf = new TemplateFile(tc, path, xml_str, json_is);
	            res.add(tf);
			}
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
		return res;
	}
	
	public int getControllerID(String name) throws FlipperException  {
		try {
			String selectSQL = "SELECT cid FROM flipper WHERE name = ?;";
			PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
			preparedStatement.setString(1, name);
			ResultSet rs = preparedStatement.executeQuery();
			if ( rs.next() )
	            return rs.getInt("cid");
	        throw new FlipperException("Flipper controller \""+name+"\" not found.");           
		} catch (SQLException e) {
			throw new FlipperException(e);
		}
	}
	
	public void create_Person_Table() throws SQLException {
		Statement st = conn.createStatement();
		st.execute("DROP TABLE IF EXISTS person;");
		st.execute("CREATE TABLE person(firstname text, lastname text, age integer);");
		st.close();
		PreparedStatement sti = conn.prepareStatement("INSERT INTO person VALUES(?, ?, ?);");
		sti.setString(1, "Jan");
		sti.setString(2, "Flokstra");
		sti.setInt(3, 33);
		sti.execute();
		sti.setString(1, "Pietje");
		sti.setString(2, "Puk");
		sti.setInt(3, 19);
		sti.execute();
		
//		JsonObject personObject = Json.createObjectBuilder()
//                .add("name", "John")
//                .add("age", 13)
//                .build();
		
		sti.close();
	}
	
//	public DefaultRecord get_record(String sql) throws SQLException {
//		DefaultRecord res = new DefaultRecord();
//		
//		Statement st = conn.createStatement();
//		ResultSet rs = st.executeQuery(sql);
//		ResultSetMetaData meta = rs.getMetaData();
//	
//		if (rs.next()) {
//			for(int i=1; i<=meta.getColumnCount(); i++) {
//				System.out.println("Col: "+i + ": "+meta.getColumnLabel(i) + " = " + rs.getObject(i));
//				res.set(meta.getColumnLabel(i), rs.getObject(i));
//			}
//			if ( rs.next() )
//				throw new SQLException("Too much results for get_record: "+sql);
//		} else {
//			throw new SQLException("No result for get_record: "+sql);
//		}
//		rs.close();
//		st.close();
//		
//		return res;
//	}
	
//	public static void main( String args[] ) throws SQLException, FlipperException
//	{
//		System.out.println("Testing Database");
//		Database db = new Database("jdbc:postgresql://things.ewi.utwente.nl/flipper", "flipper", "flipper..");
//		
//		db.create_Person_Table();
//		// db.get_record("SELECT * from person WHERE lastname = \'Flokstra\';");
//
//		db.close();
//	}
	
	private static java.sql.Timestamp getCurrentTimeStamp() {

		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}
	
	/*
	 * 
	 * 
	 */
	
	private static final String DB_CONFIG_FILE_NAME = "db.conf.local";
	
	public static Database openDatabaseFromConfig() throws FlipperException{
		String host =null, database = null, role = null, password = null;
		InputStream inputStream = null;
		try {
			Properties prop = new Properties();
			inputStream = (new Database()).getClass().getClassLoader().getResourceAsStream(DB_CONFIG_FILE_NAME);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("No DB config property file '" + DB_CONFIG_FILE_NAME + "' found in the classpath");
			} 
			host = prop.getProperty("host");
			database = prop.getProperty("database");
			role = prop.getProperty("role");
			password = prop.getProperty("password");
		} catch (Exception e) {
			throw new FlipperException(e,"Bad DB config property file in resources directory: "+DB_CONFIG_FILE_NAME);
		} finally {	
			try {
				inputStream.close();
			} catch (IOException e) {
				// IGNORE
			}
		}
		return new Database("jdbc:postgresql://"+host+"/"+database, role, password);
	}
	
	
	
	
}
