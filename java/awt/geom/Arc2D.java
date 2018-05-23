package java.awt.geom;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Arc2D
  extends RectangularShape
{
  public static final int OPEN = 0;
  public static final int CHORD = 1;
  public static final int PIE = 2;
  private int type;
  
  protected Arc2D()
  {
    this(0);
  }
  
  protected Arc2D(int paramInt)
  {
    setArcType(paramInt);
  }
  
  public abstract double getAngleStart();
  
  public abstract double getAngleExtent();
  
  public int getArcType()
  {
    return type;
  }
  
  public Point2D getStartPoint()
  {
    double d1 = Math.toRadians(-getAngleStart());
    double d2 = getX() + (Math.cos(d1) * 0.5D + 0.5D) * getWidth();
    double d3 = getY() + (Math.sin(d1) * 0.5D + 0.5D) * getHeight();
    return new Point2D.Double(d2, d3);
  }
  
  public Point2D getEndPoint()
  {
    double d1 = Math.toRadians(-getAngleStart() - getAngleExtent());
    double d2 = getX() + (Math.cos(d1) * 0.5D + 0.5D) * getWidth();
    double d3 = getY() + (Math.sin(d1) * 0.5D + 0.5D) * getHeight();
    return new Point2D.Double(d2, d3);
  }
  
  public abstract void setArc(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt);
  
  public void setArc(Point2D paramPoint2D, Dimension2D paramDimension2D, double paramDouble1, double paramDouble2, int paramInt)
  {
    setArc(paramPoint2D.getX(), paramPoint2D.getY(), paramDimension2D.getWidth(), paramDimension2D.getHeight(), paramDouble1, paramDouble2, paramInt);
  }
  
  public void setArc(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, int paramInt)
  {
    setArc(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight(), paramDouble1, paramDouble2, paramInt);
  }
  
  public void setArc(Arc2D paramArc2D)
  {
    setArc(paramArc2D.getX(), paramArc2D.getY(), paramArc2D.getWidth(), paramArc2D.getHeight(), paramArc2D.getAngleStart(), paramArc2D.getAngleExtent(), type);
  }
  
  public void setArcByCenter(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, int paramInt)
  {
    setArc(paramDouble1 - paramDouble3, paramDouble2 - paramDouble3, paramDouble3 * 2.0D, paramDouble3 * 2.0D, paramDouble4, paramDouble5, paramInt);
  }
  
  public void setArcByTangent(Point2D paramPoint2D1, Point2D paramPoint2D2, Point2D paramPoint2D3, double paramDouble)
  {
    double d1 = Math.atan2(paramPoint2D1.getY() - paramPoint2D2.getY(), paramPoint2D1.getX() - paramPoint2D2.getX());
    double d2 = Math.atan2(paramPoint2D3.getY() - paramPoint2D2.getY(), paramPoint2D3.getX() - paramPoint2D2.getX());
    double d3 = d2 - d1;
    if (d3 > 3.141592653589793D) {
      d2 -= 6.283185307179586D;
    } else if (d3 < -3.141592653589793D) {
      d2 += 6.283185307179586D;
    }
    double d4 = (d1 + d2) / 2.0D;
    double d5 = Math.abs(d2 - d4);
    double d6 = paramDouble / Math.sin(d5);
    double d7 = paramPoint2D2.getX() + d6 * Math.cos(d4);
    double d8 = paramPoint2D2.getY() + d6 * Math.sin(d4);
    if (d1 < d2)
    {
      d1 -= 1.5707963267948966D;
      d2 += 1.5707963267948966D;
    }
    else
    {
      d1 += 1.5707963267948966D;
      d2 -= 1.5707963267948966D;
    }
    d1 = Math.toDegrees(-d1);
    d2 = Math.toDegrees(-d2);
    d3 = d2 - d1;
    if (d3 < 0.0D) {
      d3 += 360.0D;
    } else {
      d3 -= 360.0D;
    }
    setArcByCenter(d7, d8, paramDouble, d1, d3, type);
  }
  
  public abstract void setAngleStart(double paramDouble);
  
  public abstract void setAngleExtent(double paramDouble);
  
  public void setAngleStart(Point2D paramPoint2D)
  {
    double d1 = getHeight() * (paramPoint2D.getX() - getCenterX());
    double d2 = getWidth() * (paramPoint2D.getY() - getCenterY());
    setAngleStart(-Math.toDegrees(Math.atan2(d2, d1)));
  }
  
  public void setAngles(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d1 = getCenterX();
    double d2 = getCenterY();
    double d3 = getWidth();
    double d4 = getHeight();
    double d5 = Math.atan2(d3 * (d2 - paramDouble2), d4 * (paramDouble1 - d1));
    double d6 = Math.atan2(d3 * (d2 - paramDouble4), d4 * (paramDouble3 - d1));
    d6 -= d5;
    if (d6 <= 0.0D) {
      d6 += 6.283185307179586D;
    }
    setAngleStart(Math.toDegrees(d5));
    setAngleExtent(Math.toDegrees(d6));
  }
  
  public void setAngles(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    setAngles(paramPoint2D1.getX(), paramPoint2D1.getY(), paramPoint2D2.getX(), paramPoint2D2.getY());
  }
  
  public void setArcType(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 2)) {
      throw new IllegalArgumentException("invalid type for Arc: " + paramInt);
    }
    type = paramInt;
  }
  
  public void setFrame(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    setArc(paramDouble1, paramDouble2, paramDouble3, paramDouble4, getAngleStart(), getAngleExtent(), type);
  }
  
  public Rectangle2D getBounds2D()
  {
    if (isEmpty()) {
      return makeBounds(getX(), getY(), getWidth(), getHeight());
    }
    if (getArcType() == 2)
    {
      d1 = d2 = d3 = d4 = 0.0D;
    }
    else
    {
      d1 = d2 = 1.0D;
      d3 = d4 = -1.0D;
    }
    double d5 = 0.0D;
    for (int i = 0; i < 6; i++)
    {
      if (i < 4)
      {
        d5 += 90.0D;
        if (!containsAngle(d5)) {
          continue;
        }
      }
      else if (i == 4)
      {
        d5 = getAngleStart();
      }
      else
      {
        d5 += getAngleExtent();
      }
      double d7 = Math.toRadians(-d5);
      double d9 = Math.cos(d7);
      double d10 = Math.sin(d7);
      d1 = Math.min(d1, d9);
      d2 = Math.min(d2, d10);
      d3 = Math.max(d3, d9);
      d4 = Math.max(d4, d10);
    }
    double d6 = getWidth();
    double d8 = getHeight();
    double d3 = (d3 - d1) * 0.5D * d6;
    double d4 = (d4 - d2) * 0.5D * d8;
    double d1 = getX() + (d1 * 0.5D + 0.5D) * d6;
    double d2 = getY() + (d2 * 0.5D + 0.5D) * d8;
    return makeBounds(d1, d2, d3, d4);
  }
  
  protected abstract Rectangle2D makeBounds(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  static double normalizeDegrees(double paramDouble)
  {
    if (paramDouble > 180.0D)
    {
      if (paramDouble <= 540.0D)
      {
        paramDouble -= 360.0D;
      }
      else
      {
        paramDouble = Math.IEEEremainder(paramDouble, 360.0D);
        if (paramDouble == -180.0D) {
          paramDouble = 180.0D;
        }
      }
    }
    else if (paramDouble <= -180.0D) {
      if (paramDouble > -540.0D)
      {
        paramDouble += 360.0D;
      }
      else
      {
        paramDouble = Math.IEEEremainder(paramDouble, 360.0D);
        if (paramDouble == -180.0D) {
          paramDouble = 180.0D;
        }
      }
    }
    return paramDouble;
  }
  
  public boolean containsAngle(double paramDouble)
  {
    double d = getAngleExtent();
    int i = d < 0.0D ? 1 : 0;
    if (i != 0) {
      d = -d;
    }
    if (d >= 360.0D) {
      return true;
    }
    paramDouble = normalizeDegrees(paramDouble) - normalizeDegrees(getAngleStart());
    if (i != 0) {
      paramDouble = -paramDouble;
    }
    if (paramDouble < 0.0D) {
      paramDouble += 360.0D;
    }
    return (paramDouble >= 0.0D) && (paramDouble < d);
  }
  
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
    double d5 = d2 * d2 + d4 * d4;
    if (d5 >= 0.25D) {
      return false;
    }
    double d6 = Math.abs(getAngleExtent());
    if (d6 >= 360.0D) {
      return true;
    }
    boolean bool1 = containsAngle(-Math.toDegrees(Math.atan2(d4, d2)));
    if (type == 2) {
      return bool1;
    }
    if (bool1)
    {
      if (d6 >= 180.0D) {
        return true;
      }
    }
    else if (d6 <= 180.0D) {
      return false;
    }
    double d7 = Math.toRadians(-getAngleStart());
    double d8 = Math.cos(d7);
    double d9 = Math.sin(d7);
    d7 += Math.toRadians(-getAngleExtent());
    double d10 = Math.cos(d7);
    double d11 = Math.sin(d7);
    boolean bool2 = Line2D.relativeCCW(d8, d9, d10, d11, 2.0D * d2, 2.0D * d4) * Line2D.relativeCCW(d8, d9, d10, d11, 0.0D, 0.0D) >= 0;
    return bool1 ? false : !bool2 ? true : bool2;
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d1 = getWidth();
    double d2 = getHeight();
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D) || (d1 <= 0.0D) || (d2 <= 0.0D)) {
      return false;
    }
    double d3 = getAngleExtent();
    if (d3 == 0.0D) {
      return false;
    }
    double d4 = getX();
    double d5 = getY();
    double d6 = d4 + d1;
    double d7 = d5 + d2;
    double d8 = paramDouble1 + paramDouble3;
    double d9 = paramDouble2 + paramDouble4;
    if ((paramDouble1 >= d6) || (paramDouble2 >= d7) || (d8 <= d4) || (d9 <= d5)) {
      return false;
    }
    double d10 = getCenterX();
    double d11 = getCenterY();
    Point2D localPoint2D1 = getStartPoint();
    Point2D localPoint2D2 = getEndPoint();
    double d12 = localPoint2D1.getX();
    double d13 = localPoint2D1.getY();
    double d14 = localPoint2D2.getX();
    double d15 = localPoint2D2.getY();
    if ((d11 >= paramDouble2) && (d11 <= d9) && (((d12 < d8) && (d14 < d8) && (d10 < d8) && (d6 > paramDouble1) && (containsAngle(0.0D))) || ((d12 > paramDouble1) && (d14 > paramDouble1) && (d10 > paramDouble1) && (d4 < d8) && (containsAngle(180.0D))))) {
      return true;
    }
    if ((d10 >= paramDouble1) && (d10 <= d8) && (((d13 > paramDouble2) && (d15 > paramDouble2) && (d11 > paramDouble2) && (d5 < d9) && (containsAngle(90.0D))) || ((d13 < d9) && (d15 < d9) && (d11 < d9) && (d7 > paramDouble2) && (containsAngle(270.0D))))) {
      return true;
    }
    Rectangle2D.Double localDouble = new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    if ((type == 2) || (Math.abs(d3) > 180.0D))
    {
      if ((localDouble.intersectsLine(d10, d11, d12, d13)) || (localDouble.intersectsLine(d10, d11, d14, d15))) {
        return true;
      }
    }
    else if (localDouble.intersectsLine(d12, d13, d14, d15)) {
      return true;
    }
    return (contains(paramDouble1, paramDouble2)) || (contains(paramDouble1 + paramDouble3, paramDouble2)) || (contains(paramDouble1, paramDouble2 + paramDouble4)) || (contains(paramDouble1 + paramDouble3, paramDouble2 + paramDouble4));
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    return contains(paramDouble1, paramDouble2, paramDouble3, paramDouble4, null);
  }
  
  public boolean contains(Rectangle2D paramRectangle2D)
  {
    return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight(), paramRectangle2D);
  }
  
  private boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, Rectangle2D paramRectangle2D)
  {
    if ((!contains(paramDouble1, paramDouble2)) || (!contains(paramDouble1 + paramDouble3, paramDouble2)) || (!contains(paramDouble1, paramDouble2 + paramDouble4)) || (!contains(paramDouble1 + paramDouble3, paramDouble2 + paramDouble4))) {
      return false;
    }
    if ((type != 2) || (Math.abs(getAngleExtent()) <= 180.0D)) {
      return true;
    }
    if (paramRectangle2D == null) {
      paramRectangle2D = new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    }
    double d1 = getWidth() / 2.0D;
    double d2 = getHeight() / 2.0D;
    double d3 = getX() + d1;
    double d4 = getY() + d2;
    double d5 = Math.toRadians(-getAngleStart());
    double d6 = d3 + d1 * Math.cos(d5);
    double d7 = d4 + d2 * Math.sin(d5);
    if (paramRectangle2D.intersectsLine(d3, d4, d6, d7)) {
      return false;
    }
    d5 += Math.toRadians(-getAngleExtent());
    d6 = d3 + d1 * Math.cos(d5);
    d7 = d4 + d2 * Math.sin(d5);
    return !paramRectangle2D.intersectsLine(d3, d4, d6, d7);
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform)
  {
    return new ArcIterator(this, paramAffineTransform);
  }
  
  public int hashCode()
  {
    long l = Double.doubleToLongBits(getX());
    l += Double.doubleToLongBits(getY()) * 37L;
    l += Double.doubleToLongBits(getWidth()) * 43L;
    l += Double.doubleToLongBits(getHeight()) * 47L;
    l += Double.doubleToLongBits(getAngleStart()) * 53L;
    l += Double.doubleToLongBits(getAngleExtent()) * 59L;
    l += getArcType() * 61;
    return (int)l ^ (int)(l >> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Arc2D))
    {
      Arc2D localArc2D = (Arc2D)paramObject;
      return (getX() == localArc2D.getX()) && (getY() == localArc2D.getY()) && (getWidth() == localArc2D.getWidth()) && (getHeight() == localArc2D.getHeight()) && (getAngleStart() == localArc2D.getAngleStart()) && (getAngleExtent() == localArc2D.getAngleExtent()) && (getArcType() == localArc2D.getArcType());
    }
    return false;
  }
  
  public static class Double
    extends Arc2D
    implements Serializable
  {
    public double x;
    public double y;
    public double width;
    public double height;
    public double start;
    public double extent;
    private static final long serialVersionUID = 728264085846882001L;
    
    public Double()
    {
      super();
    }
    
    public Double(int paramInt)
    {
      super();
    }
    
    public Double(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt)
    {
      super();
      x = paramDouble1;
      y = paramDouble2;
      width = paramDouble3;
      height = paramDouble4;
      start = paramDouble5;
      extent = paramDouble6;
    }
    
    public Double(Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2, int paramInt)
    {
      super();
      x = paramRectangle2D.getX();
      y = paramRectangle2D.getY();
      width = paramRectangle2D.getWidth();
      height = paramRectangle2D.getHeight();
      start = paramDouble1;
      extent = paramDouble2;
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
    
    public double getAngleStart()
    {
      return start;
    }
    
    public double getAngleExtent()
    {
      return extent;
    }
    
    public boolean isEmpty()
    {
      return (width <= 0.0D) || (height <= 0.0D);
    }
    
    public void setArc(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt)
    {
      setArcType(paramInt);
      x = paramDouble1;
      y = paramDouble2;
      width = paramDouble3;
      height = paramDouble4;
      start = paramDouble5;
      extent = paramDouble6;
    }
    
    public void setAngleStart(double paramDouble)
    {
      start = paramDouble;
    }
    
    public void setAngleExtent(double paramDouble)
    {
      extent = paramDouble;
    }
    
    protected Rectangle2D makeBounds(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      return new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeByte(getArcType());
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
      try
      {
        setArcType(paramObjectInputStream.readByte());
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new InvalidObjectException(localIllegalArgumentException.getMessage());
      }
    }
  }
  
  public static class Float
    extends Arc2D
    implements Serializable
  {
    public float x;
    public float y;
    public float width;
    public float height;
    public float start;
    public float extent;
    private static final long serialVersionUID = 9130893014586380278L;
    
    public Float()
    {
      super();
    }
    
    public Float(int paramInt)
    {
      super();
    }
    
    public Float(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, int paramInt)
    {
      super();
      x = paramFloat1;
      y = paramFloat2;
      width = paramFloat3;
      height = paramFloat4;
      start = paramFloat5;
      extent = paramFloat6;
    }
    
    public Float(Rectangle2D paramRectangle2D, float paramFloat1, float paramFloat2, int paramInt)
    {
      super();
      x = ((float)paramRectangle2D.getX());
      y = ((float)paramRectangle2D.getY());
      width = ((float)paramRectangle2D.getWidth());
      height = ((float)paramRectangle2D.getHeight());
      start = paramFloat1;
      extent = paramFloat2;
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
    
    public double getAngleStart()
    {
      return start;
    }
    
    public double getAngleExtent()
    {
      return extent;
    }
    
    public boolean isEmpty()
    {
      return (width <= 0.0D) || (height <= 0.0D);
    }
    
    public void setArc(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt)
    {
      setArcType(paramInt);
      x = ((float)paramDouble1);
      y = ((float)paramDouble2);
      width = ((float)paramDouble3);
      height = ((float)paramDouble4);
      start = ((float)paramDouble5);
      extent = ((float)paramDouble6);
    }
    
    public void setAngleStart(double paramDouble)
    {
      start = ((float)paramDouble);
    }
    
    public void setAngleExtent(double paramDouble)
    {
      extent = ((float)paramDouble);
    }
    
    protected Rectangle2D makeBounds(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
      return new Rectangle2D.Float((float)paramDouble1, (float)paramDouble2, (float)paramDouble3, (float)paramDouble4);
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeByte(getArcType());
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
      try
      {
        setArcType(paramObjectInputStream.readByte());
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new InvalidObjectException(localIllegalArgumentException.getMessage());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\Arc2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */