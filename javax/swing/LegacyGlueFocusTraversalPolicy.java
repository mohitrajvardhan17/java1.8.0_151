package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

final class LegacyGlueFocusTraversalPolicy
  extends FocusTraversalPolicy
  implements Serializable
{
  private transient FocusTraversalPolicy delegatePolicy;
  private transient DefaultFocusManager delegateManager;
  private HashMap<Component, Component> forwardMap = new HashMap();
  private HashMap<Component, Component> backwardMap = new HashMap();
  
  LegacyGlueFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy)
  {
    delegatePolicy = paramFocusTraversalPolicy;
  }
  
  LegacyGlueFocusTraversalPolicy(DefaultFocusManager paramDefaultFocusManager)
  {
    delegateManager = paramDefaultFocusManager;
  }
  
  void setNextFocusableComponent(Component paramComponent1, Component paramComponent2)
  {
    forwardMap.put(paramComponent1, paramComponent2);
    backwardMap.put(paramComponent2, paramComponent1);
  }
  
  void unsetNextFocusableComponent(Component paramComponent1, Component paramComponent2)
  {
    forwardMap.remove(paramComponent1);
    backwardMap.remove(paramComponent2);
  }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent)
  {
    Component localComponent1 = paramComponent;
    HashSet localHashSet = new HashSet();
    do
    {
      Component localComponent2 = localComponent1;
      localComponent1 = (Component)forwardMap.get(localComponent1);
      if (localComponent1 == null)
      {
        if ((delegatePolicy != null) && (localComponent2.isFocusCycleRoot(paramContainer))) {
          return delegatePolicy.getComponentAfter(paramContainer, localComponent2);
        }
        if (delegateManager != null) {
          return delegateManager.getComponentAfter(paramContainer, paramComponent);
        }
        return null;
      }
      if (localHashSet.contains(localComponent1)) {
        return null;
      }
      localHashSet.add(localComponent1);
    } while (!accept(localComponent1));
    return localComponent1;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent)
  {
    Component localComponent1 = paramComponent;
    HashSet localHashSet = new HashSet();
    do
    {
      Component localComponent2 = localComponent1;
      localComponent1 = (Component)backwardMap.get(localComponent1);
      if (localComponent1 == null)
      {
        if ((delegatePolicy != null) && (localComponent2.isFocusCycleRoot(paramContainer))) {
          return delegatePolicy.getComponentBefore(paramContainer, localComponent2);
        }
        if (delegateManager != null) {
          return delegateManager.getComponentBefore(paramContainer, paramComponent);
        }
        return null;
      }
      if (localHashSet.contains(localComponent1)) {
        return null;
      }
      localHashSet.add(localComponent1);
    } while (!accept(localComponent1));
    return localComponent1;
  }
  
  public Component getFirstComponent(Container paramContainer)
  {
    if (delegatePolicy != null) {
      return delegatePolicy.getFirstComponent(paramContainer);
    }
    if (delegateManager != null) {
      return delegateManager.getFirstComponent(paramContainer);
    }
    return null;
  }
  
  public Component getLastComponent(Container paramContainer)
  {
    if (delegatePolicy != null) {
      return delegatePolicy.getLastComponent(paramContainer);
    }
    if (delegateManager != null) {
      return delegateManager.getLastComponent(paramContainer);
    }
    return null;
  }
  
  public Component getDefaultComponent(Container paramContainer)
  {
    if (delegatePolicy != null) {
      return delegatePolicy.getDefaultComponent(paramContainer);
    }
    return getFirstComponent(paramContainer);
  }
  
  private boolean accept(Component paramComponent)
  {
    if ((!paramComponent.isVisible()) || (!paramComponent.isDisplayable()) || (!paramComponent.isFocusable()) || (!paramComponent.isEnabled())) {
      return false;
    }
    if (!(paramComponent instanceof Window)) {
      for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent())
      {
        if ((!localContainer.isEnabled()) && (!localContainer.isLightweight())) {
          return false;
        }
        if ((localContainer instanceof Window)) {
          break;
        }
      }
    }
    return true;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if ((delegatePolicy instanceof Serializable)) {
      paramObjectOutputStream.writeObject(delegatePolicy);
    } else {
      paramObjectOutputStream.writeObject(null);
    }
    if ((delegateManager instanceof Serializable)) {
      paramObjectOutputStream.writeObject(delegateManager);
    } else {
      paramObjectOutputStream.writeObject(null);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    delegatePolicy = ((FocusTraversalPolicy)paramObjectInputStream.readObject());
    delegateManager = ((DefaultFocusManager)paramObjectInputStream.readObject());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\LegacyGlueFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */