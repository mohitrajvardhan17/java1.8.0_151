package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

public class ScrollPaneLayout
  implements LayoutManager, ScrollPaneConstants, Serializable
{
  protected JViewport viewport;
  protected JScrollBar vsb;
  protected JScrollBar hsb;
  protected JViewport rowHead;
  protected JViewport colHead;
  protected Component lowerLeft;
  protected Component lowerRight;
  protected Component upperLeft;
  protected Component upperRight;
  protected int vsbPolicy = 20;
  protected int hsbPolicy = 30;
  
  public ScrollPaneLayout() {}
  
  public void syncWithScrollPane(JScrollPane paramJScrollPane)
  {
    viewport = paramJScrollPane.getViewport();
    vsb = paramJScrollPane.getVerticalScrollBar();
    hsb = paramJScrollPane.getHorizontalScrollBar();
    rowHead = paramJScrollPane.getRowHeader();
    colHead = paramJScrollPane.getColumnHeader();
    lowerLeft = paramJScrollPane.getCorner("LOWER_LEFT_CORNER");
    lowerRight = paramJScrollPane.getCorner("LOWER_RIGHT_CORNER");
    upperLeft = paramJScrollPane.getCorner("UPPER_LEFT_CORNER");
    upperRight = paramJScrollPane.getCorner("UPPER_RIGHT_CORNER");
    vsbPolicy = paramJScrollPane.getVerticalScrollBarPolicy();
    hsbPolicy = paramJScrollPane.getHorizontalScrollBarPolicy();
  }
  
  protected Component addSingletonComponent(Component paramComponent1, Component paramComponent2)
  {
    if ((paramComponent1 != null) && (paramComponent1 != paramComponent2)) {
      paramComponent1.getParent().remove(paramComponent1);
    }
    return paramComponent2;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent)
  {
    if (paramString.equals("VIEWPORT")) {
      viewport = ((JViewport)addSingletonComponent(viewport, paramComponent));
    } else if (paramString.equals("VERTICAL_SCROLLBAR")) {
      vsb = ((JScrollBar)addSingletonComponent(vsb, paramComponent));
    } else if (paramString.equals("HORIZONTAL_SCROLLBAR")) {
      hsb = ((JScrollBar)addSingletonComponent(hsb, paramComponent));
    } else if (paramString.equals("ROW_HEADER")) {
      rowHead = ((JViewport)addSingletonComponent(rowHead, paramComponent));
    } else if (paramString.equals("COLUMN_HEADER")) {
      colHead = ((JViewport)addSingletonComponent(colHead, paramComponent));
    } else if (paramString.equals("LOWER_LEFT_CORNER")) {
      lowerLeft = addSingletonComponent(lowerLeft, paramComponent);
    } else if (paramString.equals("LOWER_RIGHT_CORNER")) {
      lowerRight = addSingletonComponent(lowerRight, paramComponent);
    } else if (paramString.equals("UPPER_LEFT_CORNER")) {
      upperLeft = addSingletonComponent(upperLeft, paramComponent);
    } else if (paramString.equals("UPPER_RIGHT_CORNER")) {
      upperRight = addSingletonComponent(upperRight, paramComponent);
    } else {
      throw new IllegalArgumentException("invalid layout key " + paramString);
    }
  }
  
  public void removeLayoutComponent(Component paramComponent)
  {
    if (paramComponent == viewport) {
      viewport = null;
    } else if (paramComponent == vsb) {
      vsb = null;
    } else if (paramComponent == hsb) {
      hsb = null;
    } else if (paramComponent == rowHead) {
      rowHead = null;
    } else if (paramComponent == colHead) {
      colHead = null;
    } else if (paramComponent == lowerLeft) {
      lowerLeft = null;
    } else if (paramComponent == lowerRight) {
      lowerRight = null;
    } else if (paramComponent == upperLeft) {
      upperLeft = null;
    } else if (paramComponent == upperRight) {
      upperRight = null;
    }
  }
  
  public int getVerticalScrollBarPolicy()
  {
    return vsbPolicy;
  }
  
  public void setVerticalScrollBarPolicy(int paramInt)
  {
    switch (paramInt)
    {
    case 20: 
    case 21: 
    case 22: 
      vsbPolicy = paramInt;
      break;
    default: 
      throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
    }
  }
  
  public int getHorizontalScrollBarPolicy()
  {
    return hsbPolicy;
  }
  
  public void setHorizontalScrollBarPolicy(int paramInt)
  {
    switch (paramInt)
    {
    case 30: 
    case 31: 
    case 32: 
      hsbPolicy = paramInt;
      break;
    default: 
      throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
    }
  }
  
  public JViewport getViewport()
  {
    return viewport;
  }
  
  public JScrollBar getHorizontalScrollBar()
  {
    return hsb;
  }
  
  public JScrollBar getVerticalScrollBar()
  {
    return vsb;
  }
  
  public JViewport getRowHeader()
  {
    return rowHead;
  }
  
  public JViewport getColumnHeader()
  {
    return colHead;
  }
  
  public Component getCorner(String paramString)
  {
    if (paramString.equals("LOWER_LEFT_CORNER")) {
      return lowerLeft;
    }
    if (paramString.equals("LOWER_RIGHT_CORNER")) {
      return lowerRight;
    }
    if (paramString.equals("UPPER_LEFT_CORNER")) {
      return upperLeft;
    }
    if (paramString.equals("UPPER_RIGHT_CORNER")) {
      return upperRight;
    }
    return null;
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    JScrollPane localJScrollPane = (JScrollPane)paramContainer;
    vsbPolicy = localJScrollPane.getVerticalScrollBarPolicy();
    hsbPolicy = localJScrollPane.getHorizontalScrollBarPolicy();
    Insets localInsets1 = paramContainer.getInsets();
    int i = left + right;
    int j = top + bottom;
    Dimension localDimension1 = null;
    Dimension localDimension2 = null;
    Component localComponent = null;
    if (viewport != null)
    {
      localDimension1 = viewport.getPreferredSize();
      localComponent = viewport.getView();
      if (localComponent != null) {
        localDimension2 = localComponent.getPreferredSize();
      } else {
        localDimension2 = new Dimension(0, 0);
      }
    }
    if (localDimension1 != null)
    {
      i += width;
      j += height;
    }
    Border localBorder = localJScrollPane.getViewportBorder();
    if (localBorder != null)
    {
      Insets localInsets2 = localBorder.getBorderInsets(paramContainer);
      i += left + right;
      j += top + bottom;
    }
    if ((rowHead != null) && (rowHead.isVisible())) {
      i += rowHead.getPreferredSize().width;
    }
    if ((colHead != null) && (colHead.isVisible())) {
      j += colHead.getPreferredSize().height;
    }
    int k;
    if ((vsb != null) && (vsbPolicy != 21)) {
      if (vsbPolicy == 22)
      {
        i += vsb.getPreferredSize().width;
      }
      else if ((localDimension2 != null) && (localDimension1 != null))
      {
        k = 1;
        if ((localComponent instanceof Scrollable)) {
          k = !((Scrollable)localComponent).getScrollableTracksViewportHeight() ? 1 : 0;
        }
        if ((k != 0) && (height > height)) {
          i += vsb.getPreferredSize().width;
        }
      }
    }
    if ((hsb != null) && (hsbPolicy != 31)) {
      if (hsbPolicy == 32)
      {
        j += hsb.getPreferredSize().height;
      }
      else if ((localDimension2 != null) && (localDimension1 != null))
      {
        k = 1;
        if ((localComponent instanceof Scrollable)) {
          k = !((Scrollable)localComponent).getScrollableTracksViewportWidth() ? 1 : 0;
        }
        if ((k != 0) && (width > width)) {
          j += hsb.getPreferredSize().height;
        }
      }
    }
    return new Dimension(i, j);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    JScrollPane localJScrollPane = (JScrollPane)paramContainer;
    vsbPolicy = localJScrollPane.getVerticalScrollBarPolicy();
    hsbPolicy = localJScrollPane.getHorizontalScrollBarPolicy();
    Insets localInsets = paramContainer.getInsets();
    int i = left + right;
    int j = top + bottom;
    if (viewport != null)
    {
      localObject1 = viewport.getMinimumSize();
      i += width;
      j += height;
    }
    Object localObject1 = localJScrollPane.getViewportBorder();
    Object localObject2;
    if (localObject1 != null)
    {
      localObject2 = ((Border)localObject1).getBorderInsets(paramContainer);
      i += left + right;
      j += top + bottom;
    }
    if ((rowHead != null) && (rowHead.isVisible()))
    {
      localObject2 = rowHead.getMinimumSize();
      i += width;
      j = Math.max(j, height);
    }
    if ((colHead != null) && (colHead.isVisible()))
    {
      localObject2 = colHead.getMinimumSize();
      i = Math.max(i, width);
      j += height;
    }
    if ((vsb != null) && (vsbPolicy != 21))
    {
      localObject2 = vsb.getMinimumSize();
      i += width;
      j = Math.max(j, height);
    }
    if ((hsb != null) && (hsbPolicy != 31))
    {
      localObject2 = hsb.getMinimumSize();
      i = Math.max(i, width);
      j += height;
    }
    return new Dimension(i, j);
  }
  
  public void layoutContainer(Container paramContainer)
  {
    JScrollPane localJScrollPane = (JScrollPane)paramContainer;
    vsbPolicy = localJScrollPane.getVerticalScrollBarPolicy();
    hsbPolicy = localJScrollPane.getHorizontalScrollBarPolicy();
    Rectangle localRectangle1 = localJScrollPane.getBounds();
    x = (y = 0);
    Insets localInsets1 = paramContainer.getInsets();
    x = left;
    y = top;
    width -= left + right;
    height -= top + bottom;
    boolean bool1 = SwingUtilities.isLeftToRight(localJScrollPane);
    Rectangle localRectangle2 = new Rectangle(0, y, 0, 0);
    if ((colHead != null) && (colHead.isVisible()))
    {
      int i = Math.min(height, colHead.getPreferredSize().height);
      height = i;
      y += i;
      height -= i;
    }
    Rectangle localRectangle3 = new Rectangle(0, 0, 0, 0);
    if ((rowHead != null) && (rowHead.isVisible()))
    {
      int j = Math.min(width, rowHead.getPreferredSize().width);
      width = j;
      width -= j;
      if (bool1)
      {
        x = x;
        x += j;
      }
      else
      {
        x += width;
      }
    }
    Border localBorder = localJScrollPane.getViewportBorder();
    Insets localInsets2;
    if (localBorder != null)
    {
      localInsets2 = localBorder.getBorderInsets(paramContainer);
      x += left;
      y += top;
      width -= left + right;
      height -= top + bottom;
    }
    else
    {
      localInsets2 = new Insets(0, 0, 0, 0);
    }
    Object localObject = viewport != null ? viewport.getView() : null;
    Dimension localDimension1 = localObject != null ? ((Component)localObject).getPreferredSize() : new Dimension(0, 0);
    Dimension localDimension2 = viewport != null ? viewport.toViewCoordinates(localRectangle1.getSize()) : new Dimension(0, 0);
    boolean bool2 = false;
    boolean bool3 = false;
    int k = (width < 0) || (height < 0) ? 1 : 0;
    Scrollable localScrollable;
    if ((k == 0) && ((localObject instanceof Scrollable)))
    {
      localScrollable = (Scrollable)localObject;
      bool2 = localScrollable.getScrollableTracksViewportWidth();
      bool3 = localScrollable.getScrollableTracksViewportHeight();
    }
    else
    {
      localScrollable = null;
    }
    Rectangle localRectangle4 = new Rectangle(0, y - top, 0, 0);
    boolean bool4;
    if (k != 0) {
      bool4 = false;
    } else if (vsbPolicy == 22) {
      bool4 = true;
    } else if (vsbPolicy == 21) {
      bool4 = false;
    } else {
      bool4 = (!bool3) && (height > height);
    }
    if ((vsb != null) && (bool4))
    {
      adjustForVSB(true, localRectangle1, localRectangle4, localInsets2, bool1);
      localDimension2 = viewport.toViewCoordinates(localRectangle1.getSize());
    }
    Rectangle localRectangle5 = new Rectangle(x - left, 0, 0, 0);
    boolean bool5;
    if (k != 0) {
      bool5 = false;
    } else if (hsbPolicy == 32) {
      bool5 = true;
    } else if (hsbPolicy == 31) {
      bool5 = false;
    } else {
      bool5 = (!bool2) && (width > width);
    }
    if ((hsb != null) && (bool5))
    {
      adjustForHSB(true, localRectangle1, localRectangle5, localInsets2);
      if ((vsb != null) && (!bool4) && (vsbPolicy != 21))
      {
        localDimension2 = viewport.toViewCoordinates(localRectangle1.getSize());
        bool4 = height > height;
        if (bool4) {
          adjustForVSB(true, localRectangle1, localRectangle4, localInsets2, bool1);
        }
      }
    }
    if (viewport != null)
    {
      viewport.setBounds(localRectangle1);
      if (localScrollable != null)
      {
        localDimension2 = viewport.toViewCoordinates(localRectangle1.getSize());
        boolean bool6 = bool5;
        boolean bool7 = bool4;
        bool2 = localScrollable.getScrollableTracksViewportWidth();
        bool3 = localScrollable.getScrollableTracksViewportHeight();
        boolean bool8;
        if ((vsb != null) && (vsbPolicy == 20))
        {
          bool8 = (!bool3) && (height > height);
          if (bool8 != bool4)
          {
            bool4 = bool8;
            adjustForVSB(bool4, localRectangle1, localRectangle4, localInsets2, bool1);
            localDimension2 = viewport.toViewCoordinates(localRectangle1.getSize());
          }
        }
        if ((hsb != null) && (hsbPolicy == 30))
        {
          bool8 = (!bool2) && (width > width);
          if (bool8 != bool5)
          {
            bool5 = bool8;
            adjustForHSB(bool5, localRectangle1, localRectangle5, localInsets2);
            if ((vsb != null) && (!bool4) && (vsbPolicy != 21))
            {
              localDimension2 = viewport.toViewCoordinates(localRectangle1.getSize());
              bool4 = height > height;
              if (bool4) {
                adjustForVSB(true, localRectangle1, localRectangle4, localInsets2, bool1);
              }
            }
          }
        }
        if ((bool6 != bool5) || (bool7 != bool4)) {
          viewport.setBounds(localRectangle1);
        }
      }
    }
    height = (height + top + bottom);
    width = (width + left + right);
    height = (height + top + bottom);
    y -= top;
    width = (width + left + right);
    x -= left;
    if (rowHead != null) {
      rowHead.setBounds(localRectangle3);
    }
    if (colHead != null) {
      colHead.setBounds(localRectangle2);
    }
    if (vsb != null) {
      if (bool4)
      {
        if ((colHead != null) && (UIManager.getBoolean("ScrollPane.fillUpperCorner")) && (((bool1) && (upperRight == null)) || ((!bool1) && (upperLeft == null))))
        {
          y = y;
          height += height;
        }
        vsb.setVisible(true);
        vsb.setBounds(localRectangle4);
      }
      else
      {
        vsb.setVisible(false);
      }
    }
    if (hsb != null) {
      if (bool5)
      {
        if ((rowHead != null) && (UIManager.getBoolean("ScrollPane.fillLowerCorner")) && (((bool1) && (lowerLeft == null)) || ((!bool1) && (lowerRight == null))))
        {
          if (bool1) {
            x = x;
          }
          width += width;
        }
        hsb.setVisible(true);
        hsb.setBounds(localRectangle5);
      }
      else
      {
        hsb.setVisible(false);
      }
    }
    if (lowerLeft != null) {
      lowerLeft.setBounds(bool1 ? x : x, y, bool1 ? width : width, height);
    }
    if (lowerRight != null) {
      lowerRight.setBounds(bool1 ? x : x, y, bool1 ? width : width, height);
    }
    if (upperLeft != null) {
      upperLeft.setBounds(bool1 ? x : x, y, bool1 ? width : width, height);
    }
    if (upperRight != null) {
      upperRight.setBounds(bool1 ? x : x, y, bool1 ? width : width, height);
    }
  }
  
  private void adjustForVSB(boolean paramBoolean1, Rectangle paramRectangle1, Rectangle paramRectangle2, Insets paramInsets, boolean paramBoolean2)
  {
    int i = width;
    if (paramBoolean1)
    {
      int j = Math.max(0, Math.min(vsb.getPreferredSize().width, width));
      width -= j;
      width = j;
      if (paramBoolean2)
      {
        x = (x + width + right);
      }
      else
      {
        x -= left;
        x += j;
      }
    }
    else
    {
      width += i;
    }
  }
  
  private void adjustForHSB(boolean paramBoolean, Rectangle paramRectangle1, Rectangle paramRectangle2, Insets paramInsets)
  {
    int i = height;
    if (paramBoolean)
    {
      int j = Math.max(0, Math.min(height, hsb.getPreferredSize().height));
      height -= j;
      y = (y + height + bottom);
      height = j;
    }
    else
    {
      height += i;
    }
  }
  
  @Deprecated
  public Rectangle getViewportBorderBounds(JScrollPane paramJScrollPane)
  {
    return paramJScrollPane.getViewportBorderBounds();
  }
  
  public static class UIResource
    extends ScrollPaneLayout
    implements UIResource
  {
    public UIResource() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ScrollPaneLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */