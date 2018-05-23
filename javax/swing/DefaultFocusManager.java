package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

public class DefaultFocusManager
  extends FocusManager
{
  final FocusTraversalPolicy gluePolicy = new LegacyGlueFocusTraversalPolicy(this);
  private final FocusTraversalPolicy layoutPolicy = new LegacyLayoutFocusTraversalPolicy(this);
  private final LayoutComparator comparator = new LayoutComparator();
  
  public DefaultFocusManager()
  {
    setDefaultFocusTraversalPolicy(gluePolicy);
  }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent)
  {
    Container localContainer = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (localContainer != null)
    {
      FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
      if (localFocusTraversalPolicy != gluePolicy) {
        return localFocusTraversalPolicy.getComponentAfter(localContainer, paramComponent);
      }
      comparator.setComponentOrientation(localContainer.getComponentOrientation());
      return layoutPolicy.getComponentAfter(localContainer, paramComponent);
    }
    return null;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent)
  {
    Container localContainer = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (localContainer != null)
    {
      FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
      if (localFocusTraversalPolicy != gluePolicy) {
        return localFocusTraversalPolicy.getComponentBefore(localContainer, paramComponent);
      }
      comparator.setComponentOrientation(localContainer.getComponentOrientation());
      return layoutPolicy.getComponentBefore(localContainer, paramComponent);
    }
    return null;
  }
  
  public Component getFirstComponent(Container paramContainer)
  {
    Container localContainer = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (localContainer != null)
    {
      FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
      if (localFocusTraversalPolicy != gluePolicy) {
        return localFocusTraversalPolicy.getFirstComponent(localContainer);
      }
      comparator.setComponentOrientation(localContainer.getComponentOrientation());
      return layoutPolicy.getFirstComponent(localContainer);
    }
    return null;
  }
  
  public Component getLastComponent(Container paramContainer)
  {
    Container localContainer = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (localContainer != null)
    {
      FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
      if (localFocusTraversalPolicy != gluePolicy) {
        return localFocusTraversalPolicy.getLastComponent(localContainer);
      }
      comparator.setComponentOrientation(localContainer.getComponentOrientation());
      return layoutPolicy.getLastComponent(localContainer);
    }
    return null;
  }
  
  public boolean compareTabOrder(Component paramComponent1, Component paramComponent2)
  {
    return comparator.compare(paramComponent1, paramComponent2) < 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultFocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */