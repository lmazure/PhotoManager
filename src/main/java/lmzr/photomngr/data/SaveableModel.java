package lmzr.photomngr.data;

import java.io.IOException;

/**
 * @author Laurent
 */
public interface SaveableModel {

    /**
     * @throws IOException
     */
    void save() throws IOException;
}
