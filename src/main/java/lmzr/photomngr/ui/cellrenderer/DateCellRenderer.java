package lmzr.photomngr.ui.cellrenderer;

import java.text.DateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 */
public class DateCellRenderer extends DefaultTableCellRenderer {

    final private static DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.MEDIUM);

    /**
     * 
     */
    public DateCellRenderer() {
        super();
    }

    /**
     * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
     */
    @Override
	public void setValue(final Object value) {
        Date date = (Date)value;
        if (date!=null) {
            super.setValue(format.format(date));
        } else {
        	super.setValue("");
        }
    }
} 
