package de.htwmaps.util;



import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;

/**
 * @author Christian Rech, Stanislaw Tartakowski
 * 
 * Diese Klasse dient als sehr performantes Hilfsmittel zum schreiben von Ereignissen in eine Datei
 * und, oder Stdout. Es kann zusaetzlich fuer jedes Ereignis eine Prioritaet festgelegt werden, sodass
 * man schneller und einfacher die Ereignismeldungen regulieren kann, als mit beispielsweise System.out.
 * 
 * Prioritaeten sind von links nach rechts aufsteigend sortiert: 
 * ALL > TRACE > DEBUG > INFO > WARN > ERROR > FATAL > OFF
 * 
 * Beispiel: InitLogger.INSTANCE.getLogger().setLevel( Level.WARN )
 * 
 * Diese Klasse ist ein enum, um die bestmoegliche Art und Weise zu erreichen, ein Singletonmuster in
 * Java zu implementieren. Dieser Ansatz wird vom chief Java architect bei Google, Joshua Bloch, empfohlen.
 * Siehe Effective Java Second Edition S. 18
 */
public enum InitLogger {
	INSTANCE;
	private Logger htwLogger;

	/**
	 * Initialisierung des Loggers. Muss nur einmal pro Laufzeit aufgerufen werden.
	 * Ereignisanzeige sowohl auf Stdout als auch in eine Datei
	 * @param fileName Der Dateipfad der Datei, in die, die Ereignismeldung geschrieben wird
	 */
	public void initLogger(String fileName) {
		if (htwLogger == null) {
			htwLogger = Logger.getRootLogger();

			PatternLayout layout = new PatternLayout(
					"%d{ISO8601} %-5p [%t] %c: (%F:%L) %m%n"
					);

			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			htwLogger.addAppender(consoleAppender);

			if (fileName != null && !fileName.equals("")) {
				try {
					FileAppender fileAppender = new FileAppender(layout, fileName, false);
					htwLogger.addAppender(fileAppender);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			htwLogger.setLevel(Level.ALL);
		}
	}
	
	/**
	 * Initialisierung des Logers um die Ereignisanzeige nur auf Stdout zu beschraenken.
	 * Muss nur einmal pro Laufzeit aufgerufen werden.
	 */
	public void initLogger() {
		initLogger(null);
	}

	/**
	 * Schnittstelle zum Loggerobjekt
	 * @return Referenz auf das Singleton Loggerobjekt
	 */
	public Logger getLogger() {
		return htwLogger;
	}
}
