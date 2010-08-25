package de.htwmaps.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.htwmaps.util.Property;

/**
 * Name: <code>MySQL</code>
 * <p>
 * Klasse dient zum connecten zu einer Datenbank.
 * Beinhaltet ebenfalls Methoden um SQL-Querys auszufuehren
 * </p>
 * <ul>
 * 	 <li>@author YC</li>
 * 	 <li>@coauthor CR</li>
 * 	 <li>@version: 1.1 - 08.06.2010</li>
 * </ul>
 */
public class DBConnector {

	private String host;
	private String username;
	private String password;
	private String driver;
	private static boolean connected = false;
	private static Connection con = null;
	private static DBConnector instance = null;

	private DBConnector() {
		Property prop = new Property("Datenbank.ini");
		host = prop.getProp("host");
		username = prop.getProp("username");
		password = prop.getProp("password");
		driver = prop.getProp("driver");
		connected = connect();
	}

	protected void finalize() {
		connected = disconnect();
		instance = null;
	}

	public static boolean isConnected() {
		return connected;
	}

	public static Connection getConnection(){
		if (instance == null) {
			instance = new DBConnector();
		}
		return DBConnector.con;
	}

	private boolean connect() {
			try {
				Class.forName(driver).newInstance();
				con = DriverManager.getConnection(host, username, password);

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return true;
	}
	
	public static boolean disconnect() {
		if (connected) {
			try {
				con.close();
				instance = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
