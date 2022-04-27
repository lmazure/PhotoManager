package lmzr.util.checktree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * adapdated from http://jroller.com/page/santhosh/Weblog/jtree_with_checkboxes?catname=
 *
 * I should kill this buggy code and do my own stuff:
 * use JTreeTable and allow to select an element without selecting its sub-elements
 */
public class CheckTreeManager extends MouseAdapter
                              implements TreeSelectionListener {

    private CheckTreeSelectionModel selectionModel;
    private JTree _tree = new JTree();
    int _hotspot = new JCheckBox().getPreferredSize().width;

    /**
     * @param tree
     */
    public CheckTreeManager(JTree tree){
        this._tree = tree;
        selectionModel = new CheckTreeSelectionModel(tree.getModel());
        tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel));
        tree.addMouseListener(this);
        selectionModel.addTreeSelectionListener(this);
    }

    /**
     * @return the tree
     */
    public JTree getTree() {
        return _tree;
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent me){
        if (me.getClickCount()!=1) return;
        TreePath path = _tree.getPathForLocation(me.getX(), me.getY());
        if(path==null)
            return;
        if(me.getX()>_tree.getPathBounds(path).x+_hotspot)
            return;

        boolean selected = selectionModel.isPathSelected(path, true);
        selectionModel.removeTreeSelectionListener(this);

        try{
            if(selected)
                selectionModel.removeSelectionPath(path);
            else
                selectionModel.addSelectionPath(path);
        } finally{
            selectionModel.addTreeSelectionListener(this);
            _tree.treeDidChange();
        }
    }

    /**
     * @return selection model
     */
    public CheckTreeSelectionModel getSelectionModel(){
        return selectionModel;
    }

    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    @Override
    public void valueChanged(final TreeSelectionEvent e){
        _tree.treeDidChange();
    }
}