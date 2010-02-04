package lmzr.photomngr.data.phototrait;

/**
 * @author Laurent
 */
public class PhotoOriginality extends PhotoTrait {

    static final private PhotoTraitEncoding  g_encoding = 
        new PhotoTraitEncoding(-2, 
                               2, 
                               new String[] {"overused", "too common", "common", "rare", "exceptional"},
                               "unclassified");

    private PhotoOriginality(final int value) {
        super(value);
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(final Object other) {
      if ( ! (other instanceof PhotoOriginality) ) return false;
      return super.equals(other);
    }

    /**
     * @param str
     * @return PhotoQuality encoded by str
     */
    static public PhotoOriginality parse(final String str) {
        return new PhotoOriginality(parse(str,g_encoding));
    }
    
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return toString(g_encoding);
    }

    /**
     * @return traits
     */
    static public PhotoTrait[] getTraits() {
        final int v[] = getValues(g_encoding);
        final PhotoOriginality traits[] = new PhotoOriginality[v.length];
        for (int i=0; i<v.length; i++) traits[i] = new PhotoOriginality(v[i]);
        return traits;
    }
}
