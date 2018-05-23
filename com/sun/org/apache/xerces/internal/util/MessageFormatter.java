package com.sun.org.apache.xerces.internal.util;

import java.util.Locale;
import java.util.MissingResourceException;

public abstract interface MessageFormatter
{
  public abstract String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject)
    throws MissingResourceException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\MessageFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */