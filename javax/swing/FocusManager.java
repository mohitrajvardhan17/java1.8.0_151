package javax.swing;

import java.awt.DefaultFocusTraversalPolicy;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;

public abstract class FocusManager
  extends DefaultKeyboardFocusManager
{
  public static final String FOCUS_MANAGER_CLASS_PROPERTY = "FocusManagerClassName";
  private static boolean enabled = true;
  
  public FocusManager() {}
  
  public static FocusManager getCurrentManager()
  {
    KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    if ((localKeyboardFocusManager instanceof FocusManager)) {
      return (FocusManager)localKeyboardFocusManager;
    }
    return new DelegatingDefaultFocusManager(localKeyboardFocusManager);
  }
  
  public static void setCurrentManager(FocusManager paramFocusManager)
    throws SecurityException
  {
    FocusManager localFocusManager = (paramFocusManager instanceof DelegatingDefaultFocusManager) ? ((DelegatingDefaultFocusManager)paramFocusManager).getDelegate() : paramFocusManager;
    KeyboardFocusManager.setCurrentKeyboardFocusManager(localFocusManager);
  }
  
  @Deprecated
  public static void disableSwingFocusManager()
  {
    if (enabled)
    {
      enabled = false;
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new DefaultFocusTraversalPolicy());
    }
  }
  
  @Deprecated
  public static boolean isFocusManagerEnabled()
  {
    return enabled;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\FocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */