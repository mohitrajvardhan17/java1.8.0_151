package javax.swing.colorchooser;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

class SmartGridLayout
  implements LayoutManager, Serializable
{
  int rows = 2;
  int columns = 2;
  int xGap = 2;
  int yGap = 2;
  int componentCount = 0;
  Component[][] layoutGrid;
  
  public SmartGridLayout(int paramInt1, int paramInt2)
  {
    rows = paramInt2;
    columns = paramInt1;
    layoutGrid = new Component[paramInt1][paramInt2];
  }
  
  public void layoutContainer(Container paramContainer)
  {
    buildLayoutGrid(paramContainer);
    int[] arrayOfInt1 = new int[rows];
    int[] arrayOfInt2 = new int[columns];
    for (int i = 0; i < rows; i++) {
      arrayOfInt1[i] = computeRowHeight(i);
    }
    for (i = 0; i < columns; i++) {
      arrayOfInt2[i] = computeColumnWidth(i);
    }
    Insets localInsets = paramContainer.getInsets();
    int j;
    int k;
    int m;
    int n;
    Component localComponent;
    if (paramContainer.getComponentOrientation().isLeftToRight())
    {
      j = left;
      for (k = 0; k < columns; k++)
      {
        m = top;
        for (n = 0; n < rows; n++)
        {
          localComponent = layoutGrid[k][n];
          localComponent.setBounds(j, m, arrayOfInt2[k], arrayOfInt1[n]);
          m += arrayOfInt1[n] + yGap;
        }
        j += arrayOfInt2[k] + xGap;
      }
    }
    else
    {
      j = paramContainer.getWidth() - right;
      for (k = 0; k < columns; k++)
      {
        m = top;
        j -= arrayOfInt2[k];
        for (n = 0; n < rows; n++)
        {
          localComponent = layoutGrid[k][n];
          localComponent.setBounds(j, m, arrayOfInt2[k], arrayOfInt1[n]);
          m += arrayOfInt1[n] + yGap;
        }
        j -= xGap;
      }
    }
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    buildLayoutGrid(paramContainer);
    Insets localInsets = paramContainer.getInsets();
    int i = 0;
    int j = 0;
    for (int k = 0; k < rows; k++) {
      i += computeRowHeight(k);
    }
    for (k = 0; k < columns; k++) {
      j += computeColumnWidth(k);
    }
    i += yGap * (rows - 1) + top + bottom;
    j += xGap * (columns - 1) + right + left;
    return new Dimension(j, i);
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    return minimumLayoutSize(paramContainer);
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  private void buildLayoutGrid(Container paramContainer)
  {
    Component[] arrayOfComponent = paramContainer.getComponents();
    for (int i = 0; i < arrayOfComponent.length; i++)
    {
      int j = 0;
      int k = 0;
      if (i != 0)
      {
        k = i % columns;
        j = (i - k) / columns;
      }
      layoutGrid[k][j] = arrayOfComponent[i];
    }
  }
  
  private int computeColumnWidth(int paramInt)
  {
    int i = 1;
    for (int j = 0; j < rows; j++)
    {
      int k = layoutGrid[paramInt][j].getPreferredSize().width;
      if (k > i) {
        i = k;
      }
    }
    return i;
  }
  
  private int computeRowHeight(int paramInt)
  {
    int i = 1;
    for (int j = 0; j < columns; j++)
    {
      int k = layoutGrid[j][paramInt].getPreferredSize().height;
      if (k > i) {
        i = k;
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\SmartGridLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */