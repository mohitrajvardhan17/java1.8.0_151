package sun.awt.geom;

import java.awt.geom.Rectangle2D;

final class Order0
  extends Curve
{
  private double x;
  private double y;
  
  public Order0(double paramDouble1, double paramDouble2)
  {
    super(1);
    x = paramDouble1;
    y = paramDouble2;
  }
  
  public int getOrder()
  {
    return 0;
  }
  
  public double getXTop()
  {
    return x;
  }
  
  public double getYTop()
  {
    return y;
  }
  
  public double getXBot()
  {
    return x;
  }
  
  public double getYBot()
  {
    return y;
  }
  
  public double getXMin()
  {
    return x;
  }
  
  public double getXMax()
  {
    return x;
  }
  
  public double getX0()
  {
    return x;
  }
  
  public double getY0()
  {
    return y;
  }
  
  public double getX1()
  {
    return x;
  }
  
  public double getY1()
  {
    return y;
  }
  
  public double XforY(double paramDouble)
  {
    return paramDouble;
  }
  
  public double TforY(double paramDouble)
  {
    return 0.0D;
  }
  
  public double XforT(double paramDouble)
  {
    return x;
  }
  
  public double YforT(double paramDouble)
  {
    return y;
  }
  
  public double dXforT(double paramDouble, int paramInt)
  {
    return 0.0D;
  }
  
  public double dYforT(double paramDouble, int paramInt)
  {
    return 0.0D;
  }
  
  public double nextVertical(double paramDouble1, double paramDouble2)
  {
    return paramDouble2;
  }
  
  public int crossingsFor(double paramDouble1, double paramDouble2)
  {
    return 0;
  }
  
  public boolean accumulateCrossings(Crossings paramCrossings)
  {
    return (x > paramCrossings.getXLo()) && (x < paramCrossings.getXHi()) && (y > paramCrossings.getYLo()) && (y < paramCrossings.getYHi());
  }
  
  public void enlarge(Rectangle2D paramRectangle2D)
  {
    paramRectangle2D.add(x, y);
  }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt)
  {
    return this;
  }
  
  public Curve getReversedCurve()
  {
    return this;
  }
  
  public int getSegment(double[] paramArrayOfDouble)
  {
    paramArrayOfDouble[0] = x;
    paramArrayOfDouble[1] = y;
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\Order0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */