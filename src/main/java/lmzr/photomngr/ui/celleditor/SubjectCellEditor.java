package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;

import lmzr.photomngr.data.PhotoList;
import lmzr.photomngr.ui.treeSelectioner.TreeSelectioner;
import lmzr.util.string.HierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundString;
import lmzr.util.string.MultiHierarchicalCompoundStringFactory;

/**
 * @author Laurent Mazuré
 */
public class SubjectCellEditor extends JComponent
                               implements TableCellEditor {

    final private Vector<CellEditorListener> a_listenerList;
    final private JTextArea a_textfield;
    final private JButton a_button;
    private InternalSubjectCellEditor a_internal;
    final private PhotoList a_photoList;

    /**
     *
     */
    private class SubjectEditListener implements DocumentListener {

        final private JTextField a_edit;
        final private JButton a_propositions[];

        /**
         * @param edit
         * @param propositions
         */
        public SubjectEditListener(final JTextField edit,
                                   final JButton propositions[]) {
            a_edit = edit;
            a_propositions = propositions;
        }

        /**
         * @param e
         */
        @Override
        public void changedUpdate(final DocumentEvent e) {
            update();
        }

        /**
         * @param e
         */
        @Override
        public void insertUpdate(final DocumentEvent e) {
            update();
        }

        /**
         * @param e
         */
        @Override
        public void removeUpdate(final DocumentEvent e) {
            update();
        }

        /**
         */
        private void update() {
            if ( a_edit.getText().length()<2 ) {
                for (final JButton a_proposition : a_propositions) {
                    a_proposition.setText("");
                }
                return;
            }

            final String str = a_edit.getText();
            final HashMap<String,Integer> record = new HashMap<>();
            for (int i=0; i<a_photoList.getRowCount(); i++) {
                final MultiHierarchicalCompoundString subjets = (MultiHierarchicalCompoundString)a_photoList.getValueAt(i, PhotoList.PARAM_SUBJECT);
                final HierarchicalCompoundString s[] = subjets.getParts();
                for (final HierarchicalCompoundString element : s) {
                    final String ss = element.toLongString();
                    if ( ss.indexOf(str)>=0) {
                        final Integer inc = record.get(ss);
                        if ( inc == null ) {
                            record.put(ss, Integer.valueOf(0));
                        } else {
                            record.put(ss, Integer.valueOf(inc.intValue()+1));
                        }
                    }
                }
            }

            final Vector<Map.Entry<String, Integer>> list = new Vector<>(record.entrySet());
            Collections.sort(list, (entry, entry1) -> (entry.getValue().equals(entry1.getValue()) ? 0 : (entry.getValue().intValue() > entry1.getValue().intValue() ? -1 : 1)));

            for (int i=0; i<a_propositions.length; i++) {
                if ( i<list.size()) {
                    a_propositions[i].setText(list.get(i).getKey());
                } else {
                    a_propositions[i].setText("");
                }
            }
        }
    }

    private class InternalSubjectCellEditor extends JDialog {

        final private JTextArea a_text;
        final private JTextField a_edit;
        final private JButton a_propositions[] =  new JButton[10];
        final private TreeSelectioner a_tree;
        private String a_keptValue;

        InternalSubjectCellEditor(final MultiHierarchicalCompoundStringFactory factory,
                                  final Frame parent){

            super(parent,"Subject",true);

            final Container c = getContentPane();
            c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
            c.setMinimumSize(new Dimension(600,600));

            a_edit = new JTextField(80);
            c.add(a_edit);
            a_edit.setAlignmentX(0.f);
            a_edit.addActionListener(event -> transferPropositionButtonTextToTextfield(a_propositions[0]));

            for (int i=0; i<a_propositions.length; i++) {
                a_propositions[i] = new JButton();
                c.add(a_propositions[i]);
                a_propositions[i].setAlignmentX(0.f);
                a_propositions[i].addActionListener(
                        e -> transferPropositionButtonTextToTextfield((JButton)e.getSource()));

            }

            final SubjectEditListener listener = new SubjectEditListener(a_edit,a_propositions);
            a_edit.getDocument().addDocumentListener(listener);

            a_text = new JTextArea();
            a_text.setDocument(a_textfield.getDocument());
            c.add(a_text);
            a_text.setAlignmentX(0.f);

            a_tree = new TreeSelectioner("subject", factory.getHierarchicalCompoundStringFactory(), TreeSelectioner.MODE_MULTI_SELECTION_WITHOUT_SELECT_ALL_COLUMN);
            final JScrollPane j = new JScrollPane(a_tree);
            c.add(j);
            a_tree.getTreeTableModel().addTreeModelListener(
                    new TreeModelListener() {
                        @Override
                        public void treeNodesChanged(final TreeModelEvent e) {
                            final StringBuilder s = new StringBuilder();
                            final Set<HierarchicalCompoundString> selection = a_tree.getSelection();
                            boolean b = false;
                            for (final HierarchicalCompoundString h: selection ) {
                                if (b) {
                                    s.append("\n");
                                }
                                s.append(h.toLongString());
                                b = true;
                            }
                            a_text.setText(s.toString());
                        }

                        @Override
                        public void treeNodesInserted(final TreeModelEvent e) {
                            // do noting
                        }

                        @Override
                        public void treeNodesRemoved(final TreeModelEvent e) {
                            // do noting
                        }

                        @Override
                        public void treeStructureChanged(final TreeModelEvent e) {
                            // do noting
                        }});
            j.setAlignmentX(0.f);
            final JPanel buttonsPane = new JPanel(new GridLayout(1,2));
            c.add(buttonsPane);
            buttonsPane.setAlignmentX(0.f);
            final JButton bOk = new JButton("OK");
            final JButton bCancel = new JButton("Cancel");
            buttonsPane.add(bOk);
            buttonsPane.add(bCancel);
            getRootPane().setDefaultButton(bOk);
            bOk.addActionListener(e -> close());
            bCancel.addActionListener(e -> {
                a_text.setText(a_keptValue);
                close();
            });
            pack();
            setVisible(false);
        }

        /**
         * @param value
         */
        private void setText(final String value) {

            a_text.setText(value);

            if ( value.length()>0 ) {
                final MultiHierarchicalCompoundString v = a_photoList.getSubjectFactory().create(value);
                final Set<HierarchicalCompoundString> selection = new HashSet<>(Arrays.asList(v.getParts()));
                a_tree.setSelection(selection);
            } else {
                a_tree.setSelection(new HashSet<>());
            }

        }

        /**
         *
         */
        private void open(final String value) {

            a_keptValue = value;
            setText(value);

            a_edit.requestFocusInWindow();

            setVisible(true);
        }

        /**
         *
         */
        private void close() {
            setVisible(false);
            dispose();
        }

        /**
         * @param b button from which to retrieve the text to be transferred in the textfield
         */
        private void transferPropositionButtonTextToTextfield(final JButton b) {

            final String sSource = b.getText();
            if ( sSource.length()==0 ) {
                return;
            }

            final String sOldValue = a_text.getText();
            String sNewValue;
            if ( sOldValue.length()==0 ) {
                sNewValue = b.getText();
            } else {
                sNewValue = sOldValue + "\n" + sSource;
            }
            setText(sNewValue);
            a_edit.setText("");
            for (final JButton a_proposition : a_propositions) {
                a_proposition.setText("");
            }
            a_edit.requestFocusInWindow();
        }
    }

    /**
     * @param filteredList
     * @param parent
     */
    public SubjectCellEditor(final PhotoList filteredList,
                             final Frame parent) {

        a_photoList = filteredList;
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

        a_textfield = new JTextArea();
        add(a_textfield);

        a_button= new JButton("\u2193");
        add(a_button);
        a_button.addActionListener(
                e -> a_internal.open(a_textfield.getText()));

        a_internal = new InternalSubjectCellEditor(a_photoList.getSubjectFactory(), parent);

        a_listenerList = new Vector<>();
    }

    /**
     *
     */
    protected void fireEditingStopped() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = a_listenerList.size()-1; i>=0; i--) {
            a_listenerList.get(i).editingStopped(e);
        }
    }

    /**
     *
     */
    protected void fireEditingCanceled() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = a_listenerList.size()-1; i>=0; i--) {
            a_listenerList.get(i).editingCanceled(e);
        }
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
        return a_textfield.getText();
    }

    /**
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    @Override
    public boolean shouldSelectCell(final EventObject anEvent) {
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
        a_listenerList.add(l);
    }

    /**
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    @Override
    public void removeCellEditorListener(final CellEditorListener l) {
        a_listenerList.remove(l);
    }

    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    @Override
    public boolean isCellEditable(final EventObject event) {
        if (event == null) {
            // the cell is programmatically edited
            return true;
        }
        if ( event instanceof final MouseEvent e ) {
            if ( e.getModifiersEx()!=InputEvent.BUTTON1_DOWN_MASK ) {
                return false;
            }
            if ( e.getClickCount()!=2 ) {
                return false;
            }
            return true;
        } else if  ( event instanceof final KeyEvent e ) {
               if ( (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0 ) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param value
     */
    public void setText(final String value) {
        a_textfield.setText(value);
    }

    /**
     * @return current value of the cell
     */
    public String getText() {
        return a_textfield.getText();
    }

    /**
     * @param l
     */
    public void addTextFocusListener(final FocusListener l) {
        a_textfield.addFocusListener(l);
    }

    /**
     * @param l
     */
    public void removeTextFocusListener(final FocusListener l) {
        a_textfield.removeFocusListener(l);
    }
}
