package java.awt;

import java.awt.geom.Dimension2D;
import java.beans.Transient;
import java.io.Serializable;

public class Dimension
  extends Dimension2D
  implements Serializable
{
  public int width;
  public int height;
  private static final long serialVersionUID = 4723952579491349524L;
  
  private static native void initIDs();
  
  public Dimension()
  {
    this(0, 0);
  }
  
  public Dimension(Dimension paramDimension)
  {
    this(width, height);
  }
  
  public Dimension(int paramInt1, int paramInt2)
  {
    width = paramInt1;
    height = paramInt2;
  }
  
  public double getWidth()
  {
    return width;
  }
  
  public double getHeight()
  {
    return height;
  }
  
  public void setSize(double paramDouble1, double paramDouble2)
  {
    width = ((int)Math.ceil(paramDouble1));
    height = ((int)Math.ceil(paramDouble2));
  }
  
  @Transient
  public Dimension getSize()
  {
    return new Dimension(width, height);
  }
  
  public void setSize(Dimension paramDimension)
  {
    setSize(width, height);
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    width = paramInt1;
    height = paramInt2;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Dimension))
    {
      Dimension localDimension = (Dimension)paramObject;
      return (width == width) && (height == height);
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = width + height;
    return i * (i + 1) / 2 + width;
  }
  
  public String toString()
  {
    return getClass().getName() + "[width=" + width + ",height=" + height + "]";
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Dimension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */