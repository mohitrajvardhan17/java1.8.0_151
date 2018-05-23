package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import java.lang.reflect.Type;
import javax.xml.bind.JAXBElement;

public abstract interface RuntimeElementInfo
  extends ElementInfo<Type, Class>, RuntimeElement
{
  public abstract RuntimeClassInfo getScope();
  
  public abstract RuntimeElementPropertyInfo getProperty();
  
  public abstract Class<? extends JAXBElement> getType();
  
  public abstract RuntimeNonElement getContentType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeElementInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */