package lmzr.util.string;

import java.text.Collator;

/**
 *
 */
public class HierarchicalCompoundString {

    final private String a_string;
    final private HierarchicalCompoundString a_parent;
    private HierarchicalCompoundString[] a_children;
    
    /**
     * constructor of a new HierarchicalCoumpoundString
     * the newly created HierarchicalCoumpoundString is added to the list of children of its parent
     * @param parent
     * @param string
     */
    HierarchicalCompoundString(final HierarchicalCompoundString parent,
                               final String string) {
         a_string = string;
         a_parent = parent;
         if (parent!=null) parent.addChild(this);
         a_children = new HierarchicalCompoundString[0];
    }
    
    /**
     * @return the children
     */
    public HierarchicalCompoundString[] getChildren() {
        return a_children;    
    }
    
    /**
     * @return the parent
     */
    public HierarchicalCompoundString getParent() {
        return a_parent;    
    }
    
    /**
     * @return as a long string, null is this is a root HierarchicalCoumpoundString  
     */
    public String toLongString() {
        if (getParent()==null) return "";
        final String string = a_parent.toLongString();
        return (string=="") ? toShortString() : (string + '>' + toShortString());
    }
    
    /**
     * @return as a short string, null is this is a root HierarchicalCoumpoundString
     */
    public String toShortString() {
        return a_string;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toLongString();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
    	if ( this == o ) return true;
    	if ( !(o instanceof HierarchicalCompoundString) ) return false;
    	return toString().equals(o.toString());
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
    return toString().hashCode();
    }
    
    /**
     * add a child to the HierarchicalCoumpoundString
     * @param child
     */
    private void addChild(final HierarchicalCompoundString child) {
        final HierarchicalCompoundString children[] = new HierarchicalCompoundString[a_children.length+1];
        final Collator collator = Collator.getInstance();
        int i;
        for (i=0; i<a_children.length; i++) {
            if ( collator.compare(child.toShortString(),a_children[i].toShortString()) < 0 ) break; 
            children[i]=a_children[i];   
        }
        children[i] = child;
        for (; i<a_children.length; i++) {
            children[i+1]=a_children[i];   
        }
        a_children = children;
    }
}
