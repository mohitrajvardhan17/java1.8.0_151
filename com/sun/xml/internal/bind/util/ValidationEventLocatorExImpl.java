package com.sun.xml.internal.bind.util;

import com.sun.xml.internal.bind.ValidationEventLocatorEx;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

public class ValidationEventLocatorExImpl
  extends ValidationEventLocatorImpl
  implements ValidationEventLocatorEx
{
  private final String fieldName;
  
  public ValidationEventLocatorExImpl(Object paramObject, String paramString)
  {
    super(paramObject);
    fieldName = paramString;
  }
  
  public String getFieldName()
  {
    return fieldName;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("[url=");
    localStringBuffer.append(getURL());
    localStringBuffer.append(",line=");
    localStringBuffer.append(getLineNumber());
    localStringBuffer.append(",column=");
    localStringBuffer.append(getColumnNumber());
    localStringBuffer.append(",node=");
    localStringBuffer.append(getNode());
    localStringBuffer.append(",object=");
    localStringBuffer.append(getObject());
    localStringBuffer.append(",field=");
    localStringBuffer.append(getFieldName());
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\util\ValidationEventLocatorExImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */