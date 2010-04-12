package lmzr.photomngr.data.phototrait;

/**
 * 
 */
public class PhotoTrait {

    final private int a_value;
    
    /**
     * integer value encoding a undefined trait
     */
    static final private int UNDEFINED_TRAIT_VALUE = -1;
    
    /**
     * string value encoding a undefined trait
     */
    static final private String UNDEFINED_TRAID_STRING = "unclassified";

    
    /**
     * @param value
     */
    protected PhotoTrait(final int value) {
        a_value = value;
    }
    
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object other) {
      if ( ! (other instanceof PhotoTrait) ) return false;
      return (((PhotoTrait)other).a_value == a_value);
    }
    
    
    /**
     * @return value
     */
    public int getValue() {
        return a_value;
    }
    
    
    /**
     * @param str
     * @param encoding
     * @return PhotoTrait encoded by str
     */
    protected static int parse(final String str,
                               final String[] encoding) {
        if ( str == null ) return UNDEFINED_TRAIT_VALUE;
        try {
            final int i = Integer.parseInt(str);
            if ( i<encoding.length ) return i;
            return UNDEFINED_TRAIT_VALUE;
        } catch (final NumberFormatException e) {
            for (int i=0; i<encoding.length; i++) {
                if ( str.compareToIgnoreCase(encoding[i])==0 ) return i; 
            }
            return UNDEFINED_TRAIT_VALUE;
        }        
    }

    
    /**
     * @param encoding
     * @return String encoding the PhotoTrait
     */
    protected String toString(final String[] encoding) {
        if ( a_value==UNDEFINED_TRAIT_VALUE ) return UNDEFINED_TRAID_STRING;
        return encoding[a_value];
    }

    
    /**
     * @param encoding
     * @return traits
     */
    static protected int[] getValues(final String[] encoding) {
        final int traits[] = new int[encoding.length+1];
        for (int i=0; i<encoding.length; i++) traits[i] = i;
        traits[encoding.length] = UNDEFINED_TRAIT_VALUE;
        return traits;
    }
}
