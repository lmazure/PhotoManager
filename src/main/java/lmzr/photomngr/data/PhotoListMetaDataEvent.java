package lmzr.photomngr.data;

import java.util.EventObject;


/**
 *
 */
public class PhotoListMetaDataEvent extends EventObject {

    /**
     * 
     */
    static final public int FILTER_HAS_CHANGED = 1;

    final private int a_change;
    
    /**
     * @param src source of the change
     * @param change the metadata that has changed
     */
    public PhotoListMetaDataEvent(final Object src,
                                  final int change) {
        super(src);
        a_change = change;
    }
    
    /**
     * @return the metadata that has changed
     */
    public int getChange() {
        return a_change;
    }
}
