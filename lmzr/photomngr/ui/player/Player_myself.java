package lmzr.photomngr.ui.player;

import java.io.File;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent
 *
 */
public class Player_myself implements Player {

	/**
	 * @see lmzr.photomngr.ui.player.Player#getName()
	 */
	public String getName() {
		return "PhotoManager";
	}
	
	/**
	 * @see lmzr.photomngr.ui.player.Player#getExecutable()
	 */
	@Override
	public File getExecutable() {
		return null;
	}

	/**
	 * @see lmzr.photomngr.ui.player.Player#isFormatSupported(lmzr.photomngr.data.DataFormat)
	 */
	@Override
	public boolean isFormatSupported(DataFormat format) {
		if (format==DataFormat.JPEG) return true;
		if (format==DataFormat.MPEG) return true;
		if (format==DataFormat.WAV) return true;
		if (format==DataFormat.MP3) return true;
		if (format==DataFormat.AVI) return true;
		if (format==DataFormat.GIF) return true;
		return true;
	}

}
