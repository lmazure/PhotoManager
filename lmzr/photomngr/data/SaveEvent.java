package lmzr.photomngr.data;

import java.util.EventObject;

/**
 * @author Laurent Mazuré
 */
public class SaveEvent extends EventObject {

	final private boolean a_isSaved;
	
    /**
     * @param src source of the change
     * @param isSaved indicate if the event correspond to the fact that the data is now saved or unsaved
     */
    public SaveEvent(final Object src,
                     final boolean isSaved) {
        super(src);
        a_isSaved = isSaved;
    }
    
    /**
     * @return indicate if the event correspond to the fact that the data is now saved or unsaved
     */
    public boolean isSaved() {
        return a_isSaved;
    }
}
