package lmzr.photomngr.ui.player;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent
 *
 */
public class Player_QuickTime extends Player {

	/**
	 * 
	 */
	public Player_QuickTime() {
		super("QuickTime",
			  new String[] {"C:/Program Files (x86)/QuickTime/QuickTimePlayer.exe",
				            "C:/Program Files/QuickTime/QuickTimePlayer.exe"},
			  new DataFormat[] { DataFormat.JPEG,
					             DataFormat.MPEG,
					             DataFormat.WAV,
					             DataFormat.MP3,
					             DataFormat.AVI,
					             DataFormat.VOB,
					             DataFormat.GIF,
					             DataFormat.FLV,
					             DataFormat.MOV} );
	}

}
