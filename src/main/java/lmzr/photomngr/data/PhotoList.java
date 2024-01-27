package lmzr.photomngr.data;

import java.io.IOException;
import java.util.Map;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * @author Laurent Mazuré
 */
public interface PhotoList extends TableModel, SaveableModel {

    /**
     *
     */
    int PARAM_FOLDER = 0;
    /**
     *
     */
    int PARAM_FILENAME = 1;
    /**
     *
     */
    int PARAM_LOCATION = 2;
    /**
     *
     */
    int PARAM_ORIENTATION = 3;
    /**
     *
     */
    int PARAM_SUBJECT = 4;
    /**
     *
     */
    int PARAM_QUALITY = 5;
    /**
     *
     */
    /**
     *
     */
    int PARAM_ORIGINALITY = 6;
    /**
     *
     */
    int PARAM_PRIVACY = 7;
    /**
     *
     */
    int PARAM_DATE = 8;
    /**
     *
     */
    int PARAM_PANORAMA = 9;
    /**
     *
     */
    int PARAM_PANORAMA_FIRST = 10;
    /**
     *
     */
    int PARAM_AUTHOR = 11;
    /**
     *
     */
    int PARAM_COPIES = 12;
    /**
     *
     */
    int PARAM_ZOOM = 13;
    /**
     *
     */
    int PARAM_FOCUS_X = 14;
    /**
     *
     */
    int PARAM_FOCUS_Y = 15;
    /**
     *
     */
    int PARAM_ROTATION = 16;
    /**
     *
     */
    int PARAM_MANUFACTURER = 17;
    /**
     *
     */
    int PARAM_MODEL = 18;
    /**
     *
     */
    int PARAM_EXPOSURE_TIME = 19;
    /**
     *
     */
    int PARAM_SHUTTER_SPEED = 20;
    /**
     *
     */
    int PARAM_APERTURE_VALUE = 21;
    /**
     *
     */
    int PARAM_FLASH = 22;
    /**
     *
     */
    int PARAM_FOCAL_LENGTH = 23;
    /**
     *
     */
    int PARAM_SELF_TIMER_MODE = 24;
    /**
     *
     */
    int PARAM_CANON_SELF_TIMER_DELAY = 25;
    /**
     *
     */
    int PARAM_CANON_FLASH_MODE = 26;
    /**
     *
     */
    int PARAM_CANON_CONTINUOUS_DRIVE_MODE = 27;
    /**
     *
     */
    int PARAM_CANON_FOCUS_MODE = 28;
    /**
     *
     */
    int PARAM_CANON_ISO = 29;
    /**
     *
     */
    int PARAM_CANON_SUBJECT_DISTANCE = 30;
    /**
     *
     */
    int PARAM_WITDH = 31;
    /**
     *
     */
    int PARAM_HEIGHT = 32;
    /**
     *
     */
    int PARAM_FORMAT = 33;
    /**
     *
     */
    int NB_PARAM = 34;

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override int getRowCount();

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override int getColumnCount();

    /**
     * @param rowIndex
     * @return photo
     */
    Photo getPhoto(final int rowIndex);

    /**
     * @return flag indicating if the current values are saved
     */
    boolean isSaved();

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override String getColumnName(final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override Class<?> getColumnClass(final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override boolean isCellEditable(final int rowIndex, final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override Object getValueAt(final int rowIndex, final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override void setValueAt(final Object value,
                                    final int rowIndex,
                                    final int columnIndex);

    /**
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override void addTableModelListener(final TableModelListener l);

    /**
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override void removeTableModelListener(final TableModelListener l);

    /**
     * @param l
     */
    void addMetaListener(final PhotoListMetaDataListener l);

    /**
     * @param l
     */
    void removeMetaListener(final PhotoListMetaDataListener l);

    /**
     * @param l
     */
    void addSaveListener(final SaveListener l);

    /**
     * @param l
     */
    void removeSaveListener(final SaveListener l);

    /**
     * @throws IOException
     */
    @Override void save() throws IOException;

    /**
     * @return location factory
     */
    HierarchicalCompoundStringFactory getLocationFactory();

    /**
     * @return subject factory
     */
    MultiHierarchicalCompoundStringFactory getSubjectFactory();

    /**
     * @return author factory
     */
    AuthorFactory getAuthorFactory();

    /**
     * @param map
     */
    void performSubjectMapTranslation(final Map<String, String> map);

    /**
     * @param map
     */
    void performLocationMapTranslation(final Map<String, String> map);

}