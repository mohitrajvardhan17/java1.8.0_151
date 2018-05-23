package javax.swing;

import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.Set;

final class DelegatingDefaultFocusManager
  extends DefaultFocusManager
{
  private final KeyboardFocusManager delegate;
  
  DelegatingDefaultFocusManager(KeyboardFocusManager paramKeyboardFocusManager)
  {
    delegate = paramKeyboardFocusManager;
    setDefaultFocusTraversalPolicy(gluePolicy);
  }
  
  KeyboardFocusManager getDelegate()
  {
    return delegate;
  }
  
  public void processKeyEvent(Component paramComponent, KeyEvent paramKeyEvent)
  {
    delegate.processKeyEvent(paramComponent, paramKeyEvent);
  }
  
  public void focusNextComponent(Component paramComponent)
  {
    delegate.focusNextComponent(paramComponent);
  }
  
  public void focusPreviousComponent(Component paramComponent)
  {
    delegate.focusPreviousComponent(paramComponent);
  }
  
  public Component getFocusOwner()
  {
    return delegate.getFocusOwner();
  }
  
  public void clearGlobalFocusOwner()
  {
    delegate.clearGlobalFocusOwner();
  }
  
  public Component getPermanentFocusOwner()
  {
    return delegate.getPermanentFocusOwner();
  }
  
  public Window getFocusedWindow()
  {
    return delegate.getFocusedWindow();
  }
  
  public Window getActiveWindow()
  {
    return delegate.getActiveWindow();
  }
  
  public FocusTraversalPolicy getDefaultFocusTraversalPolicy()
  {
    return delegate.getDefaultFocusTraversalPolicy();
  }
  
  public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy)
  {
    if (delegate != null) {
      delegate.setDefaultFocusTraversalPolicy(paramFocusTraversalPolicy);
    }
  }
  
  public void setDefaultFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet)
  {
    delegate.setDefaultFocusTraversalKeys(paramInt, paramSet);
  }
  
  public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int paramInt)
  {
    return delegate.getDefaultFocusTraversalKeys(paramInt);
  }
  
  public Container getCurrentFocusCycleRoot()
  {
    return delegate.getCurrentFocusCycleRoot();
  }
  
  public void setGlobalCurrentFocusCycleRoot(Container paramContainer)
  {
    delegate.setGlobalCurrentFocusCycleRoot(paramContainer);
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    delegate.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    delegate.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    delegate.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    delegate.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void addVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
  {
    delegate.addVetoableChangeListener(paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(VetoableChangeListener paramVetoableChangeListener)
  {
    delegate.removeVetoableChangeListener(paramVetoableChangeListener);
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    delegate.addVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener)
  {
    delegate.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void addKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher)
  {
    delegate.addKeyEventDispatcher(paramKeyEventDispatcher);
  }
  
  public void removeKeyEventDispatcher(KeyEventDispatcher paramKeyEventDispatcher)
  {
    delegate.removeKeyEventDispatcher(paramKeyEventDispatcher);
  }
  
  public boolean dispatchEvent(AWTEvent paramAWTEvent)
  {
    return delegate.dispatchEvent(paramAWTEvent);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return delegate.dispatchKeyEvent(paramKeyEvent);
  }
  
  public void upFocusCycle(Component paramComponent)
  {
    delegate.upFocusCycle(paramComponent);
  }
  
  public void downFocusCycle(Container paramContainer)
  {
    delegate.downFocusCycle(paramContainer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DelegatingDefaultFocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */