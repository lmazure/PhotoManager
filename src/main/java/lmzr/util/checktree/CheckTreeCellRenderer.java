// adapdated from http://jroller.com/page/santhosh/Weblog/jtree_with_checkboxes?catname=

package lmzr.util.checktree;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * @author Laurent
 *
 */
public class CheckTreeCellRenderer extends JPanel
                                   implements TreeCellRenderer{

    final private CheckTreeSelectionModel a_selectionModel;
    final private TreeCellRenderer a_delegate;
    final private TristateCheckBox a_checkBox;

    /**
     * @param delegate
     * @param selectionModel
     */
    public CheckTreeCellRenderer(final TreeCellRenderer delegate,
                                 final CheckTreeSelectionModel selectionModel){
        a_delegate = delegate;
        a_selectionModel = selectionModel;
        a_checkBox = new TristateCheckBox();
        setLayout(new BorderLayout());
        setOpaque(false);
        a_checkBox.setOpaque(false);
    }


    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
                                                  final Object value,
                                                  final boolean selected,
                                                  final boolean expanded,
                                                  final boolean leaf,
                                                  final int row,
                                                  final boolean hasFocus){
        final Component renderer = a_delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        final TreePath path = tree.getPathForRow(row);
        if(path!=null){
            if(a_selectionModel.isPathSelected(path, true))
                a_checkBox.setState(TristateCheckBox.SELECTED);
            else
                a_checkBox.setState(a_selectionModel.isPartiallySelected(path) ? TristateCheckBox.DONT_CARE : TristateCheckBox.NOT_SELECTED);
        }
        removeAll();
        add(a_checkBox, BorderLayout.WEST);
        add(renderer, BorderLayout.CENTER);
        return this;
    }
}