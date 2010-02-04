package lmzr.photomngr.ui.filter;

import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

import lmzr.photomngr.data.filter.FilterOnHierarchicalCompoundString;
import lmzr.photomngr.ui.HierarchicalCompoundStringTreeDisplay;
import lmzr.photomngr.ui.MultiHierarchicalCompoundStringTreeDisplay;
import lmzr.util.checktree.CheckTreeManager;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * 
 */
public class FilterOnHierachicalCompoundStringComponent extends FilterComponent {

	final CheckTreeManager a_display;
	
    /**
     * @param title
     * @param factory
     * @param filter 
     */
    public FilterOnHierachicalCompoundStringComponent(final String title,
    		                                          final HierarchicalCompoundStringFactory factory,
    		                                          final FilterOnHierarchicalCompoundString filter) {
       super(title);
       a_display = new CheckTreeManager(new HierarchicalCompoundStringTreeDisplay(factory));
       final JScrollPane scrollpane = new JScrollPane(a_display.getTree());
       getPane().add(scrollpane);
       setFilterEnabled(filter.isEnabled());
       final HierarchicalCompoundString values[] = filter.getValues();
       final int length = (values==null) ? 0 : values.length;
       final TreePath paths[] = new TreePath[length];
       for (int i=0; i<length; i++) paths[i]=buildPath(values[i]);
       a_display.getSelectionModel().setSelectionPaths(paths);
    }
    
    /**
     * @param title
     * @param factory
     * @param filter
     */
    public FilterOnHierachicalCompoundStringComponent(final String title,
    		                                          final MultiHierarchicalCompoundStringFactory factory,
    		                                          final FilterOnHierarchicalCompoundString filter) {
       super(title);
       a_display = new CheckTreeManager(new MultiHierarchicalCompoundStringTreeDisplay(factory));
       final JScrollPane scrollpane = new JScrollPane(a_display.getTree());
       getPane().add(scrollpane);
       setFilterEnabled(filter.isEnabled());
       final HierarchicalCompoundString values[] = filter.getValues();
       final int length = (values==null) ? 0 : values.length;
       final TreePath paths[] = new TreePath[length];
       for (int i=0; i<length; i++) paths[i]=buildPath(values[i]);
       a_display.getSelectionModel().setSelectionPaths(paths);
    }
    
    
    /**
     * @return selected subjects
     */
    public HierarchicalCompoundString[] getValues() {
    	if (!isFilterEnabled()) return null;
        final TreePath checkedPaths[] = a_display.getSelectionModel().getSelectionPaths();
        if ( checkedPaths==null ) return new HierarchicalCompoundString[0];
        final HierarchicalCompoundString values[] = new HierarchicalCompoundString[checkedPaths.length];
        for (int i=0; i<checkedPaths.length; i++) values[i]=(HierarchicalCompoundString)(checkedPaths[i].getLastPathComponent());
        return values;
    }
    
    /**
     * @param string
     * @return TreePath corresponding to a HierarchicalCompoundString
     */
    static private TreePath buildPath(final HierarchicalCompoundString string) {
    	if (string.getParent()==null) {
    		return new TreePath(string);
    	}
		return buildPath(string.getParent()).pathByAddingChild(string);
    }
}
