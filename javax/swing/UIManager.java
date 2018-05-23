package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.border.Border;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AppContext;
import sun.awt.OSInfo;
import sun.awt.OSInfo.OSType;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class UIManager
  implements Serializable
{
  private static final Object classLock = new Object();
  private static final String defaultLAFKey = "swing.defaultlaf";
  private static final String auxiliaryLAFsKey = "swing.auxiliarylaf";
  private static final String multiplexingLAFKey = "swing.plaf.multiplexinglaf";
  private static final String installedLAFsKey = "swing.installedlafs";
  private static final String disableMnemonicKey = "swing.disablenavaids";
  private static LookAndFeelInfo[] installedLAFs;
  
  public UIManager() {}
  
  private static LAFState getLAFState()
  {
    LAFState localLAFState = (LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
    if (localLAFState == null) {
      synchronized (classLock)
      {
        localLAFState = (LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
        if (localLAFState == null) {
          SwingUtilities.appContextPut(SwingUtilities2.LAF_STATE_KEY, localLAFState = new LAFState(null));
        }
      }
    }
    return localLAFState;
  }
  
  private static String makeInstalledLAFKey(String paramString1, String paramString2)
  {
    return "swing.installedlaf." + paramString1 + "." + paramString2;
  }
  
  private static String makeSwingPropertiesFilename()
  {
    String str1 = File.separator;
    String str2 = System.getProperty("java.home");
    if (str2 == null) {
      str2 = "<java.home undefined>";
    }
    return str2 + str1 + "lib" + str1 + "swing.properties";
  }
  
  public static LookAndFeelInfo[] getInstalledLookAndFeels()
  {
    maybeInitialize();
    LookAndFeelInfo[] arrayOfLookAndFeelInfo1 = getLAFStateinstalledLAFs;
    if (arrayOfLookAndFeelInfo1 == null) {
      arrayOfLookAndFeelInfo1 = installedLAFs;
    }
    LookAndFeelInfo[] arrayOfLookAndFeelInfo2 = new LookAndFeelInfo[arrayOfLookAndFeelInfo1.length];
    System.arraycopy(arrayOfLookAndFeelInfo1, 0, arrayOfLookAndFeelInfo2, 0, arrayOfLookAndFeelInfo1.length);
    return arrayOfLookAndFeelInfo2;
  }
  
  public static void setInstalledLookAndFeels(LookAndFeelInfo[] paramArrayOfLookAndFeelInfo)
    throws SecurityException
  {
    maybeInitialize();
    LookAndFeelInfo[] arrayOfLookAndFeelInfo = new LookAndFeelInfo[paramArrayOfLookAndFeelInfo.length];
    System.arraycopy(paramArrayOfLookAndFeelInfo, 0, arrayOfLookAndFeelInfo, 0, paramArrayOfLookAndFeelInfo.length);
    getLAFStateinstalledLAFs = arrayOfLookAndFeelInfo;
  }
  
  public static void installLookAndFeel(LookAndFeelInfo paramLookAndFeelInfo)
  {
    LookAndFeelInfo[] arrayOfLookAndFeelInfo1 = getInstalledLookAndFeels();
    LookAndFeelInfo[] arrayOfLookAndFeelInfo2 = new LookAndFeelInfo[arrayOfLookAndFeelInfo1.length + 1];
    System.arraycopy(arrayOfLookAndFeelInfo1, 0, arrayOfLookAndFeelInfo2, 0, arrayOfLookAndFeelInfo1.length);
    arrayOfLookAndFeelInfo2[arrayOfLookAndFeelInfo1.length] = paramLookAndFeelInfo;
    setInstalledLookAndFeels(arrayOfLookAndFeelInfo2);
  }
  
  public static void installLookAndFeel(String paramString1, String paramString2)
  {
    installLookAndFeel(new LookAndFeelInfo(paramString1, paramString2));
  }
  
  public static LookAndFeel getLookAndFeel()
  {
    maybeInitialize();
    return getLAFStatelookAndFeel;
  }
  
  public static void setLookAndFeel(LookAndFeel paramLookAndFeel)
    throws UnsupportedLookAndFeelException
  {
    if ((paramLookAndFeel != null) && (!paramLookAndFeel.isSupportedLookAndFeel()))
    {
      localObject = paramLookAndFeel.toString() + " not supported on this platform";
      throw new UnsupportedLookAndFeelException((String)localObject);
    }
    Object localObject = getLAFState();
    LookAndFeel localLookAndFeel = lookAndFeel;
    if (localLookAndFeel != null) {
      localLookAndFeel.uninitialize();
    }
    lookAndFeel = paramLookAndFeel;
    if (paramLookAndFeel != null)
    {
      DefaultLookup.setDefaultLookup(null);
      paramLookAndFeel.initialize();
      ((LAFState)localObject).setLookAndFeelDefaults(paramLookAndFeel.getDefaults());
    }
    else
    {
      ((LAFState)localObject).setLookAndFeelDefaults(null);
    }
    SwingPropertyChangeSupport localSwingPropertyChangeSupport = ((LAFState)localObject).getPropertyChangeSupport(false);
    if (localSwingPropertyChangeSupport != null) {
      localSwingPropertyChangeSupport.firePropertyChange("lookAndFeel", localLookAndFeel, paramLookAndFeel);
    }
  }
  
  public static void setLookAndFeel(String paramString)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
  {
    if ("javax.swing.plaf.metal.MetalLookAndFeel".equals(paramString))
    {
      setLookAndFeel(new MetalLookAndFeel());
    }
    else
    {
      Class localClass = SwingUtilities.loadSystemClass(paramString);
      setLookAndFeel((LookAndFeel)localClass.newInstance());
    }
  }
  
  public static String getSystemLookAndFeelClassName()
  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("swing.systemlaf"));
    if (str1 != null) {
      return str1;
    }
    OSInfo.OSType localOSType = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
    if (localOSType == OSInfo.OSType.WINDOWS) {
      return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    }
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.desktop"));
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if (("gnome".equals(str2)) && ((localToolkit instanceof SunToolkit)) && (((SunToolkit)localToolkit).isNativeGTKAvailable())) {
      return "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    }
    if ((localOSType == OSInfo.OSType.MACOSX) && (localToolkit.getClass().getName().equals("sun.lwawt.macosx.LWCToolkit"))) {
      return "com.apple.laf.AquaLookAndFeel";
    }
    if (localOSType == OSInfo.OSType.SOLARIS) {
      return "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    }
    return getCrossPlatformLookAndFeelClassName();
  }
  
  public static String getCrossPlatformLookAndFeelClassName()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.crossplatformlaf"));
    if (str != null) {
      return str;
    }
    return "javax.swing.plaf.metal.MetalLookAndFeel";
  }
  
  public static UIDefaults getDefaults()
  {
    maybeInitialize();
    return getLAFStatemultiUIDefaults;
  }
  
  public static Font getFont(Object paramObject)
  {
    return getDefaults().getFont(paramObject);
  }
  
  public static Font getFont(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getFont(paramObject, paramLocale);
  }
  
  public static Color getColor(Object paramObject)
  {
    return getDefaults().getColor(paramObject);
  }
  
  public static Color getColor(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getColor(paramObject, paramLocale);
  }
  
  public static Icon getIcon(Object paramObject)
  {
    return getDefaults().getIcon(paramObject);
  }
  
  public static Icon getIcon(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getIcon(paramObject, paramLocale);
  }
  
  public static Border getBorder(Object paramObject)
  {
    return getDefaults().getBorder(paramObject);
  }
  
  public static Border getBorder(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getBorder(paramObject, paramLocale);
  }
  
  public static String getString(Object paramObject)
  {
    return getDefaults().getString(paramObject);
  }
  
  public static String getString(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getString(paramObject, paramLocale);
  }
  
  static String getString(Object paramObject, Component paramComponent)
  {
    Locale localLocale = paramComponent == null ? Locale.getDefault() : paramComponent.getLocale();
    return getString(paramObject, localLocale);
  }
  
  public static int getInt(Object paramObject)
  {
    return getDefaults().getInt(paramObject);
  }
  
  public static int getInt(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getInt(paramObject, paramLocale);
  }
  
  public static boolean getBoolean(Object paramObject)
  {
    return getDefaults().getBoolean(paramObject);
  }
  
  public static boolean getBoolean(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getBoolean(paramObject, paramLocale);
  }
  
  public static Insets getInsets(Object paramObject)
  {
    return getDefaults().getInsets(paramObject);
  }
  
  public static Insets getInsets(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getInsets(paramObject, paramLocale);
  }
  
  public static Dimension getDimension(Object paramObject)
  {
    return getDefaults().getDimension(paramObject);
  }
  
  public static Dimension getDimension(Object paramObject, Locale paramLocale)
  {
    return getDefaults().getDimension(paramObject, paramLocale);
  }
  
  public static Object get(Object paramObject)
  {
    return getDefaults().get(paramObject);
  }
  
  public static Object get(Object paramObject, Locale paramLocale)
  {
    return getDefaults().get(paramObject, paramLocale);
  }
  
  public static Object put(Object paramObject1, Object paramObject2)
  {
    return getDefaults().put(paramObject1, paramObject2);
  }
  
  public static ComponentUI getUI(JComponent paramJComponent)
  {
    maybeInitialize();
    maybeInitializeFocusPolicy(paramJComponent);
    ComponentUI localComponentUI = null;
    LookAndFeel localLookAndFeel = getLAFStatemultiLookAndFeel;
    if (localLookAndFeel != null) {
      localComponentUI = localLookAndFeel.getDefaults().getUI(paramJComponent);
    }
    if (localComponentUI == null) {
      localComponentUI = getDefaults().getUI(paramJComponent);
    }
    return localComponentUI;
  }
  
  public static UIDefaults getLookAndFeelDefaults()
  {
    maybeInitialize();
    return getLAFState().getLookAndFeelDefaults();
  }
  
  private static LookAndFeel getMultiLookAndFeel()
  {
    LookAndFeel localLookAndFeel = getLAFStatemultiLookAndFeel;
    if (localLookAndFeel == null)
    {
      String str1 = "javax.swing.plaf.multi.MultiLookAndFeel";
      String str2 = getLAFStateswingProps.getProperty("swing.plaf.multiplexinglaf", str1);
      try
      {
        Class localClass = SwingUtilities.loadSystemClass(str2);
        localLookAndFeel = (LookAndFeel)localClass.newInstance();
      }
      catch (Exception localException)
      {
        System.err.println("UIManager: failed loading " + str2);
      }
    }
    return localLookAndFeel;
  }
  
  public static void addAuxiliaryLookAndFeel(LookAndFeel paramLookAndFeel)
  {
    
    if (!paramLookAndFeel.isSupportedLookAndFeel()) {
      return;
    }
    Vector localVector = getLAFStateauxLookAndFeels;
    if (localVector == null) {
      localVector = new Vector();
    }
    if (!localVector.contains(paramLookAndFeel))
    {
      localVector.addElement(paramLookAndFeel);
      paramLookAndFeel.initialize();
      getLAFStateauxLookAndFeels = localVector;
      if (getLAFStatemultiLookAndFeel == null) {
        getLAFStatemultiLookAndFeel = getMultiLookAndFeel();
      }
    }
  }
  
  public static boolean removeAuxiliaryLookAndFeel(LookAndFeel paramLookAndFeel)
  {
    maybeInitialize();
    Vector localVector = getLAFStateauxLookAndFeels;
    if ((localVector == null) || (localVector.size() == 0)) {
      return false;
    }
    boolean bool = localVector.removeElement(paramLookAndFeel);
    if (bool) {
      if (localVector.size() == 0)
      {
        getLAFStateauxLookAndFeels = null;
        getLAFStatemultiLookAndFeel = null;
      }
      else
      {
        getLAFStateauxLookAndFeels = localVector;
      }
    }
    paramLookAndFeel.uninitialize();
    return bool;
  }
  
  public static LookAndFeel[] getAuxiliaryLookAndFeels()
  {
    maybeInitialize();
    Vector localVector = getLAFStateauxLookAndFeels;
    if ((localVector == null) || (localVector.size() == 0)) {
      return null;
    }
    LookAndFeel[] arrayOfLookAndFeel = new LookAndFeel[localVector.size()];
    for (int i = 0; i < arrayOfLookAndFeel.length; i++) {
      arrayOfLookAndFeel[i] = ((LookAndFeel)localVector.elementAt(i));
    }
    return arrayOfLookAndFeel;
  }
  
  public static void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (classLock)
    {
      getLAFState().getPropertyChangeSupport(true).addPropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  public static void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    synchronized (classLock)
    {
      getLAFState().getPropertyChangeSupport(true).removePropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  /* Error */
  public static PropertyChangeListener[] getPropertyChangeListeners()
  {
    // Byte code:
    //   0: getstatic 529	javax/swing/UIManager:classLock	Ljava/lang/Object;
    //   3: dup
    //   4: astore_0
    //   5: monitorenter
    //   6: invokestatic 634	javax/swing/UIManager:getLAFState	()Ljavax/swing/UIManager$LAFState;
    //   9: iconst_1
    //   10: invokevirtual 647	javax/swing/UIManager$LAFState:getPropertyChangeSupport	(Z)Ljavax/swing/event/SwingPropertyChangeSupport;
    //   13: invokevirtual 650	javax/swing/event/SwingPropertyChangeSupport:getPropertyChangeListeners	()[Ljava/beans/PropertyChangeListener;
    //   16: aload_0
    //   17: monitorexit
    //   18: areturn
    //   19: astore_1
    //   20: aload_0
    //   21: monitorexit
    //   22: aload_1
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   4	17	0	Ljava/lang/Object;	Object
    //   19	4	1	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	18	19	finally
    //   19	22	19	finally
  }
  
  private static Properties loadSwingProperties()
  {
    if (UIManager.class.getClassLoader() != null) {
      return new Properties();
    }
    Properties localProperties = new Properties();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        OSInfo.OSType localOSType = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
        if (localOSType == OSInfo.OSType.MACOSX) {
          val$props.put("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName());
        }
        try
        {
          File localFile = new File(UIManager.access$100());
          if (localFile.exists())
          {
            FileInputStream localFileInputStream = new FileInputStream(localFile);
            val$props.load(localFileInputStream);
            localFileInputStream.close();
          }
        }
        catch (Exception localException) {}
        UIManager.checkProperty(val$props, "swing.defaultlaf");
        UIManager.checkProperty(val$props, "swing.auxiliarylaf");
        UIManager.checkProperty(val$props, "swing.plaf.multiplexinglaf");
        UIManager.checkProperty(val$props, "swing.installedlafs");
        UIManager.checkProperty(val$props, "swing.disablenavaids");
        return null;
      }
    });
    return localProperties;
  }
  
  private static void checkProperty(Properties paramProperties, String paramString)
  {
    String str = System.getProperty(paramString);
    if (str != null) {
      paramProperties.put(paramString, str);
    }
  }
  
  private static void initializeInstalledLAFs(Properties paramProperties)
  {
    String str1 = paramProperties.getProperty("swing.installedlafs");
    if (str1 == null) {
      return;
    }
    Vector localVector1 = new Vector();
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, ",", false);
    while (localStringTokenizer.hasMoreTokens()) {
      localVector1.addElement(localStringTokenizer.nextToken());
    }
    Vector localVector2 = new Vector(localVector1.size());
    Object localObject = localVector1.iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str2 = (String)((Iterator)localObject).next();
      String str3 = paramProperties.getProperty(makeInstalledLAFKey(str2, "name"), str2);
      String str4 = paramProperties.getProperty(makeInstalledLAFKey(str2, "class"));
      if (str4 != null) {
        localVector2.addElement(new LookAndFeelInfo(str3, str4));
      }
    }
    localObject = new LookAndFeelInfo[localVector2.size()];
    for (int i = 0; i < localVector2.size(); i++) {
      localObject[i] = ((LookAndFeelInfo)localVector2.elementAt(i));
    }
    getLAFStateinstalledLAFs = ((LookAndFeelInfo[])localObject);
  }
  
  private static void initializeDefaultLAF(Properties paramProperties)
  {
    if (getLAFStatelookAndFeel != null) {
      return;
    }
    String str = null;
    HashMap localHashMap = (HashMap)AppContext.getAppContext().remove("swing.lafdata");
    if (localHashMap != null) {
      str = (String)localHashMap.remove("defaultlaf");
    }
    if (str == null) {
      str = getCrossPlatformLookAndFeelClassName();
    }
    str = paramProperties.getProperty("swing.defaultlaf", str);
    try
    {
      setLookAndFeel(str);
    }
    catch (Exception localException)
    {
      throw new Error("Cannot load " + str);
    }
    if (localHashMap != null)
    {
      Iterator localIterator = localHashMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        put(localObject, localHashMap.get(localObject));
      }
    }
  }
  
  private static void initializeAuxiliaryLAFs(Properties paramProperties)
  {
    String str1 = paramProperties.getProperty("swing.auxiliarylaf");
    if (str1 == null) {
      return;
    }
    Vector localVector = new Vector();
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, ",");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str2 = localStringTokenizer.nextToken();
      try
      {
        Class localClass = SwingUtilities.loadSystemClass(str2);
        LookAndFeel localLookAndFeel = (LookAndFeel)localClass.newInstance();
        localLookAndFeel.initialize();
        localVector.addElement(localLookAndFeel);
      }
      catch (Exception localException)
      {
        System.err.println("UIManager: failed loading auxiliary look and feel " + str2);
      }
    }
    if (localVector.size() == 0)
    {
      localVector = null;
    }
    else
    {
      getLAFStatemultiLookAndFeel = getMultiLookAndFeel();
      if (getLAFStatemultiLookAndFeel == null) {
        localVector = null;
      }
    }
    getLAFStateauxLookAndFeels = localVector;
  }
  
  private static void initializeSystemDefaults(Properties paramProperties)
  {
    getLAFStateswingProps = paramProperties;
  }
  
  private static void maybeInitialize()
  {
    synchronized (classLock)
    {
      if (!getLAFStateinitialized)
      {
        getLAFStateinitialized = true;
        initialize();
      }
    }
  }
  
  private static void maybeInitializeFocusPolicy(JComponent paramJComponent)
  {
    if ((paramJComponent instanceof JRootPane)) {
      synchronized (classLock)
      {
        if (!getLAFStatefocusPolicyInitialized)
        {
          getLAFStatefocusPolicyInitialized = true;
          if (FocusManager.isFocusManagerEnabled()) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
          }
        }
      }
    }
  }
  
  private static void initialize()
  {
    Properties localProperties = loadSwingProperties();
    initializeSystemDefaults(localProperties);
    initializeDefaultLAF(localProperties);
    initializeAuxiliaryLAFs(localProperties);
    initializeInstalledLAFs(localProperties);
    if (RepaintManager.HANDLE_TOP_LEVEL_PAINT) {
      PaintEventDispatcher.setPaintEventDispatcher(new SwingPaintEventDispatcher());
    }
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor()
    {
      public boolean postProcessKeyEvent(KeyEvent paramAnonymousKeyEvent)
      {
        Component localComponent = paramAnonymousKeyEvent.getComponent();
        if (((!(localComponent instanceof JComponent)) || ((localComponent != null) && (!localComponent.isEnabled()))) && (JComponent.KeyboardState.shouldProcess(paramAnonymousKeyEvent)) && (SwingUtilities.processKeyBindings(paramAnonymousKeyEvent)))
        {
          paramAnonymousKeyEvent.consume();
          return true;
        }
        return false;
      }
    });
    AWTAccessor.getComponentAccessor().setRequestFocusController(JComponent.focusController);
  }
  
  static
  {
    ArrayList localArrayList = new ArrayList(4);
    localArrayList.add(new LookAndFeelInfo("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"));
    localArrayList.add(new LookAndFeelInfo("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel"));
    localArrayList.add(new LookAndFeelInfo("CDE/Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"));
    OSInfo.OSType localOSType = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
    if (localOSType == OSInfo.OSType.WINDOWS)
    {
      localArrayList.add(new LookAndFeelInfo("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
      if (Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive") != null) {
        localArrayList.add(new LookAndFeelInfo("Windows Classic", "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"));
      }
    }
    else if (localOSType == OSInfo.OSType.MACOSX)
    {
      localArrayList.add(new LookAndFeelInfo("Mac OS X", "com.apple.laf.AquaLookAndFeel"));
    }
    else
    {
      localArrayList.add(new LookAndFeelInfo("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"));
    }
    installedLAFs = (LookAndFeelInfo[])localArrayList.toArray(new LookAndFeelInfo[localArrayList.size()]);
  }
  
  private static class LAFState
  {
    Properties swingProps;
    private UIDefaults[] tables = new UIDefaults[2];
    boolean initialized = false;
    boolean focusPolicyInitialized = false;
    MultiUIDefaults multiUIDefaults = new MultiUIDefaults(tables);
    LookAndFeel lookAndFeel;
    LookAndFeel multiLookAndFeel = null;
    Vector<LookAndFeel> auxLookAndFeels = null;
    SwingPropertyChangeSupport changeSupport;
    UIManager.LookAndFeelInfo[] installedLAFs;
    
    private LAFState() {}
    
    UIDefaults getLookAndFeelDefaults()
    {
      return tables[0];
    }
    
    void setLookAndFeelDefaults(UIDefaults paramUIDefaults)
    {
      tables[0] = paramUIDefaults;
    }
    
    UIDefaults getSystemDefaults()
    {
      return tables[1];
    }
    
    void setSystemDefaults(UIDefaults paramUIDefaults)
    {
      tables[1] = paramUIDefaults;
    }
    
    public synchronized SwingPropertyChangeSupport getPropertyChangeSupport(boolean paramBoolean)
    {
      if ((paramBoolean) && (changeSupport == null)) {
        changeSupport = new SwingPropertyChangeSupport(UIManager.class);
      }
      return changeSupport;
    }
  }
  
  public static class LookAndFeelInfo
  {
    private String name;
    private String className;
    
    public LookAndFeelInfo(String paramString1, String paramString2)
    {
      name = paramString1;
      className = paramString2;
    }
    
    public String getName()
    {
      return name;
    }
    
    public String getClassName()
    {
      return className;
    }
    
    public String toString()
    {
      return getClass().getName() + "[" + getName() + " " + getClassName() + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\UIManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */