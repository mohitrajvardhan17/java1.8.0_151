package javax.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultButtonModel
  implements ButtonModel, Serializable
{
  protected int stateMask = 0;
  protected String actionCommand = null;
  protected ButtonGroup group = null;
  protected int mnemonic = 0;
  protected transient ChangeEvent changeEvent = null;
  protected EventListenerList listenerList = new EventListenerList();
  private boolean menuItem = false;
  public static final int ARMED = 1;
  public static final int SELECTED = 2;
  public static final int PRESSED = 4;
  public static final int ENABLED = 8;
  public static final int ROLLOVER = 16;
  
  public DefaultButtonModel()
  {
    setEnabled(true);
  }
  
  public void setActionCommand(String paramString)
  {
    actionCommand = paramString;
  }
  
  public String getActionCommand()
  {
    return actionCommand;
  }
  
  public boolean isArmed()
  {
    return (stateMask & 0x1) != 0;
  }
  
  public boolean isSelected()
  {
    return (stateMask & 0x2) != 0;
  }
  
  public boolean isEnabled()
  {
    return (stateMask & 0x8) != 0;
  }
  
  public boolean isPressed()
  {
    return (stateMask & 0x4) != 0;
  }
  
  public boolean isRollover()
  {
    return (stateMask & 0x10) != 0;
  }
  
  public void setArmed(boolean paramBoolean)
  {
    if ((isMenuItem()) && (UIManager.getBoolean("MenuItem.disabledAreNavigable")))
    {
      if (isArmed() != paramBoolean) {}
    }
    else if ((isArmed() == paramBoolean) || (!isEnabled())) {
      return;
    }
    if (paramBoolean) {
      stateMask |= 0x1;
    } else {
      stateMask &= 0xFFFFFFFE;
    }
    fireStateChanged();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (isEnabled() == paramBoolean) {
      return;
    }
    if (paramBoolean)
    {
      stateMask |= 0x8;
    }
    else
    {
      stateMask &= 0xFFFFFFF7;
      stateMask &= 0xFFFFFFFE;
      stateMask &= 0xFFFFFFFB;
    }
    fireStateChanged();
  }
  
  public void setSelected(boolean paramBoolean)
  {
    if (isSelected() == paramBoolean) {
      return;
    }
    if (paramBoolean) {
      stateMask |= 0x2;
    } else {
      stateMask &= 0xFFFFFFFD;
    }
    fireItemStateChanged(new ItemEvent(this, 701, this, paramBoolean ? 1 : 2));
    fireStateChanged();
  }
  
  public void setPressed(boolean paramBoolean)
  {
    if ((isPressed() == paramBoolean) || (!isEnabled())) {
      return;
    }
    if (paramBoolean) {
      stateMask |= 0x4;
    } else {
      stateMask &= 0xFFFFFFFB;
    }
    if ((!isPressed()) && (isArmed()))
    {
      int i = 0;
      AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
      if ((localAWTEvent instanceof InputEvent)) {
        i = ((InputEvent)localAWTEvent).getModifiers();
      } else if ((localAWTEvent instanceof ActionEvent)) {
        i = ((ActionEvent)localAWTEvent).getModifiers();
      }
      fireActionPerformed(new ActionEvent(this, 1001, getActionCommand(), EventQueue.getMostRecentEventTime(), i));
    }
    fireStateChanged();
  }
  
  public void setRollover(boolean paramBoolean)
  {
    if ((isRollover() == paramBoolean) || (!isEnabled())) {
      return;
    }
    if (paramBoolean) {
      stateMask |= 0x10;
    } else {
      stateMask &= 0xFFFFFFEF;
    }
    fireStateChanged();
  }
  
  public void setMnemonic(int paramInt)
  {
    mnemonic = paramInt;
    fireStateChanged();
  }
  
  public int getMnemonic()
  {
    return mnemonic;
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(changeEvent);
      }
    }
  }
  
  public void addActionListener(ActionListener paramActionListener)
  {
    listenerList.add(ActionListener.class, paramActionListener);
  }
  
  public void removeActionListener(ActionListener paramActionListener)
  {
    listenerList.remove(ActionListener.class, paramActionListener);
  }
  
  public ActionListener[] getActionListeners()
  {
    return (ActionListener[])listenerList.getListeners(ActionListener.class);
  }
  
  protected void fireActionPerformed(ActionEvent paramActionEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class) {
        ((ActionListener)arrayOfObject[(i + 1)]).actionPerformed(paramActionEvent);
      }
    }
  }
  
  public void addItemListener(ItemListener paramItemListener)
  {
    listenerList.add(ItemListener.class, paramItemListener);
  }
  
  public void removeItemListener(ItemListener paramItemListener)
  {
    listenerList.remove(ItemListener.class, paramItemListener);
  }
  
  public ItemListener[] getItemListeners()
  {
    return (ItemListener[])listenerList.getListeners(ItemListener.class);
  }
  
  protected void fireItemStateChanged(ItemEvent paramItemEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ItemListener.class) {
        ((ItemListener)arrayOfObject[(i + 1)]).itemStateChanged(paramItemEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  public Object[] getSelectedObjects()
  {
    return null;
  }
  
  public void setGroup(ButtonGroup paramButtonGroup)
  {
    group = paramButtonGroup;
  }
  
  public ButtonGroup getGroup()
  {
    return group;
  }
  
  boolean isMenuItem()
  {
    return menuItem;
  }
  
  void setMenuItem(boolean paramBoolean)
  {
    menuItem = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultButtonModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */