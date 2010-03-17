package lmzr.photomngr.data.GPS;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.TreeTableModel;

import lmzr.photomngr.data.SaveEvent;
import lmzr.photomngr.data.SaveListener;
import lmzr.photomngr.data.SaveableModel;
import lmzr.util.io.StringTableFromToExcel;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;

/**
 * @author Laurent
 *
 */
public class GPSDatabase implements TreeTableModel, SaveableModel {

	/**
	 * 
	 */
	static final public int PARAM_LOCATION = 0;
	/**
	 * 
	 */
	static final public int PARAM_LATITUDE_MIN = 1;
	/**
	 * 
	 */
	static final public int PARAM_LATITUDE_MAX = 2;
	/**
	 * 
	 */
	static final public int PARAM_LONGITUDE_MIN = 3;
	/**
	 * 
	 */
	static final public int PARAM_LONGITUDE_MAX = 4;
	/**
	 * 
	 */
	static final public int PARAM_GPS_DATA_FOR_MAPPING = 5;
    /**
     * 
     */
    static final public int PARAM_GPS_DATA_FOR_DELETING = 6;

	final private HierarchicalCompoundStringFactory a_locationFactory;
    final private HashMap<HierarchicalCompoundString,GPSData> a_data;
    final private TreeModelSupport a_support; 
    final private String a_excelFilename;
    final private Vector<SaveListener> a_listOfSaveListeners;
    private boolean a_isSaved;

    
    /**
     * A GPS record is a couple (location, GPS coordinates)
     * 
     * @author Laurent Mazuré
     *
     */
    public class GPSRecord {

    	private HierarchicalCompoundString a_location;
    	private GPSData a_GPSData;
    	
    	/**
    	 * @param location
    	 * @param GPSData
    	 */

    	public GPSRecord (final HierarchicalCompoundString location,
    			          final GPSData GPSData) {
    		a_location = location;
    		a_GPSData = GPSData;
    	}
    	
    	/**
    	 * @return location
    	 */
    	public HierarchicalCompoundString getLocation() {
    		return a_location;
    	}
    	
    	/**
    	 * @return GPS coordinates
    	 */
    	public GPSData getGPSData() {
    		return a_GPSData;
    	}
    }
    
    
    /**
     * @param excelFilename
     * @param locationFactory
     */
    public GPSDatabase(final String excelFilename,
	                   final HierarchicalCompoundStringFactory locationFactory) {
        
    	a_support = new TreeModelSupport(this);
    	
    	a_excelFilename = excelFilename;
    	a_locationFactory = locationFactory;
    	
        String data[][] = null;
        try {
            data = StringTableFromToExcel.read(excelFilename);
        } catch (final IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        a_data = new HashMap<HierarchicalCompoundString,GPSData>();
        
        for (int i=1; i<data.length; i++) {
        	final HierarchicalCompoundString l = locationFactory.create(data[i][0]);
        	a_data.put(l,new GPSData(data[i][1],data[i][2],data[i][3],data[i][4]));
        }
        
        a_listOfSaveListeners = new Vector<SaveListener>();

        setAsSaved();
    }
    
    /**
     * @param location
     * @return record matching the input location
     */
    
    public GPSRecord getGPSData(final HierarchicalCompoundString location) {
    	HierarchicalCompoundString l = location;
    	do {
    		final GPSData d = a_data.get(l);
    		if ( d != null) return new GPSRecord(l,d);
    		l = l.getParent();
    	} while ( l != null);
    	return null;
    }

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
		case PARAM_LOCATION:
			return String.class;
		case PARAM_LATITUDE_MIN:
			return String.class;
		case PARAM_LATITUDE_MAX:
			return String.class;
		case PARAM_LONGITUDE_MIN:
			return String.class;
		case PARAM_LONGITUDE_MAX:
			return String.class;
		case PARAM_GPS_DATA_FOR_MAPPING:
        case PARAM_GPS_DATA_FOR_DELETING:
			return GPSRecord.class;
		}
		
		return null;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return PARAM_GPS_DATA_FOR_DELETING + 1;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int columnIndex) {
		switch (columnIndex) {
		case PARAM_LOCATION:
			return "location";
		case PARAM_LATITUDE_MIN:
		    return "min. latitude";
		case PARAM_LATITUDE_MAX:
		    return "max. latitude";
		case PARAM_LONGITUDE_MIN:
		    return "min. longitude";
		case PARAM_LONGITUDE_MAX:
		    return "max. longitude";
		case PARAM_GPS_DATA_FOR_MAPPING:
		    return "map";
        case PARAM_GPS_DATA_FOR_DELETING:
            return "delete";
		}
		
		return null;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getHierarchicalColumn()
	 */
	@Override
	public int getHierarchicalColumn() {
		return PARAM_LOCATION;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getValueAt(java.lang.Object, int)
	 */
	@Override
	public Object getValueAt(final Object node,
			                 final int columnIndex) {
		
		final HierarchicalCompoundString location = (HierarchicalCompoundString)node;
		final GPSData data = a_data.get(location);
		
		switch (columnIndex) {
		case PARAM_LOCATION:
			return location.toShortString();
		case PARAM_LATITUDE_MIN:
			if (data==null) return "";
			return data.getLatitudeMin();
		case PARAM_LATITUDE_MAX:
			if (data==null) return "";
			return data.getLatitudeMax();
		case PARAM_LONGITUDE_MIN:
			if (data==null) return "";
			return data.getLongitudeMin();
		case PARAM_LONGITUDE_MAX:
			if (data==null) return "";
			return data.getLongitudeMax();
		case PARAM_GPS_DATA_FOR_MAPPING:
        case PARAM_GPS_DATA_FOR_DELETING:
			return new GPSRecord(location,data);
		}
		
		return null;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#isCellEditable(java.lang.Object, int)
	 */
	@Override
	public boolean isCellEditable(@SuppressWarnings("unused") final Object node,
			                      final int columnIndex) {
		switch (columnIndex) {
		case PARAM_LOCATION:
			return false;
		case PARAM_LATITUDE_MIN:
			return true;
		case PARAM_LATITUDE_MAX:
			return true;
		case PARAM_LONGITUDE_MIN:
			return true;
		case PARAM_LONGITUDE_MAX:
			return true;
		case PARAM_GPS_DATA_FOR_MAPPING:
        case PARAM_GPS_DATA_FOR_DELETING:
			return true; // necessary because the column contains buttons and clicks are handled by the CellEditor
		}
		
		return false;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#setValueAt(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public void setValueAt(final Object value,
			               final Object node,
			               final int columnIndex) {
		
		final HierarchicalCompoundString location = (HierarchicalCompoundString)node;
		GPSData data = a_data.get(location);
		if ( data == null ) {
			data = new GPSData(null,null,null,null);
		}
		
		final String str = (String)value;
		try {
			switch (columnIndex) {
			case PARAM_LOCATION:
				break;
			case PARAM_LATITUDE_MIN:
			    if ( value == null ) {
			        if ( data.getLatitudeMin() == null ) return;
			    } else {
				    if ( str.equals(data.getLatitudeMin())) return;
			    }
				data.setLatitudeMin(str);
				break;
			case PARAM_LATITUDE_MAX:
                if ( value == null ) {
                    if ( data.getLatitudeMax() == null ) return;
                } else {
                    if ( str.equals(data.getLatitudeMax())) return;
                }
				data.setLatitudeMax(str);
				break;
			case PARAM_LONGITUDE_MIN:
                if ( value == null ) {
                    if ( data.getLongitudeMin() == null ) return;
                } else {
                    if ( str.equals(data.getLongitudeMin())) return;
                }
				data.setLongitudeMin(str);
				break;
			case PARAM_LONGITUDE_MAX:
                if ( value == null ) {
                    if ( data.getLongitudeMax() == null ) return;
                } else {
                    if ( str.equals(data.getLongitudeMax())) return;
                }
				data.setLongitudeMax(str);
				break;
			case PARAM_GPS_DATA_FOR_MAPPING:
	        case PARAM_GPS_DATA_FOR_DELETING:
				break;
			}
			if ( data.isEmpty() ) {
			    a_data.remove(location);
			} else {
	            a_data.put(location, data);			    
			}
			setAsUnsaved();
			a_support.fireChildChanged(HierarchicalCompoundStringFactory.getPath(location.getParent()),
					                   a_locationFactory.getIndexOfChild(location.getParent(),location),
					                   location);
		} catch (final IllegalArgumentException e) {
		}
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(final Object o,
			               final int index) {
		return a_locationFactory.getChild(o, index);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(final Object o) {
		return a_locationFactory.getChildCount(o);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(final Object o,
			                   final Object c) {
		return a_locationFactory.getIndexOfChild(o, c);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return a_locationFactory.getRoot();
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(final Object o) {
		return a_locationFactory.isLeaf(o);
	}

	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void addTreeModelListener(final TreeModelListener listener) {
		a_locationFactory.addTreeModelListener(listener);
		a_support.addTreeModelListener(listener);
	}

   /**
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void removeTreeModelListener(final TreeModelListener listener) {
		a_locationFactory.removeTreeModelListener(listener);
		a_support.removeTreeModelListener(listener);
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	@Override
	public void valueForPathChanged(@SuppressWarnings("unused") final TreePath arg0,
			                        @SuppressWarnings("unused") final Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
    /**
     * @throws IOException
     */
    public void save() throws IOException {
    	
    	if (a_isSaved) return;

    	// prepare the data
        final String data[][] = new String[a_data.size()+1][];
        data[0] = new String[5];
        data[0][0] = "location";
        data[0][1] = "min. latitude";
        data[0][2] = "min. longitude";
        data[0][3] = "max. latitude";
        data[0][4] = "max. longitude";

        int i = 1;
        for ( HierarchicalCompoundString location : a_data.keySet() ) {
            data[i] = new String[5];
            data[i][0] = location.toLongString();
            final GPSData gps = a_data.get(location);
            data[i][1] = gps.getLatitudeMin();
            if ( data[i][1] == null ) data[i+1][1]="";
            data[i][2] = gps.getLongitudeMin();
            if ( data[i][2] == null ) data[i+2][1]="";
            data[i][3] = gps.getLatitudeMax();
            if ( data[i][3] == null ) data[i+3][1]="";
            data[i][4] = gps.getLongitudeMax();
            if ( data[i][4] == null ) data[i+4][1]="";
            i++;
        }
        
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
     * @return flag indicating if the current values are saved
     */
    public boolean isSaved() {
        return a_isSaved;
    }
    
    /**
     * record and notify that the data saved on disk is obsolete
     */
    private void setAsUnsaved() {
        if (a_isSaved) {
            a_isSaved = false;
            final SaveEvent f = new SaveEvent(this, false);
            for (SaveListener l : a_listOfSaveListeners) l.saveChanged(f);
        }    	
    }

    /**
     * record and notify that the data saved on disk is obsolete
     */
    private void setAsSaved() {
        a_isSaved = true;
        final SaveEvent f = new SaveEvent(this, true);
        for (SaveListener l : a_listOfSaveListeners) l.saveChanged(f);
    }

	/**
	 * @param l
	 */
	public void addSaveListener(final SaveListener l) {
        a_listOfSaveListeners.add(l);
	}

	/**
	 * @param l
	 */
	public void removeSaveListener(final SaveListener l) {
        a_listOfSaveListeners.remove(l);
	}

	/**
	 * @param map
	 */
	public void performLocationMapTranslation(final Map<String, String> map) {
		
		for ( String oldLoc: map.keySet()) {
			
			final String newLoc = map.get(oldLoc);
			final HierarchicalCompoundString oldLocation = a_locationFactory.create(oldLoc);
			final HierarchicalCompoundString newLocation = a_locationFactory.create(newLoc);
			
			final GPSData data = a_data.get(oldLocation);

			if ( data != null ) {
				a_data.put(newLocation, data.clone());
			}
		}
	}


}
