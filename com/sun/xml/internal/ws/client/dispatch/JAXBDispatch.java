package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.message.jaxb.JAXBDispatchMessage;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;

public class JAXBDispatch
  extends DispatchImpl<Object>
{
  private final JAXBContext jaxbcontext;
  private final boolean isContextSupported;
  
  @Deprecated
  public JAXBDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
    jaxbcontext = paramJAXBContext;
    isContextSupported = BindingContextFactory.isContextSupported(paramJAXBContext);
  }
  
  public JAXBDispatch(WSPortInfo paramWSPortInfo, JAXBContext paramJAXBContext, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    super(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
    jaxbcontext = paramJAXBContext;
    isContextSupported = BindingContextFactory.isContextSupported(paramJAXBContext);
  }
  
  Object toReturnValue(Packet paramPacket)
  {
    try
    {
      Unmarshaller localUnmarshaller = jaxbcontext.createUnmarshaller();
      Message localMessage = paramPacket.getMessage();
      switch (mode)
      {
      case PAYLOAD: 
        return localMessage.readPayloadAsJAXB(localUnmarshaller);
      case MESSAGE: 
        Source localSource = localMessage.readEnvelopeAsSource();
        return localUnmarshaller.unmarshal(localSource);
      }
      throw new WebServiceException("Unrecognized dispatch mode");
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
  
  Packet createPacket(Object paramObject)
  {
    assert (jaxbcontext != null);
    Object localObject;
    if (mode == Service.Mode.MESSAGE) {
      localObject = isContextSupported ? new JAXBDispatchMessage(BindingContextFactory.create(jaxbcontext), paramObject, soapVersion) : new JAXBDispatchMessage(jaxbcontext, paramObject, soapVersion);
    } else if (paramObject == null) {
      localObject = Messages.createEmpty(soapVersion);
    } else {
      localObject = isContextSupported ? Messages.create(jaxbcontext, paramObject, soapVersion) : Messages.createRaw(jaxbcontext, paramObject, soapVersion);
    }
    return new Packet((Message)localObject);
  }
  
  public void setOutboundHeaders(Object... paramVarArgs)
  {
    if (paramVarArgs == null) {
      throw new IllegalArgumentException();
    }
    Header[] arrayOfHeader = new Header[paramVarArgs.length];
    for (int i = 0; i < arrayOfHeader.length; i++)
    {
      if (paramVarArgs[i] == null) {
        throw new IllegalArgumentException();
      }
      arrayOfHeader[i] = Headers.create((JAXBRIContext)jaxbcontext, paramVarArgs[i]);
    }
    super.setOutboundHeaders(arrayOfHeader);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\dispatch\JAXBDispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */