package lmzr.photomngr.data.GPS;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.TreeTableModel;

import lmzr.util.io.StringTableFromToExcel;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;

/**
 * @author Laurent
 *
 */
public class GPSDatabase implements TreeTableModel {

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

	final private HierarchicalCompoundStringFactory a_locationFactory;
    final private HashMap<HierarchicalCompoundString,GPSData> a_data;
    final private TreeModelSupport a_support; 

    
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
		}
		
		return null;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return PARAM_LONGITUDE_MAX + 1;
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
		}
		
		return null;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#isCellEditable(java.lang.Object, int)
	 */
	@Override
	public boolean isCellEditable(final Object node,
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
				data.setLatitudeMin(str);
				break;
			case PARAM_LATITUDE_MAX:
				data.setLatitudeMax(str);
				break;
			case PARAM_LONGITUDE_MIN:
				data.setLongitudeMin(str);
				break;
			case PARAM_LONGITUDE_MAX:
				data.setLongitudeMax(str);
				break;
			}
			a_data.put(location, data);
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
	public void valueForPathChanged(final TreePath arg0,
			                        final Object arg1) {
		// TODO Auto-generated method stub
		
	}
}
