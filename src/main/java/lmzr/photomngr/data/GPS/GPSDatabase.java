package lmzr.photomngr.data.GPS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        private final HierarchicalCompoundString a_location;
        private final GPSData a_GPSData;

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
        a_data = new HashMap<>();
        a_listOfSaveListeners = new Vector<>();
        setAsSaved();

        if (!Files.exists(Paths.get(excelFilename))) {
            System.err.println("GPS database file (" + excelFilename + ") does not exist.");
            return;
        }

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
            a_data.put(l,d);
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
            if ( d != null) {
                return new GPSRecord(l,d);
            }
            l = l.getParent();
        } while ( l != null);
        return null;
    }

    /**
     * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return switch (columnIndex) {
        case PARAM_LOCATION, PARAM_LATITUDE_MIN, PARAM_LATITUDE_MAX, PARAM_LONGITUDE_MIN, PARAM_LONGITUDE_MAX -> String.class;
        case PARAM_GPS_DATA_FOR_MAPPING, PARAM_GPS_DATA_FOR_DELETING -> GPSRecord.class;
        default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
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
        return switch (columnIndex) {
        case PARAM_LOCATION -> "location";
        case PARAM_LATITUDE_MIN -> "min. latitude";
        case PARAM_LATITUDE_MAX -> "max. latitude";
        case PARAM_LONGITUDE_MIN -> "min. longitude";
        case PARAM_LONGITUDE_MAX -> "max. longitude";
        case PARAM_GPS_DATA_FOR_MAPPING -> "Geoportail";
        case PARAM_GPS_DATA_FOR_DELETING -> "delete";
        default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
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
            if (data==null) {
                return "";
            }
            final String latMin =  data.getLatitudeMin();
            return ( latMin == null ) ? "" : latMin;
        case PARAM_LATITUDE_MAX:
            if (data==null) {
                return "";
            }
            final String latMax = data.getLatitudeMax();
            return ( latMax == null ) ? "" : latMax;
        case PARAM_LONGITUDE_MIN:
            if (data==null) {
                return "";
            }
            final String longMin =  data.getLongitudeMin();
            return ( longMin == null ) ? "" : longMin;
        case PARAM_LONGITUDE_MAX:
            if (data==null) {
                return "";
            }
            final String longMax = data.getLongitudeMax();
            return ( longMax == null ) ? "" : longMax;
        case PARAM_GPS_DATA_FOR_MAPPING:
        case PARAM_GPS_DATA_FOR_DELETING:
            return new GPSRecord(location,data);
        default:
            throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        }
    }

    /**
     * @see org.jdesktop.swingx.treetable.TreeTableModel#isCellEditable(java.lang.Object, int)
     */
    @Override
    public boolean isCellEditable(final Object node,
                                  final int columnIndex) {
        return switch (columnIndex) {
        case PARAM_LOCATION -> false;
        case PARAM_LATITUDE_MIN, PARAM_LATITUDE_MAX, PARAM_LONGITUDE_MIN, PARAM_LONGITUDE_MAX -> true;
        case PARAM_GPS_DATA_FOR_MAPPING, PARAM_GPS_DATA_FOR_DELETING -> true; // necessary because the column contains buttons and clicks are handled by the CellEditor
        default -> throw new IllegalArgumentException("Unknown column index: " + columnIndex);
        };
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

        String str = (String)value;
        try {
            switch (columnIndex) {
            case PARAM_LOCATION:
                break;
            case PARAM_LATITUDE_MIN:
                if ( str!=null && str.length()==0 ) {
                    str = null;
                }
                if ( str == null ) {
                    if ( data.getLatitudeMin() == null ) {
                        return;
                    }
                } else {
                    if ( str.equals(data.getLatitudeMin())) {
                        return;
                    }
                }
                data.setLatitudeMin(str);
                break;
            case PARAM_LATITUDE_MAX:
                if ( str!=null && str.length()==0 ) {
                    str = null;
                }
                if ( str == null ) {
                    if ( data.getLatitudeMax() == null ) {
                        return;
                    }
                } else {
                    if ( str.equals(data.getLatitudeMax())) {
                        return;
                    }
                }
                data.setLatitudeMax(str);
                break;
            case PARAM_LONGITUDE_MIN:
                if ( str!=null && str.length()==0 ) {
                    str = null;
                }
                if ( str == null ) {
                    if ( data.getLongitudeMin() == null ) {
                        return;
                    }
                } else {
                    if ( str.equals(data.getLongitudeMin())) {
                        return;
                    }
                }
                data.setLongitudeMin(str);
                break;
            case PARAM_LONGITUDE_MAX:
                if ( str!=null && str.length() == 0) {
                    str = null;
                }
                if ( str == null ) {
                    if ( data.getLongitudeMax() == null ) {
                        return;
                    }
                } else {
                    if ( str.equals(data.getLongitudeMax())) {
                        return;
                    }
                }
                data.setLongitudeMax(str);
                break;
            case PARAM_GPS_DATA_FOR_MAPPING:
            case PARAM_GPS_DATA_FOR_DELETING:
                break;
            default:
                throw new IllegalArgumentException("Unknown column index: " + columnIndex);
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
        // do nothing
    }

    /**
     * @throws IOException
     */
    @Override
    public void save() throws IOException {

        if (a_isSaved) {
            return;
        }

        // prepare the data
        final String data[][] = new String[a_data.size()+1][];
        data[0] = new String[5];
        data[0][0] = "location";
        data[0][1] = "min. latitude";
        data[0][2] = "min. longitude";
        data[0][3] = "max. latitude";
        data[0][4] = "max. longitude";

        int i = 1;
        for ( final HierarchicalCompoundString location : a_data.keySet() ) {
            data[i] = new String[5];
            data[i][0] = location.toLongString();
            final GPSData gps = a_data.get(location);
            data[i][1] = gps.getLatitudeMin();
            if ( data[i][1] == null ) {
                data[i][1]="";
            }
            data[i][2] = gps.getLongitudeMin();
            if ( data[i][2] == null ) {
                data[i][2]="";
            }
            data[i][3] = gps.getLatitudeMax();
            if ( data[i][3] == null ) {
                data[i][3]="";
            }
            data[i][4] = gps.getLongitudeMax();
            if ( data[i][4] == null ) {
                data[i][4]="";
            }
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
        if (!file.renameTo(file2)) {
            System.err.println("Failed to rename " + a_excelFilename + " into " + name);
        }

        // create the new file
        StringTableFromToExcel.save(a_excelFilename, data);

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

        for ( final String oldLoc: map.keySet()) {

            final String newLoc = map.get(oldLoc);
            final HierarchicalCompoundString oldLocation = a_locationFactory.create(oldLoc);
            final HierarchicalCompoundString newLocation = a_locationFactory.create(newLoc);

            final GPSData oldData = a_data.get(oldLocation);
            final GPSData newData = a_data.get(newLocation);

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
                    a_data.put(newLocation, oldData.clone());
                    setAsUnsaved();
                }
            }
        }
    }
}
