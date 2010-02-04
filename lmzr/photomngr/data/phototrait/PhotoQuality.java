package lmzr.photomngr.data.phototrait;

/**
 * @author Laurent
 */
public class PhotoQuality extends PhotoTrait {

    static final private PhotoTraitEncoding  g_encoding = 
        new PhotoTraitEncoding(-3, 
                               3, 
                               new String[] {"awful", "very bad", "bad", "middle", "good", "very good", "excellent"},
                               "unclassified");

    private PhotoQuality(final int value) {
        super(value);
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(final Object other) {
      if ( ! (other instanceof PhotoQuality) ) return false;
      return super.equals(other);
    }


    /**
     * @param str
     * @return PhotoQuality encoded by str
     */
    static public PhotoQuality parse(final String str) {
        return new PhotoQuality(parse(str,g_encoding));
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
        final PhotoQuality traits[] = new PhotoQuality[v.length];
        for (int i=0; i<v.length; i++) traits[i] = new PhotoQuality(v[i]);
        return traits;
    }

}
