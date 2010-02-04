package lmzr.photomngr.data;

/**
 *
 */
public class DataFormatFactory {

	/**
	 * 
	 */
	public DataFormatFactory() {
	}
	
	/**
	 * @param filename
	 * @return format of the file
	 */
	public DataFormat createFormat(final String filename) {
		for (DataFormat format: DataFormat.getAllFormats() ) {
			if ( format.hasFormat(filename)) return format;
		}
//		if ( DataFormat.JPEG.hasFormat(filename)) return DataFormat.JPEG;
//		if ( DataFormat.MPEG.hasFormat(filename)) return DataFormat.MPEG;
//		if ( DataFormat.WAV.hasFormat(filename)) return DataFormat.WAV;
//		if ( DataFormat.MP3.hasFormat(filename)) return DataFormat.MP3;
//		if ( DataFormat.AVI.hasFormat(filename)) return DataFormat.AVI;
//		if ( DataFormat.VOB.hasFormat(filename)) return DataFormat.VOB;
//		if ( DataFormat.GIF.hasFormat(filename)) return DataFormat.GIF;
//		if ( DataFormat.FLV.hasFormat(filename)) return DataFormat.FLV;
//		if ( DataFormat.VOB.hasFormat(filename)) return DataFormat.VOB;
		return null;
	}
}
