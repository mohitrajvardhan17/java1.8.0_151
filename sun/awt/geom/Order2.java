package sun.awt.geom;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

final class Order2
  extends Curve
{
  private double x0;
  private double y0;
  private double cx0;
  private double cy0;
  private double x1;
  private double y1;
  private double xmin;
  private double xmax;
  private double xcoeff0;
  private double xcoeff1;
  private double xcoeff2;
  private double ycoeff0;
  private double ycoeff1;
  private double ycoeff2;
  
  public static void insert(Vector paramVector, double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt)
  {
    int i = getHorizontalParams(paramDouble2, paramDouble4, paramDouble6, paramArrayOfDouble);
    if (i == 0)
    {
      addInstance(paramVector, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramInt);
      return;
    }
    double d = paramArrayOfDouble[0];
    paramArrayOfDouble[0] = paramDouble1;
    paramArrayOfDouble[1] = paramDouble2;
    paramArrayOfDouble[2] = paramDouble3;
    paramArrayOfDouble[3] = paramDouble4;
    paramArrayOfDouble[4] = paramDouble5;
    paramArrayOfDouble[5] = paramDouble6;
    split(paramArrayOfDouble, 0, d);
    int j = paramInt == 1 ? 0 : 4;
    int k = 4 - j;
    addInstance(paramVector, paramArrayOfDouble[j], paramArrayOfDouble[(j + 1)], paramArrayOfDouble[(j + 2)], paramArrayOfDouble[(j + 3)], paramArrayOfDouble[(j + 4)], paramArrayOfDouble[(j + 5)], paramInt);
    addInstance(paramVector, paramArrayOfDouble[k], paramArrayOfDouble[(k + 1)], paramArrayOfDouble[(k + 2)], paramArrayOfDouble[(k + 3)], paramArrayOfDouble[(k + 4)], paramArrayOfDouble[(k + 5)], paramInt);
  }
  
  public static void addInstance(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt)
  {
    if (paramDouble2 > paramDouble6) {
      paramVector.add(new Order2(paramDouble5, paramDouble6, paramDouble3, paramDouble4, paramDouble1, paramDouble2, -paramInt));
    } else if (paramDouble6 > paramDouble2) {
      paramVector.add(new Order2(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramInt));
    }
  }
  
  public static int getHorizontalParams(double paramDouble1, double paramDouble2, double paramDouble3, double[] paramArrayOfDouble)
  {
    if ((paramDouble1 <= paramDouble2) && (paramDouble2 <= paramDouble3)) {
      return 0;
    }
    paramDouble1 -= paramDouble2;
    paramDouble3 -= paramDouble2;
    double d1 = paramDouble1 + paramDouble3;
    if (d1 == 0.0D) {
      return 0;
    }
    double d2 = paramDouble1 / d1;
    if ((d2 <= 0.0D) || (d2 >= 1.0D)) {
      return 0;
    }
    paramArrayOfDouble[0] = d2;
    return 1;
  }
  
  public static void split(double[] paramArrayOfDouble, int paramInt, double paramDouble)
  {
    paramArrayOfDouble[(paramInt + 8)] = (d5 = paramArrayOfDouble[(paramInt + 4)]);
    paramArrayOfDouble[(paramInt + 9)] = (d6 = paramArrayOfDouble[(paramInt + 5)]);
    double d3 = paramArrayOfDouble[(paramInt + 2)];
    double d4 = paramArrayOfDouble[(paramInt + 3)];
    double d5 = d3 + (d5 - d3) * paramDouble;
    double d6 = d4 + (d6 - d4) * paramDouble;
    double d1 = paramArrayOfDouble[(paramInt + 0)];
    double d2 = paramArrayOfDouble[(paramInt + 1)];
    d1 += (d3 - d1) * paramDouble;
    d2 += (d4 - d2) * paramDouble;
    d3 = d1 + (d5 - d1) * paramDouble;
    d4 = d2 + (d6 - d2) * paramDouble;
    paramArrayOfDouble[(paramInt + 2)] = d1;
    paramArrayOfDouble[(paramInt + 3)] = d2;
    paramArrayOfDouble[(paramInt + 4)] = d3;
    paramArrayOfDouble[(paramInt + 5)] = d4;
    paramArrayOfDouble[(paramInt + 6)] = d5;
    paramArrayOfDouble[(paramInt + 7)] = d6;
  }
  
  public Order2(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt)
  {
    super(paramInt);
    if (paramDouble4 < paramDouble2) {
      paramDouble4 = paramDouble2;
    } else if (paramDouble4 > paramDouble6) {
      paramDouble4 = paramDouble6;
    }
    x0 = paramDouble1;
    y0 = paramDouble2;
    cx0 = paramDouble3;
    cy0 = paramDouble4;
    x1 = paramDouble5;
    y1 = paramDouble6;
    xmin = Math.min(Math.min(paramDouble1, paramDouble5), paramDouble3);
    xmax = Math.max(Math.max(paramDouble1, paramDouble5), paramDouble3);
    xcoeff0 = paramDouble1;
    xcoeff1 = (paramDouble3 + paramDouble3 - paramDouble1 - paramDouble1);
    xcoeff2 = (paramDouble1 - paramDouble3 - paramDouble3 + paramDouble5);
    ycoeff0 = paramDouble2;
    ycoeff1 = (paramDouble4 + paramDouble4 - paramDouble2 - paramDouble2);
    ycoeff2 = (paramDouble2 - paramDouble4 - paramDouble4 + paramDouble6);
  }
  
  public int getOrder()
  {
    return 2;
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
  
  public double getCX0()
  {
    return cx0;
  }
  
  public double getCY0()
  {
    return cy0;
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
    if (paramDouble <= y0) {
      return x0;
    }
    if (paramDouble >= y1) {
      return x1;
    }
    return XforT(TforY(paramDouble));
  }
  
  public double TforY(double paramDouble)
  {
    if (paramDouble <= y0) {
      return 0.0D;
    }
    if (paramDouble >= y1) {
      return 1.0D;
    }
    return TforY(paramDouble, ycoeff0, ycoeff1, ycoeff2);
  }
  
  public static double TforY(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    paramDouble2 -= paramDouble1;
    if (paramDouble4 == 0.0D)
    {
      d1 = -paramDouble2 / paramDouble3;
      if ((d1 >= 0.0D) && (d1 <= 1.0D)) {
        return d1;
      }
    }
    else
    {
      d1 = paramDouble3 * paramDouble3 - 4.0D * paramDouble4 * paramDouble2;
      if (d1 >= 0.0D)
      {
        d1 = Math.sqrt(d1);
        if (paramDouble3 < 0.0D) {
          d1 = -d1;
        }
        d2 = (paramDouble3 + d1) / -2.0D;
        double d3 = d2 / paramDouble4;
        if ((d3 >= 0.0D) && (d3 <= 1.0D)) {
          return d3;
        }
        if (d2 != 0.0D)
        {
          d3 = paramDouble2 / d2;
          if ((d3 >= 0.0D) && (d3 <= 1.0D)) {
            return d3;
          }
        }
      }
    }
    double d1 = paramDouble2;
    double d2 = paramDouble2 + paramDouble3 + paramDouble4;
    return 0.0D < (d1 + d2) / 2.0D ? 0.0D : 1.0D;
  }
  
  public double XforT(double paramDouble)
  {
    return (xcoeff2 * paramDouble + xcoeff1) * paramDouble + xcoeff0;
  }
  
  public double YforT(double paramDouble)
  {
    return (ycoeff2 * paramDouble + ycoeff1) * paramDouble + ycoeff0;
  }
  
  public double dXforT(double paramDouble, int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return (xcoeff2 * paramDouble + xcoeff1) * paramDouble + xcoeff0;
    case 1: 
      return 2.0D * xcoeff2 * paramDouble + xcoeff1;
    case 2: 
      return 2.0D * xcoeff2;
    }
    return 0.0D;
  }
  
  public double dYforT(double paramDouble, int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return (ycoeff2 * paramDouble + ycoeff1) * paramDouble + ycoeff0;
    case 1: 
      return 2.0D * ycoeff2 * paramDouble + ycoeff1;
    case 2: 
      return 2.0D * ycoeff2;
    }
    return 0.0D;
  }
  
  public double nextVertical(double paramDouble1, double paramDouble2)
  {
    double d = -xcoeff1 / (2.0D * xcoeff2);
    if ((d > paramDouble1) && (d < paramDouble2)) {
      return d;
    }
    return paramDouble2;
  }
  
  public void enlarge(Rectangle2D paramRectangle2D)
  {
    paramRectangle2D.add(x0, y0);
    double d = -xcoeff1 / (2.0D * xcoeff2);
    if ((d > 0.0D) && (d < 1.0D)) {
      paramRectangle2D.add(XforT(d), YforT(d));
    }
    paramRectangle2D.add(x1, y1);
  }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt)
  {
    double d1;
    if (paramDouble1 <= y0)
    {
      if (paramDouble2 >= y1) {
        return getWithDirection(paramInt);
      }
      d1 = 0.0D;
    }
    else
    {
      d1 = TforY(paramDouble1, ycoeff0, ycoeff1, ycoeff2);
    }
    double d2;
    if (paramDouble2 >= y1) {
      d2 = 1.0D;
    } else {
      d2 = TforY(paramDouble2, ycoeff0, ycoeff1, ycoeff2);
    }
    double[] arrayOfDouble = new double[10];
    arrayOfDouble[0] = x0;
    arrayOfDouble[1] = y0;
    arrayOfDouble[2] = cx0;
    arrayOfDouble[3] = cy0;
    arrayOfDouble[4] = x1;
    arrayOfDouble[5] = y1;
    if (d2 < 1.0D) {
      split(arrayOfDouble, 0, d2);
    }
    int i;
    if (d1 <= 0.0D)
    {
      i = 0;
    }
    else
    {
      split(arrayOfDouble, 0, d1 / d2);
      i = 4;
    }
    return new Order2(arrayOfDouble[(i + 0)], paramDouble1, arrayOfDouble[(i + 2)], arrayOfDouble[(i + 3)], arrayOfDouble[(i + 4)], paramDouble2, paramInt);
  }
  
  public Curve getReversedCurve()
  {
    return new Order2(x0, y0, cx0, cy0, x1, y1, -direction);
  }
  
  public int getSegment(double[] paramArrayOfDouble)
  {
    paramArrayOfDouble[0] = cx0;
    paramArrayOfDouble[1] = cy0;
    if (direction == 1)
    {
      paramArrayOfDouble[2] = x1;
      paramArrayOfDouble[3] = y1;
    }
    else
    {
      paramArrayOfDouble[2] = x0;
      paramArrayOfDouble[3] = y0;
    }
    return 2;
  }
  
  public String controlPointString()
  {
    return "(" + round(cx0) + ", " + round(cy0) + "), ";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\Order2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */