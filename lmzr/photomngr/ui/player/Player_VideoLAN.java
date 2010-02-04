package lmzr.photomngr.ui.player;

import java.io.File;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent
 *
 */
public class Player_VideoLAN implements Player {

	/**
	 * @see lmzr.photomngr.ui.player.Player#getName()
	 */
	public String getName() {
		return "VideoLAN";
	}
	
	/**
	 * @see lmzr.photomngr.ui.player.Player#getExecutable()
	 */
	@Override
	public File getExecutable() {
		return new File("C:\\Program Files\\VideoLAN\\VLC\\vlc.exe");
	}

	/**
	 * @see lmzr.photomngr.ui.player.Player#isFormatSupported(lmzr.photomngr.data.DataFormat)
	 */
	@Override
	public boolean isFormatSupported(final DataFormat format) {
		if (format==DataFormat.MPEG) return true;
		if (format==DataFormat.WAV) return true;
		if (format==DataFormat.MP3) return true;
		if (format==DataFormat.AVI) return true;
		if (format==DataFormat.VOB) return true;
		if (format==DataFormat.GIF) return true;
		if (format==DataFormat.FLV) return true;
		if (format==DataFormat.MOV) return true;
		return false;
	}

}
