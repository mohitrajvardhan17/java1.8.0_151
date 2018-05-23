package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class MetalRootPaneUI
  extends BasicRootPaneUI
{
  private static final String[] borderKeys = { null, "RootPane.frameBorder", "RootPane.plainDialogBorder", "RootPane.informationDialogBorder", "RootPane.errorDialogBorder", "RootPane.colorChooserDialogBorder", "RootPane.fileChooserDialogBorder", "RootPane.questionDialogBorder", "RootPane.warningDialogBorder" };
  private static final int CORNER_DRAG_WIDTH = 16;
  private static final int BORDER_DRAG_THICKNESS = 5;
  private Window window;
  private JComponent titlePane;
  private MouseInputListener mouseInputListener;
  private LayoutManager layoutManager;
  private LayoutManager savedOldLayout;
  private JRootPane root;
  private Cursor lastCursor = Cursor.getPredefinedCursor(0);
  private static final int[] cursorMapping = { 6, 6, 8, 7, 7, 6, 0, 0, 0, 7, 10, 0, 0, 0, 11, 4, 0, 0, 0, 5, 4, 4, 9, 5, 5 };
  
  public MetalRootPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalRootPaneUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    root = ((JRootPane)paramJComponent);
    int i = root.getWindowDecorationStyle();
    if (i != 0) {
      installClientDecorations(root);
    }
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    uninstallClientDecorations(root);
    layoutManager = null;
    mouseInputListener = null;
    root = null;
  }
  
  void installBorder(JRootPane paramJRootPane)
  {
    int i = paramJRootPane.getWindowDecorationStyle();
    if (i == 0) {
      LookAndFeel.uninstallBorder(paramJRootPane);
    } else {
      LookAndFeel.installBorder(paramJRootPane, borderKeys[i]);
    }
  }
  
  private void uninstallBorder(JRootPane paramJRootPane)
  {
    LookAndFeel.uninstallBorder(paramJRootPane);
  }
  
  private void installWindowListeners(JRootPane paramJRootPane, Component paramComponent)
  {
    if ((paramComponent instanceof Window)) {
      window = ((Window)paramComponent);
    } else {
      window = SwingUtilities.getWindowAncestor(paramComponent);
    }
    if (window != null)
    {
      if (mouseInputListener == null) {
        mouseInputListener = createWindowMouseInputListener(paramJRootPane);
      }
      window.addMouseListener(mouseInputListener);
      window.addMouseMotionListener(mouseInputListener);
    }
  }
  
  private void uninstallWindowListeners(JRootPane paramJRootPane)
  {
    if (window != null)
    {
      window.removeMouseListener(mouseInputListener);
      window.removeMouseMotionListener(mouseInputListener);
    }
  }
  
  private void installLayout(JRootPane paramJRootPane)
  {
    if (layoutManager == null) {
      layoutManager = createLayoutManager();
    }
    savedOldLayout = paramJRootPane.getLayout();
    paramJRootPane.setLayout(layoutManager);
  }
  
  private void uninstallLayout(JRootPane paramJRootPane)
  {
    if (savedOldLayout != null)
    {
      paramJRootPane.setLayout(savedOldLayout);
      savedOldLayout = null;
    }
  }
  
  private void installClientDecorations(JRootPane paramJRootPane)
  {
    installBorder(paramJRootPane);
    JComponent localJComponent = createTitlePane(paramJRootPane);
    setTitlePane(paramJRootPane, localJComponent);
    installWindowListeners(paramJRootPane, paramJRootPane.getParent());
    installLayout(paramJRootPane);
    if (window != null)
    {
      paramJRootPane.revalidate();
      paramJRootPane.repaint();
    }
  }
  
  private void uninstallClientDecorations(JRootPane paramJRootPane)
  {
    uninstallBorder(paramJRootPane);
    uninstallWindowListeners(paramJRootPane);
    setTitlePane(paramJRootPane, null);
    uninstallLayout(paramJRootPane);
    int i = paramJRootPane.getWindowDecorationStyle();
    if (i == 0)
    {
      paramJRootPane.repaint();
      paramJRootPane.revalidate();
    }
    if (window != null) {
      window.setCursor(Cursor.getPredefinedCursor(0));
    }
    window = null;
  }
  
  private JComponent createTitlePane(JRootPane paramJRootPane)
  {
    return new MetalTitlePane(paramJRootPane, this);
  }
  
  private MouseInputListener createWindowMouseInputListener(JRootPane paramJRootPane)
  {
    return new MouseInputHandler(null);
  }
  
  private LayoutManager createLayoutManager()
  {
    return new MetalRootLayout(null);
  }
  
  private void setTitlePane(JRootPane paramJRootPane, JComponent paramJComponent)
  {
    JLayeredPane localJLayeredPane = paramJRootPane.getLayeredPane();
    JComponent localJComponent = getTitlePane();
    if (localJComponent != null)
    {
      localJComponent.setVisible(false);
      localJLayeredPane.remove(localJComponent);
    }
    if (paramJComponent != null)
    {
      localJLayeredPane.add(paramJComponent, JLayeredPane.FRAME_CONTENT_LAYER);
      paramJComponent.setVisible(true);
    }
    titlePane = paramJComponent;
  }
  
  private JComponent getTitlePane()
  {
    return titlePane;
  }
  
  private JRootPane getRootPane()
  {
    return root;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == null) {
      return;
    }
    if (str.equals("windowDecorationStyle"))
    {
      JRootPane localJRootPane = (JRootPane)paramPropertyChangeEvent.getSource();
      int i = localJRootPane.getWindowDecorationStyle();
      uninstallClientDecorations(localJRootPane);
      if (i != 0) {
        installClientDecorations(localJRootPane);
      }
    }
    else if (str.equals("ancestor"))
    {
      uninstallWindowListeners(root);
      if (((JRootPane)paramPropertyChangeEvent.getSource()).getWindowDecorationStyle() != 0) {
        installWindowListeners(root, root.getParent());
      }
    }
  }
  
  private static class MetalRootLayout
    implements LayoutManager2
  {
    private MetalRootLayout() {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      Insets localInsets = paramContainer.getInsets();
      JRootPane localJRootPane = (JRootPane)paramContainer;
      Dimension localDimension1;
      if (localJRootPane.getContentPane() != null) {
        localDimension1 = localJRootPane.getContentPane().getPreferredSize();
      } else {
        localDimension1 = localJRootPane.getSize();
      }
      if (localDimension1 != null)
      {
        i = width;
        j = height;
      }
      if (localJRootPane.getMenuBar() != null)
      {
        Dimension localDimension2 = localJRootPane.getMenuBar().getPreferredSize();
        if (localDimension2 != null)
        {
          k = width;
          m = height;
        }
      }
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        JComponent localJComponent = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localJComponent != null)
        {
          Dimension localDimension3 = localJComponent.getPreferredSize();
          if (localDimension3 != null)
          {
            n = width;
            i1 = height;
          }
        }
      }
      return new Dimension(Math.max(Math.max(i, k), n) + left + right, j + m + n + top + bottom);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      Insets localInsets = paramContainer.getInsets();
      JRootPane localJRootPane = (JRootPane)paramContainer;
      Dimension localDimension1;
      if (localJRootPane.getContentPane() != null) {
        localDimension1 = localJRootPane.getContentPane().getMinimumSize();
      } else {
        localDimension1 = localJRootPane.getSize();
      }
      if (localDimension1 != null)
      {
        i = width;
        j = height;
      }
      if (localJRootPane.getMenuBar() != null)
      {
        Dimension localDimension2 = localJRootPane.getMenuBar().getMinimumSize();
        if (localDimension2 != null)
        {
          k = width;
          m = height;
        }
      }
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        JComponent localJComponent = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localJComponent != null)
        {
          Dimension localDimension3 = localJComponent.getMinimumSize();
          if (localDimension3 != null)
          {
            n = width;
            i1 = height;
          }
        }
      }
      return new Dimension(Math.max(Math.max(i, k), n) + left + right, j + m + n + top + bottom);
    }
    
    public Dimension maximumLayoutSize(Container paramContainer)
    {
      int i = Integer.MAX_VALUE;
      int j = Integer.MAX_VALUE;
      int k = Integer.MAX_VALUE;
      int m = Integer.MAX_VALUE;
      int n = Integer.MAX_VALUE;
      int i1 = Integer.MAX_VALUE;
      Insets localInsets = paramContainer.getInsets();
      JRootPane localJRootPane = (JRootPane)paramContainer;
      if (localJRootPane.getContentPane() != null)
      {
        Dimension localDimension1 = localJRootPane.getContentPane().getMaximumSize();
        if (localDimension1 != null)
        {
          i = width;
          j = height;
        }
      }
      if (localJRootPane.getMenuBar() != null)
      {
        Dimension localDimension2 = localJRootPane.getMenuBar().getMaximumSize();
        if (localDimension2 != null)
        {
          k = width;
          m = height;
        }
      }
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        JComponent localJComponent = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localJComponent != null)
        {
          Dimension localDimension3 = localJComponent.getMaximumSize();
          if (localDimension3 != null)
          {
            n = width;
            i1 = height;
          }
        }
      }
      int i2 = Math.max(Math.max(j, m), i1);
      if (i2 != Integer.MAX_VALUE) {
        i2 = j + m + i1 + top + bottom;
      }
      int i3 = Math.max(Math.max(i, k), n);
      if (i3 != Integer.MAX_VALUE) {
        i3 += left + right;
      }
      return new Dimension(i3, i2);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      JRootPane localJRootPane = (JRootPane)paramContainer;
      Rectangle localRectangle = localJRootPane.getBounds();
      Insets localInsets = localJRootPane.getInsets();
      int i = 0;
      int j = width - right - left;
      int k = height - top - bottom;
      if (localJRootPane.getLayeredPane() != null) {
        localJRootPane.getLayeredPane().setBounds(left, top, j, k);
      }
      if (localJRootPane.getGlassPane() != null) {
        localJRootPane.getGlassPane().setBounds(left, top, j, k);
      }
      Object localObject;
      if ((localJRootPane.getWindowDecorationStyle() != 0) && ((localJRootPane.getUI() instanceof MetalRootPaneUI)))
      {
        localObject = ((MetalRootPaneUI)localJRootPane.getUI()).getTitlePane();
        if (localObject != null)
        {
          Dimension localDimension = ((JComponent)localObject).getPreferredSize();
          if (localDimension != null)
          {
            int m = height;
            ((JComponent)localObject).setBounds(0, 0, j, m);
            i += m;
          }
        }
      }
      if (localJRootPane.getMenuBar() != null)
      {
        localObject = localJRootPane.getMenuBar().getPreferredSize();
        localJRootPane.getMenuBar().setBounds(0, i, j, height);
        i += height;
      }
      if (localJRootPane.getContentPane() != null)
      {
        localObject = localJRootPane.getContentPane().getPreferredSize();
        localJRootPane.getContentPane().setBounds(0, i, j, k < i ? 0 : k - i);
      }
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public void addLayoutComponent(Component paramComponent, Object paramObject) {}
    
    public float getLayoutAlignmentX(Container paramContainer)
    {
      return 0.0F;
    }
    
    public float getLayoutAlignmentY(Container paramContainer)
    {
      return 0.0F;
    }
    
    public void invalidateLayout(Container paramContainer) {}
  }
  
  private class MouseInputHandler
    implements MouseInputListener
  {
    private boolean isMovingWindow;
    private int dragCursor;
    private int dragOffsetX;
    private int dragOffsetY;
    private int dragWidth;
    private int dragHeight;
    
    private MouseInputHandler() {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      JRootPane localJRootPane = MetalRootPaneUI.this.getRootPane();
      if (localJRootPane.getWindowDecorationStyle() == 0) {
        return;
      }
      Point localPoint1 = paramMouseEvent.getPoint();
      Window localWindow = (Window)paramMouseEvent.getSource();
      if (localWindow != null) {
        localWindow.toFront();
      }
      Point localPoint2 = SwingUtilities.convertPoint(localWindow, localPoint1, MetalRootPaneUI.this.getTitlePane());
      Frame localFrame = null;
      Dialog localDialog = null;
      if ((localWindow instanceof Frame)) {
        localFrame = (Frame)localWindow;
      } else if ((localWindow instanceof Dialog)) {
        localDialog = (Dialog)localWindow;
      }
      int i = localFrame != null ? localFrame.getExtendedState() : 0;
      if ((MetalRootPaneUI.this.getTitlePane() != null) && (MetalRootPaneUI.this.getTitlePane().contains(localPoint2)))
      {
        if (((localFrame != null) && ((i & 0x6) == 0)) || ((localDialog != null) && (y >= 5) && (x >= 5) && (x < localWindow.getWidth() - 5)))
        {
          isMovingWindow = true;
          dragOffsetX = x;
          dragOffsetY = y;
        }
      }
      else if (((localFrame != null) && (localFrame.isResizable()) && ((i & 0x6) == 0)) || ((localDialog != null) && (localDialog.isResizable())))
      {
        dragOffsetX = x;
        dragOffsetY = y;
        dragWidth = localWindow.getWidth();
        dragHeight = localWindow.getHeight();
        dragCursor = getCursor(calculateCorner(localWindow, x, y));
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if ((dragCursor != 0) && (window != null) && (!window.isValid()))
      {
        window.validate();
        MetalRootPaneUI.this.getRootPane().repaint();
      }
      isMovingWindow = false;
      dragCursor = 0;
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      JRootPane localJRootPane = MetalRootPaneUI.this.getRootPane();
      if (localJRootPane.getWindowDecorationStyle() == 0) {
        return;
      }
      Window localWindow = (Window)paramMouseEvent.getSource();
      Frame localFrame = null;
      Dialog localDialog = null;
      if ((localWindow instanceof Frame)) {
        localFrame = (Frame)localWindow;
      } else if ((localWindow instanceof Dialog)) {
        localDialog = (Dialog)localWindow;
      }
      int i = getCursor(calculateCorner(localWindow, paramMouseEvent.getX(), paramMouseEvent.getY()));
      if ((i != 0) && (((localFrame != null) && (localFrame.isResizable()) && ((localFrame.getExtendedState() & 0x6) == 0)) || ((localDialog != null) && (localDialog.isResizable())))) {
        localWindow.setCursor(Cursor.getPredefinedCursor(i));
      } else {
        localWindow.setCursor(lastCursor);
      }
    }
    
    private void adjust(Rectangle paramRectangle, Dimension paramDimension, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      x += paramInt1;
      y += paramInt2;
      width += paramInt3;
      height += paramInt4;
      if (paramDimension != null)
      {
        int i;
        if (width < width)
        {
          i = width - width;
          if (paramInt1 != 0) {
            x -= i;
          }
          width = width;
        }
        if (height < height)
        {
          i = height - height;
          if (paramInt2 != 0) {
            y -= i;
          }
          height = height;
        }
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      Point localPoint = paramMouseEvent.getPoint();
      Object localObject;
      if (isMovingWindow)
      {
        localObject = paramMouseEvent.getLocationOnScreen();
        localWindow.setLocation(x - dragOffsetX, y - dragOffsetY);
      }
      else if (dragCursor != 0)
      {
        localObject = localWindow.getBounds();
        Rectangle localRectangle = new Rectangle((Rectangle)localObject);
        Dimension localDimension = localWindow.getMinimumSize();
        switch (dragCursor)
        {
        case 11: 
          adjust((Rectangle)localObject, localDimension, 0, 0, x + (dragWidth - dragOffsetX) - width, 0);
          break;
        case 9: 
          adjust((Rectangle)localObject, localDimension, 0, 0, 0, y + (dragHeight - dragOffsetY) - height);
          break;
        case 8: 
          adjust((Rectangle)localObject, localDimension, 0, y - dragOffsetY, 0, -(y - dragOffsetY));
          break;
        case 10: 
          adjust((Rectangle)localObject, localDimension, x - dragOffsetX, 0, -(x - dragOffsetX), 0);
          break;
        case 7: 
          adjust((Rectangle)localObject, localDimension, 0, y - dragOffsetY, x + (dragWidth - dragOffsetX) - width, -(y - dragOffsetY));
          break;
        case 5: 
          adjust((Rectangle)localObject, localDimension, 0, 0, x + (dragWidth - dragOffsetX) - width, y + (dragHeight - dragOffsetY) - height);
          break;
        case 6: 
          adjust((Rectangle)localObject, localDimension, x - dragOffsetX, y - dragOffsetY, -(x - dragOffsetX), -(y - dragOffsetY));
          break;
        case 4: 
          adjust((Rectangle)localObject, localDimension, x - dragOffsetX, 0, -(x - dragOffsetX), y + (dragHeight - dragOffsetY) - height);
          break;
        }
        if (!((Rectangle)localObject).equals(localRectangle))
        {
          localWindow.setBounds((Rectangle)localObject);
          if (Toolkit.getDefaultToolkit().isDynamicLayoutActive())
          {
            localWindow.validate();
            MetalRootPaneUI.this.getRootPane().repaint();
          }
        }
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      lastCursor = localWindow.getCursor();
      mouseMoved(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      localWindow.setCursor(lastCursor);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      Window localWindow = (Window)paramMouseEvent.getSource();
      Frame localFrame = null;
      if ((localWindow instanceof Frame)) {
        localFrame = (Frame)localWindow;
      } else {
        return;
      }
      Point localPoint = SwingUtilities.convertPoint(localWindow, paramMouseEvent.getPoint(), MetalRootPaneUI.this.getTitlePane());
      int i = localFrame.getExtendedState();
      if ((MetalRootPaneUI.this.getTitlePane() != null) && (MetalRootPaneUI.this.getTitlePane().contains(localPoint)) && (paramMouseEvent.getClickCount() % 2 == 0) && ((paramMouseEvent.getModifiers() & 0x10) != 0) && (localFrame.isResizable()))
      {
        if ((i & 0x6) != 0) {
          localFrame.setExtendedState(i & 0xFFFFFFF9);
        } else {
          localFrame.setExtendedState(i | 0x6);
        }
        return;
      }
    }
    
    private int calculateCorner(Window paramWindow, int paramInt1, int paramInt2)
    {
      Insets localInsets = paramWindow.getInsets();
      int i = calculatePosition(paramInt1 - left, paramWindow.getWidth() - left - right);
      int j = calculatePosition(paramInt2 - top, paramWindow.getHeight() - top - bottom);
      if ((i == -1) || (j == -1)) {
        return -1;
      }
      return j * 5 + i;
    }
    
    private int getCursor(int paramInt)
    {
      if (paramInt == -1) {
        return 0;
      }
      return MetalRootPaneUI.cursorMapping[paramInt];
    }
    
    private int calculatePosition(int paramInt1, int paramInt2)
    {
      if (paramInt1 < 5) {
        return 0;
      }
      if (paramInt1 < 16) {
        return 1;
      }
      if (paramInt1 >= paramInt2 - 5) {
        return 4;
      }
      if (paramInt1 >= paramInt2 - 16) {
        return 3;
      }
      return 2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalRootPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */