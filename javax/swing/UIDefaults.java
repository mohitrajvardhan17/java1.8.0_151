package javax.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.border.Border;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;
import sun.util.CoreResourceBundleControl;

public class UIDefaults
  extends Hashtable<Object, Object>
{
  private static final Object PENDING = new Object();
  private SwingPropertyChangeSupport changeSupport;
  private Vector<String> resourceBundles;
  private Locale defaultLocale = Locale.getDefault();
  private Map<Locale, Map<String, Object>> resourceCache;
  
  public UIDefaults()
  {
    this(700, 0.75F);
  }
  
  public UIDefaults(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat);
    resourceCache = new HashMap();
  }
  
  public UIDefaults(Object[] paramArrayOfObject)
  {
    super(paramArrayOfObject.length / 2);
    for (int i = 0; i < paramArrayOfObject.length; i += 2) {
      super.put(paramArrayOfObject[i], paramArrayOfObject[(i + 1)]);
    }
  }
  
  public Object get(Object paramObject)
  {
    Object localObject = getFromHashtable(paramObject);
    return localObject != null ? localObject : getFromResourceBundle(paramObject, null);
  }
  
  private Object getFromHashtable(Object paramObject)
  {
    Object localObject1 = super.get(paramObject);
    if ((localObject1 != PENDING) && (!(localObject1 instanceof ActiveValue)) && (!(localObject1 instanceof LazyValue))) {
      return localObject1;
    }
    synchronized (this)
    {
      localObject1 = super.get(paramObject);
      if (localObject1 == PENDING)
      {
        do
        {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException) {}
          localObject1 = super.get(paramObject);
        } while (localObject1 == PENDING);
        return localObject1;
      }
      if ((localObject1 instanceof LazyValue)) {
        super.put(paramObject, PENDING);
      } else if (!(localObject1 instanceof ActiveValue)) {
        return localObject1;
      }
    }
    if ((localObject1 instanceof LazyValue)) {
      try
      {
        localObject1 = ((LazyValue)localObject1).createValue(this);
      }
      finally
      {
        synchronized (this)
        {
          if (localObject1 == null) {
            super.remove(paramObject);
          } else {
            super.put(paramObject, localObject1);
          }
          notifyAll();
        }
      }
    } else {
      localObject1 = ((ActiveValue)localObject1).createValue(this);
    }
    return localObject1;
  }
  
  public Object get(Object paramObject, Locale paramLocale)
  {
    Object localObject = getFromHashtable(paramObject);
    return localObject != null ? localObject : getFromResourceBundle(paramObject, paramLocale);
  }
  
  /* Error */
  private Object getFromResourceBundle(Object paramObject, Locale paramLocale)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 310	javax/swing/UIDefaults:resourceBundles	Ljava/util/Vector;
    //   4: ifnull +20 -> 24
    //   7: aload_0
    //   8: getfield 310	javax/swing/UIDefaults:resourceBundles	Ljava/util/Vector;
    //   11: invokevirtual 340	java/util/Vector:isEmpty	()Z
    //   14: ifne +10 -> 24
    //   17: aload_1
    //   18: instanceof 171
    //   21: ifne +5 -> 26
    //   24: aconst_null
    //   25: areturn
    //   26: aload_2
    //   27: ifnonnull +17 -> 44
    //   30: aload_0
    //   31: getfield 308	javax/swing/UIDefaults:defaultLocale	Ljava/util/Locale;
    //   34: ifnonnull +5 -> 39
    //   37: aconst_null
    //   38: areturn
    //   39: aload_0
    //   40: getfield 308	javax/swing/UIDefaults:defaultLocale	Ljava/util/Locale;
    //   43: astore_2
    //   44: aload_0
    //   45: dup
    //   46: astore_3
    //   47: monitorenter
    //   48: aload_0
    //   49: aload_2
    //   50: invokespecial 352	javax/swing/UIDefaults:getResourceCache	(Ljava/util/Locale;)Ljava/util/Map;
    //   53: aload_1
    //   54: invokeinterface 370 2 0
    //   59: aload_3
    //   60: monitorexit
    //   61: areturn
    //   62: astore 4
    //   64: aload_3
    //   65: monitorexit
    //   66: aload 4
    //   68: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	69	0	this	UIDefaults
    //   0	69	1	paramObject	Object
    //   0	69	2	paramLocale	Locale
    //   46	19	3	Ljava/lang/Object;	Object
    //   62	5	4	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   48	61	62	finally
    //   62	66	62	finally
  }
  
  private Map<String, Object> getResourceCache(Locale paramLocale)
  {
    Object localObject1 = (Map)resourceCache.get(paramLocale);
    if (localObject1 == null)
    {
      localObject1 = new TextAndMnemonicHashMap(null);
      for (int i = resourceBundles.size() - 1; i >= 0; i--)
      {
        String str1 = (String)resourceBundles.get(i);
        try
        {
          CoreResourceBundleControl localCoreResourceBundleControl = CoreResourceBundleControl.getRBControlInstance(str1);
          ResourceBundle localResourceBundle;
          if (localCoreResourceBundleControl != null) {
            localResourceBundle = ResourceBundle.getBundle(str1, paramLocale, localCoreResourceBundleControl);
          } else {
            localResourceBundle = ResourceBundle.getBundle(str1, paramLocale);
          }
          Enumeration localEnumeration = localResourceBundle.getKeys();
          while (localEnumeration.hasMoreElements())
          {
            String str2 = (String)localEnumeration.nextElement();
            if (((Map)localObject1).get(str2) == null)
            {
              Object localObject2 = localResourceBundle.getObject(str2);
              ((Map)localObject1).put(str2, localObject2);
            }
          }
        }
        catch (MissingResourceException localMissingResourceException) {}
      }
      resourceCache.put(paramLocale, localObject1);
    }
    return (Map<String, Object>)localObject1;
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    Object localObject = paramObject2 == null ? super.remove(paramObject1) : super.put(paramObject1, paramObject2);
    if ((paramObject1 instanceof String)) {
      firePropertyChange((String)paramObject1, localObject, paramObject2);
    }
    return localObject;
  }
  
  public void putDefaults(Object[] paramArrayOfObject)
  {
    int i = 0;
    int j = paramArrayOfObject.length;
    while (i < j)
    {
      Object localObject = paramArrayOfObject[(i + 1)];
      if (localObject == null) {
        super.remove(paramArrayOfObject[i]);
      } else {
        super.put(paramArrayOfObject[i], localObject);
      }
      i += 2;
    }
    firePropertyChange("UIDefaults", null, null);
  }
  
  public Font getFont(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Font) ? (Font)localObject : null;
  }
  
  public Font getFont(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Font) ? (Font)localObject : null;
  }
  
  public Color getColor(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Color) ? (Color)localObject : null;
  }
  
  public Color getColor(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Color) ? (Color)localObject : null;
  }
  
  public Icon getIcon(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Icon) ? (Icon)localObject : null;
  }
  
  public Icon getIcon(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Icon) ? (Icon)localObject : null;
  }
  
  public Border getBorder(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Border) ? (Border)localObject : null;
  }
  
  public Border getBorder(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Border) ? (Border)localObject : null;
  }
  
  public String getString(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof String) ? (String)localObject : null;
  }
  
  public String getString(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof String) ? (String)localObject : null;
  }
  
  public int getInt(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Integer) ? ((Integer)localObject).intValue() : 0;
  }
  
  public int getInt(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Integer) ? ((Integer)localObject).intValue() : 0;
  }
  
  public boolean getBoolean(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Boolean) ? ((Boolean)localObject).booleanValue() : false;
  }
  
  public boolean getBoolean(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Boolean) ? ((Boolean)localObject).booleanValue() : false;
  }
  
  public Insets getInsets(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Insets) ? (Insets)localObject : null;
  }
  
  public Insets getInsets(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Insets) ? (Insets)localObject : null;
  }
  
  public Dimension getDimension(Object paramObject)
  {
    Object localObject = get(paramObject);
    return (localObject instanceof Dimension) ? (Dimension)localObject : null;
  }
  
  public Dimension getDimension(Object paramObject, Locale paramLocale)
  {
    Object localObject = get(paramObject, paramLocale);
    return (localObject instanceof Dimension) ? (Dimension)localObject : null;
  }
  
  public Class<? extends ComponentUI> getUIClass(String paramString, ClassLoader paramClassLoader)
  {
    try
    {
      String str = (String)get(paramString);
      if (str != null)
      {
        ReflectUtil.checkPackageAccess(str);
        Class localClass = (Class)get(str);
        if (localClass == null)
        {
          if (paramClassLoader == null) {
            localClass = SwingUtilities.loadSystemClass(str);
          } else {
            localClass = paramClassLoader.loadClass(str);
          }
          if (localClass != null) {
            put(str, localClass);
          }
        }
        return localClass;
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return null;
    }
    catch (ClassCastException localClassCastException)
    {
      return null;
    }
    return null;
  }
  
  public Class<? extends ComponentUI> getUIClass(String paramString)
  {
    return getUIClass(paramString, null);
  }
  
  protected void getUIError(String paramString)
  {
    System.err.println("UIDefaults.getUI() failed: " + paramString);
    try
    {
      throw new Error();
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  public ComponentUI getUI(JComponent paramJComponent)
  {
    Object localObject1 = get("ClassLoader");
    ClassLoader localClassLoader = localObject1 != null ? (ClassLoader)localObject1 : paramJComponent.getClass().getClassLoader();
    Class localClass = getUIClass(paramJComponent.getUIClassID(), localClassLoader);
    Object localObject2 = null;
    if (localClass == null) {
      getUIError("no ComponentUI class for: " + paramJComponent);
    } else {
      try
      {
        Method localMethod = (Method)get(localClass);
        if (localMethod == null)
        {
          localMethod = localClass.getMethod("createUI", new Class[] { JComponent.class });
          put(localClass, localMethod);
        }
        localObject2 = MethodUtil.invoke(localMethod, null, new Object[] { paramJComponent });
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        getUIError("static createUI() method not found in " + localClass);
      }
      catch (Exception localException)
      {
        getUIError("createUI() failed for " + paramJComponent + " " + localException);
      }
    }
    return (ComponentUI)localObject2;
  }
  
  public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport == null) {
      changeSupport = new SwingPropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport != null) {
      changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
    }
  }
  
  public synchronized PropertyChangeListener[] getPropertyChangeListeners()
  {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners();
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if (changeSupport != null) {
      changeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
    }
  }
  
  public synchronized void addResourceBundle(String paramString)
  {
    if (paramString == null) {
      return;
    }
    if (resourceBundles == null) {
      resourceBundles = new Vector(5);
    }
    if (!resourceBundles.contains(paramString))
    {
      resourceBundles.add(paramString);
      resourceCache.clear();
    }
  }
  
  public synchronized void removeResourceBundle(String paramString)
  {
    if (resourceBundles != null) {
      resourceBundles.remove(paramString);
    }
    resourceCache.clear();
  }
  
  public void setDefaultLocale(Locale paramLocale)
  {
    defaultLocale = paramLocale;
  }
  
  public Locale getDefaultLocale()
  {
    return defaultLocale;
  }
  
  public static abstract interface ActiveValue
  {
    public abstract Object createValue(UIDefaults paramUIDefaults);
  }
  
  public static class LazyInputMap
    implements UIDefaults.LazyValue
  {
    private Object[] bindings;
    
    public LazyInputMap(Object[] paramArrayOfObject)
    {
      bindings = paramArrayOfObject;
    }
    
    public Object createValue(UIDefaults paramUIDefaults)
    {
      if (bindings != null)
      {
        InputMap localInputMap = LookAndFeel.makeInputMap(bindings);
        return localInputMap;
      }
      return null;
    }
  }
  
  public static abstract interface LazyValue
  {
    public abstract Object createValue(UIDefaults paramUIDefaults);
  }
  
  public static class ProxyLazyValue
    implements UIDefaults.LazyValue
  {
    private AccessControlContext acc = AccessController.getContext();
    private String className;
    private String methodName;
    private Object[] args;
    
    public ProxyLazyValue(String paramString)
    {
      this(paramString, (String)null);
    }
    
    public ProxyLazyValue(String paramString1, String paramString2)
    {
      this(paramString1, paramString2, null);
    }
    
    public ProxyLazyValue(String paramString, Object[] paramArrayOfObject)
    {
      this(paramString, null, paramArrayOfObject);
    }
    
    public ProxyLazyValue(String paramString1, String paramString2, Object[] paramArrayOfObject)
    {
      className = paramString1;
      methodName = paramString2;
      if (paramArrayOfObject != null) {
        args = ((Object[])paramArrayOfObject.clone());
      }
    }
    
    public Object createValue(final UIDefaults paramUIDefaults)
    {
      if ((acc == null) && (System.getSecurityManager() != null)) {
        throw new SecurityException("null AccessControlContext");
      }
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            Object localObject1;
            if ((paramUIDefaults == null) || (!((localObject1 = paramUIDefaults.get("ClassLoader")) instanceof ClassLoader)))
            {
              localObject1 = Thread.currentThread().getContextClassLoader();
              if (localObject1 == null) {
                localObject1 = ClassLoader.getSystemClassLoader();
              }
            }
            ReflectUtil.checkPackageAccess(className);
            Class localClass = Class.forName(className, true, (ClassLoader)localObject1);
            SwingUtilities2.checkAccess(localClass.getModifiers());
            if (methodName != null)
            {
              arrayOfClass = UIDefaults.ProxyLazyValue.this.getClassArray(args);
              localObject2 = localClass.getMethod(methodName, arrayOfClass);
              return MethodUtil.invoke((Method)localObject2, localClass, args);
            }
            Class[] arrayOfClass = UIDefaults.ProxyLazyValue.this.getClassArray(args);
            Object localObject2 = localClass.getConstructor(arrayOfClass);
            SwingUtilities2.checkAccess(((Constructor)localObject2).getModifiers());
            return ((Constructor)localObject2).newInstance(args);
          }
          catch (Exception localException) {}
          return null;
        }
      }, acc);
    }
    
    private Class[] getClassArray(Object[] paramArrayOfObject)
    {
      Class[] arrayOfClass = null;
      if (paramArrayOfObject != null)
      {
        arrayOfClass = new Class[paramArrayOfObject.length];
        for (int i = 0; i < paramArrayOfObject.length; i++) {
          if ((paramArrayOfObject[i] instanceof Integer)) {
            arrayOfClass[i] = Integer.TYPE;
          } else if ((paramArrayOfObject[i] instanceof Boolean)) {
            arrayOfClass[i] = Boolean.TYPE;
          } else if ((paramArrayOfObject[i] instanceof ColorUIResource)) {
            arrayOfClass[i] = Color.class;
          } else {
            arrayOfClass[i] = paramArrayOfObject[i].getClass();
          }
        }
      }
      return arrayOfClass;
    }
    
    private String printArgs(Object[] paramArrayOfObject)
    {
      String str = "{";
      if (paramArrayOfObject != null)
      {
        for (int i = 0; i < paramArrayOfObject.length - 1; i++) {
          str = str.concat(paramArrayOfObject[i] + ",");
        }
        str = str.concat(paramArrayOfObject[(paramArrayOfObject.length - 1)] + "}");
      }
      else
      {
        str = str.concat("}");
      }
      return str;
    }
  }
  
  private static class TextAndMnemonicHashMap
    extends HashMap<String, Object>
  {
    static final String AND_MNEMONIC = "AndMnemonic";
    static final String TITLE_SUFFIX = ".titleAndMnemonic";
    static final String TEXT_SUFFIX = ".textAndMnemonic";
    
    private TextAndMnemonicHashMap() {}
    
    public Object get(Object paramObject)
    {
      Object localObject = super.get(paramObject);
      if (localObject == null)
      {
        int i = 0;
        String str1 = paramObject.toString();
        String str2 = null;
        if (str1.endsWith("AndMnemonic")) {
          return null;
        }
        if (str1.endsWith(".mnemonic"))
        {
          str2 = composeKey(str1, 9, ".textAndMnemonic");
        }
        else if (str1.endsWith("NameMnemonic"))
        {
          str2 = composeKey(str1, 12, ".textAndMnemonic");
        }
        else if (str1.endsWith("Mnemonic"))
        {
          str2 = composeKey(str1, 8, ".textAndMnemonic");
          i = 1;
        }
        if (str2 != null)
        {
          localObject = super.get(str2);
          if ((localObject == null) && (i != 0))
          {
            str2 = composeKey(str1, 8, ".titleAndMnemonic");
            localObject = super.get(str2);
          }
          return localObject == null ? null : getMnemonicFromProperty(localObject.toString());
        }
        if (str1.endsWith("NameText")) {
          str2 = composeKey(str1, 8, ".textAndMnemonic");
        } else if (str1.endsWith(".nameText")) {
          str2 = composeKey(str1, 9, ".textAndMnemonic");
        } else if (str1.endsWith("Text")) {
          str2 = composeKey(str1, 4, ".textAndMnemonic");
        } else if (str1.endsWith("Title")) {
          str2 = composeKey(str1, 5, ".titleAndMnemonic");
        }
        if (str2 != null)
        {
          localObject = super.get(str2);
          return localObject == null ? null : getTextFromProperty(localObject.toString());
        }
        if (str1.endsWith("DisplayedMnemonicIndex"))
        {
          str2 = composeKey(str1, 22, ".textAndMnemonic");
          localObject = super.get(str2);
          if (localObject == null)
          {
            str2 = composeKey(str1, 22, ".titleAndMnemonic");
            localObject = super.get(str2);
          }
          return localObject == null ? null : getIndexFromProperty(localObject.toString());
        }
      }
      return localObject;
    }
    
    String composeKey(String paramString1, int paramInt, String paramString2)
    {
      return paramString1.substring(0, paramString1.length() - paramInt) + paramString2;
    }
    
    String getTextFromProperty(String paramString)
    {
      return paramString.replace("&", "");
    }
    
    String getMnemonicFromProperty(String paramString)
    {
      int i = paramString.indexOf('&');
      if ((0 <= i) && (i < paramString.length() - 1))
      {
        char c = paramString.charAt(i + 1);
        return Integer.toString(Character.toUpperCase(c));
      }
      return null;
    }
    
    String getIndexFromProperty(String paramString)
    {
      int i = paramString.indexOf('&');
      return i == -1 ? null : Integer.toString(i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\UIDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */