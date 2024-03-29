package lmzr.util.checktree;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * adapdated from http://jroller.com/page/santhosh/Weblog/jtree_with_checkboxes?catname=
 */
public class CheckTreeSelectionModel extends DefaultTreeSelectionModel {

    final private TreeModel a_model;

    /**
     * @param model
     */
    public CheckTreeSelectionModel(final TreeModel model){
        a_model = model;
        setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }

    /**
     * tests whether there is any unselected node in the subtree of given path
     * @param path
     * @return flag
     */
    public boolean isPartiallySelected(final TreePath path){
        if(isPathSelected(path, true)) {
            return false;
        }
        final TreePath[] selectionPaths = getSelectionPaths();
        if(selectionPaths==null) {
            return false;
        }
        for (final TreePath selectionPath : selectionPaths) {
            if(isDescendant(selectionPath, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * tells whether given path is selected.
     * if dig is true, then a path is assumed to be selected, if
     * one of its ancestor is selected.
     * @param path
     * @param dig
     * @return flag
     */
    public boolean isPathSelected(TreePath path, final boolean dig){
        if(!dig) {
            return super.isPathSelected(path);
        }
        while(path!=null && !super.isPathSelected(path)) {
            path = path.getParentPath();
        }
        return path!=null;
    }

    // is path1 descendant of path2
    private static boolean isDescendant(final TreePath path1, final TreePath path2){
        final Object obj1[] = path1.getPath();
        final Object obj2[] = path2.getPath();
        for(int i = 0; i<obj2.length; i++){
            if(obj1[i]!=obj2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see javax.swing.tree.TreeSelectionModel#setSelectionPaths(javax.swing.tree.TreePath[])
     */
    @Override
    public void setSelectionPaths(final TreePath[] paths){
        final TreePath selectedPaths[] = getSelectionPaths();
        if ( selectedPaths!=null ) {
            removeSelectionPaths(selectedPaths);
        }
        /*for (int i=0; i<paths.length; i++)*/ addSelectionPaths(paths);
    }

    /**
     * @see javax.swing.tree.TreeSelectionModel#addSelectionPaths(javax.swing.tree.TreePath[])
     */
    @Override
    public void addSelectionPaths(final TreePath[] paths){
        // unselect all descendants of paths[]
        for (final TreePath path : paths) {
            final TreePath[] selectionPaths = getSelectionPaths();
            if(selectionPaths==null) {
                break;
            }
            final ArrayList<TreePath> toBeRemoved = new ArrayList<>();
            for (final TreePath selectionPath : selectionPaths) {
                if(isDescendant(selectionPath, path)) {
                    toBeRemoved.add(selectionPath);
                }
            }
            super.removeSelectionPaths(toBeRemoved.toArray(new TreePath[0]));
        }

        // if all siblings are selected then unselect them and select parent recursively
        // otherwize just select that path.
        for (final TreePath path2 : paths) {
            TreePath path = path2;
            TreePath temp = null;
            while(areSiblingsSelected(path)){
                temp = path;
                if(path.getParentPath()==null) {
                    break;
                }
                path = path.getParentPath();
            }
            if(temp!=null){
                if(temp.getParentPath()!=null) {
                    addSelectionPath(temp.getParentPath());
                } else{
                    if(!isSelectionEmpty()) {
                        removeSelectionPaths(getSelectionPaths());
                    }
                    super.addSelectionPaths(new TreePath[]{temp});
                }
            }else
                if ( getSelectionMode()== TreeSelectionModel.SINGLE_TREE_SELECTION) {
                    super.setSelectionPaths(new TreePath[]{ path});
                } else {
                    super.addSelectionPaths(new TreePath[]{ path});
                }
        }
    }

    // tells whether all siblings of given path are selected.
    private boolean areSiblingsSelected(final TreePath path){
        final TreePath parent = path.getParentPath();
        if(parent==null) {
            return true;
        }
        final Object node = path.getLastPathComponent();
        final Object parentNode = parent.getLastPathComponent();

        final int childCount = a_model.getChildCount(parentNode);
        for(int i = 0; i<childCount; i++){
            final Object childNode = a_model.getChild(parentNode, i);
            if(childNode==node) {
                continue;
            }
            if(!isPathSelected(parent.pathByAddingChild(childNode))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see javax.swing.tree.TreeSelectionModel#removeSelectionPaths(javax.swing.tree.TreePath[])
     */
    @Override
    public void removeSelectionPaths(final TreePath[] paths){
        for (final TreePath path : paths) {
            if(path.getPathCount()==1) {
                super.removeSelectionPaths(new TreePath[]{ path});
            } else {
                toggleRemoveSelection(path);
            }
        }
    }

    // if any ancestor node of given path is selected then unselect it
    //  and selection all its descendants except given path and descendants.
    // otherwise just unselect the given path
    private void toggleRemoveSelection(final TreePath path){
        final Stack<TreePath> stack = new Stack<>();
        TreePath parent = path.getParentPath();
        while(parent!=null && !isPathSelected(parent)){
            stack.push(parent);
            parent = parent.getParentPath();
        }
        if(parent == null) {
            super.removeSelectionPaths(new TreePath[]{path});
            return;
        }
        stack.push(parent);

        while(!stack.isEmpty()){
            final TreePath temp = stack.pop();
            final TreePath peekPath = stack.isEmpty() ? path : stack.peek();
            final Object node = temp.getLastPathComponent();
            final Object peekNode = peekPath.getLastPathComponent();
            final int childCount = a_model.getChildCount(node);
            for(int i = 0; i<childCount; i++){
                final Object childNode = a_model.getChild(node, i);
                if(childNode!=peekNode) {
                    super.addSelectionPaths(new TreePath[]{temp.pathByAddingChild(childNode)});
                }
            }
        }
        super.removeSelectionPaths(new TreePath[]{parent});
    }
}