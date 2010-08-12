package lmzr.photomngr.ui;

import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoHeaderData;
import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent
 *
 */
public class PhotoParametersTableModel implements TableModel, ListSelectionListener {

    /**
     * 
     */
    static final public int PARAM_WITDH = 0;
    /**
     * 
     */
    static final public int PARAM_HEIGHT = 1;
    /**
     * 
     */
    static final public int PARAM_MANUFACTURER = 2;
    /**
     * 
     */
    static final public int PARAM_MODEL = 3;
    /**
     * 
     */
    static final public int PARAM_EXPOSURE_TIME = 4;
    /**
     * 
     */
    static final public int PARAM_SHUTTER_SPEED = 5;
    /**
     * 
     */
    static final public int PARAM_APERTURE_VALUE = 6;
    /**
     * 
     */
    static final public int PARAM_FLASH = 7;
    /**
     * 
     */
    static final public int PARAM_FOCAL_LENGTH = 8;
    /**
     * 
     */
    static final public int PARAM_SELF_TIMER_MODE = 9;
    /**
     * 
     */
    static final public int PARAM_CANON_SELF_TIMER_DELAY = 10;
    /**
     * 
     */
    static final public int PARAM_CANON_FLASH_MODE = 11;
    /**
     * 
     */
    static final public int PARAM_CANON_CONTINUOUS_DRIVE_MODE = 12;
    /**
     * 
     */
    static final public int PARAM_CANON_FOCUS_MODE = 13;
    /**
     * 
     */
    static final public int PARAM_CANON_ISO = 14;
    /**
     * 
     */
    static final public int PARAM_CANON_SUBJECT_DISTANCE = 15;
    /**
     * 
     */
    static final public int NB_PARAM = 16;

    
    final private PhotoList a_photoList;
    final private ListSelectionManager a_selection;
	final private Vector<TableModelListener> a_listOfListeners;

	
	/**
	 * @param photoList
	 * @param selection
	 */
	public PhotoParametersTableModel(final PhotoList photoList,
                                     final ListSelectionManager selection) {
		a_photoList = photoList;
        a_selection = selection;
        a_selection.addListener(this);
		a_listOfListeners = new Vector<TableModelListener>();
	}

	/**
	 * 
	 */
	public void dispose() {
        a_selection.removeListener(this);		
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
        a_listOfListeners.add(l);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return a_selection.getSelection().length+1;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int columnIndex) {
		
		if ( columnIndex == 0 ) return "";
		
        final int selection[] = a_selection.getSelection();
        final Photo photo = a_photoList.getPhoto(selection[columnIndex-1]);
        final String folder = photo.getFolder();
        final String filename = photo.getFilename();

		return filename + " (" + folder + ")";
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return NB_PARAM;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex,
			                 final int columnIndex) {
		
		if ( columnIndex == 0) {
			return getRowHeader(rowIndex);
		}
		
		return getCellValueCell(rowIndex,columnIndex);
	}

	/**
	 * return the value of a cell which is not in the header row
	 * 
	 * @param rowIndex
	 * @param columnIndex
	 * @return value of the cell
	 */
	private String getCellValueCell(final int rowIndex,
                                    final int columnIndex) {
		
        final int selection[] = a_selection.getSelection();
        final PhotoHeaderData headerData = a_photoList.getPhoto(selection[columnIndex-1]).getHeaderData();

        switch (rowIndex) {
        case PARAM_WITDH:
        	return Integer.toString(headerData.getWidth());
        case PARAM_HEIGHT:
        	return Integer.toString(headerData.getHeight());
        case PARAM_MANUFACTURER:
        	return headerData.getManufacturer();
        case PARAM_MODEL:
        	return headerData.getModel();
        case PARAM_EXPOSURE_TIME:
        	return headerData.getExposureTime();
        case PARAM_SHUTTER_SPEED:
        	return headerData.getShutterSpeed();
        case PARAM_APERTURE_VALUE:
        	return headerData.getApertureValue();
        case PARAM_FLASH:
        	return headerData.getFlash();
        case PARAM_FOCAL_LENGTH:
        	return Double.toString(headerData.getFocalLength());
        case PARAM_SELF_TIMER_MODE:
        	return headerData.getSelfTimerMode();
        case PARAM_CANON_SELF_TIMER_DELAY:
        	return headerData.getCanonSelfTimerDelay();
        case PARAM_CANON_FLASH_MODE:
        	return headerData.getCanonFlashMode();
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE:
        	return headerData.getCanonContinuousDriveMode();
        case PARAM_CANON_FOCUS_MODE:
        	return headerData.getCanonFocusMode();
        case PARAM_CANON_ISO:
        	return headerData.getCanonISO();
        case PARAM_CANON_SUBJECT_DISTANCE:
        	return Integer.toString(headerData.getCanonSubjectDistance());
        }

        return null;
	}

	/**
	 * return the value of a cell which is in the header row
	 * 
	 * @param rowIndex
	 * @return value of the cell
	 */
	private String getRowHeader(final int rowIndex) {
		
        switch (rowIndex) {
        case PARAM_WITDH:
        	return "Width";
        case PARAM_HEIGHT:
        	return "Height";
        case PARAM_MANUFACTURER:
        	return "Manufacturer";
        case PARAM_MODEL:
        	return "Model";
        case PARAM_EXPOSURE_TIME:
        	return "Exposure Time";
        case PARAM_SHUTTER_SPEED:
        	return "Shutter Speed";
        case PARAM_APERTURE_VALUE:
        	return "Aperture Value";
        case PARAM_FLASH:
        	return "Flash";
        case PARAM_FOCAL_LENGTH:
        	return "Focal Length";
        case PARAM_SELF_TIMER_MODE:
        	return "Self Timer Mode";
        case PARAM_CANON_SELF_TIMER_DELAY:
        	return "Canon Self Timer Delay";
        case PARAM_CANON_FLASH_MODE:
        	return "Canon Flash Mode";
        case PARAM_CANON_CONTINUOUS_DRIVE_MODE:
        	return "Canon Continuous Drive Mode";
        case PARAM_CANON_FOCUS_MODE:
        	return "Canon Focus Mode";
        case PARAM_CANON_ISO:
        	return "Canon ISO";
        case PARAM_CANON_SUBJECT_DISTANCE:
        	return "Canon Subject Distance";
        }
        
        return null;
	}
	
	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(final int rowIndex,
			                      final int columnIndex) {
		return false;
	}


	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(final Object value,
			               final int rowIndex,
			               final int columnIndex) {
		// do nothing, this table is not editable
	}

	/**
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(final ListSelectionEvent e) {
        final TableModelEvent event = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
        for (TableModelListener l : a_listOfListeners) l.tableChanged(event);		
	}

}
