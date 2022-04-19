package lmzr.util.string;

/**
 *
 */
public class MultiHierarchicalCompoundString {
    
	private final HierarchicalCompoundString a_parts[];
	
	/**
	 * @param parts
	 */
	MultiHierarchicalCompoundString(final HierarchicalCompoundString parts[]) {
		a_parts = parts;
	}
	
	/**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
    	String s = "";
    	for (int i=0; i<a_parts.length; i++) {
    		if (i>0) s = s + '\n';
    		s = s + a_parts[i].toLongString();
    	}
        return s;
    }
    
    /**
     * @return parts
     */
    public HierarchicalCompoundString[] getParts() {
    	return a_parts;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object o) {
    	if ( this == o ) return true;
    	if ( !(o instanceof MultiHierarchicalCompoundString) ) return false;
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
}
