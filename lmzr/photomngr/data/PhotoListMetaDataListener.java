package lmzr.photomngr.data;

import java.util.EventListener;

/**
 * @author Laurent Mazur�
 */
public interface PhotoListMetaDataListener extends EventListener {

    /**
     * @param e
     */
    public void photoListMetaDataChanged(final PhotoListMetaDataEvent e);
}
