package javax.xml.ws;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;

public abstract interface LogicalMessage
{
  public abstract Source getPayload();
  
  public abstract void setPayload(Source paramSource);
  
  public abstract Object getPayload(JAXBContext paramJAXBContext);
  
  public abstract void setPayload(Object paramObject, JAXBContext paramJAXBContext);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\LogicalMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */