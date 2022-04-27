package lmzr.photomngr.ui;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.data.phototrait.PhotoOriginality;
import lmzr.photomngr.data.phototrait.PhotoPrivacy;
import lmzr.photomngr.data.phototrait.PhotoQuality;
import lmzr.photomngr.ui.celleditor.AuthorCellEditor;
import lmzr.photomngr.ui.celleditor.CopiesCellEditor;
import lmzr.photomngr.ui.celleditor.FocusCellEditor;
import lmzr.photomngr.ui.celleditor.LocationCellEditor;
import lmzr.photomngr.ui.celleditor.RotationCellEditor;
import lmzr.photomngr.ui.celleditor.PhotoTraitCellEditor;
import lmzr.photomngr.ui.celleditor.SubjectCellEditor;
import lmzr.photomngr.ui.celleditor.ZoomCellEditor;
import lmzr.photomngr.ui.cellrenderer.DateCellRenderer;
import lmzr.photomngr.ui.cellrenderer.LocationCellRenderer;
import lmzr.photomngr.ui.cellrenderer.SubjectCellRenderer;
import lmzr.util.ui.EnhancedJTable;

/**
 *
 */
public class PhotoListTable extends EnhancedJTable {

    private boolean a_setVisibility;

    /**
     * @param photoList
     * @param filteredPhotoList
     * @param parent
     */
    public PhotoListTable(final PhotoList photoList,
                          final PhotoList filteredPhotoList,
                          final Frame parent) {

        super(filteredPhotoList);

        getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setAutoResizeMode(AUTO_RESIZE_OFF);

        getColumnModel().getColumn(PhotoList.PARAM_SUBJECT).setCellRenderer(new SubjectCellRenderer());
        getColumnModel().getColumn(PhotoList.PARAM_LOCATION).setCellRenderer(new LocationCellRenderer());
        getColumnModel().getColumn(PhotoList.PARAM_DATE).setCellRenderer(new DateCellRenderer());

        getColumnModel().getColumn(PhotoList.PARAM_QUALITY).setCellEditor(new PhotoTraitCellEditor(PhotoQuality.getTraits()));
        getColumnModel().getColumn(PhotoList.PARAM_PRIVACY).setCellEditor(new PhotoTraitCellEditor(PhotoPrivacy.getTraits()));
        getColumnModel().getColumn(PhotoList.PARAM_ORIGINALITY).setCellEditor(new PhotoTraitCellEditor(PhotoOriginality.getTraits()));
        getColumnModel().getColumn(PhotoList.PARAM_SUBJECT).setCellEditor(new SubjectCellEditor(photoList,parent));
        getColumnModel().getColumn(PhotoList.PARAM_LOCATION).setCellEditor(new LocationCellEditor(filteredPhotoList.getLocationFactory(),parent));
        getColumnModel().getColumn(PhotoList.PARAM_COPIES).setCellEditor(new CopiesCellEditor());
        getColumnModel().getColumn(PhotoList.PARAM_ZOOM).setCellEditor(new ZoomCellEditor());
        getColumnModel().getColumn(PhotoList.PARAM_FOCUS_X).setCellEditor(new FocusCellEditor());
        getColumnModel().getColumn(PhotoList.PARAM_FOCUS_Y).setCellEditor(new FocusCellEditor());
        getColumnModel().getColumn(PhotoList.PARAM_ROTATION).setCellEditor(new RotationCellEditor());
        getColumnModel().getColumn(PhotoList.PARAM_AUTHOR).setCellEditor(new AuthorCellEditor(filteredPhotoList.getAuthorFactory()));

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
}
