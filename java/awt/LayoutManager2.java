package java.awt;

public abstract interface LayoutManager2
  extends LayoutManager
{
  public abstract void addLayoutComponent(Component paramComponent, Object paramObject);
  
  public abstract Dimension maximumLayoutSize(Container paramContainer);
  
  public abstract float getLayoutAlignmentX(Container paramContainer);
  
  public abstract float getLayoutAlignmentY(Container paramContainer);
  
  public abstract void invalidateLayout(Container paramContainer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\LayoutManager2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */