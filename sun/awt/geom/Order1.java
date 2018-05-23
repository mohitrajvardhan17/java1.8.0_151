package sun.awt.geom;

import java.awt.geom.Rectangle2D;

final class Order1
  extends Curve
{
  private double x0;
  private double y0;
  private double x1;
  private double y1;
  private double xmin;
  private double xmax;
  
  public Order1(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt)
  {
    super(paramInt);
    x0 = paramDouble1;
    y0 = paramDouble2;
    x1 = paramDouble3;
    y1 = paramDouble4;
    if (paramDouble1 < paramDouble3)
    {
      xmin = paramDouble1;
      xmax = paramDouble3;
    }
    else
    {
      xmin = paramDouble3;
      xmax = paramDouble1;
    }
  }
  
  public int getOrder()
  {
    return 1;
  }
  
  public double getXTop()
  {
    return x0;
  }
  
  public double getYTop()
  {
    return y0;
  }
  
  public double getXBot()
  {
    return x1;
  }
  
  public double getYBot()
  {
    return y1;
  }
  
  public double getXMin()
  {
    return xmin;
  }
  
  public double getXMax()
  {
    return xmax;
  }
  
  public double getX0()
  {
    return direction == 1 ? x0 : x1;
  }
  
  public double getY0()
  {
    return direction == 1 ? y0 : y1;
  }
  
  public double getX1()
  {
    return direction == -1 ? x0 : x1;
  }
  
  public double getY1()
  {
    return direction == -1 ? y0 : y1;
  }
  
  public double XforY(double paramDouble)
  {
    if ((x0 == x1) || (paramDouble <= y0)) {
      return x0;
    }
    if (paramDouble >= y1) {
      return x1;
    }
    return x0 + (paramDouble - y0) * (x1 - x0) / (y1 - y0);
  }
  
  public double TforY(double paramDouble)
  {
    if (paramDouble <= y0) {
      return 0.0D;
    }
    if (paramDouble >= y1) {
      return 1.0D;
    }
    return (paramDouble - y0) / (y1 - y0);
  }
  
  public double XforT(double paramDouble)
  {
    return x0 + paramDouble * (x1 - x0);
  }
  
  public double YforT(double paramDouble)
  {
    return y0 + paramDouble * (y1 - y0);
  }
  
  public double dXforT(double paramDouble, int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return x0 + paramDouble * (x1 - x0);
    case 1: 
      return x1 - x0;
    }
    return 0.0D;
  }
  
  public double dYforT(double paramDouble, int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return y0 + paramDouble * (y1 - y0);
    case 1: 
      return y1 - y0;
    }
    return 0.0D;
  }
  
  public double nextVertical(double paramDouble1, double paramDouble2)
  {
    return paramDouble2;
  }
  
  public boolean accumulateCrossings(Crossings paramCrossings)
  {
    double d1 = paramCrossings.getXLo();
    double d2 = paramCrossings.getYLo();
    double d3 = paramCrossings.getXHi();
    double d4 = paramCrossings.getYHi();
    if (xmin >= d3) {
      return false;
    }
    double d6;
    double d5;
    if (y0 < d2)
    {
      if (y1 <= d2) {
        return false;
      }
      d6 = d2;
      d5 = XforY(d2);
    }
    else
    {
      if (y0 >= d4) {
        return false;
      }
      d6 = y0;
      d5 = x0;
    }
    double d8;
    double d7;
    if (y1 > d4)
    {
      d8 = d4;
      d7 = XforY(d4);
    }
    else
    {
      d8 = y1;
      d7 = x1;
    }
    if ((d5 >= d3) && (d7 >= d3)) {
      return false;
    }
    if ((d5 > d1) || (d7 > d1)) {
      return true;
    }
    paramCrossings.record(d6, d8, direction);
    return false;
  }
  
  public void enlarge(Rectangle2D paramRectangle2D)
  {
    paramRectangle2D.add(x0, y0);
    paramRectangle2D.add(x1, y1);
  }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt)
  {
    if ((paramDouble1 == y0) && (paramDouble2 == y1)) {
      return getWithDirection(paramInt);
    }
    if (x0 == x1) {
      return new Order1(x0, paramDouble1, x1, paramDouble2, paramInt);
    }
    double d1 = x0 - x1;
    double d2 = y0 - y1;
    double d3 = x0 + (paramDouble1 - y0) * d1 / d2;
    double d4 = x0 + (paramDouble2 - y0) * d1 / d2;
    return new Order1(d3, paramDouble1, d4, paramDouble2, paramInt);
  }
  
  public Curve getReversedCurve()
  {
    return new Order1(x0, y0, x1, y1, -direction);
  }
  
  public int compareTo(Curve paramCurve, double[] paramArrayOfDouble)
  {
    if (!(paramCurve instanceof Order1)) {
      return super.compareTo(paramCurve, paramArrayOfDouble);
    }
    Order1 localOrder1 = (Order1)paramCurve;
    if (paramArrayOfDouble[1] <= paramArrayOfDouble[0]) {
      throw new InternalError("yrange already screwed up...");
    }
    paramArrayOfDouble[1] = Math.min(Math.min(paramArrayOfDouble[1], y1), y1);
    if (paramArrayOfDouble[1] <= paramArrayOfDouble[0]) {
      throw new InternalError("backstepping from " + paramArrayOfDouble[0] + " to " + paramArrayOfDouble[1]);
    }
    if (xmax <= xmin) {
      return xmin == xmax ? 0 : -1;
    }
    if (xmin >= xmax) {
      return 1;
    }
    double d1 = x1 - x0;
    double d2 = y1 - y0;
    double d3 = x1 - x0;
    double d4 = y1 - y0;
    double d5 = d3 * d2 - d1 * d4;
    double d6;
    if (d5 != 0.0D)
    {
      double d7 = (x0 - x0) * d2 * d4 - y0 * d1 * d4 + y0 * d3 * d2;
      d6 = d7 / d5;
      if (d6 <= paramArrayOfDouble[0])
      {
        d6 = Math.min(y1, y1);
      }
      else
      {
        if (d6 < paramArrayOfDouble[1]) {
          paramArrayOfDouble[1] = d6;
        }
        d6 = Math.max(y0, y0);
      }
    }
    else
    {
      d6 = Math.max(y0, y0);
    }
    return orderof(XforY(d6), localOrder1.XforY(d6));
  }
  
  public int getSegment(double[] paramArrayOfDouble)
  {
    if (direction == 1)
    {
      paramArrayOfDouble[0] = x1;
      paramArrayOfDouble[1] = y1;
    }
    else
    {
      paramArrayOfDouble[0] = x0;
      paramArrayOfDouble[1] = y0;
    }
    return 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\Order1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */