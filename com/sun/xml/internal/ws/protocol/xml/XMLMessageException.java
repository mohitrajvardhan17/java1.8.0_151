package com.sun.xml.internal.ws.protocol.xml;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLMessageException
  extends JAXWSExceptionBase
{
  public XMLMessageException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public XMLMessageException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public XMLMessageException(Localizable paramLocalizable)
  {
    super("server.rt.err", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.xmlmessage";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\protocol\xml\XMLMessageException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */