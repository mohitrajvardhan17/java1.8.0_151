package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageContextImpl
  extends MessageUpdatableContext
  implements SOAPMessageContext
{
  private Set<String> roles;
  private SOAPMessage soapMsg = null;
  private WSBinding binding;
  
  public SOAPMessageContextImpl(WSBinding paramWSBinding, Packet paramPacket, Set<String> paramSet)
  {
    super(paramPacket);
    binding = paramWSBinding;
    roles = paramSet;
  }
  
  public SOAPMessage getMessage()
  {
    if (soapMsg == null) {
      try
      {
        Message localMessage = packet.getMessage();
        soapMsg = (localMessage != null ? localMessage.readAsSOAPMessage() : null);
      }
      catch (SOAPException localSOAPException)
      {
        throw new WebServiceException(localSOAPException);
      }
    }
    return soapMsg;
  }
  
  public void setMessage(SOAPMessage paramSOAPMessage)
  {
    try
    {
      soapMsg = paramSOAPMessage;
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  void setPacketMessage(Message paramMessage)
  {
    if (paramMessage != null)
    {
      packet.setMessage(paramMessage);
      soapMsg = null;
    }
  }
  
  protected void updateMessage()
  {
    if (soapMsg != null)
    {
      packet.setMessage(SAAJFactory.create(soapMsg));
      soapMsg = null;
    }
  }
  
  public Object[] getHeaders(QName paramQName, JAXBContext paramJAXBContext, boolean paramBoolean)
  {
    SOAPVersion localSOAPVersion = binding.getSOAPVersion();
    ArrayList localArrayList = new ArrayList();
    try
    {
      Iterator localIterator = packet.getMessage().getHeaders().getHeaders(paramQName, false);
      if (paramBoolean) {
        while (localIterator.hasNext()) {
          localArrayList.add(((Header)localIterator.next()).readAsJAXB(paramJAXBContext.createUnmarshaller()));
        }
      }
      while (localIterator.hasNext())
      {
        Header localHeader = (Header)localIterator.next();
        String str = localHeader.getRole(localSOAPVersion);
        if (getRoles().contains(str)) {
          localArrayList.add(localHeader.readAsJAXB(paramJAXBContext.createUnmarshaller()));
        }
      }
      return localArrayList.toArray();
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  public Set<String> getRoles()
  {
    return roles;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\SOAPMessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */