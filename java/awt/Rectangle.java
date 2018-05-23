package java.awt;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.beans.Transient;
import java.io.Serializable;

public class Rectangle
  extends Rectangle2D
  implements Shape, Serializable
{
  public int x;
  public int y;
  public int width;
  public int height;
  private static final long serialVersionUID = -4345857070255674764L;
  
  private static native void initIDs();
  
  public Rectangle()
  {
    this(0, 0, 0, 0);
  }
  
  public Rectangle(Rectangle paramRectangle)
  {
    this(x, y, width, height);
  }
  
  public Rectangle(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    x = paramInt1;
    y = paramInt2;
    width = paramInt3;
    height = paramInt4;
  }
  
  public Rectangle(int paramInt1, int paramInt2)
  {
    this(0, 0, paramInt1, paramInt2);
  }
  
  public Rectangle(Point paramPoint, Dimension paramDimension)
  {
    this(x, y, width, height);
  }
  
  public Rectangle(Point paramPoint)
  {
    this(x, y, 0, 0);
  }
  
  public Rectangle(Dimension paramDimension)
  {
    this(0, 0, width, height);
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
  
  @Transient
  public Rectangle getBounds()
  {
    return new Rectangle(x, y, width, height);
  }
  
  public Rectangle2D getBounds2D()
  {
    return new Rectangle(x, y, width, height);
  }
  
  public void setBounds(Rectangle paramRectangle)
  {
    setBounds(x, y, width, height);
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    reshape(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    int i;
    int k;
    if (paramDouble1 > 4.294967294E9D)
    {
      i = Integer.MAX_VALUE;
      k = -1;
    }
    else
    {
      i = clip(paramDouble1, false);
      if (paramDouble3 >= 0.0D) {
        paramDouble3 += paramDouble1 - i;
      }
      k = clip(paramDouble3, paramDouble3 >= 0.0D);
    }
    int j;
    int m;
    if (paramDouble2 > 4.294967294E9D)
    {
      j = Integer.MAX_VALUE;
      m = -1;
    }
    else
    {
      j = clip(paramDouble2, false);
      if (paramDouble4 >= 0.0D) {
        paramDouble4 += paramDouble2 - j;
      }
      m = clip(paramDouble4, paramDouble4 >= 0.0D);
    }
    reshape(i, j, k, m);
  }
  
  private static int clip(double paramDouble, boolean paramBoolean)
  {
    if (paramDouble <= -2.147483648E9D) {
      return Integer.MIN_VALUE;
    }
    if (paramDouble >= 2.147483647E9D) {
      return Integer.MAX_VALUE;
    }
    return (int)(paramBoolean ? Math.ceil(paramDouble) : Math.floor(paramDouble));
  }
  
  @Deprecated
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    x = paramInt1;
    y = paramInt2;
    width = paramInt3;
    height = paramInt4;
  }
  
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
  
  @Deprecated
  public void move(int paramInt1, int paramInt2)
  {
    x = paramInt1;
    y = paramInt2;
  }
  
  public void translate(int paramInt1, int paramInt2)
  {
    int i = x;
    int j = i + paramInt1;
    if (paramInt1 < 0)
    {
      if (j > i)
      {
        if (width >= 0) {
          width += j - Integer.MIN_VALUE;
        }
        j = Integer.MIN_VALUE;
      }
    }
    else if (j < i)
    {
      if (width >= 0)
      {
        width += j - Integer.MAX_VALUE;
        if (width < 0) {
          width = Integer.MAX_VALUE;
        }
      }
      j = Integer.MAX_VALUE;
    }
    x = j;
    i = y;
    j = i + paramInt2;
    if (paramInt2 < 0)
    {
      if (j > i)
      {
        if (height >= 0) {
          height += j - Integer.MIN_VALUE;
        }
        j = Integer.MIN_VALUE;
      }
    }
    else if (j < i)
    {
      if (height >= 0)
      {
        height += j - Integer.MAX_VALUE;
        if (height < 0) {
          height = Integer.MAX_VALUE;
        }
      }
      j = Integer.MAX_VALUE;
    }
    y = j;
  }
  
  public Dimension getSize()
  {
    return new Dimension(width, height);
  }
  
  public void setSize(Dimension paramDimension)
  {
    setSize(width, height);
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    resize(paramInt1, paramInt2);
  }
  
  @Deprecated
  public void resize(int paramInt1, int paramInt2)
  {
    width = paramInt1;
    height = paramInt2;
  }
  
  public boolean contains(Point paramPoint)
  {
    return contains(x, y);
  }
  
  public boolean contains(int paramInt1, int paramInt2)
  {
    return inside(paramInt1, paramInt2);
  }
  
  public boolean contains(Rectangle paramRectangle)
  {
    return contains(x, y, width, height);
  }
  
  public boolean contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = width;
    int j = height;
    if ((i | j | paramInt3 | paramInt4) < 0) {
      return false;
    }
    int k = x;
    int m = y;
    if ((paramInt1 < k) || (paramInt2 < m)) {
      return false;
    }
    i += k;
    paramInt3 += paramInt1;
    if (paramInt3 <= paramInt1)
    {
      if ((i >= k) || (paramInt3 > i)) {
        return false;
      }
    }
    else if ((i >= k) && (paramInt3 > i)) {
      return false;
    }
    j += m;
    paramInt4 += paramInt2;
    if (paramInt4 <= paramInt2)
    {
      if ((j >= m) || (paramInt4 > j)) {
        return false;
      }
    }
    else if ((j >= m) && (paramInt4 > j)) {
      return false;
    }
    return true;
  }
  
  @Deprecated
  public boolean inside(int paramInt1, int paramInt2)
  {
    int i = width;
    int j = height;
    if ((i | j) < 0) {
      return false;
    }
    int k = x;
    int m = y;
    if ((paramInt1 < k) || (paramInt2 < m)) {
      return false;
    }
    i += k;
    j += m;
    return ((i < k) || (i > paramInt1)) && ((j < m) || (j > paramInt2));
  }
  
  public boolean intersects(Rectangle paramRectangle)
  {
    int i = width;
    int j = height;
    int k = width;
    int m = height;
    if ((k <= 0) || (m <= 0) || (i <= 0) || (j <= 0)) {
      return false;
    }
    int n = x;
    int i1 = y;
    int i2 = x;
    int i3 = y;
    k += i2;
    m += i3;
    i += n;
    j += i1;
    return ((k < i2) || (k > n)) && ((m < i3) || (m > i1)) && ((i < n) || (i > i2)) && ((j < i1) || (j > i3));
  }
  
  public Rectangle intersection(Rectangle paramRectangle)
  {
    int i = x;
    int j = y;
    int k = x;
    int m = y;
    long l1 = i;
    l1 += width;
    long l2 = j;
    l2 += height;
    long l3 = k;
    l3 += width;
    long l4 = m;
    l4 += height;
    if (i < k) {
      i = k;
    }
    if (j < m) {
      j = m;
    }
    if (l1 > l3) {
      l1 = l3;
    }
    if (l2 > l4) {
      l2 = l4;
    }
    l1 -= i;
    l2 -= j;
    if (l1 < -2147483648L) {
      l1 = -2147483648L;
    }
    if (l2 < -2147483648L) {
      l2 = -2147483648L;
    }
    return new Rectangle(i, j, (int)l1, (int)l2);
  }
  
  public Rectangle union(Rectangle paramRectangle)
  {
    long l1 = width;
    long l2 = height;
    if ((l1 | l2) < 0L) {
      return new Rectangle(paramRectangle);
    }
    long l3 = width;
    long l4 = height;
    if ((l3 | l4) < 0L) {
      return new Rectangle(this);
    }
    int i = x;
    int j = y;
    l1 += i;
    l2 += j;
    int k = x;
    int m = y;
    l3 += k;
    l4 += m;
    if (i > k) {
      i = k;
    }
    if (j > m) {
      j = m;
    }
    if (l1 < l3) {
      l1 = l3;
    }
    if (l2 < l4) {
      l2 = l4;
    }
    l1 -= i;
    l2 -= j;
    if (l1 > 2147483647L) {
      l1 = 2147483647L;
    }
    if (l2 > 2147483647L) {
      l2 = 2147483647L;
    }
    return new Rectangle(i, j, (int)l1, (int)l2);
  }
  
  public void add(int paramInt1, int paramInt2)
  {
    if ((width | height) < 0)
    {
      x = paramInt1;
      y = paramInt2;
      width = (height = 0);
      return;
    }
    int i = x;
    int j = y;
    long l1 = width;
    long l2 = height;
    l1 += i;
    l2 += j;
    if (i > paramInt1) {
      i = paramInt1;
    }
    if (j > paramInt2) {
      j = paramInt2;
    }
    if (l1 < paramInt1) {
      l1 = paramInt1;
    }
    if (l2 < paramInt2) {
      l2 = paramInt2;
    }
    l1 -= i;
    l2 -= j;
    if (l1 > 2147483647L) {
      l1 = 2147483647L;
    }
    if (l2 > 2147483647L) {
      l2 = 2147483647L;
    }
    reshape(i, j, (int)l1, (int)l2);
  }
  
  public void add(Point paramPoint)
  {
    add(x, y);
  }
  
  public void add(Rectangle paramRectangle)
  {
    long l1 = width;
    long l2 = height;
    if ((l1 | l2) < 0L) {
      reshape(x, y, width, height);
    }
    long l3 = width;
    long l4 = height;
    if ((l3 | l4) < 0L) {
      return;
    }
    int i = x;
    int j = y;
    l1 += i;
    l2 += j;
    int k = x;
    int m = y;
    l3 += k;
    l4 += m;
    if (i > k) {
      i = k;
    }
    if (j > m) {
      j = m;
    }
    if (l1 < l3) {
      l1 = l3;
    }
    if (l2 < l4) {
      l2 = l4;
    }
    l1 -= i;
    l2 -= j;
    if (l1 > 2147483647L) {
      l1 = 2147483647L;
    }
    if (l2 > 2147483647L) {
      l2 = 2147483647L;
    }
    reshape(i, j, (int)l1, (int)l2);
  }
  
  public void grow(int paramInt1, int paramInt2)
  {
    long l1 = x;
    long l2 = y;
    long l3 = width;
    long l4 = height;
    l3 += l1;
    l4 += l2;
    l1 -= paramInt1;
    l2 -= paramInt2;
    l3 += paramInt1;
    l4 += paramInt2;
    if (l3 < l1)
    {
      l3 -= l1;
      if (l3 < -2147483648L) {
        l3 = -2147483648L;
      }
      if (l1 < -2147483648L) {
        l1 = -2147483648L;
      } else if (l1 > 2147483647L) {
        l1 = 2147483647L;
      }
    }
    else
    {
      if (l1 < -2147483648L) {
        l1 = -2147483648L;
      } else if (l1 > 2147483647L) {
        l1 = 2147483647L;
      }
      l3 -= l1;
      if (l3 < -2147483648L) {
        l3 = -2147483648L;
      } else if (l3 > 2147483647L) {
        l3 = 2147483647L;
      }
    }
    if (l4 < l2)
    {
      l4 -= l2;
      if (l4 < -2147483648L) {
        l4 = -2147483648L;
      }
      if (l2 < -2147483648L) {
        l2 = -2147483648L;
      } else if (l2 > 2147483647L) {
        l2 = 2147483647L;
      }
    }
    else
    {
      if (l2 < -2147483648L) {
        l2 = -2147483648L;
      } else if (l2 > 2147483647L) {
        l2 = 2147483647L;
      }
      l4 -= l2;
      if (l4 < -2147483648L) {
        l4 = -2147483648L;
      } else if (l4 > 2147483647L) {
        l4 = 2147483647L;
      }
    }
    reshape((int)l1, (int)l2, (int)l3, (int)l4);
  }
  
  public boolean isEmpty()
  {
    return (width <= 0) || (height <= 0);
  }
  
  public int outcode(double paramDouble1, double paramDouble2)
  {
    int i = 0;
    if (width <= 0) {
      i |= 0x5;
    } else if (paramDouble1 < x) {
      i |= 0x1;
    } else if (paramDouble1 > x + width) {
      i |= 0x4;
    }
    if (height <= 0) {
      i |= 0xA;
    } else if (paramDouble2 < y) {
      i |= 0x2;
    } else if (paramDouble2 > y + height) {
      i |= 0x8;
    }
    return i;
  }
  
  public Rectangle2D createIntersection(Rectangle2D paramRectangle2D)
  {
    if ((paramRectangle2D instanceof Rectangle)) {
      return intersection((Rectangle)paramRectangle2D);
    }
    Rectangle2D.Double localDouble = new Rectangle2D.Double();
    Rectangle2D.intersect(this, paramRectangle2D, localDouble);
    return localDouble;
  }
  
  public Rectangle2D createUnion(Rectangle2D paramRectangle2D)
  {
    if ((paramRectangle2D instanceof Rectangle)) {
      return union((Rectangle)paramRectangle2D);
    }
    Rectangle2D.Double localDouble = new Rectangle2D.Double();
    Rectangle2D.union(this, paramRectangle2D, localDouble);
    return localDouble;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Rectangle))
    {
      Rectangle localRectangle = (Rectangle)paramObject;
      return (x == x) && (y == y) && (width == width) && (height == height);
    }
    return super.equals(paramObject);
  }
  
  public String toString()
  {
    return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
  }
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Rectangle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */