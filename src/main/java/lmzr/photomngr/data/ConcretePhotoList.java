package lmzr.photomngr.data;

// the asynchronous version which has been given up because slower is recorded in revision 33

import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.scheduler.Scheduler;
import lmzr.util.io.StringTableFromToExcel;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * @author Laurent Mazuré
 */
public class ConcretePhotoList extends Object
                               implements PhotoList {

    final private Vector<Photo> a_listOfPhotos;
    final private Vector<TableModelListener> a_listOfListeners;
    final private Vector<PhotoListMetaDataListener> a_listOfMetaDataListeners;
    final private Vector<SaveListener> a_listOfSaveListeners;
    final private String a_excelFilename;
    final private HierarchicalCompoundStringFactory a_locationFactory;
    final private MultiHierarchicalCompoundStringFactory a_subjectFactory;
    final private AuthorFactory a_authorFactory;
    final private Scheduler a_scheduler;
    final private PhotoHeaderDataCache a_photoHeaderDataCache;
    private boolean a_isSaved;

    /**
     * @param excelFilename
     * @param rootDirPhoto
     * @param cacheDir
     * @param scheduler
     */
    public ConcretePhotoList(final String excelFilename,
                             final String rootDirPhoto,
                             final String cacheDir,
                             final Scheduler scheduler) {

        a_listOfListeners = new Vector<>();
        a_listOfMetaDataListeners = new Vector<>();
        a_listOfSaveListeners = new Vector<>();
        a_locationFactory = new HierarchicalCompoundStringFactory();
        a_subjectFactory = new MultiHierarchicalCompoundStringFactory();
        a_authorFactory = new AuthorFactory();
        a_excelFilename = excelFilename;
        a_scheduler = scheduler;
        a_isSaved = true;
        a_listOfPhotos = new Vector<>();
        a_photoHeaderDataCache = new PhotoHeaderDataCache(rootDirPhoto, cacheDir, scheduler);
        Photo.initializeByDirtyHack(rootDirPhoto, a_photoHeaderDataCache);

        String data[][] = null;
        if (!Files.exists(Paths.get(excelFilename))) {
            System.err.println("Database file (" + excelFilename + ") does not exist.");
            data = new String[0][0];
        } else {
            // load the data
            try {
                data = StringTableFromToExcel.read(excelFilename);
            } catch (final ProtocolException e) {
                System.err.println("Cannot parse file " + excelFilename);
                e.printStackTrace();
                System.exit(1);
            } catch (final IOException e) {
                System.err.println("Cannot read file " + excelFilename);
                e.printStackTrace();
                data = new String[0][0];
            }
        }

        // quick check that the data is not corrupted
        for (int i=1; i<data.length; i++) {
            if (data[i][0]=="") {
                System.err.println(excelFilename + " is corrupted: no folder name at line " + (i+1));
                System.exit(1);
            }
            if (data[i][1]=="") {
                System.err.println(excelFilename + " is corrupted: no file name at line " + (i+1));
                System.exit(1);
            }
        }

        // update the list of files relatively to the content of the file system
        String previousFolderName = "";
        Vector<String> currentFolderContent = new Vector<>();
        final Vector<String> folderListOnDisk = getFolderListOnDisk(rootDirPhoto);
        for (int i=1; i<data.length; i++) {
            final String folderName = data[i][0];
            if ( ! previousFolderName.equals(folderName) ) {
                if ( folderListOnDisk.contains(folderName)) {
                    folderListOnDisk.remove(folderName);
                } else {
                    System.err.println("folder \""+folderName+"\" does not exists on the disk");
                }
                sortFolderContentByDateTime(rootDirPhoto,previousFolderName,currentFolderContent);
                for (int j=0; j<currentFolderContent.size(); j++) {
                    final String fileName = currentFolderContent.get(j);
                    final Photo photo = new Photo(previousFolderName,fileName,a_locationFactory,a_subjectFactory,a_authorFactory);
                    a_listOfPhotos.add(photo);
                    System.err.println(photo.getFullPath()+" is missing from the index");
                    setAsUnsaved();
                }
                currentFolderContent = getFolderContentOnDisk(rootDirPhoto, folderName);
                previousFolderName = folderName;
            }
            final String fileName = data[i][1];
            final Photo photo = new Photo(data[i],a_locationFactory,a_subjectFactory,a_authorFactory);
            a_listOfPhotos.add(photo);
            if ( currentFolderContent.contains(fileName) ) {
                currentFolderContent.remove(fileName);
            } else {
                System.err.println(photo.getFullPath()+" does not exist on the disk");
            }
        }
        sortFolderContentByDateTime(rootDirPhoto,previousFolderName,currentFolderContent);
        for (int j=0; j<currentFolderContent.size(); j++) {
            final String fileName = currentFolderContent.get(j);
            final Photo photo = new Photo(previousFolderName,fileName,a_locationFactory,a_subjectFactory,a_authorFactory);
            a_listOfPhotos.add(photo);
            System.err.println(photo.getFullPath()+" is missing from the index");
            setAsUnsaved();
        }
        for (int i=0; i<folderListOnDisk.size(); i++) {
            final String folderName = folderListOnDisk.get(i);
            System.err.println("folder \""+folderName+"\" is missing from the index");
            currentFolderContent = getFolderContentOnDisk(rootDirPhoto, folderName);
            sortFolderContentByDateTime(rootDirPhoto,folderName,currentFolderContent);
            for (int j=0; j<currentFolderContent.size(); j++) {
                final String fileName = currentFolderContent.get(j);
                final Photo photo = new Photo(folderName,fileName,a_locationFactory,a_subjectFactory,a_authorFactory);
                a_listOfPhotos.add(photo);
                System.err.println(photo.getFullPath()+" is missing from the index");
            }
            setAsUnsaved();
        }

    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {

        return a_listOfPhotos.size();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return NB_PARAM;
    }

    /**
     * @param rowIndex
     * @return photo
     */
    @Override
    public Photo getPhoto(final int rowIndex) {

        return a_listOfPhotos.get(rowIndex);
    }

    /**
     * @return flag indicating if the current values are saved
     */
    @Override
    public boolean isSaved() {

        return a_isSaved;
    }

    /**
     * record and notify that the data saved on disk is obsolete<BR/>
     * <B>the write lock must be owned by the calling routine</B>
     */
    private void setAsUnsaved() {

        if (a_isSaved) {
            a_isSaved = false;
            final SaveEvent f = new SaveEvent(this, false);
            for (final SaveListener l : a_listOfSaveListeners) {
                l.saveChanged(f);
            }
        }

    }

    /**
     * record and notify that the data saved on disk is obsolete
     */
    private void setAsSaved() {

        a_isSaved = true;

        final SaveEvent f = new SaveEvent(this, true);
        for (final SaveListener l : a_listOfSaveListeners) {
            l.saveChanged(f);
        }
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(final int columnIndex) {
        return switch (columnIndex) {
        case PARAM_FOLDER -> "folder";
        case PARAM_FILENAME -> "filename";
        case PARAM_LOCATION -> "location (index)";
        case PARAM_ORIENTATION -> "orientation (header)";
        case PARAM_SUBJECT -> "subject (index)";
        case PARAM_QUALITY -> "quality (index)";
        case PARAM_ORIGINALITY -> "originality (index)";
        case PARAM_PRIVACY -> "privacy (index)";
        case PARAM_DATE -> "date (header)";
        case PARAM_PANORAMA -> "panorama (index)";
        case PARAM_PANORAMA_FIRST -> "panorama first (index)";
        case PARAM_AUTHOR -> "author (index)";
        case PARAM_COPIES -> "copies (index)";
        case PARAM_ZOOM -> "zoom (index)";
        case PARAM_FOCUS_X -> "focus X (index)";
        case PARAM_FOCUS_Y -> "focus Y (index)";
        case PARAM_ROTATION -> "rotation (index)";
        case PARAM_MANUFACTURER -> "manufacturer (header)";
        case PARAM_MODEL -> "model (header)";
        case PARAM_EXPOSURE_TIME -> "exposure time (header)";
        case PARAM_SHUTTER_SPEED -> "shutter speed (header)";
        case PARAM_APERTURE_VALUE -> "aperture value (header)";
        case PARAM_FLASH -> "flash (header)";
        case PARAM_FOCAL_LENGTH -> "focal length (header)";
        case PARAM_SELF_TIMER_MODE -> "Self timer mode (header)";
        case PARAM_CANON_SELF_TIMER_DELAY -> "Canon self timer delay (header)";
        case PARAM_CANON_FLASH_MODE -> "Canon flash mode (header)";
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE -> "Canon continuous drive mode (header)";
        case PARAM_CANON_FOCUS_MODE -> "Canon focus mode (header)";
        case PARAM_CANON_ISO -> "Canon ISO (header)";
        case PARAM_CANON_SUBJECT_DISTANCE -> "Canon subject distance (header)";
        case PARAM_HEIGHT -> "Height (header)";
        case PARAM_WITDH -> "Width (header)";
        case PARAM_FORMAT -> "Format";
        default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return switch (columnIndex) {
        case PARAM_FOLDER, PARAM_FILENAME, PARAM_PANORAMA, PARAM_PANORAMA_FIRST, PARAM_AUTHOR, PARAM_MANUFACTURER, PARAM_MODEL, PARAM_EXPOSURE_TIME, PARAM_SHUTTER_SPEED, PARAM_APERTURE_VALUE, PARAM_FLASH ->
            String.class;
        case PARAM_FOCAL_LENGTH -> Double.class;
        case PARAM_SELF_TIMER_MODE, PARAM_CANON_SELF_TIMER_DELAY, PARAM_CANON_FLASH_MODE, PARAM_CANON_CONTINUOUS_DRIVE_MODE, PARAM_CANON_FOCUS_MODE, PARAM_CANON_ISO -> String.class;
        case PARAM_CANON_SUBJECT_DISTANCE -> Integer.class;
        case PARAM_ZOOM, PARAM_FOCUS_X, PARAM_FOCUS_Y, PARAM_ROTATION -> Float.class;
        case PARAM_LOCATION -> HierarchicalCompoundString.class;
        case PARAM_SUBJECT -> MultiHierarchicalCompoundString.class;
        case PARAM_ORIENTATION, PARAM_HEIGHT, PARAM_WITDH -> Integer.class;
        case PARAM_QUALITY -> PhotoQuality.class;
        case PARAM_ORIGINALITY -> PhotoOriginality.class;
        case PARAM_PRIVACY -> PhotoPrivacy.class;
        case PARAM_DATE -> Date.class;
        case PARAM_COPIES -> Integer.class;
        case PARAM_FORMAT -> DataFormat.class;
        default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(final int rowIndex,
                                  final int columnIndex) {
        return switch (columnIndex) {
        case PARAM_PRIVACY, PARAM_QUALITY, PARAM_ORIGINALITY, PARAM_COPIES, PARAM_SUBJECT, PARAM_ZOOM, PARAM_FOCUS_X, PARAM_FOCUS_Y, PARAM_ROTATION, PARAM_AUTHOR, PARAM_LOCATION ->
            true;
        case PARAM_FOLDER, PARAM_FILENAME, PARAM_ORIENTATION, PARAM_PANORAMA, PARAM_PANORAMA_FIRST, PARAM_DATE, PARAM_MANUFACTURER, PARAM_MODEL, PARAM_EXPOSURE_TIME, PARAM_SHUTTER_SPEED, PARAM_APERTURE_VALUE, PARAM_FLASH,
                PARAM_FOCAL_LENGTH, PARAM_SELF_TIMER_MODE, PARAM_CANON_SELF_TIMER_DELAY, PARAM_CANON_FLASH_MODE, PARAM_CANON_CONTINUOUS_DRIVE_MODE, PARAM_CANON_FOCUS_MODE, PARAM_CANON_ISO, PARAM_CANON_SUBJECT_DISTANCE, PARAM_HEIGHT, PARAM_WITDH, PARAM_FORMAT ->
            false;
        default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(final int rowIndex,
                             final int columnIndex) {
        Object value = null;

        value = switch (columnIndex) {
        case PARAM_FOLDER -> getPhoto(rowIndex).getFolder();
        case PARAM_FILENAME -> getPhoto(rowIndex).getFilename();
        case PARAM_LOCATION -> getPhoto(rowIndex).getIndexData().getLocation();
        case PARAM_ORIENTATION -> Integer.valueOf(getPhoto(rowIndex).getHeaderData().getOrientation());
        case PARAM_SUBJECT -> getPhoto(rowIndex).getIndexData().getSubject();
        case PARAM_QUALITY -> getPhoto(rowIndex).getIndexData().getQuality();
        case PARAM_ORIGINALITY -> getPhoto(rowIndex).getIndexData().getOriginality();
        case PARAM_PRIVACY -> getPhoto(rowIndex).getIndexData().getPrivacy();
        case PARAM_DATE -> getPhoto(rowIndex).getHeaderData().getDate();
        case PARAM_PANORAMA -> getPhoto(rowIndex).getIndexData().getPanorama();
        case PARAM_PANORAMA_FIRST -> getPhoto(rowIndex).getIndexData().getPanoramaFirst();
        case PARAM_AUTHOR -> getPhoto(rowIndex).getIndexData().getAuthor();
        case PARAM_COPIES -> Integer.valueOf(getPhoto(rowIndex).getIndexData().getCopies());
        case PARAM_ZOOM -> Float.valueOf(getPhoto(rowIndex).getIndexData().getZoom());
        case PARAM_FOCUS_X -> Float.valueOf(getPhoto(rowIndex).getIndexData().getFocusX());
        case PARAM_FOCUS_Y -> Float.valueOf(getPhoto(rowIndex).getIndexData().getFocusY());
        case PARAM_ROTATION -> Float.valueOf(getPhoto(rowIndex).getIndexData().getRotation());
        case PARAM_MANUFACTURER -> getPhoto(rowIndex).getHeaderData().getManufacturer();
        case PARAM_MODEL -> getPhoto(rowIndex).getHeaderData().getModel();
        case PARAM_EXPOSURE_TIME -> getPhoto(rowIndex).getHeaderData().getExposureTime();
        case PARAM_SHUTTER_SPEED -> getPhoto(rowIndex).getHeaderData().getShutterSpeed();
        case PARAM_APERTURE_VALUE -> getPhoto(rowIndex).getHeaderData().getApertureValue();
        case PARAM_FLASH -> getPhoto(rowIndex).getHeaderData().getFlash();
        case PARAM_FOCAL_LENGTH -> {
            final PhotoHeaderData d = getPhoto(rowIndex).getHeaderData();
            yield d.getFocalLength();
        }
        case PARAM_SELF_TIMER_MODE -> getPhoto(rowIndex).getHeaderData().getSelfTimerMode();
        case PARAM_CANON_SELF_TIMER_DELAY -> getPhoto(rowIndex).getHeaderData().getCanonSelfTimerDelay();
        case PARAM_CANON_FLASH_MODE -> getPhoto(rowIndex).getHeaderData().getCanonFlashMode();
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE -> getPhoto(rowIndex).getHeaderData().getCanonContinuousDriveMode();
        case PARAM_CANON_FOCUS_MODE -> getPhoto(rowIndex).getHeaderData().getCanonFocusMode();
        case PARAM_CANON_ISO -> getPhoto(rowIndex).getHeaderData().getCanonISO();
        case PARAM_CANON_SUBJECT_DISTANCE -> Integer.valueOf(getPhoto(rowIndex).getHeaderData().getCanonSubjectDistance());
        case PARAM_HEIGHT -> Integer.valueOf(getPhoto(rowIndex).getHeaderData().getHeight());
        case PARAM_WITDH -> Integer.valueOf(getPhoto(rowIndex).getHeaderData().getWidth());
        case PARAM_FORMAT -> getPhoto(rowIndex).getFormat();
        default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };

        return value;
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(final Object value,
                           final int rowIndex,
                           final int columnIndex) {

        final NumberFormat format = NumberFormat.getInstance();

        switch (columnIndex) {
        case PARAM_FOLDER: {
            final String v = (String)value;
            getPhoto(rowIndex).overrideFolder(v);
            break;
        }
        case PARAM_FILENAME: {
            final String v = (String)value;
            getPhoto(rowIndex).overrideFilename(v);
            break;
        }
        case PARAM_SUBJECT: {
            MultiHierarchicalCompoundString subject;
            if ( value instanceof MultiHierarchicalCompoundString ) {
                subject = (MultiHierarchicalCompoundString)value;
            } else {
                subject = a_subjectFactory.create((String)value);
            }
            if ( !subject.equals(getPhoto(rowIndex).getIndexData().getSubject()) ) {
                getPhoto(rowIndex).getIndexData().setSubject(subject);
            }
            break; }
        case PARAM_LOCATION: {
            HierarchicalCompoundString location;
            if ( value instanceof HierarchicalCompoundString ) {
                location = (HierarchicalCompoundString)value;
            } else {
                location = a_locationFactory.create((String)value);
            }
            if ( location!=getPhoto(rowIndex).getIndexData().getLocation() ) {
                getPhoto(rowIndex).getIndexData().setLocation(location);
            }
            break; }
        case PARAM_AUTHOR: {
            final String v = (String)value;
            final String vv = a_authorFactory.create(v);
            if ( vv!=getPhoto(rowIndex).getIndexData().getAuthor() ) {
                getPhoto(rowIndex).getIndexData().setAuthor(v);
            }
            break; }
        case PARAM_QUALITY: {
            PhotoQuality quality;
            if ( value instanceof PhotoQuality ) {
                quality = (PhotoQuality)value;
            } else {
                quality = PhotoQuality.parse((String)value);
            }
            if ( !quality.equals(getPhoto(rowIndex).getIndexData().getQuality()) ) {
                getPhoto(rowIndex).getIndexData().setQuality(quality);
            }
            break; }
        case PARAM_ORIGINALITY: {
            PhotoOriginality originality;
            if ( value instanceof PhotoOriginality ) {
                originality = (PhotoOriginality)value;
            } else {
                originality = PhotoOriginality.parse((String)value);
            }
            if ( !originality.equals(getPhoto(rowIndex).getIndexData().getOriginality()) ) {
                getPhoto(rowIndex).getIndexData().setOriginality(originality);
            }
            break; }
        case PARAM_PRIVACY: {
            PhotoPrivacy privacy;
            if ( value instanceof PhotoPrivacy ) {
                privacy = (PhotoPrivacy)value;
            } else {
                privacy = PhotoPrivacy.parse((String)value);
            }
            if ( !privacy.equals(getPhoto(rowIndex).getIndexData().getPrivacy()) ) {
                getPhoto(rowIndex).getIndexData().setPrivacy(privacy);
            }
            break; }
        case PARAM_COPIES: {
            Integer copies;
            if ( value instanceof Integer ) {
                copies = (Integer)value;
            } else {
                try {
                    copies = Integer.valueOf(format.parse((String)value).intValue());
                } catch (final ParseException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            if ( copies.intValue() != getPhoto(rowIndex).getIndexData().getCopies() ) {
                getPhoto(rowIndex).getIndexData().setCopies(copies.intValue());
            }
            break; }
        case PARAM_ZOOM: {
            Float zoom;
            if ( value instanceof Float ) {
                zoom = (Float)value;
            } else {
                try {
                    zoom = Float.valueOf(format.parse((String)value).floatValue());
                } catch (final ParseException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            if ( zoom.floatValue() != getPhoto(rowIndex).getIndexData().getZoom() ) {
                getPhoto(rowIndex).getIndexData().setZoom(zoom.floatValue());
            }
            break; }
        case PARAM_FOCUS_X: {
            Float focusX;
            if ( value instanceof Float ) {
                focusX = (Float)value;
            } else {
                try {
                    focusX = Float.valueOf(format.parse((String)value).floatValue());
                } catch (final ParseException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            if ( focusX.floatValue() != getPhoto(rowIndex).getIndexData().getFocusX() ) {
                getPhoto(rowIndex).getIndexData().setFocusX(focusX.floatValue());
            }
            break; }
        case PARAM_FOCUS_Y: {
            Float focusY;
            if ( value instanceof Float ) {
                focusY = (Float)value;
            } else {
                try {
                    focusY = Float.valueOf(format.parse((String)value).floatValue());
                } catch (final ParseException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            if ( focusY.floatValue() != getPhoto(rowIndex).getIndexData().getFocusY() ) {
                getPhoto(rowIndex).getIndexData().setFocusY(focusY.floatValue());

            }
            break; }
        case PARAM_ROTATION: {
            Float rotation;
            if ( value instanceof Float ) {
                rotation = (Float)value;
            } else {
                try {
                    rotation = Float.valueOf(format.parse((String)value).floatValue());
                } catch (final ParseException e1) {
                    e1.printStackTrace();
                    return;
                }
            }
            float vnorm = rotation.floatValue();
            vnorm = vnorm % 360;
            if (vnorm>180) {
                vnorm -= 360;
            }
            if (vnorm<=-180) {
                vnorm += 360;
            }
            if ( vnorm != getPhoto(rowIndex).getIndexData().getRotation() ) {
                getPhoto(rowIndex).getIndexData().setRotation(vnorm);
            }
            break; }
        case PARAM_PANORAMA: {
            final String v = (String)value;
            if ( v != getPhoto(rowIndex).getIndexData().getPanorama() ) {
                getPhoto(rowIndex).getIndexData().setPanorama(v);
            }
            break; }
        case PARAM_PANORAMA_FIRST: {
            final String v = (String)value;
            if ( v != getPhoto(rowIndex).getIndexData().getPanoramaFirst() ) {
                getPhoto(rowIndex).getIndexData().setPanoramaFirst(v);
            }
            break; }
        default:
            throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        }

        final TableModelEvent e = new TableModelEvent(this, rowIndex, rowIndex, columnIndex);
        for (final TableModelListener l : a_listOfListeners) {
            l.tableChanged(e);
        }

        setAsUnsaved();
    }

    /**
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override
    public void addTableModelListener(final TableModelListener l) {
        a_listOfListeners.add(l);
    }

    /**
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override
    public void removeTableModelListener(final TableModelListener l) {
        a_listOfListeners.remove(l);
    }

    /**
     * @param l
     */
    @Override
    public void addMetaListener(final PhotoListMetaDataListener l) {
        a_listOfMetaDataListeners.add(l);
    }

    /**
     * @param l
     */
    @Override
    public void removeMetaListener(final PhotoListMetaDataListener l) {
        a_listOfMetaDataListeners.remove(l);
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#addSaveListener(lmzr.photomngr.data.SaveListener)
     */
    @Override
    public void addSaveListener(final SaveListener l) {
        a_listOfSaveListeners.add(l);
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#removeSaveListener(lmzr.photomngr.data.SaveListener)
     */
    @Override
    public void removeSaveListener(final SaveListener l) {
        a_listOfSaveListeners.remove(l);
    }

    /**
     * @return location factory
     */
    @Override
    public HierarchicalCompoundStringFactory getLocationFactory() {
        return a_locationFactory;
    }

    /**
     * @return subject factory
     */
    @Override
    public MultiHierarchicalCompoundStringFactory getSubjectFactory() {
        return a_subjectFactory;
    }

    /**
     * @return author factory
     */
    @Override
    public AuthorFactory getAuthorFactory() {
        return a_authorFactory;
    }

    /**
     * @throws IOException
     */
    @Override
    public void save() throws IOException {

        // check that the data is not already saved
        if (a_isSaved) {
            return;
        }

        // prepare the data
        final String data[][] = new String[a_listOfPhotos.size()+1][];
        data[0] = new String[15];
        data[0][0] = "directory";
        data[0][1] = "photo";
        data[0][2] = "location";
        data[0][3] = "subject";
        data[0][4] = "quality";
        data[0][5] = "originality";
        data[0][6] = "privacy";
        data[0][7] = "panorama";
        data[0][8] = "panorama first";
        data[0][9] = "author";
        data[0][10] = "copies";
        data[0][11] = "zoom";
        data[0][12] = "focus X";
        data[0][13] = "focus Y";
        data[0][14] = "rotation";
        for (int i=0; i<a_listOfPhotos.size(); i++) {
            data[i+1] = new String[15];
            final Photo photo = getPhoto(i);
            final PhotoIndexData indexData = photo.getIndexData();
            data[i+1][0] = photo.getFolder();
            data[i+1][1] = photo.getFilename();
            data[i+1][2] = (indexData.getLocation().toLongString()==null) ? "" : indexData.getLocation().toLongString();
            data[i+1][3] = (indexData.getSubject().toString()==null) ? "" : indexData.getSubject().toString();
            data[i+1][4] = indexData.getQuality().toString();
            data[i+1][5] = indexData.getOriginality().toString();
            data[i+1][6] = indexData.getPrivacy().toString();

            data[i+1][7] = (indexData.getPanorama()==null) ? "" : indexData.getPanorama();
            data[i+1][8] = (indexData.getPanoramaFirst()==null) ? "" : indexData.getPanoramaFirst();
            data[i+1][9] = (indexData.getAuthor()==null) ? "" : indexData.getAuthor();
            data[i+1][10] = Integer.toString(indexData.getCopies());
            data[i+1][11] = Float.toString(indexData.getZoom());
            data[i+1][12] = Float.toString(indexData.getFocusX());
            data[i+1][13] = Float.toString(indexData.getFocusY());
            data[i+1][14] = Float.toString(indexData.getRotation());
        }

        a_scheduler.submitIO("save index file",
                             () -> save(data));

        // notify the SaveListerners
        setAsSaved(); //TODO this is not correct if the saving fails
    }

    /**
     * @param data
     */
    private void save(final String data[][]) {

        try {
            // keep a copy of the old file
            final Calendar now = Calendar.getInstance();
            final NumberFormat f2 = new DecimalFormat("00");
            final NumberFormat f3 = new DecimalFormat("000");
            final String backupName = Integer.toString(now.get(Calendar.YEAR)) +
                                      "_" + f2.format(now.get(Calendar.MONTH)+1)+
                                      "_" + f2.format(now.get(Calendar.DAY_OF_MONTH)) +
                                      "_" + f2.format(now.get(Calendar.HOUR_OF_DAY)) +
                                      "_" + f2.format(now.get(Calendar.MINUTE)) +
                                      "_" + f2.format(now.get(Calendar.SECOND)) +
                                      "_" + f3.format(now.get(Calendar.MILLISECOND));
            final String name = a_excelFilename.replace(".txt","_"+backupName+".txt");
            final File file = new File(a_excelFilename);
            final File file2 = new File(name);
            if (!file.renameTo(file2)) {
                System.err.println("Failed to rename "+a_excelFilename+" into "+name);
            }

            // create the new file
            StringTableFromToExcel.save(a_excelFilename,data);
        } catch (final IOException e) {
            System.err.println("failed to save index file");
            e.printStackTrace();
        }
    }

    /**
     * @param rootDir
     * @return list of folders on the disk
     */
    private static Vector<String> getFolderListOnDisk(final String rootDir) {
        final File folder = new File(rootDir);
        final Vector<String> content = new Vector<>();
        final File list[] = folder.listFiles();
        if ( list == null ) {
            System.err.println("Cannot find the root directory \""+rootDir+"\"");
        } else {
            for (final File element : list) {
                if (element.isDirectory()) {
                    // workaround the fact that MacOS returns filenames as decomposed Unicode strings
                    final String name = Normalizer.normalize(element.getName(),Normalizer.Form.NFC);
                    content.add(name);
                }
            }
        }

        return content;
    }

    /**
     * get the list of media files contained in the folder <i>folderName</i>
     * the order of the files is unspecified
     *
     * @param rootDir
     * @param folderName
     * @return content of the folder on the disk
     */
    private static Vector<String> getFolderContentOnDisk(final String rootDir,
                                                         final String folderName) {
        final File folder = new File(rootDir + File.separator + folderName);
        final Vector<String> content = new Vector<>();
        final String list[] = folder.list();

        if (list==null) {
            return content;
        }

        for ( final String str: list) {
            if ( DataFormatFactory.createFormat(folder.getAbsolutePath() + File.separator + str) != null ) {
                content.add(str);
            }
        }

        return content;
    }

    /**
     * sort the files listed in <i>content</i> (which must be in folder <i>folderName</i>) by increasing date/time
     *
     * @param rootDir
     * @param folderName
     * @param content
     */
    private static void sortFolderContentByDateTime(final String rootDir,
                                                    final String folderName,
                                                    final Vector<String> content) {

        final File folder = new File(rootDir + File.separator + folderName);
        final HashMap<String, Long> modificationDateTimes = new HashMap<>();

        for (final String s : content) {
            final File f = new File(folder + File.separator + s);
            final long modificationDateTime = f.lastModified();
            modificationDateTimes.put(s, Long.valueOf(modificationDateTime));
        }

        Collections.sort(content, (s1, s2) -> {
            final Long lastModified1 = modificationDateTimes.get(s1);
            final Long lastModified2 = modificationDateTimes.get(s2);
            return lastModified1.compareTo(lastModified2);
        });
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#performSubjectMapTranslation(java.util.Map)
     */
    @Override
    public void performSubjectMapTranslation(final Map<String, String> map) {

        for (int i=0; i<a_listOfPhotos.size(); i++) {
            final MultiHierarchicalCompoundString oldSubjects = getPhoto(i).getIndexData().getSubject();
            final HierarchicalCompoundString[] oldParts = oldSubjects.getParts();
            String newSubjectsAsString = "";
            for (final HierarchicalCompoundString oldPart : oldParts) {
                final String oldPartAsString = oldPart.toLongString();
                if (map.containsKey(oldPartAsString)) {
                    newSubjectsAsString += "\n"+ map.get(oldPartAsString);
                } else {
                    newSubjectsAsString += "\n" + oldPartAsString;
                }
            }
            setValueAt(newSubjectsAsString.substring(1), i, PARAM_SUBJECT);
        }
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#performLocationMapTranslation(java.util.Map)
     */
    @Override
    public void performLocationMapTranslation(final Map<String, String> map) {

        for (int i=0; i<a_listOfPhotos.size(); i++) {
            final String location = getPhoto(i).getIndexData().getLocation().toLongString();
            if (map.containsKey(location)) {
                setValueAt(map.get(location),i,PARAM_LOCATION);
            }
        }
    }
}
