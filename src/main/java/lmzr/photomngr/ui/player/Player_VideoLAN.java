package lmzr.photomngr.ui.player;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent Mazuré
 */
public class Player_VideoLAN extends Player {

	/**
	 * 
	 */
	public Player_VideoLAN() {
		super("VideoLAN",
			  new String[] {"C:/Program Files (x86)/VideoLAN/VLC/vlc.exe",
				            "C:/Program Files/VideoLAN/VLC/vlc.exe"},
		  	  new DataFormat[] { DataFormat.MPEG,
                                 DataFormat.WAV,
                                 DataFormat.MP3,
                                 DataFormat.AVI,
                                 DataFormat.VOB,
                                 DataFormat.GIF,
                                 DataFormat.FLV,
                                 DataFormat.MOV,
                                 DataFormat.WMV,
                                 DataFormat.MTS,
					             DataFormat.MP4} );
	}

}
