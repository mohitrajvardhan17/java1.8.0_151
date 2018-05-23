package sun.awt.im;

import java.awt.Component;
import java.util.Locale;

public abstract class InputMethodManager
{
  private static final String threadName = "AWT-InputMethodManager";
  private static final Object LOCK = new Object();
  private static InputMethodManager inputMethodManager;
  
  public InputMethodManager() {}
  
  public static final InputMethodManager getInstance()
  {
    if (inputMethodManager != null) {
      return inputMethodManager;
    }
    synchronized (LOCK)
    {
      if (inputMethodManager == null)
      {
        ExecutableInputMethodManager localExecutableInputMethodManager = new ExecutableInputMethodManager();
        if (localExecutableInputMethodManager.hasMultipleInputMethods())
        {
          localExecutableInputMethodManager.initialize();
          Thread localThread = new Thread(localExecutableInputMethodManager, "AWT-InputMethodManager");
          localThread.setDaemon(true);
          localThread.setPriority(6);
          localThread.start();
        }
        inputMethodManager = localExecutableInputMethodManager;
      }
    }
    return inputMethodManager;
  }
  
  public abstract String getTriggerMenuString();
  
  public abstract void notifyChangeRequest(Component paramComponent);
  
  public abstract void notifyChangeRequestByHotKey(Component paramComponent);
  
  abstract void setInputContext(InputContext paramInputContext);
  
  abstract InputMethodLocator findInputMethod(Locale paramLocale);
  
  abstract Locale getDefaultKeyboardLocale();
  
  abstract boolean hasMultipleInputMethods();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\InputMethodManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */