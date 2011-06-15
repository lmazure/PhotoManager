package lmzr.photomngr.data;

import java.io.IOException;

/**
 * @author Laurent Mazuré
 */
public interface SaveableModel {

	/**
	 * @throws IOException
	 */
	public void save() throws IOException;
}
