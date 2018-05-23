package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;

public abstract class PropertyGetterBase
  implements PropertyGetter
{
  protected Class type;
  
  public PropertyGetterBase() {}
  
  public Class getType()
  {
    return type;
  }
  
  public static boolean getterPattern(Method paramMethod)
  {
    if ((!paramMethod.getReturnType().equals(Void.TYPE)) && ((paramMethod.getParameterTypes() == null) || (paramMethod.getParameterTypes().length == 0)))
    {
      if ((paramMethod.getName().startsWith("get")) && (paramMethod.getName().length() > 3)) {
        return true;
      }
      if ((paramMethod.getReturnType().equals(Boolean.TYPE)) && (paramMethod.getName().startsWith("is")) && (paramMethod.getName().length() > 2)) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertyGetterBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */