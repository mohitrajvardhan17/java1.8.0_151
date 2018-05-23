package com.sun.xml.internal.ws.model;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.databinding.DatabindingMode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.MappingInfo;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebFault;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;

public class RuntimeModeler
{
  private final WebServiceFeatureList features;
  private BindingID bindingId;
  private WSBinding wsBinding;
  private final Class portClass;
  private AbstractSEIModelImpl model;
  private SOAPBindingImpl defaultBinding;
  private String packageName;
  private String targetNamespace;
  private boolean isWrapped = true;
  private ClassLoader classLoader;
  private final WSDLPort binding;
  private QName serviceName;
  private QName portName;
  private Set<Class> classUsesWebMethod;
  private DatabindingConfig config;
  private MetadataReader metadataReader;
  public static final String PD_JAXWS_PACKAGE_PD = ".jaxws.";
  public static final String JAXWS_PACKAGE_PD = "jaxws.";
  public static final String RESPONSE = "Response";
  public static final String RETURN = "return";
  public static final String BEAN = "Bean";
  public static final String SERVICE = "Service";
  public static final String PORT = "Port";
  public static final Class HOLDER_CLASS = Holder.class;
  public static final Class<RemoteException> REMOTE_EXCEPTION_CLASS = RemoteException.class;
  public static final Class<RuntimeException> RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
  public static final Class<Exception> EXCEPTION_CLASS = Exception.class;
  public static final String DecapitalizeExceptionBeanProperties = "com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties";
  public static final String SuppressDocLitWrapperGeneration = "com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration";
  public static final String DocWrappeeNamespapceQualified = "com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified";
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server");
  
  public RuntimeModeler(@NotNull DatabindingConfig paramDatabindingConfig)
  {
    portClass = (paramDatabindingConfig.getEndpointClass() != null ? paramDatabindingConfig.getEndpointClass() : paramDatabindingConfig.getContractClass());
    serviceName = paramDatabindingConfig.getMappingInfo().getServiceName();
    binding = paramDatabindingConfig.getWsdlPort();
    classLoader = paramDatabindingConfig.getClassLoader();
    portName = paramDatabindingConfig.getMappingInfo().getPortName();
    config = paramDatabindingConfig;
    wsBinding = paramDatabindingConfig.getWSBinding();
    metadataReader = paramDatabindingConfig.getMetadataReader();
    targetNamespace = paramDatabindingConfig.getMappingInfo().getTargetNamespace();
    if (metadataReader == null) {
      metadataReader = new ReflectAnnotationReader();
    }
    if (wsBinding != null)
    {
      bindingId = wsBinding.getBindingId();
      if (paramDatabindingConfig.getFeatures() != null) {
        wsBinding.getFeatures().mergeFeatures(paramDatabindingConfig.getFeatures(), false);
      }
      if (binding != null) {
        wsBinding.getFeatures().mergeFeatures(binding.getFeatures(), false);
      }
      features = WebServiceFeatureList.toList(wsBinding.getFeatures());
    }
    else
    {
      bindingId = paramDatabindingConfig.getMappingInfo().getBindingID();
      features = WebServiceFeatureList.toList(paramDatabindingConfig.getFeatures());
      if (binding != null) {
        bindingId = binding.getBinding().getBindingId();
      }
      if (bindingId == null) {
        bindingId = getDefaultBindingID();
      }
      Object localObject;
      if (!features.contains(MTOMFeature.class))
      {
        localObject = (MTOM)getAnnotation(portClass, MTOM.class);
        if (localObject != null) {
          features.add(WebServiceFeatureList.getFeature((Annotation)localObject));
        }
      }
      if (!features.contains(EnvelopeStyleFeature.class))
      {
        localObject = (EnvelopeStyle)getAnnotation(portClass, EnvelopeStyle.class);
        if (localObject != null) {
          features.add(WebServiceFeatureList.getFeature((Annotation)localObject));
        }
      }
      wsBinding = bindingId.createBinding(features);
    }
  }
  
  private BindingID getDefaultBindingID()
  {
    BindingType localBindingType = (BindingType)getAnnotation(portClass, BindingType.class);
    if (localBindingType != null) {
      return BindingID.parse(localBindingType.value());
    }
    SOAPVersion localSOAPVersion = WebServiceFeatureList.getSoapVersion(features);
    boolean bool = features.isEnabled(MTOMFeature.class);
    if (SOAPVersion.SOAP_12.equals(localSOAPVersion)) {
      return bool ? BindingID.SOAP12_HTTP_MTOM : BindingID.SOAP12_HTTP;
    }
    return bool ? BindingID.SOAP11_HTTP_MTOM : BindingID.SOAP11_HTTP;
  }
  
  public void setClassLoader(ClassLoader paramClassLoader)
  {
    classLoader = paramClassLoader;
  }
  
  public void setPortName(QName paramQName)
  {
    portName = paramQName;
  }
  
  private <T extends Annotation> T getAnnotation(Class<?> paramClass, Class<T> paramClass1)
  {
    return metadataReader.getAnnotation(paramClass1, paramClass);
  }
  
  private <T extends Annotation> T getAnnotation(Method paramMethod, Class<T> paramClass)
  {
    return metadataReader.getAnnotation(paramClass, paramMethod);
  }
  
  private Annotation[] getAnnotations(Method paramMethod)
  {
    return metadataReader.getAnnotations(paramMethod);
  }
  
  private Annotation[] getAnnotations(Class<?> paramClass)
  {
    return metadataReader.getAnnotations(paramClass);
  }
  
  private Annotation[][] getParamAnnotations(Method paramMethod)
  {
    return metadataReader.getParameterAnnotations(paramMethod);
  }
  
  public AbstractSEIModelImpl buildRuntimeModel()
  {
    model = new SOAPSEIModel(features);
    model.contractClass = config.getContractClass();
    model.endpointClass = config.getEndpointClass();
    model.classLoader = classLoader;
    model.wsBinding = wsBinding;
    model.databindingInfo.setWsdlURL(config.getWsdlURL());
    model.databindingInfo.properties().putAll(config.properties());
    if (model.contractClass == null) {
      model.contractClass = portClass;
    }
    if ((model.endpointClass == null) && (!portClass.isInterface())) {
      model.endpointClass = portClass;
    }
    Object localObject1 = portClass;
    metadataReader.getProperties(model.databindingInfo.properties(), portClass);
    WebService localWebService = (WebService)getAnnotation(portClass, WebService.class);
    if (localWebService == null) {
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { portClass.getCanonicalName() });
    }
    Class localClass = configEndpointInterface();
    if ((localWebService.endpointInterface().length() > 0) || (localClass != null))
    {
      if (localClass != null) {
        localObject1 = localClass;
      } else {
        localObject1 = getClass(localWebService.endpointInterface(), ModelerMessages.localizableRUNTIME_MODELER_CLASS_NOT_FOUND(localWebService.endpointInterface()));
      }
      model.contractClass = ((Class)localObject1);
      model.endpointClass = portClass;
      localObject2 = (WebService)getAnnotation((Class)localObject1, WebService.class);
      if (localObject2 == null) {
        throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { localWebService.endpointInterface() });
      }
      SOAPBinding localSOAPBinding1 = (SOAPBinding)getAnnotation(portClass, SOAPBinding.class);
      SOAPBinding localSOAPBinding2 = (SOAPBinding)getAnnotation((Class)localObject1, SOAPBinding.class);
      if ((localSOAPBinding1 != null) && ((localSOAPBinding2 == null) || (localSOAPBinding2.style() != localSOAPBinding1.style()) || (localSOAPBinding2.use() != localSOAPBinding1.use()))) {
        logger.warning(ServerMessages.RUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL("@SOAPBinding", portClass.getName(), ((Class)localObject1).getName()));
      }
    }
    if (serviceName == null) {
      serviceName = getServiceName(portClass, metadataReader);
    }
    model.setServiceQName(serviceName);
    if (portName == null) {
      portName = getPortName(portClass, metadataReader, serviceName.getNamespaceURI());
    }
    model.setPortName(portName);
    Object localObject2 = (DatabindingMode)getAnnotation(portClass, DatabindingMode.class);
    if (localObject2 != null) {
      model.databindingInfo.setDatabindingMode(((DatabindingMode)localObject2).value());
    }
    processClass((Class)localObject1);
    if (model.getJavaMethods().size() == 0) {
      throw new RuntimeModelerException("runtime.modeler.no.operations", new Object[] { portClass.getName() });
    }
    model.postProcess();
    config.properties().put(BindingContext.class.getName(), model.bindingContext);
    if (binding != null) {
      model.freeze(binding);
    }
    return model;
  }
  
  private Class configEndpointInterface()
  {
    if ((config.getEndpointClass() == null) || (config.getEndpointClass().isInterface())) {
      return null;
    }
    return config.getContractClass();
  }
  
  private Class getClass(String paramString, Localizable paramLocalizable)
  {
    try
    {
      if (classLoader == null) {
        return Thread.currentThread().getContextClassLoader().loadClass(paramString);
      }
      return classLoader.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new RuntimeModelerException(paramLocalizable);
    }
  }
  
  private boolean noWrapperGen()
  {
    Object localObject = config.properties().get("com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration");
    return (localObject != null) && ((localObject instanceof Boolean)) ? ((Boolean)localObject).booleanValue() : false;
  }
  
  private Class getRequestWrapperClass(String paramString, Method paramMethod, QName paramQName)
  {
    ClassLoader localClassLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
    try
    {
      return localClassLoader.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (noWrapperGen()) {
        return WrapperComposite.class;
      }
      logger.fine("Dynamically creating request wrapper Class " + paramString);
    }
    return WrapperBeanGenerator.createRequestWrapperBean(paramString, paramMethod, paramQName, localClassLoader);
  }
  
  private Class getResponseWrapperClass(String paramString, Method paramMethod, QName paramQName)
  {
    ClassLoader localClassLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
    try
    {
      return localClassLoader.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      if (noWrapperGen()) {
        return WrapperComposite.class;
      }
      logger.fine("Dynamically creating response wrapper bean Class " + paramString);
    }
    return WrapperBeanGenerator.createResponseWrapperBean(paramString, paramMethod, paramQName, localClassLoader);
  }
  
  private Class getExceptionBeanClass(String paramString1, Class paramClass, String paramString2, String paramString3)
  {
    boolean bool = true;
    Object localObject = config.properties().get("com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties");
    if ((localObject != null) && ((localObject instanceof Boolean))) {
      bool = ((Boolean)localObject).booleanValue();
    }
    ClassLoader localClassLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
    try
    {
      return localClassLoader.loadClass(paramString1);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      logger.fine("Dynamically creating exception bean Class " + paramString1);
    }
    return WrapperBeanGenerator.createExceptionBean(paramString1, paramClass, targetNamespace, paramString2, paramString3, localClassLoader, bool);
  }
  
  protected void determineWebMethodUse(Class paramClass)
  {
    if (paramClass == null) {
      return;
    }
    if (!paramClass.isInterface())
    {
      if (paramClass == Object.class) {
        return;
      }
      for (Method localMethod : paramClass.getMethods()) {
        if (localMethod.getDeclaringClass() == paramClass)
        {
          WebMethod localWebMethod = (WebMethod)getAnnotation(localMethod, WebMethod.class);
          if ((localWebMethod != null) && (!localWebMethod.exclude()))
          {
            classUsesWebMethod.add(paramClass);
            break;
          }
        }
      }
    }
    determineWebMethodUse(paramClass.getSuperclass());
  }
  
  void processClass(Class paramClass)
  {
    classUsesWebMethod = new HashSet();
    determineWebMethodUse(paramClass);
    WebService localWebService = (WebService)getAnnotation(paramClass, WebService.class);
    QName localQName = getPortTypeName(paramClass, targetNamespace, metadataReader);
    packageName = "";
    if (paramClass.getPackage() != null) {
      packageName = paramClass.getPackage().getName();
    }
    targetNamespace = localQName.getNamespaceURI();
    model.setPortTypeName(localQName);
    model.setTargetNamespace(targetNamespace);
    model.defaultSchemaNamespaceSuffix = config.getMappingInfo().getDefaultSchemaNamespaceSuffix();
    model.setWSDLLocation(localWebService.wsdlLocation());
    SOAPBinding localSOAPBinding = (SOAPBinding)getAnnotation(paramClass, SOAPBinding.class);
    if (localSOAPBinding != null)
    {
      if ((localSOAPBinding.style() == SOAPBinding.Style.RPC) && (localSOAPBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE)) {
        throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { localSOAPBinding, paramClass });
      }
      isWrapped = (localSOAPBinding.parameterStyle() == SOAPBinding.ParameterStyle.WRAPPED);
    }
    defaultBinding = createBinding(localSOAPBinding);
    for (Method localMethod : paramClass.getMethods()) {
      if ((paramClass.isInterface()) || ((localMethod.getDeclaringClass() != Object.class) && (!getBooleanSystemProperty("com.sun.xml.internal.ws.legacyWebMethod").booleanValue() ? isWebMethodBySpec(localMethod, paramClass) : isWebMethod(localMethod)))) {
        processMethod(localMethod);
      }
    }
    ??? = (XmlSeeAlso)getAnnotation(paramClass, XmlSeeAlso.class);
    if (??? != null) {
      model.addAdditionalClasses(((XmlSeeAlso)???).value());
    }
  }
  
  private boolean isWebMethodBySpec(Method paramMethod, Class paramClass)
  {
    int i = paramMethod.getModifiers();
    int j = (Modifier.isStatic(i)) || (Modifier.isFinal(i)) ? 1 : 0;
    assert (Modifier.isPublic(i));
    assert (!paramClass.isInterface());
    WebMethod localWebMethod = (WebMethod)getAnnotation(paramMethod, WebMethod.class);
    if (localWebMethod != null)
    {
      if (localWebMethod.exclude()) {
        return false;
      }
      if (j != 0) {
        throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(paramMethod));
      }
      return true;
    }
    if (j != 0) {
      return false;
    }
    Class localClass = paramMethod.getDeclaringClass();
    return getAnnotation(localClass, WebService.class) != null;
  }
  
  private boolean isWebMethod(Method paramMethod)
  {
    int i = paramMethod.getModifiers();
    if ((Modifier.isStatic(i)) || (Modifier.isFinal(i))) {
      return false;
    }
    Class localClass = paramMethod.getDeclaringClass();
    int j = getAnnotation(localClass, WebService.class) != null ? 1 : 0;
    WebMethod localWebMethod = (WebMethod)getAnnotation(paramMethod, WebMethod.class);
    if ((localWebMethod != null) && (!localWebMethod.exclude()) && (j != 0)) {
      return true;
    }
    return (j != 0) && (!classUsesWebMethod.contains(localClass));
  }
  
  protected SOAPBindingImpl createBinding(SOAPBinding paramSOAPBinding)
  {
    SOAPBindingImpl localSOAPBindingImpl = new SOAPBindingImpl();
    SOAPBinding.Style localStyle = paramSOAPBinding != null ? paramSOAPBinding.style() : SOAPBinding.Style.DOCUMENT;
    localSOAPBindingImpl.setStyle(localStyle);
    assert (bindingId != null);
    model.bindingId = bindingId;
    SOAPVersion localSOAPVersion = bindingId.getSOAPVersion();
    localSOAPBindingImpl.setSOAPVersion(localSOAPVersion);
    return localSOAPBindingImpl;
  }
  
  public static String getNamespace(@NotNull String paramString)
  {
    if (paramString.length() == 0) {
      return null;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".");
    String[] arrayOfString;
    if (localStringTokenizer.countTokens() == 0)
    {
      arrayOfString = new String[0];
    }
    else
    {
      arrayOfString = new String[localStringTokenizer.countTokens()];
      for (int i = localStringTokenizer.countTokens() - 1; i >= 0; i--) {
        arrayOfString[i] = localStringTokenizer.nextToken();
      }
    }
    StringBuilder localStringBuilder = new StringBuilder("http://");
    for (int j = 0; j < arrayOfString.length; j++)
    {
      if (j != 0) {
        localStringBuilder.append('.');
      }
      localStringBuilder.append(arrayOfString[j]);
    }
    localStringBuilder.append('/');
    return localStringBuilder.toString();
  }
  
  private boolean isServiceException(Class<?> paramClass)
  {
    return (EXCEPTION_CLASS.isAssignableFrom(paramClass)) && (!RUNTIME_EXCEPTION_CLASS.isAssignableFrom(paramClass)) && (!REMOTE_EXCEPTION_CLASS.isAssignableFrom(paramClass));
  }
  
  private void processMethod(Method paramMethod)
  {
    WebMethod localWebMethod = (WebMethod)getAnnotation(paramMethod, WebMethod.class);
    if ((localWebMethod != null) && (localWebMethod.exclude())) {
      return;
    }
    String str1 = paramMethod.getName();
    int i = getAnnotation(paramMethod, Oneway.class) != null ? 1 : 0;
    if (i != 0) {
      for (localObject3 : paramMethod.getExceptionTypes()) {
        if (isServiceException((Class)localObject3)) {
          throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.checked.exceptions", new Object[] { portClass.getCanonicalName(), str1, ((Class)localObject3).getName() });
        }
      }
    }
    if (paramMethod.getDeclaringClass() == portClass) {
      ??? = new JavaMethodImpl(model, paramMethod, paramMethod, metadataReader);
    } else {
      try
      {
        Method localMethod = portClass.getMethod(paramMethod.getName(), paramMethod.getParameterTypes());
        ??? = new JavaMethodImpl(model, localMethod, paramMethod, metadataReader);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new RuntimeModelerException("runtime.modeler.method.not.found", new Object[] { paramMethod.getName(), portClass.getName() });
      }
    }
    MEP localMEP = getMEP(paramMethod);
    ((JavaMethodImpl)???).setMEP(localMEP);
    Object localObject2 = null;
    Object localObject3 = paramMethod.getName();
    if (localWebMethod != null)
    {
      localObject2 = localWebMethod.action();
      localObject3 = localWebMethod.operationName().length() > 0 ? localWebMethod.operationName() : localObject3;
    }
    if (binding != null)
    {
      localObject4 = binding.getBinding().get(new QName(targetNamespace, (String)localObject3));
      if (localObject4 != null)
      {
        WSDLInput localWSDLInput = ((WSDLBoundOperation)localObject4).getOperation().getInput();
        localObject5 = localWSDLInput.getAction();
        if ((localObject5 != null) && (!localWSDLInput.isDefaultAction())) {
          localObject2 = localObject5;
        } else {
          localObject2 = ((WSDLBoundOperation)localObject4).getSOAPAction();
        }
      }
    }
    ((JavaMethodImpl)???).setOperationQName(new QName(targetNamespace, (String)localObject3));
    Object localObject4 = (SOAPBinding)getAnnotation(paramMethod, SOAPBinding.class);
    if ((localObject4 != null) && (((SOAPBinding)localObject4).style() == SOAPBinding.Style.RPC))
    {
      logger.warning(ModelerMessages.RUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(localObject4, paramMethod.getName(), paramMethod.getDeclaringClass().getName()));
    }
    else if ((localObject4 == null) && (!paramMethod.getDeclaringClass().equals(portClass)))
    {
      localObject4 = (SOAPBinding)getAnnotation(paramMethod.getDeclaringClass(), SOAPBinding.class);
      if ((localObject4 != null) && (((SOAPBinding)localObject4).style() == SOAPBinding.Style.RPC) && (((SOAPBinding)localObject4).parameterStyle() == SOAPBinding.ParameterStyle.BARE)) {
        throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[] { localObject4, paramMethod.getDeclaringClass() });
      }
    }
    if ((localObject4 != null) && (defaultBinding.getStyle() != ((SOAPBinding)localObject4).style())) {
      throw new RuntimeModelerException("runtime.modeler.soapbinding.conflict", new Object[] { ((SOAPBinding)localObject4).style(), paramMethod.getName(), defaultBinding.getStyle() });
    }
    boolean bool = isWrapped;
    Object localObject5 = defaultBinding.getStyle();
    SOAPBindingImpl localSOAPBindingImpl;
    if (localObject4 != null)
    {
      localSOAPBindingImpl = createBinding((SOAPBinding)localObject4);
      localObject5 = localSOAPBindingImpl.getStyle();
      if (localObject2 != null) {
        localSOAPBindingImpl.setSOAPAction((String)localObject2);
      }
      bool = ((SOAPBinding)localObject4).parameterStyle().equals(SOAPBinding.ParameterStyle.WRAPPED);
      ((JavaMethodImpl)???).setBinding(localSOAPBindingImpl);
    }
    else
    {
      localSOAPBindingImpl = new SOAPBindingImpl(defaultBinding);
      if (localObject2 != null)
      {
        localSOAPBindingImpl.setSOAPAction((String)localObject2);
      }
      else
      {
        String str2 = SOAPVersion.SOAP_11 == localSOAPBindingImpl.getSOAPVersion() ? "" : null;
        localSOAPBindingImpl.setSOAPAction(str2);
      }
      ((JavaMethodImpl)???).setBinding(localSOAPBindingImpl);
    }
    if (!bool) {
      processDocBareMethod((JavaMethodImpl)???, (String)localObject3, paramMethod);
    } else if (((SOAPBinding.Style)localObject5).equals(SOAPBinding.Style.DOCUMENT)) {
      processDocWrappedMethod((JavaMethodImpl)???, str1, (String)localObject3, paramMethod);
    } else {
      processRpcMethod((JavaMethodImpl)???, str1, (String)localObject3, paramMethod);
    }
    model.addJavaMethod((JavaMethodImpl)???);
  }
  
  private MEP getMEP(Method paramMethod)
  {
    if (getAnnotation(paramMethod, Oneway.class) != null) {
      return MEP.ONE_WAY;
    }
    if (Response.class.isAssignableFrom(paramMethod.getReturnType())) {
      return MEP.ASYNC_POLL;
    }
    if (Future.class.isAssignableFrom(paramMethod.getReturnType())) {
      return MEP.ASYNC_CALLBACK;
    }
    return MEP.REQUEST_RESPONSE;
  }
  
  protected void processDocWrappedMethod(JavaMethodImpl paramJavaMethodImpl, String paramString1, String paramString2, Method paramMethod)
  {
    int i = 0;
    int j = getAnnotation(paramMethod, Oneway.class) != null ? 1 : 0;
    RequestWrapper localRequestWrapper = (RequestWrapper)getAnnotation(paramMethod, RequestWrapper.class);
    ResponseWrapper localResponseWrapper = (ResponseWrapper)getAnnotation(paramMethod, ResponseWrapper.class);
    String str1 = packageName + ".jaxws.";
    if ((packageName == null) || (packageName.length() == 0)) {
      str1 = "jaxws.";
    }
    String str2;
    if ((localRequestWrapper != null) && (localRequestWrapper.className().length() > 0)) {
      str2 = localRequestWrapper.className();
    } else {
      str2 = str1 + capitalize(paramMethod.getName());
    }
    String str3;
    if ((localResponseWrapper != null) && (localResponseWrapper.className().length() > 0)) {
      str3 = localResponseWrapper.className();
    } else {
      str3 = str1 + capitalize(paramMethod.getName()) + "Response";
    }
    String str4 = paramString2;
    String str5 = targetNamespace;
    String str6 = "parameters";
    if (localRequestWrapper != null)
    {
      if (localRequestWrapper.targetNamespace().length() > 0) {
        str5 = localRequestWrapper.targetNamespace();
      }
      if (localRequestWrapper.localName().length() > 0) {
        str4 = localRequestWrapper.localName();
      }
      try
      {
        if (localRequestWrapper.partName().length() > 0) {
          str6 = localRequestWrapper.partName();
        }
      }
      catch (LinkageError localLinkageError1) {}
    }
    QName localQName1 = new QName(str5, str4);
    paramJavaMethodImpl.setRequestPayloadName(localQName1);
    Class localClass1 = getRequestWrapperClass(str2, paramMethod, localQName1);
    Class localClass2 = null;
    String str7 = paramString2 + "Response";
    String str8 = targetNamespace;
    QName localQName2 = null;
    String str9 = "parameters";
    if (j == 0)
    {
      if (localResponseWrapper != null)
      {
        if (localResponseWrapper.targetNamespace().length() > 0) {
          str8 = localResponseWrapper.targetNamespace();
        }
        if (localResponseWrapper.localName().length() > 0) {
          str7 = localResponseWrapper.localName();
        }
        try
        {
          if (localResponseWrapper.partName().length() > 0) {
            str9 = localResponseWrapper.partName();
          }
        }
        catch (LinkageError localLinkageError2) {}
      }
      localQName2 = new QName(str8, str7);
      localClass2 = getResponseWrapperClass(str3, paramMethod, localQName2);
    }
    TypeInfo localTypeInfo = new TypeInfo(localQName1, localClass1, new Annotation[0]);
    localTypeInfo.setNillable(false);
    WrapperParameter localWrapperParameter1 = new WrapperParameter(paramJavaMethodImpl, localTypeInfo, WebParam.Mode.IN, 0);
    localWrapperParameter1.setPartName(str6);
    localWrapperParameter1.setBinding(ParameterBinding.BODY);
    paramJavaMethodImpl.addParameter(localWrapperParameter1);
    WrapperParameter localWrapperParameter2 = null;
    if (j == 0)
    {
      localTypeInfo = new TypeInfo(localQName2, localClass2, new Annotation[0]);
      localTypeInfo.setNillable(false);
      localWrapperParameter2 = new WrapperParameter(paramJavaMethodImpl, localTypeInfo, WebParam.Mode.OUT, -1);
      paramJavaMethodImpl.addParameter(localWrapperParameter2);
      localWrapperParameter2.setBinding(ParameterBinding.BODY);
    }
    WebResult localWebResult = (WebResult)getAnnotation(paramMethod, WebResult.class);
    XmlElement localXmlElement = (XmlElement)getAnnotation(paramMethod, XmlElement.class);
    QName localQName3 = getReturnQName(paramMethod, localWebResult, localXmlElement);
    Class localClass3 = paramMethod.getReturnType();
    boolean bool1 = false;
    if (localWebResult != null)
    {
      bool1 = localWebResult.header();
      i = (bool1) || (i != 0) ? 1 : 0;
      if ((bool1) && (localXmlElement != null)) {
        throw new RuntimeModelerException("@XmlElement cannot be specified on method " + paramMethod + " as the return value is bound to header", new Object[0]);
      }
      if ((localQName3.getNamespaceURI().length() == 0) && (localWebResult.header())) {
        localQName3 = new QName(targetNamespace, localQName3.getLocalPart());
      }
    }
    if (paramJavaMethodImpl.isAsync())
    {
      localClass3 = getAsyncReturnType(paramMethod, localClass3);
      localQName3 = new QName("return");
    }
    localQName3 = qualifyWrappeeIfNeeded(localQName3, str8);
    if ((j == 0) && (localClass3 != null) && (!localClass3.getName().equals("void")))
    {
      localObject1 = getAnnotations(paramMethod);
      if (localQName3.getLocalPart() != null)
      {
        localObject2 = new TypeInfo(localQName3, localClass3, (Annotation[])localObject1);
        metadataReader.getProperties(((TypeInfo)localObject2).properties(), paramMethod);
        ((TypeInfo)localObject2).setGenericType(paramMethod.getGenericReturnType());
        localObject3 = new ParameterImpl(paramJavaMethodImpl, (TypeInfo)localObject2, WebParam.Mode.OUT, -1);
        if (bool1)
        {
          ((ParameterImpl)localObject3).setBinding(ParameterBinding.HEADER);
          paramJavaMethodImpl.addParameter((ParameterImpl)localObject3);
        }
        else
        {
          ((ParameterImpl)localObject3).setBinding(ParameterBinding.BODY);
          localWrapperParameter2.addWrapperChild((ParameterImpl)localObject3);
        }
      }
    }
    Object localObject1 = paramMethod.getParameterTypes();
    Object localObject2 = paramMethod.getGenericParameterTypes();
    Object localObject3 = getParamAnnotations(paramMethod);
    int k = 0;
    for (Class localClass4 : localObject1)
    {
      String str10 = null;
      String str11 = "arg" + k;
      boolean bool2 = false;
      if ((!paramJavaMethodImpl.isAsync()) || (!AsyncHandler.class.isAssignableFrom(localClass4)))
      {
        boolean bool3 = HOLDER_CLASS.isAssignableFrom(localClass4);
        if ((bool3) && (localClass4 == Holder.class)) {
          localClass4 = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((java.lang.reflect.ParameterizedType)localObject2[k]).getActualTypeArguments()[0]);
        }
        WebParam.Mode localMode = bool3 ? WebParam.Mode.INOUT : WebParam.Mode.IN;
        WebParam localWebParam = null;
        localXmlElement = null;
        for (Object localObject5 : localObject3[k]) {
          if (((Annotation)localObject5).annotationType() == WebParam.class) {
            localWebParam = (WebParam)localObject5;
          } else if (((Annotation)localObject5).annotationType() == XmlElement.class) {
            localXmlElement = (XmlElement)localObject5;
          }
        }
        ??? = getParameterQName(paramMethod, localWebParam, localXmlElement, str11);
        if (localWebParam != null)
        {
          bool2 = localWebParam.header();
          i = (bool2) || (i != 0) ? 1 : 0;
          if ((bool2) && (localXmlElement != null)) {
            throw new RuntimeModelerException("@XmlElement cannot be specified on method " + paramMethod + " parameter that is bound to header", new Object[0]);
          }
          if (localWebParam.partName().length() > 0) {
            str10 = localWebParam.partName();
          } else {
            str10 = ???.getLocalPart();
          }
          if ((bool2) && (???.getNamespaceURI().equals(""))) {
            ??? = new QName(targetNamespace, ???.getLocalPart());
          }
          localMode = localWebParam.mode();
          if ((bool3) && (localMode == WebParam.Mode.IN)) {
            localMode = WebParam.Mode.INOUT;
          }
        }
        ??? = qualifyWrappeeIfNeeded(???, str5);
        localTypeInfo = new TypeInfo(???, localClass4, localObject3[k]);
        metadataReader.getProperties(localTypeInfo.properties(), paramMethod, k);
        localTypeInfo.setGenericType(localObject2[k]);
        ParameterImpl localParameterImpl = new ParameterImpl(paramJavaMethodImpl, localTypeInfo, localMode, k++);
        if (bool2)
        {
          localParameterImpl.setBinding(ParameterBinding.HEADER);
          paramJavaMethodImpl.addParameter(localParameterImpl);
          localParameterImpl.setPartName(str10);
        }
        else
        {
          localParameterImpl.setBinding(ParameterBinding.BODY);
          if (localMode != WebParam.Mode.OUT) {
            localWrapperParameter1.addWrapperChild(localParameterImpl);
          }
          if (localMode != WebParam.Mode.IN)
          {
            if (j != 0) {
              throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { portClass.getCanonicalName(), paramString1 });
            }
            localWrapperParameter2.addWrapperChild(localParameterImpl);
          }
        }
      }
    }
    if (i != 0) {
      str9 = "result";
    }
    if (localWrapperParameter2 != null) {
      localWrapperParameter2.setPartName(str9);
    }
    processExceptions(paramJavaMethodImpl, paramMethod);
  }
  
  private QName qualifyWrappeeIfNeeded(QName paramQName, String paramString)
  {
    Object localObject = config.properties().get("com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified");
    int i = (localObject != null) && ((localObject instanceof Boolean)) ? ((Boolean)localObject).booleanValue() : 0;
    if ((i != 0) && ((paramQName.getNamespaceURI() == null) || ("".equals(paramQName.getNamespaceURI())))) {
      return new QName(paramString, paramQName.getLocalPart());
    }
    return paramQName;
  }
  
  protected void processRpcMethod(JavaMethodImpl paramJavaMethodImpl, String paramString1, String paramString2, Method paramMethod)
  {
    int i = getAnnotation(paramMethod, Oneway.class) != null ? 1 : 0;
    TreeMap localTreeMap1 = new TreeMap();
    TreeMap localTreeMap2 = new TreeMap();
    String str1 = targetNamespace;
    String str2 = targetNamespace;
    if ((binding != null) && (SOAPBinding.Style.RPC.equals(binding.getBinding().getStyle())))
    {
      localQName1 = new QName(binding.getBinding().getPortTypeName().getNamespaceURI(), paramString2);
      localObject1 = binding.getBinding().get(localQName1);
      if (localObject1 != null)
      {
        if (((WSDLBoundOperation)localObject1).getRequestNamespace() != null) {
          str1 = ((WSDLBoundOperation)localObject1).getRequestNamespace();
        }
        if (((WSDLBoundOperation)localObject1).getResponseNamespace() != null) {
          str2 = ((WSDLBoundOperation)localObject1).getResponseNamespace();
        }
      }
    }
    QName localQName1 = new QName(str1, paramString2);
    paramJavaMethodImpl.setRequestPayloadName(localQName1);
    Object localObject1 = null;
    if (i == 0) {
      localObject1 = new QName(str2, paramString2 + "Response");
    }
    Class localClass1 = WrapperComposite.class;
    TypeInfo localTypeInfo = new TypeInfo(localQName1, localClass1, new Annotation[0]);
    WrapperParameter localWrapperParameter1 = new WrapperParameter(paramJavaMethodImpl, localTypeInfo, WebParam.Mode.IN, 0);
    localWrapperParameter1.setInBinding(ParameterBinding.BODY);
    paramJavaMethodImpl.addParameter(localWrapperParameter1);
    WrapperParameter localWrapperParameter2 = null;
    if (i == 0)
    {
      localTypeInfo = new TypeInfo((QName)localObject1, localClass1, new Annotation[0]);
      localWrapperParameter2 = new WrapperParameter(paramJavaMethodImpl, localTypeInfo, WebParam.Mode.OUT, -1);
      localWrapperParameter2.setOutBinding(ParameterBinding.BODY);
      paramJavaMethodImpl.addParameter(localWrapperParameter2);
    }
    Class localClass2 = paramMethod.getReturnType();
    Object localObject2 = "return";
    String str3 = targetNamespace;
    Object localObject3 = localObject2;
    boolean bool1 = false;
    WebResult localWebResult = (WebResult)getAnnotation(paramMethod, WebResult.class);
    if (localWebResult != null)
    {
      bool1 = localWebResult.header();
      if (localWebResult.name().length() > 0) {
        localObject2 = localWebResult.name();
      }
      if (localWebResult.partName().length() > 0)
      {
        localObject3 = localWebResult.partName();
        if (!bool1) {
          localObject2 = localObject3;
        }
      }
      else
      {
        localObject3 = localObject2;
      }
      if (localWebResult.targetNamespace().length() > 0) {
        str3 = localWebResult.targetNamespace();
      }
      bool1 = localWebResult.header();
    }
    QName localQName2;
    if (bool1) {
      localQName2 = new QName(str3, (String)localObject2);
    } else {
      localQName2 = new QName((String)localObject2);
    }
    if (paramJavaMethodImpl.isAsync()) {
      localClass2 = getAsyncReturnType(paramMethod, localClass2);
    }
    if ((i == 0) && (localClass2 != null) && (localClass2 != Void.TYPE))
    {
      localObject4 = getAnnotations(paramMethod);
      localObject5 = new TypeInfo(localQName2, localClass2, (Annotation[])localObject4);
      metadataReader.getProperties(((TypeInfo)localObject5).properties(), paramMethod);
      ((TypeInfo)localObject5).setGenericType(paramMethod.getGenericReturnType());
      localObject6 = new ParameterImpl(paramJavaMethodImpl, (TypeInfo)localObject5, WebParam.Mode.OUT, -1);
      ((ParameterImpl)localObject6).setPartName((String)localObject3);
      if (bool1)
      {
        ((ParameterImpl)localObject6).setBinding(ParameterBinding.HEADER);
        paramJavaMethodImpl.addParameter((ParameterImpl)localObject6);
        ((TypeInfo)localObject5).setGlobalElement(true);
      }
      else
      {
        ParameterBinding localParameterBinding = getBinding(paramString2, (String)localObject3, false, WebParam.Mode.OUT);
        ((ParameterImpl)localObject6).setBinding(localParameterBinding);
        if (localParameterBinding.isBody())
        {
          ((TypeInfo)localObject5).setGlobalElement(false);
          localObject7 = getPart(new QName(targetNamespace, paramString2), (String)localObject3, WebParam.Mode.OUT);
          if (localObject7 == null) {
            localTreeMap1.put(Integer.valueOf(localTreeMap1.size() + 10000), localObject6);
          } else {
            localTreeMap1.put(Integer.valueOf(((WSDLPart)localObject7).getIndex()), localObject6);
          }
        }
        else
        {
          paramJavaMethodImpl.addParameter((ParameterImpl)localObject6);
        }
      }
    }
    Object localObject4 = paramMethod.getParameterTypes();
    Object localObject5 = paramMethod.getGenericParameterTypes();
    Object localObject6 = getParamAnnotations(paramMethod);
    int j = 0;
    for (Class localClass3 : localObject4)
    {
      Object localObject8 = "";
      String str4 = "";
      Object localObject9 = "";
      boolean bool2 = false;
      if ((!paramJavaMethodImpl.isAsync()) || (!AsyncHandler.class.isAssignableFrom(localClass3)))
      {
        boolean bool3 = HOLDER_CLASS.isAssignableFrom(localClass3);
        if ((bool3) && (localClass3 == Holder.class)) {
          localClass3 = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((java.lang.reflect.ParameterizedType)localObject5[j]).getActualTypeArguments()[0]);
        }
        Object localObject10 = bool3 ? WebParam.Mode.INOUT : WebParam.Mode.IN;
        for (Object localObject12 : localObject6[j]) {
          if (((Annotation)localObject12).annotationType() == WebParam.class)
          {
            WebParam localWebParam = (WebParam)localObject12;
            localObject8 = localWebParam.name();
            localObject9 = localWebParam.partName();
            bool2 = localWebParam.header();
            WebParam.Mode localMode = localWebParam.mode();
            str4 = localWebParam.targetNamespace();
            if ((bool3) && (localMode == WebParam.Mode.IN)) {
              localMode = WebParam.Mode.INOUT;
            }
            localObject10 = localMode;
            break;
          }
        }
        if (((String)localObject8).length() == 0) {
          localObject8 = "arg" + j;
        }
        if (((String)localObject9).length() == 0) {
          localObject9 = localObject8;
        } else if (!bool2) {
          localObject8 = localObject9;
        }
        if (((String)localObject9).length() == 0) {
          localObject9 = localObject8;
        }
        if (!bool2)
        {
          ??? = new QName("", (String)localObject8);
        }
        else
        {
          if (str4.length() == 0) {
            str4 = targetNamespace;
          }
          ??? = new QName(str4, (String)localObject8);
        }
        localTypeInfo = new TypeInfo(???, localClass3, localObject6[j]);
        metadataReader.getProperties(localTypeInfo.properties(), paramMethod, j);
        localTypeInfo.setGenericType(localObject5[j]);
        ParameterImpl localParameterImpl2 = new ParameterImpl(paramJavaMethodImpl, localTypeInfo, (WebParam.Mode)localObject10, j++);
        localParameterImpl2.setPartName((String)localObject9);
        Object localObject11;
        if (localObject10 == WebParam.Mode.INOUT)
        {
          localObject11 = getBinding(paramString2, (String)localObject9, bool2, WebParam.Mode.IN);
          localParameterImpl2.setInBinding((ParameterBinding)localObject11);
          localObject11 = getBinding(paramString2, (String)localObject9, bool2, WebParam.Mode.OUT);
          localParameterImpl2.setOutBinding((ParameterBinding)localObject11);
        }
        else if (bool2)
        {
          localTypeInfo.setGlobalElement(true);
          localParameterImpl2.setBinding(ParameterBinding.HEADER);
        }
        else
        {
          localObject11 = getBinding(paramString2, (String)localObject9, false, (WebParam.Mode)localObject10);
          localParameterImpl2.setBinding((ParameterBinding)localObject11);
        }
        if (localParameterImpl2.getInBinding().isBody())
        {
          localTypeInfo.setGlobalElement(false);
          if (!localParameterImpl2.isOUT())
          {
            localObject11 = getPart(new QName(targetNamespace, paramString2), (String)localObject9, WebParam.Mode.IN);
            if (localObject11 == null) {
              localTreeMap2.put(Integer.valueOf(localTreeMap2.size() + 10000), localParameterImpl2);
            } else {
              localTreeMap2.put(Integer.valueOf(((WSDLPart)localObject11).getIndex()), localParameterImpl2);
            }
          }
          if (!localParameterImpl2.isIN())
          {
            if (i != 0) {
              throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[] { portClass.getCanonicalName(), paramString1 });
            }
            localObject11 = getPart(new QName(targetNamespace, paramString2), (String)localObject9, WebParam.Mode.OUT);
            if (localObject11 == null) {
              localTreeMap1.put(Integer.valueOf(localTreeMap1.size() + 10000), localParameterImpl2);
            } else {
              localTreeMap1.put(Integer.valueOf(((WSDLPart)localObject11).getIndex()), localParameterImpl2);
            }
          }
        }
        else
        {
          paramJavaMethodImpl.addParameter(localParameterImpl2);
        }
      }
    }
    Object localObject7 = localTreeMap2.values().iterator();
    ParameterImpl localParameterImpl1;
    while (((Iterator)localObject7).hasNext())
    {
      localParameterImpl1 = (ParameterImpl)((Iterator)localObject7).next();
      localWrapperParameter1.addWrapperChild(localParameterImpl1);
    }
    localObject7 = localTreeMap1.values().iterator();
    while (((Iterator)localObject7).hasNext())
    {
      localParameterImpl1 = (ParameterImpl)((Iterator)localObject7).next();
      localWrapperParameter2.addWrapperChild(localParameterImpl1);
    }
    processExceptions(paramJavaMethodImpl, paramMethod);
  }
  
  protected void processExceptions(JavaMethodImpl paramJavaMethodImpl, Method paramMethod)
  {
    Action localAction = (Action)getAnnotation(paramMethod, Action.class);
    FaultAction[] arrayOfFaultAction1 = new FaultAction[0];
    if (localAction != null) {
      arrayOfFaultAction1 = localAction.fault();
    }
    for (Class localClass1 : paramMethod.getExceptionTypes()) {
      if ((EXCEPTION_CLASS.isAssignableFrom(localClass1)) && (!RUNTIME_EXCEPTION_CLASS.isAssignableFrom(localClass1)) && (!REMOTE_EXCEPTION_CLASS.isAssignableFrom(localClass1)))
      {
        WebFault localWebFault = (WebFault)getAnnotation(localClass1, WebFault.class);
        Method localMethod = getWSDLExceptionFaultInfo(localClass1);
        ExceptionType localExceptionType = ExceptionType.WSDLException;
        String str1 = targetNamespace;
        String str2 = localClass1.getSimpleName();
        String str3 = packageName + ".jaxws.";
        if (packageName.length() == 0) {
          str3 = "jaxws.";
        }
        String str4 = str3 + str2 + "Bean";
        String str5 = localClass1.getSimpleName();
        if (localWebFault != null)
        {
          if (localWebFault.faultBean().length() > 0) {
            str4 = localWebFault.faultBean();
          }
          if (localWebFault.name().length() > 0) {
            str2 = localWebFault.name();
          }
          if (localWebFault.targetNamespace().length() > 0) {
            str1 = localWebFault.targetNamespace();
          }
          if (localWebFault.messageName().length() > 0) {
            str5 = localWebFault.messageName();
          }
        }
        Class localClass2;
        Annotation[] arrayOfAnnotation;
        if (localMethod == null)
        {
          localClass2 = getExceptionBeanClass(str4, localClass1, str2, str1);
          localExceptionType = ExceptionType.UserDefined;
          arrayOfAnnotation = getAnnotations(localClass2);
        }
        else
        {
          localClass2 = localMethod.getReturnType();
          arrayOfAnnotation = getAnnotations(localMethod);
        }
        QName localQName = new QName(str1, str2);
        TypeInfo localTypeInfo = new TypeInfo(localQName, localClass2, arrayOfAnnotation);
        CheckedExceptionImpl localCheckedExceptionImpl = new CheckedExceptionImpl(paramJavaMethodImpl, localClass1, localTypeInfo, localExceptionType);
        localCheckedExceptionImpl.setMessageName(str5);
        for (FaultAction localFaultAction : arrayOfFaultAction1) {
          if ((localFaultAction.className().equals(localClass1)) && (!localFaultAction.value().equals("")))
          {
            localCheckedExceptionImpl.setFaultAction(localFaultAction.value());
            break;
          }
        }
        paramJavaMethodImpl.addException(localCheckedExceptionImpl);
      }
    }
  }
  
  protected Method getWSDLExceptionFaultInfo(Class paramClass)
  {
    if (getAnnotation(paramClass, WebFault.class) == null) {
      return null;
    }
    try
    {
      return paramClass.getMethod("getFaultInfo", new Class[0]);
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
  
  protected void processDocBareMethod(JavaMethodImpl paramJavaMethodImpl, String paramString, Method paramMethod)
  {
    String str1 = paramString + "Response";
    String str2 = targetNamespace;
    String str3 = null;
    boolean bool1 = false;
    WebResult localWebResult = (WebResult)getAnnotation(paramMethod, WebResult.class);
    if (localWebResult != null)
    {
      if (localWebResult.name().length() > 0) {
        str1 = localWebResult.name();
      }
      if (localWebResult.targetNamespace().length() > 0) {
        str2 = localWebResult.targetNamespace();
      }
      str3 = localWebResult.partName();
      bool1 = localWebResult.header();
    }
    Class localClass1 = paramMethod.getReturnType();
    Type localType = paramMethod.getGenericReturnType();
    if (paramJavaMethodImpl.isAsync()) {
      localClass1 = getAsyncReturnType(paramMethod, localClass1);
    }
    Object localObject4;
    if ((localClass1 != null) && (!localClass1.getName().equals("void")))
    {
      localObject1 = getAnnotations(paramMethod);
      if (str1 != null)
      {
        localObject2 = new QName(str2, str1);
        localObject3 = new TypeInfo((QName)localObject2, localClass1, (Annotation[])localObject1);
        ((TypeInfo)localObject3).setGenericType(localType);
        metadataReader.getProperties(((TypeInfo)localObject3).properties(), paramMethod);
        ParameterImpl localParameterImpl1 = new ParameterImpl(paramJavaMethodImpl, (TypeInfo)localObject3, WebParam.Mode.OUT, -1);
        if ((str3 == null) || (str3.length() == 0)) {
          str3 = str1;
        }
        localParameterImpl1.setPartName(str3);
        if (bool1)
        {
          localParameterImpl1.setBinding(ParameterBinding.HEADER);
        }
        else
        {
          localObject4 = getBinding(paramString, str3, false, WebParam.Mode.OUT);
          localParameterImpl1.setBinding((ParameterBinding)localObject4);
        }
        paramJavaMethodImpl.addParameter(localParameterImpl1);
      }
    }
    Object localObject1 = paramMethod.getParameterTypes();
    Object localObject2 = paramMethod.getGenericParameterTypes();
    Object localObject3 = getParamAnnotations(paramMethod);
    int i = 0;
    for (Class localClass2 : localObject1)
    {
      String str4 = paramString;
      String str5 = null;
      String str6 = targetNamespace;
      boolean bool2 = false;
      if ((!paramJavaMethodImpl.isAsync()) || (!AsyncHandler.class.isAssignableFrom(localClass2)))
      {
        boolean bool3 = HOLDER_CLASS.isAssignableFrom(localClass2);
        if ((bool3) && (localClass2 == Holder.class)) {
          localClass2 = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((java.lang.reflect.ParameterizedType)localObject2[i]).getActualTypeArguments()[0]);
        }
        WebParam.Mode localMode = bool3 ? WebParam.Mode.INOUT : WebParam.Mode.IN;
        ParameterBinding localParameterBinding;
        for (localParameterBinding : localObject3[i]) {
          if (localParameterBinding.annotationType() == WebParam.class)
          {
            WebParam localWebParam = (WebParam)localParameterBinding;
            localMode = localWebParam.mode();
            if ((bool3) && (localMode == WebParam.Mode.IN)) {
              localMode = WebParam.Mode.INOUT;
            }
            bool2 = localWebParam.header();
            if (bool2) {
              str4 = "arg" + i;
            }
            if ((localMode == WebParam.Mode.OUT) && (!bool2)) {
              str4 = paramString + "Response";
            }
            if (localWebParam.name().length() > 0) {
              str4 = localWebParam.name();
            }
            str5 = localWebParam.partName();
            if (localWebParam.targetNamespace().equals("")) {
              break;
            }
            str6 = localWebParam.targetNamespace();
            break;
          }
        }
        ??? = new QName(str6, str4);
        if ((!bool2) && (localMode != WebParam.Mode.OUT)) {
          paramJavaMethodImpl.setRequestPayloadName(???);
        }
        TypeInfo localTypeInfo = new TypeInfo(???, localClass2, localObject3[i]);
        metadataReader.getProperties(localTypeInfo.properties(), paramMethod, i);
        localTypeInfo.setGenericType(localObject2[i]);
        ParameterImpl localParameterImpl2 = new ParameterImpl(paramJavaMethodImpl, localTypeInfo, localMode, i++);
        if ((str5 == null) || (str5.length() == 0)) {
          str5 = str4;
        }
        localParameterImpl2.setPartName(str5);
        if (localMode == WebParam.Mode.INOUT)
        {
          localParameterBinding = getBinding(paramString, str5, bool2, WebParam.Mode.IN);
          localParameterImpl2.setInBinding(localParameterBinding);
          localParameterBinding = getBinding(paramString, str5, bool2, WebParam.Mode.OUT);
          localParameterImpl2.setOutBinding(localParameterBinding);
        }
        else if (bool2)
        {
          localParameterImpl2.setBinding(ParameterBinding.HEADER);
        }
        else
        {
          localParameterBinding = getBinding(paramString, str5, false, localMode);
          localParameterImpl2.setBinding(localParameterBinding);
        }
        paramJavaMethodImpl.addParameter(localParameterImpl2);
      }
    }
    validateDocBare(paramJavaMethodImpl);
    processExceptions(paramJavaMethodImpl, paramMethod);
  }
  
  private void validateDocBare(JavaMethodImpl paramJavaMethodImpl)
  {
    int i = 0;
    Iterator localIterator = paramJavaMethodImpl.getRequestParameters().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Parameter)localIterator.next();
      if ((((Parameter)localObject).getBinding().equals(ParameterBinding.BODY)) && (((Parameter)localObject).isIN())) {
        i++;
      }
      if (i > 1) {
        throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(portClass.getName(), paramJavaMethodImpl.getMethod().getName()));
      }
    }
    int j = 0;
    Object localObject = paramJavaMethodImpl.getResponseParameters().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Parameter localParameter = (Parameter)((Iterator)localObject).next();
      if ((localParameter.getBinding().equals(ParameterBinding.BODY)) && (localParameter.isOUT())) {
        j++;
      }
      if (j > 1) {
        throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(portClass.getName(), paramJavaMethodImpl.getMethod().getName()));
      }
    }
  }
  
  private Class getAsyncReturnType(Method paramMethod, Class paramClass)
  {
    if (Response.class.isAssignableFrom(paramClass))
    {
      localObject = paramMethod.getGenericReturnType();
      return (Class)Utils.REFLECTION_NAVIGATOR.erasure(((java.lang.reflect.ParameterizedType)localObject).getActualTypeArguments()[0]);
    }
    Object localObject = paramMethod.getGenericParameterTypes();
    Class[] arrayOfClass1 = paramMethod.getParameterTypes();
    int i = 0;
    for (Class localClass : arrayOfClass1)
    {
      if (AsyncHandler.class.isAssignableFrom(localClass)) {
        return (Class)Utils.REFLECTION_NAVIGATOR.erasure(((java.lang.reflect.ParameterizedType)localObject[i]).getActualTypeArguments()[0]);
      }
      i++;
    }
    return paramClass;
  }
  
  public static String capitalize(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toUpperCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
  
  public static QName getServiceName(Class<?> paramClass)
  {
    return getServiceName(paramClass, null);
  }
  
  public static QName getServiceName(Class<?> paramClass, boolean paramBoolean)
  {
    return getServiceName(paramClass, null, paramBoolean);
  }
  
  public static QName getServiceName(Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    return getServiceName(paramClass, paramMetadataReader, true);
  }
  
  public static QName getServiceName(Class<?> paramClass, MetadataReader paramMetadataReader, boolean paramBoolean)
  {
    if (paramClass.isInterface()) {
      throw new RuntimeModelerException("runtime.modeler.cannot.get.serviceName.from.interface", new Object[] { paramClass.getCanonicalName() });
    }
    String str1 = paramClass.getSimpleName() + "Service";
    String str2 = "";
    if (paramClass.getPackage() != null) {
      str2 = paramClass.getPackage().getName();
    }
    WebService localWebService = (WebService)getAnnotation(WebService.class, paramClass, paramMetadataReader);
    if ((paramBoolean) && (localWebService == null)) {
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() });
    }
    if ((localWebService != null) && (localWebService.serviceName().length() > 0)) {
      str1 = localWebService.serviceName();
    }
    String str3 = getNamespace(str2);
    if ((localWebService != null) && (localWebService.targetNamespace().length() > 0)) {
      str3 = localWebService.targetNamespace();
    } else if (str3 == null) {
      throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { paramClass.getName() });
    }
    return new QName(str3, str1);
  }
  
  public static QName getPortName(Class<?> paramClass, String paramString)
  {
    return getPortName(paramClass, null, paramString);
  }
  
  public static QName getPortName(Class<?> paramClass, String paramString, boolean paramBoolean)
  {
    return getPortName(paramClass, null, paramString, paramBoolean);
  }
  
  public static QName getPortName(Class<?> paramClass, MetadataReader paramMetadataReader, String paramString)
  {
    return getPortName(paramClass, paramMetadataReader, paramString, true);
  }
  
  public static QName getPortName(Class<?> paramClass, MetadataReader paramMetadataReader, String paramString, boolean paramBoolean)
  {
    WebService localWebService = (WebService)getAnnotation(WebService.class, paramClass, paramMetadataReader);
    if ((paramBoolean) && (localWebService == null)) {
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() });
    }
    String str1;
    if ((localWebService != null) && (localWebService.portName().length() > 0)) {
      str1 = localWebService.portName();
    } else if ((localWebService != null) && (localWebService.name().length() > 0)) {
      str1 = localWebService.name() + "Port";
    } else {
      str1 = paramClass.getSimpleName() + "Port";
    }
    if (paramString == null) {
      if ((localWebService != null) && (localWebService.targetNamespace().length() > 0))
      {
        paramString = localWebService.targetNamespace();
      }
      else
      {
        String str2 = null;
        if (paramClass.getPackage() != null) {
          str2 = paramClass.getPackage().getName();
        }
        if (str2 != null) {
          paramString = getNamespace(str2);
        }
        if (paramString == null) {
          throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { paramClass.getName() });
        }
      }
    }
    return new QName(paramString, str1);
  }
  
  static <A extends Annotation> A getAnnotation(Class<A> paramClass, Class<?> paramClass1, MetadataReader paramMetadataReader)
  {
    return paramMetadataReader == null ? paramClass1.getAnnotation(paramClass) : paramMetadataReader.getAnnotation(paramClass, paramClass1);
  }
  
  public static QName getPortTypeName(Class<?> paramClass)
  {
    return getPortTypeName(paramClass, null, null);
  }
  
  public static QName getPortTypeName(Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    return getPortTypeName(paramClass, null, paramMetadataReader);
  }
  
  public static QName getPortTypeName(Class<?> paramClass, String paramString, MetadataReader paramMetadataReader)
  {
    assert (paramClass != null);
    WebService localWebService1 = (WebService)getAnnotation(WebService.class, paramClass, paramMetadataReader);
    Object localObject = paramClass;
    if (localWebService1 == null) {
      throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() });
    }
    if (!paramClass.isInterface())
    {
      str = localWebService1.endpointInterface();
      if (str.length() > 0)
      {
        try
        {
          localObject = Thread.currentThread().getContextClassLoader().loadClass(str);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new RuntimeModelerException("runtime.modeler.class.not.found", new Object[] { str });
        }
        WebService localWebService2 = (WebService)getAnnotation(WebService.class, (Class)localObject, paramMetadataReader);
        if (localWebService2 == null) {
          throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[] { localWebService1.endpointInterface() });
        }
      }
    }
    localWebService1 = (WebService)getAnnotation(WebService.class, (Class)localObject, paramMetadataReader);
    String str = localWebService1.name();
    if (str.length() == 0) {
      str = ((Class)localObject).getSimpleName();
    }
    if ((paramString == null) || ("".equals(paramString.trim()))) {
      paramString = localWebService1.targetNamespace();
    }
    if (paramString.length() == 0) {
      paramString = getNamespace(((Class)localObject).getPackage().getName());
    }
    if (paramString == null) {
      throw new RuntimeModelerException("runtime.modeler.no.package", new Object[] { ((Class)localObject).getName() });
    }
    return new QName(paramString, str);
  }
  
  private ParameterBinding getBinding(String paramString1, String paramString2, boolean paramBoolean, WebParam.Mode paramMode)
  {
    if (binding == null)
    {
      if (paramBoolean) {
        return ParameterBinding.HEADER;
      }
      return ParameterBinding.BODY;
    }
    QName localQName = new QName(binding.getBinding().getPortType().getName().getNamespaceURI(), paramString1);
    return binding.getBinding().getBinding(localQName, paramString2, paramMode);
  }
  
  private WSDLPart getPart(QName paramQName, String paramString, WebParam.Mode paramMode)
  {
    if (binding != null)
    {
      WSDLBoundOperation localWSDLBoundOperation = binding.getBinding().get(paramQName);
      if (localWSDLBoundOperation != null) {
        return localWSDLBoundOperation.getPart(paramString, paramMode);
      }
    }
    return null;
  }
  
  private static Boolean getBooleanSystemProperty(String paramString)
  {
    (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        String str = System.getProperty(val$prop);
        return str != null ? Boolean.valueOf(str) : Boolean.FALSE;
      }
    });
  }
  
  private static QName getReturnQName(Method paramMethod, WebResult paramWebResult, XmlElement paramXmlElement)
  {
    String str1 = null;
    if ((paramWebResult != null) && (paramWebResult.name().length() > 0)) {
      str1 = paramWebResult.name();
    }
    String str2 = null;
    if ((paramXmlElement != null) && (!paramXmlElement.name().equals("##default"))) {
      str2 = paramXmlElement.name();
    }
    if ((str2 != null) && (str1 != null) && (!str2.equals(str1))) {
      throw new RuntimeModelerException("@XmlElement(name)=" + str2 + " and @WebResult(name)=" + str1 + " are different for method " + paramMethod, new Object[0]);
    }
    String str3 = "return";
    if (str1 != null) {
      str3 = str1;
    } else if (str2 != null) {
      str3 = str2;
    }
    String str4 = null;
    if ((paramWebResult != null) && (paramWebResult.targetNamespace().length() > 0)) {
      str4 = paramWebResult.targetNamespace();
    }
    String str5 = null;
    if ((paramXmlElement != null) && (!paramXmlElement.namespace().equals("##default"))) {
      str5 = paramXmlElement.namespace();
    }
    if ((str5 != null) && (str4 != null) && (!str5.equals(str4))) {
      throw new RuntimeModelerException("@XmlElement(namespace)=" + str5 + " and @WebResult(targetNamespace)=" + str4 + " are different for method " + paramMethod, new Object[0]);
    }
    String str6 = "";
    if (str4 != null) {
      str6 = str4;
    } else if (str5 != null) {
      str6 = str5;
    }
    return new QName(str6, str3);
  }
  
  private static QName getParameterQName(Method paramMethod, WebParam paramWebParam, XmlElement paramXmlElement, String paramString)
  {
    String str1 = null;
    if ((paramWebParam != null) && (paramWebParam.name().length() > 0)) {
      str1 = paramWebParam.name();
    }
    String str2 = null;
    if ((paramXmlElement != null) && (!paramXmlElement.name().equals("##default"))) {
      str2 = paramXmlElement.name();
    }
    if ((str2 != null) && (str1 != null) && (!str2.equals(str1))) {
      throw new RuntimeModelerException("@XmlElement(name)=" + str2 + " and @WebParam(name)=" + str1 + " are different for method " + paramMethod, new Object[0]);
    }
    String str3 = paramString;
    if (str1 != null) {
      str3 = str1;
    } else if (str2 != null) {
      str3 = str2;
    }
    String str4 = null;
    if ((paramWebParam != null) && (paramWebParam.targetNamespace().length() > 0)) {
      str4 = paramWebParam.targetNamespace();
    }
    String str5 = null;
    if ((paramXmlElement != null) && (!paramXmlElement.namespace().equals("##default"))) {
      str5 = paramXmlElement.namespace();
    }
    if ((str5 != null) && (str4 != null) && (!str5.equals(str4))) {
      throw new RuntimeModelerException("@XmlElement(namespace)=" + str5 + " and @WebParam(targetNamespace)=" + str4 + " are different for method " + paramMethod, new Object[0]);
    }
    String str6 = "";
    if (str4 != null) {
      str6 = str4;
    } else if (str5 != null) {
      str6 = str5;
    }
    return new QName(str6, str3);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\RuntimeModeler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */