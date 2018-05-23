package java.awt.geom;

import java.io.Serializable;

public abstract class Ellipse2D
  extends RectangularShape
{
  protected Ellipse2D() {}
  
  public boolean contains(double paramDouble1, double paramDouble2)
  {
    double d1 = getWidth();
    if (d1 <= 0.0D) {
      return false;
    }
    double d2 = (paramDouble1 - getX()) / d1 - 0.5D;
    double d3 = getHeight();
    if (d3 <= 0.0D) {
      return false;
    }
    double d4 = (paramDouble2 - getY()) / d3 - 0.5D;
    return d2 * d2 + d4 * d4 < 0.25D;
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    double d1 = getWidth();
    if (d1 <= 0.0D) {
      return false;
    }
    double d2 = (paramDouble1 - getX()) / d1 - 0.5D;
    double d3 = d2 + paramDouble3 / d1;
    double d4 = getHeight();
    if (d4 <= 0.0D) {
      return false;
    }
    double d5 = (paramDouble2 - getY()) / d4 - 0.5D;
    double d6 = d5 + paramDouble4 / d4;
    double d7;
    if (d2 > 0.0D) {
      d7 = d2;
    } else if (d3 < 0.0D) {
      d7 = d3;
    } else {
      d7 = 0.0D;
    }
    double d8;
    if (d5 > 0.0D) {
      d8 = d5;
    } else if (d6 < 0.0D) {
      d8 = d6;
    } else {
      d8 = 0.0D;
    }
    return d7 * d7 + d8 * d8 < 0.25D;
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    return (contains(paramDouble1, paramDouble2)) && (contains(paramDouble1 + paramDouble3, paramDouble2)) && (contains(paramDouble1, paramDouble2 + paramDouble4)) && (contains(paramDouble1 + paramDouble3, paramDouble2 + paramDouble4));
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform)
  {
    return new EllipseIterator(this, paramAffineTransform);
  }
  
  public int hashCode()
  {
    long l = Double.doubleToLongBits(getX());
    l += Double.doubleToLongBits(getY()) * 37L;
    l += Double.doubleToLongBits(getWidth()) * 43L;
    l += Double.doubleToLongBits(getHeight()) * 47L;
    return (int)l ^ (int)(l >> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Ellipse2D))
    {
      Ellipse2D localEllipse2D = (Ellipse2D)paramObject;
      return (getX() == localEllipse2D.getX()) && (getY() == localEllipse2D.getY()) && (getWidth() == localEllipse2D.getWidth()) && (getHeight() == localEllipse2D.getHeight());
    }
    return false;
  }
  
  public static class Double
    extends Ellipse2D
    implements Serializable
  {
    public double x;
    public double y;
    public double width;
    public double height;
    private static final long serialVersionUID = 5555464816372320683L;
    
    public Double() {}
    
    public Double(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      setFrame(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    }
    
    public double getX()
    {
      return x;
    }
    
    public double getY()
    {
      return y;
    }
    
    public double getWidth()
    {
      return width;
    }
    
    public double getHeight()
    {
      return height;
    }
    
    public boolean isEmpty()
    {
      return (width <= 0.0D) || (height <= 0.0D);
    }
    
    public void setFrame(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      x = paramDouble1;
      y = paramDouble2;
      width = paramDouble3;
      height = paramDouble4;
    }
    
    public Rectangle2D getBounds2D()
    {
      return new Rectangle2D.Double(x, y, width, height);
    }
  }
  
  public static class Float
    extends Ellipse2D
    implements Serializable
  {
    public float x;
    public float y;
    public float width;
    public float height;
    private static final long serialVersionUID = -6633761252372475977L;
    
    public Float() {}
    
    public Float(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      setFrame(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    }
    
    public double getX()
    {
      return x;
    }
    
    public double getY()
    {
      return y;
    }
    
    public double getWidth()
    {
      return width;
    }
    
    public double getHeight()
    {
      return height;
    }
    
    public boolean isEmpty()
    {
      return (width <= 0.0D) || (height <= 0.0D);
    }
    
    public void setFrame(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      x = paramFloat1;
      y = paramFloat2;
      width = paramFloat3;
      height = paramFloat4;
    }
    
    public void setFrame(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      x = ((float)paramDouble1);
      y = ((float)paramDouble2);
      width = ((float)paramDouble3);
      height = ((float)paramDouble4);
    }
    
    public Rectangle2D getBounds2D()
    {
      return new Rectangle2D.Float(x, y, width, height);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\Ellipse2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */