package java.awt;

import java.io.Serializable;

public class GridLayout
  implements LayoutManager, Serializable
{
  private static final long serialVersionUID = -7411804673224730901L;
  int hgap;
  int vgap;
  int rows;
  int cols;
  
  public GridLayout()
  {
    this(1, 0, 0, 0);
  }
  
  public GridLayout(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, 0, 0);
  }
  
  public GridLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      throw new IllegalArgumentException("rows and cols cannot both be zero");
    }
    rows = paramInt1;
    cols = paramInt2;
    hgap = paramInt3;
    vgap = paramInt4;
  }
  
  public int getRows()
  {
    return rows;
  }
  
  public void setRows(int paramInt)
  {
    if ((paramInt == 0) && (cols == 0)) {
      throw new IllegalArgumentException("rows and cols cannot both be zero");
    }
    rows = paramInt;
  }
  
  public int getColumns()
  {
    return cols;
  }
  
  public void setColumns(int paramInt)
  {
    if ((paramInt == 0) && (rows == 0)) {
      throw new IllegalArgumentException("rows and cols cannot both be zero");
    }
    cols = paramInt;
  }
  
  public int getHgap()
  {
    return hgap;
  }
  
  public void setHgap(int paramInt)
  {
    hgap = paramInt;
  }
  
  public int getVgap()
  {
    return vgap;
  }
  
  public void setVgap(int paramInt)
  {
    vgap = paramInt;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = rows;
      int k = cols;
      if (j > 0) {
        k = (i + j - 1) / j;
      } else {
        j = (i + k - 1) / k;
      }
      int m = 0;
      int n = 0;
      for (int i1 = 0; i1 < i; i1++)
      {
        Component localComponent = paramContainer.getComponent(i1);
        Dimension localDimension = localComponent.getPreferredSize();
        if (m < width) {
          m = width;
        }
        if (n < height) {
          n = height;
        }
      }
      return new Dimension(left + right + k * m + (k - 1) * hgap, top + bottom + j * n + (j - 1) * vgap);
    }
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = rows;
      int k = cols;
      if (j > 0) {
        k = (i + j - 1) / j;
      } else {
        j = (i + k - 1) / k;
      }
      int m = 0;
      int n = 0;
      for (int i1 = 0; i1 < i; i1++)
      {
        Component localComponent = paramContainer.getComponent(i1);
        Dimension localDimension = localComponent.getMinimumSize();
        if (m < width) {
          m = width;
        }
        if (n < height) {
          n = height;
        }
      }
      return new Dimension(left + right + k * m + (k - 1) * hgap, top + bottom + j * n + (j - 1) * vgap);
    }
  }
  
  public void layoutContainer(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = rows;
      int k = cols;
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      if (i == 0) {
        return;
      }
      if (j > 0) {
        k = (i + j - 1) / j;
      } else {
        j = (i + k - 1) / k;
      }
      int m = (k - 1) * hgap;
      int n = width - (left + right);
      int i1 = (n - m) / k;
      int i2 = (n - (i1 * k + m)) / 2;
      int i3 = (j - 1) * vgap;
      int i4 = height - (top + bottom);
      int i5 = (i4 - i3) / j;
      int i6 = (i4 - (i5 * j + i3)) / 2;
      int i7;
      int i8;
      int i9;
      int i10;
      int i11;
      if (bool)
      {
        i7 = 0;
        i8 = left + i2;
        while (i7 < k)
        {
          i9 = 0;
          i10 = top + i6;
          while (i9 < j)
          {
            i11 = i9 * k + i7;
            if (i11 < i) {
              paramContainer.getComponent(i11).setBounds(i8, i10, i1, i5);
            }
            i9++;
            i10 += i5 + vgap;
          }
          i7++;
          i8 += i1 + hgap;
        }
      }
      else
      {
        i7 = 0;
        i8 = width - right - i1 - i2;
        while (i7 < k)
        {
          i9 = 0;
          i10 = top + i6;
          while (i9 < j)
          {
            i11 = i9 * k + i7;
            if (i11 < i) {
              paramContainer.getComponent(i11).setBounds(i8, i10, i1, i5);
            }
            i9++;
            i10 += i5 + vgap;
          }
          i7++;
          i8 -= i1 + hgap;
        }
      }
    }
  }
  
  public String toString()
  {
    return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + ",rows=" + rows + ",cols=" + cols + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\GridLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */