package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;

import lmzr.photomngr.ui.treeSelectioner.DatabaseForTreeSelectioner;
import lmzr.photomngr.ui.treeSelectioner.TreeSelectioner;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.HierarchicalCompoundStringFactory;

/**
 *
 */
public class LocationCellEditor extends JComponent
                                implements TableCellEditor {

    final private Vector<CellEditorListener> a_listenerList;
    final private JTextField a_textfield;
    final private JButton a_button;
    private InternalLocationCellEditor a_internal;
    final private HierarchicalCompoundStringFactory a_factory;
    private String a_keptValue;

    private class InternalLocationCellEditor extends JDialog {

        final private JTextField a_text;
        final private TreeSelectioner a_tree;

        InternalLocationCellEditor(final HierarchicalCompoundStringFactory factory,
                                   final Frame parent){

            super(parent,"location",true);

            final Container c = getContentPane();
            setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));

            this.a_text = new JTextField(60);
            this.a_text.setDocument(LocationCellEditor.this.a_textfield.getDocument());
            c.add(this.a_text);

            this.a_tree = new TreeSelectioner("location", factory, TreeSelectioner.MODE_MONO_SELECTION);
            c.add(new JScrollPane(this.a_tree));
            this.a_tree.getTreeTableModel().addTreeModelListener(
                    new TreeModelListener() {
                        @Override
                        public void treeNodesChanged(final TreeModelEvent e) {
                            final HierarchicalCompoundString string = (HierarchicalCompoundString)e.getChildren()[0];
                            final Boolean value = (Boolean)InternalLocationCellEditor.this.a_tree.getTreeTableModel().getValueAt(string, DatabaseForTreeSelectioner.PARAM_SELECTED);
                            if ( value.booleanValue() ) {
                                InternalLocationCellEditor.this.a_text.setText(string.toLongString());
                            }
                        }

                        @Override
                        public void treeNodesInserted(final TreeModelEvent e) {
                        	// do nothing
                        }

                        @Override
                        public void treeNodesRemoved(final TreeModelEvent e) {
                        	// do nothing
                        }

                        @Override
                        public void treeStructureChanged(final TreeModelEvent e) {
                        	// do nothing
                        }});

            final JPanel buttonsPane = new JPanel(new GridLayout(1,2));
            c.add(buttonsPane);
            final JButton bOk = new JButton("OK");
            final JButton bCancel = new JButton("Cancel");
            buttonsPane.add(bOk);
            buttonsPane.add(bCancel);
            getRootPane().setDefaultButton(bOk);
            bOk.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        close();
                    }
            });
            bCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            InternalLocationCellEditor.this.a_text.setText(LocationCellEditor.this.a_keptValue);
                            close();
                        }
            });
            pack();
            setVisible(false);
        }

        private void open(final String value) {

            LocationCellEditor.this.a_keptValue = value;
            this.a_text.setText(value);

            if ( !value.equals("") ) {
                final HierarchicalCompoundString v = LocationCellEditor.this.a_factory.create(value);
                final Set<HierarchicalCompoundString> selection = new HashSet<>();
                selection.add(v);
                this.a_tree.setSelection(selection);
            } else {
                this.a_tree.setSelection(new HashSet<HierarchicalCompoundString>());
            }

            setVisible(true);
        }

        /**
         *
         */
        private void close() {
            setVisible(false);
            dispose();
        }

    }

    /**
     * @param factory
     * @param parent
    *
     */
    public LocationCellEditor(final HierarchicalCompoundStringFactory factory,
                              final Frame parent) {

        super();

        this.a_factory = factory;
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

        this.a_textfield = new JTextField();
        add(this.a_textfield);

        this.a_button= new JButton("\u2193");
        add(this.a_button);
        this.a_button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        LocationCellEditor.this.a_internal.open(LocationCellEditor.this.a_textfield.getText());}});

        this.a_internal = new InternalLocationCellEditor(this.a_factory, parent);

        this.a_listenerList = new Vector<>();
    }

    /**
     *
     */
    protected void fireEditingStopped() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = this.a_listenerList.size()-1; i>=0; i--) this.a_listenerList.get(i).editingStopped(e);
    }

    /**
     *
     */
    protected void fireEditingCanceled() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = this.a_listenerList.size()-1; i>=0; i--) this.a_listenerList.get(i).editingCanceled(e);
    }

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    @Override
    public Component getTableCellEditorComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final int row,
                                                 final int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setText(value.toString());
        final int preferredheight = getPreferredSize().height;
        if ( table.getRowHeight(row) < preferredheight ) {
            table.setRowHeight(row,preferredheight);
        }

        return this;
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
    public Object getCellEditorValue() {
        return this.a_textfield.getText();
    }

    /**
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    @Override
    public boolean shouldSelectCell(final EventObject e) {
        return true;
    }

    /**
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    @Override
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    /**
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    @Override
    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    /**
     * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
     */
    @Override
    public void addCellEditorListener(final CellEditorListener l) {
        this.a_listenerList.add(l);
    }

    /**
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    @Override
    public void removeCellEditorListener(final CellEditorListener l) {
        this.a_listenerList.remove(l);
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    @Override
    public boolean isCellEditable(final EventObject event) {
        if (event == null) {
            // the cell is programmatically edited
            return true;
        } else if ( event instanceof MouseEvent ) {
            final MouseEvent e = (MouseEvent)event;
            if ( e.getModifiersEx()!=InputEvent.BUTTON1_DOWN_MASK ) return false;
            if ( e.getClickCount()!=2 ) return false;
            return true;
        } else if  ( event instanceof KeyEvent ) {
            final KeyEvent e = (KeyEvent)event;
            if ( (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0 ) return false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param value
     */
    public void setText(final String value) {
        this.a_textfield.setText(value);
    }

    /**
     * @return current value of the cell
     */
    public String getText() {
        return this.a_textfield.getText();
    }

    /**
     * @param l
     */
    public void addTextFocusListener(final FocusListener l) {
        this.a_textfield.addFocusListener(l);
    }

    /**
     * @param l
     */
    public void removeTextFocusListener(final FocusListener l) {
        this.a_textfield.removeFocusListener(l);
    }
}
