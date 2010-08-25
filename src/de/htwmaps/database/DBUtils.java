package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtils {
	
	private final static String GETNODEID_SELECT = "SELECT nodeID FROM streets WHERE cityname LIKE ? AND streetname = ?";
	private final static String GETCITIESSTARTWITH_SELECT = "SELECT DISTINCT cityname FROM streets WHERE cityname LIKE ?";
	private final static String GETSTREETSSTARTWITH_SELECT = "SELECT DISTINCT streetname FROM streets WHERE cityname = ? AND streetname LIKE ?";
	
	private DBUtils(){ }
	
	public static int getNodeId(String city, String street) throws SQLException, NodeNotFoundException{
		PreparedStatement select = DBConnector.getConnection().prepareStatement(GETNODEID_SELECT);
		select.setString(1, city);
		select.setString(2, street);
		ResultSet rs = select.executeQuery();
		if(!rs.next())
			throw new NodeNotFoundException();
		return rs.getInt(1);
	}
	
	public static String[] getCitiesStartsWith(String s) throws SQLException{
		PreparedStatement select = DBConnector.getConnection().prepareStatement(GETCITIESSTARTWITH_SELECT);
		select.setString(1, s + "%");
		ResultSet rs  = select.executeQuery();
		if(!rs.next())
			return null;
		rs.last();
		int tableLength = rs.getRow();
		rs.beforeFirst();
		String[] result = new String[tableLength];
		for(int i=0; rs.next();i++){
			result[i] = rs.getString(1);
		}
		return result;
	}
	
	public static String[] getStreetsStartsWith(String city, String s) throws SQLException{
		PreparedStatement select = DBConnector.getConnection().prepareStatement(GETSTREETSSTARTWITH_SELECT);
		select.setString(1, city);
		select.setString(2, s + "%");
		ResultSet rs  = select.executeQuery();
		if(!rs.next())
			return null;
		rs.last();
		int tableLength = rs.getRow();
		rs.beforeFirst();
		String[] result = new String[tableLength];
		for(int i=0; rs.next();i++){
			result[i] = rs.getString(1);select.setString(1, s + "%");
		}
		return result;
	}
}
