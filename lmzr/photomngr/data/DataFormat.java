package lmzr.photomngr.data;

/**
 *
 */
public class DataFormat {

	/**
	 * 
	 */
	final static public DataFormat JPEG = new DataFormat("JPEG/JIFF Image", new String[]{"jpg","jpeg"});
	/**
	 * 
	 */
	final static public DataFormat MPEG = new DataFormat("MPEG", new String[]{"mpg","mpeg"});
	/**
	 * 
	 */
	final static public DataFormat WAV = new DataFormat("Waveform Audio", new String[]{"wav"});
	/**
	 * 
	 */
	final static public DataFormat MP3 = new DataFormat("MPEG Audio Stream, Layer III", new String[]{"mp3"});
	/**
	 * 
	 */
	final static public DataFormat AVI = new DataFormat("Audio Video Interleave", new String[]{"avi"});
	/**
	 * 
	 */
	final static public DataFormat VOB = new DataFormat("DVD Video Movie", new String[]{"vob"});
	/**
	 * 
	 */
	final static public DataFormat GIF = new DataFormat("Graphics Interchange Format", new String[]{"gif"});
	/**
	 * 
	 */
	final static public DataFormat FLV = new DataFormat("Flash Video", new String[]{"flv"});
	/**
	 * 
	 */
	final static public DataFormat MOV = new DataFormat("QuickTime", new String[]{"mov"});
	/**
	 * 
	 */
	final static public DataFormat WMV = new DataFormat("WindowMediaPlayer", new String[]{"wmv"});
		
	final private String a_format;
	final private String a_extensions[];
	
	/**
	 * @param format
	 * @param extensions
	 */
	private DataFormat(final String format,
			           final String extensions[]) {
		a_format = format;
		a_extensions = extensions;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return a_format;
	}
	
	/**
	 * @param filename
	 * @return true is the file is of this format
	 */
	public boolean hasFormat(final String filename) {
		
		final String f = filename.toLowerCase();

		for (String e : a_extensions) {
			if (f.endsWith("."+e)) return true;
		}
		
		return false;
	}
	
	/**
	 * @return list of all formats
	 */
	static public DataFormat[] getAllFormats() {
		final DataFormat all[] = { DataFormat.JPEG,
				                   DataFormat.MPEG,
				                   DataFormat.WAV,
				                   DataFormat.MP3,
				                   DataFormat.AVI,
				                   DataFormat.VOB,
				                   DataFormat.GIF,
				                   DataFormat.FLV,
				                   DataFormat.MOV,
				                   DataFormat.WMV };
		return all;
	}
}
