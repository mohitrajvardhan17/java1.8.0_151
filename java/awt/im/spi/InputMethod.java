package java.awt.im.spi;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.util.Locale;

public abstract interface InputMethod
{
  public abstract void setInputMethodContext(InputMethodContext paramInputMethodContext);
  
  public abstract boolean setLocale(Locale paramLocale);
  
  public abstract Locale getLocale();
  
  public abstract void setCharacterSubsets(Character.Subset[] paramArrayOfSubset);
  
  public abstract void setCompositionEnabled(boolean paramBoolean);
  
  public abstract boolean isCompositionEnabled();
  
  public abstract void reconvert();
  
  public abstract void dispatchEvent(AWTEvent paramAWTEvent);
  
  public abstract void notifyClientWindowChange(Rectangle paramRectangle);
  
  public abstract void activate();
  
  public abstract void deactivate(boolean paramBoolean);
  
  public abstract void hideWindows();
  
  public abstract void removeNotify();
  
  public abstract void endComposition();
  
  public abstract void dispose();
  
  public abstract Object getControlObject();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\im\spi\InputMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */