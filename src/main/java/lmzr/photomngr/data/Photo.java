package lmzr.photomngr.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 *
 */
public class Photo {

    static private String a_rootDir;
    static private PhotoHeaderDataCache a_photoHeaderDataCache;
    private String a_folder;
    private String a_filename;
    final private PhotoIndexData a_indexData;
    private PhotoHeaderData a_headerData;
    final private DataFormat a_format;
    private boolean a_isOK;

    static final StringPool s_pool = new StringPool();

    /**
     * awful static initialization, to be clean up
     *
     * @param rootDir
     * @param photoHeaderDataCache
     */
    static public void initializeByDirtyHack(final String rootDir,
                                             final PhotoHeaderDataCache photoHeaderDataCache) {

        a_rootDir = rootDir;
        a_photoHeaderDataCache = photoHeaderDataCache;
    }

    /**
     * @param data
     * @param locationFactory
     * @param subjectFactory
     * @param authorFactory
     */
    public Photo(final String data[],
                 final HierarchicalCompoundStringFactory locationFactory,
                 final MultiHierarchicalCompoundStringFactory subjectFactory,
                 final AuthorFactory authorFactory) {

        this.a_folder = s_pool.replace(data[0]);
        this.a_filename = data[1];
        this.a_format = DataFormatFactory.createFormat(getFullPath());
        this.a_indexData = new PhotoIndexData(data, locationFactory, subjectFactory, authorFactory);
        this.a_headerData = null;
        this.a_isOK = true;
    }

    /**
     * @param folderName
     * @param filename
     * @param locationFactory
     * @param subjectFactory
     * @param authorFactory
     */
    public Photo(final String folderName,
                 final String filename,
                 final HierarchicalCompoundStringFactory locationFactory,
                 final MultiHierarchicalCompoundStringFactory subjectFactory,
                 final AuthorFactory authorFactory) {
        this.a_folder = s_pool.replace(folderName);
        this.a_filename = filename;
        this.a_format = DataFormatFactory.createFormat(getFullPath());
        this.a_indexData = new PhotoIndexData(locationFactory, subjectFactory, authorFactory);
        this.a_headerData = null;
        this.a_isOK = true;
    }
    /**
     * @return the image
     */
    public BufferedImage getImage() {

        if ( !this.a_isOK ) return null;
        String filename = getFullPath();

        // if this is an AVI file, try to read the corresponding THM file
        if ( this.a_format == DataFormat.AVI ) {
            String f = "";
            if ( filename.endsWith(".AVI") ) f = filename.substring(0,filename.length()-3) + "THM";
            if ( filename.endsWith(".avi") ) f = filename.substring(0,filename.length()-3) + "thm";
            final File ff = new File(f);
            if ( !ff.exists()) return null;
            filename = f;
        }

        final File file = new File(filename);
        try {
            return ImageIO.read(file);
        } catch (final IOException e) {
            System.out.println("failed to parse "+filename);
            this.a_isOK = false;
            return null;
        }
    }

    /**
     * @return folder
     */
    public String getFolder() {
        return this.a_folder;
    }

    /**
     * override the folder name (e.g. used when a folder is renamed)
     * @param folder
     */
    void overrideFolder(final String folder) {
        this.a_folder = folder;
    }

    /**
     * @return filename
     */
    public String getFilename() {
        return this.a_filename;
    }

    /**
     * override the filename (e.g. used when a file is renamed)
     * @param filename
     */
    void overrideFilename(final String filename) {
        this.a_filename = filename;
    }

    /**
     * @return data extracted from the index data
     */
    public PhotoIndexData getIndexData() {
        return this.a_indexData;
    }

    /**
     * @return data extracted from the photo header
     */
    public PhotoHeaderData getHeaderData() {
        if ( this.a_headerData == null ) {
            this.a_headerData = a_photoHeaderDataCache.getHeaderData(this.a_folder, this.a_filename, this.a_format);
        }

        return this.a_headerData;
    }

    /**
     * @return full path of the photo file
     */
    public String getFullPath() {
        return a_rootDir + File.separator + this.a_folder + File.separator + this.a_filename;
    }

    /**
     * @return format of the file
     */
    public DataFormat getFormat() {
        return this.a_format;
    }
}
