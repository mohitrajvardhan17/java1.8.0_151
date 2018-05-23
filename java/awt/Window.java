package java.awt;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Path2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.im.InputContext;
import java.awt.image.BufferStrategy;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.WindowAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.pipe.Region;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants.AWT;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class Window
  extends Container
  implements Accessible
{
  String warningString;
  transient List<Image> icons;
  private transient Component temporaryLostComponent;
  static boolean systemSyncLWRequests = false;
  boolean syncLWRequests = false;
  transient boolean beforeFirstShow = true;
  private transient boolean disposing = false;
  transient WindowDisposerRecord disposerRecord = null;
  static final int OPENED = 1;
  int state;
  private boolean alwaysOnTop;
  private static final IdentityArrayList<Window> allWindows = new IdentityArrayList();
  transient Vector<WeakReference<Window>> ownedWindowList = new Vector();
  private transient WeakReference<Window> weakThis;
  transient boolean showWithParent;
  transient Dialog modalBlocker;
  Dialog.ModalExclusionType modalExclusionType;
  transient WindowListener windowListener;
  transient WindowStateListener windowStateListener;
  transient WindowFocusListener windowFocusListener;
  transient InputContext inputContext;
  private transient Object inputContextLock = new Object();
  private FocusManager focusMgr;
  private boolean focusableWindowState = true;
  private volatile boolean autoRequestFocus = true;
  transient boolean isInShow = false;
  private volatile float opacity = 1.0F;
  private Shape shape = null;
  private static final String base = "win";
  private static int nameCounter = 0;
  private static final long serialVersionUID = 4497834738069338734L;
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Window");
  private static final boolean locationByPlatformProp;
  transient boolean isTrayIconWindow = false;
  private volatile transient int securityWarningWidth = 0;
  private volatile transient int securityWarningHeight = 0;
  private transient double securityWarningPointX = 2.0D;
  private transient double securityWarningPointY = 0.0D;
  private transient float securityWarningAlignmentX = 1.0F;
  private transient float securityWarningAlignmentY = 0.0F;
  transient Object anchor = new Object();
  private static final AtomicBoolean beforeFirstWindowShown;
  private Type type = Type.NORMAL;
  private int windowSerializedDataVersion = 2;
  private volatile boolean locationByPlatform = locationByPlatformProp;
  
  private static native void initIDs();
  
  Window(GraphicsConfiguration paramGraphicsConfiguration)
  {
    init(paramGraphicsConfiguration);
  }
  
  private GraphicsConfiguration initGC(GraphicsConfiguration paramGraphicsConfiguration)
  {
    
    if (paramGraphicsConfiguration == null) {
      paramGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    setGraphicsConfiguration(paramGraphicsConfiguration);
    return paramGraphicsConfiguration;
  }
  
  private void init(GraphicsConfiguration paramGraphicsConfiguration)
  {
    GraphicsEnvironment.checkHeadless();
    syncLWRequests = systemSyncLWRequests;
    weakThis = new WeakReference(this);
    addToWindowList();
    setWarningString();
    cursor = Cursor.getPredefinedCursor(0);
    visible = false;
    paramGraphicsConfiguration = initGC(paramGraphicsConfiguration);
    if (paramGraphicsConfiguration.getDevice().getType() != 0) {
      throw new IllegalArgumentException("not a screen device");
    }
    setLayout(new BorderLayout());
    Rectangle localRectangle = paramGraphicsConfiguration.getBounds();
    Insets localInsets = getToolkit().getScreenInsets(paramGraphicsConfiguration);
    int i = getX() + x + left;
    int j = getY() + y + top;
    if ((i != x) || (j != y))
    {
      setLocation(i, j);
      setLocationByPlatform(locationByPlatformProp);
    }
    modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    disposerRecord = new WindowDisposerRecord(appContext, this);
    Disposer.addRecord(anchor, disposerRecord);
    SunToolkit.checkAndSetPolicy(this);
  }
  
  Window()
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    init((GraphicsConfiguration)null);
  }
  
  public Window(Frame paramFrame)
  {
    this(paramFrame == null ? (GraphicsConfiguration)null : paramFrame.getGraphicsConfiguration());
    ownedInit(paramFrame);
  }
  
  public Window(Window paramWindow)
  {
    this(paramWindow == null ? (GraphicsConfiguration)null : paramWindow.getGraphicsConfiguration());
    ownedInit(paramWindow);
  }
  
  public Window(Window paramWindow, GraphicsConfiguration paramGraphicsConfiguration)
  {
    this(paramGraphicsConfiguration);
    ownedInit(paramWindow);
  }
  
  private void ownedInit(Window paramWindow)
  {
    parent = paramWindow;
    if (paramWindow != null)
    {
      paramWindow.addOwnedWindow(weakThis);
      if (paramWindow.isAlwaysOnTop()) {
        try
        {
          setAlwaysOnTop(true);
        }
        catch (SecurityException localSecurityException) {}
      }
    }
    disposerRecord.updateOwner();
  }
  
  /* Error */
  String constructComponentName()
  {
    // Byte code:
    //   0: ldc 8
    //   2: dup
    //   3: astore_1
    //   4: monitorenter
    //   5: new 684	java/lang/StringBuilder
    //   8: dup
    //   9: invokespecial 1552	java/lang/StringBuilder:<init>	()V
    //   12: ldc 7
    //   14: invokevirtual 1556	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   17: getstatic 1266	java/awt/Window:nameCounter	I
    //   20: dup
    //   21: iconst_1
    //   22: iadd
    //   23: putstatic 1266	java/awt/Window:nameCounter	I
    //   26: invokevirtual 1554	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   29: invokevirtual 1553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: aload_1
    //   33: monitorexit
    //   34: areturn
    //   35: astore_2
    //   36: aload_1
    //   37: monitorexit
    //   38: aload_2
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	Window
    //   3	34	1	Ljava/lang/Object;	Object
    //   35	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   5	34	35	finally
    //   35	38	35	finally
  }
  
  public List<Image> getIconImages()
  {
    List localList = icons;
    if ((localList == null) || (localList.size() == 0)) {
      return new ArrayList();
    }
    return new ArrayList(localList);
  }
  
  public synchronized void setIconImages(List<? extends Image> paramList)
  {
    icons = (paramList == null ? new ArrayList() : new ArrayList(paramList));
    WindowPeer localWindowPeer = (WindowPeer)peer;
    if (localWindowPeer != null) {
      localWindowPeer.updateIconImages();
    }
    firePropertyChange("iconImage", null, null);
  }
  
  public void setIconImage(Image paramImage)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramImage != null) {
      localArrayList.add(paramImage);
    }
    setIconImages(localArrayList);
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      Container localContainer = parent;
      if ((localContainer != null) && (localContainer.getPeer() == null)) {
        localContainer.addNotify();
      }
      if (peer == null) {
        peer = getToolkit().createWindow(this);
      }
      synchronized (allWindows)
      {
        allWindows.add(this);
      }
      super.addNotify();
    }
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      synchronized (allWindows)
      {
        allWindows.remove(this);
      }
      super.removeNotify();
    }
  }
  
  public void pack()
  {
    Container localContainer = parent;
    if ((localContainer != null) && (localContainer.getPeer() == null)) {
      localContainer.addNotify();
    }
    if (peer == null) {
      addNotify();
    }
    Dimension localDimension = getPreferredSize();
    if (peer != null) {
      setClientSize(width, height);
    }
    if (beforeFirstShow) {
      isPacked = true;
    }
    validateUnconditionally();
  }
  
  public void setMinimumSize(Dimension paramDimension)
  {
    synchronized (getTreeLock())
    {
      super.setMinimumSize(paramDimension);
      Dimension localDimension = getSize();
      if ((isMinimumSizeSet()) && ((width < width) || (height < height)))
      {
        int i = Math.max(width, width);
        int j = Math.max(height, height);
        setSize(i, j);
      }
      if (peer != null) {
        ((WindowPeer)peer).updateMinimumSize();
      }
    }
  }
  
  public void setSize(Dimension paramDimension)
  {
    super.setSize(paramDimension);
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    super.setSize(paramInt1, paramInt2);
  }
  
  public void setLocation(int paramInt1, int paramInt2)
  {
    super.setLocation(paramInt1, paramInt2);
  }
  
  public void setLocation(Point paramPoint)
  {
    super.setLocation(paramPoint);
  }
  
  @Deprecated
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (isMinimumSizeSet())
    {
      Dimension localDimension = getMinimumSize();
      if (paramInt3 < width) {
        paramInt3 = width;
      }
      if (paramInt4 < height) {
        paramInt4 = height;
      }
    }
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void setClientSize(int paramInt1, int paramInt2)
  {
    synchronized (getTreeLock())
    {
      setBoundsOp(4);
      setBounds(x, y, paramInt1, paramInt2);
    }
  }
  
  final void closeSplashScreen()
  {
    if (isTrayIconWindow) {
      return;
    }
    if (beforeFirstWindowShown.getAndSet(false))
    {
      SunToolkit.closeSplashScreen();
      SplashScreen.markClosed();
    }
  }
  
  public void setVisible(boolean paramBoolean)
  {
    super.setVisible(paramBoolean);
  }
  
  @Deprecated
  public void show()
  {
    if (peer == null) {
      addNotify();
    }
    validateUnconditionally();
    isInShow = true;
    if (visible)
    {
      toFront();
    }
    else
    {
      beforeFirstShow = false;
      closeSplashScreen();
      Dialog.checkShouldBeBlocked(this);
      super.show();
      locationByPlatform = false;
      for (int i = 0; i < ownedWindowList.size(); i++)
      {
        Window localWindow = (Window)((WeakReference)ownedWindowList.elementAt(i)).get();
        if ((localWindow != null) && (showWithParent))
        {
          localWindow.show();
          showWithParent = false;
        }
      }
      if (!isModalBlocked()) {
        updateChildrenBlocking();
      } else {
        modalBlocker.toFront_NoClientCode();
      }
      if (((this instanceof Frame)) || ((this instanceof Dialog))) {
        updateChildFocusableWindowState(this);
      }
    }
    isInShow = false;
    if ((state & 0x1) == 0)
    {
      postWindowEvent(200);
      state |= 0x1;
    }
  }
  
  static void updateChildFocusableWindowState(Window paramWindow)
  {
    if ((paramWindow.getPeer() != null) && (paramWindow.isShowing())) {
      ((WindowPeer)paramWindow.getPeer()).updateFocusableWindowState();
    }
    for (int i = 0; i < ownedWindowList.size(); i++)
    {
      Window localWindow = (Window)((WeakReference)ownedWindowList.elementAt(i)).get();
      if (localWindow != null) {
        updateChildFocusableWindowState(localWindow);
      }
    }
  }
  
  synchronized void postWindowEvent(int paramInt)
  {
    if ((windowListener != null) || ((eventMask & 0x40) != 0L) || (Toolkit.enabledOnToolkit(64L)))
    {
      WindowEvent localWindowEvent = new WindowEvent(this, paramInt);
      Toolkit.getEventQueue().postEvent(localWindowEvent);
    }
  }
  
  @Deprecated
  public void hide()
  {
    synchronized (ownedWindowList)
    {
      for (int i = 0; i < ownedWindowList.size(); i++)
      {
        Window localWindow = (Window)((WeakReference)ownedWindowList.elementAt(i)).get();
        if ((localWindow != null) && (visible))
        {
          localWindow.hide();
          showWithParent = true;
        }
      }
    }
    if (isModalBlocked()) {
      modalBlocker.unblockWindow(this);
    }
    super.hide();
    locationByPlatform = false;
  }
  
  final void clearMostRecentFocusOwnerOnHide() {}
  
  public void dispose()
  {
    doDispose();
  }
  
  void disposeImpl()
  {
    dispose();
    if (getPeer() != null) {
      doDispose();
    }
  }
  
  void doDispose()
  {
    boolean bool = isDisplayable();
    Runnable local1DisposeAction = new Runnable()
    {
      public void run()
      {
        disposing = true;
        try
        {
          GraphicsDevice localGraphicsDevice = getGraphicsConfiguration().getDevice();
          if (localGraphicsDevice.getFullScreenWindow() == Window.this) {
            localGraphicsDevice.setFullScreenWindow(null);
          }
          Object[] arrayOfObject;
          synchronized (ownedWindowList)
          {
            arrayOfObject = new Object[ownedWindowList.size()];
            ownedWindowList.copyInto(arrayOfObject);
          }
          for (??? = 0; ??? < arrayOfObject.length; ???++)
          {
            Window localWindow = (Window)((WeakReference)arrayOfObject[???]).get();
            if (localWindow != null) {
              localWindow.disposeImpl();
            }
          }
          hide();
          beforeFirstShow = true;
          removeNotify();
          synchronized (inputContextLock)
          {
            if (inputContext != null)
            {
              inputContext.dispose();
              inputContext = null;
            }
          }
          clearCurrentFocusCycleRootOnHide();
        }
        finally
        {
          disposing = false;
        }
      }
    };
    if (EventQueue.isDispatchThread()) {
      local1DisposeAction.run();
    } else {
      try
      {
        EventQueue.invokeAndWait(this, local1DisposeAction);
      }
      catch (InterruptedException localInterruptedException)
      {
        System.err.println("Disposal was interrupted:");
        localInterruptedException.printStackTrace();
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        System.err.println("Exception during disposal:");
        localInvocationTargetException.printStackTrace();
      }
    }
    if (bool) {
      postWindowEvent(202);
    }
  }
  
  void adjustListeningChildrenOnParent(long paramLong, int paramInt) {}
  
  void adjustDecendantsOnParent(int paramInt) {}
  
  public void toFront()
  {
    toFront_NoClientCode();
  }
  
  final void toFront_NoClientCode()
  {
    if (visible)
    {
      WindowPeer localWindowPeer = (WindowPeer)peer;
      if (localWindowPeer != null) {
        localWindowPeer.toFront();
      }
      if (isModalBlocked()) {
        modalBlocker.toFront_NoClientCode();
      }
    }
  }
  
  public void toBack()
  {
    toBack_NoClientCode();
  }
  
  final void toBack_NoClientCode()
  {
    if (isAlwaysOnTop()) {
      try
      {
        setAlwaysOnTop(false);
      }
      catch (SecurityException localSecurityException) {}
    }
    if (visible)
    {
      WindowPeer localWindowPeer = (WindowPeer)peer;
      if (localWindowPeer != null) {
        localWindowPeer.toBack();
      }
    }
  }
  
  public Toolkit getToolkit()
  {
    return Toolkit.getDefaultToolkit();
  }
  
  public final String getWarningString()
  {
    return warningString;
  }
  
  private void setWarningString()
  {
    warningString = null;
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkPermission(SecurityConstants.AWT.TOPLEVEL_WINDOW_PERMISSION);
      }
      catch (SecurityException localSecurityException)
      {
        warningString = ((String)AccessController.doPrivileged(new GetPropertyAction("awt.appletWarning", "Java Applet Window")));
      }
    }
  }
  
  public Locale getLocale()
  {
    if (locale == null) {
      return Locale.getDefault();
    }
    return locale;
  }
  
  public InputContext getInputContext()
  {
    synchronized (inputContextLock)
    {
      if (inputContext == null) {
        inputContext = InputContext.getInstance();
      }
    }
    return inputContext;
  }
  
  public void setCursor(Cursor paramCursor)
  {
    if (paramCursor == null) {
      paramCursor = Cursor.getPredefinedCursor(0);
    }
    super.setCursor(paramCursor);
  }
  
  public Window getOwner()
  {
    return getOwner_NoClientCode();
  }
  
  final Window getOwner_NoClientCode()
  {
    return (Window)parent;
  }
  
  public Window[] getOwnedWindows()
  {
    return getOwnedWindows_NoClientCode();
  }
  
  final Window[] getOwnedWindows_NoClientCode()
  {
    Window[] arrayOfWindow1;
    synchronized (ownedWindowList)
    {
      int i = ownedWindowList.size();
      int j = 0;
      Window[] arrayOfWindow2 = new Window[i];
      for (int k = 0; k < i; k++)
      {
        arrayOfWindow2[j] = ((Window)((WeakReference)ownedWindowList.elementAt(k)).get());
        if (arrayOfWindow2[j] != null) {
          j++;
        }
      }
      if (i != j) {
        arrayOfWindow1 = (Window[])Arrays.copyOf(arrayOfWindow2, j);
      } else {
        arrayOfWindow1 = arrayOfWindow2;
      }
    }
    return arrayOfWindow1;
  }
  
  boolean isModalBlocked()
  {
    return modalBlocker != null;
  }
  
  void setModalBlocked(Dialog paramDialog, boolean paramBoolean1, boolean paramBoolean2)
  {
    modalBlocker = (paramBoolean1 ? paramDialog : null);
    if (paramBoolean2)
    {
      WindowPeer localWindowPeer = (WindowPeer)peer;
      if (localWindowPeer != null) {
        localWindowPeer.setModalBlocked(paramDialog, paramBoolean1);
      }
    }
  }
  
  Dialog getModalBlocker()
  {
    return modalBlocker;
  }
  
  static IdentityArrayList<Window> getAllWindows()
  {
    synchronized (allWindows)
    {
      IdentityArrayList localIdentityArrayList = new IdentityArrayList();
      localIdentityArrayList.addAll(allWindows);
      return localIdentityArrayList;
    }
  }
  
  static IdentityArrayList<Window> getAllUnblockedWindows()
  {
    synchronized (allWindows)
    {
      IdentityArrayList localIdentityArrayList = new IdentityArrayList();
      for (int i = 0; i < allWindows.size(); i++)
      {
        Window localWindow = (Window)allWindows.get(i);
        if (!localWindow.isModalBlocked()) {
          localIdentityArrayList.add(localWindow);
        }
      }
      return localIdentityArrayList;
    }
  }
  
  private static Window[] getWindows(AppContext paramAppContext)
  {
    synchronized (Window.class)
    {
      Vector localVector = (Vector)paramAppContext.get(Window.class);
      Window[] arrayOfWindow1;
      if (localVector != null)
      {
        int i = localVector.size();
        int j = 0;
        Window[] arrayOfWindow2 = new Window[i];
        for (int k = 0; k < i; k++)
        {
          Window localWindow = (Window)((WeakReference)localVector.get(k)).get();
          if (localWindow != null) {
            arrayOfWindow2[(j++)] = localWindow;
          }
        }
        if (i != j) {
          arrayOfWindow1 = (Window[])Arrays.copyOf(arrayOfWindow2, j);
        } else {
          arrayOfWindow1 = arrayOfWindow2;
        }
      }
      else
      {
        arrayOfWindow1 = new Window[0];
      }
      return arrayOfWindow1;
    }
  }
  
  public static Window[] getWindows()
  {
    return getWindows(AppContext.getAppContext());
  }
  
  public static Window[] getOwnerlessWindows()
  {
    Window[] arrayOfWindow1 = getWindows();
    int i = 0;
    for (Window localWindow1 : arrayOfWindow1) {
      if (localWindow1.getOwner() == null) {
        i++;
      }
    }
    ??? = new Window[i];
    ??? = 0;
    for (Window localWindow2 : arrayOfWindow1) {
      if (localWindow2.getOwner() == null) {
        ???[(???++)] = localWindow2;
      }
    }
    return (Window[])???;
  }
  
  Window getDocumentRoot()
  {
    synchronized (getTreeLock())
    {
      for (Window localWindow = this; localWindow.getOwner() != null; localWindow = localWindow.getOwner()) {}
      return localWindow;
    }
  }
  
  public void setModalExclusionType(Dialog.ModalExclusionType paramModalExclusionType)
  {
    if (paramModalExclusionType == null) {
      paramModalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    }
    if (!Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(paramModalExclusionType)) {
      paramModalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    }
    if (modalExclusionType == paramModalExclusionType) {
      return;
    }
    if (paramModalExclusionType == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
      }
    }
    modalExclusionType = paramModalExclusionType;
  }
  
  public Dialog.ModalExclusionType getModalExclusionType()
  {
    return modalExclusionType;
  }
  
  boolean isModalExcluded(Dialog.ModalExclusionType paramModalExclusionType)
  {
    if ((modalExclusionType != null) && (modalExclusionType.compareTo(paramModalExclusionType) >= 0)) {
      return true;
    }
    Window localWindow = getOwner_NoClientCode();
    return (localWindow != null) && (localWindow.isModalExcluded(paramModalExclusionType));
  }
  
  void updateChildrenBlocking()
  {
    Vector localVector = new Vector();
    Window[] arrayOfWindow = getOwnedWindows();
    for (int i = 0; i < arrayOfWindow.length; i++) {
      localVector.add(arrayOfWindow[i]);
    }
    for (i = 0; i < localVector.size(); i++)
    {
      Window localWindow = (Window)localVector.get(i);
      if (localWindow.isVisible())
      {
        if (localWindow.isModalBlocked())
        {
          localObject = localWindow.getModalBlocker();
          ((Dialog)localObject).unblockWindow(localWindow);
        }
        Dialog.checkShouldBeBlocked(localWindow);
        Object localObject = localWindow.getOwnedWindows();
        for (int j = 0; j < localObject.length; j++) {
          localVector.add(localObject[j]);
        }
      }
    }
  }
  
  public synchronized void addWindowListener(WindowListener paramWindowListener)
  {
    if (paramWindowListener == null) {
      return;
    }
    newEventsOnly = true;
    windowListener = AWTEventMulticaster.add(windowListener, paramWindowListener);
  }
  
  public synchronized void addWindowStateListener(WindowStateListener paramWindowStateListener)
  {
    if (paramWindowStateListener == null) {
      return;
    }
    windowStateListener = AWTEventMulticaster.add(windowStateListener, paramWindowStateListener);
    newEventsOnly = true;
  }
  
  public synchronized void addWindowFocusListener(WindowFocusListener paramWindowFocusListener)
  {
    if (paramWindowFocusListener == null) {
      return;
    }
    windowFocusListener = AWTEventMulticaster.add(windowFocusListener, paramWindowFocusListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeWindowListener(WindowListener paramWindowListener)
  {
    if (paramWindowListener == null) {
      return;
    }
    windowListener = AWTEventMulticaster.remove(windowListener, paramWindowListener);
  }
  
  public synchronized void removeWindowStateListener(WindowStateListener paramWindowStateListener)
  {
    if (paramWindowStateListener == null) {
      return;
    }
    windowStateListener = AWTEventMulticaster.remove(windowStateListener, paramWindowStateListener);
  }
  
  public synchronized void removeWindowFocusListener(WindowFocusListener paramWindowFocusListener)
  {
    if (paramWindowFocusListener == null) {
      return;
    }
    windowFocusListener = AWTEventMulticaster.remove(windowFocusListener, paramWindowFocusListener);
  }
  
  public synchronized WindowListener[] getWindowListeners()
  {
    return (WindowListener[])getListeners(WindowListener.class);
  }
  
  public synchronized WindowFocusListener[] getWindowFocusListeners()
  {
    return (WindowFocusListener[])getListeners(WindowFocusListener.class);
  }
  
  public synchronized WindowStateListener[] getWindowStateListeners()
  {
    return (WindowStateListener[])getListeners(WindowStateListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    Object localObject = null;
    if (paramClass == WindowFocusListener.class) {
      localObject = windowFocusListener;
    } else if (paramClass == WindowStateListener.class) {
      localObject = windowStateListener;
    } else if (paramClass == WindowListener.class) {
      localObject = windowListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners((EventListener)localObject, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    switch (id)
    {
    case 200: 
    case 201: 
    case 202: 
    case 203: 
    case 204: 
    case 205: 
    case 206: 
      return ((eventMask & 0x40) != 0L) || (windowListener != null);
    case 207: 
    case 208: 
      return ((eventMask & 0x80000) != 0L) || (windowFocusListener != null);
    case 209: 
      return ((eventMask & 0x40000) != 0L) || (windowStateListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof WindowEvent))
    {
      switch (paramAWTEvent.getID())
      {
      case 200: 
      case 201: 
      case 202: 
      case 203: 
      case 204: 
      case 205: 
      case 206: 
        processWindowEvent((WindowEvent)paramAWTEvent);
        break;
      case 207: 
      case 208: 
        processWindowFocusEvent((WindowEvent)paramAWTEvent);
        break;
      case 209: 
        processWindowStateEvent((WindowEvent)paramAWTEvent);
      }
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processWindowEvent(WindowEvent paramWindowEvent)
  {
    WindowListener localWindowListener = windowListener;
    if (localWindowListener != null) {
      switch (paramWindowEvent.getID())
      {
      case 200: 
        localWindowListener.windowOpened(paramWindowEvent);
        break;
      case 201: 
        localWindowListener.windowClosing(paramWindowEvent);
        break;
      case 202: 
        localWindowListener.windowClosed(paramWindowEvent);
        break;
      case 203: 
        localWindowListener.windowIconified(paramWindowEvent);
        break;
      case 204: 
        localWindowListener.windowDeiconified(paramWindowEvent);
        break;
      case 205: 
        localWindowListener.windowActivated(paramWindowEvent);
        break;
      case 206: 
        localWindowListener.windowDeactivated(paramWindowEvent);
        break;
      }
    }
  }
  
  protected void processWindowFocusEvent(WindowEvent paramWindowEvent)
  {
    WindowFocusListener localWindowFocusListener = windowFocusListener;
    if (localWindowFocusListener != null) {
      switch (paramWindowEvent.getID())
      {
      case 207: 
        localWindowFocusListener.windowGainedFocus(paramWindowEvent);
        break;
      case 208: 
        localWindowFocusListener.windowLostFocus(paramWindowEvent);
        break;
      }
    }
  }
  
  protected void processWindowStateEvent(WindowEvent paramWindowEvent)
  {
    WindowStateListener localWindowStateListener = windowStateListener;
    if (localWindowStateListener != null) {
      switch (paramWindowEvent.getID())
      {
      case 209: 
        localWindowStateListener.windowStateChanged(paramWindowEvent);
        break;
      }
    }
  }
  
  void preProcessKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.isActionKey()) && (paramKeyEvent.getKeyCode() == 112) && (paramKeyEvent.isControlDown()) && (paramKeyEvent.isShiftDown()) && (paramKeyEvent.getID() == 401)) {
      list(System.out, 0);
    }
  }
  
  void postProcessKeyEvent(KeyEvent paramKeyEvent) {}
  
  public final void setAlwaysOnTop(boolean paramBoolean)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
    }
    boolean bool;
    synchronized (this)
    {
      bool = alwaysOnTop;
      alwaysOnTop = paramBoolean;
    }
    if (bool != paramBoolean)
    {
      if (isAlwaysOnTopSupported())
      {
        ??? = (WindowPeer)peer;
        synchronized (getTreeLock())
        {
          if (??? != null) {
            ((WindowPeer)???).updateAlwaysOnTopState();
          }
        }
      }
      firePropertyChange("alwaysOnTop", bool, paramBoolean);
    }
    setOwnedWindowsAlwaysOnTop(paramBoolean);
  }
  
  private void setOwnedWindowsAlwaysOnTop(boolean paramBoolean)
  {
    WeakReference[] arrayOfWeakReference;
    synchronized (ownedWindowList)
    {
      arrayOfWeakReference = new WeakReference[ownedWindowList.size()];
      ownedWindowList.copyInto(arrayOfWeakReference);
    }
    for (Object localObject2 : arrayOfWeakReference)
    {
      Window localWindow = (Window)((WeakReference)localObject2).get();
      if (localWindow != null) {
        try
        {
          localWindow.setAlwaysOnTop(paramBoolean);
        }
        catch (SecurityException localSecurityException) {}
      }
    }
  }
  
  public boolean isAlwaysOnTopSupported()
  {
    return Toolkit.getDefaultToolkit().isAlwaysOnTopSupported();
  }
  
  public final boolean isAlwaysOnTop()
  {
    return alwaysOnTop;
  }
  
  public Component getFocusOwner()
  {
    return isFocused() ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() : null;
  }
  
  public Component getMostRecentFocusOwner()
  {
    if (isFocused()) {
      return getFocusOwner();
    }
    Component localComponent = KeyboardFocusManager.getMostRecentFocusOwner(this);
    if (localComponent != null) {
      return localComponent;
    }
    return isFocusableWindow() ? getFocusTraversalPolicy().getInitialComponent(this) : null;
  }
  
  public boolean isActive()
  {
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == this;
  }
  
  public boolean isFocused()
  {
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getGlobalFocusedWindow() == this;
  }
  
  public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("invalid focus traversal key identifier");
    }
    Set<AWTKeyStroke> localSet = focusTraversalKeys != null ? focusTraversalKeys[paramInt] : null;
    if (localSet != null) {
      return localSet;
    }
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(paramInt);
  }
  
  public final void setFocusCycleRoot(boolean paramBoolean) {}
  
  public final boolean isFocusCycleRoot()
  {
    return true;
  }
  
  public final Container getFocusCycleRootAncestor()
  {
    return null;
  }
  
  public final boolean isFocusableWindow()
  {
    if (!getFocusableWindowState()) {
      return false;
    }
    if (((this instanceof Frame)) || ((this instanceof Dialog))) {
      return true;
    }
    if (getFocusTraversalPolicy().getDefaultComponent(this) == null) {
      return false;
    }
    for (Window localWindow = getOwner(); localWindow != null; localWindow = localWindow.getOwner()) {
      if (((localWindow instanceof Frame)) || ((localWindow instanceof Dialog))) {
        return localWindow.isShowing();
      }
    }
    return false;
  }
  
  public boolean getFocusableWindowState()
  {
    return focusableWindowState;
  }
  
  public void setFocusableWindowState(boolean paramBoolean)
  {
    boolean bool;
    synchronized (this)
    {
      bool = focusableWindowState;
      focusableWindowState = paramBoolean;
    }
    ??? = (WindowPeer)peer;
    if (??? != null) {
      ((WindowPeer)???).updateFocusableWindowState();
    }
    firePropertyChange("focusableWindowState", bool, paramBoolean);
    if ((bool) && (!paramBoolean) && (isFocused()))
    {
      for (Window localWindow = getOwner(); localWindow != null; localWindow = localWindow.getOwner())
      {
        Component localComponent = KeyboardFocusManager.getMostRecentFocusOwner(localWindow);
        if ((localComponent != null) && (localComponent.requestFocus(false, CausedFocusEvent.Cause.ACTIVATION))) {
          return;
        }
      }
      KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwnerPriv();
    }
  }
  
  public void setAutoRequestFocus(boolean paramBoolean)
  {
    autoRequestFocus = paramBoolean;
  }
  
  public boolean isAutoRequestFocus()
  {
    return autoRequestFocus;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    super.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    super.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent)
  {
    if (paramAWTEvent.getID() == 101)
    {
      invalidate();
      validate();
    }
    super.dispatchEventImpl(paramAWTEvent);
  }
  
  @Deprecated
  public boolean postEvent(Event paramEvent)
  {
    if (handleEvent(paramEvent))
    {
      paramEvent.consume();
      return true;
    }
    return false;
  }
  
  public boolean isShowing()
  {
    return visible;
  }
  
  boolean isDisposing()
  {
    return disposing;
  }
  
  @Deprecated
  public void applyResourceBundle(ResourceBundle paramResourceBundle)
  {
    applyComponentOrientation(ComponentOrientation.getOrientation(paramResourceBundle));
  }
  
  @Deprecated
  public void applyResourceBundle(String paramString)
  {
    applyResourceBundle(ResourceBundle.getBundle(paramString));
  }
  
  void addOwnedWindow(WeakReference<Window> paramWeakReference)
  {
    if (paramWeakReference != null) {
      synchronized (ownedWindowList)
      {
        if (!ownedWindowList.contains(paramWeakReference)) {
          ownedWindowList.addElement(paramWeakReference);
        }
      }
    }
  }
  
  void removeOwnedWindow(WeakReference<Window> paramWeakReference)
  {
    if (paramWeakReference != null) {
      ownedWindowList.removeElement(paramWeakReference);
    }
  }
  
  void connectOwnedWindow(Window paramWindow)
  {
    parent = this;
    addOwnedWindow(weakThis);
    disposerRecord.updateOwner();
  }
  
  private void addToWindowList()
  {
    synchronized (Window.class)
    {
      Vector localVector = (Vector)appContext.get(Window.class);
      if (localVector == null)
      {
        localVector = new Vector();
        appContext.put(Window.class, localVector);
      }
      localVector.add(weakThis);
    }
  }
  
  private static void removeFromWindowList(AppContext paramAppContext, WeakReference<Window> paramWeakReference)
  {
    synchronized (Window.class)
    {
      Vector localVector = (Vector)paramAppContext.get(Window.class);
      if (localVector != null) {
        localVector.remove(paramWeakReference);
      }
    }
  }
  
  private void removeFromWindowList()
  {
    removeFromWindowList(appContext, weakThis);
  }
  
  public void setType(Type paramType)
  {
    if (paramType == null) {
      throw new IllegalArgumentException("type should not be null.");
    }
    synchronized (getTreeLock())
    {
      if (isDisplayable()) {
        throw new IllegalComponentStateException("The window is displayable.");
      }
      synchronized (getObjectLock())
      {
        type = paramType;
      }
    }
  }
  
  /* Error */
  public Type getType()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 1505	java/awt/Window:getObjectLock	()Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 1297	java/awt/Window:type	Ljava/awt/Window$Type;
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
    //   0	19	0	this	Window
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    synchronized (this)
    {
      focusMgr = new FocusManager();
      focusMgr.focusRoot = this;
      focusMgr.focusOwner = getMostRecentFocusOwner();
      paramObjectOutputStream.defaultWriteObject();
      focusMgr = null;
      AWTEventMulticaster.save(paramObjectOutputStream, "windowL", windowListener);
      AWTEventMulticaster.save(paramObjectOutputStream, "windowFocusL", windowFocusListener);
      AWTEventMulticaster.save(paramObjectOutputStream, "windowStateL", windowStateListener);
    }
    paramObjectOutputStream.writeObject(null);
    synchronized (ownedWindowList)
    {
      for (int i = 0; i < ownedWindowList.size(); i++)
      {
        Window localWindow = (Window)((WeakReference)ownedWindowList.elementAt(i)).get();
        if (localWindow != null)
        {
          paramObjectOutputStream.writeObject("ownedL");
          paramObjectOutputStream.writeObject(localWindow);
        }
      }
    }
    paramObjectOutputStream.writeObject(null);
    if (icons != null)
    {
      ??? = icons.iterator();
      while (((Iterator)???).hasNext())
      {
        Image localImage = (Image)((Iterator)???).next();
        if ((localImage instanceof Serializable)) {
          paramObjectOutputStream.writeObject(localImage);
        }
      }
    }
    paramObjectOutputStream.writeObject(null);
  }
  
  private void initDeserializedWindow()
  {
    setWarningString();
    inputContextLock = new Object();
    visible = false;
    weakThis = new WeakReference(this);
    anchor = new Object();
    disposerRecord = new WindowDisposerRecord(appContext, this);
    Disposer.addRecord(anchor, disposerRecord);
    addToWindowList();
    initGC(null);
    ownedWindowList = new Vector();
  }
  
  private void deserializeResources(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    if (windowSerializedDataVersion < 2)
    {
      if ((focusMgr != null) && (focusMgr.focusOwner != null)) {
        KeyboardFocusManager.setMostRecentFocusOwner(this, focusMgr.focusOwner);
      }
      focusableWindowState = true;
    }
    Object localObject1;
    Object localObject2;
    while (null != (localObject1 = paramObjectInputStream.readObject()))
    {
      localObject2 = ((String)localObject1).intern();
      if ("windowL" == localObject2) {
        addWindowListener((WindowListener)paramObjectInputStream.readObject());
      } else if ("windowFocusL" == localObject2) {
        addWindowFocusListener((WindowFocusListener)paramObjectInputStream.readObject());
      } else if ("windowStateL" == localObject2) {
        addWindowStateListener((WindowStateListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
    try
    {
      while (null != (localObject1 = paramObjectInputStream.readObject()))
      {
        localObject2 = ((String)localObject1).intern();
        if ("ownedL" == localObject2) {
          connectOwnedWindow((Window)paramObjectInputStream.readObject());
        } else {
          paramObjectInputStream.readObject();
        }
      }
      localObject2 = paramObjectInputStream.readObject();
      icons = new ArrayList();
      while (localObject2 != null)
      {
        if ((localObject2 instanceof Image)) {
          icons.add((Image)localObject2);
        }
        localObject2 = paramObjectInputStream.readObject();
      }
    }
    catch (OptionalDataException localOptionalDataException) {}
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    initDeserializedWindow();
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    syncLWRequests = localGetField.get("syncLWRequests", systemSyncLWRequests);
    state = localGetField.get("state", 0);
    focusableWindowState = localGetField.get("focusableWindowState", true);
    windowSerializedDataVersion = localGetField.get("windowSerializedDataVersion", 1);
    locationByPlatform = localGetField.get("locationByPlatform", locationByPlatformProp);
    focusMgr = ((FocusManager)localGetField.get("focusMgr", null));
    Dialog.ModalExclusionType localModalExclusionType = (Dialog.ModalExclusionType)localGetField.get("modalExclusionType", Dialog.ModalExclusionType.NO_EXCLUDE);
    setModalExclusionType(localModalExclusionType);
    boolean bool = localGetField.get("alwaysOnTop", false);
    if (bool) {
      setAlwaysOnTop(bool);
    }
    shape = ((Shape)localGetField.get("shape", null));
    opacity = Float.valueOf(localGetField.get("opacity", 1.0F)).floatValue();
    securityWarningWidth = 0;
    securityWarningHeight = 0;
    securityWarningPointX = 2.0D;
    securityWarningPointY = 0.0D;
    securityWarningAlignmentX = 1.0F;
    securityWarningAlignmentY = 0.0F;
    deserializeResources(paramObjectInputStream);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTWindow();
    }
    return accessibleContext;
  }
  
  void setGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
  {
    if (paramGraphicsConfiguration == null) {
      paramGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    synchronized (getTreeLock())
    {
      super.setGraphicsConfiguration(paramGraphicsConfiguration);
      if (log.isLoggable(PlatformLogger.Level.FINER)) {
        log.finer("+ Window.setGraphicsConfiguration(): new GC is \n+ " + getGraphicsConfiguration_NoClientCode() + "\n+ this is " + this);
      }
    }
  }
  
  public void setLocationRelativeTo(Component paramComponent)
  {
    int i = 0;
    int j = 0;
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration_NoClientCode();
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    Dimension localDimension = getSize();
    Window localWindow = SunToolkit.getContainingWindow(paramComponent);
    Object localObject;
    Point localPoint;
    if ((paramComponent == null) || (localWindow == null))
    {
      localObject = GraphicsEnvironment.getLocalGraphicsEnvironment();
      localGraphicsConfiguration = ((GraphicsEnvironment)localObject).getDefaultScreenDevice().getDefaultConfiguration();
      localRectangle = localGraphicsConfiguration.getBounds();
      localPoint = ((GraphicsEnvironment)localObject).getCenterPoint();
      i = x - width / 2;
      j = y - height / 2;
    }
    else if (!paramComponent.isShowing())
    {
      localGraphicsConfiguration = localWindow.getGraphicsConfiguration();
      localRectangle = localGraphicsConfiguration.getBounds();
      i = x + (width - width) / 2;
      j = y + (height - height) / 2;
    }
    else
    {
      localGraphicsConfiguration = localWindow.getGraphicsConfiguration();
      localRectangle = localGraphicsConfiguration.getBounds();
      localObject = paramComponent.getSize();
      localPoint = paramComponent.getLocationOnScreen();
      i = x + (width - width) / 2;
      j = y + (height - height) / 2;
      if (j + height > y + height)
      {
        j = y + height - height;
        if (x - x + width / 2 < width / 2) {
          i = x + width;
        } else {
          i = x - width;
        }
      }
    }
    if (j + height > y + height) {
      j = y + height - height;
    }
    if (j < y) {
      j = y;
    }
    if (i + width > x + width) {
      i = x + width - width;
    }
    if (i < x) {
      i = x;
    }
    setLocation(i, j);
  }
  
  void deliverMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent) {}
  
  boolean dispatchMouseWheelToAncestor(MouseWheelEvent paramMouseWheelEvent)
  {
    return false;
  }
  
  public void createBufferStrategy(int paramInt)
  {
    super.createBufferStrategy(paramInt);
  }
  
  public void createBufferStrategy(int paramInt, BufferCapabilities paramBufferCapabilities)
    throws AWTException
  {
    super.createBufferStrategy(paramInt, paramBufferCapabilities);
  }
  
  public BufferStrategy getBufferStrategy()
  {
    return super.getBufferStrategy();
  }
  
  Component getTemporaryLostComponent()
  {
    return temporaryLostComponent;
  }
  
  Component setTemporaryLostComponent(Component paramComponent)
  {
    Component localComponent = temporaryLostComponent;
    if ((paramComponent == null) || (paramComponent.canBeFocusOwner())) {
      temporaryLostComponent = paramComponent;
    } else {
      temporaryLostComponent = null;
    }
    return localComponent;
  }
  
  boolean canContainFocusOwner(Component paramComponent)
  {
    return (super.canContainFocusOwner(paramComponent)) && (isFocusableWindow());
  }
  
  public void setLocationByPlatform(boolean paramBoolean)
  {
    synchronized (getTreeLock())
    {
      if ((paramBoolean) && (isShowing())) {
        throw new IllegalComponentStateException("The window is showing on screen.");
      }
      locationByPlatform = paramBoolean;
    }
  }
  
  public boolean isLocationByPlatform()
  {
    return locationByPlatform;
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (getTreeLock())
    {
      if ((getBoundsOp() == 1) || (getBoundsOp() == 3)) {
        locationByPlatform = false;
      }
      super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setBounds(Rectangle paramRectangle)
  {
    setBounds(x, y, width, height);
  }
  
  boolean isRecursivelyVisible()
  {
    return visible;
  }
  
  public float getOpacity()
  {
    return opacity;
  }
  
  public void setOpacity(float paramFloat)
  {
    synchronized (getTreeLock())
    {
      if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
        throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
      }
      if (paramFloat < 1.0F)
      {
        localObject1 = getGraphicsConfiguration();
        GraphicsDevice localGraphicsDevice = ((GraphicsConfiguration)localObject1).getDevice();
        if (((GraphicsConfiguration)localObject1).getDevice().getFullScreenWindow() == this) {
          throw new IllegalComponentStateException("Setting opacity for full-screen window is not supported.");
        }
        if (!localGraphicsDevice.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
          throw new UnsupportedOperationException("TRANSLUCENT translucency is not supported.");
        }
      }
      opacity = paramFloat;
      Object localObject1 = (WindowPeer)getPeer();
      if (localObject1 != null) {
        ((WindowPeer)localObject1).setOpacity(paramFloat);
      }
    }
  }
  
  public Shape getShape()
  {
    synchronized (getTreeLock())
    {
      return shape == null ? null : new Path2D.Float(shape);
    }
  }
  
  public void setShape(Shape paramShape)
  {
    synchronized (getTreeLock())
    {
      if (paramShape != null)
      {
        localObject1 = getGraphicsConfiguration();
        GraphicsDevice localGraphicsDevice = ((GraphicsConfiguration)localObject1).getDevice();
        if (((GraphicsConfiguration)localObject1).getDevice().getFullScreenWindow() == this) {
          throw new IllegalComponentStateException("Setting shape for full-screen window is not supported.");
        }
        if (!localGraphicsDevice.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
          throw new UnsupportedOperationException("PERPIXEL_TRANSPARENT translucency is not supported.");
        }
      }
      shape = (paramShape == null ? null : new Path2D.Float(paramShape));
      Object localObject1 = (WindowPeer)getPeer();
      if (localObject1 != null) {
        ((WindowPeer)localObject1).applyShape(paramShape == null ? null : Region.getInstance(paramShape, null));
      }
    }
  }
  
  public Color getBackground()
  {
    return super.getBackground();
  }
  
  public void setBackground(Color paramColor)
  {
    Color localColor = getBackground();
    super.setBackground(paramColor);
    if ((localColor != null) && (localColor.equals(paramColor))) {
      return;
    }
    int i = localColor != null ? localColor.getAlpha() : 255;
    int j = paramColor != null ? paramColor.getAlpha() : 255;
    if ((i == 255) && (j < 255))
    {
      localObject = getGraphicsConfiguration();
      GraphicsDevice localGraphicsDevice = ((GraphicsConfiguration)localObject).getDevice();
      if (((GraphicsConfiguration)localObject).getDevice().getFullScreenWindow() == this) {
        throw new IllegalComponentStateException("Making full-screen window non opaque is not supported.");
      }
      if (!((GraphicsConfiguration)localObject).isTranslucencyCapable())
      {
        GraphicsConfiguration localGraphicsConfiguration = localGraphicsDevice.getTranslucencyCapableGC();
        if (localGraphicsConfiguration == null) {
          throw new UnsupportedOperationException("PERPIXEL_TRANSLUCENT translucency is not supported");
        }
        setGraphicsConfiguration(localGraphicsConfiguration);
      }
      setLayersOpaque(this, false);
    }
    else if ((i < 255) && (j == 255))
    {
      setLayersOpaque(this, true);
    }
    Object localObject = (WindowPeer)getPeer();
    if (localObject != null) {
      ((WindowPeer)localObject).setOpaque(j == 255);
    }
  }
  
  public boolean isOpaque()
  {
    Color localColor = getBackground();
    return localColor.getAlpha() == 255;
  }
  
  private void updateWindow()
  {
    synchronized (getTreeLock())
    {
      WindowPeer localWindowPeer = (WindowPeer)getPeer();
      if (localWindowPeer != null) {
        localWindowPeer.updateWindow();
      }
    }
  }
  
  /* Error */
  public void paint(Graphics paramGraphics)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 1454	java/awt/Window:isOpaque	()Z
    //   4: ifne +62 -> 66
    //   7: aload_1
    //   8: invokevirtual 1389	java/awt/Graphics:create	()Ljava/awt/Graphics;
    //   11: astore_2
    //   12: aload_2
    //   13: instanceof 638
    //   16: ifeq +36 -> 52
    //   19: aload_2
    //   20: aload_0
    //   21: invokevirtual 1467	java/awt/Window:getBackground	()Ljava/awt/Color;
    //   24: invokevirtual 1388	java/awt/Graphics:setColor	(Ljava/awt/Color;)V
    //   27: aload_2
    //   28: checkcast 638	java/awt/Graphics2D
    //   31: iconst_2
    //   32: invokestatic 1334	java/awt/AlphaComposite:getInstance	(I)Ljava/awt/AlphaComposite;
    //   35: invokevirtual 1390	java/awt/Graphics2D:setComposite	(Ljava/awt/Composite;)V
    //   38: aload_2
    //   39: iconst_0
    //   40: iconst_0
    //   41: aload_0
    //   42: invokevirtual 1425	java/awt/Window:getWidth	()I
    //   45: aload_0
    //   46: invokevirtual 1424	java/awt/Window:getHeight	()I
    //   49: invokevirtual 1387	java/awt/Graphics:fillRect	(IIII)V
    //   52: aload_2
    //   53: invokevirtual 1386	java/awt/Graphics:dispose	()V
    //   56: goto +10 -> 66
    //   59: astore_3
    //   60: aload_2
    //   61: invokevirtual 1386	java/awt/Graphics:dispose	()V
    //   64: aload_3
    //   65: athrow
    //   66: aload_0
    //   67: aload_1
    //   68: invokespecial 1365	java/awt/Container:paint	(Ljava/awt/Graphics;)V
    //   71: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	Window
    //   0	72	1	paramGraphics	Graphics
    //   11	50	2	localGraphics	Graphics
    //   59	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   12	52	59	finally
  }
  
  private static void setLayersOpaque(Component paramComponent, boolean paramBoolean)
  {
    if (SunToolkit.isInstanceOf(paramComponent, "javax.swing.RootPaneContainer"))
    {
      RootPaneContainer localRootPaneContainer = (RootPaneContainer)paramComponent;
      JRootPane localJRootPane = localRootPaneContainer.getRootPane();
      JLayeredPane localJLayeredPane = localJRootPane.getLayeredPane();
      Container localContainer = localJRootPane.getContentPane();
      Object localObject = (localContainer instanceof JComponent) ? (JComponent)localContainer : null;
      localJLayeredPane.setOpaque(paramBoolean);
      localJRootPane.setOpaque(paramBoolean);
      if (localObject != null)
      {
        ((JComponent)localObject).setOpaque(paramBoolean);
        int i = ((JComponent)localObject).getComponentCount();
        if (i > 0)
        {
          Component localComponent = ((JComponent)localObject).getComponent(0);
          if ((localComponent instanceof RootPaneContainer)) {
            setLayersOpaque(localComponent, paramBoolean);
          }
        }
      }
    }
  }
  
  final Container getContainer()
  {
    return null;
  }
  
  final void applyCompoundShape(Region paramRegion) {}
  
  final void applyCurrentShape() {}
  
  final void mixOnReshaping() {}
  
  final Point getLocationOnWindow()
  {
    return new Point(0, 0);
  }
  
  private static double limit(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    paramDouble1 = Math.max(paramDouble1, paramDouble2);
    paramDouble1 = Math.min(paramDouble1, paramDouble3);
    return paramDouble1;
  }
  
  private Point2D calculateSecurityWarningPosition(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    double d1 = paramDouble1 + paramDouble3 * securityWarningAlignmentX + securityWarningPointX;
    double d2 = paramDouble2 + paramDouble4 * securityWarningAlignmentY + securityWarningPointY;
    d1 = limit(d1, paramDouble1 - securityWarningWidth - 2.0D, paramDouble1 + paramDouble3 + 2.0D);
    d2 = limit(d2, paramDouble2 - securityWarningHeight - 2.0D, paramDouble2 + paramDouble4 + 2.0D);
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration_NoClientCode();
    Rectangle localRectangle = localGraphicsConfiguration.getBounds();
    Insets localInsets = Toolkit.getDefaultToolkit().getScreenInsets(localGraphicsConfiguration);
    d1 = limit(d1, x + left, x + width - right - securityWarningWidth);
    d2 = limit(d2, y + top, y + height - bottom - securityWarningHeight);
    return new Point2D.Double(d1, d2);
  }
  
  void updateZOrder() {}
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.syncLWRequests"));
    systemSyncLWRequests = (str != null) && (str.equals("true"));
    str = (String)AccessController.doPrivileged(new GetPropertyAction("java.awt.Window.locationByPlatform"));
    locationByPlatformProp = (str != null) && (str.equals("true"));
    beforeFirstWindowShown = new AtomicBoolean(true);
    AWTAccessor.setWindowAccessor(new AWTAccessor.WindowAccessor()
    {
      public float getOpacity(Window paramAnonymousWindow)
      {
        return opacity;
      }
      
      public void setOpacity(Window paramAnonymousWindow, float paramAnonymousFloat)
      {
        paramAnonymousWindow.setOpacity(paramAnonymousFloat);
      }
      
      public Shape getShape(Window paramAnonymousWindow)
      {
        return paramAnonymousWindow.getShape();
      }
      
      public void setShape(Window paramAnonymousWindow, Shape paramAnonymousShape)
      {
        paramAnonymousWindow.setShape(paramAnonymousShape);
      }
      
      public void setOpaque(Window paramAnonymousWindow, boolean paramAnonymousBoolean)
      {
        Color localColor = paramAnonymousWindow.getBackground();
        if (localColor == null) {
          localColor = new Color(0, 0, 0, 0);
        }
        paramAnonymousWindow.setBackground(new Color(localColor.getRed(), localColor.getGreen(), localColor.getBlue(), paramAnonymousBoolean ? 255 : 0));
      }
      
      public void updateWindow(Window paramAnonymousWindow)
      {
        paramAnonymousWindow.updateWindow();
      }
      
      public Dimension getSecurityWarningSize(Window paramAnonymousWindow)
      {
        return new Dimension(securityWarningWidth, securityWarningHeight);
      }
      
      public void setSecurityWarningSize(Window paramAnonymousWindow, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        securityWarningWidth = paramAnonymousInt1;
        securityWarningHeight = paramAnonymousInt2;
      }
      
      public void setSecurityWarningPosition(Window paramAnonymousWindow, Point2D paramAnonymousPoint2D, float paramAnonymousFloat1, float paramAnonymousFloat2)
      {
        securityWarningPointX = paramAnonymousPoint2D.getX();
        securityWarningPointY = paramAnonymousPoint2D.getY();
        securityWarningAlignmentX = paramAnonymousFloat1;
        securityWarningAlignmentY = paramAnonymousFloat2;
        synchronized (paramAnonymousWindow.getTreeLock())
        {
          WindowPeer localWindowPeer = (WindowPeer)paramAnonymousWindow.getPeer();
          if (localWindowPeer != null) {
            localWindowPeer.repositionSecurityWarning();
          }
        }
      }
      
      public Point2D calculateSecurityWarningPosition(Window paramAnonymousWindow, double paramAnonymousDouble1, double paramAnonymousDouble2, double paramAnonymousDouble3, double paramAnonymousDouble4)
      {
        return paramAnonymousWindow.calculateSecurityWarningPosition(paramAnonymousDouble1, paramAnonymousDouble2, paramAnonymousDouble3, paramAnonymousDouble4);
      }
      
      public void setLWRequestStatus(Window paramAnonymousWindow, boolean paramAnonymousBoolean)
      {
        syncLWRequests = paramAnonymousBoolean;
      }
      
      public boolean isAutoRequestFocus(Window paramAnonymousWindow)
      {
        return autoRequestFocus;
      }
      
      public boolean isTrayIconWindow(Window paramAnonymousWindow)
      {
        return isTrayIconWindow;
      }
      
      public void setTrayIconWindow(Window paramAnonymousWindow, boolean paramAnonymousBoolean)
      {
        isTrayIconWindow = paramAnonymousBoolean;
      }
      
      public Window[] getOwnedWindows(Window paramAnonymousWindow)
      {
        return paramAnonymousWindow.getOwnedWindows_NoClientCode();
      }
    });
  }
  
  protected class AccessibleAWTWindow
    extends Container.AccessibleAWTContainer
  {
    private static final long serialVersionUID = 4215068635060671780L;
    
    protected AccessibleAWTWindow()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.WINDOW;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (getFocusOwner() != null) {
        localAccessibleStateSet.add(AccessibleState.ACTIVE);
      }
      return localAccessibleStateSet;
    }
  }
  
  public static enum Type
  {
    NORMAL,  UTILITY,  POPUP;
    
    private Type() {}
  }
  
  static class WindowDisposerRecord
    implements DisposerRecord
  {
    WeakReference<Window> owner;
    final WeakReference<Window> weakThis;
    final WeakReference<AppContext> context;
    
    WindowDisposerRecord(AppContext paramAppContext, Window paramWindow)
    {
      weakThis = weakThis;
      context = new WeakReference(paramAppContext);
    }
    
    public void updateOwner()
    {
      Window localWindow = (Window)weakThis.get();
      owner = (localWindow == null ? null : new WeakReference(localWindow.getOwner()));
    }
    
    public void dispose()
    {
      if (owner != null)
      {
        localObject = (Window)owner.get();
        if (localObject != null) {
          ((Window)localObject).removeOwnedWindow(weakThis);
        }
      }
      Object localObject = (AppContext)context.get();
      if (null != localObject) {
        Window.removeFromWindowList((AppContext)localObject, weakThis);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Window.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */