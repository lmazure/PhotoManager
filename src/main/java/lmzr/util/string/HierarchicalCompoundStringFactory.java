/*
 * Created on 8 juil. 2005 by Laurent Mazuré
 */

package lmzr.util.string;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 */
public class HierarchicalCompoundStringFactory implements TreeModel {

    final private HierarchicalCompoundString a_root;
    final private Vector<TreeModelListener> a_listOfListeners;
    private SoftReference<Hashtable<String,HierarchicalCompoundString>> a_cacheSoftRef;


    /**
     * create a factory of HierarchicalCoumpountStrings
     */
    public HierarchicalCompoundStringFactory() {
        this.a_root = new HierarchicalCompoundString(null,"#");
        this.a_listOfListeners = new Vector<>();
        final Hashtable<String,HierarchicalCompoundString> cache = new Hashtable<>();
        this.a_cacheSoftRef = new SoftReference<>(cache);
    }

    /**
     * @param string
     * @return newly built HierarchicalCoumpountString
     */
    public HierarchicalCompoundString create(final String string) {

        if (string.length()==0) return this.a_root;

        Hashtable<String,HierarchicalCompoundString> cache = this.a_cacheSoftRef.get();
        if ( cache != null ) {
            final HierarchicalCompoundString cached = cache.get(string);
            if ( cached != null ) return cached;
        } else {
            cache = new Hashtable<>();
            this.a_cacheSoftRef = new SoftReference<>(cache);
        }

        final int index = string.lastIndexOf('>');
        final HierarchicalCompoundString parent = (index==-1) ? this.a_root : create(string.substring(0,index));
        final String endString = string.substring(index+1);

        final HierarchicalCompoundString[] parentsChildren = parent.getChildren();
        for (int i=0; i<parentsChildren.length; i++) {
            final HierarchicalCompoundString child = parentsChildren[i];
            if (child.toShortString().equals(endString)) return child;
        }

        final HierarchicalCompoundString newString = new HierarchicalCompoundString(parent,endString);

        if ( this.a_listOfListeners.size()>0 ) {
            final TreeModelEvent event = new TreeModelEvent(this,
                                                            getPath(parent),
                                                            new int[] { getIndexOfChild(parent,newString) },
                                                            new Object[] { newString });
            for ( TreeModelListener l: this.a_listOfListeners) {
                l.treeNodesInserted(event);
            }
        }

        cache.put(string,newString);

        return newString;
    }

    /**
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    @Override
    public Object getRoot() {
        return this.a_root;
    }

    /**
     * @return the root of the factory as a HierarchicalCompoundString
     */
    public HierarchicalCompoundString getRootAsHierarchicalCompoundString() {
        return this.a_root;
    }

    /**
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    @Override
    public Object getChild(final Object o,
                           final int index) {
        final HierarchicalCompoundString string = (HierarchicalCompoundString)o;
        return string.getChildren()[index];
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    @Override
    public int getChildCount(final Object o) {
        final HierarchicalCompoundString string = (HierarchicalCompoundString)o;
        return string.getChildren().length;
    }

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    @Override
    public boolean isLeaf(final Object o) {
        final HierarchicalCompoundString string = (HierarchicalCompoundString)o;
        return (string.getChildren().length==0);
    }

    /**
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
     */
    @Override
    public void valueForPathChanged(final TreePath path,
                                    final Object value) {
        // the Factory is not editable for the time being
    }

    /**
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    @Override
    public int getIndexOfChild(final Object o,
                               final Object c) {

        final HierarchicalCompoundString string = (HierarchicalCompoundString)o;
        final HierarchicalCompoundString child = (HierarchicalCompoundString)c;

        final HierarchicalCompoundString[] children = string.getChildren();

        for (int i=0; i<children.length; i++) {
            if (child==children[i]) return i;
        }

        return -1;
    }

    /**
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void addTreeModelListener(final TreeModelListener l) {
        this.a_listOfListeners.add(l);
    }

    /**
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void removeTreeModelListener(final TreeModelListener l) {
        this.a_listOfListeners.remove(l);
    }

    /**
     * @param node
     * @return path of the node
     */
    static public TreePath getPath(final HierarchicalCompoundString node) {

        HierarchicalCompoundString n = node;

        final List<HierarchicalCompoundString> list = new ArrayList<>();
        while (n != null) {
            list.add(n);
            n = n.getParent();
        }
        Collections.reverse(list);

        return new TreePath(list.toArray());
    }
}