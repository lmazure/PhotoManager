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
        this.a_isEnabled = isEnabled;
    }

    /**
     * @return true if the filter is enable, false otherwise
     */
    public boolean isEnabled() {
        return this.a_isEnabled;
    }

    /**
     * @param isEnabled
     */
    public void setEnabled(final boolean isEnabled) {
        this.a_isEnabled = isEnabled;
    }
}
