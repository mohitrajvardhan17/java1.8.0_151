package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.databinding.ClientCallBridge;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.Packet.State;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public class StubHandler
  implements ClientCallBridge
{
  private final BodyBuilder bodyBuilder;
  private final MessageFiller[] inFillers;
  protected final String soapAction;
  protected final boolean isOneWay;
  protected final JavaMethodImpl javaMethod;
  protected final Map<QName, CheckedExceptionImpl> checkedExceptions = new HashMap();
  protected SOAPVersion soapVersion = SOAPVersion.SOAP_11;
  protected ResponseBuilder responseBuilder;
  protected MessageContextFactory packetFactory;
  
  public StubHandler(JavaMethodImpl paramJavaMethodImpl, MessageContextFactory paramMessageContextFactory)
  {
    Object localObject1 = paramJavaMethodImpl.getCheckedExceptions().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (CheckedExceptionImpl)((Iterator)localObject1).next();
      checkedExceptions.put(getBondgetTypeInfotagName, localObject2);
    }
    localObject1 = paramJavaMethodImpl.getBinding().getSOAPAction();
    if ((paramJavaMethodImpl.getInputAction() != null) && (localObject1 != null) && (!((String)localObject1).equals(""))) {
      soapAction = paramJavaMethodImpl.getInputAction();
    } else {
      soapAction = ((String)localObject1);
    }
    javaMethod = paramJavaMethodImpl;
    packetFactory = paramMessageContextFactory;
    soapVersion = javaMethod.getBinding().getSOAPVersion();
    Object localObject2 = paramJavaMethodImpl.getRequestParameters();
    Object localObject3 = null;
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = ((List)localObject2).iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      ValueGetter localValueGetter = getValueGetterFactory().get(localParameterImpl);
      switch (getInBindingkind)
      {
      case BODY: 
        if (localParameterImpl.isWrapperStyle())
        {
          if (localParameterImpl.getParent().getBinding().isRpcLit()) {
            localObject3 = new BodyBuilder.RpcLit((WrapperParameter)localParameterImpl, soapVersion, getValueGetterFactory());
          } else {
            localObject3 = new BodyBuilder.DocLit((WrapperParameter)localParameterImpl, soapVersion, getValueGetterFactory());
          }
        }
        else {
          localObject3 = new BodyBuilder.Bare(localParameterImpl, soapVersion, localValueGetter);
        }
        break;
      case HEADER: 
        localArrayList.add(new MessageFiller.Header(localParameterImpl.getIndex(), localParameterImpl.getXMLBridge(), localValueGetter));
        break;
      case ATTACHMENT: 
        localArrayList.add(MessageFiller.AttachmentFiller.createAttachmentFiller(localParameterImpl, localValueGetter));
        break;
      case UNBOUND: 
        break;
      default: 
        throw new AssertionError();
      }
    }
    if (localObject3 == null) {
      switch (soapVersion)
      {
      case SOAP_11: 
        localObject3 = BodyBuilder.EMPTY_SOAP11;
        break;
      case SOAP_12: 
        localObject3 = BodyBuilder.EMPTY_SOAP12;
        break;
      default: 
        throw new AssertionError();
      }
    }
    bodyBuilder = ((BodyBuilder)localObject3);
    inFillers = ((MessageFiller[])localArrayList.toArray(new MessageFiller[localArrayList.size()]));
    isOneWay = paramJavaMethodImpl.getMEP().isOneWay();
    responseBuilder = buildResponseBuilder(paramJavaMethodImpl, ValueSetterFactory.SYNC);
  }
  
  ResponseBuilder buildResponseBuilder(JavaMethodImpl paramJavaMethodImpl, ValueSetterFactory paramValueSetterFactory)
  {
    List localList = paramJavaMethodImpl.getResponseParameters();
    ArrayList localArrayList = new ArrayList();
    Object localObject = localList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)((Iterator)localObject).next();
      ValueSetter localValueSetter;
      switch (getOutBindingkind)
      {
      case BODY: 
        if (localParameterImpl.isWrapperStyle())
        {
          if (localParameterImpl.getParent().getBinding().isRpcLit()) {
            localArrayList.add(new ResponseBuilder.RpcLit((WrapperParameter)localParameterImpl, paramValueSetterFactory));
          } else {
            localArrayList.add(new ResponseBuilder.DocLit((WrapperParameter)localParameterImpl, paramValueSetterFactory));
          }
        }
        else
        {
          localValueSetter = paramValueSetterFactory.get(localParameterImpl);
          localArrayList.add(new ResponseBuilder.Body(localParameterImpl.getXMLBridge(), localValueSetter));
        }
        break;
      case HEADER: 
        localValueSetter = paramValueSetterFactory.get(localParameterImpl);
        localArrayList.add(new ResponseBuilder.Header(soapVersion, localParameterImpl, localValueSetter));
        break;
      case ATTACHMENT: 
        localValueSetter = paramValueSetterFactory.get(localParameterImpl);
        localArrayList.add(ResponseBuilder.AttachmentBuilder.createAttachmentBuilder(localParameterImpl, localValueSetter));
        break;
      case UNBOUND: 
        localValueSetter = paramValueSetterFactory.get(localParameterImpl);
        localArrayList.add(new ResponseBuilder.NullSetter(localValueSetter, ResponseBuilder.getVMUninitializedValue(getTypeInfotype)));
        break;
      default: 
        throw new AssertionError();
      }
    }
    switch (localArrayList.size())
    {
    case 0: 
      localObject = ResponseBuilder.NONE;
      break;
    case 1: 
      localObject = (ResponseBuilder)localArrayList.get(0);
      break;
    default: 
      localObject = new ResponseBuilder.Composite(localArrayList);
    }
    return (ResponseBuilder)localObject;
  }
  
  public Packet createRequestPacket(JavaCallInfo paramJavaCallInfo)
  {
    Message localMessage = bodyBuilder.createMessage(paramJavaCallInfo.getParameters());
    for (Object localObject2 : inFillers) {
      ((MessageFiller)localObject2).fillIn(paramJavaCallInfo.getParameters(), localMessage);
    }
    ??? = (Packet)packetFactory.createContext(localMessage);
    ((Packet)???).setState(Packet.State.ClientRequest);
    soapAction = soapAction;
    expectReply = Boolean.valueOf(!isOneWay);
    ((Packet)???).getMessage().assertOneWay(isOneWay);
    ((Packet)???).setWSDLOperation(getOperationName());
    return (Packet)???;
  }
  
  ValueGetterFactory getValueGetterFactory()
  {
    return ValueGetterFactory.SYNC;
  }
  
  public JavaCallInfo readResponse(Packet paramPacket, JavaCallInfo paramJavaCallInfo)
    throws Throwable
  {
    Message localMessage = paramPacket.getMessage();
    if (localMessage.isFault())
    {
      localObject = SOAPFaultBuilder.create(localMessage);
      Throwable localThrowable = ((SOAPFaultBuilder)localObject).createException(checkedExceptions);
      paramJavaCallInfo.setException(localThrowable);
      throw localThrowable;
    }
    initArgs(paramJavaCallInfo.getParameters());
    Object localObject = responseBuilder.readResponse(localMessage, paramJavaCallInfo.getParameters());
    paramJavaCallInfo.setReturnValue(localObject);
    return paramJavaCallInfo;
  }
  
  public QName getOperationName()
  {
    return javaMethod.getOperationQName();
  }
  
  public String getSoapAction()
  {
    return soapAction;
  }
  
  public boolean isOneWay()
  {
    return isOneWay;
  }
  
  protected void initArgs(Object[] paramArrayOfObject)
    throws Exception
  {}
  
  public Method getMethod()
  {
    return javaMethod.getMethod();
  }
  
  public JavaMethod getOperationModel()
  {
    return javaMethod;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\StubHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */