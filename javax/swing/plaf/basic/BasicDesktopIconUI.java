package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLayeredPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopIconUI;

public class BasicDesktopIconUI
  extends DesktopIconUI
{
  protected JInternalFrame.JDesktopIcon desktopIcon;
  protected JInternalFrame frame;
  protected JComponent iconPane;
  MouseInputListener mouseInputListener;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicDesktopIconUI();
  }
  
  public BasicDesktopIconUI() {}
  
  public void installUI(JComponent paramJComponent)
  {
    desktopIcon = ((JInternalFrame.JDesktopIcon)paramJComponent);
    frame = desktopIcon.getInternalFrame();
    installDefaults();
    installComponents();
    JInternalFrame localJInternalFrame = desktopIcon.getInternalFrame();
    if ((localJInternalFrame.isIcon()) && (localJInternalFrame.getParent() == null))
    {
      JDesktopPane localJDesktopPane = desktopIcon.getDesktopPane();
      if (localJDesktopPane != null)
      {
        DesktopManager localDesktopManager = localJDesktopPane.getDesktopManager();
        if ((localDesktopManager instanceof DefaultDesktopManager)) {
          localDesktopManager.iconifyFrame(localJInternalFrame);
        }
      }
    }
    installListeners();
    JLayeredPane.putLayer(desktopIcon, JLayeredPane.getLayer(frame));
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults();
    uninstallComponents();
    JInternalFrame localJInternalFrame = desktopIcon.getInternalFrame();
    if (localJInternalFrame.isIcon())
    {
      JDesktopPane localJDesktopPane = desktopIcon.getDesktopPane();
      if (localJDesktopPane != null)
      {
        DesktopManager localDesktopManager = localJDesktopPane.getDesktopManager();
        if ((localDesktopManager instanceof DefaultDesktopManager))
        {
          localJInternalFrame.putClientProperty("wasIconOnce", null);
          desktopIcon.setLocation(Integer.MIN_VALUE, 0);
        }
      }
    }
    uninstallListeners();
    frame = null;
    desktopIcon = null;
  }
  
  protected void installComponents()
  {
    iconPane = new BasicInternalFrameTitlePane(frame);
    desktopIcon.setLayout(new BorderLayout());
    desktopIcon.add(iconPane, "Center");
  }
  
  protected void uninstallComponents()
  {
    desktopIcon.remove(iconPane);
    desktopIcon.setLayout(null);
    iconPane = null;
  }
  
  protected void installListeners()
  {
    mouseInputListener = createMouseInputListener();
    desktopIcon.addMouseMotionListener(mouseInputListener);
    desktopIcon.addMouseListener(mouseInputListener);
  }
  
  protected void uninstallListeners()
  {
    desktopIcon.removeMouseMotionListener(mouseInputListener);
    desktopIcon.removeMouseListener(mouseInputListener);
    mouseInputListener = null;
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installBorder(desktopIcon, "DesktopIcon.border");
    LookAndFeel.installProperty(desktopIcon, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(desktopIcon);
  }
  
  protected MouseInputListener createMouseInputListener()
  {
    return new MouseInputHandler();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return desktopIcon.getLayout().preferredLayoutSize(desktopIcon);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = new Dimension(iconPane.getMinimumSize());
    Border localBorder = frame.getBorder();
    if (localBorder != null) {
      height += getBorderInsetsframe).bottom + getBorderInsetsframe).top;
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return iconPane.getMaximumSize();
  }
  
  public Insets getInsets(JComponent paramJComponent)
  {
    JInternalFrame localJInternalFrame = desktopIcon.getInternalFrame();
    Border localBorder = localJInternalFrame.getBorder();
    if (localBorder != null) {
      return localBorder.getBorderInsets(localJInternalFrame);
    }
    return new Insets(0, 0, 0, 0);
  }
  
  public void deiconize()
  {
    try
    {
      frame.setIcon(false);
    }
    catch (PropertyVetoException localPropertyVetoException) {}
  }
  
  public class MouseInputHandler
    extends MouseInputAdapter
  {
    int _x;
    int _y;
    int __x;
    int __y;
    Rectangle startingBounds;
    
    public MouseInputHandler() {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      _x = 0;
      _y = 0;
      __x = 0;
      __y = 0;
      startingBounds = null;
      JDesktopPane localJDesktopPane;
      if ((localJDesktopPane = desktopIcon.getDesktopPane()) != null)
      {
        DesktopManager localDesktopManager = localJDesktopPane.getDesktopManager();
        localDesktopManager.endDraggingFrame(desktopIcon);
      }
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      Point localPoint = SwingUtilities.convertPoint((Component)paramMouseEvent.getSource(), paramMouseEvent.getX(), paramMouseEvent.getY(), null);
      __x = paramMouseEvent.getX();
      __y = paramMouseEvent.getY();
      _x = x;
      _y = y;
      startingBounds = desktopIcon.getBounds();
      JDesktopPane localJDesktopPane;
      if ((localJDesktopPane = desktopIcon.getDesktopPane()) != null)
      {
        DesktopManager localDesktopManager = localJDesktopPane.getDesktopManager();
        localDesktopManager.beginDraggingFrame(desktopIcon);
      }
      try
      {
        frame.setSelected(true);
      }
      catch (PropertyVetoException localPropertyVetoException) {}
      if ((desktopIcon.getParent() instanceof JLayeredPane)) {
        ((JLayeredPane)desktopIcon.getParent()).moveToFront(desktopIcon);
      }
      if ((paramMouseEvent.getClickCount() > 1) && (frame.isIconifiable()) && (frame.isIcon())) {
        deiconize();
      }
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      Point localPoint = SwingUtilities.convertPoint((Component)paramMouseEvent.getSource(), paramMouseEvent.getX(), paramMouseEvent.getY(), null);
      Insets localInsets = desktopIcon.getInsets();
      int k = ((JComponent)desktopIcon.getParent()).getWidth();
      int m = ((JComponent)desktopIcon.getParent()).getHeight();
      if (startingBounds == null) {
        return;
      }
      int i = startingBounds.x - (_x - x);
      int j = startingBounds.y - (_y - y);
      if (i + left <= -__x) {
        i = -__x - left;
      }
      if (j + top <= -__y) {
        j = -__y - top;
      }
      if (i + __x + right > k) {
        i = k - __x - right;
      }
      if (j + __y + bottom > m) {
        j = m - __y - bottom;
      }
      JDesktopPane localJDesktopPane;
      if ((localJDesktopPane = desktopIcon.getDesktopPane()) != null)
      {
        DesktopManager localDesktopManager = localJDesktopPane.getDesktopManager();
        localDesktopManager.dragFrame(desktopIcon, i, j);
      }
      else
      {
        moveAndRepaint(desktopIcon, i, j, desktopIcon.getWidth(), desktopIcon.getHeight());
      }
    }
    
    public void moveAndRepaint(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rectangle localRectangle = paramJComponent.getBounds();
      paramJComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, localRectangle);
      paramJComponent.getParent().repaint(x, y, width, height);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */