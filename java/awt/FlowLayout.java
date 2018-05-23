package java.awt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class FlowLayout
  implements LayoutManager, Serializable
{
  public static final int LEFT = 0;
  public static final int CENTER = 1;
  public static final int RIGHT = 2;
  public static final int LEADING = 3;
  public static final int TRAILING = 4;
  int align;
  int newAlign;
  int hgap;
  int vgap;
  private boolean alignOnBaseline;
  private static final long serialVersionUID = -7262534875583282631L;
  private static final int currentSerialVersion = 1;
  private int serialVersionOnStream = 1;
  
  public FlowLayout()
  {
    this(1, 5, 5);
  }
  
  public FlowLayout(int paramInt)
  {
    this(paramInt, 5, 5);
  }
  
  public FlowLayout(int paramInt1, int paramInt2, int paramInt3)
  {
    hgap = paramInt2;
    vgap = paramInt3;
    setAlignment(paramInt1);
  }
  
  public int getAlignment()
  {
    return newAlign;
  }
  
  public void setAlignment(int paramInt)
  {
    newAlign = paramInt;
    switch (paramInt)
    {
    case 3: 
      align = 0;
      break;
    case 4: 
      align = 2;
      break;
    default: 
      align = paramInt;
    }
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
  
  public void setAlignOnBaseline(boolean paramBoolean)
  {
    alignOnBaseline = paramBoolean;
  }
  
  public boolean getAlignOnBaseline()
  {
    return alignOnBaseline;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Dimension localDimension1 = new Dimension(0, 0);
      int i = paramContainer.getComponentCount();
      int j = 1;
      boolean bool = getAlignOnBaseline();
      int k = 0;
      int m = 0;
      for (int n = 0; n < i; n++)
      {
        Component localComponent = paramContainer.getComponent(n);
        if (localComponent.isVisible())
        {
          Dimension localDimension2 = localComponent.getPreferredSize();
          height = Math.max(height, height);
          if (j != 0) {
            j = 0;
          } else {
            width += hgap;
          }
          width += width;
          if (bool)
          {
            int i1 = localComponent.getBaseline(width, height);
            if (i1 >= 0)
            {
              k = Math.max(k, i1);
              m = Math.max(m, height - i1);
            }
          }
        }
      }
      if (bool) {
        height = Math.max(k + m, height);
      }
      Insets localInsets = paramContainer.getInsets();
      width += left + right + hgap * 2;
      height += top + bottom + vgap * 2;
      return localDimension1;
    }
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      boolean bool = getAlignOnBaseline();
      Dimension localDimension1 = new Dimension(0, 0);
      int i = paramContainer.getComponentCount();
      int j = 0;
      int k = 0;
      int m = 1;
      for (int n = 0; n < i; n++)
      {
        Component localComponent = paramContainer.getComponent(n);
        if (visible)
        {
          Dimension localDimension2 = localComponent.getMinimumSize();
          height = Math.max(height, height);
          if (m != 0) {
            m = 0;
          } else {
            width += hgap;
          }
          width += width;
          if (bool)
          {
            int i1 = localComponent.getBaseline(width, height);
            if (i1 >= 0)
            {
              j = Math.max(j, i1);
              k = Math.max(k, height - i1);
            }
          }
        }
      }
      if (bool) {
        height = Math.max(j + k, height);
      }
      Insets localInsets = paramContainer.getInsets();
      width += left + right + hgap * 2;
      height += top + bottom + vgap * 2;
      return localDimension1;
    }
  }
  
  private int moveComponents(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    switch (newAlign)
    {
    case 0: 
      paramInt1 += (paramBoolean1 ? 0 : paramInt3);
      break;
    case 1: 
      paramInt1 += paramInt3 / 2;
      break;
    case 2: 
      paramInt1 += (paramBoolean1 ? paramInt3 : 0);
      break;
    case 3: 
      break;
    case 4: 
      paramInt1 += paramInt3;
    }
    int i = 0;
    int j = 0;
    int k = 0;
    if (paramBoolean2)
    {
      m = 0;
      for (int n = paramInt5; n < paramInt6; n++)
      {
        Component localComponent2 = paramContainer.getComponent(n);
        if (visible) {
          if (paramArrayOfInt1[n] >= 0)
          {
            i = Math.max(i, paramArrayOfInt1[n]);
            m = Math.max(m, paramArrayOfInt2[n]);
          }
          else
          {
            j = Math.max(localComponent2.getHeight(), j);
          }
        }
      }
      paramInt4 = Math.max(i + m, j);
      k = (paramInt4 - i - m) / 2;
    }
    for (int m = paramInt5; m < paramInt6; m++)
    {
      Component localComponent1 = paramContainer.getComponent(m);
      if (localComponent1.isVisible())
      {
        int i1;
        if ((paramBoolean2) && (paramArrayOfInt1[m] >= 0)) {
          i1 = paramInt2 + k + i - paramArrayOfInt1[m];
        } else {
          i1 = paramInt2 + (paramInt4 - height) / 2;
        }
        if (paramBoolean1) {
          localComponent1.setLocation(paramInt1, i1);
        } else {
          localComponent1.setLocation(width - paramInt1 - width, i1);
        }
        paramInt1 += width + hgap;
      }
    }
    return paramInt4;
  }
  
  public void layoutContainer(Container paramContainer)
  {
    synchronized (paramContainer.getTreeLock())
    {
      Insets localInsets = paramContainer.getInsets();
      int i = width - (left + right + hgap * 2);
      int j = paramContainer.getComponentCount();
      int k = 0;
      int m = top + vgap;
      int n = 0;
      int i1 = 0;
      boolean bool1 = paramContainer.getComponentOrientation().isLeftToRight();
      boolean bool2 = getAlignOnBaseline();
      int[] arrayOfInt1 = null;
      int[] arrayOfInt2 = null;
      if (bool2)
      {
        arrayOfInt1 = new int[j];
        arrayOfInt2 = new int[j];
      }
      for (int i2 = 0; i2 < j; i2++)
      {
        Component localComponent = paramContainer.getComponent(i2);
        if (localComponent.isVisible())
        {
          Dimension localDimension = localComponent.getPreferredSize();
          localComponent.setSize(width, height);
          if (bool2)
          {
            int i3 = localComponent.getBaseline(width, height);
            if (i3 >= 0)
            {
              arrayOfInt1[i2] = i3;
              arrayOfInt2[i2] = (height - i3);
            }
            else
            {
              arrayOfInt1[i2] = -1;
            }
          }
          if ((k == 0) || (k + width <= i))
          {
            if (k > 0) {
              k += hgap;
            }
            k += width;
            n = Math.max(n, height);
          }
          else
          {
            n = moveComponents(paramContainer, left + hgap, m, i - k, n, i1, i2, bool1, bool2, arrayOfInt1, arrayOfInt2);
            k = width;
            m += vgap + n;
            n = height;
            i1 = i2;
          }
        }
      }
      moveComponents(paramContainer, left + hgap, m, i - k, n, i1, j, bool1, bool2, arrayOfInt1, arrayOfInt2);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (serialVersionOnStream < 1) {
      setAlignment(align);
    }
    serialVersionOnStream = 1;
  }
  
  public String toString()
  {
    String str = "";
    switch (align)
    {
    case 0: 
      str = ",align=left";
      break;
    case 1: 
      str = ",align=center";
      break;
    case 2: 
      str = ",align=right";
      break;
    case 3: 
      str = ",align=leading";
      break;
    case 4: 
      str = ",align=trailing";
    }
    return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\FlowLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */