package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.Serializable;

public class ViewportLayout
  implements LayoutManager, Serializable
{
  static ViewportLayout SHARED_INSTANCE = new ViewportLayout();
  
  public ViewportLayout() {}
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    Component localComponent = ((JViewport)paramContainer).getView();
    if (localComponent == null) {
      return new Dimension(0, 0);
    }
    if ((localComponent instanceof Scrollable)) {
      return ((Scrollable)localComponent).getPreferredScrollableViewportSize();
    }
    return localComponent.getPreferredSize();
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    return new Dimension(4, 4);
  }
  
  public void layoutContainer(Container paramContainer)
  {
    JViewport localJViewport = (JViewport)paramContainer;
    Component localComponent = localJViewport.getView();
    Scrollable localScrollable = null;
    if (localComponent == null) {
      return;
    }
    if ((localComponent instanceof Scrollable)) {
      localScrollable = (Scrollable)localComponent;
    }
    Insets localInsets = localJViewport.getInsets();
    Dimension localDimension1 = localComponent.getPreferredSize();
    Dimension localDimension2 = localJViewport.getSize();
    Dimension localDimension3 = localJViewport.toViewCoordinates(localDimension2);
    Dimension localDimension4 = new Dimension(localDimension1);
    if (localScrollable != null)
    {
      if (localScrollable.getScrollableTracksViewportWidth()) {
        width = width;
      }
      if (localScrollable.getScrollableTracksViewportHeight()) {
        height = height;
      }
    }
    Point localPoint = localJViewport.getViewPosition();
    if ((localScrollable == null) || (localJViewport.getParent() == null) || (localJViewport.getParent().getComponentOrientation().isLeftToRight()))
    {
      if (x + width > width) {
        x = Math.max(0, width - width);
      }
    }
    else if (width > width) {
      x = (width - width);
    } else {
      x = Math.max(0, Math.min(width - width, x));
    }
    if (y + height > height) {
      y = Math.max(0, height - height);
    }
    if (localScrollable == null)
    {
      if ((x == 0) && (width > width)) {
        width = width;
      }
      if ((y == 0) && (height > height)) {
        height = height;
      }
    }
    localJViewport.setViewPosition(localPoint);
    localJViewport.setViewSize(localDimension4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ViewportLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */