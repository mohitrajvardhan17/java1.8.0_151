package com.sun.xml.internal.ws.fault;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

public class ServerSOAPFaultException
  extends SOAPFaultException
{
  public ServerSOAPFaultException(SOAPFault paramSOAPFault)
  {
    super(paramSOAPFault);
  }
  
  public String getMessage()
  {
    return "Client received SOAP Fault from server: " + super.getMessage() + " Please see the server log to find more detail regarding exact cause of the failure.";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\fault\ServerSOAPFaultException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */