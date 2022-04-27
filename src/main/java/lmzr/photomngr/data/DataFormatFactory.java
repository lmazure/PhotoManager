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
    public static DataFormat createFormat(final String filename) {
        for (DataFormat format: DataFormat.getAllFormats() ) {
            if ( format.hasFormat(filename)) return format;
        }
        return null;
    }
}
