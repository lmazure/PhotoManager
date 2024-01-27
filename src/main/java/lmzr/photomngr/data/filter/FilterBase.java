package lmzr.photomngr.data.filter;

/**
 * @author Laurent Mazuré
 */
public class FilterBase {

    private boolean a_isEnabled;

    /**
     * @param isEnabled
     */
    protected FilterBase(final boolean isEnabled) {
        a_isEnabled = isEnabled;
    }

    /**
     * @return true if the filter is enabled, false otherwise
     */
    public boolean isEnabled() {
        return a_isEnabled;
    }

    /**
     * @param isEnabled
     */
    public void setEnabled(final boolean isEnabled) {
        a_isEnabled = isEnabled;
    }
}
