package com.sun.xml.internal.ws.model;

import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.Pool.Marshaller;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam.Mode;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class AbstractSEIModelImpl
  implements SEIModel
{
  private List<Class> additionalClasses = new ArrayList();
  private Pool.Marshaller marshallers;
  /**
   * @deprecated
   */
  protected JAXBRIContext jaxbContext;
  protected BindingContext bindingContext;
  private String wsdlLocation;
  private QName serviceName;
  private QName portName;
  private QName portTypeName;
  private Map<Method, JavaMethodImpl> methodToJM = new HashMap();
  private Map<QName, JavaMethodImpl> nameToJM = new HashMap();
  private Map<QName, JavaMethodImpl> wsdlOpToJM = new HashMap();
  private List<JavaMethodImpl> javaMethods = new ArrayList();
  private final Map<TypeReference, Bridge> bridgeMap = new HashMap();
  private final Map<TypeInfo, XMLBridge> xmlBridgeMap = new HashMap();
  protected final QName emptyBodyName = new QName("");
  private String targetNamespace = "";
  private List<String> knownNamespaceURIs = null;
  private WSDLPort port;
  private final WebServiceFeatureList features;
  private Databinding databinding;
  BindingID bindingId;
  protected Class contractClass;
  protected Class endpointClass;
  protected ClassLoader classLoader = null;
  protected WSBinding wsBinding;
  protected BindingInfo databindingInfo;
  protected String defaultSchemaNamespaceSuffix;
  private static final Logger LOGGER = Logger.getLogger(AbstractSEIModelImpl.class.getName());
  
  protected AbstractSEIModelImpl(WebServiceFeatureList paramWebServiceFeatureList)
  {
    features = paramWebServiceFeatureList;
    databindingInfo = new BindingInfo();
    databindingInfo.setSEIModel(this);
  }
  
  void postProcess()
  {
    if (jaxbContext != null) {
      return;
    }
    populateMaps();
    createJAXBContext();
  }
  
  public void freeze(WSDLPort paramWSDLPort)
  {
    port = paramWSDLPort;
    Iterator localIterator = javaMethods.iterator();
    while (localIterator.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      localJavaMethodImpl.freeze(paramWSDLPort);
      putOp(localJavaMethodImpl.getOperationQName(), localJavaMethodImpl);
    }
    if (databinding != null) {
      ((DatabindingImpl)databinding).freeze(paramWSDLPort);
    }
  }
  
  protected abstract void populateMaps();
  
  public Pool.Marshaller getMarshallerPool()
  {
    return marshallers;
  }
  
  /**
   * @deprecated
   */
  public JAXBContext getJAXBContext()
  {
    JAXBContext localJAXBContext = bindingContext.getJAXBContext();
    if (localJAXBContext != null) {
      return localJAXBContext;
    }
    return jaxbContext;
  }
  
  public BindingContext getBindingContext()
  {
    return bindingContext;
  }
  
  public List<String> getKnownNamespaceURIs()
  {
    return knownNamespaceURIs;
  }
  
  /**
   * @deprecated
   */
  public final Bridge getBridge(TypeReference paramTypeReference)
  {
    Bridge localBridge = (Bridge)bridgeMap.get(paramTypeReference);
    assert (localBridge != null);
    return localBridge;
  }
  
  public final XMLBridge getXMLBridge(TypeInfo paramTypeInfo)
  {
    XMLBridge localXMLBridge = (XMLBridge)xmlBridgeMap.get(paramTypeInfo);
    assert (localXMLBridge != null);
    return localXMLBridge;
  }
  
  private void createJAXBContext()
  {
    final List localList = getAllTypeInfos();
    final ArrayList localArrayList = new ArrayList(localList.size() + additionalClasses.size());
    localArrayList.addAll(additionalClasses);
    Iterator localIterator1 = localList.iterator();
    Object localObject;
    while (localIterator1.hasNext())
    {
      localObject = (TypeInfo)localIterator1.next();
      localArrayList.add((Class)type);
    }
    try
    {
      bindingContext = ((BindingContext)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public BindingContext run()
          throws Exception
        {
          if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINEST)) {
            AbstractSEIModelImpl.LOGGER.log(Level.FINEST, "Creating JAXBContext with classes={0} and types={1}", new Object[] { localArrayList, localList });
          }
          UsesJAXBContextFeature localUsesJAXBContextFeature = (UsesJAXBContextFeature)features.get(UsesJAXBContextFeature.class);
          DatabindingModeFeature localDatabindingModeFeature = (DatabindingModeFeature)features.get(DatabindingModeFeature.class);
          JAXBContextFactory localJAXBContextFactory = localUsesJAXBContextFeature != null ? localUsesJAXBContextFeature.getFactory() : null;
          if (localJAXBContextFactory == null) {
            localJAXBContextFactory = JAXBContextFactory.DEFAULT;
          }
          databindingInfo.properties().put(JAXBContextFactory.class.getName(), localJAXBContextFactory);
          if (localDatabindingModeFeature != null)
          {
            if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINE)) {
              AbstractSEIModelImpl.LOGGER.log(Level.FINE, "DatabindingModeFeature in SEI specifies mode: {0}", localDatabindingModeFeature.getMode());
            }
            databindingInfo.setDatabindingMode(localDatabindingModeFeature.getMode());
          }
          if (localUsesJAXBContextFeature != null) {
            databindingInfo.setDatabindingMode("glassfish.jaxb");
          }
          databindingInfo.setClassLoader(classLoader);
          databindingInfo.contentClasses().addAll(localArrayList);
          databindingInfo.typeInfos().addAll(localList);
          databindingInfo.properties().put("c14nSupport", Boolean.FALSE);
          databindingInfo.setDefaultNamespace(getDefaultSchemaNamespace());
          BindingContext localBindingContext = BindingContextFactory.create(databindingInfo);
          if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINE)) {
            AbstractSEIModelImpl.LOGGER.log(Level.FINE, "Created binding context: " + localBindingContext.getClass().getName());
          }
          return localBindingContext;
        }
      }));
      createBondMap(localList);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new WebServiceException(ModelerMessages.UNABLE_TO_CREATE_JAXB_CONTEXT(), localPrivilegedActionException);
    }
    knownNamespaceURIs = new ArrayList();
    Iterator localIterator2 = bindingContext.getKnownNamespaceURIs().iterator();
    while (localIterator2.hasNext())
    {
      localObject = (String)localIterator2.next();
      if ((((String)localObject).length() > 0) && (!((String)localObject).equals("http://www.w3.org/2001/XMLSchema")) && (!((String)localObject).equals("http://www.w3.org/XML/1998/namespace"))) {
        knownNamespaceURIs.add(localObject);
      }
    }
    marshallers = new Pool.Marshaller(jaxbContext);
  }
  
  private List<TypeInfo> getAllTypeInfos()
  {
    ArrayList localArrayList = new ArrayList();
    Collection localCollection = methodToJM.values();
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      localJavaMethodImpl.fillTypes(localArrayList);
    }
    return localArrayList;
  }
  
  private void createBridgeMap(List<TypeReference> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      TypeReference localTypeReference = (TypeReference)localIterator.next();
      Bridge localBridge = jaxbContext.createBridge(localTypeReference);
      bridgeMap.put(localTypeReference, localBridge);
    }
  }
  
  private void createBondMap(List<TypeInfo> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      TypeInfo localTypeInfo = (TypeInfo)localIterator.next();
      XMLBridge localXMLBridge = bindingContext.createBridge(localTypeInfo);
      xmlBridgeMap.put(localTypeInfo, localXMLBridge);
    }
  }
  
  public boolean isKnownFault(QName paramQName, Method paramMethod)
  {
    JavaMethodImpl localJavaMethodImpl = getJavaMethod(paramMethod);
    Iterator localIterator = localJavaMethodImpl.getCheckedExceptions().iterator();
    while (localIterator.hasNext())
    {
      CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator.next();
      if (getDetailTypetagName.equals(paramQName)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isCheckedException(Method paramMethod, Class paramClass)
  {
    JavaMethodImpl localJavaMethodImpl = getJavaMethod(paramMethod);
    Iterator localIterator = localJavaMethodImpl.getCheckedExceptions().iterator();
    while (localIterator.hasNext())
    {
      CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator.next();
      if (localCheckedExceptionImpl.getExceptionClass().equals(paramClass)) {
        return true;
      }
    }
    return false;
  }
  
  public JavaMethodImpl getJavaMethod(Method paramMethod)
  {
    return (JavaMethodImpl)methodToJM.get(paramMethod);
  }
  
  public JavaMethodImpl getJavaMethod(QName paramQName)
  {
    return (JavaMethodImpl)nameToJM.get(paramQName);
  }
  
  public JavaMethod getJavaMethodForWsdlOperation(QName paramQName)
  {
    return (JavaMethod)wsdlOpToJM.get(paramQName);
  }
  
  /**
   * @deprecated
   */
  public QName getQNameForJM(JavaMethodImpl paramJavaMethodImpl)
  {
    Iterator localIterator = nameToJM.keySet().iterator();
    while (localIterator.hasNext())
    {
      QName localQName = (QName)localIterator.next();
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)nameToJM.get(localQName);
      if (localJavaMethodImpl.getOperationName().equals(paramJavaMethodImpl.getOperationName())) {
        return localQName;
      }
    }
    return null;
  }
  
  public final Collection<JavaMethodImpl> getJavaMethods()
  {
    return Collections.unmodifiableList(javaMethods);
  }
  
  void addJavaMethod(JavaMethodImpl paramJavaMethodImpl)
  {
    if (paramJavaMethodImpl != null) {
      javaMethods.add(paramJavaMethodImpl);
    }
  }
  
  private List<ParameterImpl> applyRpcLitParamBinding(JavaMethodImpl paramJavaMethodImpl, WrapperParameter paramWrapperParameter, WSDLBoundPortType paramWSDLBoundPortType, WebParam.Mode paramMode)
  {
    QName localQName = new QName(paramWSDLBoundPortType.getPortTypeName().getNamespaceURI(), paramJavaMethodImpl.getOperationName());
    WSDLBoundOperation localWSDLBoundOperation = paramWSDLBoundPortType.get(localQName);
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator1 = wrapperChildren.iterator();
    ParameterImpl localParameterImpl;
    while (localIterator1.hasNext())
    {
      localParameterImpl = (ParameterImpl)localIterator1.next();
      String str = localParameterImpl.getPartName();
      if (str != null)
      {
        ParameterBinding localParameterBinding = paramWSDLBoundPortType.getBinding(localQName, str, paramMode);
        if (localParameterBinding != null)
        {
          if (paramMode == WebParam.Mode.IN) {
            localParameterImpl.setInBinding(localParameterBinding);
          } else if ((paramMode == WebParam.Mode.OUT) || (paramMode == WebParam.Mode.INOUT)) {
            localParameterImpl.setOutBinding(localParameterBinding);
          }
          if (localParameterBinding.isUnbound()) {
            localArrayList1.add(localParameterImpl);
          } else if (localParameterBinding.isAttachment()) {
            localArrayList2.add(localParameterImpl);
          } else if (localParameterBinding.isBody()) {
            if (localWSDLBoundOperation != null)
            {
              WSDLPart localWSDLPart = localWSDLBoundOperation.getPart(localParameterImpl.getPartName(), paramMode);
              if (localWSDLPart != null) {
                localHashMap.put(Integer.valueOf(localWSDLPart.getIndex()), localParameterImpl);
              } else {
                localHashMap.put(Integer.valueOf(localHashMap.size()), localParameterImpl);
              }
            }
            else
            {
              localHashMap.put(Integer.valueOf(localHashMap.size()), localParameterImpl);
            }
          }
        }
      }
    }
    paramWrapperParameter.clear();
    for (int i = 0; i < localHashMap.size(); i++)
    {
      localParameterImpl = (ParameterImpl)localHashMap.get(Integer.valueOf(i));
      paramWrapperParameter.addWrapperChild(localParameterImpl);
    }
    Iterator localIterator2 = localArrayList1.iterator();
    while (localIterator2.hasNext())
    {
      localParameterImpl = (ParameterImpl)localIterator2.next();
      paramWrapperParameter.addWrapperChild(localParameterImpl);
    }
    return localArrayList2;
  }
  
  void put(QName paramQName, JavaMethodImpl paramJavaMethodImpl)
  {
    nameToJM.put(paramQName, paramJavaMethodImpl);
  }
  
  void put(Method paramMethod, JavaMethodImpl paramJavaMethodImpl)
  {
    methodToJM.put(paramMethod, paramJavaMethodImpl);
  }
  
  void putOp(QName paramQName, JavaMethodImpl paramJavaMethodImpl)
  {
    wsdlOpToJM.put(paramQName, paramJavaMethodImpl);
  }
  
  public String getWSDLLocation()
  {
    return wsdlLocation;
  }
  
  void setWSDLLocation(String paramString)
  {
    wsdlLocation = paramString;
  }
  
  public QName getServiceQName()
  {
    return serviceName;
  }
  
  public WSDLPort getPort()
  {
    return port;
  }
  
  public QName getPortName()
  {
    return portName;
  }
  
  public QName getPortTypeName()
  {
    return portTypeName;
  }
  
  void setServiceQName(QName paramQName)
  {
    serviceName = paramQName;
  }
  
  void setPortName(QName paramQName)
  {
    portName = paramQName;
  }
  
  void setPortTypeName(QName paramQName)
  {
    portTypeName = paramQName;
  }
  
  void setTargetNamespace(String paramString)
  {
    targetNamespace = paramString;
  }
  
  public String getTargetNamespace()
  {
    return targetNamespace;
  }
  
  String getDefaultSchemaNamespace()
  {
    String str = getTargetNamespace();
    if (defaultSchemaNamespaceSuffix == null) {
      return str;
    }
    if (!str.endsWith("/")) {
      str = str + "/";
    }
    return str + defaultSchemaNamespaceSuffix;
  }
  
  @NotNull
  public QName getBoundPortTypeName()
  {
    assert (portName != null);
    return new QName(portName.getNamespaceURI(), portName.getLocalPart() + "Binding");
  }
  
  public void addAdditionalClasses(Class... paramVarArgs)
  {
    for (Class localClass : paramVarArgs) {
      additionalClasses.add(localClass);
    }
  }
  
  public Databinding getDatabinding()
  {
    return databinding;
  }
  
  public void setDatabinding(Databinding paramDatabinding)
  {
    databinding = paramDatabinding;
  }
  
  public WSBinding getWSBinding()
  {
    return wsBinding;
  }
  
  public Class getContractClass()
  {
    return contractClass;
  }
  
  public Class getEndpointClass()
  {
    return endpointClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\AbstractSEIModelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */