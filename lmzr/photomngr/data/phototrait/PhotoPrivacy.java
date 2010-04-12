package lmzr.photomngr.data.phototrait;

/**
 * @author Laurent
 */
public class PhotoPrivacy extends PhotoTrait {

    static final private String[] g_encoding = new String[] {"anyone", "knowledgeable", "friends", "familly", "personal"};

    private PhotoPrivacy(final int value) {
        super(value);
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(final Object other) {
      if ( ! (other instanceof PhotoPrivacy) ) return false;
      return super.equals(other);
    }


    /**
     * @param str
     * @return PhotoQuality encoded by str
     */
    static public PhotoPrivacy parse(final String str) {
        return new PhotoPrivacy(parse(str,g_encoding));
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
        final PhotoPrivacy traits[] = new PhotoPrivacy[v.length];
        for (int i=0; i<v.length; i++) traits[i] = new PhotoPrivacy(v[i]);
        return traits;
    }

}
