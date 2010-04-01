package lmzr.photomngr.ui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellRenderer;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.ui.celleditor.AuthorCellEditor;
import lmzr.photomngr.ui.celleditor.CopiesCellEditor;
import lmzr.photomngr.ui.celleditor.FocusCellEditor;
import lmzr.photomngr.ui.celleditor.LocationCellEditor;
import lmzr.photomngr.ui.celleditor.PhotoTraitCellEditor;
import lmzr.photomngr.ui.celleditor.SubjectCellEditor;
import lmzr.photomngr.ui.celleditor.ZoomCellEditor;
import lmzr.photomngr.ui.cellrenderer.DateCellRenderer;
import lmzr.photomngr.ui.cellrenderer.LocationCellRenderer;
import lmzr.photomngr.ui.cellrenderer.SubjectCellRenderer;

/**
 * 
 */
public class PhotoListTable extends JTable {

    private boolean a_setVisibility;
    
    /**
     * @param unfilteredList
     * @param filteredList
     */
    public PhotoListTable(final PhotoList unfilteredList,
    		              final PhotoList filteredList) {
        
        super(filteredList);
        
        getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        setAutoResizeMode(AUTO_RESIZE_OFF);
		        
		getColumnModel().getColumn(PhotoList.PARAM_SUBJECT).setCellRenderer(new SubjectCellRenderer());
		getColumnModel().getColumn(PhotoList.PARAM_LOCATION).setCellRenderer(new LocationCellRenderer());
		getColumnModel().getColumn(PhotoList.PARAM_DATE).setCellRenderer(new DateCellRenderer());

		getColumnModel().getColumn(PhotoList.PARAM_QUALITY).setCellEditor(new PhotoTraitCellEditor(PhotoQuality.getTraits()));
		getColumnModel().getColumn(PhotoList.PARAM_PRIVACY).setCellEditor(new PhotoTraitCellEditor(PhotoPrivacy.getTraits()));
		getColumnModel().getColumn(PhotoList.PARAM_ORIGINALITY).setCellEditor(new PhotoTraitCellEditor(PhotoOriginality.getTraits()));
		getColumnModel().getColumn(PhotoList.PARAM_SUBJECT).setCellEditor(new SubjectCellEditor(unfilteredList,filteredList));
		getColumnModel().getColumn(PhotoList.PARAM_LOCATION).setCellEditor(new LocationCellEditor(filteredList.getLocationFactory()));
		getColumnModel().getColumn(PhotoList.PARAM_COPIES).setCellEditor(new CopiesCellEditor());
		getColumnModel().getColumn(PhotoList.PARAM_ZOOM).setCellEditor(new ZoomCellEditor());
		getColumnModel().getColumn(PhotoList.PARAM_FOCUS_X).setCellEditor(new FocusCellEditor());
		getColumnModel().getColumn(PhotoList.PARAM_FOCUS_Y).setCellEditor(new FocusCellEditor());
		getColumnModel().getColumn(PhotoList.PARAM_AUTHOR).setCellEditor(new AuthorCellEditor(filteredList.getAuthorFactory()));

		a_setVisibility = true;
    }

    /**
     * @see java.awt.Component#processEvent(java.awt.AWTEvent)
     */
    @Override
	protected void processEvent(final AWTEvent e) {
        a_setVisibility = false;
        super.processEvent(e); 
        a_setVisibility = true;
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
	public void valueChanged(final ListSelectionEvent e) {
        
        super.valueChanged(e);
        repaint(new Rectangle());
        
        if ( !a_setVisibility ) {
            // the user is manipulating the table itself -> we do not override its scrolling
            return;
        }

        if (e.getValueIsAdjusting()) {
            // event compression -> only the last one is taken into account
            return;
        }

        final int row = getSelectionModel().getMinSelectionIndex();
        if ( row == -1 ) return;
        if (!isCellVisible(row,0)) scrollToVisible(row,0);
    }

    /**
     * see http://javaalmanac.com/egs/javax.swing.table/IsVis.html
     * @param rowIndex
     * @param vColIndex
     * @return boolean indicating if the cell is visible
     */
    private boolean isCellVisible(final int rowIndex,
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
    private void scrollToVisible(final int rowIndex,
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
        // TODO 5 le tooltip devrait aussi s'afficher quand la dernière colonne est partiellement cachée
        final int row = rowAtPoint(event.getPoint());
        final int col = columnAtPoint(event.getPoint());
        if ( (row == -1) || (col == -1) ) return null;

        final Object obj = getValueAt(row,col);
        final TableCellRenderer tcr = getDefaultRenderer(getModel().getColumnClass(convertColumnIndexToModel(col)));
        final Component c = tcr.getTableCellRendererComponent(this,obj,false,false,row,col);
        if (c.getPreferredSize().width < getCellRect(row, col, true).width) return null;
        if (c instanceof JLabel) return ((JLabel)c).getText();
        return obj.toString();
    }
}
