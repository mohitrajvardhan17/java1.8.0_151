package sun.awt.im;

import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.im.InputMethodRequests;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodDescriptor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class InputContext
  extends java.awt.im.InputContext
  implements ComponentListener, WindowListener
{
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.im.InputContext");
  private InputMethodLocator inputMethodLocator;
  private InputMethod inputMethod;
  private boolean inputMethodCreationFailed;
  private HashMap<InputMethodLocator, InputMethod> usedInputMethods;
  private Component currentClientComponent;
  private Component awtFocussedComponent;
  private boolean isInputMethodActive;
  private Character.Subset[] characterSubsets = null;
  private boolean compositionAreaHidden = false;
  private static InputContext inputMethodWindowContext;
  private static InputMethod previousInputMethod = null;
  private boolean clientWindowNotificationEnabled = false;
  private Window clientWindowListened;
  private Rectangle clientWindowLocation = null;
  private HashMap<InputMethod, Boolean> perInputMethodState;
  private static AWTKeyStroke inputMethodSelectionKey;
  private static boolean inputMethodSelectionKeyInitialized = false;
  private static final String inputMethodSelectionKeyPath = "/java/awt/im/selectionKey";
  private static final String inputMethodSelectionKeyCodeName = "keyCode";
  private static final String inputMethodSelectionKeyModifiersName = "modifiers";
  
  protected InputContext()
  {
    InputMethodManager localInputMethodManager = InputMethodManager.getInstance();
    synchronized (InputContext.class)
    {
      if (!inputMethodSelectionKeyInitialized)
      {
        inputMethodSelectionKeyInitialized = true;
        if (localInputMethodManager.hasMultipleInputMethods()) {
          initializeInputMethodSelectionKey();
        }
      }
    }
    selectInputMethod(localInputMethodManager.getDefaultKeyboardLocale());
  }
  
  public synchronized boolean selectInputMethod(Locale paramLocale)
  {
    if (paramLocale == null) {
      throw new NullPointerException();
    }
    if (inputMethod != null)
    {
      if (inputMethod.setLocale(paramLocale)) {
        return true;
      }
    }
    else if ((inputMethodLocator != null) && (inputMethodLocator.isLocaleAvailable(paramLocale)))
    {
      inputMethodLocator = inputMethodLocator.deriveLocator(paramLocale);
      return true;
    }
    InputMethodLocator localInputMethodLocator = InputMethodManager.getInstance().findInputMethod(paramLocale);
    if (localInputMethodLocator != null)
    {
      changeInputMethod(localInputMethodLocator);
      return true;
    }
    if ((inputMethod == null) && (inputMethodLocator != null))
    {
      inputMethod = getInputMethod();
      if (inputMethod != null) {
        return inputMethod.setLocale(paramLocale);
      }
    }
    return false;
  }
  
  public Locale getLocale()
  {
    if (inputMethod != null) {
      return inputMethod.getLocale();
    }
    if (inputMethodLocator != null) {
      return inputMethodLocator.getLocale();
    }
    return null;
  }
  
  public void setCharacterSubsets(Character.Subset[] paramArrayOfSubset)
  {
    if (paramArrayOfSubset == null)
    {
      characterSubsets = null;
    }
    else
    {
      characterSubsets = new Character.Subset[paramArrayOfSubset.length];
      System.arraycopy(paramArrayOfSubset, 0, characterSubsets, 0, characterSubsets.length);
    }
    if (inputMethod != null) {
      inputMethod.setCharacterSubsets(paramArrayOfSubset);
    }
  }
  
  public synchronized void reconvert()
  {
    InputMethod localInputMethod = getInputMethod();
    if (localInputMethod == null) {
      throw new UnsupportedOperationException();
    }
    localInputMethod.reconvert();
  }
  
  public void dispatchEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof InputMethodEvent)) {
      return;
    }
    if ((paramAWTEvent instanceof FocusEvent))
    {
      localObject = ((FocusEvent)paramAWTEvent).getOppositeComponent();
      if ((localObject != null) && ((getComponentWindow((Component)localObject) instanceof InputMethodWindow)) && (((Component)localObject).getInputContext() == this)) {
        return;
      }
    }
    Object localObject = getInputMethod();
    int i = paramAWTEvent.getID();
    switch (i)
    {
    case 1004: 
      focusGained((Component)paramAWTEvent.getSource());
      break;
    case 1005: 
      focusLost((Component)paramAWTEvent.getSource(), ((FocusEvent)paramAWTEvent).isTemporary());
      break;
    case 401: 
      if (checkInputMethodSelectionKey((KeyEvent)paramAWTEvent)) {
        InputMethodManager.getInstance().notifyChangeRequestByHotKey((Component)paramAWTEvent.getSource());
      }
      break;
    }
    if ((localObject != null) && ((paramAWTEvent instanceof InputEvent))) {
      ((InputMethod)localObject).dispatchEvent(paramAWTEvent);
    }
  }
  
  private void focusGained(Component paramComponent)
  {
    synchronized (paramComponent.getTreeLock())
    {
      synchronized (this)
      {
        if ((!"sun.awt.im.CompositionArea".equals(paramComponent.getClass().getName())) && (!(getComponentWindow(paramComponent) instanceof InputMethodWindow)))
        {
          if (!paramComponent.isDisplayable()) {
            return;
          }
          if ((inputMethod != null) && (currentClientComponent != null) && (currentClientComponent != paramComponent))
          {
            if (!isInputMethodActive) {
              activateInputMethod(false);
            }
            endComposition();
            deactivateInputMethod(false);
          }
          currentClientComponent = paramComponent;
        }
        awtFocussedComponent = paramComponent;
        if ((inputMethod instanceof InputMethodAdapter)) {
          ((InputMethodAdapter)inputMethod).setAWTFocussedComponent(paramComponent);
        }
        if (!isInputMethodActive) {
          activateInputMethod(true);
        }
        InputMethodContext localInputMethodContext = (InputMethodContext)this;
        if (!localInputMethodContext.isCompositionAreaVisible())
        {
          InputMethodRequests localInputMethodRequests = paramComponent.getInputMethodRequests();
          if ((localInputMethodRequests != null) && (localInputMethodContext.useBelowTheSpotInput())) {
            localInputMethodContext.setCompositionAreaUndecorated(true);
          } else {
            localInputMethodContext.setCompositionAreaUndecorated(false);
          }
        }
        if (compositionAreaHidden == true)
        {
          ((InputMethodContext)this).setCompositionAreaVisible(true);
          compositionAreaHidden = false;
        }
      }
    }
  }
  
  private void activateInputMethod(boolean paramBoolean)
  {
    if ((inputMethodWindowContext != null) && (inputMethodWindowContext != this) && (inputMethodWindowContextinputMethodLocator != null) && (!inputMethodWindowContextinputMethodLocator.sameInputMethod(inputMethodLocator)) && (inputMethodWindowContextinputMethod != null)) {
      inputMethodWindowContextinputMethod.hideWindows();
    }
    inputMethodWindowContext = this;
    if (inputMethod != null)
    {
      if ((previousInputMethod != inputMethod) && ((previousInputMethod instanceof InputMethodAdapter))) {
        ((InputMethodAdapter)previousInputMethod).stopListening();
      }
      previousInputMethod = null;
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
        log.fine("Current client component " + currentClientComponent);
      }
      if ((inputMethod instanceof InputMethodAdapter)) {
        ((InputMethodAdapter)inputMethod).setClientComponent(currentClientComponent);
      }
      inputMethod.activate();
      isInputMethodActive = true;
      if (perInputMethodState != null)
      {
        Boolean localBoolean = (Boolean)perInputMethodState.remove(inputMethod);
        if (localBoolean != null) {
          clientWindowNotificationEnabled = localBoolean.booleanValue();
        }
      }
      if (clientWindowNotificationEnabled)
      {
        if (!addedClientWindowListeners()) {
          addClientWindowListeners();
        }
        synchronized (this)
        {
          if (clientWindowListened != null) {
            notifyClientWindowChange(clientWindowListened);
          }
        }
      }
      else if (addedClientWindowListeners())
      {
        removeClientWindowListeners();
      }
    }
    InputMethodManager.getInstance().setInputContext(this);
    ((InputMethodContext)this).grabCompositionArea(paramBoolean);
  }
  
  static Window getComponentWindow(Component paramComponent)
  {
    for (;;)
    {
      if (paramComponent == null) {
        return null;
      }
      if ((paramComponent instanceof Window)) {
        return (Window)paramComponent;
      }
      paramComponent = paramComponent.getParent();
    }
  }
  
  private void focusLost(Component paramComponent, boolean paramBoolean)
  {
    synchronized (paramComponent.getTreeLock())
    {
      synchronized (this)
      {
        if (isInputMethodActive) {
          deactivateInputMethod(paramBoolean);
        }
        awtFocussedComponent = null;
        if ((inputMethod instanceof InputMethodAdapter)) {
          ((InputMethodAdapter)inputMethod).setAWTFocussedComponent(null);
        }
        InputMethodContext localInputMethodContext = (InputMethodContext)this;
        if (localInputMethodContext.isCompositionAreaVisible())
        {
          localInputMethodContext.setCompositionAreaVisible(false);
          compositionAreaHidden = true;
        }
      }
    }
  }
  
  private boolean checkInputMethodSelectionKey(KeyEvent paramKeyEvent)
  {
    if (inputMethodSelectionKey != null)
    {
      AWTKeyStroke localAWTKeyStroke = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
      return inputMethodSelectionKey.equals(localAWTKeyStroke);
    }
    return false;
  }
  
  private void deactivateInputMethod(boolean paramBoolean)
  {
    InputMethodManager.getInstance().setInputContext(null);
    if (inputMethod != null)
    {
      isInputMethodActive = false;
      inputMethod.deactivate(paramBoolean);
      previousInputMethod = inputMethod;
    }
  }
  
  synchronized void changeInputMethod(InputMethodLocator paramInputMethodLocator)
  {
    if (inputMethodLocator == null)
    {
      inputMethodLocator = paramInputMethodLocator;
      inputMethodCreationFailed = false;
      return;
    }
    if (inputMethodLocator.sameInputMethod(paramInputMethodLocator))
    {
      localLocale = paramInputMethodLocator.getLocale();
      if ((localLocale != null) && (inputMethodLocator.getLocale() != localLocale))
      {
        if (inputMethod != null) {
          inputMethod.setLocale(localLocale);
        }
        inputMethodLocator = paramInputMethodLocator;
      }
      return;
    }
    Locale localLocale = inputMethodLocator.getLocale();
    boolean bool1 = isInputMethodActive;
    int i = 0;
    boolean bool2 = false;
    if (inputMethod != null)
    {
      try
      {
        bool2 = inputMethod.isCompositionEnabled();
        i = 1;
      }
      catch (UnsupportedOperationException localUnsupportedOperationException1) {}
      if (currentClientComponent != null)
      {
        if (!isInputMethodActive) {
          activateInputMethod(false);
        }
        endComposition();
        deactivateInputMethod(false);
        if ((inputMethod instanceof InputMethodAdapter)) {
          ((InputMethodAdapter)inputMethod).setClientComponent(null);
        }
      }
      localLocale = inputMethod.getLocale();
      if (usedInputMethods == null) {
        usedInputMethods = new HashMap(5);
      }
      if (perInputMethodState == null) {
        perInputMethodState = new HashMap(5);
      }
      usedInputMethods.put(inputMethodLocator.deriveLocator(null), inputMethod);
      perInputMethodState.put(inputMethod, Boolean.valueOf(clientWindowNotificationEnabled));
      enableClientWindowNotification(inputMethod, false);
      if (this == inputMethodWindowContext)
      {
        inputMethod.hideWindows();
        inputMethodWindowContext = null;
      }
      inputMethodLocator = null;
      inputMethod = null;
      inputMethodCreationFailed = false;
    }
    if ((paramInputMethodLocator.getLocale() == null) && (localLocale != null) && (paramInputMethodLocator.isLocaleAvailable(localLocale))) {
      paramInputMethodLocator = paramInputMethodLocator.deriveLocator(localLocale);
    }
    inputMethodLocator = paramInputMethodLocator;
    inputMethodCreationFailed = false;
    if (bool1)
    {
      inputMethod = getInputMethodInstance();
      if ((inputMethod instanceof InputMethodAdapter)) {
        ((InputMethodAdapter)inputMethod).setAWTFocussedComponent(awtFocussedComponent);
      }
      activateInputMethod(true);
    }
    if (i != 0)
    {
      inputMethod = getInputMethod();
      if (inputMethod != null) {
        try
        {
          inputMethod.setCompositionEnabled(bool2);
        }
        catch (UnsupportedOperationException localUnsupportedOperationException2) {}
      }
    }
  }
  
  Component getClientComponent()
  {
    return currentClientComponent;
  }
  
  public synchronized void removeNotify(Component paramComponent)
  {
    if (paramComponent == null) {
      throw new NullPointerException();
    }
    if (inputMethod == null)
    {
      if (paramComponent == currentClientComponent) {
        currentClientComponent = null;
      }
      return;
    }
    if (paramComponent == awtFocussedComponent) {
      focusLost(paramComponent, false);
    }
    if (paramComponent == currentClientComponent)
    {
      if (isInputMethodActive) {
        deactivateInputMethod(false);
      }
      inputMethod.removeNotify();
      if ((clientWindowNotificationEnabled) && (addedClientWindowListeners())) {
        removeClientWindowListeners();
      }
      currentClientComponent = null;
      if ((inputMethod instanceof InputMethodAdapter)) {
        ((InputMethodAdapter)inputMethod).setClientComponent(null);
      }
      if (EventQueue.isDispatchThread()) {
        ((InputMethodContext)this).releaseCompositionArea();
      } else {
        EventQueue.invokeLater(new Runnable()
        {
          public void run()
          {
            ((InputMethodContext)InputContext.this).releaseCompositionArea();
          }
        });
      }
    }
  }
  
  public synchronized void dispose()
  {
    if (currentClientComponent != null) {
      throw new IllegalStateException("Can't dispose InputContext while it's active");
    }
    if (inputMethod != null)
    {
      if (this == inputMethodWindowContext)
      {
        inputMethod.hideWindows();
        inputMethodWindowContext = null;
      }
      if (inputMethod == previousInputMethod) {
        previousInputMethod = null;
      }
      if (clientWindowNotificationEnabled)
      {
        if (addedClientWindowListeners()) {
          removeClientWindowListeners();
        }
        clientWindowNotificationEnabled = false;
      }
      inputMethod.dispose();
      if (clientWindowNotificationEnabled) {
        enableClientWindowNotification(inputMethod, false);
      }
      inputMethod = null;
    }
    inputMethodLocator = null;
    if ((usedInputMethods != null) && (!usedInputMethods.isEmpty()))
    {
      Iterator localIterator = usedInputMethods.values().iterator();
      usedInputMethods = null;
      while (localIterator.hasNext()) {
        ((InputMethod)localIterator.next()).dispose();
      }
    }
    clientWindowNotificationEnabled = false;
    clientWindowListened = null;
    perInputMethodState = null;
  }
  
  public synchronized Object getInputMethodControlObject()
  {
    InputMethod localInputMethod = getInputMethod();
    if (localInputMethod != null) {
      return localInputMethod.getControlObject();
    }
    return null;
  }
  
  public void setCompositionEnabled(boolean paramBoolean)
  {
    InputMethod localInputMethod = getInputMethod();
    if (localInputMethod == null) {
      throw new UnsupportedOperationException();
    }
    localInputMethod.setCompositionEnabled(paramBoolean);
  }
  
  public boolean isCompositionEnabled()
  {
    InputMethod localInputMethod = getInputMethod();
    if (localInputMethod == null) {
      throw new UnsupportedOperationException();
    }
    return localInputMethod.isCompositionEnabled();
  }
  
  public String getInputMethodInfo()
  {
    InputMethod localInputMethod = getInputMethod();
    if (localInputMethod == null) {
      throw new UnsupportedOperationException("Null input method");
    }
    String str = null;
    if ((localInputMethod instanceof InputMethodAdapter)) {
      str = ((InputMethodAdapter)localInputMethod).getNativeInputMethodInfo();
    }
    if ((str == null) && (inputMethodLocator != null)) {
      str = inputMethodLocator.getDescriptor().getInputMethodDisplayName(getLocale(), SunToolkit.getStartupLocale());
    }
    if ((str != null) && (!str.equals(""))) {
      return str;
    }
    return localInputMethod.toString() + "-" + localInputMethod.getLocale().toString();
  }
  
  public void disableNativeIM()
  {
    InputMethod localInputMethod = getInputMethod();
    if ((localInputMethod != null) && ((localInputMethod instanceof InputMethodAdapter))) {
      ((InputMethodAdapter)localInputMethod).stopListening();
    }
  }
  
  private synchronized InputMethod getInputMethod()
  {
    if (inputMethod != null) {
      return inputMethod;
    }
    if (inputMethodCreationFailed) {
      return null;
    }
    inputMethod = getInputMethodInstance();
    return inputMethod;
  }
  
  private InputMethod getInputMethodInstance()
  {
    InputMethodLocator localInputMethodLocator = inputMethodLocator;
    if (localInputMethodLocator == null)
    {
      inputMethodCreationFailed = true;
      return null;
    }
    Locale localLocale = localInputMethodLocator.getLocale();
    InputMethod localInputMethod = null;
    if (usedInputMethods != null)
    {
      localInputMethod = (InputMethod)usedInputMethods.remove(localInputMethodLocator.deriveLocator(null));
      if (localInputMethod != null)
      {
        if (localLocale != null) {
          localInputMethod.setLocale(localLocale);
        }
        localInputMethod.setCharacterSubsets(characterSubsets);
        Boolean localBoolean = (Boolean)perInputMethodState.remove(localInputMethod);
        if (localBoolean != null) {
          enableClientWindowNotification(localInputMethod, localBoolean.booleanValue());
        }
        ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(localInputMethod instanceof InputMethodAdapter)) || (((InputMethodAdapter)localInputMethod).supportsBelowTheSpot()));
        return localInputMethod;
      }
    }
    try
    {
      localInputMethod = localInputMethodLocator.getDescriptor().createInputMethod();
      if (localLocale != null) {
        localInputMethod.setLocale(localLocale);
      }
      localInputMethod.setInputMethodContext((InputMethodContext)this);
      localInputMethod.setCharacterSubsets(characterSubsets);
    }
    catch (Exception localException)
    {
      logCreationFailed(localException);
      inputMethodCreationFailed = true;
      if (localInputMethod != null) {
        localInputMethod = null;
      }
    }
    catch (LinkageError localLinkageError)
    {
      logCreationFailed(localLinkageError);
      inputMethodCreationFailed = true;
    }
    ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot((!(localInputMethod instanceof InputMethodAdapter)) || (((InputMethodAdapter)localInputMethod).supportsBelowTheSpot()));
    return localInputMethod;
  }
  
  private void logCreationFailed(Throwable paramThrowable)
  {
    PlatformLogger localPlatformLogger = PlatformLogger.getLogger("sun.awt.im");
    if (localPlatformLogger.isLoggable(PlatformLogger.Level.CONFIG))
    {
      String str = Toolkit.getProperty("AWT.InputMethodCreationFailed", "Could not create {0}. Reason: {1}");
      Object[] arrayOfObject = { inputMethodLocator.getDescriptor().getInputMethodDisplayName(null, Locale.getDefault()), paramThrowable.getLocalizedMessage() };
      MessageFormat localMessageFormat = new MessageFormat(str);
      localPlatformLogger.config(localMessageFormat.format(arrayOfObject));
    }
  }
  
  InputMethodLocator getInputMethodLocator()
  {
    if (inputMethod != null) {
      return inputMethodLocator.deriveLocator(inputMethod.getLocale());
    }
    return inputMethodLocator;
  }
  
  public synchronized void endComposition()
  {
    if (inputMethod != null) {
      inputMethod.endComposition();
    }
  }
  
  synchronized void enableClientWindowNotification(InputMethod paramInputMethod, boolean paramBoolean)
  {
    if (paramInputMethod != inputMethod)
    {
      if (perInputMethodState == null) {
        perInputMethodState = new HashMap(5);
      }
      perInputMethodState.put(paramInputMethod, Boolean.valueOf(paramBoolean));
      return;
    }
    if (clientWindowNotificationEnabled != paramBoolean)
    {
      clientWindowLocation = null;
      clientWindowNotificationEnabled = paramBoolean;
    }
    if (clientWindowNotificationEnabled)
    {
      if (!addedClientWindowListeners()) {
        addClientWindowListeners();
      }
      if (clientWindowListened != null)
      {
        clientWindowLocation = null;
        notifyClientWindowChange(clientWindowListened);
      }
    }
    else if (addedClientWindowListeners())
    {
      removeClientWindowListeners();
    }
  }
  
  private synchronized void notifyClientWindowChange(Window paramWindow)
  {
    if (inputMethod == null) {
      return;
    }
    if ((!paramWindow.isVisible()) || (((paramWindow instanceof Frame)) && (((Frame)paramWindow).getState() == 1)))
    {
      clientWindowLocation = null;
      inputMethod.notifyClientWindowChange(null);
      return;
    }
    Rectangle localRectangle = paramWindow.getBounds();
    if ((clientWindowLocation == null) || (!clientWindowLocation.equals(localRectangle)))
    {
      clientWindowLocation = localRectangle;
      inputMethod.notifyClientWindowChange(clientWindowLocation);
    }
  }
  
  private synchronized void addClientWindowListeners()
  {
    Component localComponent = getClientComponent();
    if (localComponent == null) {
      return;
    }
    Window localWindow = getComponentWindow(localComponent);
    if (localWindow == null) {
      return;
    }
    localWindow.addComponentListener(this);
    localWindow.addWindowListener(this);
    clientWindowListened = localWindow;
  }
  
  private synchronized void removeClientWindowListeners()
  {
    clientWindowListened.removeComponentListener(this);
    clientWindowListened.removeWindowListener(this);
    clientWindowListened = null;
  }
  
  private boolean addedClientWindowListeners()
  {
    return clientWindowListened != null;
  }
  
  public void componentResized(ComponentEvent paramComponentEvent)
  {
    notifyClientWindowChange((Window)paramComponentEvent.getComponent());
  }
  
  public void componentMoved(ComponentEvent paramComponentEvent)
  {
    notifyClientWindowChange((Window)paramComponentEvent.getComponent());
  }
  
  public void componentShown(ComponentEvent paramComponentEvent)
  {
    notifyClientWindowChange((Window)paramComponentEvent.getComponent());
  }
  
  public void componentHidden(ComponentEvent paramComponentEvent)
  {
    notifyClientWindowChange((Window)paramComponentEvent.getComponent());
  }
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent) {}
  
  public void windowClosed(WindowEvent paramWindowEvent) {}
  
  public void windowIconified(WindowEvent paramWindowEvent)
  {
    notifyClientWindowChange(paramWindowEvent.getWindow());
  }
  
  public void windowDeiconified(WindowEvent paramWindowEvent)
  {
    notifyClientWindowChange(paramWindowEvent.getWindow());
  }
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
  
  private void initializeInputMethodSelectionKey()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Preferences localPreferences = Preferences.userRoot();
        InputContext.access$002(InputContext.this.getInputMethodSelectionKeyStroke(localPreferences));
        if (InputContext.inputMethodSelectionKey == null)
        {
          localPreferences = Preferences.systemRoot();
          InputContext.access$002(InputContext.this.getInputMethodSelectionKeyStroke(localPreferences));
        }
        return null;
      }
    });
  }
  
  private AWTKeyStroke getInputMethodSelectionKeyStroke(Preferences paramPreferences)
  {
    try
    {
      if (paramPreferences.nodeExists("/java/awt/im/selectionKey"))
      {
        Preferences localPreferences = paramPreferences.node("/java/awt/im/selectionKey");
        int i = localPreferences.getInt("keyCode", 0);
        if (i != 0)
        {
          int j = localPreferences.getInt("modifiers", 0);
          return AWTKeyStroke.getAWTKeyStroke(i, j);
        }
      }
    }
    catch (BackingStoreException localBackingStoreException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\InputContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */