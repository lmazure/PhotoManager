package lmzr.photomngr.data;

import java.util.EventListener;

public interface SaveListener extends EventListener {

    /**
     * @param e
     */
    public void saveChanged(final SaveEvent e);
}
