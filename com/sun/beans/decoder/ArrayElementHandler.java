package com.sun.beans.decoder;

import java.lang.reflect.Array;

final class ArrayElementHandler
  extends NewElementHandler
{
  private Integer length;
  
  ArrayElementHandler() {}
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (paramString1.equals("length")) {
      length = Integer.valueOf(paramString2);
    } else {
      super.addAttribute(paramString1, paramString2);
    }
  }
  
  public void startElement()
  {
    if (length != null) {
      getValueObject();
    }
  }
  
  protected boolean isArgument()
  {
    return true;
  }
  
  protected ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject)
  {
    if (paramClass == null) {
      paramClass = Object.class;
    }
    if (length != null) {
      return ValueObjectImpl.create(Array.newInstance(paramClass, length.intValue()));
    }
    Object localObject = Array.newInstance(paramClass, paramArrayOfObject.length);
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      Array.set(localObject, i, paramArrayOfObject[i]);
    }
    return ValueObjectImpl.create(localObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\ArrayElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */