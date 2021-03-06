package lmzr.photomngr.data.GPS;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
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
 * @author Laurent Mazuré
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
            this.a_location = location;
            this.a_GPSData = GPSData;
        }

        /**
         * @return location
         */
        public HierarchicalCompoundString getLocation() {
            return this.a_location;
        }

        /**
         * @return GPS coordinates
         */
        public GPSData getGPSData() {
            return this.a_GPSData;
        }
    }

    /**
     * @param excelFilename
     * @param locationFactory
     */
    public GPSDatabase(final String excelFilename,
                       final HierarchicalCompoundStringFactory locationFactory) {

        this.a_support = new TreeModelSupport(this);

        this.a_excelFilename = excelFilename;
        this.a_locationFactory = locationFactory;
        this.a_data = new HashMap<>();
        this.a_listOfSaveListeners = new Vector<>();
        setAsSaved();

        String data[][] = null;
        try {
            data = StringTableFromToExcel.read(excelFilename);
        } catch (final IOException e) {
            e.printStackTrace();
            return;
        }

        for (int i=1; i<data.length; i++) {
            final HierarchicalCompoundString l = locationFactory.create(data[i][0]);
            final GPSData d = new GPSData( (data[i][1].length()==0) ? null : data[i][1],
                                           (data[i][2].length()==0) ? null : data[i][2],
                                           (data[i][3].length()==0) ? null : data[i][3],
                                           (data[i][4].length()==0) ? null : data[i][4] );
            this.a_data.put(l,d);
        }
    }

    /**
     * @param location
     * @return record matching the input location
     */

    public GPSRecord getGPSData(final HierarchicalCompoundString location) {
        HierarchicalCompoundString l = location;
        do {
            final GPSData d = this.a_data.get(l);
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
        case PARAM_LATITUDE_MIN:
        case PARAM_LATITUDE_MAX:
        case PARAM_LONGITUDE_MIN:
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
            return "Geoportail";
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
        final GPSData data = this.a_data.get(location);

        switch (columnIndex) {
        case PARAM_LOCATION:
            return location.toShortString();
        case PARAM_LATITUDE_MIN:
            if (data==null) return "";
            final String latMin =  data.getLatitudeMin();
            return ( latMin == null ) ? "" : latMin;
        case PARAM_LATITUDE_MAX:
            if (data==null) return "";
            final String latMax = data.getLatitudeMax();
            return ( latMax == null ) ? "" : latMax;
        case PARAM_LONGITUDE_MIN:
            if (data==null) return "";
            final String longMin =  data.getLongitudeMin();
            return ( longMin == null ) ? "" : longMin;
        case PARAM_LONGITUDE_MAX:
            if (data==null) return "";
            final String longMax = data.getLongitudeMax();
            return ( longMax == null ) ? "" : longMax;
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
    public boolean isCellEditable(final Object node,
                                  final int columnIndex) {
        switch (columnIndex) {
        case PARAM_LOCATION:
            return false;
        case PARAM_LATITUDE_MIN:
        case PARAM_LATITUDE_MAX:
        case PARAM_LONGITUDE_MIN:
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
        GPSData data = this.a_data.get(location);
        if ( data == null ) {
            data = new GPSData(null,null,null,null);
        }

        String str = (String)value;
        try {
            switch (columnIndex) {
            case PARAM_LOCATION:
                break;
            case PARAM_LATITUDE_MIN:
                if ( str!=null && str.length()==0 ) str = null;
                if ( str == null ) {
                    if ( data.getLatitudeMin() == null ) return;
                } else {
                    if ( str.equals(data.getLatitudeMin())) return;
                }
                data.setLatitudeMin(str);
                break;
            case PARAM_LATITUDE_MAX:
                if ( str!=null && str.length()==0 ) str = null;
                if ( str == null ) {
                    if ( data.getLatitudeMax() == null ) return;
                } else {
                    if ( str.equals(data.getLatitudeMax())) return;
                }
                data.setLatitudeMax(str);
                break;
            case PARAM_LONGITUDE_MIN:
                if ( str!=null && str.length()==0 ) str = null;
                if ( str == null ) {
                    if ( data.getLongitudeMin() == null ) return;
                } else {
                    if ( str.equals(data.getLongitudeMin())) return;
                }
                data.setLongitudeMin(str);
                break;
            case PARAM_LONGITUDE_MAX:
                if ( str!=null && str.length()==0 ) str = null;
                if ( str == null ) {
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
                this.a_data.remove(location);
            } else {
                this.a_data.put(location, data);
            }
            setAsUnsaved();
            this.a_support.fireChildChanged(HierarchicalCompoundStringFactory.getPath(location.getParent()),
                                       this.a_locationFactory.getIndexOfChild(location.getParent(),location),
                                       location);
        } catch (final IllegalArgumentException e) {
              JOptionPane.showMessageDialog(null,
                                            "Incorrect coordinate\n"+e.toString(),
                                            "Edit error",
                                            JOptionPane.ERROR_MESSAGE);

        }
    }

    /**
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    @Override
    public Object getChild(final Object o,
                           final int index) {
        return this.a_locationFactory.getChild(o, index);
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    @Override
    public int getChildCount(final Object o) {
        return this.a_locationFactory.getChildCount(o);
    }

    /**
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    @Override
    public int getIndexOfChild(final Object o,
                               final Object c) {
        return this.a_locationFactory.getIndexOfChild(o, c);
    }

    /**
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    @Override
    public Object getRoot() {
        return this.a_locationFactory.getRoot();
    }

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    @Override
    public boolean isLeaf(final Object o) {
        return this.a_locationFactory.isLeaf(o);
    }

    /**
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void addTreeModelListener(final TreeModelListener listener) {
        this.a_locationFactory.addTreeModelListener(listener);
        this.a_support.addTreeModelListener(listener);
    }

   /**
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void removeTreeModelListener(final TreeModelListener listener) {
        this.a_locationFactory.removeTreeModelListener(listener);
        this.a_support.removeTreeModelListener(listener);
    }

    /**
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
     */
    @Override
    public void valueForPathChanged(final TreePath arg0,
                                    final Object arg1) {
    	// do nothing
    }

    /**
     * @throws IOException
     */
    @Override
    public void save() throws IOException {

        if (this.a_isSaved) return;

        // prepare the data
        final String data[][] = new String[this.a_data.size()+1][];
        data[0] = new String[5];
        data[0][0] = "location";
        data[0][1] = "min. latitude";
        data[0][2] = "min. longitude";
        data[0][3] = "max. latitude";
        data[0][4] = "max. longitude";

        int i = 1;
        for ( HierarchicalCompoundString location : this.a_data.keySet() ) {
            data[i] = new String[5];
            data[i][0] = location.toLongString();
            final GPSData gps = this.a_data.get(location);
            data[i][1] = gps.getLatitudeMin();
            if ( data[i][1] == null ) data[i][1]="";
            data[i][2] = gps.getLongitudeMin();
            if ( data[i][2] == null ) data[i][2]="";
            data[i][3] = gps.getLatitudeMax();
            if ( data[i][3] == null ) data[i][3]="";
            data[i][4] = gps.getLongitudeMax();
            if ( data[i][4] == null ) data[i][4]="";
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
        final String name = this.a_excelFilename.replace(".txt","_"+backupName+".txt");
        final File file = new File(this.a_excelFilename);
        final File file2 = new File(name);
        if (!file.renameTo(file2)) {
            System.err.println("Failed to rename " + this.a_excelFilename + " into " + name);
        }

        // create the new file
        StringTableFromToExcel.save(this.a_excelFilename, data);

        // notify the SaveListerners
        setAsSaved();
    }

    /**
     * @return flag indicating if the current values are saved
     */
    public boolean isSaved() {
        return this.a_isSaved;
    }

    /**
     * record and notify that the data saved on disk is obsolete
     */
    private void setAsUnsaved() {
        if (this.a_isSaved) {
            this.a_isSaved = false;
            final SaveEvent f = new SaveEvent(this, false);
            for (SaveListener l : this.a_listOfSaveListeners) l.saveChanged(f);
        }
    }

    /**
     * record and notify that the data saved on disk is obsolete
     */
    private void setAsSaved() {
        this.a_isSaved = true;
        final SaveEvent f = new SaveEvent(this, true);
        for (SaveListener l : this.a_listOfSaveListeners) l.saveChanged(f);
    }

    /**
     * @param l
     */
    public void addSaveListener(final SaveListener l) {
        this.a_listOfSaveListeners.add(l);
    }

    /**
     * @param l
     */
    public void removeSaveListener(final SaveListener l) {
        this.a_listOfSaveListeners.remove(l);
    }

    /**
     * @param map
     */
    public void performLocationMapTranslation(final Map<String, String> map) {

        for ( final String oldLoc: map.keySet()) {

            final String newLoc = map.get(oldLoc);
            final HierarchicalCompoundString oldLocation = this.a_locationFactory.create(oldLoc);
            final HierarchicalCompoundString newLocation = this.a_locationFactory.create(newLoc);

            final GPSData oldData = this.a_data.get(oldLocation);
            final GPSData newData = this.a_data.get(newLocation);

            if ( oldData != null ) {
                if ( newData != null ){
                    if ( !oldData.equals(newData) ) {
                        System.err.println("Cannot transfer GPS coordinates from "
                                           + oldLocation.toLongString()
                                           + " ("
                                           + oldData.toString()
                                           + ") to "
                                           + newLocation.toLongString()
                                           + " ("
                                           + newData.toString()
                                           + ") because that one has already some different coordinates ");
                    }
                } else {
                    this.a_data.put(newLocation, oldData.clone());
                    setAsUnsaved();
                }
            }
        }
    }
}
