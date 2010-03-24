package lmzr.photomngr.data;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.util.io.StringTableFromToExcel;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * @author Laurent
 */
public class ConcretePhotoList extends Object
                               implements PhotoList, SaveableModel {
    
    final private ReentrantReadWriteLock a_lock;
    final private Vector<Photo> a_listOfPhotos;
    final private Vector<TableModelListener> a_listOfListeners;
    final private Vector<PhotoListMetaDataListener> a_listOfMetaDataListeners;
    final private Vector<SaveListener> a_listOfSaveListeners;
    final private String a_excelFilename;
    final private HierarchicalCompoundStringFactory a_locationFactory;
    final private MultiHierarchicalCompoundStringFactory a_subjectFactory;
    final private AuthorFactory a_authorFactory;
    private boolean a_isSaved; 
    
    
    /**
     * @param excelFilename
     * @param rootDirPhoto
     */
    public ConcretePhotoList(final String excelFilename,
                             final String rootDirPhoto) {
        
        a_lock = new ReentrantReadWriteLock();
        a_listOfListeners = new Vector<TableModelListener>();
        a_listOfMetaDataListeners = new Vector<PhotoListMetaDataListener>();
        a_listOfSaveListeners = new Vector<SaveListener>();
        a_locationFactory = new HierarchicalCompoundStringFactory();
        a_subjectFactory = new MultiHierarchicalCompoundStringFactory();
        a_authorFactory = new AuthorFactory(); 
        a_excelFilename = excelFilename;
        a_isSaved = true;
        
        // load the data
        String data[][] = null;
        try {
            data = StringTableFromToExcel.read(excelFilename);
        } catch (final IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        // quick check that the data is not corrupted
        for (int i=1; i<data.length; i++) {
            if (data[i][0]=="") {
                System.err.println(excelFilename+" is corrupted: no folder name a line "+(i+1));
                System.exit(1);
            }
            if (data[i][1]=="") {
                System.err.println(excelFilename+" is corrupted: no file name a line "+(i+1));
                System.exit(1);
            }
        }
        
        // update the list of files relatively to the content of the file system
        Photo.setRootDirectory(rootDirPhoto);
        a_listOfPhotos = new Vector<Photo>();

        String previousFolderName = "";
        Vector<String> currentFolderContent = new Vector<String>();
        final Vector<String> folderListOnDisk = getFolderListOnDisk(rootDirPhoto); 
        for (int i=1; i<data.length; i++) {
            final String folderName = data[i][0];
            if ( ! previousFolderName.equals(folderName) ) {
                if ( folderListOnDisk.contains(folderName)) {
                    folderListOnDisk.remove(folderName);
                } else { 
                    System.err.println("folder \""+folderName+"\" does not exists on the disk");
                }
                for (int j=0; j<currentFolderContent.size(); j++) {
                    final String fileName = currentFolderContent.get(j);                    
                    final Photo photo = new Photo(previousFolderName,fileName,new String[]{previousFolderName,fileName},a_locationFactory,a_subjectFactory,a_authorFactory);
                    a_listOfPhotos.add(photo);
                    System.err.println(photo.getFullPath()+" is missing from the index");                    
                    setAsUnsaved();
                }
                currentFolderContent = getFolderContentOnDisk(rootDirPhoto, folderName);
                previousFolderName = folderName;
            }
            final String fileName = data[i][1];
            final Photo photo = new Photo(folderName,fileName,data[i],a_locationFactory,a_subjectFactory,a_authorFactory);
            a_listOfPhotos.add(photo);
            if ( currentFolderContent.contains(fileName) ) {
                currentFolderContent.remove(fileName);
            } else {
                System.err.println(photo.getFullPath()+" does not exist on the disk");
            }
        }
        for (int j=0; j<currentFolderContent.size(); j++) {
            final String fileName = currentFolderContent.get(j);                    
            final Photo photo = new Photo(previousFolderName,fileName,new String[]{previousFolderName,fileName},a_locationFactory,a_subjectFactory,a_authorFactory);
            a_listOfPhotos.add(photo);
            System.err.println(photo.getFullPath()+" is missing from the index");
            setAsUnsaved();
        }
        for (int i=0; i<folderListOnDisk.size(); i++) {
            final String folderName = folderListOnDisk.get(i);                    
            System.err.println("folder \""+folderName+"\" is missing from the index");                    
            currentFolderContent = getFolderContentOnDisk(rootDirPhoto, folderName);
            for (int j=0; j<currentFolderContent.size(); j++) {
                final String fileName = currentFolderContent.get(j);                    
                final Photo photo = new Photo(folderName,fileName,new String[]{previousFolderName,fileName},a_locationFactory,a_subjectFactory,a_authorFactory);
                a_listOfPhotos.add(photo);
                System.err.println(photo.getFullPath()+" is missing from the index");                    
            }
            setAsUnsaved();
        }
                
    }
    
    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        
        a_lock.readLock().lock();
        
        final int rowCount = a_listOfPhotos.size();
        
        a_lock.readLock().unlock();
        
        return rowCount;
    }
    
    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return NB_PARAM;
    }

    
    /**
     * @param rowIndex
     * @return photo
     */
    public Photo getPhoto(final int rowIndex) {

        a_lock.readLock().lock();
        
        final Photo photo = a_listOfPhotos.get(rowIndex);
        
        a_lock.readLock().unlock();
        
        return photo;
    }

    /**
     * @return flag indicating if the current values are saved
     */
    public boolean isSaved() {

        a_lock.readLock().lock();
        
        final boolean isSaved = a_isSaved;
        
        a_lock.readLock().unlock();
        
        return isSaved;
    }
    
    /**
     * record and notify that the data saved on disk is obsolete
     */
    private void setAsUnsaved() {

        a_lock.readLock().lock();
        
        if (a_isSaved) {
            a_lock.readLock().unlock();
            a_lock.writeLock().lock();
            if (a_isSaved) {
                a_isSaved = false;
                final SaveEvent f = new SaveEvent(this, false);
                for (SaveListener l : a_listOfSaveListeners) l.saveChanged(f);
            }
            a_lock.writeLock().unlock();
        } else {
            a_lock.readLock().unlock();           
        }
        
    }

    /**
     * record and notify that the data saved on disk is obsolete
     */
    private void setAsSaved() {

        a_lock.writeLock().lock();

        a_isSaved = true;
        
        final SaveEvent f = new SaveEvent(this, true);
        for (SaveListener l : a_listOfSaveListeners) l.saveChanged(f);
        
        a_lock.writeLock().unlock();        
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(final int columnIndex) {
        switch (columnIndex) {
        case PARAM_FOLDER:
            return "folder";
        case PARAM_FILENAME:
            return "filename";
        case PARAM_LOCATION:
            return "location (index)";
        case PARAM_ORIENTATION:
            return "orientation (header)";
        case PARAM_SUBJECT:
            return "subject (index)";
        case PARAM_QUALITY:
            return "quality (index)";
        case PARAM_ORIGINALITY:
            return "originality (index)";
        case PARAM_PRIVACY:
            return "privacy (index)";
        case PARAM_DATE:
            return "date (header)";
        case PARAM_PANORAMA:
            return "panorama (index)";
        case PARAM_PANORAMA_FIRST:
            return "panorama first (index)";
        case PARAM_AUTHOR:
            return "author (index)";
        case PARAM_COPIES:
            return "copies (index)";
        case PARAM_ZOOM:
            return "zoom (index)";
        case PARAM_FOCUS_X:
            return "focus X (index)";
        case PARAM_FOCUS_Y:
            return "focus Y (index)";
        case PARAM_ROTATION:
            return "rotation (index)";
        case PARAM_MANUFACTURER:
            return "manufacturer (header)";
        case PARAM_MODEL:
            return "model (header)";
        case PARAM_EXPOSURE_TIME:
            return "exposure time (header)";
        case PARAM_SHUTTER_SPEED:
            return "shutter speed (header)";
        case PARAM_APERTURE_VALUE:
            return "aperture value (header)";
        case PARAM_FLASH:
            return "flash (header)";
        case PARAM_FOCAL_LENGTH:
            return "focal lenght (header)";
        case PARAM_CANON_SELF_TIMER_DELAY:
            return "Canon self timer delay (header)";
        case PARAM_CANON_FLASH_MODE:
            return "Canon flash mode (header)";
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE:
            return "Canon continuous drive mode (header)";
        case PARAM_CANON_FOCUS_MODE:
            return "Canon focus mode (header)";
        case PARAM_CANON_ISO:
            return "Canon ISO (header)";
        case PARAM_CANON_SUBJECT_DISTANCE:
            return "Canon subject distance (header)";
        case PARAM_HEIGHT:
            return "Height (header)";
        case PARAM_WITDH:
            return "Width (header)";
        case PARAM_FORMAT:
            return "Format";
        }

        return null;
    }
    
    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
        case PARAM_FOLDER:
        case PARAM_FILENAME:
        case PARAM_PANORAMA:
        case PARAM_PANORAMA_FIRST:
        case PARAM_AUTHOR:
        case PARAM_MANUFACTURER:
        case PARAM_MODEL:
        case PARAM_EXPOSURE_TIME:
        case PARAM_SHUTTER_SPEED:
        case PARAM_APERTURE_VALUE:
        case PARAM_FLASH:
        case PARAM_FOCAL_LENGTH:
        case PARAM_CANON_SELF_TIMER_DELAY:
        case PARAM_CANON_FLASH_MODE:
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE:
        case PARAM_CANON_FOCUS_MODE:
        case PARAM_CANON_ISO:
        case PARAM_CANON_SUBJECT_DISTANCE:
            return String.class;
        case PARAM_ZOOM:
        case PARAM_FOCUS_X:
        case PARAM_FOCUS_Y:
        case PARAM_ROTATION:
            return Float.class;
        case PARAM_LOCATION:
            return HierarchicalCompoundString.class;
        case PARAM_SUBJECT:
        	return MultiHierarchicalCompoundString.class;
        case PARAM_ORIENTATION:
        case PARAM_HEIGHT:
        case PARAM_WITDH:
            return Integer.class;
        case PARAM_QUALITY:
            return PhotoQuality.class;
        case PARAM_ORIGINALITY:
            return PhotoOriginality.class;
        case PARAM_PRIVACY:
            return PhotoPrivacy.class;
        case PARAM_DATE:
            return Date.class;
        case PARAM_COPIES:
            return Integer.class;
        case PARAM_FORMAT:
            return DataFormat.class;
        }
        return null;
    }
    
    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(@SuppressWarnings("unused") final int rowIndex,
    		                      final int columnIndex) {
        switch (columnIndex) {
        case PARAM_PRIVACY:
        case PARAM_QUALITY:
        case PARAM_ORIGINALITY:
        case PARAM_COPIES:
        case PARAM_SUBJECT:
        case PARAM_ZOOM:
        case PARAM_FOCUS_X:
        case PARAM_FOCUS_Y:
        case PARAM_ROTATION:
        case PARAM_AUTHOR:
        case PARAM_LOCATION:
            return true;
        case PARAM_FOLDER:
        case PARAM_FILENAME:
        case PARAM_ORIENTATION:
        case PARAM_PANORAMA:
        case PARAM_PANORAMA_FIRST:
        case PARAM_DATE:
        case PARAM_MANUFACTURER:
        case PARAM_MODEL:
        case PARAM_EXPOSURE_TIME:
        case PARAM_SHUTTER_SPEED:
        case PARAM_APERTURE_VALUE:
        case PARAM_FLASH:
        case PARAM_FOCAL_LENGTH:
        case PARAM_CANON_SELF_TIMER_DELAY:
        case PARAM_CANON_FLASH_MODE:
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE:
        case PARAM_CANON_FOCUS_MODE:
        case PARAM_CANON_ISO:
        case PARAM_CANON_SUBJECT_DISTANCE:
        case PARAM_HEIGHT:
        case PARAM_WITDH:
        case PARAM_FORMAT:
            return false;
        }
        return false;
    }
    
    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex,
                             final int columnIndex) {
        Object value = null;
        
        a_lock.readLock().lock();
        
        switch (columnIndex) {
        case PARAM_FOLDER:
            value = getPhoto(rowIndex).getFolder();
            break;
        case PARAM_FILENAME:
            value = getPhoto(rowIndex).getFilename();
            break;
        case PARAM_LOCATION:
            value = getPhoto(rowIndex).getIndexData().getLocation();
            break;
        case PARAM_ORIENTATION:
            value = new Integer(getPhoto(rowIndex).getHeaderData().getOrientation());
            break;
        case PARAM_SUBJECT:
            value = getPhoto(rowIndex).getIndexData().getSubject();
            break;
        case PARAM_QUALITY:
            value = getPhoto(rowIndex).getIndexData().getQuality();
            break;
        case PARAM_ORIGINALITY:
            value = getPhoto(rowIndex).getIndexData().getOriginality();
            break;
        case PARAM_PRIVACY:
            value = getPhoto(rowIndex).getIndexData().getPrivacy();
            break;
        case PARAM_DATE:
            value = getPhoto(rowIndex).getHeaderData().getDate();
            break;
        case PARAM_PANORAMA:
            value = getPhoto(rowIndex).getIndexData().getPanorama();
            break;
        case PARAM_PANORAMA_FIRST:
            value = getPhoto(rowIndex).getIndexData().getPanoramaFirst();
            break;
        case PARAM_AUTHOR:
            value = getPhoto(rowIndex).getIndexData().getAuthor();
            break;
        case PARAM_COPIES:
            value = new Integer(getPhoto(rowIndex).getIndexData().getCopies());
            break;
        case PARAM_ZOOM:
            value = new Float(getPhoto(rowIndex).getIndexData().getZoom());
            break;
        case PARAM_FOCUS_X:
            value = new Float(getPhoto(rowIndex).getIndexData().getFocusX());
            break;
        case PARAM_FOCUS_Y:
            value = new Float(getPhoto(rowIndex).getIndexData().getFocusY());
            break;
        case PARAM_ROTATION:
            value = new Float(getPhoto(rowIndex).getIndexData().getRotation());
            break;
        case PARAM_MANUFACTURER:
            value = getPhoto(rowIndex).getHeaderData().getManufacturer();
            break;
        case PARAM_MODEL:
            value = getPhoto(rowIndex).getHeaderData().getModel();
            break;
        case PARAM_EXPOSURE_TIME:
            value = getPhoto(rowIndex).getHeaderData().getExposureTime();
            break;
        case PARAM_SHUTTER_SPEED:
            value = getPhoto(rowIndex).getHeaderData().getShutterSpeed();
            break;
        case PARAM_APERTURE_VALUE:
            value = getPhoto(rowIndex).getHeaderData().getApertureValue();
            break;
        case PARAM_FLASH:
            value = getPhoto(rowIndex).getHeaderData().getFlash();
            break;
        case PARAM_FOCAL_LENGTH:
            value = getPhoto(rowIndex).getHeaderData().getFocalLength();
            break;
        case PARAM_CANON_SELF_TIMER_DELAY:
            value = getPhoto(rowIndex).getHeaderData().getCanonSelfTimerDelay();
            break;
        case PARAM_CANON_FLASH_MODE:
            value = getPhoto(rowIndex).getHeaderData().getCanonFlashMode();
            break;
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE:
            value = getPhoto(rowIndex).getHeaderData().getCanonContinuousDriveMode();
            break;
        case PARAM_CANON_FOCUS_MODE:
            value = getPhoto(rowIndex).getHeaderData().getCanonFocusMode();
            break;
        case PARAM_CANON_ISO:
            value = getPhoto(rowIndex).getHeaderData().getCanonISO();
            break;
        case PARAM_CANON_SUBJECT_DISTANCE:
            value = getPhoto(rowIndex).getHeaderData().getCanonSubjectDistance();
            break;
        case PARAM_HEIGHT:
            value = new Integer(getPhoto(rowIndex).getHeaderData().getHeight());
            break;
        case PARAM_WITDH:
            value = new Integer(getPhoto(rowIndex).getHeaderData().getWidth());
            break;
        case PARAM_FORMAT:
            value = getPhoto(rowIndex).getFormat();
            break;
        }
        
        a_lock.readLock().unlock();

        return value;
    }
    
    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(final Object value,
                           final int rowIndex,
                           final int columnIndex) {
    	
    	final NumberFormat format = NumberFormat.getInstance();
    	
        a_lock.writeLock().lock();

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
					copies= format.parse((String)value).intValue();
				} catch (ParseException e1) {
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
					zoom= format.parse((String)value).floatValue();
				} catch (ParseException e1) {
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
					focusX= format.parse((String)value).floatValue();
				} catch (ParseException e1) {
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
					focusY= format.parse((String)value).floatValue();
				} catch (ParseException e1) {
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
        		rotation= Float.parseFloat((String)value);
        	}
            float vnorm = rotation.floatValue();
            vnorm = vnorm % 360;
            if (vnorm>180) vnorm -= 360;
            if (vnorm<=-180) vnorm += 360;
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
            return;
        }
        
        final TableModelEvent e = new TableModelEvent(this, rowIndex, rowIndex, columnIndex);
        for (TableModelListener l : a_listOfListeners) l.tableChanged(e);
        
        setAsUnsaved();
        
        a_lock.writeLock().lock();
    }
    
    /**
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(final TableModelListener l) {
        a_listOfListeners.add(l);
    }
    
    /**
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(final TableModelListener l) {
        a_listOfListeners.remove(l);
    }
    
    /**
     * @param l
     */
    public void addMetaListener(final PhotoListMetaDataListener l) {
        a_listOfMetaDataListeners.add(l);
    }
    
    /**
     * @param l
     */
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
    public HierarchicalCompoundStringFactory getLocationFactory() {
        return a_locationFactory;
    }

    /**
     * @return subject factory
     */
    public MultiHierarchicalCompoundStringFactory getSubjectFactory() {
        return a_subjectFactory;
    }

    /**
     * @return author factory
     */
    public AuthorFactory getAuthorFactory() {
        return a_authorFactory;
    }
    
    /**
     * @throws IOException
     */
    public void save() throws IOException {
    	
        // get the lock
        a_lock.readLock().lock();

        // check that the data is not already saved
    	if (a_isSaved) {
            a_lock.readLock().unlock();
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
        
        // free the lock
        a_lock.readLock().unlock();

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
        if (!file.renameTo(file2)) throw new IOException("Failed to rename "+a_excelFilename+" into "+name);
        
        // create the new file
        StringTableFromToExcel.save(a_excelFilename,data);
        
        // notify the SaveListerners
        setAsSaved();
        
    }
    
    /**
     * @param rootDir
     * @return list of folders on the disk
     */
    private static Vector<String> getFolderListOnDisk(final String rootDir) {
        final File folder = new File(rootDir);
        final Vector<String> content = new Vector<String>();
        final File list[] = folder.listFiles();
        for (int i=0; i<list.length; i++) {
            if (list[i].isDirectory()) {
                // workaround the fact that MacOS returns filenames as decomposed Unicode strings
                final String name = Normalizer.normalize(list[i].getName(),Normalizer.Form.NFC);
                content.add(name);
            }
        }
        
        return content;
    }
    
    /**
     * @param rootDir
     * @param folderName
     * @return content of the folder on the disk
     */
    private static Vector<String> getFolderContentOnDisk(final String rootDir,
                                                         final String folderName) {
    	final DataFormatFactory s_formatFactory = new DataFormatFactory();
        final File folder = new File(rootDir + File.separator + folderName);
        final Vector<String> content = new Vector<String>();
        final String list[] = folder.list();
        
        if (list==null) return content;

        for ( String str: list)
        	if ( s_formatFactory.createFormat(folder.getAbsolutePath()+ File.separator + str) != null )
                content.add(str);
        
        return content;
    }

	/**
	 * @see lmzr.photomngr.data.PhotoList#performSubjectMapTranslation(java.util.Map)
	 */
	@Override
	public void performSubjectMapTranslation(final Map<String, String> map) {

	    a_lock.writeLock().lock();

        for (int i=0; i<a_listOfPhotos.size(); i++) {
        	final MultiHierarchicalCompoundString oldSubjects = getPhoto(i).getIndexData().getSubject();
        	final HierarchicalCompoundString[] oldParts = oldSubjects.getParts();
        	String newSubjectsAsString = "";
        	for (int j=0; j<oldParts.length; j++) {
        		final String oldPartAsString = oldParts[j].toLongString();
        		if (map.containsKey(oldPartAsString)) {
        			newSubjectsAsString += "\n"+ map.get(oldPartAsString);
        		} else {
        			newSubjectsAsString += "\n" + oldPartAsString;
        		}
        	}
    		setValueAt(newSubjectsAsString.substring(1), i, PARAM_SUBJECT);
        }

        a_lock.writeLock().unlock();
	}
	
	/**
	 * @see lmzr.photomngr.data.PhotoList#performLocationMapTranslation(java.util.Map)
	 */
	@Override
	public void performLocationMapTranslation(final Map<String, String> map) {

       a_lock.writeLock().lock();

        for (int i=0; i<a_listOfPhotos.size(); i++) {
        	final String location = getPhoto(i).getIndexData().getLocation().toLongString();
        	if (map.containsKey(location)) {
        		setValueAt(map.get(location),i,PARAM_LOCATION);
        	}
        }
        
        a_lock.writeLock().unlock();        
	}

}
