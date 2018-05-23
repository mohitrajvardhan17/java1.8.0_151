package java.awt;

import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
import java.awt.event.AWTEventListenerProxy;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ToolkitAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.NullComponentPeer;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.UngrabEvent;
import sun.security.util.SecurityConstants.AWT;
import sun.util.CoreResourceBundleControl;

public abstract class Toolkit
{
  private static LightweightPeer lightweightMarker;
  private static Toolkit toolkit;
  private static String atNames;
  private static ResourceBundle resources;
  private static ResourceBundle platformResources;
  private static boolean loaded;
  protected final Map<String, Object> desktopProperties = new HashMap();
  protected final PropertyChangeSupport desktopPropsSupport = createPropertyChangeSupport(this);
  private static final int LONG_BITS = 64;
  private int[] calls = new int[64];
  private static volatile long enabledOnToolkitMask;
  private AWTEventListener eventListener = null;
  private WeakHashMap<AWTEventListener, SelectiveAWTEventListener> listener2SelectiveListener = new WeakHashMap();
  
  public Toolkit() {}
  
  protected abstract DesktopPeer createDesktopPeer(Desktop paramDesktop)
    throws HeadlessException;
  
  protected abstract ButtonPeer createButton(Button paramButton)
    throws HeadlessException;
  
  protected abstract TextFieldPeer createTextField(TextField paramTextField)
    throws HeadlessException;
  
  protected abstract LabelPeer createLabel(Label paramLabel)
    throws HeadlessException;
  
  protected abstract ListPeer createList(List paramList)
    throws HeadlessException;
  
  protected abstract CheckboxPeer createCheckbox(Checkbox paramCheckbox)
    throws HeadlessException;
  
  protected abstract ScrollbarPeer createScrollbar(Scrollbar paramScrollbar)
    throws HeadlessException;
  
  protected abstract ScrollPanePeer createScrollPane(ScrollPane paramScrollPane)
    throws HeadlessException;
  
  protected abstract TextAreaPeer createTextArea(TextArea paramTextArea)
    throws HeadlessException;
  
  protected abstract ChoicePeer createChoice(Choice paramChoice)
    throws HeadlessException;
  
  protected abstract FramePeer createFrame(Frame paramFrame)
    throws HeadlessException;
  
  protected abstract CanvasPeer createCanvas(Canvas paramCanvas);
  
  protected abstract PanelPeer createPanel(Panel paramPanel);
  
  protected abstract WindowPeer createWindow(Window paramWindow)
    throws HeadlessException;
  
  protected abstract DialogPeer createDialog(Dialog paramDialog)
    throws HeadlessException;
  
  protected abstract MenuBarPeer createMenuBar(MenuBar paramMenuBar)
    throws HeadlessException;
  
  protected abstract MenuPeer createMenu(Menu paramMenu)
    throws HeadlessException;
  
  protected abstract PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu)
    throws HeadlessException;
  
  protected abstract MenuItemPeer createMenuItem(MenuItem paramMenuItem)
    throws HeadlessException;
  
  protected abstract FileDialogPeer createFileDialog(FileDialog paramFileDialog)
    throws HeadlessException;
  
  protected abstract CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem)
    throws HeadlessException;
  
  protected MouseInfoPeer getMouseInfoPeer()
  {
    throw new UnsupportedOperationException("Not implemented");
  }
  
  protected LightweightPeer createComponent(Component paramComponent)
  {
    if (lightweightMarker == null) {
      lightweightMarker = new NullComponentPeer();
    }
    return lightweightMarker;
  }
  
  @Deprecated
  protected abstract FontPeer getFontPeer(String paramString, int paramInt);
  
  protected void loadSystemColors(int[] paramArrayOfInt)
    throws HeadlessException
  {}
  
  public void setDynamicLayout(boolean paramBoolean)
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      getDefaultToolkit().setDynamicLayout(paramBoolean);
    }
  }
  
  protected boolean isDynamicLayoutSet()
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().isDynamicLayoutSet();
    }
    return false;
  }
  
  public boolean isDynamicLayoutActive()
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().isDynamicLayoutActive();
    }
    return false;
  }
  
  public abstract Dimension getScreenSize()
    throws HeadlessException;
  
  public abstract int getScreenResolution()
    throws HeadlessException;
  
  public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration)
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().getScreenInsets(paramGraphicsConfiguration);
    }
    return new Insets(0, 0, 0, 0);
  }
  
  public abstract ColorModel getColorModel()
    throws HeadlessException;
  
  @Deprecated
  public abstract String[] getFontList();
  
  @Deprecated
  public abstract FontMetrics getFontMetrics(Font paramFont);
  
  public abstract void sync();
  
  private static void initAssistiveTechnologies()
  {
    String str = File.separator;
    final Properties localProperties = new Properties();
    atNames = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        try
        {
          File localFile1 = new File(System.getProperty("user.home") + val$sep + ".accessibility.properties");
          localObject = new FileInputStream(localFile1);
          localProperties.load((InputStream)localObject);
          ((FileInputStream)localObject).close();
        }
        catch (Exception localException1) {}
        if (localProperties.size() == 0) {
          try
          {
            File localFile2 = new File(System.getProperty("java.home") + val$sep + "lib" + val$sep + "accessibility.properties");
            localObject = new FileInputStream(localFile2);
            localProperties.load((InputStream)localObject);
            ((FileInputStream)localObject).close();
          }
          catch (Exception localException2) {}
        }
        String str = System.getProperty("javax.accessibility.screen_magnifier_present");
        if (str == null)
        {
          str = localProperties.getProperty("screen_magnifier_present", null);
          if (str != null) {
            System.setProperty("javax.accessibility.screen_magnifier_present", str);
          }
        }
        Object localObject = System.getProperty("javax.accessibility.assistive_technologies");
        if (localObject == null)
        {
          localObject = localProperties.getProperty("assistive_technologies", null);
          if (localObject != null) {
            System.setProperty("javax.accessibility.assistive_technologies", (String)localObject);
          }
        }
        return (String)localObject;
      }
    });
  }
  
  private static void loadAssistiveTechnologies()
  {
    if (atNames != null)
    {
      ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
      StringTokenizer localStringTokenizer = new StringTokenizer(atNames, " ,");
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken();
        try
        {
          Class localClass;
          if (localClassLoader != null) {
            localClass = localClassLoader.loadClass(str);
          } else {
            localClass = Class.forName(str);
          }
          localClass.newInstance();
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new AWTError("Assistive Technology not found: " + str);
        }
        catch (InstantiationException localInstantiationException)
        {
          throw new AWTError("Could not instantiate Assistive Technology: " + str);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new AWTError("Could not access Assistive Technology: " + str);
        }
        catch (Exception localException)
        {
          throw new AWTError("Error trying to install Assistive Technology: " + str + " " + localException);
        }
      }
    }
  }
  
  public static synchronized Toolkit getDefaultToolkit()
  {
    if (toolkit == null)
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          Class localClass = null;
          String str = System.getProperty("awt.toolkit");
          try
          {
            localClass = Class.forName(str);
          }
          catch (ClassNotFoundException localClassNotFoundException1)
          {
            ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
            if (localClassLoader != null) {
              try
              {
                localClass = localClassLoader.loadClass(str);
              }
              catch (ClassNotFoundException localClassNotFoundException2)
              {
                throw new AWTError("Toolkit not found: " + str);
              }
            }
          }
          try
          {
            if (localClass != null)
            {
              Toolkit.access$002((Toolkit)localClass.newInstance());
              if (GraphicsEnvironment.isHeadless()) {
                Toolkit.access$002(new HeadlessToolkit(Toolkit.toolkit));
              }
            }
          }
          catch (InstantiationException localInstantiationException)
          {
            throw new AWTError("Could not instantiate Toolkit: " + str);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new AWTError("Could not access Toolkit: " + str);
          }
          return null;
        }
      });
      loadAssistiveTechnologies();
    }
    return toolkit;
  }
  
  public abstract Image getImage(String paramString);
  
  public abstract Image getImage(URL paramURL);
  
  public abstract Image createImage(String paramString);
  
  public abstract Image createImage(URL paramURL);
  
  public abstract boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
  
  public abstract int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
  
  public abstract Image createImage(ImageProducer paramImageProducer);
  
  public Image createImage(byte[] paramArrayOfByte)
  {
    return createImage(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public abstract Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties);
  
  public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes)
  {
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().getPrintJob(paramFrame, paramString, paramJobAttributes, paramPageAttributes);
    }
    return getPrintJob(paramFrame, paramString, null);
  }
  
  public abstract void beep();
  
  public abstract Clipboard getSystemClipboard()
    throws HeadlessException;
  
  public Clipboard getSystemSelection()
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().getSystemSelection();
    }
    GraphicsEnvironment.checkHeadless();
    return null;
  }
  
  public int getMenuShortcutKeyMask()
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    return 2;
  }
  
  public boolean getLockingKeyState(int paramInt)
    throws UnsupportedOperationException
  {
    
    if ((paramInt != 20) && (paramInt != 144) && (paramInt != 145) && (paramInt != 262)) {
      throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
    }
    throw new UnsupportedOperationException("Toolkit.getLockingKeyState");
  }
  
  public void setLockingKeyState(int paramInt, boolean paramBoolean)
    throws UnsupportedOperationException
  {
    
    if ((paramInt != 20) && (paramInt != 144) && (paramInt != 145) && (paramInt != 262)) {
      throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState");
    }
    throw new UnsupportedOperationException("Toolkit.setLockingKeyState");
  }
  
  protected static Container getNativeContainer(Component paramComponent)
  {
    return paramComponent.getNativeContainer();
  }
  
  public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString)
    throws IndexOutOfBoundsException, HeadlessException
  {
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().createCustomCursor(paramImage, paramPoint, paramString);
    }
    return new Cursor(0);
  }
  
  public Dimension getBestCursorSize(int paramInt1, int paramInt2)
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().getBestCursorSize(paramInt1, paramInt2);
    }
    return new Dimension(0, 0);
  }
  
  public int getMaximumCursorColors()
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().getMaximumCursorColors();
    }
    return 0;
  }
  
  public boolean isFrameStateSupported(int paramInt)
    throws HeadlessException
  {
    
    if (this != getDefaultToolkit()) {
      return getDefaultToolkit().isFrameStateSupported(paramInt);
    }
    return paramInt == 0;
  }
  
  private static void setPlatformResources(ResourceBundle paramResourceBundle)
  {
    platformResources = paramResourceBundle;
  }
  
  private static native void initIDs();
  
  static void loadLibraries()
  {
    if (!loaded)
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          System.loadLibrary("awt");
          return null;
        }
      });
      loaded = true;
    }
  }
  
  public static String getProperty(String paramString1, String paramString2)
  {
    if (platformResources != null) {
      try
      {
        return platformResources.getString(paramString1);
      }
      catch (MissingResourceException localMissingResourceException1) {}
    }
    if (resources != null) {
      try
      {
        return resources.getString(paramString1);
      }
      catch (MissingResourceException localMissingResourceException2) {}
    }
    return paramString2;
  }
  
  public final EventQueue getSystemEventQueue()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
    }
    return getSystemEventQueueImpl();
  }
  
  protected abstract EventQueue getSystemEventQueueImpl();
  
  static EventQueue getEventQueue()
  {
    return getDefaultToolkit().getSystemEventQueueImpl();
  }
  
  public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent)
    throws InvalidDnDOperationException;
  
  public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener)
  {
    return null;
  }
  
  public final synchronized Object getDesktopProperty(String paramString)
  {
    if ((this instanceof HeadlessToolkit)) {
      return ((HeadlessToolkit)this).getUnderlyingToolkit().getDesktopProperty(paramString);
    }
    if (desktopProperties.isEmpty()) {
      initializeDesktopProperties();
    }
    if (paramString.equals("awt.dynamicLayoutSupported")) {
      return getDefaultToolkit().lazilyLoadDesktopProperty(paramString);
    }
    Object localObject = desktopProperties.get(paramString);
    if (localObject == null)
    {
      localObject = lazilyLoadDesktopProperty(paramString);
      if (localObject != null) {
        setDesktopProperty(paramString, localObject);
      }
    }
    if ((localObject instanceof RenderingHints)) {
      localObject = ((RenderingHints)localObject).clone();
    }
    return localObject;
  }
  
  protected final void setDesktopProperty(String paramString, Object paramObject)
  {
    if ((this instanceof HeadlessToolkit))
    {
      ((HeadlessToolkit)this).getUnderlyingToolkit().setDesktopProperty(paramString, paramObject);
      return;
    }
    Object localObject1;
    synchronized (this)
    {
      localObject1 = desktopProperties.get(paramString);
      desktopProperties.put(paramString, paramObject);
    }
    if ((localObject1 != null) || (paramObject != null)) {
      desktopPropsSupport.firePropertyChange(paramString, localObject1, paramObject);
    }
  }
  
  protected Object lazilyLoadDesktopProperty(String paramString)
  {
    return null;
  }
  
  protected void initializeDesktopProperties() {}
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    desktopPropsSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    desktopPropsSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    return desktopPropsSupport.getPropertyChangeListeners();
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString)
  {
    return desktopPropsSupport.getPropertyChangeListeners(paramString);
  }
  
  public boolean isAlwaysOnTopSupported()
  {
    return true;
  }
  
  public abstract boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType);
  
  public abstract boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType);
  
  private static AWTEventListener deProxyAWTEventListener(AWTEventListener paramAWTEventListener)
  {
    AWTEventListener localAWTEventListener = paramAWTEventListener;
    if (localAWTEventListener == null) {
      return null;
    }
    if ((paramAWTEventListener instanceof AWTEventListenerProxy)) {
      localAWTEventListener = (AWTEventListener)((AWTEventListenerProxy)paramAWTEventListener).getListener();
    }
    return localAWTEventListener;
  }
  
  public void addAWTEventListener(AWTEventListener paramAWTEventListener, long paramLong)
  {
    AWTEventListener localAWTEventListener = deProxyAWTEventListener(paramAWTEventListener);
    if (localAWTEventListener == null) {
      return;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
    }
    synchronized (this)
    {
      SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)listener2SelectiveListener.get(localAWTEventListener);
      if (localSelectiveAWTEventListener == null)
      {
        localSelectiveAWTEventListener = new SelectiveAWTEventListener(localAWTEventListener, paramLong);
        listener2SelectiveListener.put(localAWTEventListener, localSelectiveAWTEventListener);
        eventListener = ToolkitEventMulticaster.add(eventListener, localSelectiveAWTEventListener);
      }
      localSelectiveAWTEventListener.orEventMasks(paramLong);
      enabledOnToolkitMask |= paramLong;
      long l = paramLong;
      for (int i = 0; (i < 64) && (l != 0L); i++)
      {
        if ((l & 1L) != 0L) {
          calls[i] += 1;
        }
        l >>>= 1;
      }
    }
  }
  
  public void removeAWTEventListener(AWTEventListener paramAWTEventListener)
  {
    AWTEventListener localAWTEventListener = deProxyAWTEventListener(paramAWTEventListener);
    if (paramAWTEventListener == null) {
      return;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
    }
    synchronized (this)
    {
      SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)listener2SelectiveListener.get(localAWTEventListener);
      if (localSelectiveAWTEventListener != null)
      {
        listener2SelectiveListener.remove(localAWTEventListener);
        int[] arrayOfInt = localSelectiveAWTEventListener.getCalls();
        for (int i = 0; i < 64; i++)
        {
          calls[i] -= arrayOfInt[i];
          assert (calls[i] >= 0) : "Negative Listeners count";
          if (calls[i] == 0) {
            enabledOnToolkitMask &= (1L << i ^ 0xFFFFFFFFFFFFFFFF);
          }
        }
      }
      eventListener = ToolkitEventMulticaster.remove(eventListener, localSelectiveAWTEventListener == null ? localAWTEventListener : localSelectiveAWTEventListener);
    }
  }
  
  static boolean enabledOnToolkit(long paramLong)
  {
    return (enabledOnToolkitMask & paramLong) != 0L;
  }
  
  synchronized int countAWTEventListeners(long paramLong)
  {
    for (int i = 0; paramLong != 0L; i++) {
      paramLong >>>= 1;
    }
    i--;
    return calls[i];
  }
  
  public AWTEventListener[] getAWTEventListeners()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
    }
    synchronized (this)
    {
      EventListener[] arrayOfEventListener = ToolkitEventMulticaster.getListeners(eventListener, AWTEventListener.class);
      AWTEventListener[] arrayOfAWTEventListener = new AWTEventListener[arrayOfEventListener.length];
      for (int i = 0; i < arrayOfEventListener.length; i++)
      {
        SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)arrayOfEventListener[i];
        AWTEventListener localAWTEventListener = localSelectiveAWTEventListener.getListener();
        arrayOfAWTEventListener[i] = new AWTEventListenerProxy(localSelectiveAWTEventListener.getEventMask(), localAWTEventListener);
      }
      return arrayOfAWTEventListener;
    }
  }
  
  public AWTEventListener[] getAWTEventListeners(long paramLong)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
    }
    synchronized (this)
    {
      EventListener[] arrayOfEventListener = ToolkitEventMulticaster.getListeners(eventListener, AWTEventListener.class);
      ArrayList localArrayList = new ArrayList(arrayOfEventListener.length);
      for (int i = 0; i < arrayOfEventListener.length; i++)
      {
        SelectiveAWTEventListener localSelectiveAWTEventListener = (SelectiveAWTEventListener)arrayOfEventListener[i];
        if ((localSelectiveAWTEventListener.getEventMask() & paramLong) == paramLong) {
          localArrayList.add(new AWTEventListenerProxy(localSelectiveAWTEventListener.getEventMask(), localSelectiveAWTEventListener.getListener()));
        }
      }
      return (AWTEventListener[])localArrayList.toArray(new AWTEventListener[0]);
    }
  }
  
  void notifyAWTEventListeners(AWTEvent paramAWTEvent)
  {
    if ((this instanceof HeadlessToolkit))
    {
      ((HeadlessToolkit)this).getUnderlyingToolkit().notifyAWTEventListeners(paramAWTEvent);
      return;
    }
    AWTEventListener localAWTEventListener = eventListener;
    if (localAWTEventListener != null) {
      localAWTEventListener.eventDispatched(paramAWTEvent);
    }
  }
  
  public abstract Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight)
    throws HeadlessException;
  
  private static PropertyChangeSupport createPropertyChangeSupport(Toolkit paramToolkit)
  {
    if (((paramToolkit instanceof SunToolkit)) || ((paramToolkit instanceof HeadlessToolkit))) {
      return new DesktopPropertyChangeSupport(paramToolkit);
    }
    return new PropertyChangeSupport(paramToolkit);
  }
  
  public boolean areExtraMouseButtonsEnabled()
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    return getDefaultToolkit().areExtraMouseButtonsEnabled();
  }
  
  static
  {
    loaded = false;
    AWTAccessor.setToolkitAccessor(new AWTAccessor.ToolkitAccessor()
    {
      public void setPlatformResources(ResourceBundle paramAnonymousResourceBundle)
      {
        Toolkit.setPlatformResources(paramAnonymousResourceBundle);
      }
    });
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        try
        {
          Toolkit.access$202(ResourceBundle.getBundle("sun.awt.resources.awt", CoreResourceBundleControl.getRBControlInstance()));
        }
        catch (MissingResourceException localMissingResourceException) {}
        return null;
      }
    });
    loadLibraries();
    initAssistiveTechnologies();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
  
  private static class DesktopPropertyChangeSupport
    extends PropertyChangeSupport
  {
    private static final StringBuilder PROP_CHANGE_SUPPORT_KEY = new StringBuilder("desktop property change support key");
    private final Object source;
    
    public DesktopPropertyChangeSupport(Object paramObject)
    {
      super();
      source = paramObject;
    }
    
    public synchronized void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
    {
      PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null == localPropertyChangeSupport)
      {
        localPropertyChangeSupport = new PropertyChangeSupport(source);
        AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, localPropertyChangeSupport);
      }
      localPropertyChangeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
    {
      PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null != localPropertyChangeSupport) {
        localPropertyChangeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
      }
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners()
    {
      PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null != localPropertyChangeSupport) {
        return localPropertyChangeSupport.getPropertyChangeListeners();
      }
      return new PropertyChangeListener[0];
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String paramString)
    {
      PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null != localPropertyChangeSupport) {
        return localPropertyChangeSupport.getPropertyChangeListeners(paramString);
      }
      return new PropertyChangeListener[0];
    }
    
    public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    {
      PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null == localPropertyChangeSupport)
      {
        localPropertyChangeSupport = new PropertyChangeSupport(source);
        AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, localPropertyChangeSupport);
      }
      localPropertyChangeSupport.addPropertyChangeListener(paramPropertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
    {
      PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null != localPropertyChangeSupport) {
        localPropertyChangeSupport.removePropertyChangeListener(paramPropertyChangeListener);
      }
    }
    
    public void firePropertyChange(final PropertyChangeEvent paramPropertyChangeEvent)
    {
      Object localObject1 = paramPropertyChangeEvent.getOldValue();
      Object localObject2 = paramPropertyChangeEvent.getNewValue();
      String str = paramPropertyChangeEvent.getPropertyName();
      if ((localObject1 != null) && (localObject2 != null) && (localObject1.equals(localObject2))) {
        return;
      }
      Runnable local1 = new Runnable()
      {
        public void run()
        {
          PropertyChangeSupport localPropertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(Toolkit.DesktopPropertyChangeSupport.PROP_CHANGE_SUPPORT_KEY);
          if (null != localPropertyChangeSupport) {
            localPropertyChangeSupport.firePropertyChange(paramPropertyChangeEvent);
          }
        }
      };
      AppContext localAppContext1 = AppContext.getAppContext();
      Iterator localIterator = AppContext.getAppContexts().iterator();
      while (localIterator.hasNext())
      {
        AppContext localAppContext2 = (AppContext)localIterator.next();
        if ((null != localAppContext2) && (!localAppContext2.isDisposed())) {
          if (localAppContext1 == localAppContext2)
          {
            local1.run();
          }
          else
          {
            PeerEvent localPeerEvent = new PeerEvent(source, local1, 2L);
            SunToolkit.postEvent(localAppContext2, localPeerEvent);
          }
        }
      }
    }
  }
  
  private class SelectiveAWTEventListener
    implements AWTEventListener
  {
    AWTEventListener listener;
    private long eventMask;
    int[] calls = new int[64];
    
    public AWTEventListener getListener()
    {
      return listener;
    }
    
    public long getEventMask()
    {
      return eventMask;
    }
    
    public int[] getCalls()
    {
      return calls;
    }
    
    public void orEventMasks(long paramLong)
    {
      eventMask |= paramLong;
      for (int i = 0; (i < 64) && (paramLong != 0L); i++)
      {
        if ((paramLong & 1L) != 0L) {
          calls[i] += 1;
        }
        paramLong >>>= 1;
      }
    }
    
    SelectiveAWTEventListener(AWTEventListener paramAWTEventListener, long paramLong)
    {
      listener = paramAWTEventListener;
      eventMask = paramLong;
    }
    
    public void eventDispatched(AWTEvent paramAWTEvent)
    {
      long l1 = 0L;
      if ((((l1 = eventMask & 1L) != 0L) && (id >= 100) && (id <= 103)) || (((l1 = eventMask & 0x2) != 0L) && (id >= 300) && (id <= 301)) || (((l1 = eventMask & 0x4) != 0L) && (id >= 1004) && (id <= 1005)) || (((l1 = eventMask & 0x8) != 0L) && (id >= 400) && (id <= 402)) || (((l1 = eventMask & 0x20000) != 0L) && (id == 507)) || (((l1 = eventMask & 0x20) != 0L) && ((id == 503) || (id == 506))) || (((l1 = eventMask & 0x10) != 0L) && (id != 503) && (id != 506) && (id != 507) && (id >= 500) && (id <= 507)) || (((l1 = eventMask & 0x40) != 0L) && (id >= 200) && (id <= 209)) || (((l1 = eventMask & 0x80) != 0L) && (id >= 1001) && (id <= 1001)) || (((l1 = eventMask & 0x100) != 0L) && (id >= 601) && (id <= 601)) || (((l1 = eventMask & 0x200) != 0L) && (id >= 701) && (id <= 701)) || (((l1 = eventMask & 0x400) != 0L) && (id >= 900) && (id <= 900)) || (((l1 = eventMask & 0x800) != 0L) && (id >= 1100) && (id <= 1101)) || (((l1 = eventMask & 0x2000) != 0L) && (id >= 800) && (id <= 801)) || (((l1 = eventMask & 0x4000) != 0L) && (id >= 1200) && (id <= 1200)) || (((l1 = eventMask & 0x8000) != 0L) && (id == 1400)) || (((l1 = eventMask & 0x10000) != 0L) && ((id == 1401) || (id == 1402))) || (((l1 = eventMask & 0x40000) != 0L) && (id == 209)) || (((l1 = eventMask & 0x80000) != 0L) && ((id == 207) || (id == 208))) || (((l1 = eventMask & 0xFFFFFFFF80000000) != 0L) && ((paramAWTEvent instanceof UngrabEvent))))
      {
        int i = 0;
        long l2 = l1;
        while (l2 != 0L)
        {
          l2 >>>= 1;
          i++;
        }
        i--;
        for (int j = 0; j < calls[i]; j++) {
          listener.eventDispatched(paramAWTEvent);
        }
      }
    }
  }
  
  private static class ToolkitEventMulticaster
    extends AWTEventMulticaster
    implements AWTEventListener
  {
    ToolkitEventMulticaster(AWTEventListener paramAWTEventListener1, AWTEventListener paramAWTEventListener2)
    {
      super(paramAWTEventListener2);
    }
    
    static AWTEventListener add(AWTEventListener paramAWTEventListener1, AWTEventListener paramAWTEventListener2)
    {
      if (paramAWTEventListener1 == null) {
        return paramAWTEventListener2;
      }
      if (paramAWTEventListener2 == null) {
        return paramAWTEventListener1;
      }
      return new ToolkitEventMulticaster(paramAWTEventListener1, paramAWTEventListener2);
    }
    
    static AWTEventListener remove(AWTEventListener paramAWTEventListener1, AWTEventListener paramAWTEventListener2)
    {
      return (AWTEventListener)removeInternal(paramAWTEventListener1, paramAWTEventListener2);
    }
    
    protected EventListener remove(EventListener paramEventListener)
    {
      if (paramEventListener == a) {
        return b;
      }
      if (paramEventListener == b) {
        return a;
      }
      AWTEventListener localAWTEventListener1 = (AWTEventListener)removeInternal(a, paramEventListener);
      AWTEventListener localAWTEventListener2 = (AWTEventListener)removeInternal(b, paramEventListener);
      if ((localAWTEventListener1 == a) && (localAWTEventListener2 == b)) {
        return this;
      }
      return add(localAWTEventListener1, localAWTEventListener2);
    }
    
    public void eventDispatched(AWTEvent paramAWTEvent)
    {
      ((AWTEventListener)a).eventDispatched(paramAWTEvent);
      ((AWTEventListener)b).eventDispatched(paramAWTEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Toolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */