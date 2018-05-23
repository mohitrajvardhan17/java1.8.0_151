package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class MotifDesktopPaneUI
  extends BasicDesktopPaneUI
{
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifDesktopPaneUI();
  }
  
  public MotifDesktopPaneUI() {}
  
  protected void installDesktopManager()
  {
    desktopManager = desktop.getDesktopManager();
    if (desktopManager == null)
    {
      desktopManager = new MotifDesktopManager(null);
      desktop.setDesktopManager(desktopManager);
      ((MotifDesktopManager)desktopManager).adjustIcons(desktop);
    }
  }
  
  public Insets getInsets(JComponent paramJComponent)
  {
    return new Insets(0, 0, 0, 0);
  }
  
  private class DragPane
    extends JComponent
  {
    private DragPane() {}
    
    public void paint(Graphics paramGraphics)
    {
      paramGraphics.setColor(Color.darkGray);
      paramGraphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }
  
  private class MotifDesktopManager
    extends DefaultDesktopManager
    implements Serializable, UIResource
  {
    JComponent dragPane;
    boolean usingDragPane = false;
    private transient JLayeredPane layeredPaneForDragPane;
    int iconWidth;
    int iconHeight;
    
    private MotifDesktopManager() {}
    
    public void setBoundsForFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!usingDragPane)
      {
        int i = (paramJComponent.getWidth() != paramInt3) || (paramJComponent.getHeight() != paramInt4) ? 1 : 0;
        Rectangle localRectangle2 = paramJComponent.getBounds();
        paramJComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
        SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle2);
        paramJComponent.getParent().repaint(x, y, width, height);
        if (i != 0) {
          paramJComponent.validate();
        }
      }
      else
      {
        Rectangle localRectangle1 = dragPane.getBounds();
        dragPane.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
        SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle1);
        dragPane.getParent().repaint(x, y, width, height);
      }
    }
    
    public void beginDraggingFrame(JComponent paramJComponent)
    {
      usingDragPane = false;
      if ((paramJComponent.getParent() instanceof JLayeredPane))
      {
        if (dragPane == null) {
          dragPane = new MotifDesktopPaneUI.DragPane(MotifDesktopPaneUI.this, null);
        }
        layeredPaneForDragPane = ((JLayeredPane)paramJComponent.getParent());
        layeredPaneForDragPane.setLayer(dragPane, Integer.MAX_VALUE);
        dragPane.setBounds(paramJComponent.getX(), paramJComponent.getY(), paramJComponent.getWidth(), paramJComponent.getHeight());
        layeredPaneForDragPane.add(dragPane);
        usingDragPane = true;
      }
    }
    
    public void dragFrame(JComponent paramJComponent, int paramInt1, int paramInt2)
    {
      setBoundsForFrame(paramJComponent, paramInt1, paramInt2, paramJComponent.getWidth(), paramJComponent.getHeight());
    }
    
    public void endDraggingFrame(JComponent paramJComponent)
    {
      if (usingDragPane)
      {
        layeredPaneForDragPane.remove(dragPane);
        usingDragPane = false;
        if ((paramJComponent instanceof JInternalFrame)) {
          setBoundsForFrame(paramJComponent, dragPane.getX(), dragPane.getY(), dragPane.getWidth(), dragPane.getHeight());
        } else if ((paramJComponent instanceof JInternalFrame.JDesktopIcon)) {
          adjustBoundsForIcon((JInternalFrame.JDesktopIcon)paramJComponent, dragPane.getX(), dragPane.getY());
        }
      }
    }
    
    public void beginResizingFrame(JComponent paramJComponent, int paramInt)
    {
      usingDragPane = false;
      if ((paramJComponent.getParent() instanceof JLayeredPane))
      {
        if (dragPane == null) {
          dragPane = new MotifDesktopPaneUI.DragPane(MotifDesktopPaneUI.this, null);
        }
        JLayeredPane localJLayeredPane = (JLayeredPane)paramJComponent.getParent();
        localJLayeredPane.setLayer(dragPane, Integer.MAX_VALUE);
        dragPane.setBounds(paramJComponent.getX(), paramJComponent.getY(), paramJComponent.getWidth(), paramJComponent.getHeight());
        localJLayeredPane.add(dragPane);
        usingDragPane = true;
      }
    }
    
    public void resizeFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      setBoundsForFrame(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void endResizingFrame(JComponent paramJComponent)
    {
      if (usingDragPane)
      {
        JLayeredPane localJLayeredPane = (JLayeredPane)paramJComponent.getParent();
        localJLayeredPane.remove(dragPane);
        usingDragPane = false;
        setBoundsForFrame(paramJComponent, dragPane.getX(), dragPane.getY(), dragPane.getWidth(), dragPane.getHeight());
      }
    }
    
    public void iconifyFrame(JInternalFrame paramJInternalFrame)
    {
      JInternalFrame.JDesktopIcon localJDesktopIcon = paramJInternalFrame.getDesktopIcon();
      Point localPoint = localJDesktopIcon.getLocation();
      adjustBoundsForIcon(localJDesktopIcon, x, y);
      super.iconifyFrame(paramJInternalFrame);
    }
    
    protected void adjustIcons(JDesktopPane paramJDesktopPane)
    {
      JInternalFrame.JDesktopIcon localJDesktopIcon = new JInternalFrame.JDesktopIcon(new JInternalFrame());
      Dimension localDimension = localJDesktopIcon.getPreferredSize();
      iconWidth = width;
      iconHeight = height;
      JInternalFrame[] arrayOfJInternalFrame = paramJDesktopPane.getAllFrames();
      for (int i = 0; i < arrayOfJInternalFrame.length; i++)
      {
        localJDesktopIcon = arrayOfJInternalFrame[i].getDesktopIcon();
        Point localPoint = localJDesktopIcon.getLocation();
        adjustBoundsForIcon(localJDesktopIcon, x, y);
      }
    }
    
    protected void adjustBoundsForIcon(JInternalFrame.JDesktopIcon paramJDesktopIcon, int paramInt1, int paramInt2)
    {
      JDesktopPane localJDesktopPane = paramJDesktopIcon.getDesktopPane();
      int i = localJDesktopPane.getHeight();
      int j = iconWidth;
      int k = iconHeight;
      localJDesktopPane.repaint(paramInt1, paramInt2, j, k);
      paramInt1 = paramInt1 < 0 ? 0 : paramInt1;
      paramInt2 = paramInt2 < 0 ? 0 : paramInt2;
      paramInt2 = paramInt2 >= i ? i - 1 : paramInt2;
      int m = paramInt1 / j * j;
      int n = i % k;
      int i1 = (paramInt2 - n) / k * k + n;
      int i2 = paramInt1 - m;
      int i3 = paramInt2 - i1;
      paramInt1 = i2 < j / 2 ? m : m + j;
      paramInt2 = i1 + k < i ? i1 + k : i3 < k / 2 ? i1 : i1;
      while (getIconAt(localJDesktopPane, paramJDesktopIcon, paramInt1, paramInt2) != null) {
        paramInt1 += j;
      }
      if (paramInt1 > localJDesktopPane.getWidth()) {
        return;
      }
      if (paramJDesktopIcon.getParent() != null) {
        setBoundsForFrame(paramJDesktopIcon, paramInt1, paramInt2, j, k);
      } else {
        paramJDesktopIcon.setLocation(paramInt1, paramInt2);
      }
    }
    
    protected JInternalFrame.JDesktopIcon getIconAt(JDesktopPane paramJDesktopPane, JInternalFrame.JDesktopIcon paramJDesktopIcon, int paramInt1, int paramInt2)
    {
      Object localObject = null;
      Component[] arrayOfComponent = paramJDesktopPane.getComponents();
      for (int i = 0; i < arrayOfComponent.length; i++)
      {
        Component localComponent = arrayOfComponent[i];
        if (((localComponent instanceof JInternalFrame.JDesktopIcon)) && (localComponent != paramJDesktopIcon))
        {
          Point localPoint = localComponent.getLocation();
          if ((x == paramInt1) && (y == paramInt2)) {
            return (JInternalFrame.JDesktopIcon)localComponent;
          }
        }
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */