package lmzr.photomngr.data;

import java.util.EventListener;

/**
 * @author Laurent Mazuré
 */
public interface SaveListener extends EventListener {

    /**
     * @param e
     */
    void saveChanged(final SaveEvent e);
}
