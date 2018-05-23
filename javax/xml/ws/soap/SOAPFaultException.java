package javax.xml.ws.soap;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;

public class SOAPFaultException
  extends ProtocolException
{
  private SOAPFault fault;
  
  public SOAPFaultException(SOAPFault paramSOAPFault)
  {
    super(paramSOAPFault.getFaultString());
    fault = paramSOAPFault;
  }
  
  public SOAPFault getFault()
  {
    return fault;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\soap\SOAPFaultException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */