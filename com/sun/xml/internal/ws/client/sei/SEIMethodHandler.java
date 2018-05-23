package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

abstract class SEIMethodHandler
  extends MethodHandler
{
  private BodyBuilder bodyBuilder;
  private MessageFiller[] inFillers;
  protected String soapAction;
  protected boolean isOneWay;
  protected JavaMethodImpl javaMethod;
  protected Map<QName, CheckedExceptionImpl> checkedExceptions;
  
  SEIMethodHandler(SEIStub paramSEIStub)
  {
    super(paramSEIStub, null);
  }
  
  SEIMethodHandler(SEIStub paramSEIStub, JavaMethodImpl paramJavaMethodImpl)
  {
    super(paramSEIStub, null);
    checkedExceptions = new HashMap();
    Object localObject1 = paramJavaMethodImpl.getCheckedExceptions().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (CheckedExceptionImpl)((Iterator)localObject1).next();
      checkedExceptions.put(getBondgetTypeInfotagName, localObject2);
    }
    if ((paramJavaMethodImpl.getInputAction() != null) && (!paramJavaMethodImpl.getBinding().getSOAPAction().equals(""))) {
      soapAction = paramJavaMethodImpl.getInputAction();
    } else {
      soapAction = paramJavaMethodImpl.getBinding().getSOAPAction();
    }
    javaMethod = paramJavaMethodImpl;
    localObject1 = paramJavaMethodImpl.getRequestParameters();
    Object localObject2 = null;
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = ((List)localObject1).iterator();
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
            localObject2 = new BodyBuilder.RpcLit((WrapperParameter)localParameterImpl, soapVersion, getValueGetterFactory());
          } else {
            localObject2 = new BodyBuilder.DocLit((WrapperParameter)localParameterImpl, soapVersion, getValueGetterFactory());
          }
        }
        else {
          localObject2 = new BodyBuilder.Bare(localParameterImpl, soapVersion, localValueGetter);
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
    if (localObject2 == null) {
      switch (soapVersion)
      {
      case SOAP_11: 
        localObject2 = BodyBuilder.EMPTY_SOAP11;
        break;
      case SOAP_12: 
        localObject2 = BodyBuilder.EMPTY_SOAP12;
        break;
      default: 
        throw new AssertionError();
      }
    }
    bodyBuilder = ((BodyBuilder)localObject2);
    inFillers = ((MessageFiller[])localArrayList.toArray(new MessageFiller[localArrayList.size()]));
    isOneWay = paramJavaMethodImpl.getMEP().isOneWay();
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
        localArrayList.add(new ResponseBuilder.Header(owner.soapVersion, localParameterImpl, localValueSetter));
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
  
  Message createRequestMessage(Object[] paramArrayOfObject)
  {
    Message localMessage = bodyBuilder.createMessage(paramArrayOfObject);
    for (MessageFiller localMessageFiller : inFillers) {
      localMessageFiller.fillIn(paramArrayOfObject, localMessage);
    }
    return localMessage;
  }
  
  abstract ValueGetterFactory getValueGetterFactory();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\SEIMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */