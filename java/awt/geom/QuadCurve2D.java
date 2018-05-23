package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;

public abstract class QuadCurve2D
  implements Shape, Cloneable
{
  private static final int BELOW = -2;
  private static final int LOWEDGE = -1;
  private static final int INSIDE = 0;
  private static final int HIGHEDGE = 1;
  private static final int ABOVE = 2;
  
  protected QuadCurve2D() {}
  
  public abstract double getX1();
  
  public abstract double getY1();
  
  public abstract Point2D getP1();
  
  public abstract double getCtrlX();
  
  public abstract double getCtrlY();
  
  public abstract Point2D getCtrlPt();
  
  public abstract double getX2();
  
  public abstract double getY2();
  
  public abstract Point2D getP2();
  
  public abstract void setCurve(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public void setCurve(double[] paramArrayOfDouble, int paramInt)
  {
    setCurve(paramArrayOfDouble[(paramInt + 0)], paramArrayOfDouble[(paramInt + 1)], paramArrayOfDouble[(paramInt + 2)], paramArrayOfDouble[(paramInt + 3)], paramArrayOfDouble[(paramInt + 4)], paramArrayOfDouble[(paramInt + 5)]);
  }
  
  public void setCurve(Point2D paramPoint2D1, Point2D paramPoint2D2, Point2D paramPoint2D3)
  {
    setCurve(paramPoint2D1.getX(), paramPoint2D1.getY(), paramPoint2D2.getX(), paramPoint2D2.getY(), paramPoint2D3.getX(), paramPoint2D3.getY());
  }
  
  public void setCurve(Point2D[] paramArrayOfPoint2D, int paramInt)
  {
    setCurve(paramArrayOfPoint2D[(paramInt + 0)].getX(), paramArrayOfPoint2D[(paramInt + 0)].getY(), paramArrayOfPoint2D[(paramInt + 1)].getX(), paramArrayOfPoint2D[(paramInt + 1)].getY(), paramArrayOfPoint2D[(paramInt + 2)].getX(), paramArrayOfPoint2D[(paramInt + 2)].getY());
  }
  
  public void setCurve(QuadCurve2D paramQuadCurve2D)
  {
    setCurve(paramQuadCurve2D.getX1(), paramQuadCurve2D.getY1(), paramQuadCurve2D.getCtrlX(), paramQuadCurve2D.getCtrlY(), paramQuadCurve2D.getX2(), paramQuadCurve2D.getY2());
  }
  
  public static double getFlatnessSq(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
  {
    return Line2D.ptSegDistSq(paramDouble1, paramDouble2, paramDouble5, paramDouble6, paramDouble3, paramDouble4);
  }
  
  public static double getFlatness(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
  {
    return Line2D.ptSegDist(paramDouble1, paramDouble2, paramDouble5, paramDouble6, paramDouble3, paramDouble4);
  }
  
  public static double getFlatnessSq(double[] paramArrayOfDouble, int paramInt)
  {
    return Line2D.ptSegDistSq(paramArrayOfDouble[(paramInt + 0)], paramArrayOfDouble[(paramInt + 1)], paramArrayOfDouble[(paramInt + 4)], paramArrayOfDouble[(paramInt + 5)], paramArrayOfDouble[(paramInt + 2)], paramArrayOfDouble[(paramInt + 3)]);
  }
  
  public static double getFlatness(double[] paramArrayOfDouble, int paramInt)
  {
    return Line2D.ptSegDist(paramArrayOfDouble[(paramInt + 0)], paramArrayOfDouble[(paramInt + 1)], paramArrayOfDouble[(paramInt + 4)], paramArrayOfDouble[(paramInt + 5)], paramArrayOfDouble[(paramInt + 2)], paramArrayOfDouble[(paramInt + 3)]);
  }
  
  public double getFlatnessSq()
  {
    return Line2D.ptSegDistSq(getX1(), getY1(), getX2(), getY2(), getCtrlX(), getCtrlY());
  }
  
  public double getFlatness()
  {
    return Line2D.ptSegDist(getX1(), getY1(), getX2(), getY2(), getCtrlX(), getCtrlY());
  }
  
  public void subdivide(QuadCurve2D paramQuadCurve2D1, QuadCurve2D paramQuadCurve2D2)
  {
    subdivide(this, paramQuadCurve2D1, paramQuadCurve2D2);
  }
  
  public static void subdivide(QuadCurve2D paramQuadCurve2D1, QuadCurve2D paramQuadCurve2D2, QuadCurve2D paramQuadCurve2D3)
  {
    double d1 = paramQuadCurve2D1.getX1();
    double d2 = paramQuadCurve2D1.getY1();
    double d3 = paramQuadCurve2D1.getCtrlX();
    double d4 = paramQuadCurve2D1.getCtrlY();
    double d5 = paramQuadCurve2D1.getX2();
    double d6 = paramQuadCurve2D1.getY2();
    double d7 = (d1 + d3) / 2.0D;
    double d8 = (d2 + d4) / 2.0D;
    double d9 = (d5 + d3) / 2.0D;
    double d10 = (d6 + d4) / 2.0D;
    d3 = (d7 + d9) / 2.0D;
    d4 = (d8 + d10) / 2.0D;
    if (paramQuadCurve2D2 != null) {
      paramQuadCurve2D2.setCurve(d1, d2, d7, d8, d3, d4);
    }
    if (paramQuadCurve2D3 != null) {
      paramQuadCurve2D3.setCurve(d3, d4, d9, d10, d5, d6);
    }
  }
  
  public static void subdivide(double[] paramArrayOfDouble1, int paramInt1, double[] paramArrayOfDouble2, int paramInt2, double[] paramArrayOfDouble3, int paramInt3)
  {
    double d1 = paramArrayOfDouble1[(paramInt1 + 0)];
    double d2 = paramArrayOfDouble1[(paramInt1 + 1)];
    double d3 = paramArrayOfDouble1[(paramInt1 + 2)];
    double d4 = paramArrayOfDouble1[(paramInt1 + 3)];
    double d5 = paramArrayOfDouble1[(paramInt1 + 4)];
    double d6 = paramArrayOfDouble1[(paramInt1 + 5)];
    if (paramArrayOfDouble2 != null)
    {
      paramArrayOfDouble2[(paramInt2 + 0)] = d1;
      paramArrayOfDouble2[(paramInt2 + 1)] = d2;
    }
    if (paramArrayOfDouble3 != null)
    {
      paramArrayOfDouble3[(paramInt3 + 4)] = d5;
      paramArrayOfDouble3[(paramInt3 + 5)] = d6;
    }
    d1 = (d1 + d3) / 2.0D;
    d2 = (d2 + d4) / 2.0D;
    d5 = (d5 + d3) / 2.0D;
    d6 = (d6 + d4) / 2.0D;
    d3 = (d1 + d5) / 2.0D;
    d4 = (d2 + d6) / 2.0D;
    if (paramArrayOfDouble2 != null)
    {
      paramArrayOfDouble2[(paramInt2 + 2)] = d1;
      paramArrayOfDouble2[(paramInt2 + 3)] = d2;
      paramArrayOfDouble2[(paramInt2 + 4)] = d3;
      paramArrayOfDouble2[(paramInt2 + 5)] = d4;
    }
    if (paramArrayOfDouble3 != null)
    {
      paramArrayOfDouble3[(paramInt3 + 0)] = d3;
      paramArrayOfDouble3[(paramInt3 + 1)] = d4;
      paramArrayOfDouble3[(paramInt3 + 2)] = d5;
      paramArrayOfDouble3[(paramInt3 + 3)] = d6;
    }
  }
  
  public static int solveQuadratic(double[] paramArrayOfDouble)
  {
    return solveQuadratic(paramArrayOfDouble, paramArrayOfDouble);
  }
  
  public static int solveQuadratic(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    double d1 = paramArrayOfDouble1[2];
    double d2 = paramArrayOfDouble1[1];
    double d3 = paramArrayOfDouble1[0];
    int i = 0;
    if (d1 == 0.0D)
    {
      if (d2 == 0.0D) {
        return -1;
      }
      paramArrayOfDouble2[(i++)] = (-d3 / d2);
    }
    else
    {
      double d4 = d2 * d2 - 4.0D * d1 * d3;
      if (d4 < 0.0D) {
        return 0;
      }
      d4 = Math.sqrt(d4);
      if (d2 < 0.0D) {
        d4 = -d4;
      }
      double d5 = (d2 + d4) / -2.0D;
      paramArrayOfDouble2[(i++)] = (d5 / d1);
      if (d5 != 0.0D) {
        paramArrayOfDouble2[(i++)] = (d3 / d5);
      }
    }
    return i;
  }
  
  public boolean contains(double paramDouble1, double paramDouble2)
  {
    double d1 = getX1();
    double d2 = getY1();
    double d3 = getCtrlX();
    double d4 = getCtrlY();
    double d5 = getX2();
    double d6 = getY2();
    double d7 = d1 - 2.0D * d3 + d5;
    double d8 = d2 - 2.0D * d4 + d6;
    double d9 = paramDouble1 - d1;
    double d10 = paramDouble2 - d2;
    double d11 = d5 - d1;
    double d12 = d6 - d2;
    double d13 = (d9 * d8 - d10 * d7) / (d11 * d8 - d12 * d7);
    if ((d13 < 0.0D) || (d13 > 1.0D) || (d13 != d13)) {
      return false;
    }
    double d14 = d7 * d13 * d13 + 2.0D * (d3 - d1) * d13 + d1;
    double d15 = d8 * d13 * d13 + 2.0D * (d4 - d2) * d13 + d2;
    double d16 = d11 * d13 + d1;
    double d17 = d12 * d13 + d2;
    return ((paramDouble1 >= d14) && (paramDouble1 < d16)) || ((paramDouble1 >= d16) && (paramDouble1 < d14)) || ((paramDouble2 >= d15) && (paramDouble2 < d17)) || ((paramDouble2 >= d17) && (paramDouble2 < d15));
  }
  
  public boolean contains(Point2D paramPoint2D)
  {
    return contains(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  private static void fillEqn(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    paramArrayOfDouble[0] = (paramDouble2 - paramDouble1);
    paramArrayOfDouble[1] = (paramDouble3 + paramDouble3 - paramDouble2 - paramDouble2);
    paramArrayOfDouble[2] = (paramDouble2 - paramDouble3 - paramDouble3 + paramDouble4);
  }
  
  private static int evalQuadratic(double[] paramArrayOfDouble1, int paramInt, boolean paramBoolean1, boolean paramBoolean2, double[] paramArrayOfDouble2, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j++)
    {
      double d1 = paramArrayOfDouble1[j];
      if ((paramBoolean1 ? d1 >= 0.0D : d1 > 0.0D) && (paramBoolean2 ? d1 <= 1.0D : d1 < 1.0D) && ((paramArrayOfDouble2 == null) || (paramArrayOfDouble2[1] + 2.0D * paramArrayOfDouble2[2] * d1 != 0.0D)))
      {
        double d2 = 1.0D - d1;
        paramArrayOfDouble1[(i++)] = (paramDouble1 * d2 * d2 + 2.0D * paramDouble2 * d1 * d2 + paramDouble3 * d1 * d1);
      }
    }
    return i;
  }
  
  private static int getTag(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    if (paramDouble1 <= paramDouble2) {
      return paramDouble1 < paramDouble2 ? -2 : -1;
    }
    if (paramDouble1 >= paramDouble3) {
      return paramDouble1 > paramDouble3 ? 2 : 1;
    }
    return 0;
  }
  
  private static boolean inwards(int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt1)
    {
    case -2: 
    case 2: 
    default: 
      return false;
    case -1: 
      return (paramInt2 >= 0) || (paramInt3 >= 0);
    case 0: 
      return true;
    }
    return (paramInt2 <= 0) || (paramInt3 <= 0);
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    double d1 = getX1();
    double d2 = getY1();
    int i = getTag(d1, paramDouble1, paramDouble1 + paramDouble3);
    int j = getTag(d2, paramDouble2, paramDouble2 + paramDouble4);
    if ((i == 0) && (j == 0)) {
      return true;
    }
    double d3 = getX2();
    double d4 = getY2();
    int k = getTag(d3, paramDouble1, paramDouble1 + paramDouble3);
    int m = getTag(d4, paramDouble2, paramDouble2 + paramDouble4);
    if ((k == 0) && (m == 0)) {
      return true;
    }
    double d5 = getCtrlX();
    double d6 = getCtrlY();
    int n = getTag(d5, paramDouble1, paramDouble1 + paramDouble3);
    int i1 = getTag(d6, paramDouble2, paramDouble2 + paramDouble4);
    if ((i < 0) && (k < 0) && (n < 0)) {
      return false;
    }
    if ((j < 0) && (m < 0) && (i1 < 0)) {
      return false;
    }
    if ((i > 0) && (k > 0) && (n > 0)) {
      return false;
    }
    if ((j > 0) && (m > 0) && (i1 > 0)) {
      return false;
    }
    if ((inwards(i, k, n)) && (inwards(j, m, i1))) {
      return true;
    }
    if ((inwards(k, i, n)) && (inwards(m, j, i1))) {
      return true;
    }
    int i2 = i * k <= 0 ? 1 : 0;
    int i3 = j * m <= 0 ? 1 : 0;
    if ((i == 0) && (k == 0) && (i3 != 0)) {
      return true;
    }
    if ((j == 0) && (m == 0) && (i2 != 0)) {
      return true;
    }
    double[] arrayOfDouble1 = new double[3];
    double[] arrayOfDouble2 = new double[3];
    if (i3 == 0)
    {
      fillEqn(arrayOfDouble1, j < 0 ? paramDouble2 : paramDouble2 + paramDouble4, d2, d6, d4);
      return (solveQuadratic(arrayOfDouble1, arrayOfDouble2) == 2) && (evalQuadratic(arrayOfDouble2, 2, true, true, null, d1, d5, d3) == 2) && (getTag(arrayOfDouble2[0], paramDouble1, paramDouble1 + paramDouble3) * getTag(arrayOfDouble2[1], paramDouble1, paramDouble1 + paramDouble3) <= 0);
    }
    if (i2 == 0)
    {
      fillEqn(arrayOfDouble1, i < 0 ? paramDouble1 : paramDouble1 + paramDouble3, d1, d5, d3);
      return (solveQuadratic(arrayOfDouble1, arrayOfDouble2) == 2) && (evalQuadratic(arrayOfDouble2, 2, true, true, null, d2, d6, d4) == 2) && (getTag(arrayOfDouble2[0], paramDouble2, paramDouble2 + paramDouble4) * getTag(arrayOfDouble2[1], paramDouble2, paramDouble2 + paramDouble4) <= 0);
    }
    double d7 = d3 - d1;
    double d8 = d4 - d2;
    double d9 = d4 * d1 - d3 * d2;
    if (j == 0) {
      i4 = i;
    } else {
      i4 = getTag((d9 + d7 * (j < 0 ? paramDouble2 : paramDouble2 + paramDouble4)) / d8, paramDouble1, paramDouble1 + paramDouble3);
    }
    if (m == 0) {
      i5 = k;
    } else {
      i5 = getTag((d9 + d7 * (m < 0 ? paramDouble2 : paramDouble2 + paramDouble4)) / d8, paramDouble1, paramDouble1 + paramDouble3);
    }
    if (i4 * i5 <= 0) {
      return true;
    }
    int i4 = i4 * i <= 0 ? j : m;
    fillEqn(arrayOfDouble1, i5 < 0 ? paramDouble1 : paramDouble1 + paramDouble3, d1, d5, d3);
    int i6 = solveQuadratic(arrayOfDouble1, arrayOfDouble2);
    evalQuadratic(arrayOfDouble2, i6, true, true, null, d2, d6, d4);
    int i5 = getTag(arrayOfDouble2[0], paramDouble2, paramDouble2 + paramDouble4);
    return i4 * i5 <= 0;
  }
  
  public boolean intersects(Rectangle2D paramRectangle2D)
  {
    return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((paramDouble3 <= 0.0D) || (paramDouble4 <= 0.0D)) {
      return false;
    }
    return (contains(paramDouble1, paramDouble2)) && (contains(paramDouble1 + paramDouble3, paramDouble2)) && (contains(paramDouble1 + paramDouble3, paramDouble2 + paramDouble4)) && (contains(paramDouble1, paramDouble2 + paramDouble4));
  }
  
  public boolean contains(Rectangle2D paramRectangle2D)
  {
    return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public Rectangle getBounds()
  {
    return getBounds2D().getBounds();
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform)
  {
    return new QuadIterator(this, paramAffineTransform);
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble)
  {
    return new FlatteningPathIterator(getPathIterator(paramAffineTransform), paramDouble);
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
  
  public static class Double
    extends QuadCurve2D
    implements Serializable
  {
    public double x1;
    public double y1;
    public double ctrlx;
    public double ctrly;
    public double x2;
    public double y2;
    private static final long serialVersionUID = 4217149928428559721L;
    
    public Double() {}
    
    public Double(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      setCurve(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6);
    }
    
    public double getX1()
    {
      return x1;
    }
    
    public double getY1()
    {
      return y1;
    }
    
    public Point2D getP1()
    {
      return new Point2D.Double(x1, y1);
    }
    
    public double getCtrlX()
    {
      return ctrlx;
    }
    
    public double getCtrlY()
    {
      return ctrly;
    }
    
    public Point2D getCtrlPt()
    {
      return new Point2D.Double(ctrlx, ctrly);
    }
    
    public double getX2()
    {
      return x2;
    }
    
    public double getY2()
    {
      return y2;
    }
    
    public Point2D getP2()
    {
      return new Point2D.Double(x2, y2);
    }
    
    public void setCurve(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      x1 = paramDouble1;
      y1 = paramDouble2;
      ctrlx = paramDouble3;
      ctrly = paramDouble4;
      x2 = paramDouble5;
      y2 = paramDouble6;
    }
    
    public Rectangle2D getBounds2D()
    {
      double d1 = Math.min(Math.min(x1, x2), ctrlx);
      double d2 = Math.min(Math.min(y1, y2), ctrly);
      double d3 = Math.max(Math.max(x1, x2), ctrlx);
      double d4 = Math.max(Math.max(y1, y2), ctrly);
      return new Rectangle2D.Double(d1, d2, d3 - d1, d4 - d2);
    }
  }
  
  public static class Float
    extends QuadCurve2D
    implements Serializable
  {
    public float x1;
    public float y1;
    public float ctrlx;
    public float ctrly;
    public float x2;
    public float y2;
    private static final long serialVersionUID = -8511188402130719609L;
    
    public Float() {}
    
    public Float(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      setCurve(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6);
    }
    
    public double getX1()
    {
      return x1;
    }
    
    public double getY1()
    {
      return y1;
    }
    
    public Point2D getP1()
    {
      return new Point2D.Float(x1, y1);
    }
    
    public double getCtrlX()
    {
      return ctrlx;
    }
    
    public double getCtrlY()
    {
      return ctrly;
    }
    
    public Point2D getCtrlPt()
    {
      return new Point2D.Float(ctrlx, ctrly);
    }
    
    public double getX2()
    {
      return x2;
    }
    
    public double getY2()
    {
      return y2;
    }
    
    public Point2D getP2()
    {
      return new Point2D.Float(x2, y2);
    }
    
    public void setCurve(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
    {
      x1 = ((float)paramDouble1);
      y1 = ((float)paramDouble2);
      ctrlx = ((float)paramDouble3);
      ctrly = ((float)paramDouble4);
      x2 = ((float)paramDouble5);
      y2 = ((float)paramDouble6);
    }
    
    public void setCurve(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
    {
      x1 = paramFloat1;
      y1 = paramFloat2;
      ctrlx = paramFloat3;
      ctrly = paramFloat4;
      x2 = paramFloat5;
      y2 = paramFloat6;
    }
    
    public Rectangle2D getBounds2D()
    {
      float f1 = Math.min(Math.min(x1, x2), ctrlx);
      float f2 = Math.min(Math.min(y1, y2), ctrly);
      float f3 = Math.max(Math.max(x1, x2), ctrlx);
      float f4 = Math.max(Math.max(y1, y2), ctrly);
      return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\geom\QuadCurve2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */