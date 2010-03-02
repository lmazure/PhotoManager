package lmzr.photomngr.data;

import java.io.IOException;
import java.util.Map;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * @author Laurent
 */
public interface PhotoList extends TableModel, SaveableModel {

    /**
     * 
     */
	static final public int PARAM_FOLDER = 0;
    /**
     * 
     */
    static final public int PARAM_FILENAME = 1;
    /**
     * 
     */
    static final public int PARAM_LOCATION = 2;
    /**
     * 
     */
    static final public int PARAM_ORIENTATION = 3;
    /**
     * 
     */
    static final public int PARAM_SUBJECT = 4;
    /**
     * 
     */
    static final public int PARAM_QUALITY = 5;
    /**
     * 
     */
    /**
     * 
     */
    static final public int PARAM_ORIGINALITY = 6;
    /**
     * 
     */
    static final public int PARAM_PRIVACY = 7;
    /**
     * 
     */
    static final public int PARAM_DATE = 8;
    /**
     * 
     */
    static final public int PARAM_PANORAMA = 9;
    /**
     * 
     */
    static final public int PARAM_PANORAMA_FIRST = 10;
    /**
     * 
     */
    static final public int PARAM_AUTHOR = 11;
    /**
     * 
     */
    static final public int PARAM_COPIES = 12;
    /**
     * 
     */
    static final public int PARAM_ZOOM = 13;
    /**
     * 
     */
    static final public int PARAM_FOCUS_X = 14;
    /**
     * 
     */
    static final public int PARAM_FOCUS_Y = 15;
    /**
     * 
     */
    static final public int PARAM_ROTATION = 16;
    /**
     * 
     */
    static final public int PARAM_MANUFACTURER = 17;
    /**
     * 
     */
    static final public int PARAM_MODEL = 18;
    /**
     * 
     */
    static final public int PARAM_EXPOSURE_TIME = 19;
    /**
     * 
     */
    static final public int PARAM_SHUTTER_SPEED = 20;
    /**
     * 
     */
    static final public int PARAM_APERTURE_VALUE = 21;
    /**
     * 
     */
    static final public int PARAM_FLASH = 22;
    /**
     * 
     */
    static final public int PARAM_FOCAL_LENGTH = 23;
    /**
     * 
     */
    static final public int PARAM_CANON_SELF_TIMER_DELAY = 24;
    /**
     * 
     */
    static final public int PARAM_CANON_FLASH_MODE = 25;
    /**
     * 
     */
    static final public int PARAM_CANON_CONTINUOUS_DRIVE_MODE = 26;
    /**
     * 
     */
    static final public int PARAM_CANON_FOCUS_MODE = 27;
    /**
     * 
     */
    static final public int PARAM_CANON_ISO = 28;
    /**
     * 
     */
    static final public int PARAM_CANON_SUBJECT_DISTANCE = 29;
    /**
     * 
     */
    static final public int PARAM_WITDH = 30;
    /**
     * 
     */
    static final public int PARAM_HEIGHT = 31;
    /**
     * 
     */
    static final public int PARAM_FORMAT = 32;
    /**
     * 
     */
    static final public int NB_PARAM = 33;

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public abstract int getRowCount();

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public abstract int getColumnCount();

    /**
     * @param rowIndex
     * @return photo
     */
    public abstract Photo getPhoto(final int rowIndex);

    /**
     * @return flag indicating if the current values are saved
     */
    public abstract boolean isSaved();

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public abstract String getColumnName(final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public abstract Class<?> getColumnClass(final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public abstract boolean isCellEditable(final int rowIndex, final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public abstract Object getValueAt(final int rowIndex, final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public abstract void setValueAt(final Object value,
                                    final int rowIndex,
                                    final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public abstract void addTableModelListener(final TableModelListener l);

    /**
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public abstract void removeTableModelListener(final TableModelListener l);

    /**
     * @param l
     */
    public abstract void addMetaListener(final PhotoListMetaDataListener l);

    /**
     * @param l
     */
    public abstract void removeMetaListener(final PhotoListMetaDataListener l);

    /**
     * @param l
     */
    public abstract void addSaveListener(final SaveListener l);

    /**
     * @param l
     */
    public abstract void removeSaveListener(final SaveListener l);

    /**
     * @throws IOException
     */
    public abstract void save() throws IOException;
    
    /**
     * @return location factory
     */
    public HierarchicalCompoundStringFactory getLocationFactory();

    /**
     * @return subject factory
     */
    public MultiHierarchicalCompoundStringFactory getSubjectFactory();
    
    /**
     * @return author factory
     */
    public AuthorFactory getAuthorFactory();

	/**
	 * @param map
	 */
	public abstract void performSubjectMapTranslation(Map<String, String> map);

	/**
	 * @param map
	 */
	void performLocationMapTranslation(Map<String, String> map);

}