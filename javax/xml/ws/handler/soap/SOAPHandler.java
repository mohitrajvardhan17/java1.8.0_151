package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public abstract interface SOAPHandler<T extends SOAPMessageContext>
  extends Handler<T>
{
  public abstract Set<QName> getHeaders();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\handler\soap\SOAPHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */