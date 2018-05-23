package sun.awt.geom;

import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

final class Order3
  extends Curve
{
  private double x0;
  private double y0;
  private double cx0;
  private double cy0;
  private double cx1;
  private double cy1;
  private double x1;
  private double y1;
  private double xmin;
  private double xmax;
  private double xcoeff0;
  private double xcoeff1;
  private double xcoeff2;
  private double xcoeff3;
  private double ycoeff0;
  private double ycoeff1;
  private double ycoeff2;
  private double ycoeff3;
  private double TforY1;
  private double YforT1;
  private double TforY2;
  private double YforT2;
  private double TforY3;
  private double YforT3;
  
  public static void insert(Vector paramVector, double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, int paramInt)
  {
    int i = getHorizontalParams(paramDouble2, paramDouble4, paramDouble6, paramDouble8, paramArrayOfDouble);
    if (i == 0)
    {
      addInstance(paramVector, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramInt);
      return;
    }
    paramArrayOfDouble[3] = paramDouble1;
    paramArrayOfDouble[4] = paramDouble2;
    paramArrayOfDouble[5] = paramDouble3;
    paramArrayOfDouble[6] = paramDouble4;
    paramArrayOfDouble[7] = paramDouble5;
    paramArrayOfDouble[8] = paramDouble6;
    paramArrayOfDouble[9] = paramDouble7;
    paramArrayOfDouble[10] = paramDouble8;
    double d = paramArrayOfDouble[0];
    if ((i > 1) && (d > paramArrayOfDouble[1]))
    {
      paramArrayOfDouble[0] = paramArrayOfDouble[1];
      paramArrayOfDouble[1] = d;
      d = paramArrayOfDouble[0];
    }
    split(paramArrayOfDouble, 3, d);
    if (i > 1)
    {
      d = (paramArrayOfDouble[1] - d) / (1.0D - d);
      split(paramArrayOfDouble, 9, d);
    }
    int j = 3;
    if (paramInt == -1) {
      j += i * 6;
    }
    while (i >= 0)
    {
      addInstance(paramVector, paramArrayOfDouble[(j + 0)], paramArrayOfDouble[(j + 1)], paramArrayOfDouble[(j + 2)], paramArrayOfDouble[(j + 3)], paramArrayOfDouble[(j + 4)], paramArrayOfDouble[(j + 5)], paramArrayOfDouble[(j + 6)], paramArrayOfDouble[(j + 7)], paramInt);
      i--;
      if (paramInt == 1) {
        j += 6;
      } else {
        j -= 6;
      }
    }
  }
  
  public static void addInstance(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, int paramInt)
  {
    if (paramDouble2 > paramDouble8) {
      paramVector.add(new Order3(paramDouble7, paramDouble8, paramDouble5, paramDouble6, paramDouble3, paramDouble4, paramDouble1, paramDouble2, -paramInt));
    } else if (paramDouble8 > paramDouble2) {
      paramVector.add(new Order3(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramInt));
    }
  }
  
  public static int getHorizontalParams(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double[] paramArrayOfDouble)
  {
    if ((paramDouble1 <= paramDouble2) && (paramDouble2 <= paramDouble3) && (paramDouble3 <= paramDouble4)) {
      return 0;
    }
    paramDouble4 -= paramDouble3;
    paramDouble3 -= paramDouble2;
    paramDouble2 -= paramDouble1;
    paramArrayOfDouble[0] = paramDouble2;
    paramArrayOfDouble[1] = ((paramDouble3 - paramDouble2) * 2.0D);
    paramArrayOfDouble[2] = (paramDouble4 - paramDouble3 - paramDouble3 + paramDouble2);
    int i = QuadCurve2D.solveQuadratic(paramArrayOfDouble, paramArrayOfDouble);
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      double d = paramArrayOfDouble[k];
      if ((d > 0.0D) && (d < 1.0D))
      {
        if (j < k) {
          paramArrayOfDouble[j] = d;
        }
        j++;
      }
    }
    return j;
  }
  
  public static void split(double[] paramArrayOfDouble, int paramInt, double paramDouble)
  {
    paramArrayOfDouble[(paramInt + 12)] = (d7 = paramArrayOfDouble[(paramInt + 6)]);
    paramArrayOfDouble[(paramInt + 13)] = (d8 = paramArrayOfDouble[(paramInt + 7)]);
    double d5 = paramArrayOfDouble[(paramInt + 4)];
    double d6 = paramArrayOfDouble[(paramInt + 5)];
    double d7 = d5 + (d7 - d5) * paramDouble;
    double d8 = d6 + (d8 - d6) * paramDouble;
    double d1 = paramArrayOfDouble[(paramInt + 0)];
    double d2 = paramArrayOfDouble[(paramInt + 1)];
    double d3 = paramArrayOfDouble[(paramInt + 2)];
    double d4 = paramArrayOfDouble[(paramInt + 3)];
    d1 += (d3 - d1) * paramDouble;
    d2 += (d4 - d2) * paramDouble;
    d3 += (d5 - d3) * paramDouble;
    d4 += (d6 - d4) * paramDouble;
    d5 = d3 + (d7 - d3) * paramDouble;
    d6 = d4 + (d8 - d4) * paramDouble;
    d3 = d1 + (d3 - d1) * paramDouble;
    d4 = d2 + (d4 - d2) * paramDouble;
    paramArrayOfDouble[(paramInt + 2)] = d1;
    paramArrayOfDouble[(paramInt + 3)] = d2;
    paramArrayOfDouble[(paramInt + 4)] = d3;
    paramArrayOfDouble[(paramInt + 5)] = d4;
    paramArrayOfDouble[(paramInt + 6)] = (d3 + (d5 - d3) * paramDouble);
    paramArrayOfDouble[(paramInt + 7)] = (d4 + (d6 - d4) * paramDouble);
    paramArrayOfDouble[(paramInt + 8)] = d5;
    paramArrayOfDouble[(paramInt + 9)] = d6;
    paramArrayOfDouble[(paramInt + 10)] = d7;
    paramArrayOfDouble[(paramInt + 11)] = d8;
  }
  
  public Order3(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, int paramInt)
  {
    super(paramInt);
    if (paramDouble4 < paramDouble2) {
      paramDouble4 = paramDouble2;
    }
    if (paramDouble6 > paramDouble8) {
      paramDouble6 = paramDouble8;
    }
    x0 = paramDouble1;
    y0 = paramDouble2;
    cx0 = paramDouble3;
    cy0 = paramDouble4;
    cx1 = paramDouble5;
    cy1 = paramDouble6;
    x1 = paramDouble7;
    y1 = paramDouble8;
    xmin = Math.min(Math.min(paramDouble1, paramDouble7), Math.min(paramDouble3, paramDouble5));
    xmax = Math.max(Math.max(paramDouble1, paramDouble7), Math.max(paramDouble3, paramDouble5));
    xcoeff0 = paramDouble1;
    xcoeff1 = ((paramDouble3 - paramDouble1) * 3.0D);
    xcoeff2 = ((paramDouble5 - paramDouble3 - paramDouble3 + paramDouble1) * 3.0D);
    xcoeff3 = (paramDouble7 - (paramDouble5 - paramDouble3) * 3.0D - paramDouble1);
    ycoeff0 = paramDouble2;
    ycoeff1 = ((paramDouble4 - paramDouble2) * 3.0D);
    ycoeff2 = ((paramDouble6 - paramDouble4 - paramDouble4 + paramDouble2) * 3.0D);
    ycoeff3 = (paramDouble8 - (paramDouble6 - paramDouble4) * 3.0D - paramDouble2);
    YforT1 = (YforT2 = YforT3 = paramDouble2);
  }
  
  public int getOrder()
  {
    return 3;
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
    return direction == 1 ? cx0 : cx1;
  }
  
  public double getCY0()
  {
    return direction == 1 ? cy0 : cy1;
  }
  
  public double getCX1()
  {
    return direction == -1 ? cx0 : cx1;
  }
  
  public double getCY1()
  {
    return direction == -1 ? cy0 : cy1;
  }
  
  public double getX1()
  {
    return direction == -1 ? x0 : x1;
  }
  
  public double getY1()
  {
    return direction == -1 ? y0 : y1;
  }
  
  public double TforY(double paramDouble)
  {
    if (paramDouble <= y0) {
      return 0.0D;
    }
    if (paramDouble >= y1) {
      return 1.0D;
    }
    if (paramDouble == YforT1) {
      return TforY1;
    }
    if (paramDouble == YforT2) {
      return TforY2;
    }
    if (paramDouble == YforT3) {
      return TforY3;
    }
    if (ycoeff3 == 0.0D) {
      return Order2.TforY(paramDouble, ycoeff0, ycoeff1, ycoeff2);
    }
    double d1 = ycoeff2 / ycoeff3;
    double d2 = ycoeff1 / ycoeff3;
    double d3 = (ycoeff0 - paramDouble) / ycoeff3;
    int i = 0;
    double d4 = (d1 * d1 - 3.0D * d2) / 9.0D;
    double d5 = (2.0D * d1 * d1 * d1 - 9.0D * d1 * d2 + 27.0D * d3) / 54.0D;
    double d6 = d5 * d5;
    double d7 = d4 * d4 * d4;
    double d8 = d1 / 3.0D;
    double d9;
    if (d6 < d7)
    {
      double d10 = Math.acos(d5 / Math.sqrt(d7));
      d4 = -2.0D * Math.sqrt(d4);
      d9 = refine(d1, d2, d3, paramDouble, d4 * Math.cos(d10 / 3.0D) - d8);
      if (d9 < 0.0D) {
        d9 = refine(d1, d2, d3, paramDouble, d4 * Math.cos((d10 + 6.283185307179586D) / 3.0D) - d8);
      }
      if (d9 < 0.0D) {
        d9 = refine(d1, d2, d3, paramDouble, d4 * Math.cos((d10 - 6.283185307179586D) / 3.0D) - d8);
      }
    }
    else
    {
      int j = d5 < 0.0D ? 1 : 0;
      double d12 = Math.sqrt(d6 - d7);
      if (j != 0) {
        d5 = -d5;
      }
      double d14 = Math.pow(d5 + d12, 0.3333333333333333D);
      if (j == 0) {
        d14 = -d14;
      }
      double d16 = d14 == 0.0D ? 0.0D : d4 / d14;
      d9 = refine(d1, d2, d3, paramDouble, d14 + d16 - d8);
    }
    if (d9 < 0.0D)
    {
      double d11 = 0.0D;
      double d13 = 1.0D;
      for (;;)
      {
        d9 = (d11 + d13) / 2.0D;
        if ((d9 == d11) || (d9 == d13)) {
          break;
        }
        double d15 = YforT(d9);
        if (d15 < paramDouble)
        {
          d11 = d9;
        }
        else
        {
          if (d15 <= paramDouble) {
            break;
          }
          d13 = d9;
        }
      }
    }
    if (d9 >= 0.0D)
    {
      TforY3 = TforY2;
      YforT3 = YforT2;
      TforY2 = TforY1;
      YforT2 = YforT1;
      TforY1 = d9;
      YforT1 = paramDouble;
    }
    return d9;
  }
  
  public double refine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    if ((paramDouble5 < -0.1D) || (paramDouble5 > 1.1D)) {
      return -1.0D;
    }
    double d1 = YforT(paramDouble5);
    double d2;
    double d3;
    if (d1 < paramDouble4)
    {
      d2 = paramDouble5;
      d3 = 1.0D;
    }
    else
    {
      d2 = 0.0D;
      d3 = paramDouble5;
    }
    double d4 = paramDouble5;
    double d5 = d1;
    int i = 1;
    while (d1 != paramDouble4)
    {
      double d6;
      if (i == 0)
      {
        d6 = (d2 + d3) / 2.0D;
        if ((d6 == d2) || (d6 == d3)) {
          break;
        }
        paramDouble5 = d6;
      }
      else
      {
        d6 = dYforT(paramDouble5, 1);
        if (d6 == 0.0D)
        {
          i = 0;
          continue;
        }
        double d7 = paramDouble5 + (paramDouble4 - d1) / d6;
        if ((d7 == paramDouble5) || (d7 <= d2) || (d7 >= d3))
        {
          i = 0;
          continue;
        }
        paramDouble5 = d7;
      }
      d1 = YforT(paramDouble5);
      if (d1 < paramDouble4)
      {
        d2 = paramDouble5;
      }
      else
      {
        if (d1 <= paramDouble4) {
          break;
        }
        d3 = paramDouble5;
      }
    }
    int j = 0;
    return paramDouble5 > 1.0D ? -1.0D : paramDouble5;
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
  
  public double XforT(double paramDouble)
  {
    return ((xcoeff3 * paramDouble + xcoeff2) * paramDouble + xcoeff1) * paramDouble + xcoeff0;
  }
  
  public double YforT(double paramDouble)
  {
    return ((ycoeff3 * paramDouble + ycoeff2) * paramDouble + ycoeff1) * paramDouble + ycoeff0;
  }
  
  public double dXforT(double paramDouble, int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return ((xcoeff3 * paramDouble + xcoeff2) * paramDouble + xcoeff1) * paramDouble + xcoeff0;
    case 1: 
      return (3.0D * xcoeff3 * paramDouble + 2.0D * xcoeff2) * paramDouble + xcoeff1;
    case 2: 
      return 6.0D * xcoeff3 * paramDouble + 2.0D * xcoeff2;
    case 3: 
      return 6.0D * xcoeff3;
    }
    return 0.0D;
  }
  
  public double dYforT(double paramDouble, int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return ((ycoeff3 * paramDouble + ycoeff2) * paramDouble + ycoeff1) * paramDouble + ycoeff0;
    case 1: 
      return (3.0D * ycoeff3 * paramDouble + 2.0D * ycoeff2) * paramDouble + ycoeff1;
    case 2: 
      return 6.0D * ycoeff3 * paramDouble + 2.0D * ycoeff2;
    case 3: 
      return 6.0D * ycoeff3;
    }
    return 0.0D;
  }
  
  public double nextVertical(double paramDouble1, double paramDouble2)
  {
    double[] arrayOfDouble = { xcoeff1, 2.0D * xcoeff2, 3.0D * xcoeff3 };
    int i = QuadCurve2D.solveQuadratic(arrayOfDouble, arrayOfDouble);
    for (int j = 0; j < i; j++) {
      if ((arrayOfDouble[j] > paramDouble1) && (arrayOfDouble[j] < paramDouble2)) {
        paramDouble2 = arrayOfDouble[j];
      }
    }
    return paramDouble2;
  }
  
  public void enlarge(Rectangle2D paramRectangle2D)
  {
    paramRectangle2D.add(x0, y0);
    double[] arrayOfDouble = { xcoeff1, 2.0D * xcoeff2, 3.0D * xcoeff3 };
    int i = QuadCurve2D.solveQuadratic(arrayOfDouble, arrayOfDouble);
    for (int j = 0; j < i; j++)
    {
      double d = arrayOfDouble[j];
      if ((d > 0.0D) && (d < 1.0D)) {
        paramRectangle2D.add(XforT(d), YforT(d));
      }
    }
    paramRectangle2D.add(x1, y1);
  }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt)
  {
    if ((paramDouble1 <= y0) && (paramDouble2 >= y1)) {
      return getWithDirection(paramInt);
    }
    double[] arrayOfDouble = new double[14];
    double d1 = TforY(paramDouble1);
    double d2 = TforY(paramDouble2);
    arrayOfDouble[0] = x0;
    arrayOfDouble[1] = y0;
    arrayOfDouble[2] = cx0;
    arrayOfDouble[3] = cy0;
    arrayOfDouble[4] = cx1;
    arrayOfDouble[5] = cy1;
    arrayOfDouble[6] = x1;
    arrayOfDouble[7] = y1;
    if (d1 > d2)
    {
      double d3 = d1;
      d1 = d2;
      d2 = d3;
    }
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
      i = 6;
    }
    return new Order3(arrayOfDouble[(i + 0)], paramDouble1, arrayOfDouble[(i + 2)], arrayOfDouble[(i + 3)], arrayOfDouble[(i + 4)], arrayOfDouble[(i + 5)], arrayOfDouble[(i + 6)], paramDouble2, paramInt);
  }
  
  public Curve getReversedCurve()
  {
    return new Order3(x0, y0, cx0, cy0, cx1, cy1, x1, y1, -direction);
  }
  
  public int getSegment(double[] paramArrayOfDouble)
  {
    if (direction == 1)
    {
      paramArrayOfDouble[0] = cx0;
      paramArrayOfDouble[1] = cy0;
      paramArrayOfDouble[2] = cx1;
      paramArrayOfDouble[3] = cy1;
      paramArrayOfDouble[4] = x1;
      paramArrayOfDouble[5] = y1;
    }
    else
    {
      paramArrayOfDouble[0] = cx1;
      paramArrayOfDouble[1] = cy1;
      paramArrayOfDouble[2] = cx0;
      paramArrayOfDouble[3] = cy0;
      paramArrayOfDouble[4] = x0;
      paramArrayOfDouble[5] = y0;
    }
    return 3;
  }
  
  public String controlPointString()
  {
    return "(" + round(getCX0()) + ", " + round(getCY0()) + "), " + "(" + round(getCX1()) + ", " + round(getCY1()) + "), ";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\geom\Order3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */