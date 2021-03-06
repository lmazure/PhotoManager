package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
            this.a_edit = edit;
            this.a_propositions = propositions;
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
            if ( this.a_edit.getText().length()<2 ) {
                for (int i=0; i<this.a_propositions.length; i++) this.a_propositions[i].setText("");
                return;
            }


            final String str = this.a_edit.getText();
            final HashMap<String,Integer> record = new HashMap<>();
            for (int i=0; i<SubjectCellEditor.this.a_photoList.getRowCount(); i++) {
                final MultiHierarchicalCompoundString subjets = (MultiHierarchicalCompoundString)SubjectCellEditor.this.a_photoList.getValueAt(i, PhotoList.PARAM_SUBJECT);
                final HierarchicalCompoundString s[] = subjets.getParts();
                for ( int j=0; j<s.length;  j++) {
                    final String ss = s[j].toLongString();
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
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>(){
                @Override
                public int compare(Map.Entry<String, Integer> entry, Map.Entry<String, Integer> entry1)
                {
                    return (entry.getValue().equals(entry1.getValue()) ? 0 : (entry.getValue() > entry1.getValue() ? -1 : 1));
                }
            });

            for (int i=0; i<this.a_propositions.length; i++) {
                if ( i<list.size()) {
                    this.a_propositions[i].setText(list.get(i).getKey());
                } else {
                    this.a_propositions[i].setText("");
                }
            }
        }
    }

    private class InternalSubjectCellEditor extends JDialog {

        final private JTextArea a_text;
        final private JTextField a_edit;
        final private JButton a_propositions[];
        final private TreeSelectioner a_tree;
        private String a_keptValue;

        InternalSubjectCellEditor(final MultiHierarchicalCompoundStringFactory factory,
                                  final Frame parent){

            super(parent,"Subject",true);

            final Container c = getContentPane();
            c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
            c.setMinimumSize(new Dimension(600,600));

            this.a_edit = new JTextField(80);
            c.add(this.a_edit);
            this.a_edit.setAlignmentX(0.f);
            this.a_edit.addActionListener(new ActionListener() {
                // ceci est un copier/coller de ci-dessous
                @Override
                public void actionPerformed(ActionEvent event) {
                    transferPropositionButtonTextToTextfield(InternalSubjectCellEditor.this.a_propositions[0]);
                }
            });

            this.a_propositions =  new JButton[10];
            for (int i=0; i<this.a_propositions.length; i++) {
                this.a_propositions[i] = new JButton();
                c.add(this.a_propositions[i]);
                this.a_propositions[i].setAlignmentX(0.f);
                this.a_propositions[i].addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                transferPropositionButtonTextToTextfield((JButton)e.getSource());
                            }});

            }

            final SubjectEditListener listener = new SubjectEditListener(this.a_edit,this.a_propositions);
            this.a_edit.getDocument().addDocumentListener(listener);

            this.a_text = new JTextArea();
            this.a_text.setDocument(SubjectCellEditor.this.a_textfield.getDocument());
            c.add(this.a_text);
            this.a_text.setAlignmentX(0.f);

            this.a_tree = new TreeSelectioner("subject", factory.getHierarchicalCompoundStringFactory(), TreeSelectioner.MODE_MULTI_SELECTION_WITHOUT_SELECT_ALL_COLUMN);
            final JScrollPane j = new JScrollPane(this.a_tree);
            c.add(j);
            this.a_tree.getTreeTableModel().addTreeModelListener(
                    new TreeModelListener() {
                        @Override
                        public void treeNodesChanged(final TreeModelEvent e) {
                            final StringBuilder s = new StringBuilder();
                            final Set<HierarchicalCompoundString> selection = InternalSubjectCellEditor.this.a_tree.getSelection();
                            boolean b = false;
                            for (HierarchicalCompoundString h: selection ) {
                                if (b) {
                                    s.append("\n");
                                }
                                s.append(h.toLongString());
                                b = true;
                            }
                            InternalSubjectCellEditor.this.a_text.setText(s.toString());
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
            bOk.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        close();
                    }
            });
            bCancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            InternalSubjectCellEditor.this.a_text.setText(InternalSubjectCellEditor.this.a_keptValue);
                            close();
                        }
            });
            pack();
            setVisible(false);
        }

        /**
         * @param value
         */
        private void setText(final String value) {

            this.a_text.setText(value);

            if ( value.length()>0 ) {
                final MultiHierarchicalCompoundString v = SubjectCellEditor.this.a_photoList.getSubjectFactory().create(value);
                final Set<HierarchicalCompoundString> selection = new HashSet<>(Arrays.asList(v.getParts()));
                this.a_tree.setSelection(selection);
            } else {
                this.a_tree.setSelection(new HashSet<HierarchicalCompoundString>());
            }

        }

        /**
         *
         */
        private void open(final String value) {

            this.a_keptValue = value;
            setText(value);

            this.a_edit.requestFocusInWindow();

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
            if ( sSource.length()==0 ) return;

            final String sOldValue = this.a_text.getText();
            String sNewValue;
            if ( sOldValue.length()==0 ) {
                sNewValue = b.getText();
            } else {
                sNewValue = sOldValue + "\n" + sSource;
            }
            setText(sNewValue);
            this.a_edit.setText("");
            for (int j=0; j<this.a_propositions.length; j++) this.a_propositions[j].setText("");
            this.a_edit.requestFocusInWindow();
        }
    }


    /**
     * @param filteredList
     * @param parent
     */
    public SubjectCellEditor(final PhotoList filteredList,
                             final Frame parent) {

        super();

        this.a_photoList = filteredList;
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

        this.a_textfield = new JTextArea();
        add(this.a_textfield);

        this.a_button= new JButton("\u2193");
        add(this.a_button);
        this.a_button.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SubjectCellEditor.this.a_internal.open(SubjectCellEditor.this.a_textfield.getText());}});

        this.a_internal = new InternalSubjectCellEditor(this.a_photoList.getSubjectFactory(), parent);

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
