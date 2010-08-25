package de.htwmaps.database.importscripts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import de.htwmaps.database.DBConnector;

public class ParseOSM {

    private final String READ_PATH;

    /**
     * Konstruktor
     * @param READ_PATH Pfad + Name der Datei aus welcher gelesen wird
     */
    public ParseOSM(String READ_PATH) {
    	this.READ_PATH = READ_PATH;
    	start();
    }

    /**
     * Parst die OSM Datei und schreibt alle relevanten Daten in die Datenbank
     */
    private void start() {
    	final String SQL1 = "INSERT INTO `nodes` (`ID`, `lat`, `lon`) VALUES (?, ?, ?)";
    	final String SQL2 = "INSERT INTO `cities` (`ID`, `lat`, `lon`, `name`, `is_in`, `cityCategory`) VALUES (?, ?, ?, ?, ?, ?)";
    	final String SQL3 = "INSERT INTO `edges_all` (`ID`, `node1ID`, `node1lat`, `node1lon`, `node2ID`, `node2lat`, `node2lon`, `wayID`, `isOneway`, `speedID`, `length`, `visited`) VALUES (NULL, ?, NULL, NULL, ?, NULL, NULL, ?, ?, ?, NULL, 0)";
    	final String SQL4 = "INSERT INTO `ways` (`ID`, `startEdgeID`, `endEdgeID`, `nameValue`, `cityName`, `cityNodeID`, `is_in`, `highwayValue`, `isOneway`, `ref`, `startNodeID`, `endNodeID`) VALUES (?, ?, ?, ?, NULL, NULL, NULL, ?, ?, ?, ?, ?)";
    	final String SQL5 = "INSERT INTO `edges_borders` (`ID`, `node1ID`, `node2ID`, `wayID`, `name`) VALUES (NULL, ?, ?, ?, ?)";
    	final String SQL6 = "SELECT ID FROM `edges_all` WHERE `node1ID` = ? AND `node2ID` = ?";
    	final String SQL7 = "SELECT ID FROM `edges_borders` WHERE `node1ID` = ? AND `node2ID` = ?";
    	boolean taggeWays = false;
    	boolean isOneway = false;
    	int startEdgeID = 0;
    	int endEdgeID = 0;
    	int speedID = 0;
    	String node1ID = "";
    	String node2ID = "";
        String wayID = "";
        String nodeID = "";
        String nodeLat = "";
        String nodeLon = "";
        String node_tag_attributeName = "";
        String node_tag_name = "";
        String node_tag_is_in = "";
        String node_tag_cityCategory = "";
        String way_tag_onewayValue = "";
        String way_tag_nameValue = "";
        String way_tag_highwayValue = "";
        String way_tag_landuseValue = "";
       	String way_tag_ref = "";
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        LinkedList<String> nd_ref = new LinkedList<String>();
        try {
        	PreparedStatement ps1 = DBConnector.getConnection().prepareStatement(SQL1);
        	PreparedStatement ps2 = DBConnector.getConnection().prepareStatement(SQL2);
        	PreparedStatement ps3 = DBConnector.getConnection().prepareStatement(SQL3);
        	PreparedStatement ps4 = DBConnector.getConnection().prepareStatement(SQL4);
        	PreparedStatement ps5 = DBConnector.getConnection().prepareStatement(SQL5);
        	PreparedStatement ps6 = DBConnector.getConnection().prepareStatement(SQL6);
        	PreparedStatement ps7 = DBConnector.getConnection().prepareStatement(SQL7);
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(READ_PATH));
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT) {
                	//Nodes werden geholt
                	if (xmlStreamReader.getName().toString().equals("node")) {
						for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
							node_tag_attributeName = xmlStreamReader.getAttributeLocalName(i);
							if (node_tag_attributeName.equals("id")) {
	                            nodeID = xmlStreamReader.getAttributeValue(i);
							}
							if (node_tag_attributeName.equals("lat")) {
								nodeLat = xmlStreamReader.getAttributeValue(i);
							}
							if (node_tag_attributeName.equals("lon")) {
								nodeLon = xmlStreamReader.getAttributeValue(i);
							}
						}
                	}
                	// Tagwoerter von den Nodes werden geholt
                	if (!taggeWays) {
	                    if (xmlStreamReader.getLocalName().equals("tag")) {
	                    	if (xmlStreamReader.getAttributeValue(0).equals("place") && ( //Nehme nur Nodes mit einem Staedte-Tag
	                    		xmlStreamReader.getAttributeValue(1).equals("city") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("town") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("village") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("hamlet") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("isolated_dwelling") ||
	                    		xmlStreamReader.getAttributeValue(1).equals("suburb"))) {
	                    		node_tag_cityCategory = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("name")) {
	                    		node_tag_name = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("is_in")) {
	                    		node_tag_is_in = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    }
                	}
                    //Ways werden geholt
                	if (xmlStreamReader.getName().toString().equals("way")) {
                		wayID = xmlStreamReader.getAttributeValue(0).toString();
                		taggeWays = true;
                	}
                	//Tagwoerter von den Ways werden geholt
                	if (taggeWays) {
	                    if (xmlStreamReader.getLocalName().equals("nd")) {
	                    	nd_ref.add(xmlStreamReader.getAttributeValue(0).toString());
	                    }
	                    if (xmlStreamReader.getLocalName().equals("tag")) {
	                    	if (xmlStreamReader.getAttributeValue(0).equals("name")) {
	                    		way_tag_nameValue = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("highway")) {
	                    		if (xmlStreamReader.getAttributeValue(1).equals("motorway") ||
	                    		    xmlStreamReader.getAttributeValue(1).equals("motorway_link") ||
	                    		    xmlStreamReader.getAttributeValue(1).equals("trunk") ||
	                    		    xmlStreamReader.getAttributeValue(1).equals("trunk_link")) {
	                    			speedID = 1;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("primary") ||
	                    			xmlStreamReader.getAttributeValue(1).equals("primary_link")) {
	                    			speedID = 5;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("secondary") ||
	                    			xmlStreamReader.getAttributeValue(1).equals("secondary_link") ||
	                    			xmlStreamReader.getAttributeValue(1).equals("tertiary")) {
	                    			speedID = 7;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("unclassified") ||
	                    			xmlStreamReader.getAttributeValue(1).equals("residential")) {
	                    			speedID = 10;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("road")) {
	                    			speedID = 11;
	                    		}
	                    		if (xmlStreamReader.getAttributeValue(1).equals("living_street")) {
	                    			speedID = 13;
	                    		}
	                    		way_tag_highwayValue = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("ref")) {
	                    		way_tag_ref = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("landuse")) {
	                    		way_tag_landuseValue = xmlStreamReader.getAttributeValue(1);
	                    	}
	                    	if (xmlStreamReader.getAttributeValue(0).equals("oneway")) {
	                    		way_tag_onewayValue = xmlStreamReader.getAttributeValue(1);
	                    		//moegliche positive onewayValue Werte: yes/true/1
	                    		if (way_tag_onewayValue.equals("yes") || way_tag_onewayValue.equals("true") || way_tag_onewayValue.equals("1")) {
	                    			isOneway = true;
	                    		} else {
	                    			isOneway = false;
	                    		}
	                    	}
	                    }
                	}
                }
            	//Nodes werden in DB geschrieben
                if (xmlStreamReader.getEventType() == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("node")) {
                    if (!(node_tag_cityCategory.length() == 0)) { //Wenn der Node einen Place Tag bzw value von Place nicht leer is
                    	ps2.setString(1, nodeID);
                    	ps2.setString(2, nodeLat);
                    	ps2.setString(3, nodeLon);
                    	ps2.setString(4, node_tag_name);
                    	ps2.setString(5, node_tag_is_in);
                    	ps2.setString(6, node_tag_cityCategory);
                    	ps2.executeUpdate();  //INSERT INTO `cities` (`ID`, `lat`, `lon`, `name`, `is_in`, `cityCategory`)
                    }
                	ps1.setString(1, nodeID);
                	ps1.setString(2, nodeLat);
                	ps1.setString(3, nodeLon);
                	ps1.executeUpdate(); //INSERT INTO `nodes` (`ID`, `lat`, `lon`)
                    nodeID = "";
                    nodeLat = "";
                    nodeLon = "";
                    node_tag_name = "";
                    node_tag_is_in = "";
                    node_tag_cityCategory = "";
                }
                //Ways werden in DB geschrieben
                if (xmlStreamReader.getEventType() == XMLStreamConstants.END_ELEMENT && xmlStreamReader.getLocalName().equals("way")) {
                	if (speedID != 0) { //Nur in DB eintragen, wenn eine speedID gesetzt ist (Highway-Tag)
	                    for (int i = 0; i < nd_ref.size() - 1; i++) {
	                    	node1ID =  nd_ref.get(i);
	                    	node2ID = nd_ref.get(i + 1);
	                    	ps3.setString(1, node1ID);
	                    	ps3.setString(2, node2ID);
	                    	ps3.setString(3, wayID);
	                    	ps3.setBoolean(4, isOneway);
	                    	ps3.setInt(5, speedID);
	                    	ps3.executeUpdate(); //INSERT INTO `edges_all` (`ID`, `node1ID`, `node1lat`, `node1lon`, `node2ID`, `node2lat`, `node2lon`, `wayID`, `isOneway`, `speedID`, `length`, `visited`)
	                        if (i == 0) {
		                    	ps6.setString(1, node1ID);
		                    	ps6.setString(2, node2ID);
		                    	ps6.executeQuery();
		                    	rs1 = ps6.executeQuery(); //SELECT ID FROM `edges_all` WHERE `node1ID` = ? AND `node2ID` = ?
		                    	rs1.next();
		                    	startEdgeID = rs1.getInt(1);
	                        }
	                        if (i == nd_ref.size() - 2) {
		                    	ps6.setString(1, node1ID);
		                    	ps6.setString(2, node2ID);
		                    	rs1 = ps6.executeQuery(); //SELECT ID FROM `edges_all` WHERE `node1ID` = ? AND `node2ID` = ?
		                    	rs1.next();
		                    	endEdgeID = rs1.getInt(1);
		                    }
	                    }
	                    ps4.setString(1, wayID);
	                    ps4.setInt(2, startEdgeID);
	                    ps4.setInt(3, endEdgeID);
	                    ps4.setString(4, way_tag_nameValue);
	                    ps4.setString(5, way_tag_highwayValue);
	                    ps4.setBoolean(6, isOneway);
	                    ps4.setString(7, way_tag_ref);
	                    ps4.setString(8, nd_ref.getFirst());
	                    ps4.setString(9, nd_ref.getLast());
	                    ps4.executeUpdate(); //INSERT INTO `ways` (`ID`, `startEdgeID`, `endEdgeID`, `nameValue`, `cityName`, `cityNodeID`, `is_in`, `highwayValue`, `isOneway`, `ref`, `startNodeID`, `endNodeID`)
                	}
                	if (nd_ref.getFirst().equals(nd_ref.getLast()) && way_tag_landuseValue.equals("residential") && (!way_tag_nameValue.equals(""))) { //Trage in edges_borders ein ansonsten in edges_all
	                    for (int i = 0; i < nd_ref.size() - 1; i++) {
	                    	node1ID =  nd_ref.get(i);
	                    	node2ID = nd_ref.get(i + 1);
	                    	ps7.setString(1, node1ID);
	                    	ps7.setString(2, node2ID);
	                    	rs2 = ps7.executeQuery(); //SELECT ID FROM `edges_borders` WHERE `node1ID` = ? AND `node2ID` = ?
	                    	if (!rs2.next()) {
		                    	ps5.setString(1, node1ID);
		                    	ps5.setString(2, node2ID);
		                    	ps5.setString(3, wayID);
		                    	ps5.setString(4, way_tag_nameValue);
		                    	ps5.executeUpdate(); //INSERT INTO `edges_borders` (`ID`, `node1ID`, `node2ID`, `wayID`, `name`)
	                    	}
	                    }
                	}
            	isOneway = false;
            	speedID = 0;
            	node1ID = "";
            	node2ID = "";
                wayID = "";
                way_tag_onewayValue = "";
                way_tag_nameValue = "";
                way_tag_highwayValue = "";
                way_tag_landuseValue = "";
               	way_tag_ref = "";
                nd_ref.clear();
              }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}