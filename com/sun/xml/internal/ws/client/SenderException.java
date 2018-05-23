package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class SenderException
  extends JAXWSExceptionBase
{
  public SenderException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public SenderException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SenderException(Localizable paramLocalizable)
  {
    super("sender.nestedError", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.sender";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\SenderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */