package lmzr.photomngr.data;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

/**
 *
 */
public class ListSelectionManager {

    final private PhotoList a_list;
    final private ListSelectionModel a_selection;

    /**
     * @param list
     * @param selection
     */
    public ListSelectionManager(final PhotoList list,
                                final ListSelectionModel selection) {
        this.a_list = list;
        this.a_selection = selection;
    }

    /**
     * @return list of selected indexes
     */
    public int[] getSelection() {

        if ( this.a_selection.getMinSelectionIndex() == -1 ) return new int[0];

        final Vector<Integer> v = new Vector<>();

        for (int i=this.a_selection.getMinSelectionIndex(); i<=this.a_selection.getMaxSelectionIndex(); i++) {
            if (this.a_selection.isSelectedIndex(i)) v.add(Integer.valueOf(i));
        }

        final int r[] = new int[v.size()];
        for (int i=0; i<v.size(); i++) r[i] = v.get(i).intValue();

        return r;
    }

    /**
     * @param selection
     */
    public void setSelection(final int selection[]) {
        if ( selection.length==0 ) {
            this.a_selection.clearSelection();
            return;
        }

        this.a_selection.setSelectionInterval(selection[0],selection[0]);

        for (int i=1; i<selection.length;i++ ) this.a_selection.addSelectionInterval(selection[i],selection[i]);
    }

    /**
     * @param incr
     */
    public void next(final int incr) {
        int i = this.a_selection.getMaxSelectionIndex() + incr;
        if (i>=this.a_list.getRowCount()) i-= this.a_list.getRowCount();
        this.a_selection.setSelectionInterval(i,i);
    }

    /**
     * @param incr
     */
    public void previous(final int incr ) {
        int i = this.a_selection.getMinSelectionIndex() - incr;
        if (i<0) i+= this.a_list.getRowCount();
        this.a_selection.setSelectionInterval(i,i);
   }

    /**
     * @param l
     */
    public void addListener(final ListSelectionListener l) {
        this.a_selection.addListSelectionListener(l);
    }

    /**
     * @param l
     */
    public void removeListener(final ListSelectionListener l) {
        this.a_selection.removeListSelectionListener(l);
    }
}
