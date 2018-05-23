package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;

public abstract class PropertySetterBase
  implements PropertySetter
{
  protected Class type;
  
  public PropertySetterBase() {}
  
  public Class getType()
  {
    return type;
  }
  
  public static boolean setterPattern(Method paramMethod)
  {
    return (paramMethod.getName().startsWith("set")) && (paramMethod.getName().length() > 3) && (paramMethod.getReturnType().equals(Void.TYPE)) && (paramMethod.getParameterTypes() != null) && (paramMethod.getParameterTypes().length == 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertySetterBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */