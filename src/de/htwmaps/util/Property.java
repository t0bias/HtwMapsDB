package de.htwmaps.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Name: <code>Property</code>
 * 
 * Hiermit koennen Eigenschaften aus einer Datei gelesen werden
 * <ul>
 * 	 <li>@author CR</li>
 * 	 <li>@version:
 * 			<ul>
 * 				<li> 1.0 - 26.05.2010 - Klasse erstellt</li>
 * 				<li> 1.1 - 10.0.2010 - Kommentare hinzugefuegt</li>
 * 			</ul>
 * </li>
 * </ul>
 */

public class Property {
	
	//Attribute
	private Properties properties;
	
	/**
	 * Konstruktor: Oeffnet die Angegeben Datei und ladet sie in einen Stream
	 * @param filename	Dateiname der einzulesenden Datei
	 */
	public Property(String filename){
		properties = new Properties();
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream("./config/" + filename.toString());
			properties.load(stream);
			
			if (stream != null)
				stream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * get-Methode um Parameter auszulesen
	 * @param name	zugewiesener Name der erwuenschten Variable 
	 * @return Inhalt der Variable
	 */
	public String getProp(String name) {
		return properties.getProperty(name);
	}
}