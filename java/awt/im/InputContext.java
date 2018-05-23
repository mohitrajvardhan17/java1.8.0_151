package java.awt.im;

import java.awt.AWTEvent;
import java.awt.Component;
import java.beans.Transient;
import java.util.Locale;
import sun.awt.im.InputMethodContext;

public class InputContext
{
  protected InputContext() {}
  
  public static InputContext getInstance()
  {
    return new InputMethodContext();
  }
  
  public boolean selectInputMethod(Locale paramLocale)
  {
    return false;
  }
  
  public Locale getLocale()
  {
    return null;
  }
  
  public void setCharacterSubsets(Character.Subset[] paramArrayOfSubset) {}
  
  public void setCompositionEnabled(boolean paramBoolean) {}
  
  @Transient
  public boolean isCompositionEnabled()
  {
    return false;
  }
  
  public void reconvert() {}
  
  public void dispatchEvent(AWTEvent paramAWTEvent) {}
  
  public void removeNotify(Component paramComponent) {}
  
  public void endComposition() {}
  
  public void dispose() {}
  
  public Object getInputMethodControlObject()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\im\InputContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */