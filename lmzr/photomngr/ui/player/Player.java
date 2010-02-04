package lmzr.photomngr.ui.player;

import java.io.File;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent
 *
 */
public interface Player {


	/**
	 * @return name of the player
	 */
	String getName();
	
     /**
	 * @return executable file
	 */
	File getExecutable();
	
	/**
	 * @param format
	 * @return true is the format is supported, false otherwise
	 */
	boolean isFormatSupported(DataFormat format);
}
