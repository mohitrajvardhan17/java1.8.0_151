package java.awt.geom;

public abstract class Dimension2D
  implements Cloneable
{
  protected Dimension2D() {}
  
  public abstract double getWidth();
  
  public abstract double getHeight();
  
  public abstract void setSize(double paramDouble1, double paramDouble2);
  
  public void setSize(Dimension2D paramDimension2D)
  {
    setSize(paramDimension2D.getWidth(), paramDimension2D.getHeight());
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\Dimension2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */