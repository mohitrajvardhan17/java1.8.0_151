package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;

class AncestorNotifier
  implements ComponentListener, PropertyChangeListener, Serializable
{
  transient Component firstInvisibleAncestor;
  EventListenerList listenerList = new EventListenerList();
  JComponent root;
  
  AncestorNotifier(JComponent paramJComponent)
  {
    root = paramJComponent;
    addListeners(paramJComponent, true);
  }
  
  void addAncestorListener(AncestorListener paramAncestorListener)
  {
    listenerList.add(AncestorListener.class, paramAncestorListener);
  }
  
  void removeAncestorListener(AncestorListener paramAncestorListener)
  {
    listenerList.remove(AncestorListener.class, paramAncestorListener);
  }
  
  AncestorListener[] getAncestorListeners()
  {
    return (AncestorListener[])listenerList.getListeners(AncestorListener.class);
  }
  
  protected void fireAncestorAdded(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AncestorListener.class)
      {
        AncestorEvent localAncestorEvent = new AncestorEvent(paramJComponent, paramInt, paramContainer1, paramContainer2);
        ((AncestorListener)arrayOfObject[(i + 1)]).ancestorAdded(localAncestorEvent);
      }
    }
  }
  
  protected void fireAncestorRemoved(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AncestorListener.class)
      {
        AncestorEvent localAncestorEvent = new AncestorEvent(paramJComponent, paramInt, paramContainer1, paramContainer2);
        ((AncestorListener)arrayOfObject[(i + 1)]).ancestorRemoved(localAncestorEvent);
      }
    }
  }
  
  protected void fireAncestorMoved(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == AncestorListener.class)
      {
        AncestorEvent localAncestorEvent = new AncestorEvent(paramJComponent, paramInt, paramContainer1, paramContainer2);
        ((AncestorListener)arrayOfObject[(i + 1)]).ancestorMoved(localAncestorEvent);
      }
    }
  }
  
  void removeAllListeners()
  {
    removeListeners(root);
  }
  
  void addListeners(Component paramComponent, boolean paramBoolean)
  {
    firstInvisibleAncestor = null;
    for (Object localObject = paramComponent; firstInvisibleAncestor == null; localObject = ((Component)localObject).getParent())
    {
      if ((paramBoolean) || (localObject != paramComponent))
      {
        ((Component)localObject).addComponentListener(this);
        if ((localObject instanceof JComponent))
        {
          JComponent localJComponent = (JComponent)localObject;
          localJComponent.addPropertyChangeListener(this);
        }
      }
      if ((!((Component)localObject).isVisible()) || (((Component)localObject).getParent() == null) || ((localObject instanceof Window))) {
        firstInvisibleAncestor = ((Component)localObject);
      }
    }
    if (((firstInvisibleAncestor instanceof Window)) && (firstInvisibleAncestor.isVisible())) {
      firstInvisibleAncestor = null;
    }
  }
  
  void removeListeners(Component paramComponent)
  {
    for (Object localObject = paramComponent; localObject != null; localObject = ((Component)localObject).getParent())
    {
      ((Component)localObject).removeComponentListener(this);
      if ((localObject instanceof JComponent))
      {
        JComponent localJComponent = (JComponent)localObject;
        localJComponent.removePropertyChangeListener(this);
      }
      if ((localObject == firstInvisibleAncestor) || ((localObject instanceof Window))) {
        break;
      }
    }
  }
  
  public void componentResized(ComponentEvent paramComponentEvent) {}
  
  public void componentMoved(ComponentEvent paramComponentEvent)
  {
    Component localComponent = paramComponentEvent.getComponent();
    fireAncestorMoved(root, 3, (Container)localComponent, localComponent.getParent());
  }
  
  public void componentShown(ComponentEvent paramComponentEvent)
  {
    Component localComponent = paramComponentEvent.getComponent();
    if (localComponent == firstInvisibleAncestor)
    {
      addListeners(localComponent, false);
      if (firstInvisibleAncestor == null) {
        fireAncestorAdded(root, 1, (Container)localComponent, localComponent.getParent());
      }
    }
  }
  
  public void componentHidden(ComponentEvent paramComponentEvent)
  {
    Component localComponent = paramComponentEvent.getComponent();
    int i = firstInvisibleAncestor == null ? 1 : 0;
    if (!(localComponent instanceof Window)) {
      removeListeners(localComponent.getParent());
    }
    firstInvisibleAncestor = localComponent;
    if (i != 0) {
      fireAncestorRemoved(root, 2, (Container)localComponent, localComponent.getParent());
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if ((str != null) && ((str.equals("parent")) || (str.equals("ancestor"))))
    {
      JComponent localJComponent = (JComponent)paramPropertyChangeEvent.getSource();
      if (paramPropertyChangeEvent.getNewValue() != null)
      {
        if (localJComponent == firstInvisibleAncestor)
        {
          addListeners(localJComponent, false);
          if (firstInvisibleAncestor == null) {
            fireAncestorAdded(root, 1, localJComponent, localJComponent.getParent());
          }
        }
      }
      else
      {
        int i = firstInvisibleAncestor == null ? 1 : 0;
        Container localContainer = (Container)paramPropertyChangeEvent.getOldValue();
        removeListeners(localContainer);
        firstInvisibleAncestor = localJComponent;
        if (i != 0) {
          fireAncestorRemoved(root, 2, localJComponent, localContainer);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\AncestorNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */