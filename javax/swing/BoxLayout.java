package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.beans.ConstructorProperties;
import java.io.PrintStream;
import java.io.Serializable;

public class BoxLayout
  implements LayoutManager2, Serializable
{
  public static final int X_AXIS = 0;
  public static final int Y_AXIS = 1;
  public static final int LINE_AXIS = 2;
  public static final int PAGE_AXIS = 3;
  private int axis;
  private Container target;
  private transient SizeRequirements[] xChildren;
  private transient SizeRequirements[] yChildren;
  private transient SizeRequirements xTotal;
  private transient SizeRequirements yTotal;
  private transient PrintStream dbg;
  
  @ConstructorProperties({"target", "axis"})
  public BoxLayout(Container paramContainer, int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2) && (paramInt != 3)) {
      throw new AWTError("Invalid axis");
    }
    axis = paramInt;
    target = paramContainer;
  }
  
  BoxLayout(Container paramContainer, int paramInt, PrintStream paramPrintStream)
  {
    this(paramContainer, paramInt);
    dbg = paramPrintStream;
  }
  
  public final Container getTarget()
  {
    return target;
  }
  
  public final int getAxis()
  {
    return axis;
  }
  
  public synchronized void invalidateLayout(Container paramContainer)
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
    Dimension localDimension;
    synchronized (this)
    {
      checkContainer(paramContainer);
      checkRequests();
      localDimension = new Dimension(xTotal.preferred, yTotal.preferred);
    }
    ??? = paramContainer.getInsets();
    width = ((int)Math.min(width + left + right, 2147483647L));
    height = ((int)Math.min(height + top + bottom, 2147483647L));
    return localDimension;
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    Dimension localDimension;
    synchronized (this)
    {
      checkContainer(paramContainer);
      checkRequests();
      localDimension = new Dimension(xTotal.minimum, yTotal.minimum);
    }
    ??? = paramContainer.getInsets();
    width = ((int)Math.min(width + left + right, 2147483647L));
    height = ((int)Math.min(height + top + bottom, 2147483647L));
    return localDimension;
  }
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    Dimension localDimension;
    synchronized (this)
    {
      checkContainer(paramContainer);
      checkRequests();
      localDimension = new Dimension(xTotal.maximum, yTotal.maximum);
    }
    ??? = paramContainer.getInsets();
    width = ((int)Math.min(width + left + right, 2147483647L));
    height = ((int)Math.min(height + top + bottom, 2147483647L));
    return localDimension;
  }
  
  public synchronized float getLayoutAlignmentX(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    return xTotal.alignment;
  }
  
  public synchronized float getLayoutAlignmentY(Container paramContainer)
  {
    checkContainer(paramContainer);
    checkRequests();
    return yTotal.alignment;
  }
  
  public void layoutContainer(Container paramContainer)
  {
    checkContainer(paramContainer);
    Object localObject1 = paramContainer.getComponentCount();
    int[] arrayOfInt1 = new int[localObject1];
    int[] arrayOfInt2 = new int[localObject1];
    int[] arrayOfInt3 = new int[localObject1];
    int[] arrayOfInt4 = new int[localObject1];
    Dimension localDimension = paramContainer.getSize();
    Insets localInsets = paramContainer.getInsets();
    width -= left + right;
    height -= top + bottom;
    ComponentOrientation localComponentOrientation = paramContainer.getComponentOrientation();
    int i = resolveAxis(axis, localComponentOrientation);
    boolean bool = i != axis ? localComponentOrientation.isLeftToRight() : true;
    synchronized (this)
    {
      checkRequests();
      if (i == 0)
      {
        SizeRequirements.calculateTiledPositions(width, xTotal, xChildren, arrayOfInt1, arrayOfInt2, bool);
        SizeRequirements.calculateAlignedPositions(height, yTotal, yChildren, arrayOfInt3, arrayOfInt4);
      }
      else
      {
        SizeRequirements.calculateAlignedPositions(width, xTotal, xChildren, arrayOfInt1, arrayOfInt2, bool);
        SizeRequirements.calculateTiledPositions(height, yTotal, yChildren, arrayOfInt3, arrayOfInt4);
      }
    }
    Component localComponent;
    for (??? = 0; ??? < localObject1; ???++)
    {
      localComponent = paramContainer.getComponent(???);
      localComponent.setBounds((int)Math.min(left + arrayOfInt1[???], 2147483647L), (int)Math.min(top + arrayOfInt3[???], 2147483647L), arrayOfInt2[???], arrayOfInt4[???]);
    }
    if (dbg != null) {
      for (??? = 0; ??? < localObject1; ???++)
      {
        localComponent = paramContainer.getComponent(???);
        dbg.println(localComponent.toString());
        dbg.println("X: " + xChildren[???]);
        dbg.println("Y: " + yChildren[???]);
      }
    }
  }
  
  void checkContainer(Container paramContainer)
  {
    if (target != paramContainer) {
      throw new AWTError("BoxLayout can't be shared");
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
        if (!localComponent.isVisible())
        {
          xChildren[j] = new SizeRequirements(0, 0, 0, localComponent.getAlignmentX());
          yChildren[j] = new SizeRequirements(0, 0, 0, localComponent.getAlignmentY());
        }
        else
        {
          Dimension localDimension1 = localComponent.getMinimumSize();
          Dimension localDimension2 = localComponent.getPreferredSize();
          Dimension localDimension3 = localComponent.getMaximumSize();
          xChildren[j] = new SizeRequirements(width, width, width, localComponent.getAlignmentX());
          yChildren[j] = new SizeRequirements(height, height, height, localComponent.getAlignmentY());
        }
      }
      j = resolveAxis(axis, target.getComponentOrientation());
      if (j == 0)
      {
        xTotal = SizeRequirements.getTiledSizeRequirements(xChildren);
        yTotal = SizeRequirements.getAlignedSizeRequirements(yChildren);
      }
      else
      {
        xTotal = SizeRequirements.getAlignedSizeRequirements(xChildren);
        yTotal = SizeRequirements.getTiledSizeRequirements(yChildren);
      }
    }
  }
  
  private int resolveAxis(int paramInt, ComponentOrientation paramComponentOrientation)
  {
    int i;
    if (paramInt == 2) {
      i = paramComponentOrientation.isHorizontal() ? 0 : 1;
    } else if (paramInt == 3) {
      i = paramComponentOrientation.isHorizontal() ? 1 : 0;
    } else {
      i = paramInt;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\BoxLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */