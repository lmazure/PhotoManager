package lmzr.photomngr.data.phototrait;

/**
 * 
 */
public class PhotoTrait {

    final private int a_value;

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
                               final PhotoTraitEncoding encoding) {
        if ( str == null ) return Integer.MIN_VALUE;
        try {
            int i = Integer.parseInt(str);
            if ( i>=encoding.getMinValue() && i<=encoding.getMaxValue()) return i;
            return Integer.MIN_VALUE;
        } catch (final NumberFormatException e) {
            for (int i=0; i<=(encoding.getMaxValue()-encoding.getMinValue()); i++) {
                if ( str.compareToIgnoreCase(encoding.getNames()[i])==0 ) {
                    return (i+encoding.getMinValue()); 
                }
            }
            return Integer.MIN_VALUE;
        }        
    }

    /**
     * @param encoding
     * @return String encoding the PhotoTrait
     */
    protected String toString(final PhotoTraitEncoding encoding) {
        if ( a_value==Integer.MIN_VALUE ) return encoding.getUndefined();
        return encoding.getNames()[a_value-encoding.getMinValue()];
    }

    /**
     * @param encoding
     * @return traits
     */
    static protected int[] getValues(final PhotoTraitEncoding encoding) {
        int traits[] = new int[encoding.getMaxValue()-encoding.getMinValue()+2];
        for (int i=0; i<=(encoding.getMaxValue()-encoding.getMinValue()); i++) {
            traits[i] = i+encoding.getMinValue();
        }
        traits[encoding.getMaxValue()-encoding.getMinValue()+1] = Integer.MIN_VALUE;
        return traits;
    }
}
