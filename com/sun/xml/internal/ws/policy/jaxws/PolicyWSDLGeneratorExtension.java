package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.addressing.policy.AddressingPolicyMapConfigurator;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.policy.ModelGenerator;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ServerContext;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.encoding.policy.MtomPolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapUtil;
import com.sun.xml.internal.ws.policy.PolicyMerger;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject.WsdlMessageType;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class PolicyWSDLGeneratorExtension
  extends WSDLGeneratorExtension
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLGeneratorExtension.class);
  private PolicyMap policyMap;
  private SEIModel seiModel;
  private final Collection<PolicySubject> subjects = new LinkedList();
  private final PolicyModelMarshaller marshaller = PolicyModelMarshaller.getXmlMarshaller(true);
  private final PolicyMerger merger = PolicyMerger.getMerger();
  
  public PolicyWSDLGeneratorExtension() {}
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext)
  {
    LOGGER.entering();
    try
    {
      seiModel = paramWSDLGenExtnContext.getModel();
      PolicyMapConfigurator[] arrayOfPolicyMapConfigurator = loadConfigurators();
      PolicyMapExtender[] arrayOfPolicyMapExtender = new PolicyMapExtender[arrayOfPolicyMapConfigurator.length];
      for (int i = 0; i < arrayOfPolicyMapConfigurator.length; i++) {
        arrayOfPolicyMapExtender[i] = PolicyMapExtender.createPolicyMapExtender();
      }
      policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(policyMap, paramWSDLGenExtnContext.getContainer(), paramWSDLGenExtnContext.getEndpointClass(), false, arrayOfPolicyMapExtender));
      if (policyMap == null)
      {
        LOGGER.fine(PolicyMessages.WSP_1019_CREATE_EMPTY_POLICY_MAP());
        policyMap = PolicyMap.createPolicyMap(Arrays.asList(arrayOfPolicyMapExtender));
      }
      WSBinding localWSBinding = paramWSDLGenExtnContext.getBinding();
      try
      {
        LinkedList localLinkedList = new LinkedList();
        for (int j = 0; j < arrayOfPolicyMapConfigurator.length; j++)
        {
          localLinkedList.addAll(arrayOfPolicyMapConfigurator[j].update(policyMap, seiModel, localWSBinding));
          arrayOfPolicyMapExtender[j].disconnect();
        }
        PolicyMapUtil.insertPolicies(policyMap, localLinkedList, seiModel.getServiceQName(), seiModel.getPortName());
      }
      catch (PolicyException localPolicyException)
      {
        throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1017_MAP_UPDATE_FAILED(), localPolicyException)));
      }
      TypedXmlWriter localTypedXmlWriter = paramWSDLGenExtnContext.getRoot();
      localTypedXmlWriter._namespace(NamespaceVersion.v1_2.toString(), NamespaceVersion.v1_2.getDefaultNamespacePrefix());
      localTypedXmlWriter._namespace(NamespaceVersion.v1_5.toString(), NamespaceVersion.v1_5.getDefaultNamespacePrefix());
      localTypedXmlWriter._namespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
    }
    finally
    {
      LOGGER.exiting();
    }
  }
  
  public void addDefinitionsExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    try
    {
      LOGGER.entering();
      if (policyMap == null)
      {
        LOGGER.fine(PolicyMessages.WSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL());
      }
      else
      {
        subjects.addAll(policyMap.getPolicySubjects());
        PolicyModelGenerator localPolicyModelGenerator = ModelGenerator.getGenerator();
        HashSet localHashSet = new HashSet();
        Iterator localIterator = subjects.iterator();
        while (localIterator.hasNext())
        {
          PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
          if (localPolicySubject.getSubject() == null)
          {
            LOGGER.fine(PolicyMessages.WSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(localPolicySubject));
          }
          else
          {
            Policy localPolicy;
            try
            {
              localPolicy = localPolicySubject.getEffectivePolicy(merger);
            }
            catch (PolicyException localPolicyException1)
            {
              throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(localPolicySubject.toString()), localPolicyException1)));
            }
            if ((null == localPolicy.getIdOrName()) || (localHashSet.contains(localPolicy.getIdOrName())))
            {
              LOGGER.fine(PolicyMessages.WSP_1016_POLICY_ID_NULL_OR_DUPLICATE(localPolicy));
            }
            else
            {
              try
              {
                PolicySourceModel localPolicySourceModel = localPolicyModelGenerator.translate(localPolicy);
                marshaller.marshal(localPolicySourceModel, paramTypedXmlWriter);
              }
              catch (PolicyException localPolicyException2)
              {
                throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1018_FAILED_TO_MARSHALL_POLICY(localPolicy.getIdOrName()), localPolicyException2)));
              }
              localHashSet.add(localPolicy.getIdOrName());
            }
          }
        }
      }
    }
    finally
    {
      LOGGER.exiting();
    }
  }
  
  public void addServiceExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    LOGGER.entering();
    String str = null == seiModel ? null : seiModel.getServiceQName().getLocalPart();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLService.class, ScopeType.SERVICE, str);
    LOGGER.exiting();
  }
  
  public void addPortExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    LOGGER.entering();
    String str = null == seiModel ? null : seiModel.getPortName().getLocalPart();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLPort.class, ScopeType.ENDPOINT, str);
    LOGGER.exiting();
  }
  
  public void addPortTypeExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    LOGGER.entering();
    String str = null == seiModel ? null : seiModel.getPortTypeName().getLocalPart();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLPortType.class, ScopeType.ENDPOINT, str);
    LOGGER.exiting();
  }
  
  public void addBindingExtension(TypedXmlWriter paramTypedXmlWriter)
  {
    LOGGER.entering();
    QName localQName = null == seiModel ? null : seiModel.getBoundPortTypeName();
    selectAndProcessBindingSubject(paramTypedXmlWriter, WSDLBoundPortType.class, ScopeType.ENDPOINT, localQName);
    LOGGER.exiting();
  }
  
  public void addOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLOperation.class, ScopeType.OPERATION, (String)null);
    LOGGER.exiting();
  }
  
  public void addBindingOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    QName localQName = paramJavaMethod == null ? null : new QName(paramJavaMethod.getOwner().getTargetNamespace(), paramJavaMethod.getOperationName());
    selectAndProcessBindingSubject(paramTypedXmlWriter, WSDLBoundOperation.class, ScopeType.OPERATION, localQName);
    LOGGER.exiting();
  }
  
  public void addInputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    String str = null == paramJavaMethod ? null : paramJavaMethod.getRequestMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLMessage.class, ScopeType.INPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOutputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    String str = null == paramJavaMethod ? null : paramJavaMethod.getResponseMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLMessage.class, ScopeType.OUTPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addFaultMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException)
  {
    LOGGER.entering();
    String str = null == paramCheckedException ? null : paramCheckedException.getMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLMessage.class, ScopeType.FAULT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    String str = null == paramJavaMethod ? null : paramJavaMethod.getRequestMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLInput.class, ScopeType.INPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    String str = null == paramJavaMethod ? null : paramJavaMethod.getResponseMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLOutput.class, ScopeType.OUTPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException)
  {
    LOGGER.entering();
    String str = null == paramCheckedException ? null : paramCheckedException.getMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, WSDLFault.class, ScopeType.FAULT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addBindingOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    QName localQName = new QName(paramJavaMethod.getOwner().getTargetNamespace(), paramJavaMethod.getOperationName());
    selectAndProcessBindingSubject(paramTypedXmlWriter, WSDLBoundOperation.class, ScopeType.INPUT_MESSAGE, localQName);
    LOGGER.exiting();
  }
  
  public void addBindingOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod)
  {
    LOGGER.entering();
    QName localQName = new QName(paramJavaMethod.getOwner().getTargetNamespace(), paramJavaMethod.getOperationName());
    selectAndProcessBindingSubject(paramTypedXmlWriter, WSDLBoundOperation.class, ScopeType.OUTPUT_MESSAGE, localQName);
    LOGGER.exiting();
  }
  
  public void addBindingOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException)
  {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramJavaMethod, paramCheckedException });
    if (subjects != null)
    {
      Iterator localIterator = subjects.iterator();
      while (localIterator.hasNext())
      {
        PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
        if (policyMap.isFaultMessageSubject(localPolicySubject))
        {
          Object localObject1 = localPolicySubject.getSubject();
          if (localObject1 != null)
          {
            String str = paramCheckedException == null ? null : paramCheckedException.getMessageName();
            if (str == null) {
              writePolicyOrReferenceIt(localPolicySubject, paramTypedXmlWriter);
            }
            Object localObject2;
            if (WSDLBoundFaultContainer.class.isInstance(localObject1))
            {
              localObject2 = (WSDLBoundFaultContainer)localObject1;
              WSDLBoundFault localWSDLBoundFault = ((WSDLBoundFaultContainer)localObject2).getBoundFault();
              WSDLBoundOperation localWSDLBoundOperation = ((WSDLBoundFaultContainer)localObject2).getBoundOperation();
              if ((str.equals(localWSDLBoundFault.getName())) && (localWSDLBoundOperation.getName().getLocalPart().equals(paramJavaMethod.getOperationName()))) {
                writePolicyOrReferenceIt(localPolicySubject, paramTypedXmlWriter);
              }
            }
            else if (WsdlBindingSubject.class.isInstance(localObject1))
            {
              localObject2 = (WsdlBindingSubject)localObject1;
              if ((((WsdlBindingSubject)localObject2).getMessageType() == WsdlBindingSubject.WsdlMessageType.FAULT) && (paramCheckedException.getOwner().getTargetNamespace().equals(((WsdlBindingSubject)localObject2).getName().getNamespaceURI())) && (str.equals(((WsdlBindingSubject)localObject2).getName().getLocalPart()))) {
                writePolicyOrReferenceIt(localPolicySubject, paramTypedXmlWriter);
              }
            }
          }
        }
      }
    }
    LOGGER.exiting();
  }
  
  private void selectAndProcessSubject(TypedXmlWriter paramTypedXmlWriter, Class paramClass, ScopeType paramScopeType, QName paramQName)
  {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramClass, paramScopeType, paramQName });
    if (paramQName == null)
    {
      selectAndProcessSubject(paramTypedXmlWriter, paramClass, paramScopeType, (String)null);
    }
    else
    {
      if (subjects != null)
      {
        Iterator localIterator = subjects.iterator();
        while (localIterator.hasNext())
        {
          PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
          if (paramQName.equals(localPolicySubject.getSubject())) {
            writePolicyOrReferenceIt(localPolicySubject, paramTypedXmlWriter);
          }
        }
      }
      selectAndProcessSubject(paramTypedXmlWriter, paramClass, paramScopeType, paramQName.getLocalPart());
    }
    LOGGER.exiting();
  }
  
  private void selectAndProcessBindingSubject(TypedXmlWriter paramTypedXmlWriter, Class paramClass, ScopeType paramScopeType, QName paramQName)
  {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramClass, paramScopeType, paramQName });
    if ((subjects != null) && (paramQName != null))
    {
      Iterator localIterator = subjects.iterator();
      while (localIterator.hasNext())
      {
        PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
        if ((localPolicySubject.getSubject() instanceof WsdlBindingSubject))
        {
          WsdlBindingSubject localWsdlBindingSubject = (WsdlBindingSubject)localPolicySubject.getSubject();
          if (paramQName.equals(localWsdlBindingSubject.getName())) {
            writePolicyOrReferenceIt(localPolicySubject, paramTypedXmlWriter);
          }
        }
      }
    }
    selectAndProcessSubject(paramTypedXmlWriter, paramClass, paramScopeType, paramQName);
    LOGGER.exiting();
  }
  
  private void selectAndProcessSubject(TypedXmlWriter paramTypedXmlWriter, Class paramClass, ScopeType paramScopeType, String paramString)
  {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramClass, paramScopeType, paramString });
    if (subjects != null)
    {
      Iterator localIterator = subjects.iterator();
      while (localIterator.hasNext())
      {
        PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
        if (isCorrectType(policyMap, localPolicySubject, paramScopeType))
        {
          Object localObject = localPolicySubject.getSubject();
          if ((localObject != null) && (paramClass.isInstance(localObject))) {
            if (null == paramString) {
              writePolicyOrReferenceIt(localPolicySubject, paramTypedXmlWriter);
            } else {
              try
              {
                Method localMethod = paramClass.getDeclaredMethod("getName", new Class[0]);
                if (stringEqualsToStringOrQName(paramString, localMethod.invoke(localObject, new Object[0]))) {
                  writePolicyOrReferenceIt(localPolicySubject, paramTypedXmlWriter);
                }
              }
              catch (NoSuchMethodException localNoSuchMethodException)
              {
                throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(paramClass.getName(), paramString), localNoSuchMethodException)));
              }
              catch (IllegalAccessException localIllegalAccessException)
              {
                throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(paramClass.getName(), paramString), localIllegalAccessException)));
              }
              catch (InvocationTargetException localInvocationTargetException)
              {
                throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(paramClass.getName(), paramString), localInvocationTargetException)));
              }
            }
          }
        }
      }
    }
    LOGGER.exiting();
  }
  
  private static boolean isCorrectType(PolicyMap paramPolicyMap, PolicySubject paramPolicySubject, ScopeType paramScopeType)
  {
    switch (paramScopeType)
    {
    case OPERATION: 
      return (!paramPolicyMap.isInputMessageSubject(paramPolicySubject)) && (!paramPolicyMap.isOutputMessageSubject(paramPolicySubject)) && (!paramPolicyMap.isFaultMessageSubject(paramPolicySubject));
    case INPUT_MESSAGE: 
      return paramPolicyMap.isInputMessageSubject(paramPolicySubject);
    case OUTPUT_MESSAGE: 
      return paramPolicyMap.isOutputMessageSubject(paramPolicySubject);
    case FAULT_MESSAGE: 
      return paramPolicyMap.isFaultMessageSubject(paramPolicySubject);
    }
    return true;
  }
  
  private boolean stringEqualsToStringOrQName(String paramString, Object paramObject)
  {
    return (paramObject instanceof QName) ? paramString.equals(((QName)paramObject).getLocalPart()) : paramString.equals(paramObject);
  }
  
  private void writePolicyOrReferenceIt(PolicySubject paramPolicySubject, TypedXmlWriter paramTypedXmlWriter)
  {
    Policy localPolicy;
    try
    {
      localPolicy = paramPolicySubject.getEffectivePolicy(merger);
    }
    catch (PolicyException localPolicyException1)
    {
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(paramPolicySubject.toString()), localPolicyException1)));
    }
    if (localPolicy != null)
    {
      Object localObject;
      if (null == localPolicy.getIdOrName())
      {
        localObject = ModelGenerator.getGenerator();
        try
        {
          PolicySourceModel localPolicySourceModel = ((PolicyModelGenerator)localObject).translate(localPolicy);
          marshaller.marshal(localPolicySourceModel, paramTypedXmlWriter);
        }
        catch (PolicyException localPolicyException2)
        {
          throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE(), localPolicyException2)));
        }
      }
      else
      {
        localObject = paramTypedXmlWriter._element(localPolicy.getNamespaceVersion().asQName(XmlToken.PolicyReference), TypedXmlWriter.class);
        ((TypedXmlWriter)localObject)._attribute(XmlToken.Uri.toString(), '#' + localPolicy.getIdOrName());
      }
    }
  }
  
  private PolicyMapConfigurator[] loadConfigurators()
  {
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add(new AddressingPolicyMapConfigurator());
    localLinkedList.add(new MtomPolicyMapConfigurator());
    PolicyUtil.addServiceProviders(localLinkedList, PolicyMapConfigurator.class);
    return (PolicyMapConfigurator[])localLinkedList.toArray(new PolicyMapConfigurator[localLinkedList.size()]);
  }
  
  static enum ScopeType
  {
    SERVICE,  ENDPOINT,  OPERATION,  INPUT_MESSAGE,  OUTPUT_MESSAGE,  FAULT_MESSAGE;
    
    private ScopeType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyWSDLGeneratorExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */