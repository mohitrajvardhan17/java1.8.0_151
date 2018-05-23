package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.EndpointCallBridge;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.Packet.State;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam.Mode;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;

public final class TieHandler
  implements EndpointCallBridge
{
  private final SOAPVersion soapVersion;
  private final Method method;
  private final int noOfArgs;
  private final JavaMethodImpl javaMethodModel;
  private final Boolean isOneWay;
  private final EndpointArgumentsBuilder argumentsBuilder;
  private final EndpointResponseMessageBuilder bodyBuilder;
  private final MessageFiller[] outFillers;
  protected MessageContextFactory packetFactory;
  private static final Logger LOGGER = Logger.getLogger(TieHandler.class.getName());
  
  public TieHandler(JavaMethodImpl paramJavaMethodImpl, WSBinding paramWSBinding, MessageContextFactory paramMessageContextFactory)
  {
    soapVersion = paramWSBinding.getSOAPVersion();
    method = paramJavaMethodImpl.getMethod();
    javaMethodModel = paramJavaMethodImpl;
    argumentsBuilder = createArgumentsBuilder();
    ArrayList localArrayList = new ArrayList();
    bodyBuilder = createResponseMessageBuilder(localArrayList);
    outFillers = ((MessageFiller[])localArrayList.toArray(new MessageFiller[localArrayList.size()]));
    isOneWay = Boolean.valueOf(paramJavaMethodImpl.getMEP().isOneWay());
    noOfArgs = method.getParameterTypes().length;
    packetFactory = paramMessageContextFactory;
  }
  
  private EndpointArgumentsBuilder createArgumentsBuilder()
  {
    List localList1 = javaMethodModel.getRequestParameters();
    ArrayList localArrayList = new ArrayList();
    Object localObject2 = localList1.iterator();
    Object localObject4;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (ParameterImpl)((Iterator)localObject2).next();
      localObject4 = EndpointValueSetter.get((ParameterImpl)localObject3);
      switch (getInBindingkind)
      {
      case BODY: 
        if (((ParameterImpl)localObject3).isWrapperStyle())
        {
          if (((ParameterImpl)localObject3).getParent().getBinding().isRpcLit()) {
            localArrayList.add(new EndpointArgumentsBuilder.RpcLit((WrapperParameter)localObject3));
          } else {
            localArrayList.add(new EndpointArgumentsBuilder.DocLit((WrapperParameter)localObject3, WebParam.Mode.OUT));
          }
        }
        else {
          localArrayList.add(new EndpointArgumentsBuilder.Body(((ParameterImpl)localObject3).getXMLBridge(), (EndpointValueSetter)localObject4));
        }
        break;
      case HEADER: 
        localArrayList.add(new EndpointArgumentsBuilder.Header(soapVersion, (ParameterImpl)localObject3, (EndpointValueSetter)localObject4));
        break;
      case ATTACHMENT: 
        localArrayList.add(EndpointArgumentsBuilder.AttachmentBuilder.createAttachmentBuilder((ParameterImpl)localObject3, (EndpointValueSetter)localObject4));
        break;
      case UNBOUND: 
        localArrayList.add(new EndpointArgumentsBuilder.NullSetter((EndpointValueSetter)localObject4, EndpointArgumentsBuilder.getVMUninitializedValue(getTypeInfotype)));
        break;
      default: 
        throw new AssertionError();
      }
    }
    localObject2 = javaMethodModel.getResponseParameters();
    Object localObject3 = ((List)localObject2).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (ParameterImpl)((Iterator)localObject3).next();
      Object localObject5;
      if (((ParameterImpl)localObject4).isWrapperStyle())
      {
        localObject5 = (WrapperParameter)localObject4;
        List localList2 = ((WrapperParameter)localObject5).getWrapperChildren();
        Iterator localIterator = localList2.iterator();
        while (localIterator.hasNext())
        {
          ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
          if ((localParameterImpl.isOUT()) && (localParameterImpl.getIndex() != -1))
          {
            EndpointValueSetter localEndpointValueSetter = EndpointValueSetter.get(localParameterImpl);
            localArrayList.add(new EndpointArgumentsBuilder.NullSetter(localEndpointValueSetter, null));
          }
        }
      }
      else if ((((ParameterImpl)localObject4).isOUT()) && (((ParameterImpl)localObject4).getIndex() != -1))
      {
        localObject5 = EndpointValueSetter.get((ParameterImpl)localObject4);
        localArrayList.add(new EndpointArgumentsBuilder.NullSetter((EndpointValueSetter)localObject5, null));
      }
    }
    Object localObject1;
    switch (localArrayList.size())
    {
    case 0: 
      localObject1 = EndpointArgumentsBuilder.NONE;
      break;
    case 1: 
      localObject1 = (EndpointArgumentsBuilder)localArrayList.get(0);
      break;
    default: 
      localObject1 = new EndpointArgumentsBuilder.Composite(localArrayList);
    }
    return (EndpointArgumentsBuilder)localObject1;
  }
  
  private EndpointResponseMessageBuilder createResponseMessageBuilder(List<MessageFiller> paramList)
  {
    Object localObject = null;
    List localList = javaMethodModel.getResponseParameters();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      ValueGetter localValueGetter = ValueGetter.get(localParameterImpl);
      switch (getOutBindingkind)
      {
      case BODY: 
        if (localParameterImpl.isWrapperStyle())
        {
          if (localParameterImpl.getParent().getBinding().isRpcLit()) {
            localObject = new EndpointResponseMessageBuilder.RpcLit((WrapperParameter)localParameterImpl, soapVersion);
          } else {
            localObject = new EndpointResponseMessageBuilder.DocLit((WrapperParameter)localParameterImpl, soapVersion);
          }
        }
        else {
          localObject = new EndpointResponseMessageBuilder.Bare(localParameterImpl, soapVersion);
        }
        break;
      case HEADER: 
        paramList.add(new MessageFiller.Header(localParameterImpl.getIndex(), localParameterImpl.getXMLBridge(), localValueGetter));
        break;
      case ATTACHMENT: 
        paramList.add(MessageFiller.AttachmentFiller.createAttachmentFiller(localParameterImpl, localValueGetter));
        break;
      case UNBOUND: 
        break;
      default: 
        throw new AssertionError();
      }
    }
    if (localObject == null) {
      switch (soapVersion)
      {
      case SOAP_11: 
        localObject = EndpointResponseMessageBuilder.EMPTY_SOAP11;
        break;
      case SOAP_12: 
        localObject = EndpointResponseMessageBuilder.EMPTY_SOAP12;
        break;
      default: 
        throw new AssertionError();
      }
    }
    return (EndpointResponseMessageBuilder)localObject;
  }
  
  public Object[] readRequest(Message paramMessage)
  {
    Object[] arrayOfObject = new Object[noOfArgs];
    try
    {
      argumentsBuilder.readRequest(paramMessage, arrayOfObject);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
    return arrayOfObject;
  }
  
  public Message createResponse(com.oracle.webservices.internal.api.databinding.JavaCallInfo paramJavaCallInfo)
  {
    Message localMessage;
    if (paramJavaCallInfo.getException() == null)
    {
      localMessage = isOneWay.booleanValue() ? null : createResponseMessage(paramJavaCallInfo.getParameters(), paramJavaCallInfo.getReturnValue());
    }
    else
    {
      Throwable localThrowable1 = paramJavaCallInfo.getException();
      Throwable localThrowable2 = getServiceException(localThrowable1);
      if (((localThrowable1 instanceof InvocationTargetException)) || (localThrowable2 != null))
      {
        if (localThrowable2 != null)
        {
          LOGGER.log(Level.FINE, localThrowable2.getMessage(), localThrowable2);
          localMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, javaMethodModel.getCheckedException(localThrowable2.getClass()), localThrowable2);
        }
        else
        {
          Throwable localThrowable3 = localThrowable1.getCause();
          if ((localThrowable3 instanceof ProtocolException)) {
            LOGGER.log(Level.FINE, localThrowable3.getMessage(), localThrowable3);
          } else {
            LOGGER.log(Level.SEVERE, localThrowable3.getMessage(), localThrowable3);
          }
          localMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, localThrowable3);
        }
      }
      else if ((localThrowable1 instanceof DispatchException))
      {
        localMessage = fault;
      }
      else
      {
        LOGGER.log(Level.SEVERE, localThrowable1.getMessage(), localThrowable1);
        localMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, localThrowable1);
      }
    }
    return localMessage;
  }
  
  Throwable getServiceException(Throwable paramThrowable)
  {
    if (javaMethodModel.getCheckedException(paramThrowable.getClass()) != null) {
      return paramThrowable;
    }
    if (paramThrowable.getCause() != null)
    {
      Throwable localThrowable = paramThrowable.getCause();
      if (javaMethodModel.getCheckedException(localThrowable.getClass()) != null) {
        return localThrowable;
      }
    }
    return null;
  }
  
  private Message createResponseMessage(Object[] paramArrayOfObject, Object paramObject)
  {
    Message localMessage = bodyBuilder.createMessage(paramArrayOfObject, paramObject);
    for (MessageFiller localMessageFiller : outFillers) {
      localMessageFiller.fillIn(paramArrayOfObject, paramObject, localMessage);
    }
    return localMessage;
  }
  
  public Method getMethod()
  {
    return method;
  }
  
  public com.oracle.webservices.internal.api.databinding.JavaCallInfo deserializeRequest(Packet paramPacket)
  {
    com.sun.xml.internal.ws.api.databinding.JavaCallInfo localJavaCallInfo = new com.sun.xml.internal.ws.api.databinding.JavaCallInfo();
    localJavaCallInfo.setMethod(getMethod());
    Object[] arrayOfObject = readRequest(paramPacket.getMessage());
    localJavaCallInfo.setParameters(arrayOfObject);
    return localJavaCallInfo;
  }
  
  public Packet serializeResponse(com.oracle.webservices.internal.api.databinding.JavaCallInfo paramJavaCallInfo)
  {
    Message localMessage = createResponse(paramJavaCallInfo);
    Packet localPacket = localMessage == null ? (Packet)packetFactory.createContext() : (Packet)packetFactory.createContext(localMessage);
    localPacket.setState(Packet.State.ServerResponse);
    return localPacket;
  }
  
  public JavaMethod getOperationModel()
  {
    return javaMethodModel;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\TieHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */