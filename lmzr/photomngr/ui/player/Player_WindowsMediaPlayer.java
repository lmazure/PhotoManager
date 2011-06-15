package lmzr.photomngr.ui.player;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent Mazuré
 */
public class Player_WindowsMediaPlayer extends Player {
	
	/**
	 * 
	 */
	public Player_WindowsMediaPlayer() {
		super("Windows Media Player",
			  new String[] {"C:/Program Files/Windows Media Player/wmplayer.exe",
		                    "C:/Program Files (x86)/Windows Media Player/wmplayer.exe"},
		      new DataFormat[] { DataFormat.JPEG,
                                 DataFormat.MPEG,
                                 DataFormat.WAV,
                                 DataFormat.MP3,
                                 DataFormat.AVI,
                                 DataFormat.GIF,
                                 DataFormat.MOV,
                                 DataFormat.WMV} );
	}

}
