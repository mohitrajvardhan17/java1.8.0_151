package sun.awt.im;

import java.awt.AWTException;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

final class InputMethodLocator
{
  private InputMethodDescriptor descriptor;
  private ClassLoader loader;
  private Locale locale;
  
  InputMethodLocator(InputMethodDescriptor paramInputMethodDescriptor, ClassLoader paramClassLoader, Locale paramLocale)
  {
    if (paramInputMethodDescriptor == null) {
      throw new NullPointerException("descriptor can't be null");
    }
    descriptor = paramInputMethodDescriptor;
    loader = paramClassLoader;
    locale = paramLocale;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    InputMethodLocator localInputMethodLocator = (InputMethodLocator)paramObject;
    if (!descriptor.getClass().equals(descriptor.getClass())) {
      return false;
    }
    if (((loader == null) && (loader != null)) || ((loader != null) && (!loader.equals(loader)))) {
      return false;
    }
    return ((locale != null) || (locale == null)) && ((locale == null) || (locale.equals(locale)));
  }
  
  public int hashCode()
  {
    int i = descriptor.hashCode();
    if (loader != null) {
      i |= loader.hashCode() << 10;
    }
    if (locale != null) {
      i |= locale.hashCode() << 20;
    }
    return i;
  }
  
  InputMethodDescriptor getDescriptor()
  {
    return descriptor;
  }
  
  ClassLoader getClassLoader()
  {
    return loader;
  }
  
  Locale getLocale()
  {
    return locale;
  }
  
  boolean isLocaleAvailable(Locale paramLocale)
  {
    try
    {
      Locale[] arrayOfLocale = descriptor.getAvailableLocales();
      for (int i = 0; i < arrayOfLocale.length; i++) {
        if (arrayOfLocale[i].equals(paramLocale)) {
          return true;
        }
      }
    }
    catch (AWTException localAWTException) {}
    return false;
  }
  
  InputMethodLocator deriveLocator(Locale paramLocale)
  {
    if (paramLocale == locale) {
      return this;
    }
    return new InputMethodLocator(descriptor, loader, paramLocale);
  }
  
  boolean sameInputMethod(InputMethodLocator paramInputMethodLocator)
  {
    if (paramInputMethodLocator == this) {
      return true;
    }
    if (paramInputMethodLocator == null) {
      return false;
    }
    if (!descriptor.getClass().equals(descriptor.getClass())) {
      return false;
    }
    return ((loader != null) || (loader == null)) && ((loader == null) || (loader.equals(loader)));
  }
  
  String getActionCommandString()
  {
    String str = descriptor.getClass().getName();
    if (locale == null) {
      return str;
    }
    return str + "\n" + locale.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\InputMethodLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */