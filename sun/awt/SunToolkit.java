package sun.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import sun.awt.im.InputContext;
import sun.awt.im.SimpleInputMethodWindow;
import sun.awt.image.ByteArrayImageSource;
import sun.awt.image.FileImageSource;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.ToolkitImage;
import sun.awt.image.URLImageSource;
import sun.font.FontDesignMetrics;
import sun.misc.SoftCache;
import sun.net.util.URLUtil;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants.AWT;
import sun.util.logging.PlatformLogger;

public abstract class SunToolkit
  extends Toolkit
  implements WindowClosingSupport, WindowClosingListener, ComponentFactory, InputMethodSupport, KeyboardFocusManagerPeerProvider
{
  public static final int GRAB_EVENT_MASK = Integer.MIN_VALUE;
  private static final String POST_EVENT_QUEUE_KEY = "PostEventQueue";
  protected static int numberOfButtons = 0;
  public static final int MAX_BUTTONS_SUPPORTED = 20;
  private static final ReentrantLock AWT_LOCK = new ReentrantLock();
  private static final Condition AWT_LOCK_COND = AWT_LOCK.newCondition();
  private static final Map<Object, AppContext> appContextMap = Collections.synchronizedMap(new WeakHashMap());
  static final SoftCache fileImgCache = new SoftCache();
  static final SoftCache urlImgCache = new SoftCache();
  private static Locale startupLocale = null;
  private transient WindowClosingListener windowClosingListener = null;
  private static DefaultMouseInfoPeer mPeer = null;
  private static Dialog.ModalExclusionType DEFAULT_MODAL_EXCLUSION_TYPE = null;
  private ModalityListenerList modalityListeners = new ModalityListenerList();
  public static final int DEFAULT_WAIT_TIME = 10000;
  private static final int MAX_ITERS = 20;
  private static final int MIN_ITERS = 0;
  private static final int MINIMAL_EDELAY = 0;
  private boolean eventDispatched = false;
  private boolean queueEmpty = false;
  private final Object waitLock = "Wait Lock";
  private static boolean checkedSystemAAFontSettings;
  private static boolean useSystemAAFontSettings;
  private static boolean lastExtraCondition = true;
  private static RenderingHints desktopFontHints;
  public static final String DESKTOPFONTHINTS = "awt.font.desktophints";
  private static Boolean sunAwtDisableMixing = null;
  private static final Object DEACTIVATION_TIMES_MAP_KEY = new Object();
  
  private static void initEQ(AppContext paramAppContext)
  {
    String str = System.getProperty("AWT.EventQueueClass", "java.awt.EventQueue");
    EventQueue localEventQueue;
    try
    {
      localEventQueue = (EventQueue)Class.forName(str).newInstance();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      System.err.println("Failed loading " + str + ": " + localException);
      localEventQueue = new EventQueue();
    }
    paramAppContext.put(AppContext.EVENT_QUEUE_KEY, localEventQueue);
    PostEventQueue localPostEventQueue = new PostEventQueue(localEventQueue);
    paramAppContext.put("PostEventQueue", localPostEventQueue);
  }
  
  public SunToolkit() {}
  
  public boolean useBufferPerWindow()
  {
    return false;
  }
  
  public abstract WindowPeer createWindow(Window paramWindow)
    throws HeadlessException;
  
  public abstract FramePeer createFrame(Frame paramFrame)
    throws HeadlessException;
  
  public abstract FramePeer createLightweightFrame(LightweightFrame paramLightweightFrame)
    throws HeadlessException;
  
  public abstract DialogPeer createDialog(Dialog paramDialog)
    throws HeadlessException;
  
  public abstract ButtonPeer createButton(Button paramButton)
    throws HeadlessException;
  
  public abstract TextFieldPeer createTextField(TextField paramTextField)
    throws HeadlessException;
  
  public abstract ChoicePeer createChoice(Choice paramChoice)
    throws HeadlessException;
  
  public abstract LabelPeer createLabel(Label paramLabel)
    throws HeadlessException;
  
  public abstract ListPeer createList(java.awt.List paramList)
    throws HeadlessException;
  
  public abstract CheckboxPeer createCheckbox(Checkbox paramCheckbox)
    throws HeadlessException;
  
  public abstract ScrollbarPeer createScrollbar(Scrollbar paramScrollbar)
    throws HeadlessException;
  
  public abstract ScrollPanePeer createScrollPane(ScrollPane paramScrollPane)
    throws HeadlessException;
  
  public abstract TextAreaPeer createTextArea(TextArea paramTextArea)
    throws HeadlessException;
  
  public abstract FileDialogPeer createFileDialog(FileDialog paramFileDialog)
    throws HeadlessException;
  
  public abstract MenuBarPeer createMenuBar(MenuBar paramMenuBar)
    throws HeadlessException;
  
  public abstract MenuPeer createMenu(Menu paramMenu)
    throws HeadlessException;
  
  public abstract PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu)
    throws HeadlessException;
  
  public abstract MenuItemPeer createMenuItem(MenuItem paramMenuItem)
    throws HeadlessException;
  
  public abstract CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem)
    throws HeadlessException;
  
  public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
    throws InvalidDnDOperationException;
  
  public abstract TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon)
    throws HeadlessException, AWTException;
  
  public abstract SystemTrayPeer createSystemTray(SystemTray paramSystemTray);
  
  public abstract boolean isTraySupported();
  
  public abstract FontPeer getFontPeer(String paramString, int paramInt);
  
  public abstract RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice)
    throws AWTException;
  
  public abstract KeyboardFocusManagerPeer getKeyboardFocusManagerPeer()
    throws HeadlessException;
  
  public static final void awtLock()
  {
    AWT_LOCK.lock();
  }
  
  public static final boolean awtTryLock()
  {
    return AWT_LOCK.tryLock();
  }
  
  public static final void awtUnlock()
  {
    AWT_LOCK.unlock();
  }
  
  public static final void awtLockWait()
    throws InterruptedException
  {
    AWT_LOCK_COND.await();
  }
  
  public static final void awtLockWait(long paramLong)
    throws InterruptedException
  {
    AWT_LOCK_COND.await(paramLong, TimeUnit.MILLISECONDS);
  }
  
  public static final void awtLockNotify()
  {
    AWT_LOCK_COND.signal();
  }
  
  public static final void awtLockNotifyAll()
  {
    AWT_LOCK_COND.signalAll();
  }
  
  public static final boolean isAWTLockHeldByCurrentThread()
  {
    return AWT_LOCK.isHeldByCurrentThread();
  }
  
  public static AppContext createNewAppContext()
  {
    ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
    return createNewAppContext(localThreadGroup);
  }
  
  static final AppContext createNewAppContext(ThreadGroup paramThreadGroup)
  {
    AppContext localAppContext = new AppContext(paramThreadGroup);
    initEQ(localAppContext);
    return localAppContext;
  }
  
  static void wakeupEventQueue(EventQueue paramEventQueue, boolean paramBoolean)
  {
    AWTAccessor.getEventQueueAccessor().wakeup(paramEventQueue, paramBoolean);
  }
  
  protected static Object targetToPeer(Object paramObject)
  {
    if ((paramObject != null) && (!GraphicsEnvironment.isHeadless())) {
      return AWTAutoShutdown.getInstance().getPeer(paramObject);
    }
    return null;
  }
  
  protected static void targetCreatedPeer(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 != null) && (paramObject2 != null) && (!GraphicsEnvironment.isHeadless())) {
      AWTAutoShutdown.getInstance().registerPeer(paramObject1, paramObject2);
    }
  }
  
  protected static void targetDisposedPeer(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 != null) && (paramObject2 != null) && (!GraphicsEnvironment.isHeadless())) {
      AWTAutoShutdown.getInstance().unregisterPeer(paramObject1, paramObject2);
    }
  }
  
  private static boolean setAppContext(Object paramObject, AppContext paramAppContext)
  {
    if ((paramObject instanceof Component)) {
      AWTAccessor.getComponentAccessor().setAppContext((Component)paramObject, paramAppContext);
    } else if ((paramObject instanceof MenuComponent)) {
      AWTAccessor.getMenuComponentAccessor().setAppContext((MenuComponent)paramObject, paramAppContext);
    } else {
      return false;
    }
    return true;
  }
  
  private static AppContext getAppContext(Object paramObject)
  {
    if ((paramObject instanceof Component)) {
      return AWTAccessor.getComponentAccessor().getAppContext((Component)paramObject);
    }
    if ((paramObject instanceof MenuComponent)) {
      return AWTAccessor.getMenuComponentAccessor().getAppContext((MenuComponent)paramObject);
    }
    return null;
  }
  
  public static AppContext targetToAppContext(Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    AppContext localAppContext = getAppContext(paramObject);
    if (localAppContext == null) {
      localAppContext = (AppContext)appContextMap.get(paramObject);
    }
    return localAppContext;
  }
  
  public static void setLWRequestStatus(Window paramWindow, boolean paramBoolean)
  {
    AWTAccessor.getWindowAccessor().setLWRequestStatus(paramWindow, paramBoolean);
  }
  
  public static void checkAndSetPolicy(Container paramContainer)
  {
    FocusTraversalPolicy localFocusTraversalPolicy = KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
    paramContainer.setFocusTraversalPolicy(localFocusTraversalPolicy);
  }
  
  private static FocusTraversalPolicy createLayoutPolicy()
  {
    FocusTraversalPolicy localFocusTraversalPolicy = null;
    try
    {
      Class localClass = Class.forName("javax.swing.LayoutFocusTraversalPolicy");
      localFocusTraversalPolicy = (FocusTraversalPolicy)localClass.newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (InstantiationException localInstantiationException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return localFocusTraversalPolicy;
  }
  
  public static void insertTargetMapping(Object paramObject, AppContext paramAppContext)
  {
    if (!setAppContext(paramObject, paramAppContext)) {
      appContextMap.put(paramObject, paramAppContext);
    }
  }
  
  public static void postEvent(AppContext paramAppContext, AWTEvent paramAWTEvent)
  {
    if (paramAWTEvent == null) {
      throw new NullPointerException();
    }
    AWTAccessor.SequencedEventAccessor localSequencedEventAccessor = AWTAccessor.getSequencedEventAccessor();
    if ((localSequencedEventAccessor != null) && (localSequencedEventAccessor.isSequencedEvent(paramAWTEvent)))
    {
      localObject1 = localSequencedEventAccessor.getNested(paramAWTEvent);
      if ((((AWTEvent)localObject1).getID() == 208) && ((localObject1 instanceof TimedWindowEvent)))
      {
        localObject2 = (TimedWindowEvent)localObject1;
        ((SunToolkit)Toolkit.getDefaultToolkit()).setWindowDeactivationTime((Window)((TimedWindowEvent)localObject2).getSource(), ((TimedWindowEvent)localObject2).getWhen());
      }
    }
    setSystemGenerated(paramAWTEvent);
    Object localObject1 = targetToAppContext(paramAWTEvent.getSource());
    if ((localObject1 != null) && (!localObject1.equals(paramAppContext))) {
      throw new RuntimeException("Event posted on wrong app context : " + paramAWTEvent);
    }
    Object localObject2 = (PostEventQueue)paramAppContext.get("PostEventQueue");
    if (localObject2 != null) {
      ((PostEventQueue)localObject2).postEvent(paramAWTEvent);
    }
  }
  
  public static void postPriorityEvent(AWTEvent paramAWTEvent)
  {
    PeerEvent localPeerEvent = new PeerEvent(Toolkit.getDefaultToolkit(), new Runnable()
    {
      public void run()
      {
        AWTAccessor.getAWTEventAccessor().setPosted(val$e);
        ((Component)val$e.getSource()).dispatchEvent(val$e);
      }
    }, 2L);
    postEvent(targetToAppContext(paramAWTEvent.getSource()), localPeerEvent);
  }
  
  public static void flushPendingEvents()
  {
    AppContext localAppContext = AppContext.getAppContext();
    flushPendingEvents(localAppContext);
  }
  
  public static void flushPendingEvents(AppContext paramAppContext)
  {
    PostEventQueue localPostEventQueue = (PostEventQueue)paramAppContext.get("PostEventQueue");
    if (localPostEventQueue != null) {
      localPostEventQueue.flush();
    }
  }
  
  public static void executeOnEventHandlerThread(Object paramObject, Runnable paramRunnable)
  {
    executeOnEventHandlerThread(new PeerEvent(paramObject, paramRunnable, 1L));
  }
  
  public static void executeOnEventHandlerThread(Object paramObject, Runnable paramRunnable, long paramLong)
  {
    executeOnEventHandlerThread(new PeerEvent(paramObject, paramRunnable, 1L)
    {
      public long getWhen()
      {
        return val$when;
      }
    });
  }
  
  public static void executeOnEventHandlerThread(PeerEvent paramPeerEvent)
  {
    postEvent(targetToAppContext(paramPeerEvent.getSource()), paramPeerEvent);
  }
  
  public static void invokeLaterOnAppContext(AppContext paramAppContext, Runnable paramRunnable)
  {
    postEvent(paramAppContext, new PeerEvent(Toolkit.getDefaultToolkit(), paramRunnable, 1L));
  }
  
  public static void executeOnEDTAndWait(Object paramObject, Runnable paramRunnable)
    throws InterruptedException, InvocationTargetException
  {
    if (EventQueue.isDispatchThread()) {
      throw new Error("Cannot call executeOnEDTAndWait from any event dispatcher thread");
    }
    Object local1AWTInvocationLock = new Object() {};
    PeerEvent localPeerEvent = new PeerEvent(paramObject, paramRunnable, local1AWTInvocationLock, true, 1L);
    synchronized (local1AWTInvocationLock)
    {
      executeOnEventHandlerThread(localPeerEvent);
      while (!localPeerEvent.isDispatched()) {
        local1AWTInvocationLock.wait();
      }
    }
    ??? = localPeerEvent.getThrowable();
    if (??? != null) {
      throw new InvocationTargetException((Throwable)???);
    }
  }
  
  public static boolean isDispatchThreadForAppContext(Object paramObject)
  {
    AppContext localAppContext = targetToAppContext(paramObject);
    EventQueue localEventQueue = (EventQueue)localAppContext.get(AppContext.EVENT_QUEUE_KEY);
    AWTAccessor.EventQueueAccessor localEventQueueAccessor = AWTAccessor.getEventQueueAccessor();
    return localEventQueueAccessor.isDispatchThreadImpl(localEventQueue);
  }
  
  public Dimension getScreenSize()
  {
    return new Dimension(getScreenWidth(), getScreenHeight());
  }
  
  protected abstract int getScreenWidth();
  
  protected abstract int getScreenHeight();
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    return FontDesignMetrics.getMetrics(paramFont);
  }
  
  public String[] getFontList()
  {
    String[] arrayOfString = { "Dialog", "SansSerif", "Serif", "Monospaced", "DialogInput" };
    return arrayOfString;
  }
  
  public PanelPeer createPanel(Panel paramPanel)
  {
    return (PanelPeer)createComponent(paramPanel);
  }
  
  public CanvasPeer createCanvas(Canvas paramCanvas)
  {
    return (CanvasPeer)createComponent(paramCanvas);
  }
  
  public void disableBackgroundErase(Canvas paramCanvas)
  {
    disableBackgroundEraseImpl(paramCanvas);
  }
  
  public void disableBackgroundErase(Component paramComponent)
  {
    disableBackgroundEraseImpl(paramComponent);
  }
  
  private void disableBackgroundEraseImpl(Component paramComponent)
  {
    AWTAccessor.getComponentAccessor().setBackgroundEraseDisabled(paramComponent, true);
  }
  
  public static boolean getSunAwtNoerasebackground()
  {
    return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.noerasebackground"))).booleanValue();
  }
  
  public static boolean getSunAwtErasebackgroundonresize()
  {
    return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.erasebackgroundonresize"))).booleanValue();
  }
  
  static Image getImageFromHash(Toolkit paramToolkit, URL paramURL)
  {
    checkPermissions(paramURL);
    synchronized (urlImgCache)
    {
      String str = paramURL.toString();
      Image localImage = (Image)urlImgCache.get(str);
      if (localImage == null) {
        try
        {
          localImage = paramToolkit.createImage(new URLImageSource(paramURL));
          urlImgCache.put(str, localImage);
        }
        catch (Exception localException) {}
      }
      return localImage;
    }
  }
  
  static Image getImageFromHash(Toolkit paramToolkit, String paramString)
  {
    checkPermissions(paramString);
    synchronized (fileImgCache)
    {
      Image localImage = (Image)fileImgCache.get(paramString);
      if (localImage == null) {
        try
        {
          localImage = paramToolkit.createImage(new FileImageSource(paramString));
          fileImgCache.put(paramString, localImage);
        }
        catch (Exception localException) {}
      }
      return localImage;
    }
  }
  
  public Image getImage(String paramString)
  {
    return getImageFromHash(this, paramString);
  }
  
  public Image getImage(URL paramURL)
  {
    return getImageFromHash(this, paramURL);
  }
  
  protected Image getImageWithResolutionVariant(String paramString1, String paramString2)
  {
    synchronized (fileImgCache)
    {
      Image localImage1 = getImageFromHash(this, paramString1);
      if ((localImage1 instanceof MultiResolutionImage)) {
        return localImage1;
      }
      Image localImage2 = getImageFromHash(this, paramString2);
      localImage1 = createImageWithResolutionVariant(localImage1, localImage2);
      fileImgCache.put(paramString1, localImage1);
      return localImage1;
    }
  }
  
  protected Image getImageWithResolutionVariant(URL paramURL1, URL paramURL2)
  {
    synchronized (urlImgCache)
    {
      Image localImage1 = getImageFromHash(this, paramURL1);
      if ((localImage1 instanceof MultiResolutionImage)) {
        return localImage1;
      }
      Image localImage2 = getImageFromHash(this, paramURL2);
      localImage1 = createImageWithResolutionVariant(localImage1, localImage2);
      String str = paramURL1.toString();
      urlImgCache.put(str, localImage1);
      return localImage1;
    }
  }
  
  public Image createImage(String paramString)
  {
    checkPermissions(paramString);
    return createImage(new FileImageSource(paramString));
  }
  
  public Image createImage(URL paramURL)
  {
    checkPermissions(paramURL);
    return createImage(new URLImageSource(paramURL));
  }
  
  public Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return createImage(new ByteArrayImageSource(paramArrayOfByte, paramInt1, paramInt2));
  }
  
  public Image createImage(ImageProducer paramImageProducer)
  {
    return new ToolkitImage(paramImageProducer);
  }
  
  public static Image createImageWithResolutionVariant(Image paramImage1, Image paramImage2)
  {
    return new MultiResolutionToolkitImage(paramImage1, paramImage2);
  }
  
  public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    if (!(paramImage instanceof ToolkitImage)) {
      return 32;
    }
    ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
    int i;
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      i = 32;
    } else {
      i = localToolkitImage.getImageRep().check(paramImageObserver);
    }
    return (localToolkitImage.check(paramImageObserver) | i) & checkResolutionVariant(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      return true;
    }
    if (!(paramImage instanceof ToolkitImage)) {
      return true;
    }
    ToolkitImage localToolkitImage = (ToolkitImage)paramImage;
    if (localToolkitImage.hasError())
    {
      if (paramImageObserver != null) {
        paramImageObserver.imageUpdate(paramImage, 192, -1, -1, -1, -1);
      }
      return false;
    }
    ImageRepresentation localImageRepresentation = localToolkitImage.getImageRep();
    return localImageRepresentation.prepare(paramImageObserver) & prepareResolutionVariant(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  private int checkResolutionVariant(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    ToolkitImage localToolkitImage = getResolutionVariant(paramImage);
    int i = getRVSize(paramInt1);
    int j = getRVSize(paramInt2);
    return (localToolkitImage == null) || (localToolkitImage.hasError()) ? 65535 : checkImage(localToolkitImage, i, j, MultiResolutionToolkitImage.getResolutionVariantObserver(paramImage, paramImageObserver, paramInt1, paramInt2, i, j, true));
  }
  
  private boolean prepareResolutionVariant(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    ToolkitImage localToolkitImage = getResolutionVariant(paramImage);
    int i = getRVSize(paramInt1);
    int j = getRVSize(paramInt2);
    return (localToolkitImage == null) || (localToolkitImage.hasError()) || (prepareImage(localToolkitImage, i, j, MultiResolutionToolkitImage.getResolutionVariantObserver(paramImage, paramImageObserver, paramInt1, paramInt2, i, j, true)));
  }
  
  private static int getRVSize(int paramInt)
  {
    return paramInt == -1 ? -1 : 2 * paramInt;
  }
  
  private static ToolkitImage getResolutionVariant(Image paramImage)
  {
    if ((paramImage instanceof MultiResolutionToolkitImage))
    {
      Image localImage = ((MultiResolutionToolkitImage)paramImage).getResolutionVariant();
      if ((localImage instanceof ToolkitImage)) {
        return (ToolkitImage)localImage;
      }
    }
    return null;
  }
  
  protected static boolean imageCached(String paramString)
  {
    return fileImgCache.containsKey(paramString);
  }
  
  protected static boolean imageCached(URL paramURL)
  {
    String str = paramURL.toString();
    return urlImgCache.containsKey(str);
  }
  
  protected static boolean imageExists(String paramString)
  {
    if (paramString != null)
    {
      checkPermissions(paramString);
      return new File(paramString).exists();
    }
    return false;
  }
  
  protected static boolean imageExists(URL paramURL)
  {
    if (paramURL != null)
    {
      checkPermissions(paramURL);
      try
      {
        InputStream localInputStream = paramURL.openStream();
        Object localObject1 = null;
        try
        {
          boolean bool = true;
          return bool;
        }
        catch (Throwable localThrowable1)
        {
          localObject1 = localThrowable1;
          throw localThrowable1;
        }
        finally
        {
          if (localInputStream != null) {
            if (localObject1 != null) {
              try
              {
                localInputStream.close();
              }
              catch (Throwable localThrowable3)
              {
                ((Throwable)localObject1).addSuppressed(localThrowable3);
              }
            } else {
              localInputStream.close();
            }
          }
        }
        return false;
      }
      catch (IOException localIOException)
      {
        return false;
      }
    }
  }
  
  private static void checkPermissions(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(paramString);
    }
  }
  
  private static void checkPermissions(URL paramURL)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        Permission localPermission = URLUtil.getConnectPermission(paramURL);
        if (localPermission != null) {
          try
          {
            localSecurityManager.checkPermission(localPermission);
          }
          catch (SecurityException localSecurityException)
          {
            if (((localPermission instanceof FilePermission)) && (localPermission.getActions().indexOf("read") != -1)) {
              localSecurityManager.checkRead(localPermission.getName());
            } else if (((localPermission instanceof SocketPermission)) && (localPermission.getActions().indexOf("connect") != -1)) {
              localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
            } else {
              throw localSecurityException;
            }
          }
        }
      }
      catch (IOException localIOException)
      {
        localSecurityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
      }
    }
  }
  
  public static BufferedImage getScaledIconImage(java.util.List<Image> paramList, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      return null;
    }
    Object localObject1 = null;
    int i = 0;
    int j = 0;
    double d1 = 3.0D;
    double d2 = 0.0D;
    Object localObject2 = paramList.iterator();
    int k;
    int m;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Image)((Iterator)localObject2).next();
      if (localObject3 != null)
      {
        if ((localObject3 instanceof ToolkitImage))
        {
          ImageRepresentation localImageRepresentation = ((ToolkitImage)localObject3).getImageRep();
          localImageRepresentation.reconstruct(32);
        }
        try
        {
          k = ((Image)localObject3).getWidth(null);
          m = ((Image)localObject3).getHeight(null);
        }
        catch (Exception localException) {}
        continue;
        if ((k > 0) && (m > 0))
        {
          double d3 = Math.min(paramInt1 / k, paramInt2 / m);
          int n = 0;
          int i1 = 0;
          double d4 = 1.0D;
          if (d3 >= 2.0D)
          {
            d3 = Math.floor(d3);
            n = k * (int)d3;
            i1 = m * (int)d3;
            d4 = 1.0D - 0.5D / d3;
          }
          else if (d3 >= 1.0D)
          {
            d3 = 1.0D;
            n = k;
            i1 = m;
            d4 = 0.0D;
          }
          else if (d3 >= 0.75D)
          {
            d3 = 0.75D;
            n = k * 3 / 4;
            i1 = m * 3 / 4;
            d4 = 0.3D;
          }
          else if (d3 >= 0.6666D)
          {
            d3 = 0.6666D;
            n = k * 2 / 3;
            i1 = m * 2 / 3;
            d4 = 0.33D;
          }
          else
          {
            d5 = Math.ceil(1.0D / d3);
            d3 = 1.0D / d5;
            n = (int)Math.round(k / d5);
            i1 = (int)Math.round(m / d5);
            d4 = 1.0D - 1.0D / d5;
          }
          double d5 = (paramInt1 - n) / paramInt1 + (paramInt2 - i1) / paramInt2 + d4;
          if (d5 < d1)
          {
            d1 = d5;
            d2 = d3;
            localObject1 = localObject3;
            i = n;
            j = i1;
          }
          if (d5 == 0.0D) {
            break;
          }
        }
      }
    }
    if (localObject1 == null) {
      return null;
    }
    localObject2 = new BufferedImage(paramInt1, paramInt2, 2);
    Object localObject3 = ((BufferedImage)localObject2).createGraphics();
    ((Graphics2D)localObject3).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    try
    {
      k = (paramInt1 - i) / 2;
      m = (paramInt2 - j) / 2;
      ((Graphics2D)localObject3).drawImage((Image)localObject1, k, m, i, j, null);
    }
    finally
    {
      ((Graphics2D)localObject3).dispose();
    }
    return (BufferedImage)localObject2;
  }
  
  public static DataBufferInt getScaledIconData(java.util.List<Image> paramList, int paramInt1, int paramInt2)
  {
    BufferedImage localBufferedImage = getScaledIconImage(paramList, paramInt1, paramInt2);
    if (localBufferedImage == null) {
      return null;
    }
    WritableRaster localWritableRaster = localBufferedImage.getRaster();
    DataBuffer localDataBuffer = localWritableRaster.getDataBuffer();
    return (DataBufferInt)localDataBuffer;
  }
  
  protected EventQueue getSystemEventQueueImpl()
  {
    return getSystemEventQueueImplPP();
  }
  
  static EventQueue getSystemEventQueueImplPP()
  {
    return getSystemEventQueueImplPP(AppContext.getAppContext());
  }
  
  public static EventQueue getSystemEventQueueImplPP(AppContext paramAppContext)
  {
    EventQueue localEventQueue = (EventQueue)paramAppContext.get(AppContext.EVENT_QUEUE_KEY);
    return localEventQueue;
  }
  
  public static Container getNativeContainer(Component paramComponent)
  {
    return Toolkit.getNativeContainer(paramComponent);
  }
  
  public static Component getHeavyweightComponent(Component paramComponent)
  {
    while ((paramComponent != null) && (AWTAccessor.getComponentAccessor().isLightweight(paramComponent))) {
      paramComponent = AWTAccessor.getComponentAccessor().getParent(paramComponent);
    }
    return paramComponent;
  }
  
  public int getFocusAcceleratorKeyMask()
  {
    return 8;
  }
  
  public boolean isPrintableCharacterModifiersMask(int paramInt)
  {
    return (paramInt & 0x8) == (paramInt & 0x2);
  }
  
  public boolean canPopupOverlapTaskBar()
  {
    boolean bool = true;
    try
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(SecurityConstants.AWT.SET_WINDOW_ALWAYS_ON_TOP_PERMISSION);
      }
    }
    catch (SecurityException localSecurityException)
    {
      bool = false;
    }
    return bool;
  }
  
  public Window createInputMethodWindow(String paramString, InputContext paramInputContext)
  {
    return new SimpleInputMethodWindow(paramString, paramInputContext);
  }
  
  public boolean enableInputMethodsForTextComponent()
  {
    return false;
  }
  
  public static Locale getStartupLocale()
  {
    if (startupLocale == null)
    {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.language", "en"));
      String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("user.region"));
      String str3;
      String str4;
      if (str2 != null)
      {
        int i = str2.indexOf('_');
        if (i >= 0)
        {
          str3 = str2.substring(0, i);
          str4 = str2.substring(i + 1);
        }
        else
        {
          str3 = str2;
          str4 = "";
        }
      }
      else
      {
        str3 = (String)AccessController.doPrivileged(new GetPropertyAction("user.country", ""));
        str4 = (String)AccessController.doPrivileged(new GetPropertyAction("user.variant", ""));
      }
      startupLocale = new Locale(str1, str3, str4);
    }
    return startupLocale;
  }
  
  public Locale getDefaultKeyboardLocale()
  {
    return getStartupLocale();
  }
  
  public WindowClosingListener getWindowClosingListener()
  {
    return windowClosingListener;
  }
  
  public void setWindowClosingListener(WindowClosingListener paramWindowClosingListener)
  {
    windowClosingListener = paramWindowClosingListener;
  }
  
  public RuntimeException windowClosingNotify(WindowEvent paramWindowEvent)
  {
    if (windowClosingListener != null) {
      return windowClosingListener.windowClosingNotify(paramWindowEvent);
    }
    return null;
  }
  
  public RuntimeException windowClosingDelivered(WindowEvent paramWindowEvent)
  {
    if (windowClosingListener != null) {
      return windowClosingListener.windowClosingDelivered(paramWindowEvent);
    }
    return null;
  }
  
  protected synchronized MouseInfoPeer getMouseInfoPeer()
  {
    if (mPeer == null) {
      mPeer = new DefaultMouseInfoPeer();
    }
    return mPeer;
  }
  
  public static boolean needsXEmbed()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.awt.noxembed", "false"));
    if ("true".equals(str)) {
      return false;
    }
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit)) {
      return ((SunToolkit)localToolkit).needsXEmbedImpl();
    }
    return false;
  }
  
  protected boolean needsXEmbedImpl()
  {
    return false;
  }
  
  protected final boolean isXEmbedServerRequested()
  {
    return ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.xembedserver"))).booleanValue();
  }
  
  public static boolean isModalExcludedSupported()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    return localToolkit.isModalExclusionTypeSupported(DEFAULT_MODAL_EXCLUSION_TYPE);
  }
  
  protected boolean isModalExcludedSupportedImpl()
  {
    return false;
  }
  
  public static void setModalExcluded(Window paramWindow)
  {
    if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
      DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
    }
    paramWindow.setModalExclusionType(DEFAULT_MODAL_EXCLUSION_TYPE);
  }
  
  public static boolean isModalExcluded(Window paramWindow)
  {
    if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
      DEFAULT_MODAL_EXCLUSION_TYPE = Dialog.ModalExclusionType.APPLICATION_EXCLUDE;
    }
    return paramWindow.getModalExclusionType().compareTo(DEFAULT_MODAL_EXCLUSION_TYPE) >= 0;
  }
  
  public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType)
  {
    return (paramModalityType == Dialog.ModalityType.MODELESS) || (paramModalityType == Dialog.ModalityType.APPLICATION_MODAL);
  }
  
  public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType)
  {
    return paramModalExclusionType == Dialog.ModalExclusionType.NO_EXCLUDE;
  }
  
  public void addModalityListener(ModalityListener paramModalityListener)
  {
    modalityListeners.add(paramModalityListener);
  }
  
  public void removeModalityListener(ModalityListener paramModalityListener)
  {
    modalityListeners.remove(paramModalityListener);
  }
  
  public void notifyModalityPushed(Dialog paramDialog)
  {
    notifyModalityChange(1300, paramDialog);
  }
  
  public void notifyModalityPopped(Dialog paramDialog)
  {
    notifyModalityChange(1301, paramDialog);
  }
  
  final void notifyModalityChange(int paramInt, Dialog paramDialog)
  {
    ModalityEvent localModalityEvent = new ModalityEvent(paramDialog, modalityListeners, paramInt);
    localModalityEvent.dispatch();
  }
  
  public static boolean isLightweightOrUnknown(Component paramComponent)
  {
    if ((paramComponent.isLightweight()) || (!(getDefaultToolkit() instanceof SunToolkit))) {
      return true;
    }
    return (!(paramComponent instanceof Button)) && (!(paramComponent instanceof Canvas)) && (!(paramComponent instanceof Checkbox)) && (!(paramComponent instanceof Choice)) && (!(paramComponent instanceof Label)) && (!(paramComponent instanceof java.awt.List)) && (!(paramComponent instanceof Panel)) && (!(paramComponent instanceof Scrollbar)) && (!(paramComponent instanceof ScrollPane)) && (!(paramComponent instanceof TextArea)) && (!(paramComponent instanceof TextField)) && (!(paramComponent instanceof Window));
  }
  
  public void realSync()
    throws SunToolkit.OperationTimedOut, SunToolkit.InfiniteLoop
  {
    realSync(10000L);
  }
  
  public void realSync(long paramLong)
    throws SunToolkit.OperationTimedOut, SunToolkit.InfiniteLoop
  {
    if (EventQueue.isDispatchThread()) {
      throw new IllegalThreadException("The SunToolkit.realSync() method cannot be used on the event dispatch thread (EDT).");
    }
    int i = 0;
    do
    {
      sync();
      for (int j = 0; j < 0; j++) {
        syncNativeQueue(paramLong);
      }
      while ((syncNativeQueue(paramLong)) && (j < 20)) {
        j++;
      }
      if (j >= 20) {
        throw new InfiniteLoop();
      }
      for (j = 0; j < 0; j++) {
        waitForIdle(paramLong);
      }
      while ((waitForIdle(paramLong)) && (j < 20)) {
        j++;
      }
      if (j >= 20) {
        throw new InfiniteLoop();
      }
      i++;
    } while (((syncNativeQueue(paramLong)) || (waitForIdle(paramLong))) && (i < 20));
  }
  
  protected abstract boolean syncNativeQueue(long paramLong);
  
  private boolean isEQEmpty()
  {
    EventQueue localEventQueue = getSystemEventQueueImpl();
    return AWTAccessor.getEventQueueAccessor().noEvents(localEventQueue);
  }
  
  protected final boolean waitForIdle(long paramLong)
  {
    flushPendingEvents();
    boolean bool = isEQEmpty();
    queueEmpty = false;
    eventDispatched = false;
    synchronized (waitLock)
    {
      postEvent(AppContext.getAppContext(), new PeerEvent(getSystemEventQueueImpl(), null, 4L)
      {
        public void dispatch()
        {
          for (int i = 0; i < 0; i++) {
            syncNativeQueue(val$timeout);
          }
          while ((syncNativeQueue(val$timeout)) && (i < 20)) {
            i++;
          }
          SunToolkit.flushPendingEvents();
          synchronized (waitLock)
          {
            queueEmpty = SunToolkit.this.isEQEmpty();
            eventDispatched = true;
            waitLock.notifyAll();
          }
        }
      });
      try
      {
        while (!eventDispatched) {
          waitLock.wait();
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        return false;
      }
    }
    try
    {
      Thread.sleep(0L);
    }
    catch (InterruptedException ???)
    {
      throw new RuntimeException("Interrupted");
    }
    flushPendingEvents();
    synchronized (waitLock)
    {
      return (!queueEmpty) || (!isEQEmpty()) || (!bool);
    }
  }
  
  public abstract void grab(Window paramWindow);
  
  public abstract void ungrab(Window paramWindow);
  
  public static native void closeSplashScreen();
  
  private void fireDesktopFontPropertyChanges()
  {
    setDesktopProperty("awt.font.desktophints", getDesktopFontHints());
  }
  
  public static void setAAFontSettingsCondition(boolean paramBoolean)
  {
    if (paramBoolean != lastExtraCondition)
    {
      lastExtraCondition = paramBoolean;
      if (checkedSystemAAFontSettings)
      {
        checkedSystemAAFontSettings = false;
        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        if ((localToolkit instanceof SunToolkit)) {
          ((SunToolkit)localToolkit).fireDesktopFontPropertyChanges();
        }
      }
    }
  }
  
  private static RenderingHints getDesktopAAHintsByName(String paramString)
  {
    Object localObject = null;
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    if (paramString.equals("on")) {
      localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    } else if (paramString.equals("gasp")) {
      localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
    } else if ((paramString.equals("lcd")) || (paramString.equals("lcd_hrgb"))) {
      localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
    } else if (paramString.equals("lcd_hbgr")) {
      localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
    } else if (paramString.equals("lcd_vrgb")) {
      localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
    } else if (paramString.equals("lcd_vbgr")) {
      localObject = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
    }
    if (localObject != null)
    {
      RenderingHints localRenderingHints = new RenderingHints(null);
      localRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, localObject);
      return localRenderingHints;
    }
    return null;
  }
  
  private static boolean useSystemAAFontSettings()
  {
    if (!checkedSystemAAFontSettings)
    {
      useSystemAAFontSettings = true;
      String str = null;
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      if ((localToolkit instanceof SunToolkit)) {
        str = (String)AccessController.doPrivileged(new GetPropertyAction("awt.useSystemAAFontSettings"));
      }
      if (str != null)
      {
        useSystemAAFontSettings = Boolean.valueOf(str).booleanValue();
        if (!useSystemAAFontSettings) {
          desktopFontHints = getDesktopAAHintsByName(str);
        }
      }
      if (useSystemAAFontSettings) {
        useSystemAAFontSettings = lastExtraCondition;
      }
      checkedSystemAAFontSettings = true;
    }
    return useSystemAAFontSettings;
  }
  
  protected RenderingHints getDesktopAAHints()
  {
    return null;
  }
  
  public static RenderingHints getDesktopFontHints()
  {
    if (useSystemAAFontSettings())
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      if ((localToolkit instanceof SunToolkit))
      {
        RenderingHints localRenderingHints = ((SunToolkit)localToolkit).getDesktopAAHints();
        return (RenderingHints)localRenderingHints;
      }
      return null;
    }
    if (desktopFontHints != null) {
      return (RenderingHints)desktopFontHints.clone();
    }
    return null;
  }
  
  public abstract boolean isDesktopSupported();
  
  public static synchronized void consumeNextKeyTyped(KeyEvent paramKeyEvent)
  {
    try
    {
      AWTAccessor.getDefaultKeyboardFocusManagerAccessor().consumeNextKeyTyped((DefaultKeyboardFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager(), paramKeyEvent);
    }
    catch (ClassCastException localClassCastException)
    {
      localClassCastException.printStackTrace();
    }
  }
  
  protected static void dumpPeers(PlatformLogger paramPlatformLogger)
  {
    AWTAutoShutdown.getInstance().dumpPeers(paramPlatformLogger);
  }
  
  public static Window getContainingWindow(Component paramComponent)
  {
    while ((paramComponent != null) && (!(paramComponent instanceof Window))) {
      paramComponent = paramComponent.getParent();
    }
    return (Window)paramComponent;
  }
  
  public static synchronized boolean getSunAwtDisableMixing()
  {
    if (sunAwtDisableMixing == null) {
      sunAwtDisableMixing = (Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.disableMixing"));
    }
    return sunAwtDisableMixing.booleanValue();
  }
  
  public boolean isNativeGTKAvailable()
  {
    return false;
  }
  
  public synchronized void setWindowDeactivationTime(Window paramWindow, long paramLong)
  {
    AppContext localAppContext = getAppContext(paramWindow);
    WeakHashMap localWeakHashMap = (WeakHashMap)localAppContext.get(DEACTIVATION_TIMES_MAP_KEY);
    if (localWeakHashMap == null)
    {
      localWeakHashMap = new WeakHashMap();
      localAppContext.put(DEACTIVATION_TIMES_MAP_KEY, localWeakHashMap);
    }
    localWeakHashMap.put(paramWindow, Long.valueOf(paramLong));
  }
  
  public synchronized long getWindowDeactivationTime(Window paramWindow)
  {
    AppContext localAppContext = getAppContext(paramWindow);
    WeakHashMap localWeakHashMap = (WeakHashMap)localAppContext.get(DEACTIVATION_TIMES_MAP_KEY);
    if (localWeakHashMap == null) {
      return -1L;
    }
    Long localLong = (Long)localWeakHashMap.get(paramWindow);
    return localLong == null ? -1L : localLong.longValue();
  }
  
  public boolean isWindowOpacitySupported()
  {
    return false;
  }
  
  public boolean isWindowShapingSupported()
  {
    return false;
  }
  
  public boolean isWindowTranslucencySupported()
  {
    return false;
  }
  
  public boolean isTranslucencyCapable(GraphicsConfiguration paramGraphicsConfiguration)
  {
    return false;
  }
  
  public boolean isSwingBackbufferTranslucencySupported()
  {
    return false;
  }
  
  public static boolean isContainingTopLevelOpaque(Component paramComponent)
  {
    Window localWindow = getContainingWindow(paramComponent);
    return (localWindow != null) && (localWindow.isOpaque());
  }
  
  public static boolean isContainingTopLevelTranslucent(Component paramComponent)
  {
    Window localWindow = getContainingWindow(paramComponent);
    return (localWindow != null) && (localWindow.getOpacity() < 1.0F);
  }
  
  public boolean needUpdateWindow()
  {
    return false;
  }
  
  public int getNumberOfButtons()
  {
    return 3;
  }
  
  public static boolean isInstanceOf(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      return false;
    }
    if (paramString == null) {
      return false;
    }
    return isInstanceOf(paramObject.getClass(), paramString);
  }
  
  private static boolean isInstanceOf(Class<?> paramClass, String paramString)
  {
    if (paramClass == null) {
      return false;
    }
    if (paramClass.getName().equals(paramString)) {
      return true;
    }
    for (Class localClass : paramClass.getInterfaces()) {
      if (localClass.getName().equals(paramString)) {
        return true;
      }
    }
    return isInstanceOf(paramClass.getSuperclass(), paramString);
  }
  
  protected static LightweightFrame getLightweightFrame(Component paramComponent)
  {
    while (paramComponent != null)
    {
      if ((paramComponent instanceof LightweightFrame)) {
        return (LightweightFrame)paramComponent;
      }
      if ((paramComponent instanceof Window)) {
        return null;
      }
      paramComponent = paramComponent.getParent();
    }
    return null;
  }
  
  public static void setSystemGenerated(AWTEvent paramAWTEvent)
  {
    AWTAccessor.getAWTEventAccessor().setSystemGenerated(paramAWTEvent);
  }
  
  public static boolean isSystemGenerated(AWTEvent paramAWTEvent)
  {
    return AWTAccessor.getAWTEventAccessor().isSystemGenerated(paramAWTEvent);
  }
  
  static
  {
    if (((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.awt.nativedebug"))).booleanValue()) {
      DebugSettings.init();
    }
  }
  
  public static class IllegalThreadException
    extends RuntimeException
  {
    public IllegalThreadException(String paramString)
    {
      super();
    }
    
    public IllegalThreadException() {}
  }
  
  public static class InfiniteLoop
    extends RuntimeException
  {
    public InfiniteLoop() {}
  }
  
  static class ModalityListenerList
    implements ModalityListener
  {
    Vector<ModalityListener> listeners = new Vector();
    
    ModalityListenerList() {}
    
    void add(ModalityListener paramModalityListener)
    {
      listeners.addElement(paramModalityListener);
    }
    
    void remove(ModalityListener paramModalityListener)
    {
      listeners.removeElement(paramModalityListener);
    }
    
    public void modalityPushed(ModalityEvent paramModalityEvent)
    {
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext()) {
        ((ModalityListener)localIterator.next()).modalityPushed(paramModalityEvent);
      }
    }
    
    public void modalityPopped(ModalityEvent paramModalityEvent)
    {
      Iterator localIterator = listeners.iterator();
      while (localIterator.hasNext()) {
        ((ModalityListener)localIterator.next()).modalityPopped(paramModalityEvent);
      }
    }
  }
  
  public static class OperationTimedOut
    extends RuntimeException
  {
    public OperationTimedOut(String paramString)
    {
      super();
    }
    
    public OperationTimedOut() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\SunToolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */