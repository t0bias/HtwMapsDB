package de.htwmaps.database.importscripts;

import java.util.Date;

public class DataImport {
	private final static String MESSAGE_NO_PARAMETER = "Sie muessen eine OSM-Datei zum Parsen uebergeben!";
	private String READ_PATH;

	public DataImport(String READ_PATH) {
		this.READ_PATH = READ_PATH;
	}

	private void start() {
		print("Preparing database for import...");
		new PrepareDatabase();
		print("Preparing finished!");

		print("Parse OSM-File...");
		//new ParseOSM(READ_PATH);
		print("Parse OSM-File finished!");

		
	}
	/**
	 * 
	 * @param args[0] Dateiname und Pfad der OSM-Datei, die geparst werden soll
	 */
	public static void main(String[] args) {
		if (args[0].trim().length() == 0) {
			System.out.println(MESSAGE_NO_PARAMETER);
			System.exit(0);
		}
		DataImport di = new DataImport(args[0]);
		di.start();
	}

	private void print(String s) {
		Date currentTime = new Date();
		System.out.println(currentTime + ": " + s);
	}
}