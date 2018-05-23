package java.awt;

import java.io.Serializable;

public class BorderLayout
  implements LayoutManager2, Serializable
{
  int hgap;
  int vgap;
  Component north;
  Component west;
  Component east;
  Component south;
  Component center;
  Component firstLine;
  Component lastLine;
  Component firstItem;
  Component lastItem;
  public static final String NORTH = "North";
  public static final String SOUTH = "South";
  public static final String EAST = "East";
  public static final String WEST = "West";
  public static final String CENTER = "Center";
  public static final String BEFORE_FIRST_LINE = "First";
  public static final String AFTER_LAST_LINE = "Last";
  public static final String BEFORE_LINE_BEGINS = "Before";
  public static final String AFTER_LINE_ENDS = "After";
  public static final String PAGE_START = "First";
  public static final String PAGE_END = "Last";
  public static final String LINE_START = "Before";
  public static final String LINE_END = "After";
  private static final long serialVersionUID = -8658291919501921765L;
  
  public BorderLayout()
  {
    this(0, 0);
  }
  
  public BorderLayout(int paramInt1, int paramInt2)
  {
    hgap = paramInt1;
    vgap = paramInt2;
  }
  
  public int getHgap()
  {
    return hgap;
  }
  
  public void setHgap(int paramInt)
  {
    hgap = paramInt;
  }
  
  public int getVgap()
  {
    return vgap;
  }
  
  public void setVgap(int paramInt)
  {
    vgap = paramInt;
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject)
  {
    synchronized (paramComponent.getTreeLock())
    {
      if ((paramObject == null) || ((paramObject instanceof String))) {
        addLayoutComponent((String)paramObject, paramComponent);
      } else {
        throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
      }
    }
  }
  
  @Deprecated
  public void addLayoutComponent(String paramString, Component paramComponent)
  {
    synchronized (paramComponent.getTreeLock())
    {
      if (paramString == null) {
        paramString = "Center";
      }
      if ("Center".equals(paramString)) {
        center = paramComponent;
      } else if ("North".equals(paramString)) {
        north = paramComponent;
      } else if ("South".equals(paramString)) {
        south = paramComponent;
      } else if ("East".equals(paramString)) {
        east = paramComponent;
      } else if ("West".equals(paramString)) {
        west = paramComponent;
      } else if ("First".equals(paramString)) {
        firstLine = paramComponent;
      } else if ("Last".equals(paramString)) {
        lastLine = paramComponent;
      } else if ("Before".equals(paramString)) {
        firstItem = paramComponent;
      } else if ("After".equals(paramString)) {
        lastItem = paramComponent;
      } else {
        throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + paramString);
      }
    }
  }
  
  public void removeLayoutComponent(Component paramComponent)
  {
    synchronized (paramComponent.getTreeLock())
    {
      if (paramComponent == center) {
        center = null;
      } else if (paramComponent == north) {
        north = null;
      } else if (paramComponent == south) {
        south = null;
      } else if (paramComponent == east) {
        east = null;
      } else if (paramComponent == west) {
        west = null;
      }
      if (paramComponent == firstLine) {
        firstLine = null;
      } else if (paramComponent == lastLine) {
        lastLine = null;
      } else if (paramComponent == firstItem) {
        firstItem = null;
      } else if (paramComponent == lastItem) {
        lastItem = null;
      }
    }
  }
  
  public Component getLayoutComponent(Object paramObject)
  {
    if ("Center".equals(paramObject)) {
      return center;
    }
    if ("North".equals(paramObject)) {
      return north;
    }
    if ("South".equals(paramObject)) {
      return south;
    }
    if ("West".equals(paramObject)) {
      return west;
    }
    if ("East".equals(paramObject)) {
      return east;
    }
    if ("First".equals(paramObject)) {
      return firstLine;
    }
    if ("Last".equals(paramObject)) {
      return lastLine;
    }
    if ("Before".equals(paramObject)) {
      return firstItem;
    }
    if ("After".equals(paramObject)) {
      return lastItem;
    }
    throw new IllegalArgumentException("cannot get component: unknown constraint: " + paramObject);
  }
  
  public Component getLayoutComponent(Container paramContainer, Object paramObject)
  {
    boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
    Component localComponent = null;
    if ("North".equals(paramObject))
    {
      localComponent = firstLine != null ? firstLine : north;
    }
    else if ("South".equals(paramObject))
    {
      localComponent = lastLine != null ? lastLine : south;
    }
    else if ("West".equals(paramObject))
    {
      localComponent = bool ? firstItem : lastItem;
      if (localComponent == null) {
        localComponent = west;
      }
    }
    else if ("East".equals(paramObject))
    {
      localComponent = bool ? lastItem : firstItem;
      if (localComponent == null) {
        localComponent = east;
      }
    }
    else if ("Center".equals(paramObject))
    {
      localComponent = center;
    }
    else
    {
      throw new IllegalArgumentException("cannot get component: invalid constraint: " + paramObject);
    }
    return localComponent;
  }
  
  public Object getConstraints(Component paramComponent)
  {
    if (paramComponent == null) {
      return null;
    }
    if (paramComponent == center) {
      return "Center";
    }
    if (paramComponent == north) {
      return "North";
    }
    if (paramComponent == south) {
      return "South";
    }
    if (paramComponent == west) {
      return "West";
    }
    if (paramComponent == east) {
      return "East";
    }
    if (paramComponent == firstLine) {
      return "First";
    }
    if (paramComponent == lastLine) {
      return "Last";
    }
    if (paramComponent == firstItem) {
      return "Before";
    }
    if (paramComponent == lastItem) {
      return "After";
    }
    return null;
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Dimension localDimension = new Dimension(0, 0);
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      Component localComponent = null;
      if ((localComponent = getChild("East", bool)) != null)
      {
        localObject1 = localComponent.getMinimumSize();
        width += width + hgap;
        height = Math.max(height, height);
      }
      if ((localComponent = getChild("West", bool)) != null)
      {
        localObject1 = localComponent.getMinimumSize();
        width += width + hgap;
        height = Math.max(height, height);
      }
      if ((localComponent = getChild("Center", bool)) != null)
      {
        localObject1 = localComponent.getMinimumSize();
        width += width;
        height = Math.max(height, height);
      }
      if ((localComponent = getChild("North", bool)) != null)
      {
        localObject1 = localComponent.getMinimumSize();
        width = Math.max(width, width);
        height += height + vgap;
      }
      if ((localComponent = getChild("South", bool)) != null)
      {
        localObject1 = localComponent.getMinimumSize();
        width = Math.max(width, width);
        height += height + vgap;
      }
      Object localObject1 = paramContainer.getInsets();
      width += left + right;
      height += top + bottom;
      return localDimension;
    }
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Dimension localDimension = new Dimension(0, 0);
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      Component localComponent = null;
      if ((localComponent = getChild("East", bool)) != null)
      {
        localObject1 = localComponent.getPreferredSize();
        width += width + hgap;
        height = Math.max(height, height);
      }
      if ((localComponent = getChild("West", bool)) != null)
      {
        localObject1 = localComponent.getPreferredSize();
        width += width + hgap;
        height = Math.max(height, height);
      }
      if ((localComponent = getChild("Center", bool)) != null)
      {
        localObject1 = localComponent.getPreferredSize();
        width += width;
        height = Math.max(height, height);
      }
      if ((localComponent = getChild("North", bool)) != null)
      {
        localObject1 = localComponent.getPreferredSize();
        width = Math.max(width, width);
        height += height + vgap;
      }
      if ((localComponent = getChild("South", bool)) != null)
      {
        localObject1 = localComponent.getPreferredSize();
        width = Math.max(width, width);
        height += height + vgap;
      }
      Object localObject1 = paramContainer.getInsets();
      width += left + right;
      height += top + bottom;
      return localDimension;
    }
  }
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public float getLayoutAlignmentX(Container paramContainer)
  {
    return 0.5F;
  }
  
  public float getLayoutAlignmentY(Container paramContainer)
  {
    return 0.5F;
  }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void layoutContainer(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = top;
      int j = height - bottom;
      int k = left;
      int m = width - right;
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      Component localComponent = null;
      Dimension localDimension;
      if ((localComponent = getChild("North", bool)) != null)
      {
        localComponent.setSize(m - k, height);
        localDimension = localComponent.getPreferredSize();
        localComponent.setBounds(k, i, m - k, height);
        i += height + vgap;
      }
      if ((localComponent = getChild("South", bool)) != null)
      {
        localComponent.setSize(m - k, height);
        localDimension = localComponent.getPreferredSize();
        localComponent.setBounds(k, j - height, m - k, height);
        j -= height + vgap;
      }
      if ((localComponent = getChild("East", bool)) != null)
      {
        localComponent.setSize(width, j - i);
        localDimension = localComponent.getPreferredSize();
        localComponent.setBounds(m - width, i, width, j - i);
        m -= width + hgap;
      }
      if ((localComponent = getChild("West", bool)) != null)
      {
        localComponent.setSize(width, j - i);
        localDimension = localComponent.getPreferredSize();
        localComponent.setBounds(k, i, width, j - i);
        k += width + hgap;
      }
      if ((localComponent = getChild("Center", bool)) != null) {
        localComponent.setBounds(k, i, m - k, j - i);
      }
    }
  }
  
  private Component getChild(String paramString, boolean paramBoolean)
  {
    Component localComponent = null;
    if (paramString == "North")
    {
      localComponent = firstLine != null ? firstLine : north;
    }
    else if (paramString == "South")
    {
      localComponent = lastLine != null ? lastLine : south;
    }
    else if (paramString == "West")
    {
      localComponent = paramBoolean ? firstItem : lastItem;
      if (localComponent == null) {
        localComponent = west;
      }
    }
    else if (paramString == "East")
    {
      localComponent = paramBoolean ? lastItem : firstItem;
      if (localComponent == null) {
        localComponent = east;
      }
    }
    else if (paramString == "Center")
    {
      localComponent = center;
    }
    if ((localComponent != null) && (!visible)) {
      localComponent = null;
    }
    return localComponent;
  }
  
  public String toString()
  {
    return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\BorderLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */