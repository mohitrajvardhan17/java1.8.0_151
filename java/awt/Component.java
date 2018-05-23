package java.awt;

import java.applet.Applet;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.im.InputMethodRequests;
import java.awt.image.BufferStrategy;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.MouseInfoPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.ConstrainableGraphics;
import sun.awt.EmbeddedFrame;
import sun.awt.EventQueueItem;
import sun.awt.RequestFocusController;
import sun.awt.SubRegionShowable;
import sun.awt.SunToolkit;
import sun.awt.WindowClosingListener;
import sun.awt.dnd.SunDropTargetEvent;
import sun.awt.im.CompositionArea;
import sun.awt.image.VSyncedBSManager;
import sun.font.FontDesignMetrics;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.SunFontManager;
import sun.java2d.SunGraphics2D;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities.VSyncType;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class Component
  implements ImageObserver, MenuContainer, Serializable
{
  private static final PlatformLogger log;
  private static final PlatformLogger eventLog;
  private static final PlatformLogger focusLog;
  private static final PlatformLogger mixingLog;
  transient ComponentPeer peer;
  transient Container parent;
  transient AppContext appContext = AppContext.getAppContext();
  int x;
  int y;
  int width;
  int height;
  Color foreground;
  Color background;
  volatile Font font;
  Font peerFont;
  Cursor cursor;
  Locale locale;
  private volatile transient GraphicsConfiguration graphicsConfig;
  transient BufferStrategy bufferStrategy = null;
  boolean ignoreRepaint = false;
  boolean visible = true;
  boolean enabled = true;
  private volatile boolean valid = false;
  DropTarget dropTarget;
  Vector<PopupMenu> popups;
  private String name;
  private boolean nameExplicitlySet = false;
  private boolean focusable = true;
  private static final int FOCUS_TRAVERSABLE_UNKNOWN = 0;
  private static final int FOCUS_TRAVERSABLE_DEFAULT = 1;
  private static final int FOCUS_TRAVERSABLE_SET = 2;
  private int isFocusTraversableOverridden = 0;
  Set<AWTKeyStroke>[] focusTraversalKeys;
  private static final String[] focusTraversalKeyPropertyNames;
  private boolean focusTraversalKeysEnabled = true;
  static final Object LOCK;
  private volatile transient AccessControlContext acc = AccessController.getContext();
  Dimension minSize;
  boolean minSizeSet;
  Dimension prefSize;
  boolean prefSizeSet;
  Dimension maxSize;
  boolean maxSizeSet;
  transient ComponentOrientation componentOrientation = ComponentOrientation.UNKNOWN;
  boolean newEventsOnly = false;
  transient ComponentListener componentListener;
  transient FocusListener focusListener;
  transient HierarchyListener hierarchyListener;
  transient HierarchyBoundsListener hierarchyBoundsListener;
  transient KeyListener keyListener;
  transient MouseListener mouseListener;
  transient MouseMotionListener mouseMotionListener;
  transient MouseWheelListener mouseWheelListener;
  transient InputMethodListener inputMethodListener;
  transient RuntimeException windowClosingException = null;
  static final String actionListenerK = "actionL";
  static final String adjustmentListenerK = "adjustmentL";
  static final String componentListenerK = "componentL";
  static final String containerListenerK = "containerL";
  static final String focusListenerK = "focusL";
  static final String itemListenerK = "itemL";
  static final String keyListenerK = "keyL";
  static final String mouseListenerK = "mouseL";
  static final String mouseMotionListenerK = "mouseMotionL";
  static final String mouseWheelListenerK = "mouseWheelL";
  static final String textListenerK = "textL";
  static final String ownedWindowK = "ownedL";
  static final String windowListenerK = "windowL";
  static final String inputMethodListenerK = "inputMethodL";
  static final String hierarchyListenerK = "hierarchyL";
  static final String hierarchyBoundsListenerK = "hierarchyBoundsL";
  static final String windowStateListenerK = "windowStateL";
  static final String windowFocusListenerK = "windowFocusL";
  long eventMask = 4096L;
  static boolean isInc;
  static int incRate;
  public static final float TOP_ALIGNMENT = 0.0F;
  public static final float CENTER_ALIGNMENT = 0.5F;
  public static final float BOTTOM_ALIGNMENT = 1.0F;
  public static final float LEFT_ALIGNMENT = 0.0F;
  public static final float RIGHT_ALIGNMENT = 1.0F;
  private static final long serialVersionUID = -7644114512714619750L;
  private PropertyChangeSupport changeSupport;
  private transient Object objectLock = new Object();
  boolean isPacked = false;
  private int boundsOp = 3;
  private transient Region compoundShape = null;
  private transient Region mixingCutoutRegion = null;
  private transient boolean isAddNotifyComplete = false;
  transient boolean backgroundEraseDisabled;
  transient EventQueueItem[] eventCache;
  private transient boolean coalescingEnabled = checkCoalescing();
  private static final Map<Class<?>, Boolean> coalesceMap = new WeakHashMap();
  private static final Class[] coalesceEventsParams = { AWTEvent.class, AWTEvent.class };
  private static RequestFocusController requestFocusController = new DummyRequestFocusController(null);
  private boolean autoFocusTransferOnDisposal = true;
  private int componentSerializedDataVersion = 4;
  protected AccessibleContext accessibleContext = null;
  
  Object getObjectLock()
  {
    return objectLock;
  }
  
  final AccessControlContext getAccessControlContext()
  {
    if (acc == null) {
      throw new SecurityException("Component is missing AccessControlContext");
    }
    return acc;
  }
  
  int getBoundsOp()
  {
    assert (Thread.holdsLock(getTreeLock()));
    return boundsOp;
  }
  
  void setBoundsOp(int paramInt)
  {
    assert (Thread.holdsLock(getTreeLock()));
    if (paramInt == 5) {
      boundsOp = 3;
    } else if (boundsOp == 3) {
      boundsOp = paramInt;
    }
  }
  
  protected Component() {}
  
  void initializeFocusTraversalKeys()
  {
    focusTraversalKeys = new Set[3];
  }
  
  String constructComponentName()
  {
    return null;
  }
  
  public String getName()
  {
    if ((name == null) && (!nameExplicitlySet)) {
      synchronized (getObjectLock())
      {
        if ((name == null) && (!nameExplicitlySet)) {
          name = constructComponentName();
        }
      }
    }
    return name;
  }
  
  public void setName(String paramString)
  {
    String str;
    synchronized (getObjectLock())
    {
      str = name;
      name = paramString;
      nameExplicitlySet = true;
    }
    firePropertyChange("name", str, paramString);
  }
  
  public Container getParent()
  {
    return getParent_NoClientCode();
  }
  
  final Container getParent_NoClientCode()
  {
    return parent;
  }
  
  Container getContainer()
  {
    return getParent_NoClientCode();
  }
  
  @Deprecated
  public ComponentPeer getPeer()
  {
    return peer;
  }
  
  public synchronized void setDropTarget(DropTarget paramDropTarget)
  {
    if ((paramDropTarget == dropTarget) || ((dropTarget != null) && (dropTarget.equals(paramDropTarget)))) {
      return;
    }
    DropTarget localDropTarget1;
    if ((localDropTarget1 = dropTarget) != null)
    {
      if (peer != null) {
        dropTarget.removeNotify(peer);
      }
      DropTarget localDropTarget2 = dropTarget;
      dropTarget = null;
      try
      {
        localDropTarget2.setComponent(null);
      }
      catch (IllegalArgumentException localIllegalArgumentException2) {}
    }
    if ((dropTarget = paramDropTarget) != null) {
      try
      {
        dropTarget.setComponent(this);
        if (peer != null) {
          dropTarget.addNotify(peer);
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException1)
      {
        if (localDropTarget1 != null) {
          try
          {
            localDropTarget1.setComponent(this);
            if (peer != null) {
              dropTarget.addNotify(peer);
            }
          }
          catch (IllegalArgumentException localIllegalArgumentException3) {}
        }
      }
    }
  }
  
  public synchronized DropTarget getDropTarget()
  {
    return dropTarget;
  }
  
  public GraphicsConfiguration getGraphicsConfiguration()
  {
    return getGraphicsConfiguration_NoClientCode();
  }
  
  final GraphicsConfiguration getGraphicsConfiguration_NoClientCode()
  {
    return graphicsConfig;
  }
  
  void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
  {
    synchronized (getTreeLock())
    {
      if (updateGraphicsData(paramGraphicsConfiguration))
      {
        removeNotify();
        addNotify();
      }
    }
  }
  
  boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration)
  {
    checkTreeLock();
    if (graphicsConfig == paramGraphicsConfiguration) {
      return false;
    }
    graphicsConfig = paramGraphicsConfiguration;
    ComponentPeer localComponentPeer = getPeer();
    if (localComponentPeer != null) {
      return localComponentPeer.updateGraphicsData(paramGraphicsConfiguration);
    }
    return false;
  }
  
  void checkGD(String paramString)
  {
    if ((graphicsConfig != null) && (!graphicsConfig.getDevice().getIDstring().equals(paramString))) {
      throw new IllegalArgumentException("adding a container to a container on a different GraphicsDevice");
    }
  }
  
  public final Object getTreeLock()
  {
    return LOCK;
  }
  
  final void checkTreeLock()
  {
    if (!Thread.holdsLock(getTreeLock())) {
      throw new IllegalStateException("This function should be called while holding treeLock");
    }
  }
  
  public Toolkit getToolkit()
  {
    return getToolkitImpl();
  }
  
  final Toolkit getToolkitImpl()
  {
    Container localContainer = parent;
    if (localContainer != null) {
      return localContainer.getToolkitImpl();
    }
    return Toolkit.getDefaultToolkit();
  }
  
  public boolean isValid()
  {
    return (peer != null) && (valid);
  }
  
  public boolean isDisplayable()
  {
    return getPeer() != null;
  }
  
  @Transient
  public boolean isVisible()
  {
    return isVisible_NoClientCode();
  }
  
  final boolean isVisible_NoClientCode()
  {
    return visible;
  }
  
  boolean isRecursivelyVisible()
  {
    return (visible) && ((parent == null) || (parent.isRecursivelyVisible()));
  }
  
  private Rectangle getRecursivelyVisibleBounds()
  {
    Container localContainer = getContainer();
    Rectangle localRectangle1 = getBounds();
    if (localContainer == null) {
      return localRectangle1;
    }
    Rectangle localRectangle2 = localContainer.getRecursivelyVisibleBounds();
    localRectangle2.setLocation(0, 0);
    return localRectangle2.intersection(localRectangle1);
  }
  
  Point pointRelativeToComponent(Point paramPoint)
  {
    Point localPoint = getLocationOnScreen();
    return new Point(x - x, y - y);
  }
  
  Component findUnderMouseInWindow(PointerInfo paramPointerInfo)
  {
    if (!isShowing()) {
      return null;
    }
    Window localWindow = getContainingWindow();
    if (!Toolkit.getDefaultToolkit().getMouseInfoPeer().isWindowUnderMouse(localWindow)) {
      return null;
    }
    Point localPoint = localWindow.pointRelativeToComponent(paramPointerInfo.getLocation());
    Component localComponent = localWindow.findComponentAt(x, y, true);
    return localComponent;
  }
  
  public Point getMousePosition()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    PointerInfo localPointerInfo = (PointerInfo)AccessController.doPrivileged(new PrivilegedAction()
    {
      public PointerInfo run()
      {
        return MouseInfo.getPointerInfo();
      }
    });
    synchronized (getTreeLock())
    {
      Component localComponent = findUnderMouseInWindow(localPointerInfo);
      if (!isSameOrAncestorOf(localComponent, true)) {
        return null;
      }
      return pointRelativeToComponent(localPointerInfo.getLocation());
    }
  }
  
  boolean isSameOrAncestorOf(Component paramComponent, boolean paramBoolean)
  {
    return paramComponent == this;
  }
  
  public boolean isShowing()
  {
    if ((visible) && (peer != null))
    {
      Container localContainer = parent;
      return (localContainer == null) || (localContainer.isShowing());
    }
    return false;
  }
  
  public boolean isEnabled()
  {
    return isEnabledImpl();
  }
  
  final boolean isEnabledImpl()
  {
    return enabled;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    enable(paramBoolean);
  }
  
  @Deprecated
  public void enable()
  {
    if (!enabled)
    {
      synchronized (getTreeLock())
      {
        enabled = true;
        ComponentPeer localComponentPeer = peer;
        if (localComponentPeer != null)
        {
          localComponentPeer.setEnabled(true);
          if ((visible) && (!getRecursivelyVisibleBounds().isEmpty())) {
            updateCursorImmediately();
          }
        }
      }
      if (accessibleContext != null) {
        accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
      }
    }
  }
  
  @Deprecated
  public void enable(boolean paramBoolean)
  {
    if (paramBoolean) {
      enable();
    } else {
      disable();
    }
  }
  
  @Deprecated
  public void disable()
  {
    if (enabled)
    {
      KeyboardFocusManager.clearMostRecentFocusOwner(this);
      synchronized (getTreeLock())
      {
        enabled = false;
        if (((isFocusOwner()) || ((containsFocus()) && (!isLightweight()))) && (KeyboardFocusManager.isAutoFocusTransferEnabled())) {
          transferFocus(false);
        }
        ComponentPeer localComponentPeer = peer;
        if (localComponentPeer != null)
        {
          localComponentPeer.setEnabled(false);
          if ((visible) && (!getRecursivelyVisibleBounds().isEmpty())) {
            updateCursorImmediately();
          }
        }
      }
      if (accessibleContext != null) {
        accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
      }
    }
  }
  
  public boolean isDoubleBuffered()
  {
    return false;
  }
  
  public void enableInputMethods(boolean paramBoolean)
  {
    java.awt.im.InputContext localInputContext;
    if (paramBoolean)
    {
      if ((eventMask & 0x1000) != 0L) {
        return;
      }
      if (isFocusOwner())
      {
        localInputContext = getInputContext();
        if (localInputContext != null)
        {
          FocusEvent localFocusEvent = new FocusEvent(this, 1004);
          localInputContext.dispatchEvent(localFocusEvent);
        }
      }
      eventMask |= 0x1000;
    }
    else
    {
      if ((eventMask & 0x1000) != 0L)
      {
        localInputContext = getInputContext();
        if (localInputContext != null)
        {
          localInputContext.endComposition();
          localInputContext.removeNotify(this);
        }
      }
      eventMask &= 0xFFFFFFFFFFFFEFFF;
    }
  }
  
  public void setVisible(boolean paramBoolean)
  {
    show(paramBoolean);
  }
  
  @Deprecated
  public void show()
  {
    if (!visible)
    {
      synchronized (getTreeLock())
      {
        visible = true;
        mixOnShowing();
        ComponentPeer localComponentPeer = peer;
        if (localComponentPeer != null)
        {
          localComponentPeer.setVisible(true);
          createHierarchyEvents(1400, this, parent, 4L, Toolkit.enabledOnToolkit(32768L));
          if ((localComponentPeer instanceof LightweightPeer)) {
            repaint();
          }
          updateCursorImmediately();
        }
        if ((componentListener != null) || ((eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
        {
          ComponentEvent localComponentEvent = new ComponentEvent(this, 102);
          Toolkit.getEventQueue().postEvent(localComponentEvent);
        }
      }
      ??? = parent;
      if (??? != null) {
        ((Container)???).invalidate();
      }
    }
  }
  
  @Deprecated
  public void show(boolean paramBoolean)
  {
    if (paramBoolean) {
      show();
    } else {
      hide();
    }
  }
  
  boolean containsFocus()
  {
    return isFocusOwner();
  }
  
  void clearMostRecentFocusOwnerOnHide()
  {
    KeyboardFocusManager.clearMostRecentFocusOwner(this);
  }
  
  void clearCurrentFocusCycleRootOnHide() {}
  
  @Deprecated
  public void hide()
  {
    isPacked = false;
    if (visible)
    {
      clearCurrentFocusCycleRootOnHide();
      clearMostRecentFocusOwnerOnHide();
      synchronized (getTreeLock())
      {
        visible = false;
        mixOnHiding(isLightweight());
        if ((containsFocus()) && (KeyboardFocusManager.isAutoFocusTransferEnabled())) {
          transferFocus(true);
        }
        ComponentPeer localComponentPeer = peer;
        if (localComponentPeer != null)
        {
          localComponentPeer.setVisible(false);
          createHierarchyEvents(1400, this, parent, 4L, Toolkit.enabledOnToolkit(32768L));
          if ((localComponentPeer instanceof LightweightPeer)) {
            repaint();
          }
          updateCursorImmediately();
        }
        if ((componentListener != null) || ((eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
        {
          ComponentEvent localComponentEvent = new ComponentEvent(this, 103);
          Toolkit.getEventQueue().postEvent(localComponentEvent);
        }
      }
      ??? = parent;
      if (??? != null) {
        ((Container)???).invalidate();
      }
    }
  }
  
  @Transient
  public Color getForeground()
  {
    Color localColor = foreground;
    if (localColor != null) {
      return localColor;
    }
    Container localContainer = parent;
    return localContainer != null ? localContainer.getForeground() : null;
  }
  
  public void setForeground(Color paramColor)
  {
    Color localColor = foreground;
    ComponentPeer localComponentPeer = peer;
    foreground = paramColor;
    if (localComponentPeer != null)
    {
      paramColor = getForeground();
      if (paramColor != null) {
        localComponentPeer.setForeground(paramColor);
      }
    }
    firePropertyChange("foreground", localColor, paramColor);
  }
  
  public boolean isForegroundSet()
  {
    return foreground != null;
  }
  
  @Transient
  public Color getBackground()
  {
    Color localColor = background;
    if (localColor != null) {
      return localColor;
    }
    Container localContainer = parent;
    return localContainer != null ? localContainer.getBackground() : null;
  }
  
  public void setBackground(Color paramColor)
  {
    Color localColor = background;
    ComponentPeer localComponentPeer = peer;
    background = paramColor;
    if (localComponentPeer != null)
    {
      paramColor = getBackground();
      if (paramColor != null) {
        localComponentPeer.setBackground(paramColor);
      }
    }
    firePropertyChange("background", localColor, paramColor);
  }
  
  public boolean isBackgroundSet()
  {
    return background != null;
  }
  
  @Transient
  public Font getFont()
  {
    return getFont_NoClientCode();
  }
  
  final Font getFont_NoClientCode()
  {
    Font localFont = font;
    if (localFont != null) {
      return localFont;
    }
    Container localContainer = parent;
    return localContainer != null ? localContainer.getFont_NoClientCode() : null;
  }
  
  public void setFont(Font paramFont)
  {
    Font localFont1;
    Font localFont2;
    synchronized (getTreeLock())
    {
      localFont1 = font;
      localFont2 = font = paramFont;
      ComponentPeer localComponentPeer = peer;
      if (localComponentPeer != null)
      {
        paramFont = getFont();
        if (paramFont != null)
        {
          localComponentPeer.setFont(paramFont);
          peerFont = paramFont;
        }
      }
    }
    firePropertyChange("font", localFont1, localFont2);
    if ((paramFont != localFont1) && ((localFont1 == null) || (!localFont1.equals(paramFont)))) {
      invalidateIfValid();
    }
  }
  
  public boolean isFontSet()
  {
    return font != null;
  }
  
  public Locale getLocale()
  {
    Locale localLocale = locale;
    if (localLocale != null) {
      return localLocale;
    }
    Container localContainer = parent;
    if (localContainer == null) {
      throw new IllegalComponentStateException("This component must have a parent in order to determine its locale");
    }
    return localContainer.getLocale();
  }
  
  public void setLocale(Locale paramLocale)
  {
    Locale localLocale = locale;
    locale = paramLocale;
    firePropertyChange("locale", localLocale, paramLocale);
    invalidateIfValid();
  }
  
  public ColorModel getColorModel()
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer != null) && (!(localComponentPeer instanceof LightweightPeer))) {
      return localComponentPeer.getColorModel();
    }
    if (GraphicsEnvironment.isHeadless()) {
      return ColorModel.getRGBdefault();
    }
    return getToolkit().getColorModel();
  }
  
  public Point getLocation()
  {
    return location();
  }
  
  /* Error */
  public Point getLocationOnScreen()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 2406	java/awt/Component:getTreeLock	()Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: invokevirtual 2367	java/awt/Component:getLocationOnScreen_NoTreeLock	()Ljava/awt/Point;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Component
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  final Point getLocationOnScreen_NoTreeLock()
  {
    if ((peer != null) && (isShowing()))
    {
      if ((peer instanceof LightweightPeer))
      {
        localObject1 = getNativeContainer();
        Point localPoint = peer.getLocationOnScreen();
        for (Object localObject2 = this; localObject2 != localObject1; localObject2 = ((Component)localObject2).getParent())
        {
          x += x;
          y += y;
        }
        return localPoint;
      }
      Object localObject1 = peer.getLocationOnScreen();
      return (Point)localObject1;
    }
    throw new IllegalComponentStateException("component must be showing on the screen to determine its location");
  }
  
  @Deprecated
  public Point location()
  {
    return location_NoClientCode();
  }
  
  private Point location_NoClientCode()
  {
    return new Point(x, y);
  }
  
  public void setLocation(int paramInt1, int paramInt2)
  {
    move(paramInt1, paramInt2);
  }
  
  @Deprecated
  public void move(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      setBoundsOp(1);
      setBounds(paramInt1, paramInt2, width, height);
    }
  }
  
  public void setLocation(Point paramPoint)
  {
    setLocation(x, y);
  }
  
  public Dimension getSize()
  {
    return size();
  }
  
  @Deprecated
  public Dimension size()
  {
    return new Dimension(width, height);
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    resize(paramInt1, paramInt2);
  }
  
  @Deprecated
  public void resize(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      setBoundsOp(2);
      setBounds(x, y, paramInt1, paramInt2);
    }
  }
  
  public void setSize(Dimension paramDimension)
  {
    resize(paramDimension);
  }
  
  @Deprecated
  public void resize(Dimension paramDimension)
  {
    setSize(width, height);
  }
  
  public Rectangle getBounds()
  {
    return bounds();
  }
  
  @Deprecated
  public Rectangle bounds()
  {
    return new Rectangle(x, y, width, height);
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    reshape(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  @Deprecated
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (getTreeLock())
    {
      try
      {
        setBoundsOp(3);
        boolean bool1 = (width != paramInt3) || (height != paramInt4);
        boolean bool2 = (x != paramInt1) || (y != paramInt2);
        if ((!bool1) && (!bool2))
        {
          setBoundsOp(5);
          return;
        }
        int i = x;
        int j = y;
        int k = width;
        int m = height;
        x = paramInt1;
        y = paramInt2;
        width = paramInt3;
        height = paramInt4;
        if (bool1) {
          isPacked = false;
        }
        int n = 1;
        mixOnReshaping();
        if (peer != null)
        {
          if (!(peer instanceof LightweightPeer))
          {
            reshapeNativePeer(paramInt1, paramInt2, paramInt3, paramInt4, getBoundsOp());
            bool1 = (k != width) || (m != height);
            bool2 = (i != x) || (j != y);
            if ((this instanceof Window)) {
              n = 0;
            }
          }
          if (bool1) {
            invalidate();
          }
          if (parent != null) {
            parent.invalidateIfValid();
          }
        }
        if (n != 0) {
          notifyNewBounds(bool1, bool2);
        }
        repaintParentIfNeeded(i, j, k, m);
      }
      finally
      {
        setBoundsOp(5);
      }
    }
  }
  
  private void repaintParentIfNeeded(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((parent != null) && ((peer instanceof LightweightPeer)) && (isShowing()))
    {
      parent.repaint(paramInt1, paramInt2, paramInt3, paramInt4);
      repaint();
    }
  }
  
  private void reshapeNativePeer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = paramInt1;
    int j = paramInt2;
    for (Container localContainer = parent; (localContainer != null) && ((peer instanceof LightweightPeer)); localContainer = parent)
    {
      i += x;
      j += y;
    }
    peer.setBounds(i, j, paramInt3, paramInt4, paramInt5);
  }
  
  private void notifyNewBounds(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((componentListener != null) || ((eventMask & 1L) != 0L) || (Toolkit.enabledOnToolkit(1L)))
    {
      ComponentEvent localComponentEvent;
      if (paramBoolean1)
      {
        localComponentEvent = new ComponentEvent(this, 101);
        Toolkit.getEventQueue().postEvent(localComponentEvent);
      }
      if (paramBoolean2)
      {
        localComponentEvent = new ComponentEvent(this, 100);
        Toolkit.getEventQueue().postEvent(localComponentEvent);
      }
    }
    else if (((this instanceof Container)) && (((Container)this).countComponents() > 0))
    {
      boolean bool = Toolkit.enabledOnToolkit(65536L);
      if (paramBoolean1) {
        ((Container)this).createChildHierarchyEvents(1402, 0L, bool);
      }
      if (paramBoolean2) {
        ((Container)this).createChildHierarchyEvents(1401, 0L, bool);
      }
    }
  }
  
  public void setBounds(Rectangle paramRectangle)
  {
    setBounds(x, y, width, height);
  }
  
  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public int getWidth()
  {
    return width;
  }
  
  public int getHeight()
  {
    return height;
  }
  
  public Rectangle getBounds(Rectangle paramRectangle)
  {
    if (paramRectangle == null) {
      return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
    paramRectangle.setBounds(getX(), getY(), getWidth(), getHeight());
    return paramRectangle;
  }
  
  public Dimension getSize(Dimension paramDimension)
  {
    if (paramDimension == null) {
      return new Dimension(getWidth(), getHeight());
    }
    paramDimension.setSize(getWidth(), getHeight());
    return paramDimension;
  }
  
  public Point getLocation(Point paramPoint)
  {
    if (paramPoint == null) {
      return new Point(getX(), getY());
    }
    paramPoint.setLocation(getX(), getY());
    return paramPoint;
  }
  
  public boolean isOpaque()
  {
    if (getPeer() == null) {
      return false;
    }
    return !isLightweight();
  }
  
  public boolean isLightweight()
  {
    return getPeer() instanceof LightweightPeer;
  }
  
  public void setPreferredSize(Dimension paramDimension)
  {
    Dimension localDimension;
    if (prefSizeSet) {
      localDimension = prefSize;
    } else {
      localDimension = null;
    }
    prefSize = paramDimension;
    prefSizeSet = (paramDimension != null);
    firePropertyChange("preferredSize", localDimension, paramDimension);
  }
  
  public boolean isPreferredSizeSet()
  {
    return prefSizeSet;
  }
  
  public Dimension getPreferredSize()
  {
    return preferredSize();
  }
  
  @Deprecated
  public Dimension preferredSize()
  {
    Dimension localDimension = prefSize;
    if ((localDimension == null) || ((!isPreferredSizeSet()) && (!isValid()))) {
      synchronized (getTreeLock())
      {
        prefSize = (peer != null ? peer.getPreferredSize() : getMinimumSize());
        localDimension = prefSize;
      }
    }
    return new Dimension(localDimension);
  }
  
  public void setMinimumSize(Dimension paramDimension)
  {
    Dimension localDimension;
    if (minSizeSet) {
      localDimension = minSize;
    } else {
      localDimension = null;
    }
    minSize = paramDimension;
    minSizeSet = (paramDimension != null);
    firePropertyChange("minimumSize", localDimension, paramDimension);
  }
  
  public boolean isMinimumSizeSet()
  {
    return minSizeSet;
  }
  
  public Dimension getMinimumSize()
  {
    return minimumSize();
  }
  
  @Deprecated
  public Dimension minimumSize()
  {
    Dimension localDimension = minSize;
    if ((localDimension == null) || ((!isMinimumSizeSet()) && (!isValid()))) {
      synchronized (getTreeLock())
      {
        minSize = (peer != null ? peer.getMinimumSize() : size());
        localDimension = minSize;
      }
    }
    return new Dimension(localDimension);
  }
  
  public void setMaximumSize(Dimension paramDimension)
  {
    Dimension localDimension;
    if (maxSizeSet) {
      localDimension = maxSize;
    } else {
      localDimension = null;
    }
    maxSize = paramDimension;
    maxSizeSet = (paramDimension != null);
    firePropertyChange("maximumSize", localDimension, paramDimension);
  }
  
  public boolean isMaximumSizeSet()
  {
    return maxSizeSet;
  }
  
  public Dimension getMaximumSize()
  {
    if (isMaximumSizeSet()) {
      return new Dimension(maxSize);
    }
    return new Dimension(32767, 32767);
  }
  
  public float getAlignmentX()
  {
    return 0.5F;
  }
  
  public float getAlignmentY()
  {
    return 0.5F;
  }
  
  public int getBaseline(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Width and height must be >= 0");
    }
    return -1;
  }
  
  public BaselineResizeBehavior getBaselineResizeBehavior()
  {
    return BaselineResizeBehavior.OTHER;
  }
  
  public void doLayout()
  {
    layout();
  }
  
  @Deprecated
  public void layout() {}
  
  public void validate()
  {
    synchronized (getTreeLock())
    {
      ComponentPeer localComponentPeer = peer;
      boolean bool = isValid();
      if ((!bool) && (localComponentPeer != null))
      {
        Font localFont1 = getFont();
        Font localFont2 = peerFont;
        if ((localFont1 != localFont2) && ((localFont2 == null) || (!localFont2.equals(localFont1))))
        {
          localComponentPeer.setFont(localFont1);
          peerFont = localFont1;
        }
        localComponentPeer.layout();
      }
      valid = true;
      if (!bool) {
        mixOnValidating();
      }
    }
  }
  
  public void invalidate()
  {
    synchronized (getTreeLock())
    {
      valid = false;
      if (!isPreferredSizeSet()) {
        prefSize = null;
      }
      if (!isMinimumSizeSet()) {
        minSize = null;
      }
      if (!isMaximumSizeSet()) {
        maxSize = null;
      }
      invalidateParent();
    }
  }
  
  void invalidateParent()
  {
    if (parent != null) {
      parent.invalidateIfValid();
    }
  }
  
  final void invalidateIfValid()
  {
    if (isValid()) {
      invalidate();
    }
  }
  
  public void revalidate()
  {
    revalidateSynchronously();
  }
  
  final void revalidateSynchronously()
  {
    synchronized (getTreeLock())
    {
      invalidate();
      Container localContainer = getContainer();
      if (localContainer == null)
      {
        validate();
      }
      else
      {
        while ((!localContainer.isValidateRoot()) && (localContainer.getContainer() != null)) {
          localContainer = localContainer.getContainer();
        }
        localContainer.validate();
      }
    }
  }
  
  public Graphics getGraphics()
  {
    if ((peer instanceof LightweightPeer))
    {
      if (parent == null) {
        return null;
      }
      localObject = parent.getGraphics();
      if (localObject == null) {
        return null;
      }
      if ((localObject instanceof ConstrainableGraphics))
      {
        ((ConstrainableGraphics)localObject).constrain(x, y, width, height);
      }
      else
      {
        ((Graphics)localObject).translate(x, y);
        ((Graphics)localObject).setClip(0, 0, width, height);
      }
      ((Graphics)localObject).setFont(getFont());
      return (Graphics)localObject;
    }
    Object localObject = peer;
    return localObject != null ? ((ComponentPeer)localObject).getGraphics() : null;
  }
  
  final Graphics getGraphics_NoClientCode()
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer instanceof LightweightPeer))
    {
      Container localContainer = parent;
      if (localContainer == null) {
        return null;
      }
      Graphics localGraphics = localContainer.getGraphics_NoClientCode();
      if (localGraphics == null) {
        return null;
      }
      if ((localGraphics instanceof ConstrainableGraphics))
      {
        ((ConstrainableGraphics)localGraphics).constrain(x, y, width, height);
      }
      else
      {
        localGraphics.translate(x, y);
        localGraphics.setClip(0, 0, width, height);
      }
      localGraphics.setFont(getFont_NoClientCode());
      return localGraphics;
    }
    return localComponentPeer != null ? localComponentPeer.getGraphics() : null;
  }
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    FontManager localFontManager = FontManagerFactory.getInstance();
    if (((localFontManager instanceof SunFontManager)) && (((SunFontManager)localFontManager).usePlatformFontMetrics()) && (peer != null) && (!(peer instanceof LightweightPeer))) {
      return peer.getFontMetrics(paramFont);
    }
    return FontDesignMetrics.getMetrics(paramFont);
  }
  
  public void setCursor(Cursor paramCursor)
  {
    cursor = paramCursor;
    updateCursorImmediately();
  }
  
  final void updateCursorImmediately()
  {
    if ((peer instanceof LightweightPeer))
    {
      Container localContainer = getNativeContainer();
      if (localContainer == null) {
        return;
      }
      ComponentPeer localComponentPeer = localContainer.getPeer();
      if (localComponentPeer != null) {
        localComponentPeer.updateCursorImmediately();
      }
    }
    else if (peer != null)
    {
      peer.updateCursorImmediately();
    }
  }
  
  public Cursor getCursor()
  {
    return getCursor_NoClientCode();
  }
  
  final Cursor getCursor_NoClientCode()
  {
    Cursor localCursor = cursor;
    if (localCursor != null) {
      return localCursor;
    }
    Container localContainer = parent;
    if (localContainer != null) {
      return localContainer.getCursor_NoClientCode();
    }
    return Cursor.getPredefinedCursor(0);
  }
  
  public boolean isCursorSet()
  {
    return cursor != null;
  }
  
  public void paint(Graphics paramGraphics) {}
  
  public void update(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }
  
  public void paintAll(Graphics paramGraphics)
  {
    if (isShowing()) {
      GraphicsCallback.PeerPaintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, width, height), paramGraphics, paramGraphics.getClip(), 3);
    }
  }
  
  void lightweightPaint(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }
  
  void paintHeavyweightComponents(Graphics paramGraphics) {}
  
  public void repaint()
  {
    repaint(0L, 0, 0, width, height);
  }
  
  public void repaint(long paramLong)
  {
    repaint(paramLong, 0, 0, width, height);
  }
  
  public void repaint(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    repaint(0L, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((peer instanceof LightweightPeer))
    {
      if (parent != null)
      {
        if (paramInt1 < 0)
        {
          paramInt3 += paramInt1;
          paramInt1 = 0;
        }
        if (paramInt2 < 0)
        {
          paramInt4 += paramInt2;
          paramInt2 = 0;
        }
        int i = paramInt3 > width ? width : paramInt3;
        int j = paramInt4 > height ? height : paramInt4;
        if ((i <= 0) || (j <= 0)) {
          return;
        }
        int k = x + paramInt1;
        int m = y + paramInt2;
        parent.repaint(paramLong, k, m, i, j);
      }
    }
    else if ((isVisible()) && (peer != null) && (paramInt3 > 0) && (paramInt4 > 0))
    {
      PaintEvent localPaintEvent = new PaintEvent(this, 801, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
      SunToolkit.postEvent(SunToolkit.targetToAppContext(this), localPaintEvent);
    }
  }
  
  public void print(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }
  
  public void printAll(Graphics paramGraphics)
  {
    if (isShowing()) {
      GraphicsCallback.PeerPrintCallback.getInstance().runOneComponent(this, new Rectangle(0, 0, width, height), paramGraphics, paramGraphics.getClip(), 3);
    }
  }
  
  void lightweightPrint(Graphics paramGraphics)
  {
    print(paramGraphics);
  }
  
  void printHeavyweightComponents(Graphics paramGraphics) {}
  
  private Insets getInsets_NoClientCode()
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer instanceof ContainerPeer)) {
      return (Insets)((ContainerPeer)localComponentPeer).getInsets().clone();
    }
    return new Insets(0, 0, 0, 0);
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = -1;
    if ((paramInt1 & 0x30) != 0)
    {
      i = 0;
    }
    else if (((paramInt1 & 0x8) != 0) && (isInc))
    {
      i = incRate;
      if (i < 0) {
        i = 0;
      }
    }
    if (i >= 0) {
      repaint(i, 0, 0, width, height);
    }
    return (paramInt1 & 0xA0) == 0;
  }
  
  public Image createImage(ImageProducer paramImageProducer)
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer != null) && (!(localComponentPeer instanceof LightweightPeer))) {
      return localComponentPeer.createImage(paramImageProducer);
    }
    return getToolkit().createImage(paramImageProducer);
  }
  
  public Image createImage(int paramInt1, int paramInt2)
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer instanceof LightweightPeer))
    {
      if (parent != null) {
        return parent.createImage(paramInt1, paramInt2);
      }
      return null;
    }
    return localComponentPeer != null ? localComponentPeer.createImage(paramInt1, paramInt2) : null;
  }
  
  public VolatileImage createVolatileImage(int paramInt1, int paramInt2)
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer instanceof LightweightPeer))
    {
      if (parent != null) {
        return parent.createVolatileImage(paramInt1, paramInt2);
      }
      return null;
    }
    return localComponentPeer != null ? localComponentPeer.createVolatileImage(paramInt1, paramInt2) : null;
  }
  
  public VolatileImage createVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities)
    throws AWTException
  {
    return createVolatileImage(paramInt1, paramInt2);
  }
  
  public boolean prepareImage(Image paramImage, ImageObserver paramImageObserver)
  {
    return prepareImage(paramImage, -1, -1, paramImageObserver);
  }
  
  public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer instanceof LightweightPeer)) {
      return parent != null ? parent.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
    }
    return localComponentPeer != null ? localComponentPeer.prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().prepareImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public int checkImage(Image paramImage, ImageObserver paramImageObserver)
  {
    return checkImage(paramImage, -1, -1, paramImageObserver);
  }
  
  public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    ComponentPeer localComponentPeer = peer;
    if ((localComponentPeer instanceof LightweightPeer)) {
      return parent != null ? parent.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
    }
    return localComponentPeer != null ? localComponentPeer.checkImage(paramImage, paramInt1, paramInt2, paramImageObserver) : getToolkit().checkImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  void createBufferStrategy(int paramInt)
  {
    if (paramInt > 1)
    {
      localBufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.UNDEFINED);
      try
      {
        createBufferStrategy(paramInt, localBufferCapabilities);
        return;
      }
      catch (AWTException localAWTException1) {}
    }
    BufferCapabilities localBufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), null);
    try
    {
      createBufferStrategy(paramInt, localBufferCapabilities);
      return;
    }
    catch (AWTException localAWTException2)
    {
      localBufferCapabilities = new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), null);
      try
      {
        createBufferStrategy(paramInt, localBufferCapabilities);
        return;
      }
      catch (AWTException localAWTException3)
      {
        throw new InternalError("Could not create a buffer strategy", localAWTException3);
      }
    }
  }
  
  void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("Number of buffers must be at least 1");
    }
    if (paramBufferCapabilities == null) {
      throw new IllegalArgumentException("No capabilities specified");
    }
    if (bufferStrategy != null) {
      bufferStrategy.dispose();
    }
    if (paramInt == 1)
    {
      bufferStrategy = new SingleBufferStrategy(paramBufferCapabilities);
    }
    else
    {
      SunGraphicsEnvironment localSunGraphicsEnvironment = (SunGraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
      if ((!paramBufferCapabilities.isPageFlipping()) && (localSunGraphicsEnvironment.isFlipStrategyPreferred(peer))) {
        paramBufferCapabilities = new ProxyCapabilities(paramBufferCapabilities, null);
      }
      if (paramBufferCapabilities.isPageFlipping()) {
        bufferStrategy = new FlipSubRegionBufferStrategy(paramInt, paramBufferCapabilities);
      } else {
        bufferStrategy = new BltSubRegionBufferStrategy(paramInt, paramBufferCapabilities);
      }
    }
  }
  
  BufferStrategy getBufferStrategy()
  {
    return bufferStrategy;
  }
  
  Image getBackBuffer()
  {
    if (bufferStrategy != null)
    {
      Object localObject;
      if ((bufferStrategy instanceof BltBufferStrategy))
      {
        localObject = (BltBufferStrategy)bufferStrategy;
        return ((BltBufferStrategy)localObject).getBackBuffer();
      }
      if ((bufferStrategy instanceof FlipBufferStrategy))
      {
        localObject = (FlipBufferStrategy)bufferStrategy;
        return ((FlipBufferStrategy)localObject).getBackBuffer();
      }
    }
    return null;
  }
  
  public void setIgnoreRepaint(boolean paramBoolean)
  {
    ignoreRepaint = paramBoolean;
  }
  
  public boolean getIgnoreRepaint()
  {
    return ignoreRepaint;
  }
  
  public boolean contains(int paramInt1, int paramInt2)
  {
    return inside(paramInt1, paramInt2);
  }
  
  @Deprecated
  public boolean inside(int paramInt1, int paramInt2)
  {
    return (paramInt1 >= 0) && (paramInt1 < width) && (paramInt2 >= 0) && (paramInt2 < height);
  }
  
  public boolean contains(Point paramPoint)
  {
    return contains(x, y);
  }
  
  public Component getComponentAt(int paramInt1, int paramInt2)
  {
    return locate(paramInt1, paramInt2);
  }
  
  @Deprecated
  public Component locate(int paramInt1, int paramInt2)
  {
    return contains(paramInt1, paramInt2) ? this : null;
  }
  
  public Component getComponentAt(Point paramPoint)
  {
    return getComponentAt(x, y);
  }
  
  @Deprecated
  public void deliverEvent(Event paramEvent)
  {
    postEvent(paramEvent);
  }
  
  public final void dispatchEvent(AWTEvent paramAWTEvent)
  {
    dispatchEventImpl(paramAWTEvent);
  }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent)
  {
    int i = paramAWTEvent.getID();
    AppContext localAppContext = appContext;
    if ((localAppContext != null) && (!localAppContext.equals(AppContext.getAppContext())) && (eventLog.isLoggable(PlatformLogger.Level.FINE))) {
      eventLog.fine("Event " + paramAWTEvent + " is being dispatched on the wrong AppContext");
    }
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
      eventLog.finest("{0}", new Object[] { paramAWTEvent });
    }
    if (!(paramAWTEvent instanceof KeyEvent)) {
      EventQueue.setCurrentEventAndMostRecentTime(paramAWTEvent);
    }
    if ((paramAWTEvent instanceof SunDropTargetEvent))
    {
      ((SunDropTargetEvent)paramAWTEvent).dispatch();
      return;
    }
    if (!focusManagerIsDispatching)
    {
      if (isPosted)
      {
        paramAWTEvent = KeyboardFocusManager.retargetFocusEvent(paramAWTEvent);
        isPosted = true;
      }
      if (KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchEvent(paramAWTEvent)) {
        return;
      }
    }
    if (((paramAWTEvent instanceof FocusEvent)) && (focusLog.isLoggable(PlatformLogger.Level.FINEST))) {
      focusLog.finest("" + paramAWTEvent);
    }
    if ((i == 507) && (!eventTypeEnabled(i)) && (peer != null) && (!peer.handlesWheelScrolling()) && (dispatchMouseWheelToAncestor((MouseWheelEvent)paramAWTEvent))) {
      return;
    }
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    localToolkit.notifyAWTEventListeners(paramAWTEvent);
    if ((!paramAWTEvent.isConsumed()) && ((paramAWTEvent instanceof KeyEvent)))
    {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().processKeyEvent(this, (KeyEvent)paramAWTEvent);
      if (paramAWTEvent.isConsumed()) {
        return;
      }
    }
    Object localObject;
    if (areInputMethodsEnabled())
    {
      if ((((paramAWTEvent instanceof InputMethodEvent)) && (!(this instanceof CompositionArea))) || ((paramAWTEvent instanceof InputEvent)) || ((paramAWTEvent instanceof FocusEvent)))
      {
        localObject = getInputContext();
        if (localObject != null)
        {
          ((java.awt.im.InputContext)localObject).dispatchEvent(paramAWTEvent);
          if (paramAWTEvent.isConsumed())
          {
            if (((paramAWTEvent instanceof FocusEvent)) && (focusLog.isLoggable(PlatformLogger.Level.FINEST))) {
              focusLog.finest("3579: Skipping " + paramAWTEvent);
            }
            return;
          }
        }
      }
    }
    else if (i == 1004)
    {
      localObject = getInputContext();
      if ((localObject != null) && ((localObject instanceof sun.awt.im.InputContext))) {
        ((sun.awt.im.InputContext)localObject).disableNativeIM();
      }
    }
    switch (i)
    {
    case 401: 
    case 402: 
      localObject = (Container)((this instanceof Container) ? this : parent);
      if (localObject != null)
      {
        ((Container)localObject).preProcessKeyEvent((KeyEvent)paramAWTEvent);
        if (paramAWTEvent.isConsumed())
        {
          if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("Pre-process consumed event");
          }
          return;
        }
      }
      break;
    case 201: 
      if ((localToolkit instanceof WindowClosingListener))
      {
        windowClosingException = ((WindowClosingListener)localToolkit).windowClosingNotify((WindowEvent)paramAWTEvent);
        if (checkWindowClosingException()) {
          return;
        }
      }
      break;
    }
    if (newEventsOnly)
    {
      if (eventEnabled(paramAWTEvent)) {
        processEvent(paramAWTEvent);
      }
    }
    else if (i == 507)
    {
      autoProcessMouseWheel((MouseWheelEvent)paramAWTEvent);
    }
    else if ((!(paramAWTEvent instanceof MouseEvent)) || (postsOldMouseEvents()))
    {
      localObject = paramAWTEvent.convertToOld();
      if (localObject != null)
      {
        int j = key;
        int k = modifiers;
        postEvent((Event)localObject);
        if (((Event)localObject).isConsumed()) {
          paramAWTEvent.consume();
        }
        switch (id)
        {
        case 401: 
        case 402: 
        case 403: 
        case 404: 
          if (key != j) {
            ((KeyEvent)paramAWTEvent).setKeyChar(((Event)localObject).getKeyEventChar());
          }
          if (modifiers != k) {
            ((KeyEvent)paramAWTEvent).setModifiers(modifiers);
          }
          break;
        }
      }
    }
    if ((i == 201) && (!paramAWTEvent.isConsumed()) && ((localToolkit instanceof WindowClosingListener)))
    {
      windowClosingException = ((WindowClosingListener)localToolkit).windowClosingDelivered((WindowEvent)paramAWTEvent);
      if (checkWindowClosingException()) {
        return;
      }
    }
    if (!(paramAWTEvent instanceof KeyEvent))
    {
      localObject = peer;
      if (((paramAWTEvent instanceof FocusEvent)) && ((localObject == null) || ((localObject instanceof LightweightPeer))))
      {
        Component localComponent = (Component)paramAWTEvent.getSource();
        if (localComponent != null)
        {
          Container localContainer = localComponent.getNativeContainer();
          if (localContainer != null) {
            localObject = localContainer.getPeer();
          }
        }
      }
      if (localObject != null) {
        ((ComponentPeer)localObject).handleEvent(paramAWTEvent);
      }
    }
  }
  
  void autoProcessMouseWheel(MouseWheelEvent paramMouseWheelEvent) {}
  
  boolean dispatchMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent)
  {
    int i = paramMouseWheelEvent.getX() + getX();
    int j = paramMouseWheelEvent.getY() + getY();
    if (eventLog.isLoggable(PlatformLogger.Level.FINEST))
    {
      eventLog.finest("dispatchMouseWheelToAncestor");
      eventLog.finest("orig event src is of " + paramMouseWheelEvent.getSource().getClass());
    }
    synchronized (getTreeLock())
    {
      for (Container localContainer = getParent(); (localContainer != null) && (!localContainer.eventEnabled(paramMouseWheelEvent)); localContainer = localContainer.getParent())
      {
        i += localContainer.getX();
        j += localContainer.getY();
        if ((localContainer instanceof Window)) {
          break;
        }
      }
      if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
        eventLog.finest("new event src is " + localContainer.getClass());
      }
      if ((localContainer != null) && (localContainer.eventEnabled(paramMouseWheelEvent)))
      {
        MouseWheelEvent localMouseWheelEvent = new MouseWheelEvent(localContainer, paramMouseWheelEvent.getID(), paramMouseWheelEvent.getWhen(), paramMouseWheelEvent.getModifiers(), i, j, paramMouseWheelEvent.getXOnScreen(), paramMouseWheelEvent.getYOnScreen(), paramMouseWheelEvent.getClickCount(), paramMouseWheelEvent.isPopupTrigger(), paramMouseWheelEvent.getScrollType(), paramMouseWheelEvent.getScrollAmount(), paramMouseWheelEvent.getWheelRotation(), paramMouseWheelEvent.getPreciseWheelRotation());
        paramMouseWheelEvent.copyPrivateDataInto(localMouseWheelEvent);
        localContainer.dispatchEventToSelf(localMouseWheelEvent);
        if (localMouseWheelEvent.isConsumed()) {
          paramMouseWheelEvent.consume();
        }
        return true;
      }
    }
    return false;
  }
  
  boolean checkWindowClosingException()
  {
    if (windowClosingException != null)
    {
      if ((this instanceof Dialog))
      {
        ((Dialog)this).interruptBlocking();
      }
      else
      {
        windowClosingException.fillInStackTrace();
        windowClosingException.printStackTrace();
        windowClosingException = null;
      }
      return true;
    }
    return false;
  }
  
  boolean areInputMethodsEnabled()
  {
    return ((eventMask & 0x1000) != 0L) && (((eventMask & 0x8) != 0L) || (keyListener != null));
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    return eventTypeEnabled(id);
  }
  
  boolean eventTypeEnabled(int paramInt)
  {
    switch (paramInt)
    {
    case 100: 
    case 101: 
    case 102: 
    case 103: 
      if (((eventMask & 1L) != 0L) || (componentListener != null)) {
        return true;
      }
      break;
    case 1004: 
    case 1005: 
      if (((eventMask & 0x4) != 0L) || (focusListener != null)) {
        return true;
      }
      break;
    case 400: 
    case 401: 
    case 402: 
      if (((eventMask & 0x8) != 0L) || (keyListener != null)) {
        return true;
      }
      break;
    case 500: 
    case 501: 
    case 502: 
    case 504: 
    case 505: 
      if (((eventMask & 0x10) != 0L) || (mouseListener != null)) {
        return true;
      }
      break;
    case 503: 
    case 506: 
      if (((eventMask & 0x20) != 0L) || (mouseMotionListener != null)) {
        return true;
      }
      break;
    case 507: 
      if (((eventMask & 0x20000) != 0L) || (mouseWheelListener != null)) {
        return true;
      }
      break;
    case 1100: 
    case 1101: 
      if (((eventMask & 0x800) != 0L) || (inputMethodListener != null)) {
        return true;
      }
      break;
    case 1400: 
      if (((eventMask & 0x8000) != 0L) || (hierarchyListener != null)) {
        return true;
      }
      break;
    case 1401: 
    case 1402: 
      if (((eventMask & 0x10000) != 0L) || (hierarchyBoundsListener != null)) {
        return true;
      }
      break;
    case 1001: 
      if ((eventMask & 0x80) != 0L) {
        return true;
      }
      break;
    case 900: 
      if ((eventMask & 0x400) != 0L) {
        return true;
      }
      break;
    case 701: 
      if ((eventMask & 0x200) != 0L) {
        return true;
      }
      break;
    case 601: 
      if ((eventMask & 0x100) != 0L) {
        return true;
      }
      break;
    }
    return paramInt > 1999;
  }
  
  @Deprecated
  public boolean postEvent(Event paramEvent)
  {
    ComponentPeer localComponentPeer = peer;
    if (handleEvent(paramEvent))
    {
      paramEvent.consume();
      return true;
    }
    Container localContainer = parent;
    int i = x;
    int j = y;
    if (localContainer != null)
    {
      paramEvent.translate(x, y);
      if (localContainer.postEvent(paramEvent))
      {
        paramEvent.consume();
        return true;
      }
      x = i;
      y = j;
    }
    return false;
  }
  
  public synchronized void addComponentListener(ComponentListener paramComponentListener)
  {
    if (paramComponentListener == null) {
      return;
    }
    componentListener = AWTEventMulticaster.add(componentListener, paramComponentListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeComponentListener(ComponentListener paramComponentListener)
  {
    if (paramComponentListener == null) {
      return;
    }
    componentListener = AWTEventMulticaster.remove(componentListener, paramComponentListener);
  }
  
  public synchronized ComponentListener[] getComponentListeners()
  {
    return (ComponentListener[])getListeners(ComponentListener.class);
  }
  
  public synchronized void addFocusListener(FocusListener paramFocusListener)
  {
    if (paramFocusListener == null) {
      return;
    }
    focusListener = AWTEventMulticaster.add(focusListener, paramFocusListener);
    newEventsOnly = true;
    if ((peer instanceof LightweightPeer)) {
      parent.proxyEnableEvents(4L);
    }
  }
  
  public synchronized void removeFocusListener(FocusListener paramFocusListener)
  {
    if (paramFocusListener == null) {
      return;
    }
    focusListener = AWTEventMulticaster.remove(focusListener, paramFocusListener);
  }
  
  public synchronized FocusListener[] getFocusListeners()
  {
    return (FocusListener[])getListeners(FocusListener.class);
  }
  
  public void addHierarchyListener(HierarchyListener paramHierarchyListener)
  {
    if (paramHierarchyListener == null) {
      return;
    }
    int i;
    synchronized (this)
    {
      i = (hierarchyListener == null) && ((eventMask & 0x8000) == 0L) ? 1 : 0;
      hierarchyListener = AWTEventMulticaster.add(hierarchyListener, paramHierarchyListener);
      i = (i != 0) && (hierarchyListener != null) ? 1 : 0;
      newEventsOnly = true;
    }
    if (i != 0) {
      synchronized (getTreeLock())
      {
        adjustListeningChildrenOnParent(32768L, 1);
      }
    }
  }
  
  public void removeHierarchyListener(HierarchyListener paramHierarchyListener)
  {
    if (paramHierarchyListener == null) {
      return;
    }
    int i;
    synchronized (this)
    {
      i = (hierarchyListener != null) && ((eventMask & 0x8000) == 0L) ? 1 : 0;
      hierarchyListener = AWTEventMulticaster.remove(hierarchyListener, paramHierarchyListener);
      i = (i != 0) && (hierarchyListener == null) ? 1 : 0;
    }
    if (i != 0) {
      synchronized (getTreeLock())
      {
        adjustListeningChildrenOnParent(32768L, -1);
      }
    }
  }
  
  public synchronized HierarchyListener[] getHierarchyListeners()
  {
    return (HierarchyListener[])getListeners(HierarchyListener.class);
  }
  
  public void addHierarchyBoundsListener(HierarchyBoundsListener paramHierarchyBoundsListener)
  {
    if (paramHierarchyBoundsListener == null) {
      return;
    }
    int i;
    synchronized (this)
    {
      i = (hierarchyBoundsListener == null) && ((eventMask & 0x10000) == 0L) ? 1 : 0;
      hierarchyBoundsListener = AWTEventMulticaster.add(hierarchyBoundsListener, paramHierarchyBoundsListener);
      i = (i != 0) && (hierarchyBoundsListener != null) ? 1 : 0;
      newEventsOnly = true;
    }
    if (i != 0) {
      synchronized (getTreeLock())
      {
        adjustListeningChildrenOnParent(65536L, 1);
      }
    }
  }
  
  public void removeHierarchyBoundsListener(HierarchyBoundsListener paramHierarchyBoundsListener)
  {
    if (paramHierarchyBoundsListener == null) {
      return;
    }
    int i;
    synchronized (this)
    {
      i = (hierarchyBoundsListener != null) && ((eventMask & 0x10000) == 0L) ? 1 : 0;
      hierarchyBoundsListener = AWTEventMulticaster.remove(hierarchyBoundsListener, paramHierarchyBoundsListener);
      i = (i != 0) && (hierarchyBoundsListener == null) ? 1 : 0;
    }
    if (i != 0) {
      synchronized (getTreeLock())
      {
        adjustListeningChildrenOnParent(65536L, -1);
      }
    }
  }
  
  int numListening(long paramLong)
  {
    if ((eventLog.isLoggable(PlatformLogger.Level.FINE)) && (paramLong != 32768L) && (paramLong != 65536L)) {
      eventLog.fine("Assertion failed");
    }
    if (((paramLong == 32768L) && ((hierarchyListener != null) || ((eventMask & 0x8000) != 0L))) || ((paramLong == 65536L) && ((hierarchyBoundsListener != null) || ((eventMask & 0x10000) != 0L)))) {
      return 1;
    }
    return 0;
  }
  
  int countHierarchyMembers()
  {
    return 1;
  }
  
  int createHierarchyEvents(int paramInt, Component paramComponent, Container paramContainer, long paramLong, boolean paramBoolean)
  {
    HierarchyEvent localHierarchyEvent;
    switch (paramInt)
    {
    case 1400: 
      if ((hierarchyListener != null) || ((eventMask & 0x8000) != 0L) || (paramBoolean))
      {
        localHierarchyEvent = new HierarchyEvent(this, paramInt, paramComponent, paramContainer, paramLong);
        dispatchEvent(localHierarchyEvent);
        return 1;
      }
      break;
    case 1401: 
    case 1402: 
      if ((eventLog.isLoggable(PlatformLogger.Level.FINE)) && (paramLong != 0L)) {
        eventLog.fine("Assertion (changeFlags == 0) failed");
      }
      if ((hierarchyBoundsListener != null) || ((eventMask & 0x10000) != 0L) || (paramBoolean))
      {
        localHierarchyEvent = new HierarchyEvent(this, paramInt, paramComponent, paramContainer);
        dispatchEvent(localHierarchyEvent);
        return 1;
      }
      break;
    default: 
      if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
        eventLog.fine("This code must never be reached");
      }
      break;
    }
    return 0;
  }
  
  public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners()
  {
    return (HierarchyBoundsListener[])getListeners(HierarchyBoundsListener.class);
  }
  
  void adjustListeningChildrenOnParent(long paramLong, int paramInt)
  {
    if (parent != null) {
      parent.adjustListeningChildren(paramLong, paramInt);
    }
  }
  
  public synchronized void addKeyListener(KeyListener paramKeyListener)
  {
    if (paramKeyListener == null) {
      return;
    }
    keyListener = AWTEventMulticaster.add(keyListener, paramKeyListener);
    newEventsOnly = true;
    if ((peer instanceof LightweightPeer)) {
      parent.proxyEnableEvents(8L);
    }
  }
  
  public synchronized void removeKeyListener(KeyListener paramKeyListener)
  {
    if (paramKeyListener == null) {
      return;
    }
    keyListener = AWTEventMulticaster.remove(keyListener, paramKeyListener);
  }
  
  public synchronized KeyListener[] getKeyListeners()
  {
    return (KeyListener[])getListeners(KeyListener.class);
  }
  
  public synchronized void addMouseListener(MouseListener paramMouseListener)
  {
    if (paramMouseListener == null) {
      return;
    }
    mouseListener = AWTEventMulticaster.add(mouseListener, paramMouseListener);
    newEventsOnly = true;
    if ((peer instanceof LightweightPeer)) {
      parent.proxyEnableEvents(16L);
    }
  }
  
  public synchronized void removeMouseListener(MouseListener paramMouseListener)
  {
    if (paramMouseListener == null) {
      return;
    }
    mouseListener = AWTEventMulticaster.remove(mouseListener, paramMouseListener);
  }
  
  public synchronized MouseListener[] getMouseListeners()
  {
    return (MouseListener[])getListeners(MouseListener.class);
  }
  
  public synchronized void addMouseMotionListener(MouseMotionListener paramMouseMotionListener)
  {
    if (paramMouseMotionListener == null) {
      return;
    }
    mouseMotionListener = AWTEventMulticaster.add(mouseMotionListener, paramMouseMotionListener);
    newEventsOnly = true;
    if ((peer instanceof LightweightPeer)) {
      parent.proxyEnableEvents(32L);
    }
  }
  
  public synchronized void removeMouseMotionListener(MouseMotionListener paramMouseMotionListener)
  {
    if (paramMouseMotionListener == null) {
      return;
    }
    mouseMotionListener = AWTEventMulticaster.remove(mouseMotionListener, paramMouseMotionListener);
  }
  
  public synchronized MouseMotionListener[] getMouseMotionListeners()
  {
    return (MouseMotionListener[])getListeners(MouseMotionListener.class);
  }
  
  public synchronized void addMouseWheelListener(MouseWheelListener paramMouseWheelListener)
  {
    if (paramMouseWheelListener == null) {
      return;
    }
    mouseWheelListener = AWTEventMulticaster.add(mouseWheelListener, paramMouseWheelListener);
    newEventsOnly = true;
    if ((peer instanceof LightweightPeer)) {
      parent.proxyEnableEvents(131072L);
    }
  }
  
  public synchronized void removeMouseWheelListener(MouseWheelListener paramMouseWheelListener)
  {
    if (paramMouseWheelListener == null) {
      return;
    }
    mouseWheelListener = AWTEventMulticaster.remove(mouseWheelListener, paramMouseWheelListener);
  }
  
  public synchronized MouseWheelListener[] getMouseWheelListeners()
  {
    return (MouseWheelListener[])getListeners(MouseWheelListener.class);
  }
  
  public synchronized void addInputMethodListener(InputMethodListener paramInputMethodListener)
  {
    if (paramInputMethodListener == null) {
      return;
    }
    inputMethodListener = AWTEventMulticaster.add(inputMethodListener, paramInputMethodListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeInputMethodListener(InputMethodListener paramInputMethodListener)
  {
    if (paramInputMethodListener == null) {
      return;
    }
    inputMethodListener = AWTEventMulticaster.remove(inputMethodListener, paramInputMethodListener);
  }
  
  public synchronized InputMethodListener[] getInputMethodListeners()
  {
    return (InputMethodListener[])getListeners(InputMethodListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    Object localObject = null;
    if (paramClass == ComponentListener.class) {
      localObject = componentListener;
    } else if (paramClass == FocusListener.class) {
      localObject = focusListener;
    } else if (paramClass == HierarchyListener.class) {
      localObject = hierarchyListener;
    } else if (paramClass == HierarchyBoundsListener.class) {
      localObject = hierarchyBoundsListener;
    } else if (paramClass == KeyListener.class) {
      localObject = keyListener;
    } else if (paramClass == MouseListener.class) {
      localObject = mouseListener;
    } else if (paramClass == MouseMotionListener.class) {
      localObject = mouseMotionListener;
    } else if (paramClass == MouseWheelListener.class) {
      localObject = mouseWheelListener;
    } else if (paramClass == InputMethodListener.class) {
      localObject = inputMethodListener;
    } else if (paramClass == PropertyChangeListener.class) {
      return (EventListener[])getPropertyChangeListeners();
    }
    return AWTEventMulticaster.getListeners((EventListener)localObject, paramClass);
  }
  
  public InputMethodRequests getInputMethodRequests()
  {
    return null;
  }
  
  public java.awt.im.InputContext getInputContext()
  {
    Container localContainer = parent;
    if (localContainer == null) {
      return null;
    }
    return localContainer.getInputContext();
  }
  
  protected final void enableEvents(long paramLong)
  {
    long l = 0L;
    synchronized (this)
    {
      if (((paramLong & 0x8000) != 0L) && (hierarchyListener == null) && ((eventMask & 0x8000) == 0L)) {
        l |= 0x8000;
      }
      if (((paramLong & 0x10000) != 0L) && (hierarchyBoundsListener == null) && ((eventMask & 0x10000) == 0L)) {
        l |= 0x10000;
      }
      eventMask |= paramLong;
      newEventsOnly = true;
    }
    if ((peer instanceof LightweightPeer)) {
      parent.proxyEnableEvents(eventMask);
    }
    if (l != 0L) {
      synchronized (getTreeLock())
      {
        adjustListeningChildrenOnParent(l, 1);
      }
    }
  }
  
  protected final void disableEvents(long paramLong)
  {
    long l = 0L;
    synchronized (this)
    {
      if (((paramLong & 0x8000) != 0L) && (hierarchyListener == null) && ((eventMask & 0x8000) != 0L)) {
        l |= 0x8000;
      }
      if (((paramLong & 0x10000) != 0L) && (hierarchyBoundsListener == null) && ((eventMask & 0x10000) != 0L)) {
        l |= 0x10000;
      }
      eventMask &= (paramLong ^ 0xFFFFFFFFFFFFFFFF);
    }
    if (l != 0L) {
      synchronized (getTreeLock())
      {
        adjustListeningChildrenOnParent(l, -1);
      }
    }
  }
  
  private boolean checkCoalescing()
  {
    if (getClass().getClassLoader() == null) {
      return false;
    }
    final Class localClass = getClass();
    synchronized (coalesceMap)
    {
      Boolean localBoolean1 = (Boolean)coalesceMap.get(localClass);
      if (localBoolean1 != null) {
        return localBoolean1.booleanValue();
      }
      Boolean localBoolean2 = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Boolean run()
        {
          return Boolean.valueOf(Component.isCoalesceEventsOverriden(localClass));
        }
      });
      coalesceMap.put(localClass, localBoolean2);
      return localBoolean2.booleanValue();
    }
  }
  
  private static boolean isCoalesceEventsOverriden(Class<?> paramClass)
  {
    assert (Thread.holdsLock(coalesceMap));
    Class localClass = paramClass.getSuperclass();
    if (localClass == null) {
      return false;
    }
    if (localClass.getClassLoader() != null)
    {
      Boolean localBoolean = (Boolean)coalesceMap.get(localClass);
      if (localBoolean == null)
      {
        if (isCoalesceEventsOverriden(localClass))
        {
          coalesceMap.put(localClass, Boolean.valueOf(true));
          return true;
        }
      }
      else if (localBoolean.booleanValue()) {
        return true;
      }
    }
    try
    {
      paramClass.getDeclaredMethod("coalesceEvents", coalesceEventsParams);
      return true;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return false;
  }
  
  final boolean isCoalescingEnabled()
  {
    return coalescingEnabled;
  }
  
  protected AWTEvent coalesceEvents(AWTEvent paramAWTEvent1, AWTEvent paramAWTEvent2)
  {
    return null;
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof FocusEvent)) {
      processFocusEvent((FocusEvent)paramAWTEvent);
    } else if ((paramAWTEvent instanceof MouseEvent)) {
      switch (paramAWTEvent.getID())
      {
      case 500: 
      case 501: 
      case 502: 
      case 504: 
      case 505: 
        processMouseEvent((MouseEvent)paramAWTEvent);
        break;
      case 503: 
      case 506: 
        processMouseMotionEvent((MouseEvent)paramAWTEvent);
        break;
      case 507: 
        processMouseWheelEvent((MouseWheelEvent)paramAWTEvent);
      }
    } else if ((paramAWTEvent instanceof KeyEvent)) {
      processKeyEvent((KeyEvent)paramAWTEvent);
    } else if ((paramAWTEvent instanceof ComponentEvent)) {
      processComponentEvent((ComponentEvent)paramAWTEvent);
    } else if ((paramAWTEvent instanceof InputMethodEvent)) {
      processInputMethodEvent((InputMethodEvent)paramAWTEvent);
    } else if ((paramAWTEvent instanceof HierarchyEvent)) {
      switch (paramAWTEvent.getID())
      {
      case 1400: 
        processHierarchyEvent((HierarchyEvent)paramAWTEvent);
        break;
      case 1401: 
      case 1402: 
        processHierarchyBoundsEvent((HierarchyEvent)paramAWTEvent);
      }
    }
  }
  
  protected void processComponentEvent(ComponentEvent paramComponentEvent)
  {
    ComponentListener localComponentListener = componentListener;
    if (localComponentListener != null)
    {
      int i = paramComponentEvent.getID();
      switch (i)
      {
      case 101: 
        localComponentListener.componentResized(paramComponentEvent);
        break;
      case 100: 
        localComponentListener.componentMoved(paramComponentEvent);
        break;
      case 102: 
        localComponentListener.componentShown(paramComponentEvent);
        break;
      case 103: 
        localComponentListener.componentHidden(paramComponentEvent);
      }
    }
  }
  
  protected void processFocusEvent(FocusEvent paramFocusEvent)
  {
    FocusListener localFocusListener = focusListener;
    if (localFocusListener != null)
    {
      int i = paramFocusEvent.getID();
      switch (i)
      {
      case 1004: 
        localFocusListener.focusGained(paramFocusEvent);
        break;
      case 1005: 
        localFocusListener.focusLost(paramFocusEvent);
      }
    }
  }
  
  protected void processKeyEvent(KeyEvent paramKeyEvent)
  {
    KeyListener localKeyListener = keyListener;
    if (localKeyListener != null)
    {
      int i = paramKeyEvent.getID();
      switch (i)
      {
      case 400: 
        localKeyListener.keyTyped(paramKeyEvent);
        break;
      case 401: 
        localKeyListener.keyPressed(paramKeyEvent);
        break;
      case 402: 
        localKeyListener.keyReleased(paramKeyEvent);
      }
    }
  }
  
  protected void processMouseEvent(MouseEvent paramMouseEvent)
  {
    MouseListener localMouseListener = mouseListener;
    if (localMouseListener != null)
    {
      int i = paramMouseEvent.getID();
      switch (i)
      {
      case 501: 
        localMouseListener.mousePressed(paramMouseEvent);
        break;
      case 502: 
        localMouseListener.mouseReleased(paramMouseEvent);
        break;
      case 500: 
        localMouseListener.mouseClicked(paramMouseEvent);
        break;
      case 505: 
        localMouseListener.mouseExited(paramMouseEvent);
        break;
      case 504: 
        localMouseListener.mouseEntered(paramMouseEvent);
      }
    }
  }
  
  protected void processMouseMotionEvent(MouseEvent paramMouseEvent)
  {
    MouseMotionListener localMouseMotionListener = mouseMotionListener;
    if (localMouseMotionListener != null)
    {
      int i = paramMouseEvent.getID();
      switch (i)
      {
      case 503: 
        localMouseMotionListener.mouseMoved(paramMouseEvent);
        break;
      case 506: 
        localMouseMotionListener.mouseDragged(paramMouseEvent);
      }
    }
  }
  
  protected void processMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent)
  {
    MouseWheelListener localMouseWheelListener = mouseWheelListener;
    if (localMouseWheelListener != null)
    {
      int i = paramMouseWheelEvent.getID();
      switch (i)
      {
      case 507: 
        localMouseWheelListener.mouseWheelMoved(paramMouseWheelEvent);
      }
    }
  }
  
  boolean postsOldMouseEvents()
  {
    return false;
  }
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent)
  {
    InputMethodListener localInputMethodListener = inputMethodListener;
    if (localInputMethodListener != null)
    {
      int i = paramInputMethodEvent.getID();
      switch (i)
      {
      case 1100: 
        localInputMethodListener.inputMethodTextChanged(paramInputMethodEvent);
        break;
      case 1101: 
        localInputMethodListener.caretPositionChanged(paramInputMethodEvent);
      }
    }
  }
  
  protected void processHierarchyEvent(HierarchyEvent paramHierarchyEvent)
  {
    HierarchyListener localHierarchyListener = hierarchyListener;
    if (localHierarchyListener != null)
    {
      int i = paramHierarchyEvent.getID();
      switch (i)
      {
      case 1400: 
        localHierarchyListener.hierarchyChanged(paramHierarchyEvent);
      }
    }
  }
  
  protected void processHierarchyBoundsEvent(HierarchyEvent paramHierarchyEvent)
  {
    HierarchyBoundsListener localHierarchyBoundsListener = hierarchyBoundsListener;
    if (localHierarchyBoundsListener != null)
    {
      int i = paramHierarchyEvent.getID();
      switch (i)
      {
      case 1401: 
        localHierarchyBoundsListener.ancestorMoved(paramHierarchyEvent);
        break;
      case 1402: 
        localHierarchyBoundsListener.ancestorResized(paramHierarchyEvent);
      }
    }
  }
  
  @Deprecated
  public boolean handleEvent(Event paramEvent)
  {
    switch (id)
    {
    case 504: 
      return mouseEnter(paramEvent, x, y);
    case 505: 
      return mouseExit(paramEvent, x, y);
    case 503: 
      return mouseMove(paramEvent, x, y);
    case 501: 
      return mouseDown(paramEvent, x, y);
    case 506: 
      return mouseDrag(paramEvent, x, y);
    case 502: 
      return mouseUp(paramEvent, x, y);
    case 401: 
    case 403: 
      return keyDown(paramEvent, key);
    case 402: 
    case 404: 
      return keyUp(paramEvent, key);
    case 1001: 
      return action(paramEvent, arg);
    case 1004: 
      return gotFocus(paramEvent, arg);
    case 1005: 
      return lostFocus(paramEvent, arg);
    }
    return false;
  }
  
  @Deprecated
  public boolean mouseDown(Event paramEvent, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  @Deprecated
  public boolean mouseDrag(Event paramEvent, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  @Deprecated
  public boolean mouseUp(Event paramEvent, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  @Deprecated
  public boolean mouseMove(Event paramEvent, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  @Deprecated
  public boolean mouseEnter(Event paramEvent, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  @Deprecated
  public boolean mouseExit(Event paramEvent, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  @Deprecated
  public boolean keyDown(Event paramEvent, int paramInt)
  {
    return false;
  }
  
  @Deprecated
  public boolean keyUp(Event paramEvent, int paramInt)
  {
    return false;
  }
  
  @Deprecated
  public boolean action(Event paramEvent, Object paramObject)
  {
    return false;
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      Object localObject1 = peer;
      if ((localObject1 == null) || ((localObject1 instanceof LightweightPeer)))
      {
        if (localObject1 == null) {
          peer = (localObject1 = getToolkit().createComponent(this));
        }
        if (parent != null)
        {
          long l = 0L;
          if ((mouseListener != null) || ((eventMask & 0x10) != 0L)) {
            l |= 0x10;
          }
          if ((mouseMotionListener != null) || ((eventMask & 0x20) != 0L)) {
            l |= 0x20;
          }
          if ((mouseWheelListener != null) || ((eventMask & 0x20000) != 0L)) {
            l |= 0x20000;
          }
          if ((focusListener != null) || ((eventMask & 0x4) != 0L)) {
            l |= 0x4;
          }
          if ((keyListener != null) || ((eventMask & 0x8) != 0L)) {
            l |= 0x8;
          }
          if (l != 0L) {
            parent.proxyEnableEvents(l);
          }
        }
      }
      else
      {
        Container localContainer = getContainer();
        if ((localContainer != null) && (localContainer.isLightweight()))
        {
          relocateComponent();
          if (!localContainer.isRecursivelyVisibleUpToHeavyweightContainer()) {
            ((ComponentPeer)localObject1).setVisible(false);
          }
        }
      }
      invalidate();
      int i = popups != null ? popups.size() : 0;
      for (int j = 0; j < i; j++)
      {
        PopupMenu localPopupMenu = (PopupMenu)popups.elementAt(j);
        localPopupMenu.addNotify();
      }
      if (dropTarget != null) {
        dropTarget.addNotify((ComponentPeer)localObject1);
      }
      peerFont = getFont();
      if ((getContainer() != null) && (!isAddNotifyComplete)) {
        getContainer().increaseComponentCount(this);
      }
      updateZOrder();
      if (!isAddNotifyComplete) {
        mixOnShowing();
      }
      isAddNotifyComplete = true;
      if ((hierarchyListener != null) || ((eventMask & 0x8000) != 0L) || (Toolkit.enabledOnToolkit(32768L)))
      {
        HierarchyEvent localHierarchyEvent = new HierarchyEvent(this, 1400, this, parent, 0x2 | (isRecursivelyVisible() ? 4 : 0));
        dispatchEvent(localHierarchyEvent);
      }
    }
  }
  
  public void removeNotify()
  {
    KeyboardFocusManager.clearMostRecentFocusOwner(this);
    if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner() == this) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalPermanentFocusOwner(null);
    }
    synchronized (getTreeLock())
    {
      if ((isFocusOwner()) && (KeyboardFocusManager.isAutoFocusTransferEnabledFor(this))) {
        transferFocus(true);
      }
      if ((getContainer() != null) && (isAddNotifyComplete)) {
        getContainer().decreaseComponentCount(this);
      }
      int i = popups != null ? popups.size() : 0;
      for (int j = 0; j < i; j++)
      {
        PopupMenu localPopupMenu = (PopupMenu)popups.elementAt(j);
        localPopupMenu.removeNotify();
      }
      if ((eventMask & 0x1000) != 0L)
      {
        localObject1 = getInputContext();
        if (localObject1 != null) {
          ((java.awt.im.InputContext)localObject1).removeNotify(this);
        }
      }
      Object localObject1 = peer;
      if (localObject1 != null)
      {
        boolean bool = isLightweight();
        if ((bufferStrategy instanceof FlipBufferStrategy)) {
          ((FlipBufferStrategy)bufferStrategy).destroyBuffers();
        }
        if (dropTarget != null) {
          dropTarget.removeNotify(peer);
        }
        if (visible) {
          ((ComponentPeer)localObject1).setVisible(false);
        }
        peer = null;
        peerFont = null;
        Toolkit.getEventQueue().removeSourceEvents(this, false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().discardKeyEvents(this);
        ((ComponentPeer)localObject1).dispose();
        mixOnHiding(bool);
        isAddNotifyComplete = false;
        compoundShape = null;
      }
      if ((hierarchyListener != null) || ((eventMask & 0x8000) != 0L) || (Toolkit.enabledOnToolkit(32768L)))
      {
        HierarchyEvent localHierarchyEvent = new HierarchyEvent(this, 1400, this, parent, 0x2 | (isRecursivelyVisible() ? 4 : 0));
        dispatchEvent(localHierarchyEvent);
      }
    }
  }
  
  @Deprecated
  public boolean gotFocus(Event paramEvent, Object paramObject)
  {
    return false;
  }
  
  @Deprecated
  public boolean lostFocus(Event paramEvent, Object paramObject)
  {
    return false;
  }
  
  @Deprecated
  public boolean isFocusTraversable()
  {
    if (isFocusTraversableOverridden == 0) {
      isFocusTraversableOverridden = 1;
    }
    return focusable;
  }
  
  public boolean isFocusable()
  {
    return isFocusTraversable();
  }
  
  public void setFocusable(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = focusable;
      focusable = paramBoolean;
    }
    isFocusTraversableOverridden = 2;
    firePropertyChange("focusable", bool, paramBoolean);
    if ((bool) && (!paramBoolean))
    {
      if ((isFocusOwner()) && (KeyboardFocusManager.isAutoFocusTransferEnabled())) {
        transferFocus(true);
      }
      KeyboardFocusManager.clearMostRecentFocusOwner(this);
    }
  }
  
  final boolean isFocusTraversableOverridden()
  {
    return isFocusTraversableOverridden != 1;
  }
  
  public void setFocusTraversalKeys(int paramInt, Set<? extends AWTKeyStroke> paramSet)
  {
    if ((paramInt < 0) || (paramInt >= 3)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    setFocusTraversalKeys_NoIDCheck(paramInt, paramSet);
  }
  
  public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 3)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    return getFocusTraversalKeys_NoIDCheck(paramInt);
  }
  
  final void setFocusTraversalKeys_NoIDCheck(int paramInt, Set<? extends AWTKeyStroke> paramSet)
  {
    Set localSet;
    synchronized (this)
    {
      if (focusTraversalKeys == null) {
        initializeFocusTraversalKeys();
      }
      if (paramSet != null)
      {
        Iterator localIterator = paramSet.iterator();
        while (localIterator.hasNext())
        {
          AWTKeyStroke localAWTKeyStroke = (AWTKeyStroke)localIterator.next();
          if (localAWTKeyStroke == null) {
            throw new IllegalArgumentException("cannot set null focus traversal key");
          }
          if (localAWTKeyStroke.getKeyChar() != 65535) {
            throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
          }
          for (int i = 0; i < focusTraversalKeys.length; i++) {
            if ((i != paramInt) && (getFocusTraversalKeys_NoIDCheck(i).contains(localAWTKeyStroke))) {
              throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
            }
          }
        }
      }
      localSet = focusTraversalKeys[paramInt];
      focusTraversalKeys[paramInt] = (paramSet != null ? Collections.unmodifiableSet(new HashSet(paramSet)) : null);
    }
    firePropertyChange(focusTraversalKeyPropertyNames[paramInt], localSet, paramSet);
  }
  
  final Set<AWTKeyStroke> getFocusTraversalKeys_NoIDCheck(int paramInt)
  {
    Set<AWTKeyStroke> localSet = focusTraversalKeys != null ? focusTraversalKeys[paramInt] : null;
    if (localSet != null) {
      return localSet;
    }
    Container localContainer = parent;
    if (localContainer != null) {
      return localContainer.getFocusTraversalKeys(paramInt);
    }
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(paramInt);
  }
  
  public boolean areFocusTraversalKeysSet(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 3)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    return (focusTraversalKeys != null) && (focusTraversalKeys[paramInt] != null);
  }
  
  public void setFocusTraversalKeysEnabled(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = focusTraversalKeysEnabled;
      focusTraversalKeysEnabled = paramBoolean;
    }
    firePropertyChange("focusTraversalKeysEnabled", bool, paramBoolean);
  }
  
  public boolean getFocusTraversalKeysEnabled()
  {
    return focusTraversalKeysEnabled;
  }
  
  public void requestFocus()
  {
    requestFocusHelper(false, true);
  }
  
  boolean requestFocus(CausedFocusEvent.Cause paramCause)
  {
    return requestFocusHelper(false, true, paramCause);
  }
  
  protected boolean requestFocus(boolean paramBoolean)
  {
    return requestFocusHelper(paramBoolean, true);
  }
  
  boolean requestFocus(boolean paramBoolean, CausedFocusEvent.Cause paramCause)
  {
    return requestFocusHelper(paramBoolean, true, paramCause);
  }
  
  public boolean requestFocusInWindow()
  {
    return requestFocusHelper(false, false);
  }
  
  boolean requestFocusInWindow(CausedFocusEvent.Cause paramCause)
  {
    return requestFocusHelper(false, false, paramCause);
  }
  
  protected boolean requestFocusInWindow(boolean paramBoolean)
  {
    return requestFocusHelper(paramBoolean, false);
  }
  
  boolean requestFocusInWindow(boolean paramBoolean, CausedFocusEvent.Cause paramCause)
  {
    return requestFocusHelper(paramBoolean, false, paramCause);
  }
  
  final boolean requestFocusHelper(boolean paramBoolean1, boolean paramBoolean2)
  {
    return requestFocusHelper(paramBoolean1, paramBoolean2, CausedFocusEvent.Cause.UNKNOWN);
  }
  
  final boolean requestFocusHelper(boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause)
  {
    AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
    if (((localAWTEvent instanceof MouseEvent)) && (SunToolkit.isSystemGenerated(localAWTEvent)))
    {
      localObject = ((MouseEvent)localAWTEvent).getComponent();
      if ((localObject == null) || (((Component)localObject).getContainingWindow() == getContainingWindow()))
      {
        focusLog.finest("requesting focus by mouse event \"in window\"");
        paramBoolean2 = false;
      }
    }
    if (!isRequestFocusAccepted(paramBoolean1, paramBoolean2, paramCause))
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("requestFocus is not accepted");
      }
      return false;
    }
    KeyboardFocusManager.setMostRecentFocusOwner(this);
    for (Object localObject = this; (localObject != null) && (!(localObject instanceof Window)); localObject = parent) {
      if (!((Component)localObject).isVisible())
      {
        if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
          focusLog.finest("component is recurively invisible");
        }
        return false;
      }
    }
    ComponentPeer localComponentPeer = peer;
    Component localComponent = (localComponentPeer instanceof LightweightPeer) ? getNativeContainer() : this;
    if ((localComponent == null) || (!localComponent.isVisible()))
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("Component is not a part of visible hierarchy");
      }
      return false;
    }
    localComponentPeer = peer;
    if (localComponentPeer == null)
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("Peer is null");
      }
      return false;
    }
    long l = 0L;
    if (EventQueue.isDispatchThread()) {
      l = Toolkit.getEventQueue().getMostRecentKeyEventTime();
    } else {
      l = System.currentTimeMillis();
    }
    boolean bool = localComponentPeer.requestFocus(this, paramBoolean1, paramBoolean2, l, paramCause);
    if (!bool)
    {
      KeyboardFocusManager.getCurrentKeyboardFocusManager(appContext).dequeueKeyEvents(l, this);
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("Peer request failed");
      }
    }
    else if (focusLog.isLoggable(PlatformLogger.Level.FINEST))
    {
      focusLog.finest("Pass for " + this);
    }
    return bool;
  }
  
  private boolean isRequestFocusAccepted(boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause)
  {
    if ((!isFocusable()) || (!isVisible()))
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("Not focusable or not visible");
      }
      return false;
    }
    ComponentPeer localComponentPeer = peer;
    if (localComponentPeer == null)
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("peer is null");
      }
      return false;
    }
    Window localWindow = getContainingWindow();
    if ((localWindow == null) || (!localWindow.isFocusableWindow()))
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("Component doesn't have toplevel");
      }
      return false;
    }
    Component localComponent = KeyboardFocusManager.getMostRecentFocusOwner(localWindow);
    if (localComponent == null)
    {
      localComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      if ((localComponent != null) && (localComponent.getContainingWindow() != localWindow)) {
        localComponent = null;
      }
    }
    if ((localComponent == this) || (localComponent == null))
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("focus owner is null or this");
      }
      return true;
    }
    if (CausedFocusEvent.Cause.ACTIVATION == paramCause)
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
        focusLog.finest("cause is activation");
      }
      return true;
    }
    boolean bool = requestFocusController.acceptRequestFocus(localComponent, this, paramBoolean1, paramBoolean2, paramCause);
    if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
      focusLog.finest("RequestFocusController returns {0}", new Object[] { Boolean.valueOf(bool) });
    }
    return bool;
  }
  
  static synchronized void setRequestFocusController(RequestFocusController paramRequestFocusController)
  {
    if (paramRequestFocusController == null) {
      requestFocusController = new DummyRequestFocusController(null);
    } else {
      requestFocusController = paramRequestFocusController;
    }
  }
  
  public Container getFocusCycleRootAncestor()
  {
    for (Container localContainer = parent; (localContainer != null) && (!localContainer.isFocusCycleRoot()); localContainer = parent) {}
    return localContainer;
  }
  
  public boolean isFocusCycleRoot(Container paramContainer)
  {
    Container localContainer = getFocusCycleRootAncestor();
    return localContainer == paramContainer;
  }
  
  Container getTraversalRoot()
  {
    return getFocusCycleRootAncestor();
  }
  
  public void transferFocus()
  {
    nextFocus();
  }
  
  @Deprecated
  public void nextFocus()
  {
    transferFocus(false);
  }
  
  boolean transferFocus(boolean paramBoolean)
  {
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("clearOnFailure = " + paramBoolean);
    }
    Component localComponent = getNextFocusCandidate();
    boolean bool = false;
    if ((localComponent != null) && (!localComponent.isFocusOwner()) && (localComponent != this)) {
      bool = localComponent.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_FORWARD);
    }
    if ((paramBoolean) && (!bool))
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
        focusLog.finer("clear global focus owner");
      }
      KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
    }
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("returning result: " + bool);
    }
    return bool;
  }
  
  final Component getNextFocusCandidate()
  {
    Container localContainer = getTraversalRoot();
    Object localObject1 = this;
    while ((localContainer != null) && ((!localContainer.isShowing()) || (!localContainer.canBeFocusOwner())))
    {
      localObject1 = localContainer;
      localContainer = ((Component)localObject1).getFocusCycleRootAncestor();
    }
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("comp = " + localObject1 + ", root = " + localContainer);
    }
    Object localObject2 = null;
    if (localContainer != null)
    {
      FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
      Object localObject3 = localFocusTraversalPolicy.getComponentAfter(localContainer, (Component)localObject1);
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
        focusLog.finer("component after is " + localObject3);
      }
      if (localObject3 == null)
      {
        localObject3 = localFocusTraversalPolicy.getDefaultComponent(localContainer);
        if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
          focusLog.finer("default component is " + localObject3);
        }
      }
      if (localObject3 == null)
      {
        Applet localApplet = EmbeddedFrame.getAppletIfAncestorOf(this);
        if (localApplet != null) {
          localObject3 = localApplet;
        }
      }
      localObject2 = localObject3;
    }
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("Focus transfer candidate: " + localObject2);
    }
    return (Component)localObject2;
  }
  
  public void transferFocusBackward()
  {
    transferFocusBackward(false);
  }
  
  boolean transferFocusBackward(boolean paramBoolean)
  {
    Container localContainer = getTraversalRoot();
    Object localObject = this;
    while ((localContainer != null) && ((!localContainer.isShowing()) || (!localContainer.canBeFocusOwner())))
    {
      localObject = localContainer;
      localContainer = ((Component)localObject).getFocusCycleRootAncestor();
    }
    boolean bool = false;
    if (localContainer != null)
    {
      FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
      Component localComponent = localFocusTraversalPolicy.getComponentBefore(localContainer, (Component)localObject);
      if (localComponent == null) {
        localComponent = localFocusTraversalPolicy.getDefaultComponent(localContainer);
      }
      if (localComponent != null) {
        bool = localComponent.requestFocusInWindow(CausedFocusEvent.Cause.TRAVERSAL_BACKWARD);
      }
    }
    if ((paramBoolean) && (!bool))
    {
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
        focusLog.finer("clear global focus owner");
      }
      KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
    }
    if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
      focusLog.finer("returning result: " + bool);
    }
    return bool;
  }
  
  public void transferFocusUpCycle()
  {
    for (Container localContainer = getFocusCycleRootAncestor(); (localContainer != null) && ((!localContainer.isShowing()) || (!localContainer.isFocusable()) || (!localContainer.isEnabled())); localContainer = localContainer.getFocusCycleRootAncestor()) {}
    Object localObject1;
    Object localObject2;
    if (localContainer != null)
    {
      localObject1 = localContainer.getFocusCycleRootAncestor();
      localObject2 = localObject1 != null ? localObject1 : localContainer;
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv((Container)localObject2);
      localContainer.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
    }
    else
    {
      localObject1 = getContainingWindow();
      if (localObject1 != null)
      {
        localObject2 = ((Window)localObject1).getFocusTraversalPolicy().getDefaultComponent((Container)localObject1);
        if (localObject2 != null)
        {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv((Container)localObject1);
          ((Component)localObject2).requestFocus(CausedFocusEvent.Cause.TRAVERSAL_UP);
        }
      }
    }
  }
  
  public boolean hasFocus()
  {
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this;
  }
  
  public boolean isFocusOwner()
  {
    return hasFocus();
  }
  
  void setAutoFocusTransferOnDisposal(boolean paramBoolean)
  {
    autoFocusTransferOnDisposal = paramBoolean;
  }
  
  boolean isAutoFocusTransferOnDisposal()
  {
    return autoFocusTransferOnDisposal;
  }
  
  public void add(PopupMenu paramPopupMenu)
  {
    synchronized (getTreeLock())
    {
      if (parent != null) {
        parent.remove(paramPopupMenu);
      }
      if (popups == null) {
        popups = new Vector();
      }
      popups.addElement(paramPopupMenu);
      parent = this;
      if ((peer != null) && (peer == null)) {
        paramPopupMenu.addNotify();
      }
    }
  }
  
  public void remove(MenuComponent paramMenuComponent)
  {
    synchronized (getTreeLock())
    {
      if (popups == null) {
        return;
      }
      int i = popups.indexOf(paramMenuComponent);
      if (i >= 0)
      {
        PopupMenu localPopupMenu = (PopupMenu)paramMenuComponent;
        if (peer != null) {
          localPopupMenu.removeNotify();
        }
        parent = null;
        popups.removeElementAt(i);
        if (popups.size() == 0) {
          popups = null;
        }
      }
    }
  }
  
  protected String paramString()
  {
    String str1 = Objects.toString(getName(), "");
    String str2 = isValid() ? "" : ",invalid";
    String str3 = visible ? "" : ",hidden";
    String str4 = enabled ? "" : ",disabled";
    return str1 + ',' + x + ',' + y + ',' + width + 'x' + height + str2 + str3 + str4;
  }
  
  public String toString()
  {
    return getClass().getName() + '[' + paramString() + ']';
  }
  
  public void list()
  {
    list(System.out, 0);
  }
  
  public void list(PrintStream paramPrintStream)
  {
    list(paramPrintStream, 0);
  }
  
  public void list(PrintStream paramPrintStream, int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      paramPrintStream.print(" ");
    }
    paramPrintStream.println(this);
  }
  
  public void list(PrintWriter paramPrintWriter)
  {
    list(paramPrintWriter, 0);
  }
  
  public void list(PrintWriter paramPrintWriter, int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      paramPrintWriter.print(" ");
    }
    paramPrintWriter.println(this);
  }
  
  final Container getNativeContainer()
  {
    for (Container localContainer = getContainer(); (localContainer != null) && ((peer instanceof LightweightPeer)); localContainer = localContainer.getContainer()) {}
    return localContainer;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (getObjectLock())
    {
      if (paramPropertyChangeListener == null) {
        return;
      }
      if (changeSupport == null) {
        changeSupport = new PropertyChangeSupport(this);
      }
      changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (getObjectLock())
    {
      if ((paramPropertyChangeListener == null) || (changeSupport == null)) {
        return;
      }
      changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    synchronized (getObjectLock())
    {
      if (changeSupport == null) {
        return new PropertyChangeListener[0];
      }
      return changeSupport.getPropertyChangeListeners();
    }
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (getObjectLock())
    {
      if (paramPropertyChangeListener == null) {
        return;
      }
      if (changeSupport == null) {
        changeSupport = new PropertyChangeSupport(this);
      }
      changeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
    }
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (getObjectLock())
    {
      if ((paramPropertyChangeListener == null) || (changeSupport == null)) {
        return;
      }
      changeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
    }
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString)
  {
    synchronized (getObjectLock())
    {
      if (changeSupport == null) {
        return new PropertyChangeListener[0];
      }
      return changeSupport.getPropertyChangeListeners(paramString);
    }
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    PropertyChangeSupport localPropertyChangeSupport;
    synchronized (getObjectLock())
    {
      localPropertyChangeSupport = changeSupport;
    }
    if ((localPropertyChangeSupport == null) || ((paramObject1 != null) && (paramObject2 != null) && (paramObject1.equals(paramObject2)))) {
      return;
    }
    localPropertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  protected void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    PropertyChangeSupport localPropertyChangeSupport = changeSupport;
    if ((localPropertyChangeSupport == null) || (paramBoolean1 == paramBoolean2)) {
      return;
    }
    localPropertyChangeSupport.firePropertyChange(paramString, paramBoolean1, paramBoolean2);
  }
  
  protected void firePropertyChange(String paramString, int paramInt1, int paramInt2)
  {
    PropertyChangeSupport localPropertyChangeSupport = changeSupport;
    if ((localPropertyChangeSupport == null) || (paramInt1 == paramInt2)) {
      return;
    }
    localPropertyChangeSupport.firePropertyChange(paramString, paramInt1, paramInt2);
  }
  
  public void firePropertyChange(String paramString, byte paramByte1, byte paramByte2)
  {
    if ((changeSupport == null) || (paramByte1 == paramByte2)) {
      return;
    }
    firePropertyChange(paramString, Byte.valueOf(paramByte1), Byte.valueOf(paramByte2));
  }
  
  public void firePropertyChange(String paramString, char paramChar1, char paramChar2)
  {
    if ((changeSupport == null) || (paramChar1 == paramChar2)) {
      return;
    }
    firePropertyChange(paramString, new Character(paramChar1), new Character(paramChar2));
  }
  
  public void firePropertyChange(String paramString, short paramShort1, short paramShort2)
  {
    if ((changeSupport == null) || (paramShort1 == paramShort2)) {
      return;
    }
    firePropertyChange(paramString, Short.valueOf(paramShort1), Short.valueOf(paramShort2));
  }
  
  public void firePropertyChange(String paramString, long paramLong1, long paramLong2)
  {
    if ((changeSupport == null) || (paramLong1 == paramLong2)) {
      return;
    }
    firePropertyChange(paramString, Long.valueOf(paramLong1), Long.valueOf(paramLong2));
  }
  
  public void firePropertyChange(String paramString, float paramFloat1, float paramFloat2)
  {
    if ((changeSupport == null) || (paramFloat1 == paramFloat2)) {
      return;
    }
    firePropertyChange(paramString, Float.valueOf(paramFloat1), Float.valueOf(paramFloat2));
  }
  
  public void firePropertyChange(String paramString, double paramDouble1, double paramDouble2)
  {
    if ((changeSupport == null) || (paramDouble1 == paramDouble2)) {
      return;
    }
    firePropertyChange(paramString, Double.valueOf(paramDouble1), Double.valueOf(paramDouble2));
  }
  
  private void doSwingSerialization()
  {
    Package localPackage = Package.getPackage("javax.swing");
    for (Class localClass1 = getClass(); localClass1 != null; localClass1 = localClass1.getSuperclass()) {
      if ((localClass1.getPackage() == localPackage) && (localClass1.getClassLoader() == null))
      {
        final Class localClass2 = localClass1;
        Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction()
        {
          public Method[] run()
          {
            return localClass2.getDeclaredMethods();
          }
        });
        for (int i = arrayOfMethod.length - 1; i >= 0; i--)
        {
          final Method localMethod = arrayOfMethod[i];
          if (localMethod.getName().equals("compWriteObjectNotify"))
          {
            AccessController.doPrivileged(new PrivilegedAction()
            {
              public Void run()
              {
                localMethod.setAccessible(true);
                return null;
              }
            });
            try
            {
              localMethod.invoke(this, (Object[])null);
            }
            catch (IllegalAccessException localIllegalAccessException) {}catch (InvocationTargetException localInvocationTargetException) {}
            return;
          }
        }
      }
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    doSwingSerialization();
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "componentL", componentListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "focusL", focusListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "keyL", keyListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "mouseL", mouseListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "mouseMotionL", mouseMotionListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "inputMethodL", inputMethodListener);
    paramObjectOutputStream.writeObject(null);
    paramObjectOutputStream.writeObject(componentOrientation);
    AWTEventMulticaster.save(paramObjectOutputStream, "hierarchyL", hierarchyListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "hierarchyBoundsL", hierarchyBoundsListener);
    paramObjectOutputStream.writeObject(null);
    AWTEventMulticaster.save(paramObjectOutputStream, "mouseWheelL", mouseWheelListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    objectLock = new Object();
    acc = AccessController.getContext();
    paramObjectInputStream.defaultReadObject();
    appContext = AppContext.getAppContext();
    coalescingEnabled = checkCoalescing();
    if (componentSerializedDataVersion < 4)
    {
      focusable = true;
      isFocusTraversableOverridden = 0;
      initializeFocusTraversalKeys();
      focusTraversalKeysEnabled = true;
    }
    Object localObject1;
    while (null != (localObject1 = paramObjectInputStream.readObject()))
    {
      localObject2 = ((String)localObject1).intern();
      if ("componentL" == localObject2) {
        addComponentListener((ComponentListener)paramObjectInputStream.readObject());
      } else if ("focusL" == localObject2) {
        addFocusListener((FocusListener)paramObjectInputStream.readObject());
      } else if ("keyL" == localObject2) {
        addKeyListener((KeyListener)paramObjectInputStream.readObject());
      } else if ("mouseL" == localObject2) {
        addMouseListener((MouseListener)paramObjectInputStream.readObject());
      } else if ("mouseMotionL" == localObject2) {
        addMouseMotionListener((MouseMotionListener)paramObjectInputStream.readObject());
      } else if ("inputMethodL" == localObject2) {
        addInputMethodListener((InputMethodListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
    Object localObject2 = null;
    try
    {
      localObject2 = paramObjectInputStream.readObject();
    }
    catch (OptionalDataException localOptionalDataException1)
    {
      if (!eof) {
        throw localOptionalDataException1;
      }
    }
    if (localObject2 != null) {
      componentOrientation = ((ComponentOrientation)localObject2);
    } else {
      componentOrientation = ComponentOrientation.UNKNOWN;
    }
    try
    {
      while (null != (localObject1 = paramObjectInputStream.readObject()))
      {
        String str1 = ((String)localObject1).intern();
        if ("hierarchyL" == str1) {
          addHierarchyListener((HierarchyListener)paramObjectInputStream.readObject());
        } else if ("hierarchyBoundsL" == str1) {
          addHierarchyBoundsListener((HierarchyBoundsListener)paramObjectInputStream.readObject());
        } else {
          paramObjectInputStream.readObject();
        }
      }
    }
    catch (OptionalDataException localOptionalDataException2)
    {
      if (!eof) {
        throw localOptionalDataException2;
      }
    }
    try
    {
      while (null != (localObject1 = paramObjectInputStream.readObject()))
      {
        String str2 = ((String)localObject1).intern();
        if ("mouseWheelL" == str2) {
          addMouseWheelListener((MouseWheelListener)paramObjectInputStream.readObject());
        } else {
          paramObjectInputStream.readObject();
        }
      }
    }
    catch (OptionalDataException localOptionalDataException3)
    {
      if (!eof) {
        throw localOptionalDataException3;
      }
    }
    if (popups != null)
    {
      int i = popups.size();
      for (int j = 0; j < i; j++)
      {
        PopupMenu localPopupMenu = (PopupMenu)popups.elementAt(j);
        parent = this;
      }
    }
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    ComponentOrientation localComponentOrientation = componentOrientation;
    componentOrientation = paramComponentOrientation;
    firePropertyChange("componentOrientation", localComponentOrientation, paramComponentOrientation);
    invalidateIfValid();
  }
  
  public ComponentOrientation getComponentOrientation()
  {
    return componentOrientation;
  }
  
  public void applyComponentOrientation(ComponentOrientation paramComponentOrientation)
  {
    if (paramComponentOrientation == null) {
      throw new NullPointerException();
    }
    setComponentOrientation(paramComponentOrientation);
  }
  
  final boolean canBeFocusOwner()
  {
    return (isEnabled()) && (isDisplayable()) && (isVisible()) && (isFocusable());
  }
  
  final boolean canBeFocusOwnerRecursively()
  {
    if (!canBeFocusOwner()) {
      return false;
    }
    synchronized (getTreeLock())
    {
      if (parent != null) {
        return parent.canContainFocusOwner(this);
      }
    }
    return true;
  }
  
  final void relocateComponent()
  {
    synchronized (getTreeLock())
    {
      if (peer == null) {
        return;
      }
      int i = x;
      int j = y;
      for (Container localContainer = getContainer(); (localContainer != null) && (localContainer.isLightweight()); localContainer = localContainer.getContainer())
      {
        i += x;
        j += y;
      }
      peer.setBounds(i, j, width, height, 1);
    }
  }
  
  Window getContainingWindow()
  {
    return SunToolkit.getContainingWindow(this);
  }
  
  private static native void initIDs();
  
  public AccessibleContext getAccessibleContext()
  {
    return accessibleContext;
  }
  
  int getAccessibleIndexInParent()
  {
    synchronized (getTreeLock())
    {
      int i = -1;
      Container localContainer = getParent();
      if ((localContainer != null) && ((localContainer instanceof Accessible)))
      {
        Component[] arrayOfComponent = localContainer.getComponents();
        for (int j = 0; j < arrayOfComponent.length; j++)
        {
          if ((arrayOfComponent[j] instanceof Accessible)) {
            i++;
          }
          if (equals(arrayOfComponent[j])) {
            return i;
          }
        }
      }
      return -1;
    }
  }
  
  AccessibleStateSet getAccessibleStateSet()
  {
    synchronized (getTreeLock())
    {
      AccessibleStateSet localAccessibleStateSet = new AccessibleStateSet();
      if (isEnabled()) {
        localAccessibleStateSet.add(AccessibleState.ENABLED);
      }
      if (isFocusTraversable()) {
        localAccessibleStateSet.add(AccessibleState.FOCUSABLE);
      }
      if (isVisible()) {
        localAccessibleStateSet.add(AccessibleState.VISIBLE);
      }
      if (isShowing()) {
        localAccessibleStateSet.add(AccessibleState.SHOWING);
      }
      if (isFocusOwner()) {
        localAccessibleStateSet.add(AccessibleState.FOCUSED);
      }
      if ((this instanceof Accessible))
      {
        AccessibleContext localAccessibleContext1 = ((Accessible)this).getAccessibleContext();
        if (localAccessibleContext1 != null)
        {
          Accessible localAccessible = localAccessibleContext1.getAccessibleParent();
          if (localAccessible != null)
          {
            AccessibleContext localAccessibleContext2 = localAccessible.getAccessibleContext();
            if (localAccessibleContext2 != null)
            {
              AccessibleSelection localAccessibleSelection = localAccessibleContext2.getAccessibleSelection();
              if (localAccessibleSelection != null)
              {
                localAccessibleStateSet.add(AccessibleState.SELECTABLE);
                int i = localAccessibleContext1.getAccessibleIndexInParent();
                if ((i >= 0) && (localAccessibleSelection.isAccessibleChildSelected(i))) {
                  localAccessibleStateSet.add(AccessibleState.SELECTED);
                }
              }
            }
          }
        }
      }
      if ((isInstanceOf(this, "javax.swing.JComponent")) && (((JComponent)this).isOpaque())) {
        localAccessibleStateSet.add(AccessibleState.OPAQUE);
      }
      return localAccessibleStateSet;
    }
  }
  
  static boolean isInstanceOf(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return false;
    }
    if (paramString == null) {
      return false;
    }
    for (Class localClass = paramObject.getClass(); localClass != null; localClass = localClass.getSuperclass()) {
      if (localClass.getName().equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  final boolean areBoundsValid()
  {
    Container localContainer = getContainer();
    return (localContainer == null) || (localContainer.isValid()) || (localContainer.getLayout() == null);
  }
  
  void applyCompoundShape(Region paramRegion)
  {
    checkTreeLock();
    if (!areBoundsValid())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid());
      }
      return;
    }
    if (!isLightweight())
    {
      ComponentPeer localComponentPeer = getPeer();
      if (localComponentPeer != null)
      {
        if (paramRegion.isEmpty()) {
          paramRegion = Region.EMPTY_REGION;
        }
        if (paramRegion.equals(getNormalShape()))
        {
          if (compoundShape == null) {
            return;
          }
          compoundShape = null;
          localComponentPeer.applyShape(null);
        }
        else
        {
          if (paramRegion.equals(getAppliedShape())) {
            return;
          }
          compoundShape = paramRegion;
          Point localPoint = getLocationOnWindow();
          if (mixingLog.isLoggable(PlatformLogger.Level.FINER)) {
            mixingLog.fine("this = " + this + "; compAbsolute=" + localPoint + "; shape=" + paramRegion);
          }
          localComponentPeer.applyShape(paramRegion.getTranslatedRegion(-x, -y));
        }
      }
    }
  }
  
  private Region getAppliedShape()
  {
    checkTreeLock();
    return (compoundShape == null) || (isLightweight()) ? getNormalShape() : compoundShape;
  }
  
  Point getLocationOnWindow()
  {
    checkTreeLock();
    Point localPoint = getLocation();
    for (Container localContainer = getContainer(); (localContainer != null) && (!(localContainer instanceof Window)); localContainer = localContainer.getContainer())
    {
      x += localContainer.getX();
      y += localContainer.getY();
    }
    return localPoint;
  }
  
  final Region getNormalShape()
  {
    checkTreeLock();
    Point localPoint = getLocationOnWindow();
    return Region.getInstanceXYWH(x, y, getWidth(), getHeight());
  }
  
  Region getOpaqueShape()
  {
    checkTreeLock();
    if (mixingCutoutRegion != null) {
      return mixingCutoutRegion;
    }
    return getNormalShape();
  }
  
  final int getSiblingIndexAbove()
  {
    checkTreeLock();
    Container localContainer = getContainer();
    if (localContainer == null) {
      return -1;
    }
    int i = localContainer.getComponentZOrder(this) - 1;
    return i < 0 ? -1 : i;
  }
  
  final ComponentPeer getHWPeerAboveMe()
  {
    checkTreeLock();
    Container localContainer = getContainer();
    int i = getSiblingIndexAbove();
    while (localContainer != null)
    {
      for (int j = i; j > -1; j--)
      {
        Component localComponent = localContainer.getComponent(j);
        if ((localComponent != null) && (localComponent.isDisplayable()) && (!localComponent.isLightweight())) {
          return localComponent.getPeer();
        }
      }
      if (!localContainer.isLightweight()) {
        break;
      }
      i = localContainer.getSiblingIndexAbove();
      localContainer = localContainer.getContainer();
    }
    return null;
  }
  
  final int getSiblingIndexBelow()
  {
    checkTreeLock();
    Container localContainer = getContainer();
    if (localContainer == null) {
      return -1;
    }
    int i = localContainer.getComponentZOrder(this) + 1;
    return i >= localContainer.getComponentCount() ? -1 : i;
  }
  
  final boolean isNonOpaqueForMixing()
  {
    return (mixingCutoutRegion != null) && (mixingCutoutRegion.isEmpty());
  }
  
  private Region calculateCurrentShape()
  {
    checkTreeLock();
    Region localRegion = getNormalShape();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
      mixingLog.fine("this = " + this + "; normalShape=" + localRegion);
    }
    if (getContainer() != null)
    {
      Object localObject = this;
      for (Container localContainer = ((Component)localObject).getContainer(); localContainer != null; localContainer = localContainer.getContainer())
      {
        for (int i = ((Component)localObject).getSiblingIndexAbove(); i != -1; i--)
        {
          Component localComponent = localContainer.getComponent(i);
          if ((localComponent.isLightweight()) && (localComponent.isShowing())) {
            localRegion = localRegion.getDifference(localComponent.getOpaqueShape());
          }
        }
        if (!localContainer.isLightweight()) {
          break;
        }
        localRegion = localRegion.getIntersection(localContainer.getNormalShape());
        localObject = localContainer;
      }
    }
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
      mixingLog.fine("currentShape=" + localRegion);
    }
    return localRegion;
  }
  
  void applyCurrentShape()
  {
    checkTreeLock();
    if (!areBoundsValid())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid());
      }
      return;
    }
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
      mixingLog.fine("this = " + this);
    }
    applyCompoundShape(calculateCurrentShape());
  }
  
  final void subtractAndApplyShape(Region paramRegion)
  {
    checkTreeLock();
    if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
      mixingLog.fine("this = " + this + "; s=" + paramRegion);
    }
    applyCompoundShape(getAppliedShape().getDifference(paramRegion));
  }
  
  private final void applyCurrentShapeBelowMe()
  {
    checkTreeLock();
    Object localObject = getContainer();
    if ((localObject != null) && (((Container)localObject).isShowing()))
    {
      ((Container)localObject).recursiveApplyCurrentShape(getSiblingIndexBelow());
      for (Container localContainer = ((Container)localObject).getContainer(); (!((Container)localObject).isOpaque()) && (localContainer != null); localContainer = ((Container)localObject).getContainer())
      {
        localContainer.recursiveApplyCurrentShape(((Container)localObject).getSiblingIndexBelow());
        localObject = localContainer;
      }
    }
  }
  
  final void subtractAndApplyShapeBelowMe()
  {
    checkTreeLock();
    Object localObject = getContainer();
    if ((localObject != null) && (isShowing()))
    {
      Region localRegion = getOpaqueShape();
      ((Container)localObject).recursiveSubtractAndApplyShape(localRegion, getSiblingIndexBelow());
      for (Container localContainer = ((Container)localObject).getContainer(); (!((Container)localObject).isOpaque()) && (localContainer != null); localContainer = ((Container)localObject).getContainer())
      {
        localContainer.recursiveSubtractAndApplyShape(localRegion, ((Container)localObject).getSiblingIndexBelow());
        localObject = localContainer;
      }
    }
  }
  
  void mixOnShowing()
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this);
      }
      if (!isMixingNeeded()) {
        return;
      }
      if (isLightweight()) {
        subtractAndApplyShapeBelowMe();
      } else {
        applyCurrentShape();
      }
    }
  }
  
  void mixOnHiding(boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; isLightweight = " + paramBoolean);
      }
      if (!isMixingNeeded()) {
        return;
      }
      if (paramBoolean) {
        applyCurrentShapeBelowMe();
      }
    }
  }
  
  void mixOnReshaping()
  {
    synchronized (getTreeLock())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this);
      }
      if (!isMixingNeeded()) {
        return;
      }
      if (isLightweight()) {
        applyCurrentShapeBelowMe();
      } else {
        applyCurrentShape();
      }
    }
  }
  
  void mixOnZOrderChanging(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      int i = paramInt2 < paramInt1 ? 1 : 0;
      Container localContainer = getContainer();
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; oldZorder=" + paramInt1 + "; newZorder=" + paramInt2 + "; parent=" + localContainer);
      }
      if (!isMixingNeeded()) {
        return;
      }
      if (isLightweight())
      {
        if (i != 0)
        {
          if ((localContainer != null) && (isShowing())) {
            localContainer.recursiveSubtractAndApplyShape(getOpaqueShape(), getSiblingIndexBelow(), paramInt1);
          }
        }
        else if (localContainer != null) {
          localContainer.recursiveApplyCurrentShape(paramInt1, paramInt2);
        }
      }
      else if (i != 0)
      {
        applyCurrentShape();
      }
      else if (localContainer != null)
      {
        Region localRegion = getAppliedShape();
        for (int j = paramInt1; j < paramInt2; j++)
        {
          Component localComponent = localContainer.getComponent(j);
          if ((localComponent.isLightweight()) && (localComponent.isShowing())) {
            localRegion = localRegion.getDifference(localComponent.getOpaqueShape());
          }
        }
        applyCompoundShape(localRegion);
      }
    }
  }
  
  void mixOnValidating() {}
  
  final boolean isMixingNeeded()
  {
    if (SunToolkit.getSunAwtDisableMixing())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINEST)) {
        mixingLog.finest("this = " + this + "; Mixing disabled via sun.awt.disableMixing");
      }
      return false;
    }
    if (!areBoundsValid())
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; areBoundsValid = " + areBoundsValid());
      }
      return false;
    }
    Window localWindow = getContainingWindow();
    if (localWindow != null)
    {
      if ((!localWindow.hasHeavyweightDescendants()) || (!localWindow.hasLightweightDescendants()) || (localWindow.isDisposing()))
      {
        if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
          mixingLog.fine("containing window = " + localWindow + "; has h/w descendants = " + localWindow.hasHeavyweightDescendants() + "; has l/w descendants = " + localWindow.hasLightweightDescendants() + "; disposing = " + localWindow.isDisposing());
        }
        return false;
      }
    }
    else
    {
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
        mixingLog.fine("this = " + this + "; containing window is null");
      }
      return false;
    }
    return true;
  }
  
  void updateZOrder()
  {
    peer.setZOrder(getHWPeerAboveMe());
  }
  
  static
  {
    log = PlatformLogger.getLogger("java.awt.Component");
    eventLog = PlatformLogger.getLogger("java.awt.event.Component");
    focusLog = PlatformLogger.getLogger("java.awt.focus.Component");
    mixingLog = PlatformLogger.getLogger("java.awt.mixing.Component");
    focusTraversalKeyPropertyNames = new String[] { "forwardFocusTraversalKeys", "backwardFocusTraversalKeys", "upCycleFocusTraversalKeys", "downCycleFocusTraversalKeys" };
    LOCK = new AWTTreeLock();
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.image.incrementaldraw"));
    isInc = (str == null) || (str.equals("true"));
    str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.image.redrawrate"));
    incRate = str != null ? Integer.parseInt(str) : 100;
    AWTAccessor.setComponentAccessor(new AWTAccessor.ComponentAccessor()
    {
      public void setBackgroundEraseDisabled(Component paramAnonymousComponent, boolean paramAnonymousBoolean)
      {
        backgroundEraseDisabled = paramAnonymousBoolean;
      }
      
      public boolean getBackgroundEraseDisabled(Component paramAnonymousComponent)
      {
        return backgroundEraseDisabled;
      }
      
      public Rectangle getBounds(Component paramAnonymousComponent)
      {
        return new Rectangle(x, y, width, height);
      }
      
      public void setMixingCutoutShape(Component paramAnonymousComponent, Shape paramAnonymousShape)
      {
        Region localRegion = paramAnonymousShape == null ? null : Region.getInstance(paramAnonymousShape, null);
        synchronized (paramAnonymousComponent.getTreeLock())
        {
          int i = 0;
          int j = 0;
          if (!paramAnonymousComponent.isNonOpaqueForMixing()) {
            j = 1;
          }
          mixingCutoutRegion = localRegion;
          if (!paramAnonymousComponent.isNonOpaqueForMixing()) {
            i = 1;
          }
          if (paramAnonymousComponent.isMixingNeeded())
          {
            if (j != 0) {
              paramAnonymousComponent.mixOnHiding(paramAnonymousComponent.isLightweight());
            }
            if (i != 0) {
              paramAnonymousComponent.mixOnShowing();
            }
          }
        }
      }
      
      public void setGraphicsConfiguration(Component paramAnonymousComponent, GraphicsConfiguration paramAnonymousGraphicsConfiguration)
      {
        paramAnonymousComponent.setGraphicsConfiguration(paramAnonymousGraphicsConfiguration);
      }
      
      public boolean requestFocus(Component paramAnonymousComponent, CausedFocusEvent.Cause paramAnonymousCause)
      {
        return paramAnonymousComponent.requestFocus(paramAnonymousCause);
      }
      
      public boolean canBeFocusOwner(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.canBeFocusOwner();
      }
      
      public boolean isVisible(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.isVisible_NoClientCode();
      }
      
      public void setRequestFocusController(RequestFocusController paramAnonymousRequestFocusController)
      {
        Component.setRequestFocusController(paramAnonymousRequestFocusController);
      }
      
      public AppContext getAppContext(Component paramAnonymousComponent)
      {
        return appContext;
      }
      
      public void setAppContext(Component paramAnonymousComponent, AppContext paramAnonymousAppContext)
      {
        appContext = paramAnonymousAppContext;
      }
      
      public Container getParent(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.getParent_NoClientCode();
      }
      
      public void setParent(Component paramAnonymousComponent, Container paramAnonymousContainer)
      {
        parent = paramAnonymousContainer;
      }
      
      public void setSize(Component paramAnonymousComponent, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        width = paramAnonymousInt1;
        height = paramAnonymousInt2;
      }
      
      public Point getLocation(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.location_NoClientCode();
      }
      
      public void setLocation(Component paramAnonymousComponent, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        x = paramAnonymousInt1;
        y = paramAnonymousInt2;
      }
      
      public boolean isEnabled(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.isEnabledImpl();
      }
      
      public boolean isDisplayable(Component paramAnonymousComponent)
      {
        return peer != null;
      }
      
      public Cursor getCursor(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.getCursor_NoClientCode();
      }
      
      public ComponentPeer getPeer(Component paramAnonymousComponent)
      {
        return peer;
      }
      
      public void setPeer(Component paramAnonymousComponent, ComponentPeer paramAnonymousComponentPeer)
      {
        peer = paramAnonymousComponentPeer;
      }
      
      public boolean isLightweight(Component paramAnonymousComponent)
      {
        return peer instanceof LightweightPeer;
      }
      
      public boolean getIgnoreRepaint(Component paramAnonymousComponent)
      {
        return ignoreRepaint;
      }
      
      public int getWidth(Component paramAnonymousComponent)
      {
        return width;
      }
      
      public int getHeight(Component paramAnonymousComponent)
      {
        return height;
      }
      
      public int getX(Component paramAnonymousComponent)
      {
        return x;
      }
      
      public int getY(Component paramAnonymousComponent)
      {
        return y;
      }
      
      public Color getForeground(Component paramAnonymousComponent)
      {
        return foreground;
      }
      
      public Color getBackground(Component paramAnonymousComponent)
      {
        return background;
      }
      
      public void setBackground(Component paramAnonymousComponent, Color paramAnonymousColor)
      {
        background = paramAnonymousColor;
      }
      
      public Font getFont(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.getFont_NoClientCode();
      }
      
      public void processEvent(Component paramAnonymousComponent, AWTEvent paramAnonymousAWTEvent)
      {
        paramAnonymousComponent.processEvent(paramAnonymousAWTEvent);
      }
      
      public AccessControlContext getAccessControlContext(Component paramAnonymousComponent)
      {
        return paramAnonymousComponent.getAccessControlContext();
      }
      
      public void revalidateSynchronously(Component paramAnonymousComponent)
      {
        paramAnonymousComponent.revalidateSynchronously();
      }
    });
  }
  
  static class AWTTreeLock
  {
    AWTTreeLock() {}
  }
  
  protected abstract class AccessibleAWTComponent
    extends AccessibleContext
    implements Serializable, AccessibleComponent
  {
    private static final long serialVersionUID = 642321655757800191L;
    private volatile transient int propertyListenersCount = 0;
    protected ComponentListener accessibleAWTComponentHandler = null;
    protected FocusListener accessibleAWTFocusHandler = null;
    
    protected AccessibleAWTComponent() {}
    
    public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    {
      if (accessibleAWTComponentHandler == null) {
        accessibleAWTComponentHandler = new AccessibleAWTComponentHandler();
      }
      if (accessibleAWTFocusHandler == null) {
        accessibleAWTFocusHandler = new AccessibleAWTFocusHandler();
      }
      if (propertyListenersCount++ == 0)
      {
        addComponentListener(accessibleAWTComponentHandler);
        Component.this.addFocusListener(accessibleAWTFocusHandler);
      }
      super.addPropertyChangeListener(paramPropertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    {
      if (--propertyListenersCount == 0)
      {
        removeComponentListener(accessibleAWTComponentHandler);
        Component.this.removeFocusListener(accessibleAWTFocusHandler);
      }
      super.removePropertyChangeListener(paramPropertyChangeListener);
    }
    
    public String getAccessibleName()
    {
      return accessibleName;
    }
    
    public String getAccessibleDescription()
    {
      return accessibleDescription;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.AWT_COMPONENT;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      return Component.this.getAccessibleStateSet();
    }
    
    public Accessible getAccessibleParent()
    {
      if (accessibleParent != null) {
        return accessibleParent;
      }
      Container localContainer = getParent();
      if ((localContainer instanceof Accessible)) {
        return (Accessible)localContainer;
      }
      return null;
    }
    
    public int getAccessibleIndexInParent()
    {
      return Component.this.getAccessibleIndexInParent();
    }
    
    public int getAccessibleChildrenCount()
    {
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      return null;
    }
    
    public Locale getLocale()
    {
      return Component.this.getLocale();
    }
    
    public AccessibleComponent getAccessibleComponent()
    {
      return this;
    }
    
    public Color getBackground()
    {
      return Component.this.getBackground();
    }
    
    public void setBackground(Color paramColor)
    {
      Component.this.setBackground(paramColor);
    }
    
    public Color getForeground()
    {
      return Component.this.getForeground();
    }
    
    public void setForeground(Color paramColor)
    {
      Component.this.setForeground(paramColor);
    }
    
    public Cursor getCursor()
    {
      return Component.this.getCursor();
    }
    
    public void setCursor(Cursor paramCursor)
    {
      Component.this.setCursor(paramCursor);
    }
    
    public Font getFont()
    {
      return Component.this.getFont();
    }
    
    public void setFont(Font paramFont)
    {
      Component.this.setFont(paramFont);
    }
    
    public FontMetrics getFontMetrics(Font paramFont)
    {
      if (paramFont == null) {
        return null;
      }
      return Component.this.getFontMetrics(paramFont);
    }
    
    public boolean isEnabled()
    {
      return Component.this.isEnabled();
    }
    
    public void setEnabled(boolean paramBoolean)
    {
      boolean bool = Component.this.isEnabled();
      Component.this.setEnabled(paramBoolean);
      if ((paramBoolean != bool) && (accessibleContext != null)) {
        if (paramBoolean) {
          accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.ENABLED);
        } else {
          accessibleContext.firePropertyChange("AccessibleState", AccessibleState.ENABLED, null);
        }
      }
    }
    
    public boolean isVisible()
    {
      return Component.this.isVisible();
    }
    
    public void setVisible(boolean paramBoolean)
    {
      boolean bool = Component.this.isVisible();
      Component.this.setVisible(paramBoolean);
      if ((paramBoolean != bool) && (accessibleContext != null)) {
        if (paramBoolean) {
          accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
        } else {
          accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
        }
      }
    }
    
    public boolean isShowing()
    {
      return Component.this.isShowing();
    }
    
    public boolean contains(Point paramPoint)
    {
      return Component.this.contains(paramPoint);
    }
    
    public Point getLocationOnScreen()
    {
      synchronized (getTreeLock())
      {
        if (Component.this.isShowing()) {
          return Component.this.getLocationOnScreen();
        }
        return null;
      }
    }
    
    public Point getLocation()
    {
      return Component.this.getLocation();
    }
    
    public void setLocation(Point paramPoint)
    {
      Component.this.setLocation(paramPoint);
    }
    
    public Rectangle getBounds()
    {
      return Component.this.getBounds();
    }
    
    public void setBounds(Rectangle paramRectangle)
    {
      Component.this.setBounds(paramRectangle);
    }
    
    public Dimension getSize()
    {
      return Component.this.getSize();
    }
    
    public void setSize(Dimension paramDimension)
    {
      Component.this.setSize(paramDimension);
    }
    
    public Accessible getAccessibleAt(Point paramPoint)
    {
      return null;
    }
    
    public boolean isFocusTraversable()
    {
      return Component.this.isFocusTraversable();
    }
    
    public void requestFocus()
    {
      Component.this.requestFocus();
    }
    
    public void addFocusListener(FocusListener paramFocusListener)
    {
      Component.this.addFocusListener(paramFocusListener);
    }
    
    public void removeFocusListener(FocusListener paramFocusListener)
    {
      Component.this.removeFocusListener(paramFocusListener);
    }
    
    protected class AccessibleAWTComponentHandler
      implements ComponentListener
    {
      protected AccessibleAWTComponentHandler() {}
      
      public void componentHidden(ComponentEvent paramComponentEvent)
      {
        if (accessibleContext != null) {
          accessibleContext.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
        }
      }
      
      public void componentShown(ComponentEvent paramComponentEvent)
      {
        if (accessibleContext != null) {
          accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
        }
      }
      
      public void componentMoved(ComponentEvent paramComponentEvent) {}
      
      public void componentResized(ComponentEvent paramComponentEvent) {}
    }
    
    protected class AccessibleAWTFocusHandler
      implements FocusListener
    {
      protected AccessibleAWTFocusHandler() {}
      
      public void focusGained(FocusEvent paramFocusEvent)
      {
        if (accessibleContext != null) {
          accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
        }
      }
      
      public void focusLost(FocusEvent paramFocusEvent)
      {
        if (accessibleContext != null) {
          accessibleContext.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null);
        }
      }
    }
  }
  
  public static enum BaselineResizeBehavior
  {
    CONSTANT_ASCENT,  CONSTANT_DESCENT,  CENTER_OFFSET,  OTHER;
    
    private BaselineResizeBehavior() {}
  }
  
  protected class BltBufferStrategy
    extends BufferStrategy
  {
    protected BufferCapabilities caps;
    protected VolatileImage[] backBuffers;
    protected boolean validatedContents;
    protected int width;
    protected int height;
    private Insets insets;
    
    protected BltBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
    {
      caps = paramBufferCapabilities;
      createBackBuffers(paramInt - 1);
    }
    
    public void dispose()
    {
      if (backBuffers != null) {
        for (int i = backBuffers.length - 1; i >= 0; i--) {
          if (backBuffers[i] != null)
          {
            backBuffers[i].flush();
            backBuffers[i] = null;
          }
        }
      }
      if (bufferStrategy == this) {
        bufferStrategy = null;
      }
    }
    
    protected void createBackBuffers(int paramInt)
    {
      if (paramInt == 0)
      {
        backBuffers = null;
      }
      else
      {
        width = getWidth();
        height = getHeight();
        insets = Component.this.getInsets_NoClientCode();
        int i = width - insets.left - insets.right;
        int j = height - insets.top - insets.bottom;
        i = Math.max(1, i);
        j = Math.max(1, j);
        if (backBuffers == null) {
          backBuffers = new VolatileImage[paramInt];
        } else {
          for (k = 0; k < paramInt; k++) {
            if (backBuffers[k] != null)
            {
              backBuffers[k].flush();
              backBuffers[k] = null;
            }
          }
        }
        for (int k = 0; k < paramInt; k++) {
          backBuffers[k] = createVolatileImage(i, j);
        }
      }
    }
    
    public BufferCapabilities getCapabilities()
    {
      return caps;
    }
    
    public Graphics getDrawGraphics()
    {
      revalidate();
      Image localImage = getBackBuffer();
      if (localImage == null) {
        return getGraphics();
      }
      SunGraphics2D localSunGraphics2D = (SunGraphics2D)localImage.getGraphics();
      localSunGraphics2D.constrain(-insets.left, -insets.top, localImage.getWidth(null) + insets.left, localImage.getHeight(null) + insets.top);
      return localSunGraphics2D;
    }
    
    Image getBackBuffer()
    {
      if (backBuffers != null) {
        return backBuffers[(backBuffers.length - 1)];
      }
      return null;
    }
    
    public void show()
    {
      showSubRegion(insets.left, insets.top, width - insets.right, height - insets.bottom);
    }
    
    void showSubRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (backBuffers == null) {
        return;
      }
      paramInt1 -= insets.left;
      paramInt3 -= insets.left;
      paramInt2 -= insets.top;
      paramInt4 -= insets.top;
      Graphics localGraphics = getGraphics_NoClientCode();
      if (localGraphics == null) {
        return;
      }
      try
      {
        localGraphics.translate(insets.left, insets.top);
        for (int i = 0; i < backBuffers.length; i++)
        {
          localGraphics.drawImage(backBuffers[i], paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt3, paramInt4, null);
          localGraphics.dispose();
          localGraphics = null;
          localGraphics = backBuffers[i].getGraphics();
        }
      }
      finally
      {
        if (localGraphics != null) {
          localGraphics.dispose();
        }
      }
    }
    
    protected void revalidate()
    {
      revalidate(true);
    }
    
    void revalidate(boolean paramBoolean)
    {
      validatedContents = false;
      if (backBuffers == null) {
        return;
      }
      if (paramBoolean)
      {
        localObject = Component.this.getInsets_NoClientCode();
        if ((getWidth() != width) || (getHeight() != height) || (!((Insets)localObject).equals(insets)))
        {
          createBackBuffers(backBuffers.length);
          validatedContents = true;
        }
      }
      Object localObject = getGraphicsConfiguration_NoClientCode();
      int i = backBuffers[(backBuffers.length - 1)].validate((GraphicsConfiguration)localObject);
      if (i == 2)
      {
        if (paramBoolean)
        {
          createBackBuffers(backBuffers.length);
          backBuffers[(backBuffers.length - 1)].validate((GraphicsConfiguration)localObject);
        }
        validatedContents = true;
      }
      else if (i == 1)
      {
        validatedContents = true;
      }
    }
    
    public boolean contentsLost()
    {
      if (backBuffers == null) {
        return false;
      }
      return backBuffers[(backBuffers.length - 1)].contentsLost();
    }
    
    public boolean contentsRestored()
    {
      return validatedContents;
    }
  }
  
  private class BltSubRegionBufferStrategy
    extends Component.BltBufferStrategy
    implements SubRegionShowable
  {
    protected BltSubRegionBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
    {
      super(paramInt, paramBufferCapabilities);
    }
    
    public void show(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public boolean showIfNotLost(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!contentsLost())
      {
        showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
        return !contentsLost();
      }
      return false;
    }
  }
  
  private static class DummyRequestFocusController
    implements RequestFocusController
  {
    private DummyRequestFocusController() {}
    
    public boolean acceptRequestFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause)
    {
      return true;
    }
  }
  
  protected class FlipBufferStrategy
    extends BufferStrategy
  {
    protected int numBuffers;
    protected BufferCapabilities caps;
    protected Image drawBuffer;
    protected VolatileImage drawVBuffer;
    protected boolean validatedContents;
    int width;
    int height;
    
    protected FlipBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
      throws AWTException
    {
      if ((!(Component.this instanceof Window)) && (!(Component.this instanceof Canvas))) {
        throw new ClassCastException("Component must be a Canvas or Window");
      }
      numBuffers = paramInt;
      caps = paramBufferCapabilities;
      createBuffers(paramInt, paramBufferCapabilities);
    }
    
    protected void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities)
      throws AWTException
    {
      if (paramInt < 2) {
        throw new IllegalArgumentException("Number of buffers cannot be less than two");
      }
      if (peer == null) {
        throw new IllegalStateException("Component must have a valid peer");
      }
      if ((paramBufferCapabilities == null) || (!paramBufferCapabilities.isPageFlipping())) {
        throw new IllegalArgumentException("Page flipping capabilities must be specified");
      }
      width = getWidth();
      height = getHeight();
      if (drawBuffer != null)
      {
        drawBuffer = null;
        drawVBuffer = null;
        destroyBuffers();
      }
      if ((paramBufferCapabilities instanceof ExtendedBufferCapabilities))
      {
        ExtendedBufferCapabilities localExtendedBufferCapabilities = (ExtendedBufferCapabilities)paramBufferCapabilities;
        if ((localExtendedBufferCapabilities.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON) && (!VSyncedBSManager.vsyncAllowed(this))) {
          paramBufferCapabilities = localExtendedBufferCapabilities.derive(ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT);
        }
      }
      peer.createBuffers(paramInt, paramBufferCapabilities);
      updateInternalBuffers();
    }
    
    private void updateInternalBuffers()
    {
      drawBuffer = getBackBuffer();
      if ((drawBuffer instanceof VolatileImage)) {
        drawVBuffer = ((VolatileImage)drawBuffer);
      } else {
        drawVBuffer = null;
      }
    }
    
    protected Image getBackBuffer()
    {
      if (peer != null) {
        return peer.getBackBuffer();
      }
      throw new IllegalStateException("Component must have a valid peer");
    }
    
    protected void flip(BufferCapabilities.FlipContents paramFlipContents)
    {
      if (peer != null)
      {
        Image localImage = getBackBuffer();
        if (localImage != null) {
          peer.flip(0, 0, localImage.getWidth(null), localImage.getHeight(null), paramFlipContents);
        }
      }
      else
      {
        throw new IllegalStateException("Component must have a valid peer");
      }
    }
    
    void flipSubRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents)
    {
      if (peer != null) {
        peer.flip(paramInt1, paramInt2, paramInt3, paramInt4, paramFlipContents);
      } else {
        throw new IllegalStateException("Component must have a valid peer");
      }
    }
    
    protected void destroyBuffers()
    {
      VSyncedBSManager.releaseVsync(this);
      if (peer != null) {
        peer.destroyBuffers();
      } else {
        throw new IllegalStateException("Component must have a valid peer");
      }
    }
    
    public BufferCapabilities getCapabilities()
    {
      if ((caps instanceof Component.ProxyCapabilities)) {
        return Component.ProxyCapabilities.access$300((Component.ProxyCapabilities)caps);
      }
      return caps;
    }
    
    public Graphics getDrawGraphics()
    {
      revalidate();
      return drawBuffer.getGraphics();
    }
    
    protected void revalidate()
    {
      revalidate(true);
    }
    
    void revalidate(boolean paramBoolean)
    {
      validatedContents = false;
      if ((paramBoolean) && ((getWidth() != width) || (getHeight() != height)))
      {
        try
        {
          createBuffers(numBuffers, caps);
        }
        catch (AWTException localAWTException1) {}
        validatedContents = true;
      }
      updateInternalBuffers();
      if (drawVBuffer != null)
      {
        GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration_NoClientCode();
        int i = drawVBuffer.validate(localGraphicsConfiguration);
        if (i == 2)
        {
          try
          {
            createBuffers(numBuffers, caps);
          }
          catch (AWTException localAWTException2) {}
          if (drawVBuffer != null) {
            drawVBuffer.validate(localGraphicsConfiguration);
          }
          validatedContents = true;
        }
        else if (i == 1)
        {
          validatedContents = true;
        }
      }
    }
    
    public boolean contentsLost()
    {
      if (drawVBuffer == null) {
        return false;
      }
      return drawVBuffer.contentsLost();
    }
    
    public boolean contentsRestored()
    {
      return validatedContents;
    }
    
    public void show()
    {
      flip(caps.getFlipContents());
    }
    
    void showSubRegion(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      flipSubRegion(paramInt1, paramInt2, paramInt3, paramInt4, caps.getFlipContents());
    }
    
    public void dispose()
    {
      if (bufferStrategy == this)
      {
        bufferStrategy = null;
        if (peer != null) {
          destroyBuffers();
        }
      }
    }
  }
  
  private class FlipSubRegionBufferStrategy
    extends Component.FlipBufferStrategy
    implements SubRegionShowable
  {
    protected FlipSubRegionBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
      throws AWTException
    {
      super(paramInt, paramBufferCapabilities);
    }
    
    public void show(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public boolean showIfNotLost(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!contentsLost())
      {
        showSubRegion(paramInt1, paramInt2, paramInt3, paramInt4);
        return !contentsLost();
      }
      return false;
    }
  }
  
  private class ProxyCapabilities
    extends ExtendedBufferCapabilities
  {
    private BufferCapabilities orig;
    
    private ProxyCapabilities(BufferCapabilities paramBufferCapabilities)
    {
      super(paramBufferCapabilities.getBackBufferCapabilities(), paramBufferCapabilities.getFlipContents() == BufferCapabilities.FlipContents.BACKGROUND ? BufferCapabilities.FlipContents.BACKGROUND : BufferCapabilities.FlipContents.COPIED);
      orig = paramBufferCapabilities;
    }
  }
  
  private class SingleBufferStrategy
    extends BufferStrategy
  {
    private BufferCapabilities caps;
    
    public SingleBufferStrategy(BufferCapabilities paramBufferCapabilities)
    {
      caps = paramBufferCapabilities;
    }
    
    public BufferCapabilities getCapabilities()
    {
      return caps;
    }
    
    public Graphics getDrawGraphics()
    {
      return getGraphics();
    }
    
    public boolean contentsLost()
    {
      return false;
    }
    
    public boolean contentsRestored()
    {
      return false;
    }
    
    public void show() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Component.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */