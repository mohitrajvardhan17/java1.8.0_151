package javax.swing.plaf.basic;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import javax.swing.plaf.UIResource;
import sun.swing.MenuItemLayoutHelper;

public class DefaultMenuLayout
  extends BoxLayout
  implements UIResource
{
  public DefaultMenuLayout(Container paramContainer, int paramInt)
  {
    super(paramContainer, paramInt);
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    if ((paramContainer instanceof JPopupMenu))
    {
      JPopupMenu localJPopupMenu = (JPopupMenu)paramContainer;
      MenuItemLayoutHelper.clearUsedClientProperties(localJPopupMenu);
      if (localJPopupMenu.getComponentCount() == 0) {
        return new Dimension(0, 0);
      }
    }
    super.invalidateLayout(paramContainer);
    return super.preferredLayoutSize(paramContainer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\DefaultMenuLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */