package javax.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class ActionPropertyChangeListener<T extends JComponent>
  implements PropertyChangeListener, Serializable
{
  private static ReferenceQueue<JComponent> queue;
  private transient OwnedWeakReference<T> target;
  private Action action;
  
  private static ReferenceQueue<JComponent> getQueue()
  {
    synchronized (ActionPropertyChangeListener.class)
    {
      if (queue == null) {
        queue = new ReferenceQueue();
      }
    }
    return queue;
  }
  
  public ActionPropertyChangeListener(T paramT, Action paramAction)
  {
    setTarget(paramT);
    action = paramAction;
  }
  
  public final void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JComponent localJComponent = getTarget();
    if (localJComponent == null) {
      getAction().removePropertyChangeListener(this);
    } else {
      actionPropertyChanged(localJComponent, getAction(), paramPropertyChangeEvent);
    }
  }
  
  protected abstract void actionPropertyChanged(T paramT, Action paramAction, PropertyChangeEvent paramPropertyChangeEvent);
  
  private void setTarget(T paramT)
  {
    ReferenceQueue localReferenceQueue = getQueue();
    OwnedWeakReference localOwnedWeakReference;
    while ((localOwnedWeakReference = (OwnedWeakReference)localReferenceQueue.poll()) != null)
    {
      ActionPropertyChangeListener localActionPropertyChangeListener = localOwnedWeakReference.getOwner();
      Action localAction = localActionPropertyChangeListener.getAction();
      if (localAction != null) {
        localAction.removePropertyChangeListener(localActionPropertyChangeListener);
      }
    }
    target = new OwnedWeakReference(paramT, localReferenceQueue, this);
  }
  
  public T getTarget()
  {
    if (target == null) {
      return null;
    }
    return (JComponent)target.get();
  }
  
  public Action getAction()
  {
    return action;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(getTarget());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    JComponent localJComponent = (JComponent)paramObjectInputStream.readObject();
    if (localJComponent != null) {
      setTarget(localJComponent);
    }
  }
  
  private static class OwnedWeakReference<U extends JComponent>
    extends WeakReference<U>
  {
    private ActionPropertyChangeListener<?> owner;
    
    OwnedWeakReference(U paramU, ReferenceQueue<? super U> paramReferenceQueue, ActionPropertyChangeListener<?> paramActionPropertyChangeListener)
    {
      super(paramReferenceQueue);
      owner = paramActionPropertyChangeListener;
    }
    
    public ActionPropertyChangeListener<?> getOwner()
    {
      return owner;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ActionPropertyChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */