package com.sun.xml.internal.ws.spi.db;

import java.util.Map;
import javax.xml.namespace.QName;

public abstract class WrapperAccessor
{
  protected Map<Object, PropertySetter> propertySetters;
  protected Map<Object, PropertyGetter> propertyGetters;
  protected boolean elementLocalNameCollision;
  
  public WrapperAccessor() {}
  
  protected PropertySetter getPropertySetter(QName paramQName)
  {
    String str = elementLocalNameCollision ? paramQName : paramQName.getLocalPart();
    return (PropertySetter)propertySetters.get(str);
  }
  
  protected PropertyGetter getPropertyGetter(QName paramQName)
  {
    String str = elementLocalNameCollision ? paramQName : paramQName.getLocalPart();
    return (PropertyGetter)propertyGetters.get(str);
  }
  
  public PropertyAccessor getPropertyAccessor(String paramString1, String paramString2)
  {
    QName localQName = new QName(paramString1, paramString2);
    final PropertySetter localPropertySetter = getPropertySetter(localQName);
    final PropertyGetter localPropertyGetter = getPropertyGetter(localQName);
    new PropertyAccessor()
    {
      public Object get(Object paramAnonymousObject)
        throws DatabindingException
      {
        return localPropertyGetter.get(paramAnonymousObject);
      }
      
      public void set(Object paramAnonymousObject1, Object paramAnonymousObject2)
        throws DatabindingException
      {
        localPropertySetter.set(paramAnonymousObject1, paramAnonymousObject2);
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\WrapperAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */