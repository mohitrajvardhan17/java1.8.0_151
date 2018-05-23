package javax.swing;

import com.sun.awt.AWTUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.WindowAccessor;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DefaultDesktopManager
  implements DesktopManager, Serializable
{
  static final String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";
  static final int DEFAULT_DRAG_MODE = 0;
  static final int OUTLINE_DRAG_MODE = 1;
  static final int FASTER_DRAG_MODE = 2;
  int dragMode = 0;
  private transient Rectangle currentBounds = null;
  private transient Graphics desktopGraphics = null;
  private transient Rectangle desktopBounds = null;
  private transient Rectangle[] floatingItems = new Rectangle[0];
  private transient boolean didDrag;
  private transient Point currentLoc = null;
  
  public DefaultDesktopManager() {}
  
  public void openFrame(JInternalFrame paramJInternalFrame)
  {
    if (paramJInternalFrame.getDesktopIcon().getParent() != null)
    {
      paramJInternalFrame.getDesktopIcon().getParent().add(paramJInternalFrame);
      removeIconFor(paramJInternalFrame);
    }
  }
  
  public void closeFrame(JInternalFrame paramJInternalFrame)
  {
    JDesktopPane localJDesktopPane = paramJInternalFrame.getDesktopPane();
    if (localJDesktopPane == null) {
      return;
    }
    boolean bool = paramJInternalFrame.isSelected();
    Container localContainer = paramJInternalFrame.getParent();
    JInternalFrame localJInternalFrame = null;
    if (bool)
    {
      localJInternalFrame = localJDesktopPane.getNextFrame(paramJInternalFrame);
      try
      {
        paramJInternalFrame.setSelected(false);
      }
      catch (PropertyVetoException localPropertyVetoException1) {}
    }
    if (localContainer != null)
    {
      localContainer.remove(paramJInternalFrame);
      localContainer.repaint(paramJInternalFrame.getX(), paramJInternalFrame.getY(), paramJInternalFrame.getWidth(), paramJInternalFrame.getHeight());
    }
    removeIconFor(paramJInternalFrame);
    if (paramJInternalFrame.getNormalBounds() != null) {
      paramJInternalFrame.setNormalBounds(null);
    }
    if (wasIcon(paramJInternalFrame)) {
      setWasIcon(paramJInternalFrame, null);
    }
    if (localJInternalFrame != null) {
      try
      {
        localJInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException2) {}
    } else if ((bool) && (localJDesktopPane.getComponentCount() == 0)) {
      localJDesktopPane.requestFocus();
    }
  }
  
  public void maximizeFrame(JInternalFrame paramJInternalFrame)
  {
    if (paramJInternalFrame.isIcon())
    {
      try
      {
        paramJInternalFrame.setIcon(false);
      }
      catch (PropertyVetoException localPropertyVetoException1) {}
    }
    else
    {
      paramJInternalFrame.setNormalBounds(paramJInternalFrame.getBounds());
      Rectangle localRectangle = paramJInternalFrame.getParent().getBounds();
      setBoundsForFrame(paramJInternalFrame, 0, 0, width, height);
    }
    try
    {
      paramJInternalFrame.setSelected(true);
    }
    catch (PropertyVetoException localPropertyVetoException2) {}
  }
  
  public void minimizeFrame(JInternalFrame paramJInternalFrame)
  {
    if (paramJInternalFrame.isIcon())
    {
      iconifyFrame(paramJInternalFrame);
      return;
    }
    if (paramJInternalFrame.getNormalBounds() != null)
    {
      Rectangle localRectangle = paramJInternalFrame.getNormalBounds();
      paramJInternalFrame.setNormalBounds(null);
      try
      {
        paramJInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException) {}
      setBoundsForFrame(paramJInternalFrame, x, y, width, height);
    }
  }
  
  public void iconifyFrame(JInternalFrame paramJInternalFrame)
  {
    Container localContainer = paramJInternalFrame.getParent();
    JDesktopPane localJDesktopPane = paramJInternalFrame.getDesktopPane();
    boolean bool = paramJInternalFrame.isSelected();
    JInternalFrame.JDesktopIcon localJDesktopIcon = paramJInternalFrame.getDesktopIcon();
    Object localObject;
    if (!wasIcon(paramJInternalFrame))
    {
      localObject = getBoundsForIconOf(paramJInternalFrame);
      localJDesktopIcon.setBounds(x, y, width, height);
      localJDesktopIcon.revalidate();
      setWasIcon(paramJInternalFrame, Boolean.TRUE);
    }
    if ((localContainer == null) || (localJDesktopPane == null)) {
      return;
    }
    if ((localContainer instanceof JLayeredPane))
    {
      localObject = (JLayeredPane)localContainer;
      int i = JLayeredPane.getLayer(paramJInternalFrame);
      JLayeredPane.putLayer(localJDesktopIcon, i);
    }
    if (!paramJInternalFrame.isMaximum()) {
      paramJInternalFrame.setNormalBounds(paramJInternalFrame.getBounds());
    }
    localJDesktopPane.setComponentOrderCheckingEnabled(false);
    localContainer.remove(paramJInternalFrame);
    localContainer.add(localJDesktopIcon);
    localJDesktopPane.setComponentOrderCheckingEnabled(true);
    localContainer.repaint(paramJInternalFrame.getX(), paramJInternalFrame.getY(), paramJInternalFrame.getWidth(), paramJInternalFrame.getHeight());
    if ((bool) && (localJDesktopPane.selectFrame(true) == null)) {
      paramJInternalFrame.restoreSubcomponentFocus();
    }
  }
  
  public void deiconifyFrame(JInternalFrame paramJInternalFrame)
  {
    JInternalFrame.JDesktopIcon localJDesktopIcon = paramJInternalFrame.getDesktopIcon();
    Container localContainer = localJDesktopIcon.getParent();
    JDesktopPane localJDesktopPane = paramJInternalFrame.getDesktopPane();
    if ((localContainer != null) && (localJDesktopPane != null))
    {
      localContainer.add(paramJInternalFrame);
      if (paramJInternalFrame.isMaximum())
      {
        Rectangle localRectangle = localContainer.getBounds();
        if ((paramJInternalFrame.getWidth() != width) || (paramJInternalFrame.getHeight() != height)) {
          setBoundsForFrame(paramJInternalFrame, 0, 0, width, height);
        }
      }
      removeIconFor(paramJInternalFrame);
      if (paramJInternalFrame.isSelected())
      {
        paramJInternalFrame.moveToFront();
        paramJInternalFrame.restoreSubcomponentFocus();
      }
      else
      {
        try
        {
          paramJInternalFrame.setSelected(true);
        }
        catch (PropertyVetoException localPropertyVetoException) {}
      }
    }
  }
  
  public void activateFrame(JInternalFrame paramJInternalFrame)
  {
    Container localContainer = paramJInternalFrame.getParent();
    JDesktopPane localJDesktopPane = paramJInternalFrame.getDesktopPane();
    JInternalFrame localJInternalFrame = localJDesktopPane == null ? null : localJDesktopPane.getSelectedFrame();
    if (localContainer == null)
    {
      localContainer = paramJInternalFrame.getDesktopIcon().getParent();
      if (localContainer == null) {
        return;
      }
    }
    if (localJInternalFrame == null)
    {
      if (localJDesktopPane != null) {
        localJDesktopPane.setSelectedFrame(paramJInternalFrame);
      }
    }
    else if (localJInternalFrame != paramJInternalFrame)
    {
      if (localJInternalFrame.isSelected()) {
        try
        {
          localJInternalFrame.setSelected(false);
        }
        catch (PropertyVetoException localPropertyVetoException) {}
      }
      if (localJDesktopPane != null) {
        localJDesktopPane.setSelectedFrame(paramJInternalFrame);
      }
    }
    paramJInternalFrame.moveToFront();
  }
  
  public void deactivateFrame(JInternalFrame paramJInternalFrame)
  {
    JDesktopPane localJDesktopPane = paramJInternalFrame.getDesktopPane();
    JInternalFrame localJInternalFrame = localJDesktopPane == null ? null : localJDesktopPane.getSelectedFrame();
    if (localJInternalFrame == paramJInternalFrame) {
      localJDesktopPane.setSelectedFrame(null);
    }
  }
  
  public void beginDraggingFrame(JComponent paramJComponent)
  {
    setupDragMode(paramJComponent);
    if (dragMode == 2)
    {
      Container localContainer = paramJComponent.getParent();
      floatingItems = findFloatingItems(paramJComponent);
      currentBounds = paramJComponent.getBounds();
      if ((localContainer instanceof JComponent))
      {
        desktopBounds = ((JComponent)localContainer).getVisibleRect();
      }
      else
      {
        desktopBounds = localContainer.getBounds();
        desktopBounds.x = (desktopBounds.y = 0);
      }
      desktopGraphics = JComponent.safelyGetGraphics(localContainer);
      isDragging = true;
      didDrag = false;
    }
  }
  
  private void setupDragMode(JComponent paramJComponent)
  {
    JDesktopPane localJDesktopPane = getDesktopPane(paramJComponent);
    Container localContainer = paramJComponent.getParent();
    dragMode = 0;
    if (localJDesktopPane != null)
    {
      String str = (String)localJDesktopPane.getClientProperty("JDesktopPane.dragMode");
      Window localWindow = SwingUtilities.getWindowAncestor(paramJComponent);
      if ((localWindow != null) && (!AWTUtilities.isWindowOpaque(localWindow))) {
        dragMode = 0;
      } else if ((str != null) && (str.equals("outline"))) {
        dragMode = 1;
      } else if ((str != null) && (str.equals("faster")) && ((paramJComponent instanceof JInternalFrame)) && (((JInternalFrame)paramJComponent).isOpaque()) && ((localContainer == null) || (localContainer.isOpaque()))) {
        dragMode = 2;
      } else if (localJDesktopPane.getDragMode() == 1) {
        dragMode = 1;
      } else if ((localJDesktopPane.getDragMode() == 0) && ((paramJComponent instanceof JInternalFrame)) && (((JInternalFrame)paramJComponent).isOpaque())) {
        dragMode = 2;
      } else {
        dragMode = 0;
      }
    }
  }
  
  public void dragFrame(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    if (dragMode == 1)
    {
      JDesktopPane localJDesktopPane = getDesktopPane(paramJComponent);
      if (localJDesktopPane != null)
      {
        Graphics localGraphics = JComponent.safelyGetGraphics(localJDesktopPane);
        localGraphics.setXORMode(Color.white);
        if (currentLoc != null) {
          localGraphics.drawRect(currentLoc.x, currentLoc.y, paramJComponent.getWidth() - 1, paramJComponent.getHeight() - 1);
        }
        localGraphics.drawRect(paramInt1, paramInt2, paramJComponent.getWidth() - 1, paramJComponent.getHeight() - 1);
        SurfaceData localSurfaceData = ((SunGraphics2D)localGraphics).getSurfaceData();
        if (!localSurfaceData.isSurfaceLost()) {
          currentLoc = new Point(paramInt1, paramInt2);
        }
        localGraphics.dispose();
      }
    }
    else if (dragMode == 2)
    {
      dragFrameFaster(paramJComponent, paramInt1, paramInt2);
    }
    else
    {
      setBoundsForFrame(paramJComponent, paramInt1, paramInt2, paramJComponent.getWidth(), paramJComponent.getHeight());
    }
  }
  
  public void endDraggingFrame(JComponent paramJComponent)
  {
    if ((dragMode == 1) && (currentLoc != null))
    {
      setBoundsForFrame(paramJComponent, currentLoc.x, currentLoc.y, paramJComponent.getWidth(), paramJComponent.getHeight());
      currentLoc = null;
    }
    else if (dragMode == 2)
    {
      currentBounds = null;
      if (desktopGraphics != null)
      {
        desktopGraphics.dispose();
        desktopGraphics = null;
      }
      desktopBounds = null;
      isDragging = false;
    }
  }
  
  public void beginResizingFrame(JComponent paramJComponent, int paramInt)
  {
    setupDragMode(paramJComponent);
  }
  
  public void resizeFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((dragMode == 0) || (dragMode == 2))
    {
      setBoundsForFrame(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    else
    {
      JDesktopPane localJDesktopPane = getDesktopPane(paramJComponent);
      if (localJDesktopPane != null)
      {
        Graphics localGraphics = JComponent.safelyGetGraphics(localJDesktopPane);
        localGraphics.setXORMode(Color.white);
        if (currentBounds != null) {
          localGraphics.drawRect(currentBounds.x, currentBounds.y, currentBounds.width - 1, currentBounds.height - 1);
        }
        localGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
        SurfaceData localSurfaceData = ((SunGraphics2D)localGraphics).getSurfaceData();
        if (!localSurfaceData.isSurfaceLost()) {
          currentBounds = new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4);
        }
        localGraphics.setPaintMode();
        localGraphics.dispose();
      }
    }
  }
  
  public void endResizingFrame(JComponent paramJComponent)
  {
    if ((dragMode == 1) && (currentBounds != null))
    {
      setBoundsForFrame(paramJComponent, currentBounds.x, currentBounds.y, currentBounds.width, currentBounds.height);
      currentBounds = null;
    }
  }
  
  public void setBoundsForFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramJComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    paramJComponent.revalidate();
  }
  
  protected void removeIconFor(JInternalFrame paramJInternalFrame)
  {
    JInternalFrame.JDesktopIcon localJDesktopIcon = paramJInternalFrame.getDesktopIcon();
    Container localContainer = localJDesktopIcon.getParent();
    if (localContainer != null)
    {
      localContainer.remove(localJDesktopIcon);
      localContainer.repaint(localJDesktopIcon.getX(), localJDesktopIcon.getY(), localJDesktopIcon.getWidth(), localJDesktopIcon.getHeight());
    }
  }
  
  protected Rectangle getBoundsForIconOf(JInternalFrame paramJInternalFrame)
  {
    JInternalFrame.JDesktopIcon localJDesktopIcon1 = paramJInternalFrame.getDesktopIcon();
    Dimension localDimension = localJDesktopIcon1.getPreferredSize();
    Container localContainer = paramJInternalFrame.getParent();
    if (localContainer == null) {
      localContainer = paramJInternalFrame.getDesktopIcon().getParent();
    }
    if (localContainer == null) {
      return new Rectangle(0, 0, width, height);
    }
    Rectangle localRectangle1 = localContainer.getBounds();
    Component[] arrayOfComponent = localContainer.getComponents();
    Rectangle localRectangle2 = null;
    JInternalFrame.JDesktopIcon localJDesktopIcon2 = null;
    int i = 0;
    int j = height - height;
    int k = width;
    int m = height;
    int n = 0;
    while (n == 0)
    {
      localRectangle2 = new Rectangle(i, j, k, m);
      n = 1;
      for (int i1 = 0; i1 < arrayOfComponent.length; i1++)
      {
        if ((arrayOfComponent[i1] instanceof JInternalFrame))
        {
          localJDesktopIcon2 = ((JInternalFrame)arrayOfComponent[i1]).getDesktopIcon();
        }
        else
        {
          if (!(arrayOfComponent[i1] instanceof JInternalFrame.JDesktopIcon)) {
            continue;
          }
          localJDesktopIcon2 = (JInternalFrame.JDesktopIcon)arrayOfComponent[i1];
        }
        if ((!localJDesktopIcon2.equals(localJDesktopIcon1)) && (localRectangle2.intersects(localJDesktopIcon2.getBounds())))
        {
          n = 0;
          break;
        }
      }
      if (localJDesktopIcon2 == null) {
        return localRectangle2;
      }
      i += getBoundswidth;
      if (i + k > width)
      {
        i = 0;
        j -= m;
      }
    }
    return localRectangle2;
  }
  
  protected void setPreviousBounds(JInternalFrame paramJInternalFrame, Rectangle paramRectangle)
  {
    paramJInternalFrame.setNormalBounds(paramRectangle);
  }
  
  protected Rectangle getPreviousBounds(JInternalFrame paramJInternalFrame)
  {
    return paramJInternalFrame.getNormalBounds();
  }
  
  protected void setWasIcon(JInternalFrame paramJInternalFrame, Boolean paramBoolean)
  {
    if (paramBoolean != null) {
      paramJInternalFrame.putClientProperty("wasIconOnce", paramBoolean);
    }
  }
  
  protected boolean wasIcon(JInternalFrame paramJInternalFrame)
  {
    return paramJInternalFrame.getClientProperty("wasIconOnce") == Boolean.TRUE;
  }
  
  JDesktopPane getDesktopPane(JComponent paramJComponent)
  {
    JDesktopPane localJDesktopPane = null;
    Container localContainer = paramJComponent.getParent();
    while (localJDesktopPane == null) {
      if ((localContainer instanceof JDesktopPane))
      {
        localJDesktopPane = (JDesktopPane)localContainer;
      }
      else
      {
        if (localContainer == null) {
          break;
        }
        localContainer = localContainer.getParent();
      }
    }
    return localJDesktopPane;
  }
  
  private void dragFrameFaster(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    Rectangle localRectangle1 = new Rectangle(currentBounds.x, currentBounds.y, currentBounds.width, currentBounds.height);
    currentBounds.x = paramInt1;
    currentBounds.y = paramInt2;
    if (didDrag)
    {
      emergencyCleanup(paramJComponent);
    }
    else
    {
      didDrag = true;
      danger = false;
    }
    boolean bool = isFloaterCollision(localRectangle1, currentBounds);
    JComponent localJComponent = (JComponent)paramJComponent.getParent();
    Rectangle localRectangle2 = localRectangle1.intersection(desktopBounds);
    RepaintManager localRepaintManager = RepaintManager.currentManager(paramJComponent);
    localRepaintManager.beginPaint();
    try
    {
      if (!bool) {
        localRepaintManager.copyArea(localJComponent, desktopGraphics, x, y, width, height, paramInt1 - x, paramInt2 - y, true);
      }
      paramJComponent.setBounds(currentBounds);
      if (!bool)
      {
        localObject1 = currentBounds;
        localRepaintManager.notifyRepaintPerformed(localJComponent, x, y, width, height);
      }
      if (bool)
      {
        isDragging = false;
        localJComponent.paintImmediately(currentBounds);
        isDragging = true;
      }
      localRepaintManager.markCompletelyClean(localJComponent);
      localRepaintManager.markCompletelyClean(paramJComponent);
      localObject1 = null;
      if (localRectangle1.intersects(currentBounds))
      {
        localObject1 = SwingUtilities.computeDifference(localRectangle1, currentBounds);
      }
      else
      {
        localObject1 = new Rectangle[1];
        localObject1[0] = localRectangle1;
      }
      Object localObject2;
      for (int i = 0; i < localObject1.length; i++)
      {
        localJComponent.paintImmediately(localObject1[i]);
        localObject2 = localObject1[i];
        localRepaintManager.notifyRepaintPerformed(localJComponent, x, y, width, height);
      }
      if (!localRectangle2.equals(localRectangle1))
      {
        localObject1 = SwingUtilities.computeDifference(localRectangle1, desktopBounds);
        for (i = 0; i < localObject1.length; i++)
        {
          x += paramInt1 - x;
          y += paramInt2 - y;
          isDragging = false;
          localJComponent.paintImmediately(localObject1[i]);
          isDragging = true;
          localObject2 = localObject1[i];
          localRepaintManager.notifyRepaintPerformed(localJComponent, x, y, width, height);
        }
      }
    }
    finally
    {
      localRepaintManager.endPaint();
    }
    Object localObject1 = SwingUtilities.getWindowAncestor(paramJComponent);
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((!((Window)localObject1).isOpaque()) && ((localToolkit instanceof SunToolkit)) && (((SunToolkit)localToolkit).needUpdateWindow())) {
      AWTAccessor.getWindowAccessor().updateWindow((Window)localObject1);
    }
  }
  
  private boolean isFloaterCollision(Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    if (floatingItems.length == 0) {
      return false;
    }
    for (int i = 0; i < floatingItems.length; i++)
    {
      boolean bool1 = paramRectangle1.intersects(floatingItems[i]);
      if (bool1) {
        return true;
      }
      boolean bool2 = paramRectangle2.intersects(floatingItems[i]);
      if (bool2) {
        return true;
      }
    }
    return false;
  }
  
  private Rectangle[] findFloatingItems(JComponent paramJComponent)
  {
    Container localContainer = paramJComponent.getParent();
    Component[] arrayOfComponent = localContainer.getComponents();
    int i = 0;
    for (i = 0; (i < arrayOfComponent.length) && (arrayOfComponent[i] != paramJComponent); i++) {}
    Rectangle[] arrayOfRectangle = new Rectangle[i];
    for (i = 0; i < arrayOfRectangle.length; i++) {
      arrayOfRectangle[i] = arrayOfComponent[i].getBounds();
    }
    return arrayOfRectangle;
  }
  
  private void emergencyCleanup(final JComponent paramJComponent)
  {
    if (danger)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          paramJComponentisDragging = false;
          paramJComponent.paintImmediately(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
          paramJComponentisDragging = true;
        }
      });
      danger = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultDesktopManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */