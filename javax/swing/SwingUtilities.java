package javax.swing;

import java.applet.Applet;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class SwingUtilities
  implements SwingConstants
{
  private static boolean canAccessEventQueue = false;
  private static boolean eventQueueTested = false;
  private static boolean suppressDropSupport;
  private static boolean checkedSuppressDropSupport;
  private static final Object sharedOwnerFrameKey = new StringBuffer("SwingUtilities.sharedOwnerFrame");
  
  private static boolean getSuppressDropTarget()
  {
    if (!checkedSuppressDropSupport)
    {
      suppressDropSupport = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("suppressSwingDropSupport"))).booleanValue();
      checkedSuppressDropSupport = true;
    }
    return suppressDropSupport;
  }
  
  static void installSwingDropTargetAsNecessary(Component paramComponent, TransferHandler paramTransferHandler)
  {
    if (!getSuppressDropTarget())
    {
      DropTarget localDropTarget = paramComponent.getDropTarget();
      if ((localDropTarget == null) || ((localDropTarget instanceof UIResource))) {
        if (paramTransferHandler == null) {
          paramComponent.setDropTarget(null);
        } else if (!GraphicsEnvironment.isHeadless()) {
          paramComponent.setDropTarget(new TransferHandler.SwingDropTarget(paramComponent));
        }
      }
    }
  }
  
  public static final boolean isRectangleContainingRectangle(Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    return (x >= x) && (x + width <= x + width) && (y >= y) && (y + height <= y + height);
  }
  
  public static Rectangle getLocalBounds(Component paramComponent)
  {
    Rectangle localRectangle = new Rectangle(paramComponent.getBounds());
    x = (y = 0);
    return localRectangle;
  }
  
  public static Window getWindowAncestor(Component paramComponent)
  {
    for (Container localContainer = paramComponent.getParent(); localContainer != null; localContainer = localContainer.getParent()) {
      if ((localContainer instanceof Window)) {
        return (Window)localContainer;
      }
    }
    return null;
  }
  
  static Point convertScreenLocationToParent(Container paramContainer, int paramInt1, int paramInt2)
  {
    for (Container localContainer = paramContainer; localContainer != null; localContainer = localContainer.getParent()) {
      if ((localContainer instanceof Window))
      {
        Point localPoint = new Point(paramInt1, paramInt2);
        convertPointFromScreen(localPoint, paramContainer);
        return localPoint;
      }
    }
    throw new Error("convertScreenLocationToParent: no window ancestor");
  }
  
  public static Point convertPoint(Component paramComponent1, Point paramPoint, Component paramComponent2)
  {
    if ((paramComponent1 == null) && (paramComponent2 == null)) {
      return paramPoint;
    }
    if (paramComponent1 == null)
    {
      paramComponent1 = getWindowAncestor(paramComponent2);
      if (paramComponent1 == null) {
        throw new Error("Source component not connected to component tree hierarchy");
      }
    }
    Point localPoint = new Point(paramPoint);
    convertPointToScreen(localPoint, paramComponent1);
    if (paramComponent2 == null)
    {
      paramComponent2 = getWindowAncestor(paramComponent1);
      if (paramComponent2 == null) {
        throw new Error("Destination component not connected to component tree hierarchy");
      }
    }
    convertPointFromScreen(localPoint, paramComponent2);
    return localPoint;
  }
  
  public static Point convertPoint(Component paramComponent1, int paramInt1, int paramInt2, Component paramComponent2)
  {
    Point localPoint = new Point(paramInt1, paramInt2);
    return convertPoint(paramComponent1, localPoint, paramComponent2);
  }
  
  public static Rectangle convertRectangle(Component paramComponent1, Rectangle paramRectangle, Component paramComponent2)
  {
    Point localPoint = new Point(x, y);
    localPoint = convertPoint(paramComponent1, localPoint, paramComponent2);
    return new Rectangle(x, y, width, height);
  }
  
  public static Container getAncestorOfClass(Class<?> paramClass, Component paramComponent)
  {
    if ((paramComponent == null) || (paramClass == null)) {
      return null;
    }
    for (Container localContainer = paramComponent.getParent(); (localContainer != null) && (!paramClass.isInstance(localContainer)); localContainer = localContainer.getParent()) {}
    return localContainer;
  }
  
  public static Container getAncestorNamed(String paramString, Component paramComponent)
  {
    if ((paramComponent == null) || (paramString == null)) {
      return null;
    }
    for (Container localContainer = paramComponent.getParent(); (localContainer != null) && (!paramString.equals(localContainer.getName())); localContainer = localContainer.getParent()) {}
    return localContainer;
  }
  
  public static Component getDeepestComponentAt(Component paramComponent, int paramInt1, int paramInt2)
  {
    if (!paramComponent.contains(paramInt1, paramInt2)) {
      return null;
    }
    if ((paramComponent instanceof Container))
    {
      Component[] arrayOfComponent1 = ((Container)paramComponent).getComponents();
      for (Component localComponent : arrayOfComponent1) {
        if ((localComponent != null) && (localComponent.isVisible()))
        {
          Point localPoint = localComponent.getLocation();
          if ((localComponent instanceof Container)) {
            localComponent = getDeepestComponentAt(localComponent, paramInt1 - x, paramInt2 - y);
          } else {
            localComponent = localComponent.getComponentAt(paramInt1 - x, paramInt2 - y);
          }
          if ((localComponent != null) && (localComponent.isVisible())) {
            return localComponent;
          }
        }
      }
    }
    return paramComponent;
  }
  
  public static MouseEvent convertMouseEvent(Component paramComponent1, MouseEvent paramMouseEvent, Component paramComponent2)
  {
    Point localPoint = convertPoint(paramComponent1, new Point(paramMouseEvent.getX(), paramMouseEvent.getY()), paramComponent2);
    Component localComponent;
    if (paramComponent2 != null) {
      localComponent = paramComponent2;
    } else {
      localComponent = paramComponent1;
    }
    Object localObject2;
    Object localObject1;
    if ((paramMouseEvent instanceof MouseWheelEvent))
    {
      localObject2 = (MouseWheelEvent)paramMouseEvent;
      localObject1 = new MouseWheelEvent(localComponent, ((MouseWheelEvent)localObject2).getID(), ((MouseWheelEvent)localObject2).getWhen(), ((MouseWheelEvent)localObject2).getModifiers() | ((MouseWheelEvent)localObject2).getModifiersEx(), x, y, ((MouseWheelEvent)localObject2).getXOnScreen(), ((MouseWheelEvent)localObject2).getYOnScreen(), ((MouseWheelEvent)localObject2).getClickCount(), ((MouseWheelEvent)localObject2).isPopupTrigger(), ((MouseWheelEvent)localObject2).getScrollType(), ((MouseWheelEvent)localObject2).getScrollAmount(), ((MouseWheelEvent)localObject2).getWheelRotation());
    }
    else if ((paramMouseEvent instanceof MenuDragMouseEvent))
    {
      localObject2 = (MenuDragMouseEvent)paramMouseEvent;
      localObject1 = new MenuDragMouseEvent(localComponent, ((MenuDragMouseEvent)localObject2).getID(), ((MenuDragMouseEvent)localObject2).getWhen(), ((MenuDragMouseEvent)localObject2).getModifiers() | ((MenuDragMouseEvent)localObject2).getModifiersEx(), x, y, ((MenuDragMouseEvent)localObject2).getXOnScreen(), ((MenuDragMouseEvent)localObject2).getYOnScreen(), ((MenuDragMouseEvent)localObject2).getClickCount(), ((MenuDragMouseEvent)localObject2).isPopupTrigger(), ((MenuDragMouseEvent)localObject2).getPath(), ((MenuDragMouseEvent)localObject2).getMenuSelectionManager());
    }
    else
    {
      localObject1 = new MouseEvent(localComponent, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers() | paramMouseEvent.getModifiersEx(), x, y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), paramMouseEvent.getButton());
    }
    return (MouseEvent)localObject1;
  }
  
  public static void convertPointToScreen(Point paramPoint, Component paramComponent)
  {
    do
    {
      int i;
      int j;
      if ((paramComponent instanceof JComponent))
      {
        i = paramComponent.getX();
        j = paramComponent.getY();
      }
      else if (((paramComponent instanceof Applet)) || ((paramComponent instanceof Window)))
      {
        try
        {
          Point localPoint = paramComponent.getLocationOnScreen();
          i = x;
          j = y;
        }
        catch (IllegalComponentStateException localIllegalComponentStateException)
        {
          i = paramComponent.getX();
          j = paramComponent.getY();
        }
      }
      else
      {
        i = paramComponent.getX();
        j = paramComponent.getY();
      }
      x += i;
      y += j;
      if (((paramComponent instanceof Window)) || ((paramComponent instanceof Applet))) {
        break;
      }
      paramComponent = paramComponent.getParent();
    } while (paramComponent != null);
  }
  
  public static void convertPointFromScreen(Point paramPoint, Component paramComponent)
  {
    do
    {
      int i;
      int j;
      if ((paramComponent instanceof JComponent))
      {
        i = paramComponent.getX();
        j = paramComponent.getY();
      }
      else if (((paramComponent instanceof Applet)) || ((paramComponent instanceof Window)))
      {
        try
        {
          Point localPoint = paramComponent.getLocationOnScreen();
          i = x;
          j = y;
        }
        catch (IllegalComponentStateException localIllegalComponentStateException)
        {
          i = paramComponent.getX();
          j = paramComponent.getY();
        }
      }
      else
      {
        i = paramComponent.getX();
        j = paramComponent.getY();
      }
      x -= i;
      y -= j;
      if (((paramComponent instanceof Window)) || ((paramComponent instanceof Applet))) {
        break;
      }
      paramComponent = paramComponent.getParent();
    } while (paramComponent != null);
  }
  
  public static Window windowForComponent(Component paramComponent)
  {
    return getWindowAncestor(paramComponent);
  }
  
  public static boolean isDescendingFrom(Component paramComponent1, Component paramComponent2)
  {
    if (paramComponent1 == paramComponent2) {
      return true;
    }
    for (Container localContainer = paramComponent1.getParent(); localContainer != null; localContainer = localContainer.getParent()) {
      if (localContainer == paramComponent2) {
        return true;
      }
    }
    return false;
  }
  
  public static Rectangle computeIntersection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle)
  {
    int i = paramInt1 > x ? paramInt1 : x;
    int j = paramInt1 + paramInt3 < x + width ? paramInt1 + paramInt3 : x + width;
    int k = paramInt2 > y ? paramInt2 : y;
    int m = paramInt2 + paramInt4 < y + height ? paramInt2 + paramInt4 : y + height;
    x = i;
    y = k;
    width = (j - i);
    height = (m - k);
    if ((width < 0) || (height < 0)) {
      x = (y = width = height = 0);
    }
    return paramRectangle;
  }
  
  public static Rectangle computeUnion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle)
  {
    int i = paramInt1 < x ? paramInt1 : x;
    int j = paramInt1 + paramInt3 > x + width ? paramInt1 + paramInt3 : x + width;
    int k = paramInt2 < y ? paramInt2 : y;
    int m = paramInt2 + paramInt4 > y + height ? paramInt2 + paramInt4 : y + height;
    x = i;
    y = k;
    width = (j - i);
    height = (m - k);
    return paramRectangle;
  }
  
  public static Rectangle[] computeDifference(Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    if ((paramRectangle2 == null) || (!paramRectangle1.intersects(paramRectangle2)) || (isRectangleContainingRectangle(paramRectangle2, paramRectangle1))) {
      return new Rectangle[0];
    }
    Rectangle localRectangle1 = new Rectangle();
    Rectangle localRectangle2 = null;
    Rectangle localRectangle3 = null;
    Rectangle localRectangle4 = null;
    Rectangle localRectangle5 = null;
    int i = 0;
    if (isRectangleContainingRectangle(paramRectangle1, paramRectangle2))
    {
      x = x;
      y = y;
      width = (x - x);
      height = height;
      if ((width > 0) && (height > 0))
      {
        localRectangle2 = new Rectangle(localRectangle1);
        i++;
      }
      x = x;
      y = y;
      width = width;
      height = (y - y);
      if ((width > 0) && (height > 0))
      {
        localRectangle3 = new Rectangle(localRectangle1);
        i++;
      }
      x = x;
      y += height;
      width = width;
      height = (y + height - (y + height));
      if ((width > 0) && (height > 0))
      {
        localRectangle4 = new Rectangle(localRectangle1);
        i++;
      }
      x += width;
      y = y;
      width = (x + width - (x + width));
      height = height;
      if ((width > 0) && (height > 0))
      {
        localRectangle5 = new Rectangle(localRectangle1);
        i++;
      }
    }
    else if ((x <= x) && (y <= y))
    {
      if (x + width > x + width)
      {
        x = x;
        y += height;
        width = width;
        height = (y + height - (y + height));
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = localRectangle1;
          i++;
        }
      }
      else if (y + height > y + height)
      {
        localRectangle1.setBounds(x + width, y, x + width - (x + width), height);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = localRectangle1;
          i++;
        }
      }
      else
      {
        localRectangle1.setBounds(x + width, y, x + width - (x + width), y + height - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y + height, width, y + height - (y + height));
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
      }
    }
    else if ((x <= x) && (y + height >= y + height))
    {
      if (x + width > x + width)
      {
        localRectangle1.setBounds(x, y, width, y - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = localRectangle1;
          i++;
        }
      }
      else
      {
        localRectangle1.setBounds(x, y, width, y - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x + width, y, x + width - (x + width), y + height - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
      }
    }
    else if (x <= x)
    {
      if (x + width >= x + width)
      {
        localRectangle1.setBounds(x, y, width, y - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y + height, width, y + height - (y + height));
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
      }
      else
      {
        localRectangle1.setBounds(x, y, width, y - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x + width, y, x + width - (x + width), height);
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y + height, width, y + height - (y + height));
        if ((width > 0) && (height > 0))
        {
          localRectangle4 = new Rectangle(localRectangle1);
          i++;
        }
      }
    }
    else if ((x <= x + width) && (x + width > x + width))
    {
      if ((y <= y) && (y + height > y + height))
      {
        localRectangle1.setBounds(x, y, x - x, height);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = localRectangle1;
          i++;
        }
      }
      else if (y <= y)
      {
        localRectangle1.setBounds(x, y, x - x, y + height - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y + height, width, y + height - (y + height));
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
      }
      else if (y + height > y + height)
      {
        localRectangle1.setBounds(x, y, width, y - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y, x - x, y + height - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
      }
      else
      {
        localRectangle1.setBounds(x, y, width, y - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y, x - x, height);
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y + height, width, y + height - (y + height));
        if ((width > 0) && (height > 0))
        {
          localRectangle4 = new Rectangle(localRectangle1);
          i++;
        }
      }
    }
    else if ((x >= x) && (x + width <= x + width))
    {
      if ((y <= y) && (y + height > y + height))
      {
        localRectangle1.setBounds(x, y, x - x, height);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x + width, y, x + width - (x + width), height);
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
      }
      else if (y <= y)
      {
        localRectangle1.setBounds(x, y, x - x, height);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y + height, width, y + height - (y + height));
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x + width, y, x + width - (x + width), height);
        if ((width > 0) && (height > 0))
        {
          localRectangle4 = new Rectangle(localRectangle1);
          i++;
        }
      }
      else
      {
        localRectangle1.setBounds(x, y, x - x, height);
        if ((width > 0) && (height > 0))
        {
          localRectangle2 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x, y, width, y - y);
        if ((width > 0) && (height > 0))
        {
          localRectangle3 = new Rectangle(localRectangle1);
          i++;
        }
        localRectangle1.setBounds(x + width, y, x + width - (x + width), height);
        if ((width > 0) && (height > 0))
        {
          localRectangle4 = new Rectangle(localRectangle1);
          i++;
        }
      }
    }
    Rectangle[] arrayOfRectangle = new Rectangle[i];
    i = 0;
    if (localRectangle2 != null) {
      arrayOfRectangle[(i++)] = localRectangle2;
    }
    if (localRectangle3 != null) {
      arrayOfRectangle[(i++)] = localRectangle3;
    }
    if (localRectangle4 != null) {
      arrayOfRectangle[(i++)] = localRectangle4;
    }
    if (localRectangle5 != null) {
      arrayOfRectangle[(i++)] = localRectangle5;
    }
    return arrayOfRectangle;
  }
  
  public static boolean isLeftMouseButton(MouseEvent paramMouseEvent)
  {
    return ((paramMouseEvent.getModifiersEx() & 0x400) != 0) || (paramMouseEvent.getButton() == 1);
  }
  
  public static boolean isMiddleMouseButton(MouseEvent paramMouseEvent)
  {
    return ((paramMouseEvent.getModifiersEx() & 0x800) != 0) || (paramMouseEvent.getButton() == 2);
  }
  
  public static boolean isRightMouseButton(MouseEvent paramMouseEvent)
  {
    return ((paramMouseEvent.getModifiersEx() & 0x1000) != 0) || (paramMouseEvent.getButton() == 3);
  }
  
  public static int computeStringWidth(FontMetrics paramFontMetrics, String paramString)
  {
    return SwingUtilities2.stringWidth(null, paramFontMetrics, paramString);
  }
  
  public static String layoutCompoundLabel(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5)
  {
    int i = 1;
    int j = paramInt2;
    int k = paramInt4;
    if ((paramJComponent != null) && (!paramJComponent.getComponentOrientation().isLeftToRight())) {
      i = 0;
    }
    switch (paramInt2)
    {
    case 10: 
      j = i != 0 ? 2 : 4;
      break;
    case 11: 
      j = i != 0 ? 4 : 2;
    }
    switch (paramInt4)
    {
    case 10: 
      k = i != 0 ? 2 : 4;
      break;
    case 11: 
      k = i != 0 ? 4 : 2;
    }
    return layoutCompoundLabelImpl(paramJComponent, paramFontMetrics, paramString, paramIcon, paramInt1, j, paramInt3, k, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
  }
  
  public static String layoutCompoundLabel(FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5)
  {
    return layoutCompoundLabelImpl(null, paramFontMetrics, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
  }
  
  private static String layoutCompoundLabelImpl(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5)
  {
    if (paramIcon != null)
    {
      width = paramIcon.getIconWidth();
      height = paramIcon.getIconHeight();
    }
    else
    {
      width = (height = 0);
    }
    int i = (paramString == null) || (paramString.equals("")) ? 1 : 0;
    int j = 0;
    int k = 0;
    int m;
    if (i != 0)
    {
      width = (height = 0);
      paramString = "";
      m = 0;
    }
    else
    {
      m = paramIcon == null ? 0 : paramInt5;
      if (paramInt4 == 0) {
        n = width;
      } else {
        n = width - (width + m);
      }
      Object localObject = paramJComponent != null ? (View)paramJComponent.getClientProperty("html") : null;
      if (localObject != null)
      {
        width = Math.min(n, (int)((View)localObject).getPreferredSpan(0));
        height = ((int)((View)localObject).getPreferredSpan(1));
      }
      else
      {
        width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString);
        j = SwingUtilities2.getLeftSideBearing(paramJComponent, paramFontMetrics, paramString);
        if (j < 0) {
          width -= j;
        }
        if (width > n)
        {
          paramString = SwingUtilities2.clipString(paramJComponent, paramFontMetrics, paramString, n);
          width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString);
        }
        height = paramFontMetrics.getHeight();
      }
    }
    if (paramInt3 == 1)
    {
      if (paramInt4 != 0) {
        y = 0;
      } else {
        y = (-(height + m));
      }
    }
    else if (paramInt3 == 0) {
      y = (height / 2 - height / 2);
    } else if (paramInt4 != 0) {
      y = (height - height);
    } else {
      y = (height + m);
    }
    if (paramInt4 == 2) {
      x = (-(width + m));
    } else if (paramInt4 == 0) {
      x = (width / 2 - width / 2);
    } else {
      x = (width + m);
    }
    int n = Math.min(x, x);
    int i1 = Math.max(x + width, x + width) - n;
    int i2 = Math.min(y, y);
    int i3 = Math.max(y + height, y + height) - i2;
    int i5;
    if (paramInt1 == 1) {
      i5 = y - i2;
    } else if (paramInt1 == 0) {
      i5 = y + height / 2 - (i2 + i3 / 2);
    } else {
      i5 = y + height - (i2 + i3);
    }
    int i4;
    if (paramInt2 == 2) {
      i4 = x - n;
    } else if (paramInt2 == 4) {
      i4 = x + width - (n + i1);
    } else {
      i4 = x + width / 2 - (n + i1 / 2);
    }
    x += i4;
    y += i5;
    x += i4;
    y += i5;
    if (j < 0)
    {
      x -= j;
      width += j;
    }
    if (k > 0) {
      width -= k;
    }
    return paramString;
  }
  
  public static void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    getCellRendererPane(paramComponent, paramContainer).paintComponent(paramGraphics, paramComponent, paramContainer, paramInt1, paramInt2, paramInt3, paramInt4, false);
  }
  
  public static void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, Rectangle paramRectangle)
  {
    paintComponent(paramGraphics, paramComponent, paramContainer, x, y, width, height);
  }
  
  private static CellRendererPane getCellRendererPane(Component paramComponent, Container paramContainer)
  {
    Object localObject = paramComponent.getParent();
    if ((localObject instanceof CellRendererPane))
    {
      if (((Container)localObject).getParent() != paramContainer) {
        paramContainer.add((Component)localObject);
      }
    }
    else
    {
      localObject = new CellRendererPane();
      ((Container)localObject).add(paramComponent);
      paramContainer.add((Component)localObject);
    }
    return (CellRendererPane)localObject;
  }
  
  public static void updateComponentTreeUI(Component paramComponent)
  {
    updateComponentTreeUI0(paramComponent);
    paramComponent.invalidate();
    paramComponent.validate();
    paramComponent.repaint();
  }
  
  private static void updateComponentTreeUI0(Component paramComponent)
  {
    Object localObject2;
    if ((paramComponent instanceof JComponent))
    {
      localObject1 = (JComponent)paramComponent;
      ((JComponent)localObject1).updateUI();
      localObject2 = ((JComponent)localObject1).getComponentPopupMenu();
      if (localObject2 != null) {
        updateComponentTreeUI((Component)localObject2);
      }
    }
    Object localObject1 = null;
    if ((paramComponent instanceof JMenu)) {
      localObject1 = ((JMenu)paramComponent).getMenuComponents();
    } else if ((paramComponent instanceof Container)) {
      localObject1 = ((Container)paramComponent).getComponents();
    }
    if (localObject1 != null) {
      for (Component localComponent : localObject1) {
        updateComponentTreeUI0(localComponent);
      }
    }
  }
  
  public static void invokeLater(Runnable paramRunnable)
  {
    EventQueue.invokeLater(paramRunnable);
  }
  
  public static void invokeAndWait(Runnable paramRunnable)
    throws InterruptedException, InvocationTargetException
  {
    EventQueue.invokeAndWait(paramRunnable);
  }
  
  public static boolean isEventDispatchThread()
  {
    return EventQueue.isDispatchThread();
  }
  
  public static int getAccessibleIndexInParent(Component paramComponent)
  {
    return paramComponent.getAccessibleContext().getAccessibleIndexInParent();
  }
  
  public static Accessible getAccessibleAt(Component paramComponent, Point paramPoint)
  {
    if ((paramComponent instanceof Container)) {
      return paramComponent.getAccessibleContext().getAccessibleComponent().getAccessibleAt(paramPoint);
    }
    if ((paramComponent instanceof Accessible))
    {
      Accessible localAccessible = (Accessible)paramComponent;
      if (localAccessible != null)
      {
        AccessibleContext localAccessibleContext = localAccessible.getAccessibleContext();
        if (localAccessibleContext != null)
        {
          int i = localAccessibleContext.getAccessibleChildrenCount();
          for (int j = 0; j < i; j++)
          {
            localAccessible = localAccessibleContext.getAccessibleChild(j);
            if (localAccessible != null)
            {
              localAccessibleContext = localAccessible.getAccessibleContext();
              if (localAccessibleContext != null)
              {
                AccessibleComponent localAccessibleComponent = localAccessibleContext.getAccessibleComponent();
                if ((localAccessibleComponent != null) && (localAccessibleComponent.isShowing()))
                {
                  Point localPoint1 = localAccessibleComponent.getLocation();
                  Point localPoint2 = new Point(x - x, y - y);
                  if (localAccessibleComponent.contains(localPoint2)) {
                    return localAccessible;
                  }
                }
              }
            }
          }
        }
      }
      return (Accessible)paramComponent;
    }
    return null;
  }
  
  public static AccessibleStateSet getAccessibleStateSet(Component paramComponent)
  {
    return paramComponent.getAccessibleContext().getAccessibleStateSet();
  }
  
  public static int getAccessibleChildrenCount(Component paramComponent)
  {
    return paramComponent.getAccessibleContext().getAccessibleChildrenCount();
  }
  
  public static Accessible getAccessibleChild(Component paramComponent, int paramInt)
  {
    return paramComponent.getAccessibleContext().getAccessibleChild(paramInt);
  }
  
  @Deprecated
  public static Component findFocusOwner(Component paramComponent)
  {
    Component localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    for (Object localObject = localComponent; localObject != null; localObject = (localObject instanceof Window) ? null : ((Component)localObject).getParent()) {
      if (localObject == paramComponent) {
        return localComponent;
      }
    }
    return null;
  }
  
  public static JRootPane getRootPane(Component paramComponent)
  {
    if ((paramComponent instanceof RootPaneContainer)) {
      return ((RootPaneContainer)paramComponent).getRootPane();
    }
    while (paramComponent != null)
    {
      if ((paramComponent instanceof JRootPane)) {
        return (JRootPane)paramComponent;
      }
      paramComponent = paramComponent.getParent();
    }
    return null;
  }
  
  public static Component getRoot(Component paramComponent)
  {
    Object localObject1 = null;
    for (Object localObject2 = paramComponent; localObject2 != null; localObject2 = ((Component)localObject2).getParent())
    {
      if ((localObject2 instanceof Window)) {
        return (Component)localObject2;
      }
      if ((localObject2 instanceof Applet)) {
        localObject1 = localObject2;
      }
    }
    return (Component)localObject1;
  }
  
  static JComponent getPaintingOrigin(JComponent paramJComponent)
  {
    Object localObject = paramJComponent;
    while (((localObject = ((Container)localObject).getParent()) instanceof JComponent))
    {
      JComponent localJComponent = (JComponent)localObject;
      if (localJComponent.isPaintingOrigin()) {
        return localJComponent;
      }
    }
    return null;
  }
  
  public static boolean processKeyBindings(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent != null)
    {
      if (paramKeyEvent.isConsumed()) {
        return false;
      }
      Object localObject = paramKeyEvent.getComponent();
      boolean bool = paramKeyEvent.getID() == 401;
      if (!isValidKeyEventForKeyBindings(paramKeyEvent)) {
        return false;
      }
      while (localObject != null)
      {
        if ((localObject instanceof JComponent)) {
          return ((JComponent)localObject).processKeyBindings(paramKeyEvent, bool);
        }
        if (((localObject instanceof Applet)) || ((localObject instanceof Window))) {
          return JComponent.processKeyBindingsForAllComponents(paramKeyEvent, (Container)localObject, bool);
        }
        localObject = ((Component)localObject).getParent();
      }
    }
    return false;
  }
  
  static boolean isValidKeyEventForKeyBindings(KeyEvent paramKeyEvent)
  {
    return true;
  }
  
  public static boolean notifyAction(Action paramAction, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, Object paramObject, int paramInt)
  {
    if (paramAction == null) {
      return false;
    }
    if ((paramAction instanceof UIAction))
    {
      if (!((UIAction)paramAction).isEnabled(paramObject)) {
        return false;
      }
    }
    else if (!paramAction.isEnabled()) {
      return false;
    }
    Object localObject = paramAction.getValue("ActionCommandKey");
    int i;
    if ((localObject == null) && ((paramAction instanceof JComponent.ActionStandin))) {
      i = 1;
    } else {
      i = 0;
    }
    String str;
    if (localObject != null) {
      str = localObject.toString();
    } else if ((i == 0) && (paramKeyEvent.getKeyChar() != 65535)) {
      str = String.valueOf(paramKeyEvent.getKeyChar());
    } else {
      str = null;
    }
    paramAction.actionPerformed(new ActionEvent(paramObject, 1001, str, paramKeyEvent.getWhen(), paramInt));
    return true;
  }
  
  public static void replaceUIInputMap(JComponent paramJComponent, int paramInt, InputMap paramInputMap)
  {
    InputMap localInputMap;
    for (Object localObject = paramJComponent.getInputMap(paramInt, paramInputMap != null); localObject != null; localObject = localInputMap)
    {
      localInputMap = ((InputMap)localObject).getParent();
      if ((localInputMap == null) || ((localInputMap instanceof UIResource)))
      {
        ((InputMap)localObject).setParent(paramInputMap);
        return;
      }
    }
  }
  
  public static void replaceUIActionMap(JComponent paramJComponent, ActionMap paramActionMap)
  {
    ActionMap localActionMap;
    for (Object localObject = paramJComponent.getActionMap(paramActionMap != null); localObject != null; localObject = localActionMap)
    {
      localActionMap = ((ActionMap)localObject).getParent();
      if ((localActionMap == null) || ((localActionMap instanceof UIResource)))
      {
        ((ActionMap)localObject).setParent(paramActionMap);
        return;
      }
    }
  }
  
  public static InputMap getUIInputMap(JComponent paramJComponent, int paramInt)
  {
    InputMap localInputMap;
    for (Object localObject = paramJComponent.getInputMap(paramInt, false); localObject != null; localObject = localInputMap)
    {
      localInputMap = ((InputMap)localObject).getParent();
      if ((localInputMap instanceof UIResource)) {
        return localInputMap;
      }
    }
    return null;
  }
  
  public static ActionMap getUIActionMap(JComponent paramJComponent)
  {
    ActionMap localActionMap;
    for (Object localObject = paramJComponent.getActionMap(false); localObject != null; localObject = localActionMap)
    {
      localActionMap = ((ActionMap)localObject).getParent();
      if ((localActionMap instanceof UIResource)) {
        return localActionMap;
      }
    }
    return null;
  }
  
  static Frame getSharedOwnerFrame()
    throws HeadlessException
  {
    Object localObject = (Frame)appContextGet(sharedOwnerFrameKey);
    if (localObject == null)
    {
      localObject = new SharedOwnerFrame();
      appContextPut(sharedOwnerFrameKey, localObject);
    }
    return (Frame)localObject;
  }
  
  static WindowListener getSharedOwnerFrameShutdownListener()
    throws HeadlessException
  {
    Frame localFrame = getSharedOwnerFrame();
    return (WindowListener)localFrame;
  }
  
  static Object appContextGet(Object paramObject)
  {
    return AppContext.getAppContext().get(paramObject);
  }
  
  static void appContextPut(Object paramObject1, Object paramObject2)
  {
    AppContext.getAppContext().put(paramObject1, paramObject2);
  }
  
  static void appContextRemove(Object paramObject)
  {
    AppContext.getAppContext().remove(paramObject);
  }
  
  static Class<?> loadSystemClass(String paramString)
    throws ClassNotFoundException
  {
    ReflectUtil.checkPackageAccess(paramString);
    return Class.forName(paramString, true, Thread.currentThread().getContextClassLoader());
  }
  
  static boolean isLeftToRight(Component paramComponent)
  {
    return paramComponent.getComponentOrientation().isLeftToRight();
  }
  
  private SwingUtilities()
  {
    throw new Error("SwingUtilities is just a container for static methods");
  }
  
  static boolean doesIconReferenceImage(Icon paramIcon, Image paramImage)
  {
    Object localObject = (paramIcon != null) && ((paramIcon instanceof ImageIcon)) ? ((ImageIcon)paramIcon).getImage() : null;
    return localObject == paramImage;
  }
  
  static int findDisplayedMnemonicIndex(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramInt == 0)) {
      return -1;
    }
    int i = Character.toUpperCase((char)paramInt);
    int j = Character.toLowerCase((char)paramInt);
    int k = paramString.indexOf(i);
    int m = paramString.indexOf(j);
    if (k == -1) {
      return m;
    }
    if (m == -1) {
      return k;
    }
    return m < k ? m : k;
  }
  
  public static Rectangle calculateInnerArea(JComponent paramJComponent, Rectangle paramRectangle)
  {
    if (paramJComponent == null) {
      return null;
    }
    Rectangle localRectangle = paramRectangle;
    Insets localInsets = paramJComponent.getInsets();
    if (localRectangle == null) {
      localRectangle = new Rectangle();
    }
    x = left;
    y = top;
    width = (paramJComponent.getWidth() - left - right);
    height = (paramJComponent.getHeight() - top - bottom);
    return localRectangle;
  }
  
  static void updateRendererOrEditorUI(Object paramObject)
  {
    if (paramObject == null) {
      return;
    }
    Component localComponent = null;
    if ((paramObject instanceof Component)) {
      localComponent = (Component)paramObject;
    }
    if ((paramObject instanceof DefaultCellEditor)) {
      localComponent = ((DefaultCellEditor)paramObject).getComponent();
    }
    if (localComponent != null) {
      updateComponentTreeUI(localComponent);
    }
  }
  
  public static Container getUnwrappedParent(Component paramComponent)
  {
    for (Container localContainer = paramComponent.getParent(); (localContainer instanceof JLayer); localContainer = localContainer.getParent()) {}
    return localContainer;
  }
  
  public static Component getUnwrappedView(JViewport paramJViewport)
  {
    for (Component localComponent = paramJViewport.getView(); (localComponent instanceof JLayer); localComponent = ((JLayer)localComponent).getView()) {}
    return localComponent;
  }
  
  static Container getValidateRoot(Container paramContainer, boolean paramBoolean)
  {
    Container localContainer = null;
    while (paramContainer != null)
    {
      if ((!paramContainer.isDisplayable()) || ((paramContainer instanceof CellRendererPane))) {
        return null;
      }
      if (paramContainer.isValidateRoot())
      {
        localContainer = paramContainer;
        break;
      }
      paramContainer = paramContainer.getParent();
    }
    if (localContainer == null) {
      return null;
    }
    while (paramContainer != null)
    {
      if ((!paramContainer.isDisplayable()) || ((paramBoolean) && (!paramContainer.isVisible()))) {
        return null;
      }
      if (((paramContainer instanceof Window)) || ((paramContainer instanceof Applet))) {
        return localContainer;
      }
      paramContainer = paramContainer.getParent();
    }
    return null;
  }
  
  static class SharedOwnerFrame
    extends Frame
    implements WindowListener
  {
    SharedOwnerFrame() {}
    
    public void addNotify()
    {
      super.addNotify();
      installListeners();
    }
    
    void installListeners()
    {
      Window[] arrayOfWindow1 = getOwnedWindows();
      for (Window localWindow : arrayOfWindow1) {
        if (localWindow != null)
        {
          localWindow.removeWindowListener(this);
          localWindow.addWindowListener(this);
        }
      }
    }
    
    public void windowClosed(WindowEvent paramWindowEvent)
    {
      synchronized (getTreeLock())
      {
        Window[] arrayOfWindow1 = getOwnedWindows();
        for (Window localWindow : arrayOfWindow1) {
          if (localWindow != null)
          {
            if (localWindow.isDisplayable()) {
              return;
            }
            localWindow.removeWindowListener(this);
          }
        }
        dispose();
      }
    }
    
    public void windowOpened(WindowEvent paramWindowEvent) {}
    
    public void windowClosing(WindowEvent paramWindowEvent) {}
    
    public void windowIconified(WindowEvent paramWindowEvent) {}
    
    public void windowDeiconified(WindowEvent paramWindowEvent) {}
    
    public void windowActivated(WindowEvent paramWindowEvent) {}
    
    public void windowDeactivated(WindowEvent paramWindowEvent) {}
    
    public void show() {}
    
    public void dispose()
    {
      try
      {
        getToolkit().getSystemEventQueue();
        super.dispose();
      }
      catch (Exception localException) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SwingUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */