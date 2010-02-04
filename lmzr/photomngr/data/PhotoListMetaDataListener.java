/*
 * Created on 17 mai 2005 by Laurent Mazuré
 */

package lmzr.photomngr.data;

import java.util.EventListener;

/**
 * @author Laurent Mazuré
 */
public interface PhotoListMetaDataListener extends EventListener {

    /**
     * @param e
     */
    public void photoListMetaDataChanged(final PhotoListMetaDataEvent e);
}
