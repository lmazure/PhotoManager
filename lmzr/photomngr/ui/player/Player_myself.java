package lmzr.photomngr.ui.player;

import lmzr.photomngr.data.DataFormat;

/**
 * @author Laurent Mazur�
 */
public class Player_myself extends Player {

	/**
	 * 
	 */
	public Player_myself() {
		super("PhotoManager",
			  new String[] {},
			  new DataFormat[] { DataFormat.JPEG,
					             DataFormat.GIF} );
	}

}
