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
        this.selectionModel = new CheckTreeSelectionModel(tree.getModel());
        tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), this.selectionModel));
        tree.addMouseListener(this);
        this.selectionModel.addTreeSelectionListener(this);
    }

    /**
     * @return the tree
     */
    public JTree getTree() {
        return this._tree;
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent me){
        if (me.getClickCount()!=1) return;
        TreePath path = this._tree.getPathForLocation(me.getX(), me.getY());
        if(path==null)
            return;
        if(me.getX()>this._tree.getPathBounds(path).x+this._hotspot)
            return;

        boolean selected = this.selectionModel.isPathSelected(path, true);
        this.selectionModel.removeTreeSelectionListener(this);

        try{
            if(selected)
                this.selectionModel.removeSelectionPath(path);
            else
                this.selectionModel.addSelectionPath(path);
        } finally{
            this.selectionModel.addTreeSelectionListener(this);
            this._tree.treeDidChange();
        }
    }

    /**
     * @return selection model
     */
    public CheckTreeSelectionModel getSelectionModel(){
        return this.selectionModel;
    }

    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    @Override
    public void valueChanged(final TreeSelectionEvent e){
        this._tree.treeDidChange();
    }
}