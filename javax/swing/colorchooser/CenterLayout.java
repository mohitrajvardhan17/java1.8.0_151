package javax.swing.colorchooser;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

class CenterLayout
  implements LayoutManager, Serializable
{
  CenterLayout() {}
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    Component localComponent = paramContainer.getComponent(0);
    if (localComponent != null)
    {
      Dimension localDimension = localComponent.getPreferredSize();
      Insets localInsets = paramContainer.getInsets();
      width += left + right;
      height += top + bottom;
      return localDimension;
    }
    return new Dimension(0, 0);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    return preferredLayoutSize(paramContainer);
  }
  
  public void layoutContainer(Container paramContainer)
  {
    try
    {
      Component localComponent = paramContainer.getComponent(0);
      localComponent.setSize(localComponent.getPreferredSize());
      Dimension localDimension1 = localComponent.getSize();
      Dimension localDimension2 = paramContainer.getSize();
      Insets localInsets = paramContainer.getInsets();
      width -= left + right;
      height -= top + bottom;
      int i = width / 2 - width / 2;
      int j = height / 2 - height / 2;
      i += left;
      j += top;
      localComponent.setBounds(i, j, width, height);
    }
    catch (Exception localException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\CenterLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */