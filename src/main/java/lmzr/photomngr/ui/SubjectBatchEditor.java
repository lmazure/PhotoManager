package lmzr.photomngr.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import lmzr.util.string.HierarchicalCompoundString;

/**
 * @author Laurent Mazuré
 */
public class SubjectBatchEditor extends JDialog {

    /**
     *
     */
    private class Row {

        private JCheckBox a_edit;
        private JTextField a_initial;
        private JButton a_propagate;
        private JTextField a_edited;
        private int a_position;

        Row(final JCheckBox edit,
            final JTextField initial,
            final JButton propagate,
            final JTextField edited) {

            this.a_edit = edit;
            this.a_edit.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            if ( edit.isSelected()) {
                                edited.setEditable(true);
                                setEditedText(initial.getText());
                            } else {
                                edited.setEditable(false);
                                setEditedText("");
                            }
                            }});

            this.a_initial = initial;

            this.a_propagate = propagate;
            this.a_propagate.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            int i = Row.this.a_position;
                            while ( i<SubjectBatchEditor.this.a_rows.size() && SubjectBatchEditor.this.a_rows.get(i).getInitialText().startsWith(Row.this.a_initial.getText()) ) {
                                final String initialText = SubjectBatchEditor.this.a_rows.get(i).getInitialText();
                                final String editedText = Row.this.a_edited.getText() + initialText.substring(Row.this.a_initial.getText().length());
                                SubjectBatchEditor.this.a_rows.get(i).setEditedText(editedText);
                                SubjectBatchEditor.this.a_rows.get(i).setEdited(true);
                                i++;
                            }
                        }});

            this.a_edited = edited;

            this.a_position = SubjectBatchEditor.this.a_rows.size();
        }

        String getInitialText() {
            return this.a_initial.getText();
        }

        String getEditedText() {
            return this.a_edited.getText();
        }

        private void setEditedText(final String string) {
            this.a_edited.setText(string);
        }

        private void setEdited(final boolean value) {
            this.a_edit.setSelected(value);
            this.a_edited.setEditable(value);
        }

        boolean isEdited() {
            return this.a_edit.isSelected();
        }
    }

    final private Vector<Row> a_rows;

    /**
     * @param frame
     * @param root
     * @param performer
     */
    public SubjectBatchEditor(final JFrame frame,
                              final HierarchicalCompoundString root,
                              final MapTranslationPerformer performer) {

        super(frame,true);

        this.a_rows = new Vector<>();

        final Container pane = getContentPane();

        final JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());

        buildContent(p,root);

        p.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pane.add(new JScrollPane(p),BorderLayout.CENTER);

        final JPanel buttonsPane = new JPanel(new GridLayout(1,2));
        add(buttonsPane,BorderLayout.SOUTH);
        final JButton bOk = new JButton("OK");
        final JButton bCancel = new JButton("Cancel");
        buttonsPane.add(bOk);
        buttonsPane.add(bCancel);
        getRootPane().setDefaultButton(bCancel);
        bOk.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    performer.performMapTranslation(getTranslationMap());
                    close();
                }
        });
        bCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        close();
                    }
        });
        pack();
        setSize(1000,700);
    }


    /**
     *
     */
    private void close() {
        setVisible(false);
        dispose();
    }


    /**
     * @param panel
     * @param string
     */
    private void buildContent(final JPanel panel,
                              final HierarchicalCompoundString string) {

        GridBagConstraints c = new GridBagConstraints();

        final int rowNumber = this.a_rows.size();
        c.gridy = rowNumber;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;

        final JCheckBox edit = new JCheckBox();
        c.gridx = 0;
        c.weightx = 0.0;
        panel.add(edit,c);

        final JTextField initial = new JTextField();
        initial.setEditable(false);
        initial.setText(string.toLongString());
        c.gridx = 1;
        c.weightx = 0.0;
        panel.add(initial,c);

        final JButton propagate = new JButton("propagate");
        c.gridx = 2;
        c.weightx = 0.0;
        panel.add(propagate,c);

        final JTextField edited = new JTextField();
        edited.setEditable(false);
        c.gridx = 3;
        c.weightx = 1.0;
        panel.add(edited,c);

        this.a_rows.add(new Row(edit, initial, propagate, edited));

        for (HierarchicalCompoundString s: string.getChildren()) buildContent(panel, s);
    }

    private Map<String, String> getTranslationMap() {
        final Map<String,String> map = new HashMap<>();
        for (Row r : this.a_rows) {
            if (r.isEdited()) {
                map.put(r.getInitialText(),r.getEditedText());
            }
        }
        return map;
    }
}
