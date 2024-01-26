// adapdated from http://www.javaspecialists.co.za/archive/Issue082.html

package lmzr.util.checktree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;

/**
 * Maintenance tip - There were some tricks to getting this code
 * working:
 *
 * 1. You have to overwite addMouseListener() to do nothing
 * 2. You have to add a mouse event on mousePressed by calling
 * super.addMouseListener()
 * 3. You have to replace the UIActionMap for the keyboard event
 * "pressed" with your own one.
 * 4. You have to remove the UIActionMap for the keyboard event
 * "released".
 * 5. You have to grab focus when the next state is entered,
 * otherwise clicking on the component won't get the focus.
 * 6. You have to make a TristateDecorator as a button model that
 * wraps the original button model and does state management.
 */
public class TristateCheckBox extends JCheckBox {
  /** This is a type-safe enumerated type */
  public static class State { private State() { } }
  /**
 *
 */
public static final State NOT_SELECTED = new State();
  /**
 *
 */
public static final State SELECTED = new State();
  /**
 *
 */
public static final State DONT_CARE = new State();

  private final TristateDecorator _model;

  /**
 * @param text
 * @param icon
 * @param initial
 */
public TristateCheckBox(final String text, final Icon icon, final State initial){
    super(text, icon);
    // Add a listener for when the mouse is pressed
    super.addMouseListener(new MouseAdapter() {
      @Override
    public void mousePressed(final MouseEvent e) {
        grabFocus();
        _model.nextState();
      }
    });
    // Reset the keyboard action map
    final ActionMap map = new ActionMapUIResource();
    map.put("pressed", new AbstractAction() {
      @Override
    public void actionPerformed(final ActionEvent e) {
        grabFocus();
        _model.nextState();
      }
    });
    map.put("released", null);
    SwingUtilities.replaceUIActionMap(this, map);
    // set the model to the adapted model
    _model = new TristateDecorator(getModel());
    setModel(_model);
    setState(initial);
  }
  /**
 * @param text
 * @param initial
 */
public TristateCheckBox(final String text, final State initial) {
    this(text, null, initial);
  }
  /**
 * @param text
 */
public TristateCheckBox(final String text) {
    this(text, DONT_CARE);
  }
  /**
 *
 */
public TristateCheckBox() {
    this(null);
  }

  /** No one may add mouse listeners, not even Swing! */
  @Override
public synchronized void addMouseListener(final MouseListener l) {
      // do nothing

  }
  /**
   * Set the new state to either SELECTED, NOT_SELECTED or
   * DONT_CARE.  If state == null, it is treated as DONT_CARE.
 * @param state
   */
  public void setState(final State state) { _model.setState(state); }
  /** Return the current state, which is determined by the
   * selection status of the model.
 * @return */
  public State getState() { return _model.getState(); }
  @Override
public void setSelected(final boolean b) {
    if (b) {
      setState(SELECTED);
    } else {
      setState(NOT_SELECTED);
    }
  }
  /**
   * Exactly which Design Pattern is this?  Is it an Adapter,
   * a Proxy or a Decorator?  In this case, my vote lies with the
   * Decorator, because we are extending functionality and
   * "decorating" the original model with a more powerful model.
   */
  private class TristateDecorator implements ButtonModel {
    private final ButtonModel _other;
    private TristateDecorator(final ButtonModel other) {
      _other = other;
    }
    private void setState(final State state) {
      if (state == NOT_SELECTED) {
        _other.setArmed(false);
        setPressed(false);
        setSelected(false);
      } else if (state == SELECTED) {
        _other.setArmed(false);
        setPressed(false);
        setSelected(true);
      } else { // either "null" or DONT_CARE
        _other.setArmed(true);
        setPressed(true);
        setSelected(false);
      }
    }
    /**
     * The current state is embedded in the selection / armed
     * state of the model.
     *
     * We return the SELECTED state when the checkbox is selected
     * but not armed, DONT_CARE state when the checkbox is
     * selected and armed (grey) and NOT_SELECTED when the
     * checkbox is deselected.
     */
    private State getState() {
      if (isSelected() && !isArmed()) {
        // normal black tick
        return SELECTED;
      }
    if (isSelected() && isArmed()) {
        // don't care grey tick
        return DONT_CARE;
      } else {
        // normal deselected
        return NOT_SELECTED;
      }
    }
    /** We rotate between NOT_SELECTED, SELECTED and DONT_CARE.*/
    private void nextState() {
      final State current = getState();
      if (current == NOT_SELECTED) {
        setState(SELECTED);
      } else if (current == SELECTED) {
        setState(DONT_CARE);
      } else if (current == DONT_CARE) {
        setState(NOT_SELECTED);
      }
    }
    /** Filter: No one may change the armed status except us. */
    @Override
    public void setArmed(final boolean b) {
        // do nothing
    }
    /** We disable focusing on the component when it is not
     * enabled. */
    @Override
    public void setEnabled(final boolean b) {
      setFocusable(b);
      _other.setEnabled(b);
    }
    /** All these methods simply delegate to the "other" model
     * that is being decorated. */
    @Override
    public boolean isArmed() { return _other.isArmed(); }
    @Override
    public boolean isSelected() { return _other.isSelected(); }
    @Override
    public boolean isEnabled() { return _other.isEnabled(); }
    @Override
    public boolean isPressed() { return _other.isPressed(); }
    @Override
    public boolean isRollover() { return _other.isRollover(); }
    @Override
    public void setSelected(final boolean b) { _other.setSelected(b); }
    @Override
    public void setPressed(final boolean b) { _other.setPressed(b); }
    @Override
    public void setRollover(final boolean b) { _other.setRollover(b); }
    @Override
    public void setMnemonic(final int key) { _other.setMnemonic(key); }
    @Override
    public int getMnemonic() { return _other.getMnemonic(); }
    @Override
    public void setActionCommand(final String s) {
      _other.setActionCommand(s);
    }
    @Override
    public String getActionCommand() {
      return _other.getActionCommand();
    }
    @Override
    public void setGroup(final ButtonGroup group) {
      _other.setGroup(group);
    }
    @Override
    public void addActionListener(final ActionListener l) {
      _other.addActionListener(l);
    }
    @Override
    public void removeActionListener(final ActionListener l) {
      _other.removeActionListener(l);
    }
    @Override
    public void addItemListener(final ItemListener l) {
      _other.addItemListener(l);
    }
    @Override
    public void removeItemListener(final ItemListener l) {
      _other.removeItemListener(l);
    }
    @Override
    public void addChangeListener(final ChangeListener l) {
      _other.addChangeListener(l);
    }
    @Override
    public void removeChangeListener(final ChangeListener l) {
      _other.removeChangeListener(l);
    }
    @Override
    public Object[] getSelectedObjects() {
      return _other.getSelectedObjects();
    }
  }
}
