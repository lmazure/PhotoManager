package lmzr.photomngr.data.phototrait;

import java.util.HashMap;

/**
 * @author Laurent
 */
public class PhotoQuality extends PhotoTrait {

    static final private String[] g_encoding = new String[] {"awful", "very bad", "bad", "middle", "good", "very good", "excellent"};
    static final private HashMap<String, PhotoQuality> g_pool = new HashMap<String, PhotoQuality>();
    static final private PhotoQuality g_undefined = new PhotoQuality(UNDEFINED_TRAIT_VALUE);
    
    static {
        for (int i=0; i<g_encoding.length; i++) {
        	final PhotoQuality p = new PhotoQuality(i);
        	g_pool.put(g_encoding[i],p);
        	g_pool.put(Integer.toString(i),p);        	
        }
        g_pool.put(UNDEFINED_TRAIT_STRING,g_undefined);
        g_pool.put(Integer.toString(UNDEFINED_TRAIT_VALUE),g_undefined);
    }

    private PhotoQuality(final int value) {
        super(value);
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(final Object other) {
    	return other == this;
    }


    /**
     * @param str
     * @return PhotoQuality encoded by str
     */
    static public PhotoQuality parse(final String str) {
    	final PhotoQuality p = g_pool.get(str);
    	if ( p == null ) {
    		return g_undefined;
    	} else {
    		return p;
    	}
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
        final PhotoTrait traits[] = new PhotoTrait[g_encoding.length+1];
        for (int i=0; i<g_encoding.length; i++) {
        	traits[i] = g_pool.get(Integer.toString(i));
        }
        traits[g_encoding.length] = g_undefined;
        return traits;
    }

}
