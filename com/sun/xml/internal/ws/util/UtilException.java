package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class UtilException
  extends JAXWSExceptionBase
{
  public UtilException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public UtilException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public UtilException(Localizable paramLocalizable)
  {
    super("nestedUtilError", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.util";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\UtilException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */