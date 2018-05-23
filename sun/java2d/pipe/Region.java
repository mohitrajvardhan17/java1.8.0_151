package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RectangularShape;

public class Region
{
  static final int INIT_SIZE = 50;
  static final int GROW_SIZE = 50;
  public static final Region EMPTY_REGION = new ImmutableRegion(0, 0, 0, 0);
  public static final Region WHOLE_REGION = new ImmutableRegion(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
  int lox;
  int loy;
  int hix;
  int hiy;
  int endIndex;
  int[] bands;
  static final int INCLUDE_A = 1;
  static final int INCLUDE_B = 2;
  static final int INCLUDE_COMMON = 4;
  
  private static native void initIDs();
  
  public static int dimAdd(int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 0) {
      return paramInt1;
    }
    if (paramInt2 += paramInt1 < paramInt1) {
      return Integer.MAX_VALUE;
    }
    return paramInt2;
  }
  
  public static int clipAdd(int paramInt1, int paramInt2)
  {
    int i = paramInt1 + paramInt2;
    if ((i > paramInt1 ? 1 : 0) != (paramInt2 > 0 ? 1 : 0)) {
      i = paramInt2 < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    }
    return i;
  }
  
  public static int clipScale(int paramInt, double paramDouble)
  {
    if (paramDouble == 1.0D) {
      return paramInt;
    }
    double d = paramInt * paramDouble;
    if (d < -2.147483648E9D) {
      return Integer.MIN_VALUE;
    }
    if (d > 2.147483647E9D) {
      return Integer.MAX_VALUE;
    }
    return (int)Math.round(d);
  }
  
  protected Region(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    lox = paramInt1;
    loy = paramInt2;
    hix = paramInt3;
    hiy = paramInt4;
  }
  
  private Region(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5)
  {
    lox = paramInt1;
    loy = paramInt2;
    hix = paramInt3;
    hiy = paramInt4;
    bands = paramArrayOfInt;
    endIndex = paramInt5;
  }
  
  public static Region getInstance(Shape paramShape, AffineTransform paramAffineTransform)
  {
    return getInstance(WHOLE_REGION, false, paramShape, paramAffineTransform);
  }
  
  public static Region getInstance(Region paramRegion, Shape paramShape, AffineTransform paramAffineTransform)
  {
    return getInstance(paramRegion, false, paramShape, paramAffineTransform);
  }
  
  public static Region getInstance(Region paramRegion, boolean paramBoolean, Shape paramShape, AffineTransform paramAffineTransform)
  {
    if (((paramShape instanceof RectangularShape)) && (((RectangularShape)paramShape).isEmpty())) {
      return EMPTY_REGION;
    }
    int[] arrayOfInt = new int[4];
    ShapeSpanIterator localShapeSpanIterator = new ShapeSpanIterator(paramBoolean);
    try
    {
      localShapeSpanIterator.setOutputArea(paramRegion);
      localShapeSpanIterator.appendPath(paramShape.getPathIterator(paramAffineTransform));
      localShapeSpanIterator.getPathBox(arrayOfInt);
      Region localRegion1 = getInstance(arrayOfInt);
      localRegion1.appendSpans(localShapeSpanIterator);
      Region localRegion2 = localRegion1;
      return localRegion2;
    }
    finally
    {
      localShapeSpanIterator.dispose();
    }
  }
  
  static Region getInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt[0];
    int j = paramArrayOfInt[1];
    if ((paramInt4 <= paramInt2) || (paramInt3 <= paramInt1) || (j <= i)) {
      return EMPTY_REGION;
    }
    int[] arrayOfInt = new int[(j - i) * 5];
    int k = 0;
    int m = 2;
    for (int n = i; n < j; n++)
    {
      int i1 = Math.max(clipAdd(paramInt1, paramArrayOfInt[(m++)]), paramInt1);
      int i2 = Math.min(clipAdd(paramInt1, paramArrayOfInt[(m++)]), paramInt3);
      if (i1 < i2)
      {
        int i3 = Math.max(clipAdd(paramInt2, n), paramInt2);
        int i4 = Math.min(clipAdd(i3, 1), paramInt4);
        if (i3 < i4)
        {
          arrayOfInt[(k++)] = i3;
          arrayOfInt[(k++)] = i4;
          arrayOfInt[(k++)] = 1;
          arrayOfInt[(k++)] = i1;
          arrayOfInt[(k++)] = i2;
        }
      }
    }
    return k != 0 ? new Region(paramInt1, paramInt2, paramInt3, paramInt4, arrayOfInt, k) : EMPTY_REGION;
  }
  
  public static Region getInstance(Rectangle paramRectangle)
  {
    return getInstanceXYWH(x, y, width, height);
  }
  
  public static Region getInstanceXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return getInstanceXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
  }
  
  public static Region getInstance(int[] paramArrayOfInt)
  {
    return new Region(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]);
  }
  
  public static Region getInstanceXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new Region(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setOutputArea(Rectangle paramRectangle)
  {
    setOutputAreaXYWH(x, y, width, height);
  }
  
  public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setOutputAreaXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
  }
  
  public void setOutputArea(int[] paramArrayOfInt)
  {
    lox = paramArrayOfInt[0];
    loy = paramArrayOfInt[1];
    hix = paramArrayOfInt[2];
    hiy = paramArrayOfInt[3];
  }
  
  public void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    lox = paramInt1;
    loy = paramInt2;
    hix = paramInt3;
    hiy = paramInt4;
  }
  
  public void appendSpans(SpanIterator paramSpanIterator)
  {
    int[] arrayOfInt = new int[6];
    while (paramSpanIterator.nextSpan(arrayOfInt)) {
      appendSpan(arrayOfInt);
    }
    endRow(arrayOfInt);
    calcBBox();
  }
  
  public Region getScaledRegion(double paramDouble1, double paramDouble2)
  {
    if ((paramDouble1 == 0.0D) || (paramDouble2 == 0.0D) || (this == EMPTY_REGION)) {
      return EMPTY_REGION;
    }
    if (((paramDouble1 == 1.0D) && (paramDouble2 == 1.0D)) || (this == WHOLE_REGION)) {
      return this;
    }
    int i = clipScale(lox, paramDouble1);
    int j = clipScale(loy, paramDouble2);
    int k = clipScale(hix, paramDouble1);
    int m = clipScale(hiy, paramDouble2);
    Region localRegion = new Region(i, j, k, m);
    int[] arrayOfInt1 = bands;
    if (arrayOfInt1 != null)
    {
      int n = endIndex;
      int[] arrayOfInt2 = new int[n];
      int i1 = 0;
      int i2 = 0;
      while (i1 < n)
      {
        int i4;
        arrayOfInt2[(i2++)] = (i4 = clipScale(arrayOfInt1[(i1++)], paramDouble2));
        int i5;
        arrayOfInt2[(i2++)] = (i5 = clipScale(arrayOfInt1[(i1++)], paramDouble2));
        int i3;
        arrayOfInt2[(i2++)] = (i3 = arrayOfInt1[(i1++)]);
        int i6 = i2;
        if (i4 < i5) {
          for (;;)
          {
            i3--;
            if (i3 < 0) {
              break;
            }
            int i7 = clipScale(arrayOfInt1[(i1++)], paramDouble1);
            int i8 = clipScale(arrayOfInt1[(i1++)], paramDouble1);
            if (i7 < i8)
            {
              arrayOfInt2[(i2++)] = i7;
              arrayOfInt2[(i2++)] = i8;
            }
          }
        }
        i1 += i3 * 2;
        if (i2 > i6) {
          arrayOfInt2[(i6 - 1)] = ((i2 - i6) / 2);
        } else {
          i2 = i6 - 3;
        }
      }
      if (i2 <= 5)
      {
        if (i2 < 5)
        {
          lox = (loy = hix = hiy = 0);
        }
        else
        {
          loy = arrayOfInt2[0];
          hiy = arrayOfInt2[1];
          lox = arrayOfInt2[3];
          hix = arrayOfInt2[4];
        }
      }
      else
      {
        endIndex = i2;
        bands = arrayOfInt2;
      }
    }
    return localRegion;
  }
  
  public Region getTranslatedRegion(int paramInt1, int paramInt2)
  {
    if ((paramInt1 | paramInt2) == 0) {
      return this;
    }
    int i = lox + paramInt1;
    int j = loy + paramInt2;
    int k = hix + paramInt1;
    int m = hiy + paramInt2;
    if ((i > lox ? 1 : 0) == (paramInt1 > 0 ? 1 : 0)) {
      if ((j > loy ? 1 : 0) == (paramInt2 > 0 ? 1 : 0)) {
        if ((k > hix ? 1 : 0) == (paramInt1 > 0 ? 1 : 0)) {
          if ((m > hiy ? 1 : 0) == (paramInt2 > 0 ? 1 : 0)) {
            break label149;
          }
        }
      }
    }
    return getSafeTranslatedRegion(paramInt1, paramInt2);
    label149:
    Region localRegion = new Region(i, j, k, m);
    int[] arrayOfInt1 = bands;
    if (arrayOfInt1 != null)
    {
      int n = endIndex;
      endIndex = n;
      int[] arrayOfInt2 = new int[n];
      bands = arrayOfInt2;
      int i1 = 0;
      while (i1 < n)
      {
        arrayOfInt1[i1] += paramInt2;
        i1++;
        arrayOfInt1[i1] += paramInt2;
        i1++;
        int i2;
        arrayOfInt2[i1] = (i2 = arrayOfInt1[i1]);
        i1++;
        for (;;)
        {
          i2--;
          if (i2 < 0) {
            break;
          }
          arrayOfInt1[i1] += paramInt1;
          i1++;
          arrayOfInt1[i1] += paramInt1;
          i1++;
        }
      }
    }
    return localRegion;
  }
  
  private Region getSafeTranslatedRegion(int paramInt1, int paramInt2)
  {
    int i = clipAdd(lox, paramInt1);
    int j = clipAdd(loy, paramInt2);
    int k = clipAdd(hix, paramInt1);
    int m = clipAdd(hiy, paramInt2);
    Region localRegion = new Region(i, j, k, m);
    int[] arrayOfInt1 = bands;
    if (arrayOfInt1 != null)
    {
      int n = endIndex;
      int[] arrayOfInt2 = new int[n];
      int i1 = 0;
      int i2 = 0;
      while (i1 < n)
      {
        int i4;
        arrayOfInt2[(i2++)] = (i4 = clipAdd(arrayOfInt1[(i1++)], paramInt2));
        int i5;
        arrayOfInt2[(i2++)] = (i5 = clipAdd(arrayOfInt1[(i1++)], paramInt2));
        int i3;
        arrayOfInt2[(i2++)] = (i3 = arrayOfInt1[(i1++)]);
        int i6 = i2;
        if (i4 < i5) {
          for (;;)
          {
            i3--;
            if (i3 < 0) {
              break;
            }
            int i7 = clipAdd(arrayOfInt1[(i1++)], paramInt1);
            int i8 = clipAdd(arrayOfInt1[(i1++)], paramInt1);
            if (i7 < i8)
            {
              arrayOfInt2[(i2++)] = i7;
              arrayOfInt2[(i2++)] = i8;
            }
          }
        }
        i1 += i3 * 2;
        if (i2 > i6) {
          arrayOfInt2[(i6 - 1)] = ((i2 - i6) / 2);
        } else {
          i2 = i6 - 3;
        }
      }
      if (i2 <= 5)
      {
        if (i2 < 5)
        {
          lox = (loy = hix = hiy = 0);
        }
        else
        {
          loy = arrayOfInt2[0];
          hiy = arrayOfInt2[1];
          lox = arrayOfInt2[3];
          hix = arrayOfInt2[4];
        }
      }
      else
      {
        endIndex = i2;
        bands = arrayOfInt2;
      }
    }
    return localRegion;
  }
  
  public Region getIntersection(Rectangle paramRectangle)
  {
    return getIntersectionXYWH(x, y, width, height);
  }
  
  public Region getIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return getIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
  }
  
  public Region getIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isInsideXYXY(paramInt1, paramInt2, paramInt3, paramInt4)) {
      return this;
    }
    Region localRegion = new Region(paramInt1 < lox ? lox : paramInt1, paramInt2 < loy ? loy : paramInt2, paramInt3 > hix ? hix : paramInt3, paramInt4 > hiy ? hiy : paramInt4);
    if (bands != null) {
      localRegion.appendSpans(getSpanIterator());
    }
    return localRegion;
  }
  
  public Region getIntersection(Region paramRegion)
  {
    if (isInsideQuickCheck(paramRegion)) {
      return this;
    }
    if (paramRegion.isInsideQuickCheck(this)) {
      return paramRegion;
    }
    Region localRegion = new Region(lox < lox ? lox : lox, loy < loy ? loy : loy, hix > hix ? hix : hix, hiy > hiy ? hiy : hiy);
    if (!localRegion.isEmpty()) {
      localRegion.filterSpans(this, paramRegion, 4);
    }
    return localRegion;
  }
  
  public Region getUnion(Region paramRegion)
  {
    if ((paramRegion.isEmpty()) || (paramRegion.isInsideQuickCheck(this))) {
      return this;
    }
    if ((isEmpty()) || (isInsideQuickCheck(paramRegion))) {
      return paramRegion;
    }
    Region localRegion = new Region(lox > lox ? lox : lox, loy > loy ? loy : loy, hix < hix ? hix : hix, hiy < hiy ? hiy : hiy);
    localRegion.filterSpans(this, paramRegion, 7);
    return localRegion;
  }
  
  public Region getDifference(Region paramRegion)
  {
    if (!paramRegion.intersectsQuickCheck(this)) {
      return this;
    }
    if (isInsideQuickCheck(paramRegion)) {
      return EMPTY_REGION;
    }
    Region localRegion = new Region(lox, loy, hix, hiy);
    localRegion.filterSpans(this, paramRegion, 1);
    return localRegion;
  }
  
  public Region getExclusiveOr(Region paramRegion)
  {
    if (paramRegion.isEmpty()) {
      return this;
    }
    if (isEmpty()) {
      return paramRegion;
    }
    Region localRegion = new Region(lox > lox ? lox : lox, loy > loy ? loy : loy, hix < hix ? hix : hix, hiy < hiy ? hiy : hiy);
    localRegion.filterSpans(this, paramRegion, 3);
    return localRegion;
  }
  
  private void filterSpans(Region paramRegion1, Region paramRegion2, int paramInt)
  {
    int[] arrayOfInt1 = bands;
    int[] arrayOfInt2 = bands;
    if (arrayOfInt1 == null) {
      arrayOfInt1 = new int[] { loy, hiy, 1, lox, hix };
    }
    if (arrayOfInt2 == null) {
      arrayOfInt2 = new int[] { loy, hiy, 1, lox, hix };
    }
    int[] arrayOfInt3 = new int[6];
    int i = 0;
    int j = arrayOfInt1[(i++)];
    int k = arrayOfInt1[(i++)];
    int m = arrayOfInt1[(i++)];
    m = i + 2 * m;
    int n = 0;
    int i1 = arrayOfInt2[(n++)];
    int i2 = arrayOfInt2[(n++)];
    int i3 = arrayOfInt2[(n++)];
    i3 = n + 2 * i3;
    int i4 = loy;
    while (i4 < hiy) {
      if (i4 >= k)
      {
        if (m < endIndex)
        {
          i = m;
          j = arrayOfInt1[(i++)];
          k = arrayOfInt1[(i++)];
          m = arrayOfInt1[(i++)];
          m = i + 2 * m;
        }
        else
        {
          if ((paramInt & 0x2) == 0) {
            break;
          }
          j = k = hiy;
        }
      }
      else if (i4 >= i2)
      {
        if (i3 < endIndex)
        {
          n = i3;
          i1 = arrayOfInt2[(n++)];
          i2 = arrayOfInt2[(n++)];
          i3 = arrayOfInt2[(n++)];
          i3 = n + 2 * i3;
        }
        else
        {
          if ((paramInt & 0x1) == 0) {
            break;
          }
          i1 = i2 = hiy;
        }
      }
      else
      {
        int i5;
        int i6;
        if (i4 < i1)
        {
          if (i4 < j)
          {
            i4 = Math.min(j, i1);
            continue;
          }
          i5 = Math.min(k, i1);
          if ((paramInt & 0x1) != 0)
          {
            arrayOfInt3[1] = i4;
            arrayOfInt3[3] = i5;
            i6 = i;
            while (i6 < m)
            {
              arrayOfInt3[0] = arrayOfInt1[(i6++)];
              arrayOfInt3[2] = arrayOfInt1[(i6++)];
              appendSpan(arrayOfInt3);
            }
          }
        }
        else if (i4 < j)
        {
          i5 = Math.min(i2, j);
          if ((paramInt & 0x2) != 0)
          {
            arrayOfInt3[1] = i4;
            arrayOfInt3[3] = i5;
            i6 = n;
            while (i6 < i3)
            {
              arrayOfInt3[0] = arrayOfInt2[(i6++)];
              arrayOfInt3[2] = arrayOfInt2[(i6++)];
              appendSpan(arrayOfInt3);
            }
          }
        }
        else
        {
          i5 = Math.min(k, i2);
          arrayOfInt3[1] = i4;
          arrayOfInt3[3] = i5;
          i6 = i;
          int i7 = n;
          int i8 = arrayOfInt1[(i6++)];
          int i9 = arrayOfInt1[(i6++)];
          int i10 = arrayOfInt2[(i7++)];
          int i11 = arrayOfInt2[(i7++)];
          int i12 = Math.min(i8, i10);
          if (i12 < lox) {
            i12 = lox;
          }
          while (i12 < hix) {
            if (i12 >= i9)
            {
              if (i6 < m)
              {
                i8 = arrayOfInt1[(i6++)];
                i9 = arrayOfInt1[(i6++)];
              }
              else
              {
                if ((paramInt & 0x2) == 0) {
                  break;
                }
                i8 = i9 = hix;
              }
            }
            else if (i12 >= i11)
            {
              if (i7 < i3)
              {
                i10 = arrayOfInt2[(i7++)];
                i11 = arrayOfInt2[(i7++)];
              }
              else
              {
                if ((paramInt & 0x1) == 0) {
                  break;
                }
                i10 = i11 = hix;
              }
            }
            else
            {
              int i13;
              int i14;
              if (i12 < i10)
              {
                if (i12 < i8)
                {
                  i13 = Math.min(i8, i10);
                  i14 = 0;
                }
                else
                {
                  i13 = Math.min(i9, i10);
                  i14 = (paramInt & 0x1) != 0 ? 1 : 0;
                }
              }
              else if (i12 < i8)
              {
                i13 = Math.min(i8, i11);
                i14 = (paramInt & 0x2) != 0 ? 1 : 0;
              }
              else
              {
                i13 = Math.min(i9, i11);
                i14 = (paramInt & 0x4) != 0 ? 1 : 0;
              }
              if (i14 != 0)
              {
                arrayOfInt3[0] = i12;
                arrayOfInt3[2] = i13;
                appendSpan(arrayOfInt3);
              }
              i12 = i13;
            }
          }
        }
        i4 = i5;
      }
    }
    endRow(arrayOfInt3);
    calcBBox();
  }
  
  public Region getBoundsIntersection(Rectangle paramRectangle)
  {
    return getBoundsIntersectionXYWH(x, y, width, height);
  }
  
  public Region getBoundsIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return getBoundsIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
  }
  
  public Region getBoundsIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((bands == null) && (lox >= paramInt1) && (loy >= paramInt2) && (hix <= paramInt3) && (hiy <= paramInt4)) {
      return this;
    }
    return new Region(paramInt1 < lox ? lox : paramInt1, paramInt2 < loy ? loy : paramInt2, paramInt3 > hix ? hix : paramInt3, paramInt4 > hiy ? hiy : paramInt4);
  }
  
  public Region getBoundsIntersection(Region paramRegion)
  {
    if (encompasses(paramRegion)) {
      return paramRegion;
    }
    if (paramRegion.encompasses(this)) {
      return this;
    }
    return new Region(lox < lox ? lox : lox, loy < loy ? loy : loy, hix > hix ? hix : hix, hiy > hiy ? hiy : hiy);
  }
  
  private void appendSpan(int[] paramArrayOfInt)
  {
    int i;
    if ((i = paramArrayOfInt[0]) < lox) {
      i = lox;
    }
    int j;
    if ((j = paramArrayOfInt[1]) < loy) {
      j = loy;
    }
    int k;
    if ((k = paramArrayOfInt[2]) > hix) {
      k = hix;
    }
    int m;
    if ((m = paramArrayOfInt[3]) > hiy) {
      m = hiy;
    }
    if ((k <= i) || (m <= j)) {
      return;
    }
    int n = paramArrayOfInt[4];
    if ((endIndex == 0) || (j >= bands[(n + 1)]))
    {
      if (bands == null)
      {
        bands = new int[50];
      }
      else
      {
        needSpace(5);
        endRow(paramArrayOfInt);
        n = paramArrayOfInt[4];
      }
      bands[(endIndex++)] = j;
      bands[(endIndex++)] = m;
      bands[(endIndex++)] = 0;
    }
    else if ((j == bands[n]) && (m == bands[(n + 1)]) && (i >= bands[(endIndex - 1)]))
    {
      if (i == bands[(endIndex - 1)])
      {
        bands[(endIndex - 1)] = k;
        return;
      }
      needSpace(2);
    }
    else
    {
      throw new InternalError("bad span");
    }
    bands[(endIndex++)] = i;
    bands[(endIndex++)] = k;
    bands[(n + 2)] += 1;
  }
  
  private void needSpace(int paramInt)
  {
    if (endIndex + paramInt >= bands.length)
    {
      int[] arrayOfInt = new int[bands.length + 50];
      System.arraycopy(bands, 0, arrayOfInt, 0, endIndex);
      bands = arrayOfInt;
    }
  }
  
  private void endRow(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt[4];
    int j = paramArrayOfInt[5];
    if (i > j)
    {
      int[] arrayOfInt = bands;
      if ((arrayOfInt[(j + 1)] == arrayOfInt[i]) && (arrayOfInt[(j + 2)] == arrayOfInt[(i + 2)]))
      {
        int k = arrayOfInt[(i + 2)] * 2;
        i += 3;
        j += 3;
        while ((k > 0) && (arrayOfInt[(i++)] == arrayOfInt[(j++)])) {
          k--;
        }
        if (k == 0)
        {
          arrayOfInt[(paramArrayOfInt[5] + 1)] = arrayOfInt[(j + 1)];
          endIndex = j;
          return;
        }
      }
    }
    paramArrayOfInt[5] = paramArrayOfInt[4];
    paramArrayOfInt[4] = endIndex;
  }
  
  private void calcBBox()
  {
    int[] arrayOfInt = bands;
    if (endIndex <= 5)
    {
      if (endIndex == 0)
      {
        lox = (loy = hix = hiy = 0);
      }
      else
      {
        loy = arrayOfInt[0];
        hiy = arrayOfInt[1];
        lox = arrayOfInt[3];
        hix = arrayOfInt[4];
        endIndex = 0;
      }
      bands = null;
      return;
    }
    int i = hix;
    int j = lox;
    int k = 0;
    int m = 0;
    while (m < endIndex)
    {
      k = m;
      int n = arrayOfInt[(m + 2)];
      m += 3;
      if (i > arrayOfInt[m]) {
        i = arrayOfInt[m];
      }
      m += n * 2;
      if (j < arrayOfInt[(m - 1)]) {
        j = arrayOfInt[(m - 1)];
      }
    }
    lox = i;
    loy = arrayOfInt[0];
    hix = j;
    hiy = arrayOfInt[(k + 1)];
  }
  
  public final int getLoX()
  {
    return lox;
  }
  
  public final int getLoY()
  {
    return loy;
  }
  
  public final int getHiX()
  {
    return hix;
  }
  
  public final int getHiY()
  {
    return hiy;
  }
  
  public final int getWidth()
  {
    if (hix < lox) {
      return 0;
    }
    int i;
    if ((i = hix - lox) < 0) {
      i = Integer.MAX_VALUE;
    }
    return i;
  }
  
  public final int getHeight()
  {
    if (hiy < loy) {
      return 0;
    }
    int i;
    if ((i = hiy - loy) < 0) {
      i = Integer.MAX_VALUE;
    }
    return i;
  }
  
  public boolean isEmpty()
  {
    return (hix <= lox) || (hiy <= loy);
  }
  
  public boolean isRectangular()
  {
    return bands == null;
  }
  
  public boolean contains(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < lox) || (paramInt1 >= hix) || (paramInt2 < loy) || (paramInt2 >= hiy)) {
      return false;
    }
    if (bands == null) {
      return true;
    }
    int i = 0;
    while (i < endIndex)
    {
      if (paramInt2 < bands[(i++)]) {
        return false;
      }
      int j;
      if (paramInt2 >= bands[(i++)])
      {
        j = bands[(i++)];
        i += j * 2;
      }
      else
      {
        j = bands[(i++)];
        j = i + j * 2;
        while (i < j)
        {
          if (paramInt1 < bands[(i++)]) {
            return false;
          }
          if (paramInt1 < bands[(i++)]) {
            return true;
          }
        }
        return false;
      }
    }
    return false;
  }
  
  public boolean isInsideXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return isInsideXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
  }
  
  public boolean isInsideXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return (lox >= paramInt1) && (loy >= paramInt2) && (hix <= paramInt3) && (hiy <= paramInt4);
  }
  
  public boolean isInsideQuickCheck(Region paramRegion)
  {
    return (bands == null) && (lox <= lox) && (loy <= loy) && (hix >= hix) && (hiy >= hiy);
  }
  
  public boolean intersectsQuickCheckXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return (paramInt3 > lox) && (paramInt1 < hix) && (paramInt4 > loy) && (paramInt2 < hiy);
  }
  
  public boolean intersectsQuickCheck(Region paramRegion)
  {
    return (hix > lox) && (lox < hix) && (hiy > loy) && (loy < hiy);
  }
  
  public boolean encompasses(Region paramRegion)
  {
    return (bands == null) && (lox <= lox) && (loy <= loy) && (hix >= hix) && (hiy >= hiy);
  }
  
  public boolean encompassesXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return encompassesXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4));
  }
  
  public boolean encompassesXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return (bands == null) && (lox <= paramInt1) && (loy <= paramInt2) && (hix >= paramInt3) && (hiy >= paramInt4);
  }
  
  public void getBounds(int[] paramArrayOfInt)
  {
    paramArrayOfInt[0] = lox;
    paramArrayOfInt[1] = loy;
    paramArrayOfInt[2] = hix;
    paramArrayOfInt[3] = hiy;
  }
  
  public void clipBoxToBounds(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt[0] < lox) {
      paramArrayOfInt[0] = lox;
    }
    if (paramArrayOfInt[1] < loy) {
      paramArrayOfInt[1] = loy;
    }
    if (paramArrayOfInt[2] > hix) {
      paramArrayOfInt[2] = hix;
    }
    if (paramArrayOfInt[3] > hiy) {
      paramArrayOfInt[3] = hiy;
    }
  }
  
  public RegionIterator getIterator()
  {
    return new RegionIterator(this);
  }
  
  public SpanIterator getSpanIterator()
  {
    return new RegionSpanIterator(this);
  }
  
  public SpanIterator getSpanIterator(int[] paramArrayOfInt)
  {
    SpanIterator localSpanIterator = getSpanIterator();
    localSpanIterator.intersectClipBox(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]);
    return localSpanIterator;
  }
  
  public SpanIterator filter(SpanIterator paramSpanIterator)
  {
    if (bands == null) {
      paramSpanIterator.intersectClipBox(lox, loy, hix, hiy);
    } else {
      paramSpanIterator = new RegionClipSpanIterator(this, paramSpanIterator);
    }
    return paramSpanIterator;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Region[[");
    localStringBuffer.append(lox);
    localStringBuffer.append(", ");
    localStringBuffer.append(loy);
    localStringBuffer.append(" => ");
    localStringBuffer.append(hix);
    localStringBuffer.append(", ");
    localStringBuffer.append(hiy);
    localStringBuffer.append("]");
    if (bands != null)
    {
      int i = 0;
      while (i < endIndex)
      {
        localStringBuffer.append("y{");
        localStringBuffer.append(bands[(i++)]);
        localStringBuffer.append(",");
        localStringBuffer.append(bands[(i++)]);
        localStringBuffer.append("}[");
        int j = bands[(i++)];
        j = i + j * 2;
        while (i < j)
        {
          localStringBuffer.append("x(");
          localStringBuffer.append(bands[(i++)]);
          localStringBuffer.append(", ");
          localStringBuffer.append(bands[(i++)]);
          localStringBuffer.append(")");
        }
        localStringBuffer.append("]");
      }
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
  
  public int hashCode()
  {
    return isEmpty() ? 0 : lox * 3 + loy * 5 + hix * 7 + hiy * 9;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Region)) {
      return false;
    }
    Region localRegion = (Region)paramObject;
    if (isEmpty()) {
      return localRegion.isEmpty();
    }
    if (localRegion.isEmpty()) {
      return false;
    }
    if ((lox != lox) || (loy != loy) || (hix != hix) || (hiy != hiy)) {
      return false;
    }
    if (bands == null) {
      return bands == null;
    }
    if (bands == null) {
      return false;
    }
    if (endIndex != endIndex) {
      return false;
    }
    int[] arrayOfInt1 = bands;
    int[] arrayOfInt2 = bands;
    for (int i = 0; i < endIndex; i++) {
      if (arrayOfInt1[i] != arrayOfInt2[i]) {
        return false;
      }
    }
    return true;
  }
  
  static
  {
    initIDs();
  }
  
  private static final class ImmutableRegion
    extends Region
  {
    protected ImmutableRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt2, paramInt3, paramInt4);
    }
    
    public void appendSpans(SpanIterator paramSpanIterator) {}
    
    public void setOutputArea(Rectangle paramRectangle) {}
    
    public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void setOutputArea(int[] paramArrayOfInt) {}
    
    public void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\Region.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */