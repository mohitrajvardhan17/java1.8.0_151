package com.sun.xml.internal.ws.encoding.soap;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class DeserializationException
  extends JAXWSExceptionBase
{
  public DeserializationException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public DeserializationException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public DeserializationException(Localizable paramLocalizable)
  {
    super("nestedDeserializationError", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.encoding";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\soap\DeserializationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */