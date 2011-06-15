package lmzr.photomngr.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;

import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;

/**
 * @author Laurent Mazuré
 */
public class PhotoParametersTableModel implements TableModel, ListSelectionListener {

	private List<Map<String,String>> a_maps;
	List<String> a_tags;
    
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
		updateData();
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
		return a_tags.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int rowIndex,
			                 final int columnIndex) {

		final String tag = a_tags.get(rowIndex);

		if ( columnIndex == 0) {
			return tag;
		}
		
		return a_maps.get(columnIndex-1).get(tag);
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
		updateData();
        final TableModelEvent event = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
        for (TableModelListener l : a_listOfListeners) l.tableChanged(event);		
	}

	private void updateData() {

        final int selection[] = a_selection.getSelection();
        a_maps = new ArrayList<Map<String,String>>();
        Set<String> tags = new HashSet<String>();
        a_tags = new ArrayList<String>();

        for ( final int index : selection ) {
        	final String filename = a_photoList.getPhoto(index).getFullPath();
        	final Map<String,String> map = getTags(filename);
        	a_maps.add(map);
        	tags.addAll(map.keySet());
        }
        
        a_tags = new ArrayList<String>(tags);
        Collections.sort(a_tags, String.CASE_INSENSITIVE_ORDER);
	}
	
    /**
     * @return a set containing the EXIF tags of the file
     *         if the file was not properly parsed, an empty set is returned
     */
    private Map<String,String> getTags(final String filename) {
    	
    	final Map<String,String> map = new HashMap<String,String>();    	
    		
        try {
            final File file = new File(filename);
            final Metadata metadata = JpegMetadataReader.readMetadata(file);
            final Iterator<?> directories = metadata.getDirectoryIterator();
            while (directories.hasNext()) {
                final Directory directory = (Directory)directories.next();
                final Iterator<?> tags = directory.getTagIterator();
                while (tags.hasNext()) {
                	final Tag tag = (Tag)tags.next();
                    map.put( tag.getDirectoryName() + " / " + tag.getTagName(), directory.getDescription(tag.getTagType()) );
                }
            }
        } catch (final JpegProcessingException e) {
			// should never occur, the data is corrupted
			e.printStackTrace();
        } catch (final MetadataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return map;
    }
}
