package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import java.io.CharConversionException;
import java.util.Locale;

public class MalformedByteSequenceException
  extends CharConversionException
{
  static final long serialVersionUID = 8436382245048328739L;
  private MessageFormatter fFormatter;
  private Locale fLocale;
  private String fDomain;
  private String fKey;
  private Object[] fArguments;
  private String fMessage;
  
  public MalformedByteSequenceException(MessageFormatter paramMessageFormatter, Locale paramLocale, String paramString1, String paramString2, Object[] paramArrayOfObject)
  {
    fFormatter = paramMessageFormatter;
    fLocale = paramLocale;
    fDomain = paramString1;
    fKey = paramString2;
    fArguments = paramArrayOfObject;
  }
  
  public String getDomain()
  {
    return fDomain;
  }
  
  public String getKey()
  {
    return fKey;
  }
  
  public Object[] getArguments()
  {
    return fArguments;
  }
  
  public String getMessage()
  {
    if (fMessage == null)
    {
      fMessage = fFormatter.formatMessage(fLocale, fKey, fArguments);
      fFormatter = null;
      fLocale = null;
    }
    return fMessage;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\io\MalformedByteSequenceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */