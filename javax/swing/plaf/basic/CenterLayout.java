package javax.swing.plaf.basic;

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
      return new Dimension(width + left + right, height + top + bottom);
    }
    return new Dimension(0, 0);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    return preferredLayoutSize(paramContainer);
  }
  
  public void layoutContainer(Container paramContainer)
  {
    if (paramContainer.getComponentCount() > 0)
    {
      Component localComponent = paramContainer.getComponent(0);
      Dimension localDimension = localComponent.getPreferredSize();
      int i = paramContainer.getWidth();
      int j = paramContainer.getHeight();
      Insets localInsets = paramContainer.getInsets();
      i -= left + right;
      j -= top + bottom;
      int k = (i - width) / 2 + left;
      int m = (j - height) / 2 + top;
      localComponent.setBounds(k, m, width, height);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\CenterLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */