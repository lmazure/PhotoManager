package lmzr.photomngr.data.phototrait;

import java.util.HashMap;

/**
 * @author Laurent Mazuré
 */
public class PhotoPrivacy extends PhotoTrait {

    static final private String[] g_encoding = {"anyone", "knowledgeable", "friends", "familly", "personal"};
    static final private HashMap<String, PhotoPrivacy> g_pool = new HashMap<>();
    static final private PhotoPrivacy g_undefined = new PhotoPrivacy(UNDEFINED_TRAIT_VALUE);

    static {
        for (int i=0; i<g_encoding.length; i++) {
            final PhotoPrivacy p = new PhotoPrivacy(i);
            g_pool.put(g_encoding[i],p);
            g_pool.put(Integer.toString(i),p);
        }
        g_pool.put(UNDEFINED_TRAIT_STRING,g_undefined);
        g_pool.put(Integer.toString(UNDEFINED_TRAIT_VALUE),g_undefined);
    }

    private PhotoPrivacy(final int value) {
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
    static public PhotoPrivacy parse(final String str) {
        final PhotoPrivacy p = g_pool.get(str);
        if ( p == null ) {
            return g_undefined;
        }
        return p;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(g_encoding);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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
