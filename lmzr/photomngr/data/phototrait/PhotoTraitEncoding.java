/*
 * Created on 13 mars 2005
 *
 */
package lmzr.photomngr.data.phototrait;

/**
 * @author Laurent
 */
public class PhotoTraitEncoding {

    final private int a_minValue; 
    final private int a_maxValue; 
    final private String a_names[];
    final private String a_undefined;

    /**
     * @param minValue
     * @param maxValue
     * @param names
     * @param undefined
     */
    PhotoTraitEncoding(final int minValue,
                       final int maxValue,
                       final String[] names,
                       final String undefined) {
        a_minValue = minValue;
        a_maxValue = maxValue;
        a_names = names;
        a_undefined = undefined;
    }

    /**
     * @return returns the max value
     */
    public int getMaxValue() {
        return a_maxValue;
    }
    /**
     * @return returns the min value
     */
    public int getMinValue() {
        return a_minValue;
    }
    /**
     * @return returns the names of the value
     */
    public String[] getNames() {
        return a_names;
    }
    /**
     * @return returns the name of the undefined value
     */
    public String getUndefined() {
        return a_undefined;
    }

}
