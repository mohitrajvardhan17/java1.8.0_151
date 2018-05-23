package java.awt;

import java.awt.geom.Point2D;
import java.beans.Transient;
import java.io.Serializable;

public class Point
  extends Point2D
  implements Serializable
{
  public int x;
  public int y;
  private static final long serialVersionUID = -5276940640259749850L;
  
  public Point()
  {
    this(0, 0);
  }
  
  public Point(Point paramPoint)
  {
    this(x, y);
  }
  
  public Point(int paramInt1, int paramInt2)
  {
    x = paramInt1;
    y = paramInt2;
  }
  
  public double getX()
  {
    return x;
  }
  
  public double getY()
  {
    return y;
  }
  
  @Transient
  public Point getLocation()
  {
    return new Point(x, y);
  }
  
  public void setLocation(Point paramPoint)
  {
    setLocation(x, y);
  }
  
  public void setLocation(int paramInt1, int paramInt2)
  {
    move(paramInt1, paramInt2);
  }
  
  public void setLocation(double paramDouble1, double paramDouble2)
  {
    x = ((int)Math.floor(paramDouble1 + 0.5D));
    y = ((int)Math.floor(paramDouble2 + 0.5D));
  }
  
  public void move(int paramInt1, int paramInt2)
  {
    x = paramInt1;
    y = paramInt2;
  }
  
  public void translate(int paramInt1, int paramInt2)
  {
    x += paramInt1;
    y += paramInt2;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Point))
    {
      Point localPoint = (Point)paramObject;
      return (x == x) && (y == y);
    }
    return super.equals(paramObject);
  }
  
  public String toString()
  {
    return getClass().getName() + "[x=" + x + ",y=" + y + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Point.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */