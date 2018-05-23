package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Arrays;
import sun.awt.geom.Crossings;
import sun.awt.geom.Crossings.EvenOdd;

public class Polygon
  implements Shape, Serializable
{
  public int npoints;
  public int[] xpoints;
  public int[] ypoints;
  protected Rectangle bounds;
  private static final long serialVersionUID = -6460061437900069969L;
  private static final int MIN_LENGTH = 4;
  
  public Polygon()
  {
    xpoints = new int[4];
    ypoints = new int[4];
  }
  
  public Polygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    if ((paramInt > paramArrayOfInt1.length) || (paramInt > paramArrayOfInt2.length)) {
      throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
    }
    if (paramInt < 0) {
      throw new NegativeArraySizeException("npoints < 0");
    }
    npoints = paramInt;
    xpoints = Arrays.copyOf(paramArrayOfInt1, paramInt);
    ypoints = Arrays.copyOf(paramArrayOfInt2, paramInt);
  }
  
  public void reset()
  {
    npoints = 0;
    bounds = null;
  }
  
  public void invalidate()
  {
    bounds = null;
  }
  
  public void translate(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < npoints; i++)
    {
      xpoints[i] += paramInt1;
      ypoints[i] += paramInt2;
    }
    if (bounds != null) {
      bounds.translate(paramInt1, paramInt2);
    }
  }
  
  void calculateBounds(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    int i = Integer.MAX_VALUE;
    int j = Integer.MAX_VALUE;
    int k = Integer.MIN_VALUE;
    int m = Integer.MIN_VALUE;
    for (int n = 0; n < paramInt; n++)
    {
      int i1 = paramArrayOfInt1[n];
      i = Math.min(i, i1);
      k = Math.max(k, i1);
      int i2 = paramArrayOfInt2[n];
      j = Math.min(j, i2);
      m = Math.max(m, i2);
    }
    bounds = new Rectangle(i, j, k - i, m - j);
  }
  
  void updateBounds(int paramInt1, int paramInt2)
  {
    if (paramInt1 < bounds.x)
    {
      bounds.width += bounds.x - paramInt1;
      bounds.x = paramInt1;
    }
    else
    {
      bounds.width = Math.max(bounds.width, paramInt1 - bounds.x);
    }
    if (paramInt2 < bounds.y)
    {
      bounds.height += bounds.y - paramInt2;
      bounds.y = paramInt2;
    }
    else
    {
      bounds.height = Math.max(bounds.height, paramInt2 - bounds.y);
    }
  }
  
  public void addPoint(int paramInt1, int paramInt2)
  {
    if ((npoints >= xpoints.length) || (npoints >= ypoints.length))
    {
      int i = npoints * 2;
      if (i < 4) {
        i = 4;
      } else if ((i & i - 1) != 0) {
        i = Integer.highestOneBit(i);
      }
      xpoints = Arrays.copyOf(xpoints, i);
      ypoints = Arrays.copyOf(ypoints, i);
    }
    xpoints[npoints] = paramInt1;
    ypoints[npoints] = paramInt2;
    npoints += 1;
    if (bounds != null) {
      updateBounds(paramInt1, paramInt2);
    }
  }
  
  public Rectangle getBounds()
  {
    return getBoundingBox();
  }
  
  @Deprecated
  public Rectangle getBoundingBox()
  {
    if (npoints == 0) {
      return new Rectangle();
    }
    if (bounds == null) {
      calculateBounds(xpoints, ypoints, npoints);
    }
    return bounds.getBounds();
  }
  
  public boolean contains(Point paramPoint)
  {
    return contains(x, y);
  }
  
  public boolean contains(int paramInt1, int paramInt2)
  {
    return contains(paramInt1, paramInt2);
  }
  
  @Deprecated
  public boolean inside(int paramInt1, int paramInt2)
  {
    return contains(paramInt1, paramInt2);
  }
  
  public Rectangle2D getBounds2D()
  {
    return getBounds();
  }
  
  public boolean contains(double paramDouble1, double paramDouble2)
  {
    if ((npoints <= 2) || (!getBoundingBox().contains(paramDouble1, paramDouble2))) {
      return false;
    }
    int i = 0;
    int j = xpoints[(npoints - 1)];
    int k = ypoints[(npoints - 1)];
    for (int i1 = 0; i1 < npoints; i1++)
    {
      int m = xpoints[i1];
      int n = ypoints[i1];
      if (n != k)
      {
        int i2;
        if (m < j)
        {
          if (paramDouble1 >= j) {
            break label260;
          }
          i2 = m;
        }
        else
        {
          if (paramDouble1 >= m) {
            break label260;
          }
          i2 = j;
        }
        double d1;
        double d2;
        if (n < k)
        {
          if ((paramDouble2 < n) || (paramDouble2 >= k)) {
            break label260;
          }
          if (paramDouble1 < i2)
          {
            i++;
            break label260;
          }
          d1 = paramDouble1 - m;
          d2 = paramDouble2 - n;
        }
        else
        {
          if ((paramDouble2 < k) || (paramDouble2 >= n)) {
            break label260;
          }
          if (paramDouble1 < i2)
          {
            i++;
            break label260;
          }
          d1 = paramDouble1 - j;
          d2 = paramDouble2 - k;
        }
        if (d1 < d2 / (k - n) * (j - m)) {
          i++;
        }
      }
      label260:
      j = m;
      k = n;
    }
    return (i & 0x1) != 0;
  }
  
  private Crossings getCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    Crossings.EvenOdd localEvenOdd = new Crossings.EvenOdd(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    int i = xpoints[(npoints - 1)];
    int j = ypoints[(npoints - 1)];
    for (int n = 0; n < npoints; n++)
    {
      int k = xpoints[n];
      int m = ypoints[n];
      if (localEvenOdd.accumulateLine(i, j, k, m)) {
        return null;
      }
      i = k;
      j = m;
    }
    return localEvenOdd;
  }
  
  public boolean contains(Point2D paramPoint2D)
  {
    return contains(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((npoints <= 0) || (!getBoundingBox().intersects(paramDouble1, paramDouble2, paramDouble3, paramDouble4))) {
      return false;
    }
    Crossings localCrossings = getCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (localCrossings == null) || (!localCrossings.isEmpty());
  }
  
  public boolean intersects(Rectangle2D paramRectangle2D)
  {
    return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if ((npoints <= 0) || (!getBoundingBox().intersects(paramDouble1, paramDouble2, paramDouble3, paramDouble4))) {
      return false;
    }
    Crossings localCrossings = getCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (localCrossings != null) && (localCrossings.covers(paramDouble2, paramDouble2 + paramDouble4));
  }
  
  public boolean contains(Rectangle2D paramRectangle2D)
  {
    return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform)
  {
    return new PolygonPathIterator(this, paramAffineTransform);
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble)
  {
    return getPathIterator(paramAffineTransform);
  }
  
  class PolygonPathIterator
    implements PathIterator
  {
    Polygon poly;
    AffineTransform transform;
    int index;
    
    public PolygonPathIterator(Polygon paramPolygon, AffineTransform paramAffineTransform)
    {
      poly = paramPolygon;
      transform = paramAffineTransform;
      if (npoints == 0) {
        index = 1;
      }
    }
    
    public int getWindingRule()
    {
      return 0;
    }
    
    public boolean isDone()
    {
      return index > poly.npoints;
    }
    
    public void next()
    {
      index += 1;
    }
    
    public int currentSegment(float[] paramArrayOfFloat)
    {
      if (index >= poly.npoints) {
        return 4;
      }
      paramArrayOfFloat[0] = poly.xpoints[index];
      paramArrayOfFloat[1] = poly.ypoints[index];
      if (transform != null) {
        transform.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1);
      }
      return index == 0 ? 0 : 1;
    }
    
    public int currentSegment(double[] paramArrayOfDouble)
    {
      if (index >= poly.npoints) {
        return 4;
      }
      paramArrayOfDouble[0] = poly.xpoints[index];
      paramArrayOfDouble[1] = poly.ypoints[index];
      if (transform != null) {
        transform.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1);
      }
      return index == 0 ? 0 : 1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Polygon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */