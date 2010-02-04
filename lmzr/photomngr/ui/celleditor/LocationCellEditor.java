package lmzr.photomngr.ui.celleditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lmzr.photomngr.ui.HierarchicalCompoundStringTreeDisplay;
import lmzr.util.checktree.CheckTreeManager;
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
    
    private class InternalLocationCellEditor extends JDialog {

    	final private JTextField a_text;
        final private CheckTreeManager a_location;
    	
    	InternalLocationCellEditor(final HierarchicalCompoundStringFactory factory,
    			                   final Frame parent,
    			                   final String value){
    		
            super(parent,"Location",true);
            
            final Container c = getContentPane();
            setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
            
            a_text = new JTextField(value,60);
            a_text.setDocument(LocationCellEditor.this.a_textfield.getDocument());
            c.add(a_text);
            final String keptValue = a_text.getText();
            
            a_location = new CheckTreeManager(new HierarchicalCompoundStringTreeDisplay(factory));
            a_location.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
            c.add(new JScrollPane(a_location.getTree()));
            if ( !value.equals("") ) {
            	// set the selection in the CheckTree 
            	final HierarchicalCompoundString v = LocationCellEditor.this.a_factory.create(value);
            	final TreePath paths[] = new TreePath[1];
            	paths[0]=buildPath(v);
            	a_location.getSelectionModel().setSelectionPaths(paths);
            }
            a_location.getSelectionModel().addTreeSelectionListener(
            		new TreeSelectionListener() {
            			public void valueChanged(final TreeSelectionEvent e) {
            				final HierarchicalCompoundString cs = (HierarchicalCompoundString)(e.getPath().getLastPathComponent());
            				a_text.setText(cs.toString());}});

    		final JPanel buttonsPane = new JPanel(new GridLayout(1,2));
    		c.add(buttonsPane);
    		final JButton bOk = new JButton("OK");
    		final JButton bCancel = new JButton("Cancel");
    		buttonsPane.add(bOk);
    		buttonsPane.add(bCancel);
    		getRootPane().setDefaultButton(bOk);
    		bOk.addActionListener(new ActionListener() {
    				public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
    					close();
    				}
    		});
    		bCancel.addActionListener(new ActionListener() {
    					public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
    						a_text.setText(keptValue);
    						close();
    					}
    		});
    		pack();
    		setVisible(false);
    	}
    	
    	/**
    	 * 
    	 */
    	private void close() {
    		setVisible(false);
    		dispose();		
    	}
    	
        /**
         * @param string
         * @return TreePath corresponding to a HierarchicalCompoundString
         */
        private TreePath buildPath(final HierarchicalCompoundString string) {
        	if (string.getParent()==null) {
        		return new TreePath(string);
        	}
			return buildPath(string.getParent()).pathByAddingChild(string);
        }
    }
    
    /**
     * @param factory 
     * 
     */
    public LocationCellEditor(final HierarchicalCompoundStringFactory factory) {
        super();
        a_factory = factory;
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        a_textfield = new JTextField();
        add(a_textfield);
        a_button= new JButton("\u2193");
        add(a_button);
        a_button.addActionListener(
                new ActionListener() { 
                    public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) { a_internal.setVisible(true);}});
        a_listenerList = new Vector<CellEditorListener>();
    }
    
    /**
     * 
     */
    protected void fireEditingStopped() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = a_listenerList.size()-1; i>=0; i--) a_listenerList.get(i).editingStopped(e);
    } 

    /**
     * 
     */
    protected void fireEditingCanceled() {
        final ChangeEvent e = new ChangeEvent(this);
        for (int i = a_listenerList.size()-1; i>=0; i--) a_listenerList.get(i).editingCanceled(e);
    } 
    
    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final int row,
                                                 @SuppressWarnings("unused") final int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
          } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
          }
        a_textfield.setText(value.toString());
        final int preferredheight = getPreferredSize().height;
        if ( table.getRowHeight(row) < preferredheight ) {
            table.setRowHeight(row,preferredheight);
        }
        Component component=table;
        do{component=component.getParent();} while(!(component instanceof Frame));
        a_internal = new InternalLocationCellEditor(a_factory,(Frame)component,a_textfield.getText());
        return this;
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return a_textfield.getText();
    }

    /**
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(@SuppressWarnings("unused") final EventObject e) {
        return true;
    }

    /**
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

    /**
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    /**
     * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void addCellEditorListener(final CellEditorListener l) {
        a_listenerList.add(l);
    }

    /**
     * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void removeCellEditorListener(final CellEditorListener l) {
        a_listenerList.remove(l);
    }
    
    /**
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
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
        	if ( (e.getModifiers() & InputEvent.CTRL_MASK) != 0 ) return false;
            return true;
        } else {
            return false;
        }
    }
}
