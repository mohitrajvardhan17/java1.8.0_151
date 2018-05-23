package java.awt.geom;

import java.io.Serializable;

public abstract class Point2D
  implements Cloneable
{
  protected Point2D() {}
  
  public abstract double getX();
  
  public abstract double getY();
  
  public abstract void setLocation(double paramDouble1, double paramDouble2);
  
  public void setLocation(Point2D paramPoint2D)
  {
    setLocation(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public static double distanceSq(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    paramDouble1 -= paramDouble3;
    paramDouble2 -= paramDouble4;
    return paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2;
  }
  
  public static double distance(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    paramDouble1 -= paramDouble3;
    paramDouble2 -= paramDouble4;
    return Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2);
  }
  
  public double distanceSq(double paramDouble1, double paramDouble2)
  {
    paramDouble1 -= getX();
    paramDouble2 -= getY();
    return paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2;
  }
  
  public double distanceSq(Point2D paramPoint2D)
  {
    double d1 = paramPoint2D.getX() - getX();
    double d2 = paramPoint2D.getY() - getY();
    return d1 * d1 + d2 * d2;
  }
  
  public double distance(double paramDouble1, double paramDouble2)
  {
    paramDouble1 -= getX();
    paramDouble2 -= getY();
    return Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2);
  }
  
  public double distance(Point2D paramPoint2D)
  {
    double d1 = paramPoint2D.getX() - getX();
    double d2 = paramPoint2D.getY() - getY();
    return Math.sqrt(d1 * d1 + d2 * d2);
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
  
  public int hashCode()
  {
    long l = Double.doubleToLongBits(getX());
    l ^= Double.doubleToLongBits(getY()) * 31L;
    return (int)l ^ (int)(l >> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Point2D))
    {
      Point2D localPoint2D = (Point2D)paramObject;
      return (getX() == localPoint2D.getX()) && (getY() == localPoint2D.getY());
    }
    return super.equals(paramObject);
  }
  
  public static class Double
    extends Point2D
    implements Serializable
  {
    public double x;
    public double y;
    private static final long serialVersionUID = 6150783262733311327L;
    
    public Double() {}
    
    public Double(double paramDouble1, double paramDouble2)
    {
      x = paramDouble1;
      y = paramDouble2;
    }
    
    public double getX()
    {
      return x;
    }
    
    public double getY()
    {
      return y;
    }
    
    public void setLocation(double paramDouble1, double paramDouble2)
    {
      x = paramDouble1;
      y = paramDouble2;
    }
    
    public String toString()
    {
      return "Point2D.Double[" + x + ", " + y + "]";
    }
  }
  
  public static class Float
    extends Point2D
    implements Serializable
  {
    public float x;
    public float y;
    private static final long serialVersionUID = -2870572449815403710L;
    
    public Float() {}
    
    public Float(float paramFloat1, float paramFloat2)
    {
      x = paramFloat1;
      y = paramFloat2;
    }
    
    public double getX()
    {
      return x;
    }
    
    public double getY()
    {
      return y;
    }
    
    public void setLocation(double paramDouble1, double paramDouble2)
    {
      x = ((float)paramDouble1);
      y = ((float)paramDouble2);
    }
    
    public void setLocation(float paramFloat1, float paramFloat2)
    {
      x = paramFloat1;
      y = paramFloat2;
    }
    
    public String toString()
    {
      return "Point2D.Float[" + x + ", " + y + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\Point2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */