package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;

public abstract interface Detail
  extends SOAPFaultElement
{
  public abstract DetailEntry addDetailEntry(Name paramName)
    throws SOAPException;
  
  public abstract DetailEntry addDetailEntry(QName paramQName)
    throws SOAPException;
  
  public abstract Iterator getDetailEntries();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\Detail.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */