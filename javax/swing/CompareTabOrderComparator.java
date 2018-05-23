package javax.swing;

import java.awt.Component;
import java.util.Comparator;

final class CompareTabOrderComparator
  implements Comparator<Component>
{
  private final DefaultFocusManager defaultFocusManager;
  
  CompareTabOrderComparator(DefaultFocusManager paramDefaultFocusManager)
  {
    defaultFocusManager = paramDefaultFocusManager;
  }
  
  public int compare(Component paramComponent1, Component paramComponent2)
  {
    if (paramComponent1 == paramComponent2) {
      return 0;
    }
    return defaultFocusManager.compareTabOrder(paramComponent1, paramComponent2) ? -1 : 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\CompareTabOrderComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */