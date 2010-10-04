package lmzr.photomngr.data.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import lmzr.photomngr.data.AuthorFactory;
import lmzr.photomngr.data.DataFormat;
import lmzr.photomngr.data.ListSelectionManager;
import lmzr.photomngr.data.Photo;
import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.PhotoListMetaDataEvent;
import lmzr.photomngr.data.PhotoListMetaDataListener;
import lmzr.photomngr.data.SaveEvent;
import lmzr.photomngr.data.SaveListener;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * @author Laurent Mazuré
 */
public class FilteredPhotoList implements PhotoList, PhotoListMetaDataListener, SaveListener, TableModelListener {

    final private PhotoList a_list;
    private int a_indexFromSource[];
    private int a_indexToSource[];
    private int a_rowCount;
    final private Vector<TableModelListener> a_listOfListeners;
    final private Vector<PhotoListMetaDataListener> a_listOfMetaDataListeners;
    final private Vector<SaveListener> a_listOfSaveListeners;
    private PhotoListFilter a_filter;

    /**
     * @param list
     */
    public FilteredPhotoList(final PhotoList list) {
        a_list = list;
        final int n = a_list.getRowCount();
        a_indexFromSource = new int[n];
        a_indexToSource = new int[n];
        a_listOfListeners = new Vector<TableModelListener>();
        a_listOfMetaDataListeners = new Vector<PhotoListMetaDataListener>();
        a_listOfSaveListeners = new Vector<SaveListener>();
        a_list.addMetaListener(this);
        a_list.addSaveListener(this);
        a_list.addTableModelListener(this);
	    final FilterOnPhotoTrait filterOnOriginality = new FilterOnPhotoTrait(PhotoOriginality.getTraits(),PhotoList.PARAM_ORIGINALITY);
	    final FilterOnPhotoTrait filterOnPrivacy = new FilterOnPhotoTrait(PhotoPrivacy.getTraits(),PhotoList.PARAM_PRIVACY);
	    final FilterOnPhotoTrait filterOnQuality = new FilterOnPhotoTrait(PhotoQuality.getTraits(),PhotoList.PARAM_QUALITY);
	    final FilterOnHierarchicalCompoundString filterOnLocation = new FilterOnHierarchicalCompoundString(PhotoList.PARAM_LOCATION);
	    final FilterOnHierarchicalCompoundString filterOnSubject1 = new FilterOnHierarchicalCompoundString(PhotoList.PARAM_SUBJECT);
	    final FilterOnHierarchicalCompoundString filterOnSubject2 = new FilterOnHierarchicalCompoundString(PhotoList.PARAM_SUBJECT);
	    final FilterOnHierarchicalCompoundString filterOnSubject3 = new FilterOnHierarchicalCompoundString(PhotoList.PARAM_SUBJECT);
	    final FilterOnHierarchicalCompoundString filterOnSubject4 = new FilterOnHierarchicalCompoundString(PhotoList.PARAM_SUBJECT);
	    final FilterOnFormat filterOnFormat = new FilterOnFormat(DataFormat.getAllFormats());
	    final FilterOnAuthor filterOnAuthor = new FilterOnAuthor();
	    final FilterOnCopies filterOnCopies = new FilterOnCopies();
        setFilter(new PhotoListFilter(filterOnOriginality,
        		                        filterOnPrivacy,
        		                        filterOnQuality,
        		                        filterOnLocation,
        		                        filterOnSubject1,
        		                        filterOnSubject2,
        		                        filterOnSubject3,
        		                        filterOnSubject4,
        		                        filterOnFormat,
        		                        filterOnAuthor,
        		                        filterOnCopies));
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#getRowCount()
     */
    public int getRowCount() {
        return a_rowCount;
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#getColumnCount()
     */
    public int getColumnCount() {
        return a_list.getColumnCount();
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#getPhoto(int)
     */
    public Photo getPhoto(final int index) {
        return a_list.getPhoto(a_indexFromSource[index]);
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#isSaved()
     */
    public boolean isSaved() {
        return a_list.isSaved();
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#getColumnName(int)
     */
    public String getColumnName(final int columnIndex) {
        return a_list.getColumnName(columnIndex);
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#getColumnClass(int)
     */
    public Class<?> getColumnClass(final int columnIndex) {
        return a_list.getColumnClass(columnIndex);
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#isCellEditable(int, int)
     */
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return a_list.isCellEditable(a_indexFromSource[rowIndex],columnIndex);
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return a_list.getValueAt(a_indexFromSource[rowIndex],columnIndex);
    }

    /**
     * @see lmzr.photomngr.data.PhotoList#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        a_list.setValueAt(value,a_indexFromSource[rowIndex],columnIndex);
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
     * @see lmzr.photomngr.data.PhotoList#save()
     */
    public void save() throws IOException {
        a_list.save();
    }

    /**
     * @see lmzr.photomngr.data.PhotoListMetaDataListener#photoListMetaDataChanged(lmzr.photomngr.data.PhotoListMetaDataEvent)
     */
    public void photoListMetaDataChanged(final PhotoListMetaDataEvent e) {
        final PhotoListMetaDataEvent f = new PhotoListMetaDataEvent(this,e.getChange());
        for (PhotoListMetaDataListener l: a_listOfMetaDataListeners) l.photoListMetaDataChanged(f);
    }

    /**
     * @see lmzr.photomngr.data.SaveListener#saveChanged(lmzr.photomngr.data.SaveEvent)
     */
    public void saveChanged(final SaveEvent e) {
        final SaveEvent f = new SaveEvent(this,e.isSaved());
        for (SaveListener l: a_listOfSaveListeners) l.saveChanged(f);
    }

    /**
     * @return location factory
     */
    public HierarchicalCompoundStringFactory getLocationFactory() {
        return a_list.getLocationFactory();
    }

    /**
     * @return subject factory
     */
    public MultiHierarchicalCompoundStringFactory getSubjectFactory() {
        return a_list.getSubjectFactory();
    }

    /**
     * @return author factory
     */
    public AuthorFactory getAuthorFactory() {
        return a_list.getAuthorFactory();
    }
    
    
    /**
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(final TableModelEvent e) {

    	if ( e.getType()==TableModelEvent.UPDATE && e.getLastRow()!=Integer.MAX_VALUE ) {
    		 
	    	final int inFirstRow = e.getFirstRow();
	        final int inLastRow = (e.getLastRow()==Integer.MAX_VALUE) ? (a_list.getRowCount()-1)
	                                                                  : e.getLastRow();
	    	
	        int outFirstRow = -1;
	        int outLastRow = -1;
	        for (int i=inFirstRow; i<=inLastRow; i++) {
	            final int index = a_indexToSource[i]; 
	            if (index!=-1) {
	                if (outFirstRow==-1) outFirstRow = index;
	                outLastRow = index;
	            }
	        }
    		for (TableModelListener l : a_listOfListeners) l.tableChanged(new TableModelEvent(this, outFirstRow, outLastRow));
    		
    	} else if ( e.getType()==TableModelEvent.INSERT ) {
    	    
            final int n = a_list.getRowCount();
            a_indexFromSource = new int[n];
            a_indexToSource = new int[n];

    	    applyFilter();
    	    
    	    int firstFilteredRow = -1;
            int lastFilteredRow = -1;
    	    
    	    for (int i=e.getFirstRow(); i<=e.getLastRow(); i++) {
    	           final int index = a_indexToSource[i];
    	            if ( index!=-1 ){
    	                if ( firstFilteredRow==-1 ) firstFilteredRow=index;
    	                lastFilteredRow = index;
    	            }
    	    }
    	    
    	    if ( firstFilteredRow!=-1) {
    	        final TableModelEvent ne = new TableModelEvent(this,
    	                                                       firstFilteredRow,
    	                                                       lastFilteredRow,
    	                                                       TableModelEvent.ALL_COLUMNS,
    	                                                       TableModelEvent.INSERT);
                for (TableModelListener l : a_listOfListeners) l.tableChanged(ne);
    	    }
    	    
    	} else {
    		for (TableModelListener l : a_listOfListeners) l.tableChanged(new TableModelEvent(this));
    	}
    }

    /**
     * @param filter
     * @param selection 
     */
    public void setFilter(final PhotoListFilter filter,
    		              final ListSelectionManager selection) {
    	
    	// record the current selection
    	final int select[] = selection.getSelection();
    	final int fromSelect[] = new int[select.length];
    	for (int i=0; i<select.length; i++) fromSelect[i] = a_indexFromSource[select[i]];
    	
    	// apply the new filter
        setFilter(filter);
               
        // send events
        final TableModelEvent e = new TableModelEvent(this);
        tableChanged(e);
        final PhotoListMetaDataEvent f = new PhotoListMetaDataEvent(this, PhotoListMetaDataEvent.FILTER_HAS_CHANGED);
        photoListMetaDataChanged(f);

        // update selection
        final int toSelect[] = new int[ (select.length>0) ? select.length : 1 ];
        int toSelectLength = 0;
    	for (int i=0; i<select.length; i++) { 
    		final int index = a_indexToSource[fromSelect[i]];
    		if ( index!=-1 ){
        		toSelect[toSelectLength++] = index;    			
    		}
    	}
    	if ( toSelectLength==0 && a_rowCount>0 ){
    		toSelect[0] = a_rowCount-1;
    		toSelectLength = 1;
    	}
        int finalSelect[] = new int[toSelectLength];
        for (int i=0; i<toSelectLength; i++) finalSelect[i] = toSelect[i];
        selection.setSelection(finalSelect);
    }
    
    /**
     * @return current filter
     */
    public PhotoListFilter getFilter() {
        return a_filter;
    }
    
    /**
     * @param filter
     */
    private void setFilter(final PhotoListFilter filter) {
    	a_filter = filter;
    	applyFilter();
    }
    
    /**
     * 
     */
    private void applyFilter() {
        final int n = a_list.getRowCount();
        a_rowCount = 0;
        for (int i=0; i<n; i++) {
            if (a_filter.filter(a_list,i)) {
                a_indexFromSource[a_rowCount] = i;
                a_indexToSource[i] = a_rowCount;
                a_rowCount++;
            } else {
                a_indexToSource[i] = -1;                
            }
        }

    }

	/**
	 * @see lmzr.photomngr.data.PhotoList#performSubjectMapTranslation(java.util.Map)
	 */
	@Override
	public void performSubjectMapTranslation(final Map<String, String> map) {
		a_list.performSubjectMapTranslation(map);
    	applyFilter();
	}

	/**
	 * @see lmzr.photomngr.data.PhotoList#performLocationMapTranslation(java.util.Map)
	 */
	@Override
	public void performLocationMapTranslation(final Map<String, String> map) {
		a_list.performLocationMapTranslation(map);
    	applyFilter();
	}

}
