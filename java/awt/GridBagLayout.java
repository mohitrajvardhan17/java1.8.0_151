package java.awt;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;

public class GridBagLayout
  implements LayoutManager2, Serializable
{
  static final int EMPIRICMULTIPLIER = 2;
  protected static final int MAXGRIDSIZE = 512;
  protected static final int MINSIZE = 1;
  protected static final int PREFERREDSIZE = 2;
  protected Hashtable<Component, GridBagConstraints> comptable = new Hashtable();
  protected GridBagConstraints defaultConstraints = new GridBagConstraints();
  protected GridBagLayoutInfo layoutInfo;
  public int[] columnWidths;
  public int[] rowHeights;
  public double[] columnWeights;
  public double[] rowWeights;
  private Component componentAdjusting;
  transient boolean rightToLeft = false;
  static final long serialVersionUID = 8838754796412211005L;
  
  public GridBagLayout() {}
  
  public void setConstraints(Component paramComponent, GridBagConstraints paramGridBagConstraints)
  {
    comptable.put(paramComponent, (GridBagConstraints)paramGridBagConstraints.clone());
  }
  
  public GridBagConstraints getConstraints(Component paramComponent)
  {
    GridBagConstraints localGridBagConstraints = (GridBagConstraints)comptable.get(paramComponent);
    if (localGridBagConstraints == null)
    {
      setConstraints(paramComponent, defaultConstraints);
      localGridBagConstraints = (GridBagConstraints)comptable.get(paramComponent);
    }
    return (GridBagConstraints)localGridBagConstraints.clone();
  }
  
  protected GridBagConstraints lookupConstraints(Component paramComponent)
  {
    GridBagConstraints localGridBagConstraints = (GridBagConstraints)comptable.get(paramComponent);
    if (localGridBagConstraints == null)
    {
      setConstraints(paramComponent, defaultConstraints);
      localGridBagConstraints = (GridBagConstraints)comptable.get(paramComponent);
    }
    return localGridBagConstraints;
  }
  
  private void removeConstraints(Component paramComponent)
  {
    comptable.remove(paramComponent);
  }
  
  public Point getLayoutOrigin()
  {
    Point localPoint = new Point(0, 0);
    if (layoutInfo != null)
    {
      x = layoutInfo.startx;
      y = layoutInfo.starty;
    }
    return localPoint;
  }
  
  public int[][] getLayoutDimensions()
  {
    if (layoutInfo == null) {
      return new int[2][0];
    }
    int[][] arrayOfInt = new int[2][];
    arrayOfInt[0] = new int[layoutInfo.width];
    arrayOfInt[1] = new int[layoutInfo.height];
    System.arraycopy(layoutInfo.minWidth, 0, arrayOfInt[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.minHeight, 0, arrayOfInt[1], 0, layoutInfo.height);
    return arrayOfInt;
  }
  
  public double[][] getLayoutWeights()
  {
    if (layoutInfo == null) {
      return new double[2][0];
    }
    double[][] arrayOfDouble = new double[2][];
    arrayOfDouble[0] = new double[layoutInfo.width];
    arrayOfDouble[1] = new double[layoutInfo.height];
    System.arraycopy(layoutInfo.weightX, 0, arrayOfDouble[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.weightY, 0, arrayOfDouble[1], 0, layoutInfo.height);
    return arrayOfDouble;
  }
  
  public Point location(int paramInt1, int paramInt2)
  {
    Point localPoint = new Point(0, 0);
    if (layoutInfo == null) {
      return localPoint;
    }
    int j = layoutInfo.startx;
    if (!rightToLeft) {
      for (i = 0; i < layoutInfo.width; i++)
      {
        j += layoutInfo.minWidth[i];
        if (j > paramInt1) {
          break;
        }
      }
    }
    for (int i = layoutInfo.width - 1; (i >= 0) && (j <= paramInt1); i--) {
      j += layoutInfo.minWidth[i];
    }
    i++;
    x = i;
    j = layoutInfo.starty;
    for (i = 0; i < layoutInfo.height; i++)
    {
      j += layoutInfo.minHeight[i];
      if (j > paramInt2) {
        break;
      }
    }
    y = i;
    return localPoint;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void addLayoutComponent(Component paramComponent, Object paramObject)
  {
    if ((paramObject instanceof GridBagConstraints)) {
      setConstraints(paramComponent, (GridBagConstraints)paramObject);
    } else if (paramObject != null) {
      throw new IllegalArgumentException("cannot add to layout: constraints must be a GridBagConstraint");
    }
  }
  
  public void removeLayoutComponent(Component paramComponent)
  {
    removeConstraints(paramComponent);
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    GridBagLayoutInfo localGridBagLayoutInfo = getLayoutInfo(paramContainer, 2);
    return getMinSize(paramContainer, localGridBagLayoutInfo);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    GridBagLayoutInfo localGridBagLayoutInfo = getLayoutInfo(paramContainer, 1);
    return getMinSize(paramContainer, localGridBagLayoutInfo);
  }
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public float getLayoutAlignmentX(Container paramContainer)
  {
    return 0.5F;
  }
  
  public float getLayoutAlignmentY(Container paramContainer)
  {
    return 0.5F;
  }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void layoutContainer(Container paramContainer)
  {
    arrangeGrid(paramContainer);
  }
  
  public String toString()
  {
    return getClass().getName();
  }
  
  protected GridBagLayoutInfo getLayoutInfo(Container paramContainer, int paramInt)
  {
    return GetLayoutInfo(paramContainer, paramInt);
  }
  
  private long[] preInitMaximumArraySizes(Container paramContainer)
  {
    Component[] arrayOfComponent = paramContainer.getComponents();
    int n = 0;
    int i1 = 0;
    long[] arrayOfLong = new long[2];
    for (int i2 = 0; i2 < arrayOfComponent.length; i2++)
    {
      Component localComponent = arrayOfComponent[i2];
      if (localComponent.isVisible())
      {
        GridBagConstraints localGridBagConstraints = lookupConstraints(localComponent);
        int i = gridx;
        int j = gridy;
        int k = gridwidth;
        int m = gridheight;
        if (i < 0)
        {
          i1++;
          i = i1;
        }
        if (j < 0)
        {
          n++;
          j = n;
        }
        if (k <= 0) {
          k = 1;
        }
        if (m <= 0) {
          m = 1;
        }
        n = Math.max(j + m, n);
        i1 = Math.max(i + k, i1);
      }
    }
    arrayOfLong[0] = n;
    arrayOfLong[1] = i1;
    return arrayOfLong;
  }
  
  protected GridBagLayoutInfo GetLayoutInfo(Container paramContainer, int paramInt)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Component[] arrayOfComponent = paramContainer.getComponents();
      int i5 = 0;
      int i6 = 0;
      int i7 = 1;
      int i8 = 1;
      int i11 = 0;
      int i12 = 0;
      int j;
      int i = j = 0;
      int i10;
      int i9 = i10 = -1;
      long[] arrayOfLong = preInitMaximumArraySizes(paramContainer);
      i11 = 2L * arrayOfLong[0] > 2147483647L ? Integer.MAX_VALUE : 2 * (int)arrayOfLong[0];
      i12 = 2L * arrayOfLong[1] > 2147483647L ? Integer.MAX_VALUE : 2 * (int)arrayOfLong[1];
      if (rowHeights != null) {
        i11 = Math.max(i11, rowHeights.length);
      }
      if (columnWidths != null) {
        i12 = Math.max(i12, columnWidths.length);
      }
      int[] arrayOfInt1 = new int[i11];
      int[] arrayOfInt2 = new int[i12];
      int i14 = 0;
      Component localComponent;
      GridBagConstraints localGridBagConstraints;
      int i1;
      int i2;
      for (int k = 0; k < arrayOfComponent.length; k++)
      {
        localComponent = arrayOfComponent[k];
        if (localComponent.isVisible())
        {
          localGridBagConstraints = lookupConstraints(localComponent);
          i5 = gridx;
          i6 = gridy;
          i7 = gridwidth;
          if (i7 <= 0) {
            i7 = 1;
          }
          i8 = gridheight;
          if (i8 <= 0) {
            i8 = 1;
          }
          if ((i5 < 0) && (i6 < 0)) {
            if (i9 >= 0) {
              i6 = i9;
            } else if (i10 >= 0) {
              i5 = i10;
            } else {
              i6 = 0;
            }
          }
          if (i5 < 0)
          {
            i1 = 0;
            for (m = i6; m < i6 + i8; m++) {
              i1 = Math.max(i1, arrayOfInt1[m]);
            }
            i5 = i1 - i5 - 1;
            if (i5 < 0) {
              i5 = 0;
            }
          }
          else if (i6 < 0)
          {
            i2 = 0;
            for (m = i5; m < i5 + i7; m++) {
              i2 = Math.max(i2, arrayOfInt2[m]);
            }
            i6 = i2 - i6 - 1;
            if (i6 < 0) {
              i6 = 0;
            }
          }
          i1 = i5 + i7;
          if (i < i1) {
            i = i1;
          }
          i2 = i6 + i8;
          if (j < i2) {
            j = i2;
          }
          for (m = i5; m < i5 + i7; m++) {
            arrayOfInt2[m] = i2;
          }
          for (m = i6; m < i6 + i8; m++) {
            arrayOfInt1[m] = i1;
          }
          Dimension localDimension;
          if (paramInt == 2) {
            localDimension = localComponent.getPreferredSize();
          } else {
            localDimension = localComponent.getMinimumSize();
          }
          minWidth = width;
          minHeight = height;
          if (calculateBaseline(localComponent, localGridBagConstraints, localDimension)) {
            i14 = 1;
          }
          if ((gridheight == 0) && (gridwidth == 0)) {
            i9 = i10 = -1;
          }
          if ((gridheight == 0) && (i9 < 0)) {
            i10 = i5 + i7;
          } else if ((gridwidth == 0) && (i10 < 0)) {
            i9 = i6 + i8;
          }
        }
      }
      if ((columnWidths != null) && (i < columnWidths.length)) {
        i = columnWidths.length;
      }
      if ((rowHeights != null) && (j < rowHeights.length)) {
        j = rowHeights.length;
      }
      GridBagLayoutInfo localGridBagLayoutInfo = new GridBagLayoutInfo(i, j);
      i9 = i10 = -1;
      Arrays.fill(arrayOfInt1, 0);
      Arrays.fill(arrayOfInt2, 0);
      int[] arrayOfInt3 = null;
      int[] arrayOfInt4 = null;
      short[] arrayOfShort = null;
      if (i14 != 0)
      {
        maxAscent = (arrayOfInt3 = new int[j]);
        maxDescent = (arrayOfInt4 = new int[j]);
        baselineType = (arrayOfShort = new short[j]);
        hasBaseline = true;
      }
      int i3;
      for (k = 0; k < arrayOfComponent.length; k++)
      {
        localComponent = arrayOfComponent[k];
        if (localComponent.isVisible())
        {
          localGridBagConstraints = lookupConstraints(localComponent);
          i5 = gridx;
          i6 = gridy;
          i7 = gridwidth;
          i8 = gridheight;
          if ((i5 < 0) && (i6 < 0)) {
            if (i9 >= 0) {
              i6 = i9;
            } else if (i10 >= 0) {
              i5 = i10;
            } else {
              i6 = 0;
            }
          }
          if (i5 < 0)
          {
            if (i8 <= 0)
            {
              i8 += height - i6;
              if (i8 < 1) {
                i8 = 1;
              }
            }
            i1 = 0;
            for (m = i6; m < i6 + i8; m++) {
              i1 = Math.max(i1, arrayOfInt1[m]);
            }
            i5 = i1 - i5 - 1;
            if (i5 < 0) {
              i5 = 0;
            }
          }
          else if (i6 < 0)
          {
            if (i7 <= 0)
            {
              i7 += width - i5;
              if (i7 < 1) {
                i7 = 1;
              }
            }
            i2 = 0;
            for (m = i5; m < i5 + i7; m++) {
              i2 = Math.max(i2, arrayOfInt2[m]);
            }
            i6 = i2 - i6 - 1;
            if (i6 < 0) {
              i6 = 0;
            }
          }
          if (i7 <= 0)
          {
            i7 += width - i5;
            if (i7 < 1) {
              i7 = 1;
            }
          }
          if (i8 <= 0)
          {
            i8 += height - i6;
            if (i8 < 1) {
              i8 = 1;
            }
          }
          i1 = i5 + i7;
          i2 = i6 + i8;
          for (m = i5; m < i5 + i7; m++) {
            arrayOfInt2[m] = i2;
          }
          for (m = i6; m < i6 + i8; m++) {
            arrayOfInt1[m] = i1;
          }
          if ((gridheight == 0) && (gridwidth == 0)) {
            i9 = i10 = -1;
          }
          if ((gridheight == 0) && (i9 < 0)) {
            i10 = i5 + i7;
          } else if ((gridwidth == 0) && (i10 < 0)) {
            i9 = i6 + i8;
          }
          tempX = i5;
          tempY = i6;
          tempWidth = i7;
          tempHeight = i8;
          int i13 = anchor;
          if (i14 != 0) {
            switch (i13)
            {
            case 256: 
            case 512: 
            case 768: 
              if (ascent >= 0)
              {
                if (i8 == 1)
                {
                  arrayOfInt3[i6] = Math.max(arrayOfInt3[i6], ascent);
                  arrayOfInt4[i6] = Math.max(arrayOfInt4[i6], descent);
                }
                else if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT)
                {
                  arrayOfInt4[(i6 + i8 - 1)] = Math.max(arrayOfInt4[(i6 + i8 - 1)], descent);
                }
                else
                {
                  arrayOfInt3[i6] = Math.max(arrayOfInt3[i6], ascent);
                }
                if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT)
                {
                  int tmp1469_1468 = (i6 + i8 - 1);
                  short[] tmp1469_1460 = arrayOfShort;
                  tmp1469_1460[tmp1469_1468] = ((short)(tmp1469_1460[tmp1469_1468] | 1 << baselineResizeBehavior.ordinal()));
                }
                else
                {
                  int tmp1491_1489 = i6;
                  short[] tmp1491_1487 = arrayOfShort;
                  tmp1491_1487[tmp1491_1489] = ((short)(tmp1491_1487[tmp1491_1489] | 1 << baselineResizeBehavior.ordinal()));
                }
              }
              break;
            case 1024: 
            case 1280: 
            case 1536: 
              i3 = minHeight + insets.top + ipady;
              arrayOfInt3[i6] = Math.max(arrayOfInt3[i6], i3);
              arrayOfInt4[i6] = Math.max(arrayOfInt4[i6], insets.bottom);
              break;
            case 1792: 
            case 2048: 
            case 2304: 
              i3 = minHeight + insets.bottom + ipady;
              arrayOfInt4[i6] = Math.max(arrayOfInt4[i6], i3);
              arrayOfInt3[i6] = Math.max(arrayOfInt3[i6], insets.top);
            }
          }
        }
      }
      weightX = new double[i12];
      weightY = new double[i11];
      minWidth = new int[i12];
      minHeight = new int[i11];
      if (columnWidths != null) {
        System.arraycopy(columnWidths, 0, minWidth, 0, columnWidths.length);
      }
      if (rowHeights != null) {
        System.arraycopy(rowHeights, 0, minHeight, 0, rowHeights.length);
      }
      if (columnWeights != null) {
        System.arraycopy(columnWeights, 0, weightX, 0, Math.min(weightX.length, columnWeights.length));
      }
      if (rowWeights != null) {
        System.arraycopy(rowWeights, 0, weightY, 0, Math.min(weightY.length, rowWeights.length));
      }
      int i4 = Integer.MAX_VALUE;
      int m = 1;
      while (m != Integer.MAX_VALUE)
      {
        for (k = 0; k < arrayOfComponent.length; k++)
        {
          localComponent = arrayOfComponent[k];
          if (localComponent.isVisible())
          {
            localGridBagConstraints = lookupConstraints(localComponent);
            double d1;
            int n;
            double d2;
            double d3;
            if (tempWidth == m)
            {
              i1 = tempX + tempWidth;
              d1 = weightx;
              for (n = tempX; n < i1; n++) {
                d1 -= weightX[n];
              }
              if (d1 > 0.0D)
              {
                d2 = 0.0D;
                for (n = tempX; n < i1; n++) {
                  d2 += weightX[n];
                }
                for (n = tempX; (d2 > 0.0D) && (n < i1); n++)
                {
                  d3 = weightX[n];
                  double d4 = d3 * d1 / d2;
                  weightX[n] += d4;
                  d1 -= d4;
                  d2 -= d3;
                }
                weightX[(i1 - 1)] += d1;
              }
              i3 = minWidth + ipadx + insets.left + insets.right;
              for (n = tempX; n < i1; n++) {
                i3 -= minWidth[n];
              }
              if (i3 > 0)
              {
                d2 = 0.0D;
                for (n = tempX; n < i1; n++) {
                  d2 += weightX[n];
                }
                for (n = tempX; (d2 > 0.0D) && (n < i1); n++)
                {
                  d3 = weightX[n];
                  int i15 = (int)(d3 * i3 / d2);
                  minWidth[n] += i15;
                  i3 -= i15;
                  d2 -= d3;
                }
                minWidth[(i1 - 1)] += i3;
              }
            }
            else if ((tempWidth > m) && (tempWidth < i4))
            {
              i4 = tempWidth;
            }
            if (tempHeight == m)
            {
              i2 = tempY + tempHeight;
              d1 = weighty;
              for (n = tempY; n < i2; n++) {
                d1 -= weightY[n];
              }
              if (d1 > 0.0D)
              {
                d2 = 0.0D;
                for (n = tempY; n < i2; n++) {
                  d2 += weightY[n];
                }
                for (n = tempY; (d2 > 0.0D) && (n < i2); n++)
                {
                  d3 = weightY[n];
                  double d5 = d3 * d1 / d2;
                  weightY[n] += d5;
                  d1 -= d5;
                  d2 -= d3;
                }
                weightY[(i2 - 1)] += d1;
              }
              i3 = -1;
              if (i14 != 0) {
                switch (anchor)
                {
                case 256: 
                case 512: 
                case 768: 
                  if (ascent >= 0) {
                    if (tempHeight == 1) {
                      i3 = arrayOfInt3[tempY] + arrayOfInt4[tempY];
                    } else if (baselineResizeBehavior != Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                      i3 = arrayOfInt3[tempY] + descent;
                    } else {
                      i3 = ascent + arrayOfInt4[(tempY + tempHeight - 1)];
                    }
                  }
                  break;
                case 1024: 
                case 1280: 
                case 1536: 
                  i3 = insets.top + minHeight + ipady + arrayOfInt4[tempY];
                  break;
                case 1792: 
                case 2048: 
                case 2304: 
                  i3 = arrayOfInt3[tempY] + minHeight + insets.bottom + ipady;
                }
              }
              if (i3 == -1) {
                i3 = minHeight + ipady + insets.top + insets.bottom;
              }
              for (n = tempY; n < i2; n++) {
                i3 -= minHeight[n];
              }
              if (i3 > 0)
              {
                d2 = 0.0D;
                for (n = tempY; n < i2; n++) {
                  d2 += weightY[n];
                }
                for (n = tempY; (d2 > 0.0D) && (n < i2); n++)
                {
                  d3 = weightY[n];
                  int i16 = (int)(d3 * i3 / d2);
                  minHeight[n] += i16;
                  i3 -= i16;
                  d2 -= d3;
                }
                minHeight[(i2 - 1)] += i3;
              }
            }
            else if ((tempHeight > m) && (tempHeight < i4))
            {
              i4 = tempHeight;
            }
          }
        }
        m = i4;
        i4 = Integer.MAX_VALUE;
      }
      return localGridBagLayoutInfo;
    }
  }
  
  private boolean calculateBaseline(Component paramComponent, GridBagConstraints paramGridBagConstraints, Dimension paramDimension)
  {
    int i = anchor;
    if ((i == 256) || (i == 512) || (i == 768))
    {
      int j = width + ipadx;
      int k = height + ipady;
      ascent = paramComponent.getBaseline(j, k);
      if (ascent >= 0)
      {
        int m = ascent;
        descent = (k - ascent + insets.bottom);
        ascent += insets.top;
        baselineResizeBehavior = paramComponent.getBaselineResizeBehavior();
        centerPadding = 0;
        if (baselineResizeBehavior == Component.BaselineResizeBehavior.CENTER_OFFSET)
        {
          int n = paramComponent.getBaseline(j, k + 1);
          centerOffset = (m - k / 2);
          if (k % 2 == 0)
          {
            if (m != n) {
              centerPadding = 1;
            }
          }
          else if (m == n)
          {
            centerOffset -= 1;
            centerPadding = 1;
          }
        }
      }
      return true;
    }
    ascent = -1;
    return false;
  }
  
  protected void adjustForGravity(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle)
  {
    AdjustForGravity(paramGridBagConstraints, paramRectangle);
  }
  
  protected void AdjustForGravity(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle)
  {
    int k = y;
    int m = height;
    if (!rightToLeft) {
      x += insets.left;
    } else {
      x -= width - insets.right;
    }
    width -= insets.left + insets.right;
    y += insets.top;
    height -= insets.top + insets.bottom;
    int i = 0;
    if ((fill != 2) && (fill != 1) && (width > minWidth + ipadx))
    {
      i = width - (minWidth + ipadx);
      width = (minWidth + ipadx);
    }
    int j = 0;
    if ((fill != 3) && (fill != 1) && (height > minHeight + ipady))
    {
      j = height - (minHeight + ipady);
      height = (minHeight + ipady);
    }
    switch (anchor)
    {
    case 256: 
      x += i / 2;
      alignOnBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 512: 
      if (rightToLeft) {
        x += i;
      }
      alignOnBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 768: 
      if (!rightToLeft) {
        x += i;
      }
      alignOnBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 1024: 
      x += i / 2;
      alignAboveBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 1280: 
      if (rightToLeft) {
        x += i;
      }
      alignAboveBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 1536: 
      if (!rightToLeft) {
        x += i;
      }
      alignAboveBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 1792: 
      x += i / 2;
      alignBelowBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 2048: 
      if (rightToLeft) {
        x += i;
      }
      alignBelowBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 2304: 
      if (!rightToLeft) {
        x += i;
      }
      alignBelowBaseline(paramGridBagConstraints, paramRectangle, k, m);
      break;
    case 10: 
      x += i / 2;
      y += j / 2;
      break;
    case 11: 
    case 19: 
      x += i / 2;
      break;
    case 12: 
      x += i;
      break;
    case 13: 
      x += i;
      y += j / 2;
      break;
    case 14: 
      x += i;
      y += j;
      break;
    case 15: 
    case 20: 
      x += i / 2;
      y += j;
      break;
    case 16: 
      y += j;
      break;
    case 17: 
      y += j / 2;
      break;
    case 18: 
      break;
    case 21: 
      if (rightToLeft) {
        x += i;
      }
      y += j / 2;
      break;
    case 22: 
      if (!rightToLeft) {
        x += i;
      }
      y += j / 2;
      break;
    case 23: 
      if (rightToLeft) {
        x += i;
      }
      break;
    case 24: 
      if (!rightToLeft) {
        x += i;
      }
      break;
    case 25: 
      if (rightToLeft) {
        x += i;
      }
      y += j;
      break;
    case 26: 
      if (!rightToLeft) {
        x += i;
      }
      y += j;
      break;
    default: 
      throw new IllegalArgumentException("illegal anchor value");
    }
  }
  
  private void alignOnBaseline(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    if (ascent >= 0)
    {
      int i;
      if (baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT)
      {
        i = paramInt1 + paramInt2 - layoutInfo.maxDescent[(tempY + tempHeight - 1)] + descent - insets.bottom;
        if (!paramGridBagConstraints.isVerticallyResizable())
        {
          y = (i - minHeight);
          height = minHeight;
        }
        else
        {
          height = (i - paramInt1 - insets.top);
        }
      }
      else
      {
        int j = ascent;
        if (layoutInfo.hasConstantDescent(tempY)) {
          i = paramInt2 - layoutInfo.maxDescent[tempY];
        } else {
          i = layoutInfo.maxAscent[tempY];
        }
        int k;
        int m;
        if (baselineResizeBehavior == Component.BaselineResizeBehavior.OTHER)
        {
          k = 0;
          j = componentAdjusting.getBaseline(width, height);
          if (j >= 0) {
            j += insets.top;
          }
          if ((j >= 0) && (j <= i)) {
            if (i + (height - j - insets.top) <= paramInt2 - insets.bottom)
            {
              k = 1;
            }
            else if (paramGridBagConstraints.isVerticallyResizable())
            {
              m = componentAdjusting.getBaseline(width, paramInt2 - insets.bottom - i + j);
              if (m >= 0) {
                m += insets.top;
              }
              if ((m >= 0) && (m <= j))
              {
                height = (paramInt2 - insets.bottom - i + j);
                j = m;
                k = 1;
              }
            }
          }
          if (k == 0)
          {
            j = ascent;
            width = minWidth;
            height = minHeight;
          }
        }
        y = (paramInt1 + i - j + insets.top);
        if (paramGridBagConstraints.isVerticallyResizable()) {
          switch (baselineResizeBehavior)
          {
          case CONSTANT_ASCENT: 
            height = Math.max(minHeight, paramInt1 + paramInt2 - y - insets.bottom);
            break;
          case CENTER_OFFSET: 
            k = y - paramInt1 - insets.top;
            m = paramInt1 + paramInt2 - y - minHeight - insets.bottom;
            int n = Math.min(k, m);
            n += n;
            if ((n > 0) && ((minHeight + centerPadding + n) / 2 + centerOffset != i)) {
              n--;
            }
            height = (minHeight + n);
            y = (paramInt1 + i - (height + centerPadding) / 2 - centerOffset);
            break;
          case OTHER: 
            break;
          }
        }
      }
    }
    else
    {
      centerVertically(paramGridBagConstraints, paramRectangle, paramInt2);
    }
  }
  
  private void alignAboveBaseline(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    if (layoutInfo.hasBaseline(tempY))
    {
      int i;
      if (layoutInfo.hasConstantDescent(tempY)) {
        i = paramInt1 + paramInt2 - layoutInfo.maxDescent[tempY];
      } else {
        i = paramInt1 + layoutInfo.maxAscent[tempY];
      }
      if (paramGridBagConstraints.isVerticallyResizable())
      {
        y = (paramInt1 + insets.top);
        height = (i - y);
      }
      else
      {
        height = (minHeight + ipady);
        y = (i - height);
      }
    }
    else
    {
      centerVertically(paramGridBagConstraints, paramRectangle, paramInt2);
    }
  }
  
  private void alignBelowBaseline(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    if (layoutInfo.hasBaseline(tempY))
    {
      if (layoutInfo.hasConstantDescent(tempY)) {
        y = (paramInt1 + paramInt2 - layoutInfo.maxDescent[tempY]);
      } else {
        y = (paramInt1 + layoutInfo.maxAscent[tempY]);
      }
      if (paramGridBagConstraints.isVerticallyResizable()) {
        height = (paramInt1 + paramInt2 - y - insets.bottom);
      }
    }
    else
    {
      centerVertically(paramGridBagConstraints, paramRectangle, paramInt2);
    }
  }
  
  private void centerVertically(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt)
  {
    if (!paramGridBagConstraints.isVerticallyResizable()) {
      y += Math.max(0, (paramInt - insets.top - insets.bottom - minHeight - ipady) / 2);
    }
  }
  
  protected Dimension getMinSize(Container paramContainer, GridBagLayoutInfo paramGridBagLayoutInfo)
  {
    return GetMinSize(paramContainer, paramGridBagLayoutInfo);
  }
  
  protected Dimension GetMinSize(Container paramContainer, GridBagLayoutInfo paramGridBagLayoutInfo)
  {
    Dimension localDimension = new Dimension();
    Insets localInsets = paramContainer.getInsets();
    int j = 0;
    for (int i = 0; i < width; i++) {
      j += minWidth[i];
    }
    width = (j + left + right);
    j = 0;
    for (i = 0; i < height; i++) {
      j += minHeight[i];
    }
    height = (j + top + bottom);
    return localDimension;
  }
  
  protected void arrangeGrid(Container paramContainer)
  {
    ArrangeGrid(paramContainer);
  }
  
  protected void ArrangeGrid(Container paramContainer)
  {
    Insets localInsets = paramContainer.getInsets();
    Component[] arrayOfComponent = paramContainer.getComponents();
    Rectangle localRectangle = new Rectangle();
    rightToLeft = (!paramContainer.getComponentOrientation().isLeftToRight());
    if ((arrayOfComponent.length == 0) && ((columnWidths == null) || (columnWidths.length == 0)) && ((rowHeights == null) || (rowHeights.length == 0))) {
      return;
    }
    GridBagLayoutInfo localGridBagLayoutInfo = getLayoutInfo(paramContainer, 2);
    Dimension localDimension = getMinSize(paramContainer, localGridBagLayoutInfo);
    if ((width < width) || (height < height))
    {
      localGridBagLayoutInfo = getLayoutInfo(paramContainer, 1);
      localDimension = getMinSize(paramContainer, localGridBagLayoutInfo);
    }
    layoutInfo = localGridBagLayoutInfo;
    width = width;
    height = height;
    int k = width - width;
    double d;
    int j;
    int n;
    if (k != 0)
    {
      d = 0.0D;
      for (j = 0; j < width; j++) {
        d += weightX[j];
      }
      if (d > 0.0D) {
        for (j = 0; j < width; j++)
        {
          n = (int)(k * weightX[j] / d);
          minWidth[j] += n;
          width += n;
          if (minWidth[j] < 0)
          {
            width -= minWidth[j];
            minWidth[j] = 0;
          }
        }
      }
      k = width - width;
    }
    else
    {
      k = 0;
    }
    int m = height - height;
    if (m != 0)
    {
      d = 0.0D;
      for (j = 0; j < height; j++) {
        d += weightY[j];
      }
      if (d > 0.0D) {
        for (j = 0; j < height; j++)
        {
          n = (int)(m * weightY[j] / d);
          minHeight[j] += n;
          height += n;
          if (minHeight[j] < 0)
          {
            height -= minHeight[j];
            minHeight[j] = 0;
          }
        }
      }
      m = height - height;
    }
    else
    {
      m = 0;
    }
    startx = (k / 2 + left);
    starty = (m / 2 + top);
    for (int i = 0; i < arrayOfComponent.length; i++)
    {
      Component localComponent = arrayOfComponent[i];
      if (localComponent.isVisible())
      {
        GridBagConstraints localGridBagConstraints = lookupConstraints(localComponent);
        if (!rightToLeft)
        {
          x = startx;
          for (j = 0; j < tempX; j++) {
            x += minWidth[j];
          }
        }
        x = (width - (k / 2 + right));
        for (j = 0; j < tempX; j++) {
          x -= minWidth[j];
        }
        y = starty;
        for (j = 0; j < tempY; j++) {
          y += minHeight[j];
        }
        width = 0;
        for (j = tempX; j < tempX + tempWidth; j++) {
          width += minWidth[j];
        }
        height = 0;
        for (j = tempY; j < tempY + tempHeight; j++) {
          height += minHeight[j];
        }
        componentAdjusting = localComponent;
        adjustForGravity(localGridBagConstraints, localRectangle);
        if (x < 0)
        {
          width += x;
          x = 0;
        }
        if (y < 0)
        {
          height += y;
          y = 0;
        }
        if ((width <= 0) || (height <= 0)) {
          localComponent.setBounds(0, 0, 0, 0);
        } else if ((x != x) || (y != y) || (width != width) || (height != height)) {
          localComponent.setBounds(x, y, width, height);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GridBagLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */