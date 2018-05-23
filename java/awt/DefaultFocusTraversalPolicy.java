package java.awt;

import java.awt.peer.ComponentPeer;

public class DefaultFocusTraversalPolicy
  extends ContainerOrderFocusTraversalPolicy
{
  private static final long serialVersionUID = 8876966522510157497L;
  
  public DefaultFocusTraversalPolicy() {}
  
  protected boolean accept(Component paramComponent)
  {
    if ((!paramComponent.isVisible()) || (!paramComponent.isDisplayable()) || (!paramComponent.isEnabled())) {
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
    boolean bool = paramComponent.isFocusable();
    if (paramComponent.isFocusTraversableOverridden()) {
      return bool;
    }
    ComponentPeer localComponentPeer = paramComponent.getPeer();
    return (localComponentPeer != null) && (localComponentPeer.isFocusable());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\DefaultFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */