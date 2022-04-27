package lmzr.photomngr.data.phototrait;

/**
 *
 */
public class PhotoTrait {

    final private int a_value;

    /**
     * integer value encoding an undefined trait
     */
    static final protected int UNDEFINED_TRAIT_VALUE = -1;

    /**
     * string value encoding an undefined trait
     */
    static final protected String UNDEFINED_TRAIT_STRING = "unclassified";


    /**
     * @param value
     */
    protected PhotoTrait(final int value) {
        a_value = value;
    }

    @Override
    public int hashCode() {
        return a_value;
    }

    /**
     * @return value
     */
    public int getValue() {
        return a_value;
    }

    /**
     * @param encoding
     * @return String encoding the PhotoTrait
     */
    protected String toString(final String[] encoding) {
        if ( a_value==UNDEFINED_TRAIT_VALUE ) return UNDEFINED_TRAIT_STRING;
        return encoding[a_value];
    }

}
