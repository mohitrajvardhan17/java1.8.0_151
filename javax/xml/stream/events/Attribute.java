package javax.xml.stream.events;

import javax.xml.namespace.QName;

public abstract interface Attribute
  extends XMLEvent
{
  public abstract QName getName();
  
  public abstract String getValue();
  
  public abstract String getDTDType();
  
  public abstract boolean isSpecified();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\stream\events\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */