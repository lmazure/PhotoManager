package lmzr.photomngr.ui.treeSelectioner;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;

import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.TreeTableModel;

public class DatabaseForTreeSelectioner implements TreeTableModel {

	/**
	 * 
	 */
	static final public int PARAM_VALUE = 0;
	/**
	 * 
	 */
	static final public int PARAM_SELECTED = 1;
	/**
	 * 
	 */
	static final public int PARAM_SELECT_ALL = 2;

	final private HierarchicalCompoundStringFactory a_CompoundStringFactory;
	final private HashSet<HierarchicalCompoundString> a_selection;
    final private TreeModelSupport a_support; 
    final private String a_dataDescription;
    final private int a_mode;
       
    
    /**
     * @param locationFactory
     */
    protected DatabaseForTreeSelectioner(final String dataDescription,
    		                             final HierarchicalCompoundStringFactory compoundStringFactory,
    		                             final int mode) {
        
    	a_support = new TreeModelSupport(this);
    	a_dataDescription = dataDescription;
    	a_CompoundStringFactory = compoundStringFactory;
    	a_selection = new HashSet<HierarchicalCompoundString>();        
    	a_mode = mode;
    }
    
	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		switch (columnIndex) {
		case PARAM_VALUE:
			return String.class;
		case PARAM_SELECTED:
			return Boolean.class;
		case PARAM_SELECT_ALL:
			return Boolean.class;
		}
		
		return null;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return (a_mode == TreeSelectioner.MODE_MULTI_SELECTION_WITH_SELECT_ALL_COLUMN) ? 3 : 2;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int columnIndex) {
		switch (columnIndex) {
		case PARAM_VALUE:
			return a_dataDescription;
		case PARAM_SELECTED:
		    return "(un)select";
		case PARAM_SELECT_ALL:
		    return "(un)select all";
		}
		
		return null;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getHierarchicalColumn()
	 */
	@Override
	public int getHierarchicalColumn() {
		return PARAM_VALUE;
	}

	/**
	 * @see org.jdesktop.swingx.treetable.TreeTableModel#getValueAt(java.lang.Object, int)
	 */
	@Override
	public Object getValueAt(final Object node,
			                 final int columnIndex) {
		
		final HierarchicalCompoundString string = (HierarchicalCompoundString)node;
		
		switch (columnIndex) {
		case PARAM_VALUE:
			return string.toShortString();
		case PARAM_SELECTED:
			return a_selection.contains(string);
		case PARAM_SELECT_ALL:
			return new Boolean(isSubtreeSelected(string));
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
		case PARAM_VALUE:
			return false;
		case PARAM_SELECTED:
        case PARAM_SELECT_ALL:
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
		
		final HierarchicalCompoundString string = (HierarchicalCompoundString)node;

		switch (columnIndex) {
		case PARAM_VALUE:
			break;
		case PARAM_SELECTED: {
			final Boolean b = (Boolean)value;
			if ( b.booleanValue()) {
				if ( (a_mode == TreeSelectioner.MODE_MONO_SELECTION) &&
					 (a_selection.size() > 0 ) ) {
					// unselect the current selection
					final HierarchicalCompoundString s = a_selection.iterator().next();
					setValueAt(new Boolean(false),s,PARAM_SELECTED);
				}
				a_selection.add(string);				
			} else {
				a_selection.remove(string);
			}
			notifyNode(string);
			notifyUpperNodes(string);
		}
			break;
		case PARAM_SELECT_ALL: {
			final Boolean b = (Boolean)value;
			selectAndNotifySubtree(string, b.booleanValue());
			notifyUpperNodes(string);
			}
			break;
		}
	}

	/**
	 * @param string
	 */
	private void notifyNode(final HierarchicalCompoundString string) {
		
		a_support.fireChildChanged(HierarchicalCompoundStringFactory.getPath(string.getParent()),
                                   a_CompoundStringFactory.getIndexOfChild(string.getParent(),string),
                                   string);
	}
	
	/**
	 * @param string
	 */
	private void notifyUpperNodes(final HierarchicalCompoundString string) {
		
		HierarchicalCompoundString s = string;
		
		while ( (s = s.getParent()) != a_CompoundStringFactory.getRootAsHierarchicalCompoundString() ) {
			notifyNode(s);
		}
	}
	
	/**
	 * @param string
	 * @param b
	 */
	private void selectAndNotifySubtree(final HierarchicalCompoundString string,
			                            final boolean b) {
		if ( b ) {
			if ( !a_selection.contains(string) ) {
				a_selection.add(string);
				notifyNode(string);
			}
		} else {
			if ( a_selection.contains(string) ) {
				a_selection.remove(string);
				notifyNode(string);
			}
		}
		
		final int n = a_CompoundStringFactory.getChildCount(string);
		
		for (int i=0; i<n; i++) {
			selectAndNotifySubtree((HierarchicalCompoundString)a_CompoundStringFactory.getChild(string,i),b);
		}
	}
	
	/**
	 * @param string
	 * @return
	 */
	private boolean isSubtreeSelected(final HierarchicalCompoundString string) {
		
		if ( !a_selection.contains(string) ) return false;
		
		final int n = a_CompoundStringFactory.getChildCount(string);
		
		for (int i=0; i<n; i++) {
			if ( !isSubtreeSelected((HierarchicalCompoundString)a_CompoundStringFactory.getChild(string,i)) ) return false; 
		}
		
		return true;
	}
	
	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(final Object o,
			               final int index) {
		return a_CompoundStringFactory.getChild(o, index);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(final Object o) {
		return a_CompoundStringFactory.getChildCount(o);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(final Object o,
			                   final Object c) {
		return a_CompoundStringFactory.getIndexOfChild(o, c);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return a_CompoundStringFactory.getRoot();
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(final Object o) {
		return a_CompoundStringFactory.isLeaf(o);
	}

	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void addTreeModelListener(final TreeModelListener listener) {
		a_CompoundStringFactory.addTreeModelListener(listener);
		a_support.addTreeModelListener(listener);
	}

   /**
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void removeTreeModelListener(final TreeModelListener listener) {
		a_CompoundStringFactory.removeTreeModelListener(listener);
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

	/**
	 * @return
	 */
	final protected Set<HierarchicalCompoundString> getSelection() {
		return a_selection;
	}

	/**
	 * @param selection
	 */
	final protected void setSelection(final Set<HierarchicalCompoundString> selection) {
		
		final HashSet<HierarchicalCompoundString> oldSelection = new HashSet<HierarchicalCompoundString>(a_selection); 
			
		for (HierarchicalCompoundString h : oldSelection ) {
			setValueAt(new Boolean(false), h, PARAM_SELECTED);
		}

		for (HierarchicalCompoundString h : selection ) {
			setValueAt(new Boolean(true), h, PARAM_SELECTED);
		}
	}

}