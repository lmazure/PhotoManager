package lmzr.photomngr.data.filter;

public class FilterBase {

	private boolean a_isEnabled;
	
	protected FilterBase(final boolean isEnabled) {
		a_isEnabled = isEnabled;		
	}
	
	public boolean isEnabled() {
		return a_isEnabled;
	}
	
	public void setEnabled(final boolean isEnabled) {
		a_isEnabled = isEnabled;
	}
}
