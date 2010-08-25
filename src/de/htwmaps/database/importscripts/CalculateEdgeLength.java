/**
 * 
 */
package de.htwmaps.database.importscripts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.database.DBConnector;

/**
 * @author tobiaslana
 *
 */



public class CalculateEdgeLength {

	int node1ID = 0;
	int node2ID = 0;
	int edgeID 	= 0;
	int restEdge = 0;
	float node1lat, node1lon, node2lat, node2lon;
	float length;

	public CalculateEdgeLength() {
		start();
	}
    
    /**
     * Berechnet die exakte Entfernung anhand der Breiten und Laengengrade in Metern
     * 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    private double betterDistance(float lat1, float lon1, float lat2, float lon2) {
    	float lat 	= (float) ((lat1 + lat2) / 2.0 * 0.01745);
    	float dx 	= (float) (111.3 * Math.cos(lat) * (lon1 - lon2));
    	float dy 	= (float) 111.3 * (lat1 - lat2);
    	double length = Math.sqrt(dx * dx + dy * dy);
    	length = length * 1000;
    	return length;
    }

	private void start() {
		boolean error = false;
		ResultSet allEdges 	= null;
		int min = 0;
		int max = 0;
		try {
	        PreparedStatement psLength 	= DBConnector.getConnection().prepareStatement("UPDATE `edges_all` SET `length` = ? WHERE `ID`= ?");
	        PreparedStatement psEdges 	= DBConnector.getConnection().prepareStatement("SELECT ID, node1ID, node2ID, node1lat, node1lon, node2lat, node2lon FROM edgeview WHERE ID BETWEEN ? AND ?");
	        while (!error) {
	        	min = max;
	        	max = min + 100000;
		        psEdges.setInt(1, min);
		        psEdges.setInt(2, max);
		        allEdges = psEdges.executeQuery();
//	        	allEdges = DBConnector.getConnection().createStatement().executeQuery("SELECT ID, node1ID, node2ID, node1lat, node1lon, node2lat, node2lon FROM edgeview WHERE length IS NULL LIMIT 0, 100000");
	        	System.out.println("Edges " + min + "-" + max + " geladen");
				while (allEdges.next()){
					//System.out.println("Inner");
					edgeID	= allEdges.getInt(1);
					node1ID = allEdges.getInt(2);
					node2ID = allEdges.getInt(3);
					node1lat = allEdges.getFloat(4);
					node1lon = allEdges.getFloat(5);
					node2lat = allEdges.getFloat(6);
					node2lon = allEdges.getFloat(7);
					length = (float) betterDistance(node1lat, node1lon, node2lat, node2lon);
					psLength.setFloat(1, length);
					psLength.setInt(2, edgeID);
					//System.out.println("SQL: " + psLength.toString());
					psLength.execute();
				}
        	}
			System.out.println("Fertig");
	        

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			error = true;
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CalculateEdgeLength();

	}
}