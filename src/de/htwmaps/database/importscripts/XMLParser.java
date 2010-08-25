package de.htwmaps.database.importscripts;

import java.util.Date;

public class XMLParser {
	private final static String MESSAGE_NO_PARAMETER = "Sie muessen eine OSM-Datei zum Parsen uebergeben!";
	private String READ_PATH;

	public XMLParser(String READ_PATH) {
		this.READ_PATH = READ_PATH;
	}

	private void start() {
		print("Parse OSM-File...");
		new ParseOSM(READ_PATH);
		print("Parse OSM-File finished!");

		//Update Methoden: nodes/ways/edges/tags muessen schon in der DB eingetragen sein
//		print("Updating PartOfHighway...");
//		new UpdatePartOfHightway();
//		print("Updating finished!");
//
//		print("Update edge.length");
//		new CalculateEdgeLength();
//		print("Update edge.length finished!");
//		
//		
//
//		print("Updating streets...");
//		try {
//			UpdateStreets.main();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		print("Updating finished!");
//
//		print("XMLParser finished!");
		
	}
	/**
	 * 
	 * @param args[0] Dateiname und Pfad der OSM-Datei, die geparst werden soll
	 */
	public static void main(String[] args) {
		String READ_PATH = args[0];
		if (READ_PATH.trim().length() == 0) {
			System.out.println(MESSAGE_NO_PARAMETER);
			System.exit(0);
		}
		XMLParser parser = new XMLParser(READ_PATH);
		parser.start();
	}

	private void print(String s) {
		Date currentTime = new Date();
		System.out.println(currentTime + ": " + s);
	}
}