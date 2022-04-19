package lmzr.util.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * @author Laurent
 *
 */
public class EnhancedJTable extends JTable {

    /**
     * @param model
     */
    public EnhancedJTable(final TableModel model) {
        
        super(model);
        
    }

    /**
     * see http://javaalmanac.com/egs/javax.swing.table/IsVis.html
     * @param rowIndex
     * @param vColIndex
     * @return boolean indicating if the cell is visible
     */
    protected boolean isCellVisible(final int rowIndex,
                                    final int vColIndex) {
        final JViewport viewport = (JViewport)getParent();
        if (viewport == null) {
            // we are in the creation of the table, it has not been attached to a JScrollPane yet
            return true;
        }
    
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0)
        final Rectangle rect = getCellRect(rowIndex, vColIndex, true);
    
        // The location of the viewport relative to the table
        final Point pt = viewport.getViewPosition();
    
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);
    
        // Check if view completely contains cell
        return new Rectangle(viewport.getExtentSize()).contains(rect);
    }
    
    /**
     * see http://javaalmanac.com/egs/javax.swing.table/Vis.html
     * @param rowIndex
     * @param vColIndex
     */
    protected void scrollToVisible(final int rowIndex,
                                   final int vColIndex) {
    	
        final JViewport viewport = (JViewport)getParent();
           
        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        //final Rectangle rect1 = getVisibleRect();
        //System.err.println("rect1 = "+rect1.width+"x"+rect1.height+" "+rect1.x+" "+rect1.y);
        final Rectangle rect = getCellRect(rowIndex, vColIndex, true);
        //System.err.println("rect = "+rect.width+"x"+rect.height+" "+rect.x+" "+rect.y);
    
        // The location of the viewport relative to the table
        final Point pt = viewport.getViewPosition();
        //System.err.println("pt = "+pt.x+" "+pt.y);
    
        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x-pt.x, rect.y-pt.y);
    
        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }

    /**
     * This method is overwritten to display a tooltip in order to show the cell's content
     * when this one is larger than the cell's size
     * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
     */
    @Override
	public String getToolTipText(final MouseEvent event)
    {
        // TODO le tooltip devrait aussi s'afficher quand la derni�re colonne est partiellement cach�e
        final int row = rowAtPoint(event.getPoint());
        final int col = columnAtPoint(event.getPoint());
        if ( (row == -1) || (col == -1) ) return null;

        final Object obj = getValueAt(row,col);
        final TableCellRenderer tcr = getDefaultRenderer(getModel().getColumnClass(convertColumnIndexToModel(col)));
        final Component c = tcr.getTableCellRendererComponent(this,obj,false,false,row,col);
        if (c.getPreferredSize().width < getCellRect(row, col, true).width) return null;
        
        final String tooltipText = (c instanceof JLabel) ? ((JLabel)c).getText() : obj.toString();
        
        return "<html>" + tooltipText.replace("\n", "<br>") + "</html>";
    }

}
