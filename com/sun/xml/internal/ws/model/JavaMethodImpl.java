package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceException;

public final class JavaMethodImpl
  implements JavaMethod
{
  private String inputAction = "";
  private String outputAction = "";
  private final List<CheckedExceptionImpl> exceptions = new ArrayList();
  private final Method method;
  final List<ParameterImpl> requestParams = new ArrayList();
  final List<ParameterImpl> responseParams = new ArrayList();
  private final List<ParameterImpl> unmReqParams = Collections.unmodifiableList(requestParams);
  private final List<ParameterImpl> unmResParams = Collections.unmodifiableList(responseParams);
  private SOAPBinding binding;
  private MEP mep;
  private QName operationName;
  private WSDLBoundOperation wsdlOperation;
  final AbstractSEIModelImpl owner;
  private final Method seiMethod;
  private QName requestPayloadName;
  private String soapAction;
  private static final Logger LOGGER = Logger.getLogger(JavaMethodImpl.class.getName());
  
  public JavaMethodImpl(AbstractSEIModelImpl paramAbstractSEIModelImpl, Method paramMethod1, Method paramMethod2, MetadataReader paramMetadataReader)
  {
    owner = paramAbstractSEIModelImpl;
    method = paramMethod1;
    seiMethod = paramMethod2;
    setWsaActions(paramMetadataReader);
  }
  
  private void setWsaActions(MetadataReader paramMetadataReader)
  {
    Action localAction = paramMetadataReader != null ? (Action)paramMetadataReader.getAnnotation(Action.class, seiMethod) : (Action)seiMethod.getAnnotation(Action.class);
    if (localAction != null)
    {
      inputAction = localAction.input();
      outputAction = localAction.output();
    }
    WebMethod localWebMethod = paramMetadataReader != null ? (WebMethod)paramMetadataReader.getAnnotation(WebMethod.class, seiMethod) : (WebMethod)seiMethod.getAnnotation(WebMethod.class);
    soapAction = "";
    if (localWebMethod != null) {
      soapAction = localWebMethod.action();
    }
    if (!soapAction.equals("")) {
      if (inputAction.equals("")) {
        inputAction = soapAction;
      } else if (inputAction.equals(soapAction)) {}
    }
  }
  
  public ActionBasedOperationSignature getOperationSignature()
  {
    QName localQName = getRequestPayloadName();
    if (localQName == null) {
      localQName = new QName("", "");
    }
    return new ActionBasedOperationSignature(getInputAction(), localQName);
  }
  
  public SEIModel getOwner()
  {
    return owner;
  }
  
  public Method getMethod()
  {
    return method;
  }
  
  public Method getSEIMethod()
  {
    return seiMethod;
  }
  
  public MEP getMEP()
  {
    return mep;
  }
  
  void setMEP(MEP paramMEP)
  {
    mep = paramMEP;
  }
  
  public SOAPBinding getBinding()
  {
    if (binding == null) {
      return new SOAPBindingImpl();
    }
    return binding;
  }
  
  void setBinding(SOAPBinding paramSOAPBinding)
  {
    binding = paramSOAPBinding;
  }
  
  /**
   * @deprecated
   */
  public WSDLBoundOperation getOperation()
  {
    return wsdlOperation;
  }
  
  public void setOperationQName(QName paramQName)
  {
    operationName = paramQName;
  }
  
  public QName getOperationQName()
  {
    return wsdlOperation != null ? wsdlOperation.getName() : operationName;
  }
  
  public String getSOAPAction()
  {
    return wsdlOperation != null ? wsdlOperation.getSOAPAction() : soapAction;
  }
  
  public String getOperationName()
  {
    return operationName.getLocalPart();
  }
  
  public String getRequestMessageName()
  {
    return getOperationName();
  }
  
  public String getResponseMessageName()
  {
    if (mep.isOneWay()) {
      return null;
    }
    return getOperationName() + "Response";
  }
  
  public void setRequestPayloadName(QName paramQName)
  {
    requestPayloadName = paramQName;
  }
  
  @Nullable
  public QName getRequestPayloadName()
  {
    return wsdlOperation != null ? wsdlOperation.getRequestPayloadName() : requestPayloadName;
  }
  
  @Nullable
  public QName getResponsePayloadName()
  {
    return mep == MEP.ONE_WAY ? null : wsdlOperation.getResponsePayloadName();
  }
  
  public List<ParameterImpl> getRequestParameters()
  {
    return unmReqParams;
  }
  
  public List<ParameterImpl> getResponseParameters()
  {
    return unmResParams;
  }
  
  void addParameter(ParameterImpl paramParameterImpl)
  {
    if ((paramParameterImpl.isIN()) || (paramParameterImpl.isINOUT()))
    {
      assert (!requestParams.contains(paramParameterImpl));
      requestParams.add(paramParameterImpl);
    }
    if ((paramParameterImpl.isOUT()) || (paramParameterImpl.isINOUT()))
    {
      assert (!responseParams.contains(paramParameterImpl));
      responseParams.add(paramParameterImpl);
    }
  }
  
  void addRequestParameter(ParameterImpl paramParameterImpl)
  {
    if ((paramParameterImpl.isIN()) || (paramParameterImpl.isINOUT())) {
      requestParams.add(paramParameterImpl);
    }
  }
  
  void addResponseParameter(ParameterImpl paramParameterImpl)
  {
    if ((paramParameterImpl.isOUT()) || (paramParameterImpl.isINOUT())) {
      responseParams.add(paramParameterImpl);
    }
  }
  
  /**
   * @deprecated
   */
  public int getInputParametersCount()
  {
    int i = 0;
    Iterator localIterator1 = requestParams.iterator();
    ParameterImpl localParameterImpl1;
    while (localIterator1.hasNext())
    {
      localParameterImpl1 = (ParameterImpl)localIterator1.next();
      if (localParameterImpl1.isWrapperStyle()) {
        i += ((WrapperParameter)localParameterImpl1).getWrapperChildren().size();
      } else {
        i++;
      }
    }
    localIterator1 = responseParams.iterator();
    while (localIterator1.hasNext())
    {
      localParameterImpl1 = (ParameterImpl)localIterator1.next();
      if (localParameterImpl1.isWrapperStyle())
      {
        Iterator localIterator2 = ((WrapperParameter)localParameterImpl1).getWrapperChildren().iterator();
        while (localIterator2.hasNext())
        {
          ParameterImpl localParameterImpl2 = (ParameterImpl)localIterator2.next();
          if ((!localParameterImpl2.isResponse()) && (localParameterImpl2.isOUT())) {
            i++;
          }
        }
      }
      else if ((!localParameterImpl1.isResponse()) && (localParameterImpl1.isOUT()))
      {
        i++;
      }
    }
    return i;
  }
  
  void addException(CheckedExceptionImpl paramCheckedExceptionImpl)
  {
    if (!exceptions.contains(paramCheckedExceptionImpl)) {
      exceptions.add(paramCheckedExceptionImpl);
    }
  }
  
  public CheckedExceptionImpl getCheckedException(Class paramClass)
  {
    Iterator localIterator = exceptions.iterator();
    while (localIterator.hasNext())
    {
      CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator.next();
      if (localCheckedExceptionImpl.getExceptionClass() == paramClass) {
        return localCheckedExceptionImpl;
      }
    }
    return null;
  }
  
  public List<CheckedExceptionImpl> getCheckedExceptions()
  {
    return Collections.unmodifiableList(exceptions);
  }
  
  public String getInputAction()
  {
    return inputAction;
  }
  
  public String getOutputAction()
  {
    return outputAction;
  }
  
  /**
   * @deprecated
   */
  public CheckedExceptionImpl getCheckedException(TypeReference paramTypeReference)
  {
    Iterator localIterator = exceptions.iterator();
    while (localIterator.hasNext())
    {
      CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator.next();
      TypeInfo localTypeInfo = localCheckedExceptionImpl.getDetailType();
      if ((tagName.equals(tagName)) && (type == type)) {
        return localCheckedExceptionImpl;
      }
    }
    return null;
  }
  
  public boolean isAsync()
  {
    return mep.isAsync;
  }
  
  void freeze(WSDLPort paramWSDLPort)
  {
    wsdlOperation = paramWSDLPort.getBinding().get(new QName(paramWSDLPort.getBinding().getPortType().getName().getNamespaceURI(), getOperationName()));
    if (wsdlOperation == null) {
      throw new WebServiceException("Method " + seiMethod.getName() + " is exposed as WebMethod, but there is no corresponding wsdl operation with name " + operationName + " in the wsdl:portType" + paramWSDLPort.getBinding().getPortType().getName());
    }
    if (inputAction.equals("")) {
      inputAction = wsdlOperation.getOperation().getInput().getAction();
    } else if (!inputAction.equals(wsdlOperation.getOperation().getInput().getAction())) {
      LOGGER.warning("Input Action on WSDL operation " + wsdlOperation.getName().getLocalPart() + " and @Action on its associated Web Method " + seiMethod.getName() + " did not match and will cause problems in dispatching the requests");
    }
    if (!mep.isOneWay())
    {
      if (outputAction.equals("")) {
        outputAction = wsdlOperation.getOperation().getOutput().getAction();
      }
      Iterator localIterator = exceptions.iterator();
      while (localIterator.hasNext())
      {
        CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator.next();
        if (localCheckedExceptionImpl.getFaultAction().equals(""))
        {
          QName localQName = getDetailTypetagName;
          WSDLFault localWSDLFault = wsdlOperation.getOperation().getFault(localQName);
          if (localWSDLFault == null)
          {
            LOGGER.warning("Mismatch between Java model and WSDL model found, For wsdl operation " + wsdlOperation.getName() + ",There is no matching wsdl fault with detail QName " + getDetailTypetagName);
            localCheckedExceptionImpl.setFaultAction(localCheckedExceptionImpl.getDefaultFaultAction());
          }
          else
          {
            localCheckedExceptionImpl.setFaultAction(localWSDLFault.getAction());
          }
        }
      }
    }
  }
  
  final void fillTypes(List<TypeInfo> paramList)
  {
    fillTypes(requestParams, paramList);
    fillTypes(responseParams, paramList);
    Iterator localIterator = exceptions.iterator();
    while (localIterator.hasNext())
    {
      CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator.next();
      paramList.add(localCheckedExceptionImpl.getDetailType());
    }
  }
  
  private void fillTypes(List<ParameterImpl> paramList, List<TypeInfo> paramList1)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      localParameterImpl.fillTypes(paramList1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\JavaMethodImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */