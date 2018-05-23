package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicScrollPaneUI
  extends ScrollPaneUI
  implements ScrollPaneConstants
{
  protected JScrollPane scrollpane;
  protected ChangeListener vsbChangeListener;
  protected ChangeListener hsbChangeListener;
  protected ChangeListener viewportChangeListener;
  protected PropertyChangeListener spPropertyChangeListener;
  private MouseWheelListener mouseScrollListener;
  private int oldExtent = Integer.MIN_VALUE;
  private PropertyChangeListener vsbPropertyChangeListener;
  private PropertyChangeListener hsbPropertyChangeListener;
  private Handler handler;
  private boolean setValueCalled = false;
  
  public BasicScrollPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicScrollPaneUI();
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("scrollUp"));
    paramLazyActionMap.put(new Actions("scrollDown"));
    paramLazyActionMap.put(new Actions("scrollHome"));
    paramLazyActionMap.put(new Actions("scrollEnd"));
    paramLazyActionMap.put(new Actions("unitScrollUp"));
    paramLazyActionMap.put(new Actions("unitScrollDown"));
    paramLazyActionMap.put(new Actions("scrollLeft"));
    paramLazyActionMap.put(new Actions("scrollRight"));
    paramLazyActionMap.put(new Actions("unitScrollRight"));
    paramLazyActionMap.put(new Actions("unitScrollLeft"));
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    Border localBorder = scrollpane.getViewportBorder();
    if (localBorder != null)
    {
      Rectangle localRectangle = scrollpane.getViewportBorderBounds();
      localBorder.paintBorder(scrollpane, paramGraphics, x, y, width, height);
    }
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(32767, 32767);
  }
  
  protected void installDefaults(JScrollPane paramJScrollPane)
  {
    LookAndFeel.installBorder(paramJScrollPane, "ScrollPane.border");
    LookAndFeel.installColorsAndFont(paramJScrollPane, "ScrollPane.background", "ScrollPane.foreground", "ScrollPane.font");
    Border localBorder = paramJScrollPane.getViewportBorder();
    if ((localBorder == null) || ((localBorder instanceof UIResource)))
    {
      localBorder = UIManager.getBorder("ScrollPane.viewportBorder");
      paramJScrollPane.setViewportBorder(localBorder);
    }
    LookAndFeel.installProperty(paramJScrollPane, "opaque", Boolean.TRUE);
  }
  
  protected void installListeners(JScrollPane paramJScrollPane)
  {
    vsbChangeListener = createVSBChangeListener();
    vsbPropertyChangeListener = createVSBPropertyChangeListener();
    hsbChangeListener = createHSBChangeListener();
    hsbPropertyChangeListener = createHSBPropertyChangeListener();
    viewportChangeListener = createViewportChangeListener();
    spPropertyChangeListener = createPropertyChangeListener();
    JViewport localJViewport = scrollpane.getViewport();
    JScrollBar localJScrollBar1 = scrollpane.getVerticalScrollBar();
    JScrollBar localJScrollBar2 = scrollpane.getHorizontalScrollBar();
    if (localJViewport != null) {
      localJViewport.addChangeListener(viewportChangeListener);
    }
    if (localJScrollBar1 != null)
    {
      localJScrollBar1.getModel().addChangeListener(vsbChangeListener);
      localJScrollBar1.addPropertyChangeListener(vsbPropertyChangeListener);
    }
    if (localJScrollBar2 != null)
    {
      localJScrollBar2.getModel().addChangeListener(hsbChangeListener);
      localJScrollBar2.addPropertyChangeListener(hsbPropertyChangeListener);
    }
    scrollpane.addPropertyChangeListener(spPropertyChangeListener);
    mouseScrollListener = createMouseWheelListener();
    scrollpane.addMouseWheelListener(mouseScrollListener);
  }
  
  protected void installKeyboardActions(JScrollPane paramJScrollPane)
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(paramJScrollPane, 1, localInputMap);
    LazyActionMap.installLazyActionMap(paramJScrollPane, BasicScrollPaneUI.class, "ScrollPane.actionMap");
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1)
    {
      InputMap localInputMap1 = (InputMap)DefaultLookup.get(scrollpane, this, "ScrollPane.ancestorInputMap");
      InputMap localInputMap2;
      if ((scrollpane.getComponentOrientation().isLeftToRight()) || ((localInputMap2 = (InputMap)DefaultLookup.get(scrollpane, this, "ScrollPane.ancestorInputMap.RightToLeft")) == null)) {
        return localInputMap1;
      }
      localInputMap2.setParent(localInputMap1);
      return localInputMap2;
    }
    return null;
  }
  
  public void installUI(JComponent paramJComponent)
  {
    scrollpane = ((JScrollPane)paramJComponent);
    installDefaults(scrollpane);
    installListeners(scrollpane);
    installKeyboardActions(scrollpane);
  }
  
  protected void uninstallDefaults(JScrollPane paramJScrollPane)
  {
    LookAndFeel.uninstallBorder(scrollpane);
    if ((scrollpane.getViewportBorder() instanceof UIResource)) {
      scrollpane.setViewportBorder(null);
    }
  }
  
  protected void uninstallListeners(JComponent paramJComponent)
  {
    JViewport localJViewport = scrollpane.getViewport();
    JScrollBar localJScrollBar1 = scrollpane.getVerticalScrollBar();
    JScrollBar localJScrollBar2 = scrollpane.getHorizontalScrollBar();
    if (localJViewport != null) {
      localJViewport.removeChangeListener(viewportChangeListener);
    }
    if (localJScrollBar1 != null)
    {
      localJScrollBar1.getModel().removeChangeListener(vsbChangeListener);
      localJScrollBar1.removePropertyChangeListener(vsbPropertyChangeListener);
    }
    if (localJScrollBar2 != null)
    {
      localJScrollBar2.getModel().removeChangeListener(hsbChangeListener);
      localJScrollBar2.removePropertyChangeListener(hsbPropertyChangeListener);
    }
    scrollpane.removePropertyChangeListener(spPropertyChangeListener);
    if (mouseScrollListener != null) {
      scrollpane.removeMouseWheelListener(mouseScrollListener);
    }
    vsbChangeListener = null;
    hsbChangeListener = null;
    viewportChangeListener = null;
    spPropertyChangeListener = null;
    mouseScrollListener = null;
    handler = null;
  }
  
  protected void uninstallKeyboardActions(JScrollPane paramJScrollPane)
  {
    SwingUtilities.replaceUIActionMap(paramJScrollPane, null);
    SwingUtilities.replaceUIInputMap(paramJScrollPane, 1, null);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallDefaults(scrollpane);
    uninstallListeners(scrollpane);
    uninstallKeyboardActions(scrollpane);
    scrollpane = null;
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }
  
  protected void syncScrollPaneWithViewport()
  {
    JViewport localJViewport1 = scrollpane.getViewport();
    JScrollBar localJScrollBar1 = scrollpane.getVerticalScrollBar();
    JScrollBar localJScrollBar2 = scrollpane.getHorizontalScrollBar();
    JViewport localJViewport2 = scrollpane.getRowHeader();
    JViewport localJViewport3 = scrollpane.getColumnHeader();
    boolean bool = scrollpane.getComponentOrientation().isLeftToRight();
    if (localJViewport1 != null)
    {
      Dimension localDimension1 = localJViewport1.getExtentSize();
      Dimension localDimension2 = localJViewport1.getViewSize();
      Point localPoint1 = localJViewport1.getViewPosition();
      int i;
      int j;
      int k;
      if (localJScrollBar1 != null)
      {
        i = height;
        j = height;
        k = Math.max(0, Math.min(y, j - i));
        localJScrollBar1.setValues(k, i, 0, j);
      }
      if (localJScrollBar2 != null)
      {
        i = width;
        j = width;
        if (bool)
        {
          k = Math.max(0, Math.min(x, j - i));
        }
        else
        {
          int m = localJScrollBar2.getValue();
          if ((setValueCalled) && (j - m == x))
          {
            k = Math.max(0, Math.min(j - i, m));
            if (i != 0) {
              setValueCalled = false;
            }
          }
          else if (i > j)
          {
            x = (j - i);
            localJViewport1.setViewPosition(localPoint1);
            k = 0;
          }
          else
          {
            k = Math.max(0, Math.min(j - i, j - i - x));
            if (oldExtent > i) {
              k -= oldExtent - i;
            }
          }
        }
        oldExtent = i;
        localJScrollBar2.setValues(k, i, 0, j);
      }
      Point localPoint2;
      if (localJViewport2 != null)
      {
        localPoint2 = localJViewport2.getViewPosition();
        y = getViewPositiony;
        x = 0;
        localJViewport2.setViewPosition(localPoint2);
      }
      if (localJViewport3 != null)
      {
        localPoint2 = localJViewport3.getViewPosition();
        if (bool) {
          x = getViewPositionx;
        } else {
          x = Math.max(0, getViewPositionx);
        }
        y = 0;
        localJViewport3.setViewPosition(localPoint2);
      }
    }
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    if (paramJComponent == null) {
      throw new NullPointerException("Component must be non-null");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Width and height must be >= 0");
    }
    JViewport localJViewport1 = scrollpane.getViewport();
    Insets localInsets = scrollpane.getInsets();
    int i = top;
    paramInt2 = paramInt2 - top - bottom;
    paramInt1 = paramInt1 - left - right;
    JViewport localJViewport2 = scrollpane.getColumnHeader();
    Object localObject1;
    if ((localJViewport2 != null) && (localJViewport2.isVisible()))
    {
      localComponent = localJViewport2.getView();
      if ((localComponent != null) && (localComponent.isVisible()))
      {
        localObject1 = localComponent.getPreferredSize();
        int j = localComponent.getBaseline(width, height);
        if (j >= 0) {
          return i + j;
        }
      }
      localObject1 = localJViewport2.getPreferredSize();
      paramInt2 -= height;
      i += height;
    }
    Component localComponent = localJViewport1 == null ? null : localJViewport1.getView();
    if ((localComponent != null) && (localComponent.isVisible()) && (localComponent.getBaselineResizeBehavior() == Component.BaselineResizeBehavior.CONSTANT_ASCENT))
    {
      localObject1 = scrollpane.getViewportBorder();
      Object localObject2;
      if (localObject1 != null)
      {
        localObject2 = ((Border)localObject1).getBorderInsets(scrollpane);
        i += top;
        paramInt2 = paramInt2 - top - bottom;
        paramInt1 = paramInt1 - left - right;
      }
      if ((localComponent.getWidth() > 0) && (localComponent.getHeight() > 0))
      {
        localObject2 = localComponent.getMinimumSize();
        paramInt1 = Math.max(width, localComponent.getWidth());
        paramInt2 = Math.max(height, localComponent.getHeight());
      }
      if ((paramInt1 > 0) && (paramInt2 > 0))
      {
        int k = localComponent.getBaseline(paramInt1, paramInt2);
        if (k > 0) {
          return i + k;
        }
      }
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  protected ChangeListener createViewportChangeListener()
  {
    return getHandler();
  }
  
  private PropertyChangeListener createHSBPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected ChangeListener createHSBChangeListener()
  {
    return getHandler();
  }
  
  private PropertyChangeListener createVSBPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected ChangeListener createVSBChangeListener()
  {
    return getHandler();
  }
  
  protected MouseWheelListener createMouseWheelListener()
  {
    return getHandler();
  }
  
  protected void updateScrollBarDisplayPolicy(PropertyChangeEvent paramPropertyChangeEvent)
  {
    scrollpane.revalidate();
    scrollpane.repaint();
  }
  
  protected void updateViewport(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JViewport localJViewport1 = (JViewport)paramPropertyChangeEvent.getOldValue();
    JViewport localJViewport2 = (JViewport)paramPropertyChangeEvent.getNewValue();
    if (localJViewport1 != null) {
      localJViewport1.removeChangeListener(viewportChangeListener);
    }
    if (localJViewport2 != null)
    {
      Point localPoint = localJViewport2.getViewPosition();
      if (scrollpane.getComponentOrientation().isLeftToRight())
      {
        x = Math.max(x, 0);
      }
      else
      {
        int i = getViewSizewidth;
        int j = getExtentSizewidth;
        if (j > i) {
          x = (i - j);
        } else {
          x = Math.max(0, Math.min(i - j, x));
        }
      }
      y = Math.max(y, 0);
      localJViewport2.setViewPosition(localPoint);
      localJViewport2.addChangeListener(viewportChangeListener);
    }
  }
  
  protected void updateRowHeader(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JViewport localJViewport1 = (JViewport)paramPropertyChangeEvent.getNewValue();
    if (localJViewport1 != null)
    {
      JViewport localJViewport2 = scrollpane.getViewport();
      Point localPoint = localJViewport1.getViewPosition();
      y = (localJViewport2 != null ? getViewPositiony : 0);
      localJViewport1.setViewPosition(localPoint);
    }
  }
  
  protected void updateColumnHeader(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JViewport localJViewport1 = (JViewport)paramPropertyChangeEvent.getNewValue();
    if (localJViewport1 != null)
    {
      JViewport localJViewport2 = scrollpane.getViewport();
      Point localPoint = localJViewport1.getViewPosition();
      if (localJViewport2 == null) {
        x = 0;
      } else if (scrollpane.getComponentOrientation().isLeftToRight()) {
        x = getViewPositionx;
      } else {
        x = Math.max(0, getViewPositionx);
      }
      localJViewport1.setViewPosition(localPoint);
      scrollpane.add(localJViewport1, "COLUMN_HEADER");
    }
  }
  
  private void updateHorizontalScrollBar(PropertyChangeEvent paramPropertyChangeEvent)
  {
    updateScrollBar(paramPropertyChangeEvent, hsbChangeListener, hsbPropertyChangeListener);
  }
  
  private void updateVerticalScrollBar(PropertyChangeEvent paramPropertyChangeEvent)
  {
    updateScrollBar(paramPropertyChangeEvent, vsbChangeListener, vsbPropertyChangeListener);
  }
  
  private void updateScrollBar(PropertyChangeEvent paramPropertyChangeEvent, ChangeListener paramChangeListener, PropertyChangeListener paramPropertyChangeListener)
  {
    JScrollBar localJScrollBar = (JScrollBar)paramPropertyChangeEvent.getOldValue();
    if (localJScrollBar != null)
    {
      if (paramChangeListener != null) {
        localJScrollBar.getModel().removeChangeListener(paramChangeListener);
      }
      if (paramPropertyChangeListener != null) {
        localJScrollBar.removePropertyChangeListener(paramPropertyChangeListener);
      }
    }
    localJScrollBar = (JScrollBar)paramPropertyChangeEvent.getNewValue();
    if (localJScrollBar != null)
    {
      if (paramChangeListener != null) {
        localJScrollBar.getModel().addChangeListener(paramChangeListener);
      }
      if (paramPropertyChangeListener != null) {
        localJScrollBar.addPropertyChangeListener(paramPropertyChangeListener);
      }
    }
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String SCROLL_UP = "scrollUp";
    private static final String SCROLL_DOWN = "scrollDown";
    private static final String SCROLL_HOME = "scrollHome";
    private static final String SCROLL_END = "scrollEnd";
    private static final String UNIT_SCROLL_UP = "unitScrollUp";
    private static final String UNIT_SCROLL_DOWN = "unitScrollDown";
    private static final String SCROLL_LEFT = "scrollLeft";
    private static final String SCROLL_RIGHT = "scrollRight";
    private static final String UNIT_SCROLL_LEFT = "unitScrollLeft";
    private static final String UNIT_SCROLL_RIGHT = "unitScrollRight";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JScrollPane localJScrollPane = (JScrollPane)paramActionEvent.getSource();
      boolean bool = localJScrollPane.getComponentOrientation().isLeftToRight();
      String str = getName();
      if (str == "scrollUp") {
        scroll(localJScrollPane, 1, -1, true);
      } else if (str == "scrollDown") {
        scroll(localJScrollPane, 1, 1, true);
      } else if (str == "scrollHome") {
        scrollHome(localJScrollPane);
      } else if (str == "scrollEnd") {
        scrollEnd(localJScrollPane);
      } else if (str == "unitScrollUp") {
        scroll(localJScrollPane, 1, -1, false);
      } else if (str == "unitScrollDown") {
        scroll(localJScrollPane, 1, 1, false);
      } else if (str == "scrollLeft") {
        scroll(localJScrollPane, 0, bool ? -1 : 1, true);
      } else if (str == "scrollRight") {
        scroll(localJScrollPane, 0, bool ? 1 : -1, true);
      } else if (str == "unitScrollLeft") {
        scroll(localJScrollPane, 0, bool ? -1 : 1, false);
      } else if (str == "unitScrollRight") {
        scroll(localJScrollPane, 0, bool ? 1 : -1, false);
      }
    }
    
    private void scrollEnd(JScrollPane paramJScrollPane)
    {
      JViewport localJViewport = paramJScrollPane.getViewport();
      Component localComponent;
      if ((localJViewport != null) && ((localComponent = localJViewport.getView()) != null))
      {
        Rectangle localRectangle1 = localJViewport.getViewRect();
        Rectangle localRectangle2 = localComponent.getBounds();
        if (paramJScrollPane.getComponentOrientation().isLeftToRight()) {
          localJViewport.setViewPosition(new Point(width - width, height - height));
        } else {
          localJViewport.setViewPosition(new Point(0, height - height));
        }
      }
    }
    
    private void scrollHome(JScrollPane paramJScrollPane)
    {
      JViewport localJViewport = paramJScrollPane.getViewport();
      Component localComponent;
      if ((localJViewport != null) && ((localComponent = localJViewport.getView()) != null)) {
        if (paramJScrollPane.getComponentOrientation().isLeftToRight())
        {
          localJViewport.setViewPosition(new Point(0, 0));
        }
        else
        {
          Rectangle localRectangle1 = localJViewport.getViewRect();
          Rectangle localRectangle2 = localComponent.getBounds();
          localJViewport.setViewPosition(new Point(width - width, 0));
        }
      }
    }
    
    private void scroll(JScrollPane paramJScrollPane, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      JViewport localJViewport = paramJScrollPane.getViewport();
      Component localComponent;
      if ((localJViewport != null) && ((localComponent = localJViewport.getView()) != null))
      {
        Rectangle localRectangle = localJViewport.getViewRect();
        Dimension localDimension = localComponent.getSize();
        int i;
        if ((localComponent instanceof Scrollable))
        {
          if (paramBoolean) {
            i = ((Scrollable)localComponent).getScrollableBlockIncrement(localRectangle, paramInt1, paramInt2);
          } else {
            i = ((Scrollable)localComponent).getScrollableUnitIncrement(localRectangle, paramInt1, paramInt2);
          }
        }
        else if (paramBoolean)
        {
          if (paramInt1 == 1) {
            i = height;
          } else {
            i = width;
          }
        }
        else {
          i = 10;
        }
        if (paramInt1 == 1)
        {
          y += i * paramInt2;
          if (y + height > height) {
            y = Math.max(0, height - height);
          } else if (y < 0) {
            y = 0;
          }
        }
        else if (paramJScrollPane.getComponentOrientation().isLeftToRight())
        {
          x += i * paramInt2;
          if (x + width > width) {
            x = Math.max(0, width - width);
          } else if (x < 0) {
            x = 0;
          }
        }
        else
        {
          x -= i * paramInt2;
          if (width > width) {
            x = (width - width);
          } else {
            x = Math.max(0, Math.min(width - width, x));
          }
        }
        localJViewport.setViewPosition(localRectangle.getLocation());
      }
    }
  }
  
  public class HSBChangeListener
    implements ChangeListener
  {
    public HSBChangeListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicScrollPaneUI.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
  
  class Handler
    implements ChangeListener, PropertyChangeListener, MouseWheelListener
  {
    Handler() {}
    
    public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent)
    {
      if ((scrollpane.isWheelScrollingEnabled()) && (paramMouseWheelEvent.getWheelRotation() != 0))
      {
        JScrollBar localJScrollBar = scrollpane.getVerticalScrollBar();
        int i = paramMouseWheelEvent.getWheelRotation() < 0 ? -1 : 1;
        int j = 1;
        if ((localJScrollBar == null) || (!localJScrollBar.isVisible()) || (paramMouseWheelEvent.isShiftDown()))
        {
          localJScrollBar = scrollpane.getHorizontalScrollBar();
          if ((localJScrollBar == null) || (!localJScrollBar.isVisible())) {
            return;
          }
          j = 0;
        }
        paramMouseWheelEvent.consume();
        if (paramMouseWheelEvent.getScrollType() == 0)
        {
          JViewport localJViewport = scrollpane.getViewport();
          if (localJViewport == null) {
            return;
          }
          Component localComponent = localJViewport.getView();
          int k = Math.abs(paramMouseWheelEvent.getUnitsToScroll());
          boolean bool1 = Math.abs(paramMouseWheelEvent.getWheelRotation()) == 1;
          Object localObject = localJScrollBar.getClientProperty("JScrollBar.fastWheelScrolling");
          if ((Boolean.TRUE == localObject) && ((localComponent instanceof Scrollable)))
          {
            Scrollable localScrollable = (Scrollable)localComponent;
            Rectangle localRectangle = localJViewport.getViewRect();
            int m = x;
            boolean bool2 = localComponent.getComponentOrientation().isLeftToRight();
            int n = localJScrollBar.getMinimum();
            int i1 = localJScrollBar.getMaximum() - localJScrollBar.getModel().getExtent();
            if (bool1)
            {
              i2 = localScrollable.getScrollableBlockIncrement(localRectangle, j, i);
              if (i < 0) {
                n = Math.max(n, localJScrollBar.getValue() - i2);
              } else {
                i1 = Math.min(i1, localJScrollBar.getValue() + i2);
              }
            }
            for (int i2 = 0; i2 < k; i2++)
            {
              int i3 = localScrollable.getScrollableUnitIncrement(localRectangle, j, i);
              if (j == 1)
              {
                if (i < 0)
                {
                  y -= i3;
                  if (y <= n)
                  {
                    y = n;
                    break;
                  }
                }
                else
                {
                  y += i3;
                  if (y >= i1)
                  {
                    y = i1;
                    break;
                  }
                }
              }
              else if (((bool2) && (i < 0)) || ((!bool2) && (i > 0)))
              {
                x -= i3;
                if ((bool2) && (x < n))
                {
                  x = n;
                  break;
                }
              }
              else if (((bool2) && (i > 0)) || ((!bool2) && (i < 0)))
              {
                x += i3;
                if ((bool2) && (x > i1))
                {
                  x = i1;
                  break;
                }
              }
              else if (!$assertionsDisabled)
              {
                throw new AssertionError("Non-sensical ComponentOrientation / scroll direction");
              }
            }
            if (j == 1)
            {
              localJScrollBar.setValue(y);
            }
            else if (bool2)
            {
              localJScrollBar.setValue(x);
            }
            else
            {
              i2 = localJScrollBar.getValue() - (x - m);
              if (i2 < n) {
                i2 = n;
              } else if (i2 > i1) {
                i2 = i1;
              }
              localJScrollBar.setValue(i2);
            }
          }
          else
          {
            BasicScrollBarUI.scrollByUnits(localJScrollBar, i, k, bool1);
          }
        }
        else if (paramMouseWheelEvent.getScrollType() == 1)
        {
          BasicScrollBarUI.scrollByBlock(localJScrollBar, i);
        }
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      JViewport localJViewport = scrollpane.getViewport();
      if (localJViewport != null) {
        if (paramChangeEvent.getSource() == localJViewport)
        {
          syncScrollPaneWithViewport();
        }
        else
        {
          JScrollBar localJScrollBar1 = scrollpane.getHorizontalScrollBar();
          if ((localJScrollBar1 != null) && (paramChangeEvent.getSource() == localJScrollBar1.getModel()))
          {
            hsbStateChanged(localJViewport, paramChangeEvent);
          }
          else
          {
            JScrollBar localJScrollBar2 = scrollpane.getVerticalScrollBar();
            if ((localJScrollBar2 != null) && (paramChangeEvent.getSource() == localJScrollBar2.getModel())) {
              vsbStateChanged(localJViewport, paramChangeEvent);
            }
          }
        }
      }
    }
    
    private void vsbStateChanged(JViewport paramJViewport, ChangeEvent paramChangeEvent)
    {
      BoundedRangeModel localBoundedRangeModel = (BoundedRangeModel)paramChangeEvent.getSource();
      Point localPoint = paramJViewport.getViewPosition();
      y = localBoundedRangeModel.getValue();
      paramJViewport.setViewPosition(localPoint);
    }
    
    private void hsbStateChanged(JViewport paramJViewport, ChangeEvent paramChangeEvent)
    {
      BoundedRangeModel localBoundedRangeModel = (BoundedRangeModel)paramChangeEvent.getSource();
      Point localPoint = paramJViewport.getViewPosition();
      int i = localBoundedRangeModel.getValue();
      if (scrollpane.getComponentOrientation().isLeftToRight())
      {
        x = i;
      }
      else
      {
        int j = getViewSizewidth;
        int k = getExtentSizewidth;
        int m = x;
        x = (j - k - i);
        if ((k == 0) && (i != 0) && (m == j)) {
          setValueCalled = true;
        } else if ((k != 0) && (m < 0) && (x == 0)) {
          x += i;
        }
      }
      paramJViewport.setViewPosition(localPoint);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getSource() == scrollpane) {
        scrollPanePropertyChange(paramPropertyChangeEvent);
      } else {
        sbPropertyChange(paramPropertyChangeEvent);
      }
    }
    
    private void scrollPanePropertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str == "verticalScrollBarDisplayPolicy")
      {
        updateScrollBarDisplayPolicy(paramPropertyChangeEvent);
      }
      else if (str == "horizontalScrollBarDisplayPolicy")
      {
        updateScrollBarDisplayPolicy(paramPropertyChangeEvent);
      }
      else if (str == "viewport")
      {
        updateViewport(paramPropertyChangeEvent);
      }
      else if (str == "rowHeader")
      {
        updateRowHeader(paramPropertyChangeEvent);
      }
      else if (str == "columnHeader")
      {
        updateColumnHeader(paramPropertyChangeEvent);
      }
      else if (str == "verticalScrollBar")
      {
        BasicScrollPaneUI.this.updateVerticalScrollBar(paramPropertyChangeEvent);
      }
      else if (str == "horizontalScrollBar")
      {
        BasicScrollPaneUI.this.updateHorizontalScrollBar(paramPropertyChangeEvent);
      }
      else if (str == "componentOrientation")
      {
        scrollpane.revalidate();
        scrollpane.repaint();
      }
    }
    
    private void sbPropertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object localObject1 = paramPropertyChangeEvent.getSource();
      JScrollBar localJScrollBar;
      Object localObject2;
      Object localObject3;
      if ("model" == str)
      {
        localJScrollBar = scrollpane.getVerticalScrollBar();
        localObject2 = (BoundedRangeModel)paramPropertyChangeEvent.getOldValue();
        localObject3 = null;
        if (localObject1 == localJScrollBar)
        {
          localObject3 = vsbChangeListener;
        }
        else if (localObject1 == scrollpane.getHorizontalScrollBar())
        {
          localJScrollBar = scrollpane.getHorizontalScrollBar();
          localObject3 = hsbChangeListener;
        }
        if (localObject3 != null)
        {
          if (localObject2 != null) {
            ((BoundedRangeModel)localObject2).removeChangeListener((ChangeListener)localObject3);
          }
          if (localJScrollBar.getModel() != null) {
            localJScrollBar.getModel().addChangeListener((ChangeListener)localObject3);
          }
        }
      }
      else if (("componentOrientation" == str) && (localObject1 == scrollpane.getHorizontalScrollBar()))
      {
        localJScrollBar = scrollpane.getHorizontalScrollBar();
        localObject2 = scrollpane.getViewport();
        localObject3 = ((JViewport)localObject2).getViewPosition();
        if (scrollpane.getComponentOrientation().isLeftToRight()) {
          x = localJScrollBar.getValue();
        } else {
          x = (getViewSizewidth - getExtentSizewidth - localJScrollBar.getValue());
        }
        ((JViewport)localObject2).setViewPosition((Point)localObject3);
      }
    }
  }
  
  protected class MouseWheelHandler
    implements MouseWheelListener
  {
    protected MouseWheelHandler() {}
    
    public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent)
    {
      BasicScrollPaneUI.this.getHandler().mouseWheelMoved(paramMouseWheelEvent);
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicScrollPaneUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
  
  public class VSBChangeListener
    implements ChangeListener
  {
    public VSBChangeListener() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicScrollPaneUI.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
  
  public class ViewportChangeHandler
    implements ChangeListener
  {
    public ViewportChangeHandler() {}
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      BasicScrollPaneUI.this.getHandler().stateChanged(paramChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */