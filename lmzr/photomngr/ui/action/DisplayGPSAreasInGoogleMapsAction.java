package lmzr.photomngr.ui.action;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import javax.swing.KeyStroke;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.GPS.GPSData;
import lmzr.photomngr.data.GPS.GPSDatabase;
import lmzr.photomngr.data.GPS.GPSDatabase.GPSRecord;
import lmzr.util.string.HierarchicalCompoundString;

public class DisplayGPSAreasInGoogleMapsAction extends PhotoManagerAction {

	final private GPSDatabase a_GPSDatabase;
	final private PhotoList a_photoList;
	final private ListSelectionManager a_selection;
	final private  String a_cacheDirectory;
	final private String templateName = "resources/googleMapsTemplate.html";
	final private String listPlaceholder = "__LIST_PLACEHOLDER__";
	
	/**
	 * @param text
	 * @param mnemonic
	 * @param accelerator
	 * @param tooltipText
	 * @param GPSDatabase 
	 * @param photoList 
	 * @param selection 
	 */
	public DisplayGPSAreasInGoogleMapsAction(final String text,
			                                 final int mnemonic,
			                                 final KeyStroke accelerator,
			                                 final String tooltipText,
			                                 final GPSDatabase GPSDatabase,
			                                 final PhotoList photoList,
			                                 final ListSelectionManager selection,
			                                 final String cacheDirectory) {
		super(text, mnemonic, accelerator, tooltipText);
		a_GPSDatabase = GPSDatabase;
		a_photoList = photoList;
		a_selection = selection;
		a_cacheDirectory = cacheDirectory;
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {

		final HierarchicalCompoundString location = (HierarchicalCompoundString)(a_photoList.getValueAt(a_selection.getSelection()[0],
				                                                                                        PhotoList.PARAM_LOCATION));
		//final GPSRecord data = a_GPSDatabase.getGPSData(location);

		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL url = classLoader.getResource(templateName);
		if ( url == null ) {
			System.err.println("failed to locate template \""+templateName+"\"");
			return;
		}
		
		final String folder = (String)a_photoList.getValueAt(a_selection.getSelection()[0], PhotoList.PARAM_FOLDER);
		final String filename = "displayGPSAreasInGoogleMaps_" + escape(location.toLongString()) + ".html";
		final File file = new File(a_cacheDirectory + File.separator + folder + File.separator + filename);		
				
		try {
			final InputStream inputStream = url.openStream();
		    final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
		    final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
		    String str;
		    while  ( (str = in.readLine()) != null ) {
		    	out.write(handleLine(str));
		    	out.write('\n');
		    }
		    in.close();
		    out.close();
		} catch (final IOException e1) {
			System.err.println("failed to write file \""+file+"\"");			
			e1.printStackTrace();
		}
		
		try {
			Desktop.getDesktop().browse(file.toURI());
		} catch (final HeadlessException ex) {
			System.err.println("failed to start a browser to display the map of "+location.toString());
			ex.printStackTrace();
		} catch (final UnsupportedOperationException ex) {
			System.err.println("failed to start a browser to display the map of "+location.toString());
			ex.printStackTrace();
		} catch (final IOException ex) {
			System.err.println("failed to start a browser to display the map of "+location.toString());
			ex.printStackTrace();
		}
	}

	/**
	 * generate a legal filename from a string
	 * @param string
	 * @return a acceptable filename (dubious characters are replaced by "_"
	 */
	private String escape(final String string) {
		
		int l = string.length();
		final char[] chars = new char[l];
		string.getChars(0,l,chars,0);
		
		for ( int i = 0; i<l; i++ ) {
			if ( !Character.isLetterOrDigit(chars[i]) ) {
				chars[i] = '_';
			}
		}
		
		return new String(chars);
	}
	
	/**
	 * edit a line by replacing the placeholders by their real content
	 * @param string
	 * @return 
	 */
	private String handleLine(final String string) {
		
		String str = string;
		
		if ( string.indexOf(listPlaceholder)>=0 ) {
			str = str.replace(listPlaceholder, listOfGPSAreas());
		}
		
		return str;
	}
	
	private String listOfGPSAreas() {
		final StringBuilder s = listOfGPSAreasRecurse((HierarchicalCompoundString)a_GPSDatabase.getRoot());
		return s.toString();
	}
	
	/**
	 * @param location
	 * @return
	 */
	private StringBuilder listOfGPSAreasRecurse(final HierarchicalCompoundString location) {
		
		final StringBuilder str = new StringBuilder();
		boolean stringHasBeenAdded = false;
		
		System.out.println("--- start ---");
		System.out.println("managing "+location);
		final GPSRecord record =  a_GPSDatabase.getGPSData(location);
		if ( record != null ) {
			final GPSData data = record.getGPSData();
			if ( ( data!=null) && data.isComplete() ) {
				str.append("new GPSRectangle(\"");
				str.append(location);
				str.append("\",");
				str.append(data.getLatitudeMinAsDouble());
				str.append(",");
				str.append(data.getLatitudeMaxAsDouble());
				str.append(",");
				str.append(data.getLongitudeMinAsDouble());
				str.append(",");
				str.append(data.getLongitudeMaxAsDouble());
				str.append(",\"red\")");
				stringHasBeenAdded = true;				
				System.out.println("adding String");
			}
		}
		
		for ( int i=0; i<a_GPSDatabase.getChildCount(location); i++) {
			final StringBuilder s = listOfGPSAreasRecurse((HierarchicalCompoundString)a_GPSDatabase.getChild(location, i));
			if ( s.length() > 0) {
				if ( stringHasBeenAdded ) {
					str.append(",\n");
					System.out.println("adding newline");
				}
				str.append(s);
				stringHasBeenAdded = true;
				System.out.println("adding Substring"+s);
			}
		}
		System.out.println("--- end ---");
		
		return str;
	}
	
}
