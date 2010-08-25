package de.htwmaps.database.importscripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.htwmaps.database.DBConnector;

public class UpdatePartOfHightway {

	public UpdatePartOfHightway() {
		start();
	}

	private void start() {
		Connection con = DBConnector.getConnection();
		try {
			PreparedStatement ps1 = con.prepareStatement("UPDATE edges SET partOfHighway = 1 WHERE wayID IN (SELECT r_way_tag.wayID FROM r_way_tag, k_tags WHERE k_tags.ID = r_way_tag.tagID AND k_tags.key = 'highway' AND k_tags.value IN ('motorway','motorway_link','trunk','trunk_link','primary','primary_link','secondary','secondary_link','tertiary','unclassified','road','residential','living_street'))");
			PreparedStatement ps2 = con.prepareStatement("UPDATE nodes SET partOfHighway = 1 WHERE ID IN (SELECT fromNodeID FROM edges WHERE partOfHighway = 1)");
			PreparedStatement ps3 = con.prepareStatement("UPDATE nodes SET partOfHighway = 1 WHERE ID IN (SELECT toNodeID FROM edges WHERE partOfHighway = 1)");
			ps1.executeUpdate();
			ps1.close();
			ps2.executeUpdate();
			ps2.close();
			ps3.executeUpdate();
			ps3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}