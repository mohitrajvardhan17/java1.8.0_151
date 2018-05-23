package com.sun.xml.internal.ws.server;

import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MappingInfo;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ServerContext;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.SDDocument.WSDL;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver.Parser;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import com.sun.xml.internal.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EndpointFactory
{
  private static final EndpointFactory instance = new EndpointFactory();
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint");
  
  public EndpointFactory() {}
  
  public static EndpointFactory getInstance()
  {
    return instance;
  }
  
  public static <T> WSEndpoint<T> createEndpoint(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2)
  {
    return createEndpoint(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, true);
  }
  
  public static <T> WSEndpoint<T> createEndpoint(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2, boolean paramBoolean3)
  {
    EndpointFactory localEndpointFactory = paramContainer != null ? (EndpointFactory)paramContainer.getSPI(EndpointFactory.class) : null;
    if (localEndpointFactory == null) {
      localEndpointFactory = getInstance();
    }
    return localEndpointFactory.create(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, paramBoolean3);
  }
  
  public <T> WSEndpoint<T> create(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2)
  {
    return create(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, true);
  }
  
  public <T> WSEndpoint<T> create(Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, EntityResolver paramEntityResolver, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    MetadataReader localMetadataReader = getExternalMetadatReader(paramClass, paramWSBinding);
    if (paramBoolean3) {
      verifyImplementorClass(paramClass, localMetadataReader);
    }
    if (paramInvoker == null) {
      paramInvoker = InstanceResolver.createDefault(paramClass).createInvoker();
    }
    ArrayList localArrayList = new ArrayList();
    if (paramCollection != null) {
      localArrayList.addAll(paramCollection);
    }
    if ((paramSDDocumentSource != null) && (!localArrayList.contains(paramSDDocumentSource))) {
      localArrayList.add(paramSDDocumentSource);
    }
    if (paramContainer == null) {
      paramContainer = ContainerResolver.getInstance().getContainer();
    }
    if (paramQName1 == null) {
      paramQName1 = getDefaultServiceName(paramClass, localMetadataReader);
    }
    if (paramQName2 == null) {
      paramQName2 = getDefaultPortName(paramQName1, paramClass, localMetadataReader);
    }
    Object localObject1 = paramQName1.getNamespaceURI();
    Object localObject2 = paramQName2.getNamespaceURI();
    if (!((String)localObject1).equals(localObject2)) {
      throw new ServerRtException("wrong.tns.for.port", new Object[] { localObject2, localObject1 });
    }
    if (paramWSBinding == null) {
      paramWSBinding = BindingImpl.create(BindingID.parse(paramClass));
    }
    if ((paramBoolean3) && (paramSDDocumentSource != null)) {
      verifyPrimaryWSDL(paramSDDocumentSource, paramQName1);
    }
    localObject1 = null;
    if ((paramBoolean3) && (paramClass.getAnnotation(WebServiceProvider.class) == null)) {
      localObject1 = RuntimeModeler.getPortTypeName(paramClass, localMetadataReader);
    }
    localObject2 = categoriseMetadata(localArrayList, paramQName1, (QName)localObject1);
    SDDocumentImpl localSDDocumentImpl = paramSDDocumentSource != null ? SDDocumentImpl.create(paramSDDocumentSource, paramQName1, (QName)localObject1) : findPrimary((List)localObject2);
    WSDLPort localWSDLPort = null;
    AbstractSEIModelImpl localAbstractSEIModelImpl = null;
    if (localSDDocumentImpl != null) {
      localWSDLPort = getWSDLPort(localSDDocumentImpl, (List)localObject2, paramQName1, paramQName2, paramContainer, paramEntityResolver);
    }
    WebServiceFeatureList localWebServiceFeatureList = ((BindingImpl)paramWSBinding).getFeatures();
    if (paramBoolean3) {
      localWebServiceFeatureList.parseAnnotations(paramClass);
    }
    PolicyMap localPolicyMap = null;
    EndpointAwareTube localEndpointAwareTube;
    if (isUseProviderTube(paramClass, paramBoolean3))
    {
      if (localWSDLPort != null)
      {
        localPolicyMap = localWSDLPort.getOwner().getParent().getPolicyMap();
        localObject3 = localWSDLPort.getFeatures();
      }
      else
      {
        localPolicyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(null, paramContainer, paramClass, false, new PolicyMapMutator[0]));
        localObject3 = PolicyUtil.getPortScopedFeatures(localPolicyMap, paramQName1, paramQName2);
      }
      localWebServiceFeatureList.mergeFeatures((Iterable)localObject3, true);
      localEndpointAwareTube = createProviderInvokerTube(paramClass, paramWSBinding, paramInvoker, paramContainer);
    }
    else
    {
      localAbstractSEIModelImpl = createSEIModel(localWSDLPort, paramClass, paramQName1, paramQName2, paramWSBinding, localSDDocumentImpl);
      if ((paramWSBinding instanceof SOAPBindingImpl)) {
        ((SOAPBindingImpl)paramWSBinding).setPortKnownHeaders(((SOAPSEIModel)localAbstractSEIModelImpl).getKnownHeaders());
      }
      if (localSDDocumentImpl == null)
      {
        localSDDocumentImpl = generateWSDL(paramWSBinding, localAbstractSEIModelImpl, (List)localObject2, paramContainer, paramClass);
        localWSDLPort = getWSDLPort(localSDDocumentImpl, (List)localObject2, paramQName1, paramQName2, paramContainer, paramEntityResolver);
        localAbstractSEIModelImpl.freeze(localWSDLPort);
      }
      localPolicyMap = localWSDLPort.getOwner().getParent().getPolicyMap();
      localWebServiceFeatureList.mergeFeatures(localWSDLPort.getFeatures(), true);
      localEndpointAwareTube = createSEIInvokerTube(localAbstractSEIModelImpl, paramInvoker, paramWSBinding);
    }
    if (paramBoolean1) {
      processHandlerAnnotation(paramWSBinding, paramClass, paramQName1, paramQName2);
    }
    if (localSDDocumentImpl != null) {
      localObject2 = findMetadataClosure(localSDDocumentImpl, (List)localObject2, paramEntityResolver);
    }
    Object localObject3 = localSDDocumentImpl != null ? new ServiceDefinitionImpl((List)localObject2, localSDDocumentImpl) : null;
    return create(paramQName1, paramQName2, paramWSBinding, paramContainer, localAbstractSEIModelImpl, localWSDLPort, paramClass, (ServiceDefinitionImpl)localObject3, localEndpointAwareTube, paramBoolean2, localPolicyMap);
  }
  
  protected <T> WSEndpoint<T> create(QName paramQName1, QName paramQName2, WSBinding paramWSBinding, Container paramContainer, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Class<T> paramClass, ServiceDefinitionImpl paramServiceDefinitionImpl, EndpointAwareTube paramEndpointAwareTube, boolean paramBoolean, PolicyMap paramPolicyMap)
  {
    return new WSEndpointImpl(paramQName1, paramQName2, paramWSBinding, paramContainer, paramSEIModel, paramWSDLPort, paramClass, paramServiceDefinitionImpl, paramEndpointAwareTube, paramBoolean, paramPolicyMap);
  }
  
  protected boolean isUseProviderTube(Class<?> paramClass, boolean paramBoolean)
  {
    return (!paramBoolean) || (paramClass.getAnnotation(WebServiceProvider.class) != null);
  }
  
  protected EndpointAwareTube createSEIInvokerTube(AbstractSEIModelImpl paramAbstractSEIModelImpl, Invoker paramInvoker, WSBinding paramWSBinding)
  {
    return new SEIInvokerTube(paramAbstractSEIModelImpl, paramInvoker, paramWSBinding);
  }
  
  protected <T> EndpointAwareTube createProviderInvokerTube(Class<T> paramClass, WSBinding paramWSBinding, Invoker paramInvoker, Container paramContainer)
  {
    return ProviderInvokerTube.create(paramClass, paramWSBinding, paramInvoker, paramContainer);
  }
  
  private static List<SDDocumentImpl> findMetadataClosure(SDDocumentImpl paramSDDocumentImpl, List<SDDocumentImpl> paramList, EntityResolver paramEntityResolver)
  {
    HashMap localHashMap = new HashMap();
    Object localObject1 = paramList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (SDDocumentImpl)((Iterator)localObject1).next();
      localHashMap.put(((SDDocumentImpl)localObject2).getSystemId().toString(), localObject2);
    }
    localObject1 = new HashMap();
    ((Map)localObject1).put(paramSDDocumentImpl.getSystemId().toString(), paramSDDocumentImpl);
    Object localObject2 = new ArrayList();
    ((List)localObject2).addAll(paramSDDocumentImpl.getImports());
    while (!((List)localObject2).isEmpty())
    {
      localObject3 = (String)((List)localObject2).remove(0);
      SDDocumentImpl localSDDocumentImpl = (SDDocumentImpl)localHashMap.get(localObject3);
      if ((localSDDocumentImpl == null) && (paramEntityResolver != null)) {
        try
        {
          InputSource localInputSource = paramEntityResolver.resolveEntity(null, (String)localObject3);
          if (localInputSource != null)
          {
            MutableXMLStreamBuffer localMutableXMLStreamBuffer = new MutableXMLStreamBuffer();
            XMLStreamReader localXMLStreamReader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(localInputSource.getByteStream());
            localMutableXMLStreamBuffer.createFromXMLStreamReader(localXMLStreamReader);
            SDDocumentSource localSDDocumentSource = SDDocumentImpl.create(new URL((String)localObject3), localMutableXMLStreamBuffer);
            localSDDocumentImpl = SDDocumentImpl.create(localSDDocumentSource, null, null);
          }
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
      if ((localSDDocumentImpl != null) && (!((Map)localObject1).containsKey(localObject3)))
      {
        ((Map)localObject1).put(localObject3, localSDDocumentImpl);
        ((List)localObject2).addAll(localSDDocumentImpl.getImports());
      }
    }
    Object localObject3 = new ArrayList();
    ((List)localObject3).addAll(((Map)localObject1).values());
    return (List<SDDocumentImpl>)localObject3;
  }
  
  private static <T> void processHandlerAnnotation(WSBinding paramWSBinding, Class<T> paramClass, QName paramQName1, QName paramQName2)
  {
    HandlerAnnotationInfo localHandlerAnnotationInfo = HandlerAnnotationProcessor.buildHandlerInfo(paramClass, paramQName1, paramQName2, paramWSBinding);
    if (localHandlerAnnotationInfo != null)
    {
      paramWSBinding.setHandlerChain(localHandlerAnnotationInfo.getHandlers());
      if ((paramWSBinding instanceof SOAPBinding)) {
        ((SOAPBinding)paramWSBinding).setRoles(localHandlerAnnotationInfo.getRoles());
      }
    }
  }
  
  public static boolean verifyImplementorClass(Class<?> paramClass)
  {
    return verifyImplementorClass(paramClass, null);
  }
  
  public static boolean verifyImplementorClass(Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    if (paramMetadataReader == null) {
      paramMetadataReader = new ReflectAnnotationReader();
    }
    WebServiceProvider localWebServiceProvider = (WebServiceProvider)paramMetadataReader.getAnnotation(WebServiceProvider.class, paramClass);
    WebService localWebService = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
    if ((localWebServiceProvider == null) && (localWebService == null)) {
      throw new IllegalArgumentException(paramClass + " has neither @WebService nor @WebServiceProvider annotation");
    }
    if ((localWebServiceProvider != null) && (localWebService != null)) {
      throw new IllegalArgumentException(paramClass + " has both @WebService and @WebServiceProvider annotations");
    }
    if (localWebServiceProvider != null)
    {
      if ((Provider.class.isAssignableFrom(paramClass)) || (AsyncProvider.class.isAssignableFrom(paramClass))) {
        return true;
      }
      throw new IllegalArgumentException(paramClass + " doesn't implement Provider or AsyncProvider interface");
    }
    return false;
  }
  
  private static AbstractSEIModelImpl createSEIModel(WSDLPort paramWSDLPort, Class<?> paramClass, @NotNull QName paramQName1, @NotNull QName paramQName2, WSBinding paramWSBinding, SDDocumentSource paramSDDocumentSource)
  {
    DatabindingFactory localDatabindingFactory = DatabindingFactory.newInstance();
    DatabindingConfig localDatabindingConfig = new DatabindingConfig();
    localDatabindingConfig.setEndpointClass(paramClass);
    localDatabindingConfig.getMappingInfo().setServiceName(paramQName1);
    localDatabindingConfig.setWsdlPort(paramWSDLPort);
    localDatabindingConfig.setWSBinding(paramWSBinding);
    localDatabindingConfig.setClassLoader(paramClass.getClassLoader());
    localDatabindingConfig.getMappingInfo().setPortName(paramQName2);
    if (paramSDDocumentSource != null) {
      localDatabindingConfig.setWsdlURL(paramSDDocumentSource.getSystemId());
    }
    localDatabindingConfig.setMetadataReader(getExternalMetadatReader(paramClass, paramWSBinding));
    DatabindingImpl localDatabindingImpl = (DatabindingImpl)localDatabindingFactory.createRuntime(localDatabindingConfig);
    return (AbstractSEIModelImpl)localDatabindingImpl.getModel();
  }
  
  public static MetadataReader getExternalMetadatReader(Class<?> paramClass, WSBinding paramWSBinding)
  {
    ExternalMetadataFeature localExternalMetadataFeature = (ExternalMetadataFeature)paramWSBinding.getFeature(ExternalMetadataFeature.class);
    if (localExternalMetadataFeature != null) {
      return localExternalMetadataFeature.getMetadataReader(paramClass.getClassLoader(), false);
    }
    return null;
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass)
  {
    return getDefaultServiceName(paramClass, null);
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    return getDefaultServiceName(paramClass, true, paramMetadataReader);
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass, boolean paramBoolean)
  {
    return getDefaultServiceName(paramClass, paramBoolean, null);
  }
  
  @NotNull
  public static QName getDefaultServiceName(Class<?> paramClass, boolean paramBoolean, MetadataReader paramMetadataReader)
  {
    if (paramMetadataReader == null) {
      paramMetadataReader = new ReflectAnnotationReader();
    }
    WebServiceProvider localWebServiceProvider = (WebServiceProvider)paramMetadataReader.getAnnotation(WebServiceProvider.class, paramClass);
    QName localQName;
    if (localWebServiceProvider != null)
    {
      String str1 = localWebServiceProvider.targetNamespace();
      String str2 = localWebServiceProvider.serviceName();
      localQName = new QName(str1, str2);
    }
    else
    {
      localQName = RuntimeModeler.getServiceName(paramClass, paramMetadataReader, paramBoolean);
    }
    assert (localQName != null);
    return localQName;
  }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass)
  {
    return getDefaultPortName(paramQName, paramClass, null);
  }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    return getDefaultPortName(paramQName, paramClass, true, paramMetadataReader);
  }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass, boolean paramBoolean)
  {
    return getDefaultPortName(paramQName, paramClass, paramBoolean, null);
  }
  
  @NotNull
  public static QName getDefaultPortName(QName paramQName, Class<?> paramClass, boolean paramBoolean, MetadataReader paramMetadataReader)
  {
    if (paramMetadataReader == null) {
      paramMetadataReader = new ReflectAnnotationReader();
    }
    WebServiceProvider localWebServiceProvider = (WebServiceProvider)paramMetadataReader.getAnnotation(WebServiceProvider.class, paramClass);
    QName localQName;
    if (localWebServiceProvider != null)
    {
      String str1 = localWebServiceProvider.targetNamespace();
      String str2 = localWebServiceProvider.portName();
      localQName = new QName(str1, str2);
    }
    else
    {
      localQName = RuntimeModeler.getPortName(paramClass, paramMetadataReader, paramQName.getNamespaceURI(), paramBoolean);
    }
    assert (localQName != null);
    return localQName;
  }
  
  @Nullable
  public static String getWsdlLocation(Class<?> paramClass)
  {
    return getWsdlLocation(paramClass, new ReflectAnnotationReader());
  }
  
  @Nullable
  public static String getWsdlLocation(Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    if (paramMetadataReader == null) {
      paramMetadataReader = new ReflectAnnotationReader();
    }
    WebService localWebService = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
    if (localWebService != null) {
      return nullIfEmpty(localWebService.wsdlLocation());
    }
    WebServiceProvider localWebServiceProvider = (WebServiceProvider)paramClass.getAnnotation(WebServiceProvider.class);
    assert (localWebServiceProvider != null);
    return nullIfEmpty(localWebServiceProvider.wsdlLocation());
  }
  
  private static String nullIfEmpty(String paramString)
  {
    if (paramString.length() < 1) {
      paramString = null;
    }
    return paramString;
  }
  
  private static SDDocumentImpl generateWSDL(WSBinding paramWSBinding, AbstractSEIModelImpl paramAbstractSEIModelImpl, List<SDDocumentImpl> paramList, Container paramContainer, Class paramClass)
  {
    BindingID localBindingID = paramWSBinding.getBindingId();
    if (!localBindingID.canGenerateWSDL()) {
      throw new ServerRtException("can.not.generate.wsdl", new Object[] { localBindingID });
    }
    if (localBindingID.toString().equals("http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/"))
    {
      localObject = ServerMessages.GENERATE_NON_STANDARD_WSDL();
      logger.warning((String)localObject);
    }
    Object localObject = new WSDLGenResolver(paramList, paramAbstractSEIModelImpl.getServiceQName(), paramAbstractSEIModelImpl.getPortTypeName());
    WSDLGenInfo localWSDLGenInfo = new WSDLGenInfo();
    localWSDLGenInfo.setWsdlResolver((WSDLResolver)localObject);
    localWSDLGenInfo.setContainer(paramContainer);
    localWSDLGenInfo.setExtensions((WSDLGeneratorExtension[])ServiceFinder.find(WSDLGeneratorExtension.class).toArray());
    localWSDLGenInfo.setInlineSchemas(false);
    localWSDLGenInfo.setSecureXmlProcessingDisabled(isSecureXmlProcessingDisabled(paramWSBinding.getFeatures()));
    paramAbstractSEIModelImpl.getDatabinding().generateWSDL(localWSDLGenInfo);
    return ((WSDLGenResolver)localObject).updateDocs();
  }
  
  private static boolean isSecureXmlProcessingDisabled(WSFeatureList paramWSFeatureList)
  {
    return false;
  }
  
  private static List<SDDocumentImpl> categoriseMetadata(List<SDDocumentSource> paramList, QName paramQName1, QName paramQName2)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      SDDocumentSource localSDDocumentSource = (SDDocumentSource)localIterator.next();
      localArrayList.add(SDDocumentImpl.create(localSDDocumentSource, paramQName1, paramQName2));
    }
    return localArrayList;
  }
  
  private static void verifyPrimaryWSDL(@NotNull SDDocumentSource paramSDDocumentSource, @NotNull QName paramQName)
  {
    SDDocumentImpl localSDDocumentImpl = SDDocumentImpl.create(paramSDDocumentSource, paramQName, null);
    if (!(localSDDocumentImpl instanceof SDDocument.WSDL)) {
      throw new WebServiceException(paramSDDocumentSource.getSystemId() + " is not a WSDL. But it is passed as a primary WSDL");
    }
    SDDocument.WSDL localWSDL = (SDDocument.WSDL)localSDDocumentImpl;
    if (!localWSDL.hasService())
    {
      if (localWSDL.getAllServices().isEmpty()) {
        throw new WebServiceException("Not a primary WSDL=" + paramSDDocumentSource.getSystemId() + " since it doesn't have Service " + paramQName);
      }
      throw new WebServiceException("WSDL " + localSDDocumentImpl.getSystemId() + " has the following services " + localWSDL.getAllServices() + " but not " + paramQName + ". Maybe you forgot to specify a serviceName and/or targetNamespace in @WebService/@WebServiceProvider?");
    }
  }
  
  @Nullable
  private static SDDocumentImpl findPrimary(@NotNull List<SDDocumentImpl> paramList)
  {
    Object localObject = null;
    int i = 0;
    int j = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      SDDocumentImpl localSDDocumentImpl = (SDDocumentImpl)localIterator.next();
      if ((localSDDocumentImpl instanceof SDDocument.WSDL))
      {
        SDDocument.WSDL localWSDL = (SDDocument.WSDL)localSDDocumentImpl;
        if (localWSDL.hasService())
        {
          localObject = localSDDocumentImpl;
          if (i != 0) {
            throw new ServerRtException("duplicate.primary.wsdl", new Object[] { localSDDocumentImpl.getSystemId() });
          }
          i = 1;
        }
        if (localWSDL.hasPortType())
        {
          if (j != 0) {
            throw new ServerRtException("duplicate.abstract.wsdl", new Object[] { localSDDocumentImpl.getSystemId() });
          }
          j = 1;
        }
      }
    }
    return (SDDocumentImpl)localObject;
  }
  
  @NotNull
  private static WSDLPort getWSDLPort(SDDocumentSource paramSDDocumentSource, List<? extends SDDocumentSource> paramList, @NotNull QName paramQName1, @NotNull QName paramQName2, Container paramContainer, EntityResolver paramEntityResolver)
  {
    URL localURL = paramSDDocumentSource.getSystemId();
    try
    {
      WSDLModel localWSDLModel = RuntimeWSDLParser.parse(new XMLEntityResolver.Parser(paramSDDocumentSource), new EntityResolverImpl(paramList, paramEntityResolver), false, paramContainer, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
      if (localWSDLModel.getServices().size() == 0) {
        throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(localURL));
      }
      WSDLService localWSDLService = localWSDLModel.getService(paramQName1);
      if (localWSDLService == null) {
        throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICE(paramQName1, localURL));
      }
      WSDLPort localWSDLPort = localWSDLService.get(paramQName2);
      if (localWSDLPort == null) {
        throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(paramQName1, paramQName2, localURL));
      }
      return localWSDLPort;
    }
    catch (IOException localIOException)
    {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { localURL, localIOException });
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new ServerRtException("runtime.saxparser.exception", new Object[] { localXMLStreamException.getMessage(), localXMLStreamException.getLocation(), localXMLStreamException });
    }
    catch (SAXException localSAXException)
    {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { localURL, localSAXException });
    }
    catch (ServiceConfigurationError localServiceConfigurationError)
    {
      throw new ServerRtException("runtime.parser.wsdl", new Object[] { localURL, localServiceConfigurationError });
    }
  }
  
  private static final class EntityResolverImpl
    implements XMLEntityResolver
  {
    private Map<String, SDDocumentSource> metadata = new HashMap();
    private EntityResolver resolver;
    
    public EntityResolverImpl(List<? extends SDDocumentSource> paramList, EntityResolver paramEntityResolver)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        SDDocumentSource localSDDocumentSource = (SDDocumentSource)localIterator.next();
        metadata.put(localSDDocumentSource.getSystemId().toExternalForm(), localSDDocumentSource);
      }
      resolver = paramEntityResolver;
    }
    
    public XMLEntityResolver.Parser resolveEntity(String paramString1, String paramString2)
      throws IOException, XMLStreamException
    {
      Object localObject;
      if (paramString2 != null)
      {
        localObject = (SDDocumentSource)metadata.get(paramString2);
        if (localObject != null) {
          return new XMLEntityResolver.Parser((SDDocumentSource)localObject);
        }
      }
      if (resolver != null) {
        try
        {
          localObject = resolver.resolveEntity(paramString1, paramString2);
          if (localObject != null)
          {
            XMLEntityResolver.Parser localParser = new XMLEntityResolver.Parser(null, XMLStreamReaderFactory.create((InputSource)localObject, true));
            return localParser;
          }
        }
        catch (SAXException localSAXException)
        {
          throw new XMLStreamException(localSAXException);
        }
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\EndpointFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */