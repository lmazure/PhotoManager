package lmzr.photomngr.ui.filter;

import java.util.Set;

import javax.swing.JScrollPane;

import lmzr.photomngr.data.filter.FilterOnHierarchicalCompoundString;
import lmzr.photomngr.ui.treeSelectioner.TreeSelectioner;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 *
 */
public class HierachicalCompoundStringComponentFilterUI extends ComponentFilterUI {

    final private TreeSelectioner a_tree;

    /**
     * @param title
     * @param factory
     * @param filter
     */
    public HierachicalCompoundStringComponentFilterUI(final String title,
                                                      final HierarchicalCompoundStringFactory factory,
                                                      final FilterOnHierarchicalCompoundString filter) {
       super(title, filter);
       a_tree = new TreeSelectioner(title, factory, TreeSelectioner.MODE_MULTI_SELECTION_WITH_SELECT_ALL_COLUMN);
       final JScrollPane scrollpane = new JScrollPane(a_tree);
       getPane().add(scrollpane);
       a_tree.setSelection(filter.getValues());
    }

    /**
     * @param title
     * @param factory
     * @param filter
     */
    public HierachicalCompoundStringComponentFilterUI(final String title,
                                                      final MultiHierarchicalCompoundStringFactory factory,
                                                      final FilterOnHierarchicalCompoundString filter) {
       super(title, filter);
       a_tree = new TreeSelectioner(title, factory.getHierarchicalCompoundStringFactory(), TreeSelectioner.MODE_MULTI_SELECTION_WITH_SELECT_ALL_COLUMN);
       final JScrollPane scrollpane = new JScrollPane(a_tree);
       getPane().add(scrollpane);
       setFilterEnabled(filter.isEnabled());
       a_tree.setSelection(filter.getValues());
     }

    /**
     * @return selected subjects
     */
    public Set<HierarchicalCompoundString> getValues() {
        return a_tree.getSelection();
    }
}
