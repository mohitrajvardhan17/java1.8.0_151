package sun.awt.im;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.im.spi.InputMethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import sun.awt.AppContext;
import sun.awt.InputMethodSupport;
import sun.awt.SunToolkit;

class ExecutableInputMethodManager
  extends InputMethodManager
  implements Runnable
{
  private InputContext currentInputContext;
  private String triggerMenuString;
  private InputMethodPopupMenu selectionMenu;
  private static String selectInputMethodMenuTitle;
  private InputMethodLocator hostAdapterLocator;
  private int javaInputMethodCount;
  private Vector<InputMethodLocator> javaInputMethodLocatorList;
  private Component requestComponent;
  private InputContext requestInputContext;
  private static final String preferredIMNode = "/sun/awt/im/preferredInputMethod";
  private static final String descriptorKey = "descriptor";
  private Hashtable<String, InputMethodLocator> preferredLocatorCache = new Hashtable();
  private Preferences userRoot;
  
  ExecutableInputMethodManager()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    try
    {
      if ((localToolkit instanceof InputMethodSupport))
      {
        InputMethodDescriptor localInputMethodDescriptor = ((InputMethodSupport)localToolkit).getInputMethodAdapterDescriptor();
        if (localInputMethodDescriptor != null) {
          hostAdapterLocator = new InputMethodLocator(localInputMethodDescriptor, null, null);
        }
      }
    }
    catch (AWTException localAWTException) {}
    javaInputMethodLocatorList = new Vector();
    initializeInputMethodLocatorList();
  }
  
  synchronized void initialize()
  {
    selectInputMethodMenuTitle = Toolkit.getProperty("AWT.InputMethodSelectionMenu", "Select Input Method");
    triggerMenuString = selectInputMethodMenuTitle;
  }
  
  public void run()
  {
    while (!hasMultipleInputMethods()) {
      try
      {
        synchronized (this)
        {
          wait();
        }
      }
      catch (InterruptedException localInterruptedException1) {}
    }
    for (;;)
    {
      waitForChangeRequest();
      initializeInputMethodLocatorList();
      try
      {
        if (requestComponent != null) {
          showInputMethodMenuOnRequesterEDT(requestComponent);
        } else {
          EventQueue.invokeAndWait(new Runnable()
          {
            public void run()
            {
              ExecutableInputMethodManager.this.showInputMethodMenu();
            }
          });
        }
      }
      catch (InterruptedException localInterruptedException2) {}catch (InvocationTargetException localInvocationTargetException) {}
    }
  }
  
  private void showInputMethodMenuOnRequesterEDT(Component paramComponent)
    throws InterruptedException, InvocationTargetException
  {
    if (paramComponent == null) {
      return;
    }
    Object local1AWTInvocationLock = new Object() {};
    InvocationEvent localInvocationEvent = new InvocationEvent(paramComponent, new Runnable()
    {
      public void run()
      {
        ExecutableInputMethodManager.this.showInputMethodMenu();
      }
    }, local1AWTInvocationLock, true);
    AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
    synchronized (local1AWTInvocationLock)
    {
      SunToolkit.postEvent(localAppContext, localInvocationEvent);
      while (!localInvocationEvent.isDispatched()) {
        local1AWTInvocationLock.wait();
      }
    }
    ??? = localInvocationEvent.getThrowable();
    if (??? != null) {
      throw new InvocationTargetException((Throwable)???);
    }
  }
  
  void setInputContext(InputContext paramInputContext)
  {
    if ((currentInputContext != null) && (paramInputContext != null)) {}
    currentInputContext = paramInputContext;
  }
  
  public synchronized void notifyChangeRequest(Component paramComponent)
  {
    if ((!(paramComponent instanceof Frame)) && (!(paramComponent instanceof Dialog))) {
      return;
    }
    if (requestComponent != null) {
      return;
    }
    requestComponent = paramComponent;
    notify();
  }
  
  public synchronized void notifyChangeRequestByHotKey(Component paramComponent)
  {
    while ((!(paramComponent instanceof Frame)) && (!(paramComponent instanceof Dialog)))
    {
      if (paramComponent == null) {
        return;
      }
      paramComponent = paramComponent.getParent();
    }
    notifyChangeRequest(paramComponent);
  }
  
  public String getTriggerMenuString()
  {
    return triggerMenuString;
  }
  
  boolean hasMultipleInputMethods()
  {
    return ((hostAdapterLocator != null) && (javaInputMethodCount > 0)) || (javaInputMethodCount > 1);
  }
  
  private synchronized void waitForChangeRequest()
  {
    try
    {
      while (requestComponent == null) {
        wait();
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  private void initializeInputMethodLocatorList()
  {
    synchronized (javaInputMethodLocatorList)
    {
      javaInputMethodLocatorList.clear();
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
          {
            Iterator localIterator = ServiceLoader.loadInstalled(InputMethodDescriptor.class).iterator();
            while (localIterator.hasNext())
            {
              InputMethodDescriptor localInputMethodDescriptor = (InputMethodDescriptor)localIterator.next();
              ClassLoader localClassLoader = localInputMethodDescriptor.getClass().getClassLoader();
              javaInputMethodLocatorList.add(new InputMethodLocator(localInputMethodDescriptor, localClassLoader, null));
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        localPrivilegedActionException.printStackTrace();
      }
      javaInputMethodCount = javaInputMethodLocatorList.size();
    }
    if (hasMultipleInputMethods())
    {
      if (userRoot == null) {
        userRoot = getUserRoot();
      }
    }
    else {
      triggerMenuString = null;
    }
  }
  
  private void showInputMethodMenu()
  {
    if (!hasMultipleInputMethods())
    {
      requestComponent = null;
      return;
    }
    selectionMenu = InputMethodPopupMenu.getInstance(requestComponent, selectInputMethodMenuTitle);
    selectionMenu.removeAll();
    String str = getCurrentSelection();
    if (hostAdapterLocator != null)
    {
      selectionMenu.addOneInputMethodToMenu(hostAdapterLocator, str);
      selectionMenu.addSeparator();
    }
    for (int i = 0; i < javaInputMethodLocatorList.size(); i++)
    {
      InputMethodLocator localInputMethodLocator = (InputMethodLocator)javaInputMethodLocatorList.get(i);
      selectionMenu.addOneInputMethodToMenu(localInputMethodLocator, str);
    }
    synchronized (this)
    {
      selectionMenu.addToComponent(requestComponent);
      requestInputContext = currentInputContext;
      selectionMenu.show(requestComponent, 60, 80);
      requestComponent = null;
    }
  }
  
  private String getCurrentSelection()
  {
    InputContext localInputContext = currentInputContext;
    if (localInputContext != null)
    {
      InputMethodLocator localInputMethodLocator = localInputContext.getInputMethodLocator();
      if (localInputMethodLocator != null) {
        return localInputMethodLocator.getActionCommandString();
      }
    }
    return null;
  }
  
  synchronized void changeInputMethod(String paramString)
  {
    Object localObject1 = null;
    String str1 = paramString;
    String str2 = null;
    int i = paramString.indexOf('\n');
    if (i != -1)
    {
      str2 = paramString.substring(i + 1);
      str1 = paramString.substring(0, i);
    }
    Object localObject2;
    String str4;
    if (hostAdapterLocator.getActionCommandString().equals(str1)) {
      localObject1 = hostAdapterLocator;
    } else {
      for (int j = 0; j < javaInputMethodLocatorList.size(); j++)
      {
        localObject2 = (InputMethodLocator)javaInputMethodLocatorList.get(j);
        str4 = ((InputMethodLocator)localObject2).getActionCommandString();
        if (str4.equals(str1))
        {
          localObject1 = localObject2;
          break;
        }
      }
    }
    if ((localObject1 != null) && (str2 != null))
    {
      String str3 = "";
      localObject2 = "";
      str4 = "";
      int k = str2.indexOf('_');
      if (k == -1)
      {
        str3 = str2;
      }
      else
      {
        str3 = str2.substring(0, k);
        int m = k + 1;
        k = str2.indexOf('_', m);
        if (k == -1)
        {
          localObject2 = str2.substring(m);
        }
        else
        {
          localObject2 = str2.substring(m, k);
          str4 = str2.substring(k + 1);
        }
      }
      Locale localLocale = new Locale(str3, (String)localObject2, str4);
      localObject1 = ((InputMethodLocator)localObject1).deriveLocator(localLocale);
    }
    if (localObject1 == null) {
      return;
    }
    if (requestInputContext != null)
    {
      requestInputContext.changeInputMethod((InputMethodLocator)localObject1);
      requestInputContext = null;
      putPreferredInputMethod((InputMethodLocator)localObject1);
    }
  }
  
  InputMethodLocator findInputMethod(Locale paramLocale)
  {
    InputMethodLocator localInputMethodLocator1 = getPreferredInputMethod(paramLocale);
    if (localInputMethodLocator1 != null) {
      return localInputMethodLocator1;
    }
    if ((hostAdapterLocator != null) && (hostAdapterLocator.isLocaleAvailable(paramLocale))) {
      return hostAdapterLocator.deriveLocator(paramLocale);
    }
    initializeInputMethodLocatorList();
    for (int i = 0; i < javaInputMethodLocatorList.size(); i++)
    {
      InputMethodLocator localInputMethodLocator2 = (InputMethodLocator)javaInputMethodLocatorList.get(i);
      if (localInputMethodLocator2.isLocaleAvailable(paramLocale)) {
        return localInputMethodLocator2.deriveLocator(paramLocale);
      }
    }
    return null;
  }
  
  Locale getDefaultKeyboardLocale()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof InputMethodSupport)) {
      return ((InputMethodSupport)localToolkit).getDefaultKeyboardLocale();
    }
    return Locale.getDefault();
  }
  
  private synchronized InputMethodLocator getPreferredInputMethod(Locale paramLocale)
  {
    InputMethodLocator localInputMethodLocator1 = null;
    if (!hasMultipleInputMethods()) {
      return null;
    }
    localInputMethodLocator1 = (InputMethodLocator)preferredLocatorCache.get(paramLocale.toString().intern());
    if (localInputMethodLocator1 != null) {
      return localInputMethodLocator1;
    }
    String str1 = findPreferredInputMethodNode(paramLocale);
    String str2 = readPreferredInputMethod(str1);
    if (str2 != null)
    {
      Locale localLocale;
      if ((hostAdapterLocator != null) && (hostAdapterLocator.getDescriptor().getClass().getName().equals(str2)))
      {
        localLocale = getAdvertisedLocale(hostAdapterLocator, paramLocale);
        if (localLocale != null)
        {
          localInputMethodLocator1 = hostAdapterLocator.deriveLocator(localLocale);
          preferredLocatorCache.put(paramLocale.toString().intern(), localInputMethodLocator1);
        }
        return localInputMethodLocator1;
      }
      for (int i = 0; i < javaInputMethodLocatorList.size(); i++)
      {
        InputMethodLocator localInputMethodLocator2 = (InputMethodLocator)javaInputMethodLocatorList.get(i);
        InputMethodDescriptor localInputMethodDescriptor = localInputMethodLocator2.getDescriptor();
        if (localInputMethodDescriptor.getClass().getName().equals(str2))
        {
          localLocale = getAdvertisedLocale(localInputMethodLocator2, paramLocale);
          if (localLocale != null)
          {
            localInputMethodLocator1 = localInputMethodLocator2.deriveLocator(localLocale);
            preferredLocatorCache.put(paramLocale.toString().intern(), localInputMethodLocator1);
          }
          return localInputMethodLocator1;
        }
      }
      writePreferredInputMethod(str1, null);
    }
    return null;
  }
  
  private String findPreferredInputMethodNode(Locale paramLocale)
  {
    if (userRoot == null) {
      return null;
    }
    for (String str = "/sun/awt/im/preferredInputMethod/" + createLocalePath(paramLocale); !str.equals("/sun/awt/im/preferredInputMethod"); str = str.substring(0, str.lastIndexOf('/'))) {
      try
      {
        if ((userRoot.nodeExists(str)) && (readPreferredInputMethod(str) != null)) {
          return str;
        }
      }
      catch (BackingStoreException localBackingStoreException) {}
    }
    return null;
  }
  
  private String readPreferredInputMethod(String paramString)
  {
    if ((userRoot == null) || (paramString == null)) {
      return null;
    }
    return userRoot.node(paramString).get("descriptor", null);
  }
  
  private synchronized void putPreferredInputMethod(InputMethodLocator paramInputMethodLocator)
  {
    InputMethodDescriptor localInputMethodDescriptor = paramInputMethodLocator.getDescriptor();
    Locale localLocale = paramInputMethodLocator.getLocale();
    if (localLocale == null) {
      try
      {
        Locale[] arrayOfLocale = localInputMethodDescriptor.getAvailableLocales();
        if (arrayOfLocale.length == 1) {
          localLocale = arrayOfLocale[0];
        } else {
          return;
        }
      }
      catch (AWTException localAWTException)
      {
        return;
      }
    }
    if (localLocale.equals(Locale.JAPAN)) {
      localLocale = Locale.JAPANESE;
    }
    if (localLocale.equals(Locale.KOREA)) {
      localLocale = Locale.KOREAN;
    }
    if (localLocale.equals(new Locale("th", "TH"))) {
      localLocale = new Locale("th");
    }
    String str = "/sun/awt/im/preferredInputMethod/" + createLocalePath(localLocale);
    writePreferredInputMethod(str, localInputMethodDescriptor.getClass().getName());
    preferredLocatorCache.put(localLocale.toString().intern(), paramInputMethodLocator.deriveLocator(localLocale));
  }
  
  private String createLocalePath(Locale paramLocale)
  {
    String str1 = paramLocale.getLanguage();
    String str2 = paramLocale.getCountry();
    String str3 = paramLocale.getVariant();
    String str4 = null;
    if (!str3.equals("")) {
      str4 = "_" + str1 + "/_" + str2 + "/_" + str3;
    } else if (!str2.equals("")) {
      str4 = "_" + str1 + "/_" + str2;
    } else {
      str4 = "_" + str1;
    }
    return str4;
  }
  
  private void writePreferredInputMethod(String paramString1, String paramString2)
  {
    if (userRoot != null)
    {
      Preferences localPreferences = userRoot.node(paramString1);
      if (paramString2 != null) {
        localPreferences.put("descriptor", paramString2);
      } else {
        localPreferences.remove("descriptor");
      }
    }
  }
  
  private Preferences getUserRoot()
  {
    (Preferences)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Preferences run()
      {
        return Preferences.userRoot();
      }
    });
  }
  
  private Locale getAdvertisedLocale(InputMethodLocator paramInputMethodLocator, Locale paramLocale)
  {
    Locale localLocale = null;
    if (paramInputMethodLocator.isLocaleAvailable(paramLocale)) {
      localLocale = paramLocale;
    } else if (paramLocale.getLanguage().equals("ja"))
    {
      if (paramInputMethodLocator.isLocaleAvailable(Locale.JAPAN)) {
        localLocale = Locale.JAPAN;
      } else if (paramInputMethodLocator.isLocaleAvailable(Locale.JAPANESE)) {
        localLocale = Locale.JAPANESE;
      }
    }
    else if (paramLocale.getLanguage().equals("ko"))
    {
      if (paramInputMethodLocator.isLocaleAvailable(Locale.KOREA)) {
        localLocale = Locale.KOREA;
      } else if (paramInputMethodLocator.isLocaleAvailable(Locale.KOREAN)) {
        localLocale = Locale.KOREAN;
      }
    }
    else if (paramLocale.getLanguage().equals("th")) {
      if (paramInputMethodLocator.isLocaleAvailable(new Locale("th", "TH"))) {
        localLocale = new Locale("th", "TH");
      } else if (paramInputMethodLocator.isLocaleAvailable(new Locale("th"))) {
        localLocale = new Locale("th");
      }
    }
    return localLocale;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\ExecutableInputMethodManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */