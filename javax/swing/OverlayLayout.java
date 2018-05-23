package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class OverlayLayout
  implements LayoutManager2, Serializable
{
  private Container target;
  private SizeRequirements[] xChildren;
  private SizeRequirements[] yChildren;
  private SizeRequirements xTotal;
  private SizeRequirements yTotal;
  
  @ConstructorProperties({"target"})
  public OverlayLayout(Container paramContainer)
  {
    target = paramContainer;
  }
  
  public final Container getTarget()
  {
    return target;
  }
  
  public void invalidateLayout(Container paramContainer)
  {
    checkContainer(paramContainer);
    xChildren = null;
    yChildren = null;
    xTotal = null;
    yTotal = null;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent)
  {
    invalidateLayout(paramComponent.getParent());
  }
  
  public void removeLayoutComponent(Component paramComponent)
  {
    invalidateLayout(paramComponent.getParent());
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject)
  {
    invalidateLayout(paramComponent.getParent());
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    Dimension localDimension = new Dimension(xTotal.preferred, yTotal.preferred);
    Insets localInsets = paramContainer.getInsets();
    width += left + right;
    height += top + bottom;
    return localDimension;
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    Dimension localDimension = new Dimension(xTotal.minimum, yTotal.minimum);
    Insets localInsets = paramContainer.getInsets();
    width += left + right;
    height += top + bottom;
    return localDimension;
  }
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    Dimension localDimension = new Dimension(xTotal.maximum, yTotal.maximum);
    Insets localInsets = paramContainer.getInsets();
    width += left + right;
    height += top + bottom;
    return localDimension;
  }
  
  public float getLayoutAlignmentX(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    return xTotal.alignment;
  }
  
  public float getLayoutAlignmentY(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    return yTotal.alignment;
  }
  
  public void layoutContainer(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    int i = paramContainer.getComponentCount();
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    int[] arrayOfInt3 = new int[i];
    int[] arrayOfInt4 = new int[i];
    Dimension localDimension = paramContainer.getSize();
    Insets localInsets = paramContainer.getInsets();
    width -= left + right;
    height -= top + bottom;
    SizeRequirements.calculateAlignedPositions(width, xTotal, xChildren, arrayOfInt1, arrayOfInt2);
    SizeRequirements.calculateAlignedPositions(height, yTotal, yChildren, arrayOfInt3, arrayOfInt4);
    for (int j = 0; j < i; j++)
    {
      Component localComponent = paramContainer.getComponent(j);
      localComponent.setBounds(left + arrayOfInt1[j], top + arrayOfInt3[j], arrayOfInt2[j], arrayOfInt4[j]);
    }
  }
  
  void checkContainer(Container paramContainer)
  {
    if (target != paramContainer) {
      throw new AWTError("OverlayLayout can't be shared");
    }
  }
  
  void checkRequests()
  {
    if ((xChildren == null) || (yChildren == null))
    {
      int i = target.getComponentCount();
      xChildren = new SizeRequirements[i];
      yChildren = new SizeRequirements[i];
      for (int j = 0; j < i; j++)
      {
        Component localComponent = target.getComponent(j);
        Dimension localDimension1 = localComponent.getMinimumSize();
        Dimension localDimension2 = localComponent.getPreferredSize();
        Dimension localDimension3 = localComponent.getMaximumSize();
        xChildren[j] = new SizeRequirements(width, width, width, localComponent.getAlignmentX());
        yChildren[j] = new SizeRequirements(height, height, height, localComponent.getAlignmentY());
      }
      xTotal = SizeRequirements.getAlignedSizeRequirements(xChildren);
      yTotal = SizeRequirements.getAlignedSizeRequirements(yChildren);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\OverlayLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */