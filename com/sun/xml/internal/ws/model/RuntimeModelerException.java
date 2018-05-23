package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class RuntimeModelerException
  extends JAXWSExceptionBase
{
  public RuntimeModelerException(String paramString, Object... paramVarArgs)
  {
    super(paramString, paramVarArgs);
  }
  
  public RuntimeModelerException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public RuntimeModelerException(Localizable paramLocalizable)
  {
    super("nestedModelerError", new Object[] { paramLocalizable });
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.modeler";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\RuntimeModelerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */