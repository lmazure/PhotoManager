// adapdated from http://www.javaspecialists.co.za/archive/Issue082.html

package lmzr.util.checktree;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.event.*;

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
public TristateCheckBox(String text, Icon icon, State initial){
    super(text, icon);
    // Add a listener for when the mouse is pressed
    super.addMouseListener(new MouseAdapter() {
      @Override
    public void mousePressed(MouseEvent e) {
        grabFocus();
        TristateCheckBox.this._model.nextState();
      }
    });
    // Reset the keyboard action map
    ActionMap map = new ActionMapUIResource();
    map.put("pressed", new AbstractAction() {
      @Override
    public void actionPerformed(ActionEvent e) {
        grabFocus();
        TristateCheckBox.this._model.nextState();
      }
    });
    map.put("released", null);
    SwingUtilities.replaceUIActionMap(this, map);
    // set the model to the adapted model
    this._model = new TristateDecorator(getModel());
    setModel(this._model);
    setState(initial);
  }
  /**
 * @param text
 * @param initial
 */
public TristateCheckBox(String text, State initial) {
    this(text, null, initial);
  }
  /**
 * @param text
 */
public TristateCheckBox(String text) {
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
public synchronized void addMouseListener(MouseListener l) {
  	// do nothing
	  
  }
  /**
   * Set the new state to either SELECTED, NOT_SELECTED or
   * DONT_CARE.  If state == null, it is treated as DONT_CARE.
 * @param state
   */
  public void setState(State state) { this._model.setState(state); }
  /** Return the current state, which is determined by the
   * selection status of the model.
 * @return */
  public State getState() { return this._model.getState(); }
  @Override
public void setSelected(boolean b) {
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
    private TristateDecorator(ButtonModel other) {
      this._other = other;
    }
    private void setState(State state) {
      if (state == NOT_SELECTED) {
        this._other.setArmed(false);
        setPressed(false);
        setSelected(false);
      } else if (state == SELECTED) {
        this._other.setArmed(false);
        setPressed(false);
        setSelected(true);
      } else { // either "null" or DONT_CARE
        this._other.setArmed(true);
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
      } else if (isSelected() && isArmed()) {
        // don't care grey tick
        return DONT_CARE;
      } else {
        // normal deselected
        return NOT_SELECTED;
      }
    }
    /** We rotate between NOT_SELECTED, SELECTED and DONT_CARE.*/
    private void nextState() {
      State current = getState();
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
    public void setArmed(boolean b) {
    	// do nothing
    }
    /** We disable focusing on the component when it is not
     * enabled. */
    @Override
    public void setEnabled(boolean b) {
      setFocusable(b);
      this._other.setEnabled(b);
    }
    /** All these methods simply delegate to the "other" model
     * that is being decorated. */
    @Override
    public boolean isArmed() { return this._other.isArmed(); }
    @Override
    public boolean isSelected() { return this._other.isSelected(); }
    @Override
    public boolean isEnabled() { return this._other.isEnabled(); }
    @Override
    public boolean isPressed() { return this._other.isPressed(); }
    @Override
    public boolean isRollover() { return this._other.isRollover(); }
    @Override
    public void setSelected(boolean b) { this._other.setSelected(b); }
    @Override
    public void setPressed(boolean b) { this._other.setPressed(b); }
    @Override
    public void setRollover(boolean b) { this._other.setRollover(b); }
    @Override
    public void setMnemonic(int key) { this._other.setMnemonic(key); }
    @Override
    public int getMnemonic() { return this._other.getMnemonic(); }
    @Override
    public void setActionCommand(String s) {
      this._other.setActionCommand(s);
    }
    @Override
    public String getActionCommand() {
      return this._other.getActionCommand();
    }
    @Override
    public void setGroup(ButtonGroup group) {
      this._other.setGroup(group);
    }
    @Override
    public void addActionListener(ActionListener l) {
      this._other.addActionListener(l);
    }
    @Override
    public void removeActionListener(ActionListener l) {
      this._other.removeActionListener(l);
    }
    @Override
    public void addItemListener(ItemListener l) {
      this._other.addItemListener(l);
    }
    @Override
    public void removeItemListener(ItemListener l) {
      this._other.removeItemListener(l);
    }
    @Override
    public void addChangeListener(ChangeListener l) {
      this._other.addChangeListener(l);
    }
    @Override
    public void removeChangeListener(ChangeListener l) {
      this._other.removeChangeListener(l);
    }
    @Override
    public Object[] getSelectedObjects() {
      return this._other.getSelectedObjects();
    }
  }
}
