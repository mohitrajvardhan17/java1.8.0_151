package sun.awt;

import java.awt.AWTException;
import java.awt.Window;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;
import sun.awt.im.InputContext;

public abstract interface InputMethodSupport
{
  public abstract InputMethodDescriptor getInputMethodAdapterDescriptor()
    throws AWTException;
  
  public abstract Window createInputMethodWindow(String paramString, InputContext paramInputContext);
  
  public abstract boolean enableInputMethodsForTextComponent();
  
  public abstract Locale getDefaultKeyboardLocale();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\InputMethodSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */