package lmzr.photomngr.data.GPS;

import java.io.IOException;
import java.util.HashMap;

import lmzr.util.io.StringTableFromToExcel;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;

/**
 * @author Laurent
 *
 */
public class GPSDatabase {

	/**
	 * 
	 */
	static final public int PARAM_LOCATION = 0;
	/**
	 * 
	 */
	static final public int PARAM_LATITUDE = 1;
	/**
	 * 
	 */
	static final public int PARAM_LONGITUDE = 2;
	/**
	 * 
	 */
	static final public int PARAM_LATITUDE_RANGE = 3;
	/**
	 * 
	 */
	static final public int PARAM_LONGITUDE_RANGE = 4;

    final private HashMap<HierarchicalCompoundString,GPSData> a_data;

    
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
}
