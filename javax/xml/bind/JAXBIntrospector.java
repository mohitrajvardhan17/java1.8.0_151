package javax.xml.bind;

import javax.xml.namespace.QName;

public abstract class JAXBIntrospector
{
  public JAXBIntrospector() {}
  
  public abstract boolean isElement(Object paramObject);
  
  public abstract QName getElementName(Object paramObject);
  
  public static Object getValue(Object paramObject)
  {
    if ((paramObject instanceof JAXBElement)) {
      return ((JAXBElement)paramObject).getValue();
    }
    return paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\JAXBIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */