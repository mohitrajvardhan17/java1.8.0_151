package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import java.lang.reflect.Method;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

final class SyncMethodHandler
  extends MethodHandler
{
  final boolean isVoid;
  final boolean isOneway;
  final JavaMethodImpl javaMethod;
  
  SyncMethodHandler(SEIStub paramSEIStub, JavaMethodImpl paramJavaMethodImpl)
  {
    super(paramSEIStub, paramJavaMethodImpl.getMethod());
    javaMethod = paramJavaMethodImpl;
    isVoid = Void.TYPE.equals(paramJavaMethodImpl.getMethod().getReturnType());
    isOneway = paramJavaMethodImpl.getMEP().isOneWay();
  }
  
  Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws Throwable
  {
    return invoke(paramObject, paramArrayOfObject, owner.requestContext, owner);
  }
  
  Object invoke(Object paramObject, Object[] paramArrayOfObject, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver)
    throws Throwable
  {
    JavaCallInfo localJavaCallInfo = owner.databinding.createJavaCallInfo(method, paramArrayOfObject);
    Packet localPacket1 = (Packet)owner.databinding.serializeRequest(localJavaCallInfo);
    Packet localPacket2 = owner.doProcess(localPacket1, paramRequestContext, paramResponseContextReceiver);
    Message localMessage = localPacket2.getMessage();
    if (localMessage == null)
    {
      if ((!isOneway) || (!isVoid)) {
        throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
      }
      return null;
    }
    try
    {
      localJavaCallInfo = owner.databinding.deserializeResponse(localPacket2, localJavaCallInfo);
      if (localJavaCallInfo.getException() != null) {
        throw localJavaCallInfo.getException();
      }
      Object localObject1 = localJavaCallInfo.getReturnValue();
      return localObject1;
    }
    catch (JAXBException localJAXBException)
    {
      throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { localJAXBException });
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { localXMLStreamException });
    }
    finally
    {
      if (transportBackChannel != null) {
        transportBackChannel.close();
      }
    }
  }
  
  ValueGetterFactory getValueGetterFactory()
  {
    return ValueGetterFactory.SYNC;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\SyncMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */