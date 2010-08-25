package de.htwmaps.database.importscripts;

import java.sql.SQLException;
import java.sql.Statement;

import de.htwmaps.database.DBConnector;

public class PrepareDatabase {

    public PrepareDatabase() {
    	start();
    }

    private void start() {
    	final String SQL1 = "TRUNCATE `cities`";
    	final String SQL2 = "TRUNCATE `edges_all`";
    	final String SQL3 = "TRUNCATE `edges_borders`";
    	final String SQL4 = "TRUNCATE `edges_opt`";
    	final String SQL5 = "TRUNCATE ` nodes`";
    	final String SQL6 = "TRUNCATE ` nodes_opt`";

    	final String SQL7 = "ALTER TABLE cities DROP INDEX lat";
    	final String SQL8 = "ALTER TABLE cities DROP INDEX lon";
    	final String SQL9 = "ALTER TABLE cities DROP INDEX name";
    	final String SQL10 = "ALTER TABLE cities DROP INDEX is_in";
    	final String SQL11 = "ALTER TABLE cities DROP INDEX cityCategory";

    	try {
			Statement st = DBConnector.getConnection().createStatement();
			st.executeUpdate(SQL1);
			System.out.println("1");
			st.executeUpdate(SQL2);
			System.out.println("2");
			st.executeUpdate(SQL3);
			System.out.println("3");
			st.executeUpdate(SQL4);
			System.out.println("4");
			st.executeUpdate(SQL5);
			System.out.println("5");
			st.executeUpdate(SQL6);
			System.out.println("6");
			st.executeUpdate(SQL7);
			System.out.println("7");
			st.executeUpdate(SQL8);
			System.out.println("8");
			st.executeUpdate(SQL9);
			System.out.println("9");
			st.executeUpdate(SQL10);
			System.out.println("10");
			st.executeUpdate(SQL11);
			System.out.println("11");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	//todo: alle tabellen leeren
    	//		alle indizes entfernen
    	
    }
}