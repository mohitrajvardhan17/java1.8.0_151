package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;

public abstract interface SOAPMessageContext
  extends MessageContext
{
  public abstract SOAPMessage getMessage();
  
  public abstract void setMessage(SOAPMessage paramSOAPMessage);
  
  public abstract Object[] getHeaders(QName paramQName, JAXBContext paramJAXBContext, boolean paramBoolean);
  
  public abstract Set<String> getRoles();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\handler\soap\SOAPMessageContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */