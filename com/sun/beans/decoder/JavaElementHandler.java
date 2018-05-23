package com.sun.beans.decoder;

import java.beans.XMLDecoder;

final class JavaElementHandler
  extends ElementHandler
{
  private Class<?> type;
  private ValueObject value;
  
  JavaElementHandler() {}
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (!paramString1.equals("version")) {
      if (paramString1.equals("class")) {
        type = getOwner().findClass(paramString2);
      } else {
        super.addAttribute(paramString1, paramString2);
      }
    }
  }
  
  protected void addArgument(Object paramObject)
  {
    getOwner().addObject(paramObject);
  }
  
  protected boolean isArgument()
  {
    return false;
  }
  
  protected ValueObject getValueObject()
  {
    if (value == null) {
      value = ValueObjectImpl.create(getValue());
    }
    return value;
  }
  
  private Object getValue()
  {
    Object localObject = getOwner().getOwner();
    if ((type == null) || (isValid(localObject))) {
      return localObject;
    }
    if ((localObject instanceof XMLDecoder))
    {
      XMLDecoder localXMLDecoder = (XMLDecoder)localObject;
      localObject = localXMLDecoder.getOwner();
      if (isValid(localObject)) {
        return localObject;
      }
    }
    throw new IllegalStateException("Unexpected owner class: " + localObject.getClass().getName());
  }
  
  private boolean isValid(Object paramObject)
  {
    return (paramObject == null) || (type.isInstance(paramObject));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\JavaElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */