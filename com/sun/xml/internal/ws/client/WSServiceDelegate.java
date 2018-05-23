package com.sun.xml.internal.ws.client;

import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.Closeable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.ComponentFeature.Target;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.WSService.InitParams;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.Metadata;
import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
import com.sun.xml.internal.ws.api.client.ServiceInterceptorFactory;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MappingInfo;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.pipe.Stubs;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.client.sei.SEIStub;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.soap.AddressingFeature;
import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class WSServiceDelegate
  extends WSService
{
  private final Map<QName, PortInfo> ports = new HashMap();
  @NotNull
  private HandlerConfigurator handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(null);
  private final Class<? extends Service> serviceClass;
  private final WebServiceFeatureList features;
  @NotNull
  private final QName serviceName;
  private final Map<QName, SEIPortInfo> seiContext = new HashMap();
  private volatile Executor executor;
  @Nullable
  private WSDLService wsdlService;
  private final Container container;
  @NotNull
  final ServiceInterceptor serviceInterceptor;
  private URL wsdlURL;
  protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
  
  protected Map<QName, PortInfo> getQNameToPortInfoMap()
  {
    return ports;
  }
  
  public WSServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs)
  {
    this(paramURL, paramQName, paramClass, new WebServiceFeatureList(paramVarArgs));
  }
  
  protected WSServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass, WebServiceFeatureList paramWebServiceFeatureList)
  {
    this(paramURL == null ? null : new StreamSource(paramURL.toExternalForm()), paramQName, paramClass, paramWebServiceFeatureList);
    wsdlURL = paramURL;
  }
  
  public WSServiceDelegate(@Nullable Source paramSource, @NotNull QName paramQName, @NotNull Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs)
  {
    this(paramSource, paramQName, paramClass, new WebServiceFeatureList(paramVarArgs));
  }
  
  protected WSServiceDelegate(@Nullable Source paramSource, @NotNull QName paramQName, @NotNull Class<? extends Service> paramClass, WebServiceFeatureList paramWebServiceFeatureList)
  {
    this(paramSource, null, paramQName, paramClass, paramWebServiceFeatureList);
  }
  
  public WSServiceDelegate(@Nullable Source paramSource, @Nullable WSDLService paramWSDLService, @NotNull QName paramQName, @NotNull Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs)
  {
    this(paramSource, paramWSDLService, paramQName, paramClass, new WebServiceFeatureList(paramVarArgs));
  }
  
  public WSServiceDelegate(@Nullable Source paramSource, @Nullable WSDLService paramWSDLService, @NotNull QName paramQName, @NotNull final Class<? extends Service> paramClass, WebServiceFeatureList paramWebServiceFeatureList)
  {
    if (paramQName == null) {
      throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME_NULL(null));
    }
    features = paramWebServiceFeatureList;
    WSService.InitParams localInitParams = (WSService.InitParams)INIT_PARAMS.get();
    INIT_PARAMS.set(null);
    if (localInitParams == null) {
      localInitParams = EMPTY_PARAMS;
    }
    serviceName = paramQName;
    serviceClass = paramClass;
    Object localObject1 = localInitParams.getContainer() != null ? localInitParams.getContainer() : ContainerResolver.getInstance().getContainer();
    if (localObject1 == Container.NONE) {
      localObject1 = new ClientContainer();
    }
    container = ((Container)localObject1);
    ComponentFeature localComponentFeature = (ComponentFeature)features.get(ComponentFeature.class);
    if (localComponentFeature != null) {
      switch (localComponentFeature.getTarget())
      {
      case SERVICE: 
        getComponents().add(localComponentFeature.getComponent());
        break;
      case CONTAINER: 
        container.getComponents().add(localComponentFeature.getComponent());
        break;
      default: 
        throw new IllegalArgumentException();
      }
    }
    ComponentsFeature localComponentsFeature = (ComponentsFeature)features.get(ComponentsFeature.class);
    if (localComponentsFeature != null)
    {
      localObject2 = localComponentsFeature.getComponentFeatures().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ComponentFeature)((Iterator)localObject2).next();
        switch (localObject3.getTarget())
        {
        case SERVICE: 
          getComponents().add(((ComponentFeature)localObject3).getComponent());
          break;
        case CONTAINER: 
          container.getComponents().add(((ComponentFeature)localObject3).getComponent());
          break;
        default: 
          throw new IllegalArgumentException();
        }
      }
    }
    Object localObject2 = ServiceInterceptorFactory.load(this, Thread.currentThread().getContextClassLoader());
    Object localObject3 = (ServiceInterceptor)container.getSPI(ServiceInterceptor.class);
    if (localObject3 != null) {
      localObject2 = ServiceInterceptor.aggregate(new ServiceInterceptor[] { localObject2, localObject3 });
    }
    serviceInterceptor = ((ServiceInterceptor)localObject2);
    Object localObject6;
    Object localObject5;
    if (paramWSDLService == null)
    {
      Object localObject4;
      if ((paramSource == null) && (paramClass != Service.class))
      {
        localObject4 = (WebServiceClient)AccessController.doPrivileged(new PrivilegedAction()
        {
          public WebServiceClient run()
          {
            return (WebServiceClient)paramClass.getAnnotation(WebServiceClient.class);
          }
        });
        localObject6 = ((WebServiceClient)localObject4).wsdlLocation();
        localObject6 = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName((String)localObject6));
        paramSource = new StreamSource((String)localObject6);
      }
      if (paramSource != null) {
        try
        {
          localObject4 = paramSource.getSystemId() == null ? null : JAXWSUtils.getEncodedURL(paramSource.getSystemId());
          localObject6 = parseWSDL((URL)localObject4, paramSource, paramClass);
          paramWSDLService = ((WSDLModel)localObject6).getService(serviceName);
          if (paramWSDLService == null) {
            throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(serviceName, buildNameList(((WSDLModel)localObject6).getServices().keySet())));
          }
          Iterator localIterator = paramWSDLService.getPorts().iterator();
          while (localIterator.hasNext())
          {
            WSDLPort localWSDLPort = (WSDLPort)localIterator.next();
            ports.put(localWSDLPort.getName(), new PortInfo(this, localWSDLPort));
          }
        }
        catch (MalformedURLException localMalformedURLException)
        {
          throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(paramSource.getSystemId()));
        }
      }
    }
    else
    {
      localObject5 = paramWSDLService.getPorts().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject6 = (WSDLPort)((Iterator)localObject5).next();
        ports.put(((WSDLPort)localObject6).getName(), new PortInfo(this, (WSDLPort)localObject6));
      }
    }
    wsdlService = paramWSDLService;
    if (paramClass != Service.class)
    {
      localObject5 = (HandlerChain)AccessController.doPrivileged(new PrivilegedAction()
      {
        public HandlerChain run()
        {
          return (HandlerChain)paramClass.getAnnotation(HandlerChain.class);
        }
      });
      if (localObject5 != null) {
        handlerConfigurator = new HandlerConfigurator.AnnotationConfigurator(this);
      }
    }
  }
  
  private WSDLModel parseWSDL(URL paramURL, Source paramSource, Class paramClass)
  {
    try
    {
      return RuntimeWSDLParser.parse(paramURL, paramSource, createCatalogResolver(), true, getContainer(), paramClass, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
    catch (SAXException localSAXException)
    {
      throw new WebServiceException(localSAXException);
    }
    catch (ServiceConfigurationError localServiceConfigurationError)
    {
      throw new WebServiceException(localServiceConfigurationError);
    }
  }
  
  protected EntityResolver createCatalogResolver()
  {
    return XmlUtil.createDefaultCatalogResolver();
  }
  
  public Executor getExecutor()
  {
    return executor;
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    executor = paramExecutor;
  }
  
  public HandlerResolver getHandlerResolver()
  {
    return handlerConfigurator.getResolver();
  }
  
  final HandlerConfigurator getHandlerConfigurator()
  {
    return handlerConfigurator;
  }
  
  public void setHandlerResolver(HandlerResolver paramHandlerResolver)
  {
    handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(paramHandlerResolver);
  }
  
  public <T> T getPort(QName paramQName, Class<T> paramClass)
    throws WebServiceException
  {
    return (T)getPort(paramQName, paramClass, EMPTY_FEATURES);
  }
  
  public <T> T getPort(QName paramQName, Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    if ((paramQName == null) || (paramClass == null)) {
      throw new IllegalArgumentException();
    }
    WSDLService localWSDLService = wsdlService;
    if (localWSDLService == null)
    {
      localWSDLService = getWSDLModelfromSEI(paramClass);
      if (localWSDLService == null) {
        throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(paramClass.getName()));
      }
    }
    WSDLPort localWSDLPort = getPortModel(localWSDLService, paramQName);
    return (T)getPort(localWSDLPort.getEPR(), paramQName, paramClass, new WebServiceFeatureList(paramVarArgs));
  }
  
  public <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    return (T)getPort(WSEndpointReference.create(paramEndpointReference), paramClass, paramVarArgs);
  }
  
  public <T> T getPort(WSEndpointReference paramWSEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    WebServiceFeatureList localWebServiceFeatureList = new WebServiceFeatureList(paramVarArgs);
    QName localQName1 = RuntimeModeler.getPortTypeName(paramClass, getMetadadaReader(localWebServiceFeatureList, paramClass.getClassLoader()));
    QName localQName2 = getPortNameFromEPR(paramWSEndpointReference, localQName1);
    return (T)getPort(paramWSEndpointReference, localQName2, paramClass, localWebServiceFeatureList);
  }
  
  protected <T> T getPort(WSEndpointReference paramWSEndpointReference, QName paramQName, Class<T> paramClass, WebServiceFeatureList paramWebServiceFeatureList)
  {
    ComponentFeature localComponentFeature1 = (ComponentFeature)paramWebServiceFeatureList.get(ComponentFeature.class);
    if ((localComponentFeature1 != null) && (!ComponentFeature.Target.STUB.equals(localComponentFeature1.getTarget()))) {
      throw new IllegalArgumentException();
    }
    ComponentsFeature localComponentsFeature = (ComponentsFeature)paramWebServiceFeatureList.get(ComponentsFeature.class);
    if (localComponentsFeature != null)
    {
      localObject = localComponentsFeature.getComponentFeatures().iterator();
      while (((Iterator)localObject).hasNext())
      {
        ComponentFeature localComponentFeature2 = (ComponentFeature)((Iterator)localObject).next();
        if (!ComponentFeature.Target.STUB.equals(localComponentFeature2.getTarget())) {
          throw new IllegalArgumentException();
        }
      }
    }
    paramWebServiceFeatureList.addAll(features);
    Object localObject = addSEI(paramQName, paramClass, paramWebServiceFeatureList);
    return (T)createEndpointIFBaseProxy(paramWSEndpointReference, paramQName, paramClass, paramWebServiceFeatureList, (SEIPortInfo)localObject);
  }
  
  public <T> T getPort(Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    QName localQName1 = RuntimeModeler.getPortTypeName(paramClass, getMetadadaReader(new WebServiceFeatureList(paramVarArgs), paramClass.getClassLoader()));
    WSDLService localWSDLService = wsdlService;
    if (localWSDLService == null)
    {
      localWSDLService = getWSDLModelfromSEI(paramClass);
      if (localWSDLService == null) {
        throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(paramClass.getName()));
      }
    }
    WSDLPort localWSDLPort = localWSDLService.getMatchingPort(localQName1);
    if (localWSDLPort == null) {
      throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(localQName1));
    }
    QName localQName2 = localWSDLPort.getName();
    return (T)getPort(localQName2, paramClass, paramVarArgs);
  }
  
  public <T> T getPort(Class<T> paramClass)
    throws WebServiceException
  {
    return (T)getPort(paramClass, EMPTY_FEATURES);
  }
  
  public void addPort(QName paramQName, String paramString1, String paramString2)
    throws WebServiceException
  {
    if (!ports.containsKey(paramQName))
    {
      BindingID localBindingID = paramString1 == null ? BindingID.SOAP11_HTTP : BindingID.parse(paramString1);
      ports.put(paramQName, new PortInfo(this, paramString2 == null ? null : EndpointAddress.create(paramString2), paramQName, localBindingID));
    }
    else
    {
      throw new WebServiceException(DispatchMessages.DUPLICATE_PORT(paramQName.toString()));
    }
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode)
    throws WebServiceException
  {
    return createDispatch(paramQName, paramClass, paramMode, EMPTY_FEATURES);
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return createDispatch(paramQName, paramWSEndpointReference, paramClass, paramMode, new WebServiceFeatureList(paramVarArgs));
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList)
  {
    PortInfo localPortInfo = safeGetPort(paramQName);
    ComponentFeature localComponentFeature = (ComponentFeature)paramWebServiceFeatureList.get(ComponentFeature.class);
    if ((localComponentFeature != null) && (!ComponentFeature.Target.STUB.equals(localComponentFeature.getTarget()))) {
      throw new IllegalArgumentException();
    }
    ComponentsFeature localComponentsFeature = (ComponentsFeature)paramWebServiceFeatureList.get(ComponentsFeature.class);
    if (localComponentsFeature != null)
    {
      localObject1 = localComponentsFeature.getComponentFeatures().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ComponentFeature)((Iterator)localObject1).next();
        if (!ComponentFeature.Target.STUB.equals(((ComponentFeature)localObject2).getTarget())) {
          throw new IllegalArgumentException();
        }
      }
    }
    paramWebServiceFeatureList.addAll(features);
    Object localObject1 = localPortInfo.createBinding(paramWebServiceFeatureList, null, null);
    ((BindingImpl)localObject1).setMode(paramMode);
    Object localObject2 = Stubs.createDispatch(localPortInfo, this, (WSBinding)localObject1, paramClass, paramMode, paramWSEndpointReference);
    serviceInterceptor.postCreateDispatch((WSBindingProvider)localObject2);
    return (Dispatch<T>)localObject2;
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return createDispatch(paramQName, paramClass, paramMode, new WebServiceFeatureList(paramVarArgs));
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList)
  {
    WSEndpointReference localWSEndpointReference = null;
    int i = 0;
    AddressingFeature localAddressingFeature = (AddressingFeature)paramWebServiceFeatureList.get(AddressingFeature.class);
    if (localAddressingFeature == null) {
      localAddressingFeature = (AddressingFeature)features.get(AddressingFeature.class);
    }
    if ((localAddressingFeature != null) && (localAddressingFeature.isEnabled())) {
      i = 1;
    }
    MemberSubmissionAddressingFeature localMemberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)paramWebServiceFeatureList.get(MemberSubmissionAddressingFeature.class);
    if (localMemberSubmissionAddressingFeature == null) {
      localMemberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)features.get(MemberSubmissionAddressingFeature.class);
    }
    if ((localMemberSubmissionAddressingFeature != null) && (localMemberSubmissionAddressingFeature.isEnabled())) {
      i = 1;
    }
    if ((i != 0) && (wsdlService != null) && (wsdlService.get(paramQName) != null)) {
      localWSEndpointReference = wsdlService.get(paramQName).getEPR();
    }
    return createDispatch(paramQName, localWSEndpointReference, paramClass, paramMode, paramWebServiceFeatureList);
  }
  
  public <T> Dispatch<T> createDispatch(EndpointReference paramEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    WSEndpointReference localWSEndpointReference = new WSEndpointReference(paramEndpointReference);
    QName localQName = addPortEpr(localWSEndpointReference);
    return createDispatch(localQName, localWSEndpointReference, paramClass, paramMode, paramVarArgs);
  }
  
  @NotNull
  public PortInfo safeGetPort(QName paramQName)
  {
    PortInfo localPortInfo = (PortInfo)ports.get(paramQName);
    if (localPortInfo == null) {
      throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(paramQName, buildNameList(ports.keySet())));
    }
    return localPortInfo;
  }
  
  private StringBuilder buildNameList(Collection<QName> paramCollection)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      QName localQName = (QName)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(',');
      }
      localStringBuilder.append(localQName);
    }
    return localStringBuilder;
  }
  
  public EndpointAddress getEndpointAddress(QName paramQName)
  {
    PortInfo localPortInfo = (PortInfo)ports.get(paramQName);
    return localPortInfo != null ? targetEndpoint : null;
  }
  
  public Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode)
    throws WebServiceException
  {
    return createDispatch(paramQName, paramJAXBContext, paramMode, EMPTY_FEATURES);
  }
  
  public Dispatch<Object> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return createDispatch(paramQName, paramWSEndpointReference, paramJAXBContext, paramMode, new WebServiceFeatureList(paramVarArgs));
  }
  
  protected Dispatch<Object> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList)
  {
    PortInfo localPortInfo = safeGetPort(paramQName);
    ComponentFeature localComponentFeature = (ComponentFeature)paramWebServiceFeatureList.get(ComponentFeature.class);
    if ((localComponentFeature != null) && (!ComponentFeature.Target.STUB.equals(localComponentFeature.getTarget()))) {
      throw new IllegalArgumentException();
    }
    ComponentsFeature localComponentsFeature = (ComponentsFeature)paramWebServiceFeatureList.get(ComponentsFeature.class);
    if (localComponentsFeature != null)
    {
      localObject1 = localComponentsFeature.getComponentFeatures().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ComponentFeature)((Iterator)localObject1).next();
        if (!ComponentFeature.Target.STUB.equals(((ComponentFeature)localObject2).getTarget())) {
          throw new IllegalArgumentException();
        }
      }
    }
    paramWebServiceFeatureList.addAll(features);
    Object localObject1 = localPortInfo.createBinding(paramWebServiceFeatureList, null, null);
    ((BindingImpl)localObject1).setMode(paramMode);
    Object localObject2 = Stubs.createJAXBDispatch(localPortInfo, (WSBinding)localObject1, paramJAXBContext, paramMode, paramWSEndpointReference);
    serviceInterceptor.postCreateDispatch((WSBindingProvider)localObject2);
    return (Dispatch<Object>)localObject2;
  }
  
  @NotNull
  public Container getContainer()
  {
    return container;
  }
  
  public Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return createDispatch(paramQName, paramJAXBContext, paramMode, new WebServiceFeatureList(paramVarArgs));
  }
  
  protected Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeatureList paramWebServiceFeatureList)
  {
    WSEndpointReference localWSEndpointReference = null;
    int i = 0;
    AddressingFeature localAddressingFeature = (AddressingFeature)paramWebServiceFeatureList.get(AddressingFeature.class);
    if (localAddressingFeature == null) {
      localAddressingFeature = (AddressingFeature)features.get(AddressingFeature.class);
    }
    if ((localAddressingFeature != null) && (localAddressingFeature.isEnabled())) {
      i = 1;
    }
    MemberSubmissionAddressingFeature localMemberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)paramWebServiceFeatureList.get(MemberSubmissionAddressingFeature.class);
    if (localMemberSubmissionAddressingFeature == null) {
      localMemberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)features.get(MemberSubmissionAddressingFeature.class);
    }
    if ((localMemberSubmissionAddressingFeature != null) && (localMemberSubmissionAddressingFeature.isEnabled())) {
      i = 1;
    }
    if ((i != 0) && (wsdlService != null) && (wsdlService.get(paramQName) != null)) {
      localWSEndpointReference = wsdlService.get(paramQName).getEPR();
    }
    return createDispatch(paramQName, localWSEndpointReference, paramJAXBContext, paramMode, paramWebServiceFeatureList);
  }
  
  public Dispatch<Object> createDispatch(EndpointReference paramEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    WSEndpointReference localWSEndpointReference = new WSEndpointReference(paramEndpointReference);
    QName localQName = addPortEpr(localWSEndpointReference);
    return createDispatch(localQName, localWSEndpointReference, paramJAXBContext, paramMode, paramVarArgs);
  }
  
  private QName addPortEpr(WSEndpointReference paramWSEndpointReference)
  {
    if (paramWSEndpointReference == null) {
      throw new WebServiceException(ProviderApiMessages.NULL_EPR());
    }
    QName localQName = getPortNameFromEPR(paramWSEndpointReference, null);
    PortInfo localPortInfo = new PortInfo(this, paramWSEndpointReference.getAddress() == null ? null : EndpointAddress.create(paramWSEndpointReference.getAddress()), localQName, getPortModel(wsdlService, localQName).getBinding().getBindingId());
    if (!ports.containsKey(localQName)) {
      ports.put(localQName, localPortInfo);
    }
    return localQName;
  }
  
  private QName getPortNameFromEPR(@NotNull WSEndpointReference paramWSEndpointReference, @Nullable QName paramQName)
  {
    WSEndpointReference.Metadata localMetadata = paramWSEndpointReference.getMetaData();
    QName localQName2 = localMetadata.getServiceName();
    QName localQName3 = localMetadata.getPortName();
    if ((localQName2 != null) && (!localQName2.equals(serviceName))) {
      throw new WebServiceException("EndpointReference WSDL ServiceName differs from Service Instance WSDL Service QName.\n The two Service QNames must match");
    }
    Object localObject;
    if (wsdlService == null)
    {
      localObject = localMetadata.getWsdlSource();
      if (localObject == null) {
        throw new WebServiceException(ProviderApiMessages.NULL_WSDL());
      }
      try
      {
        WSDLModel localWSDLModel = parseWSDL(new URL(paramWSEndpointReference.getAddress()), (Source)localObject, null);
        wsdlService = localWSDLModel.getService(serviceName);
        if (wsdlService == null) {
          throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(serviceName, buildNameList(localWSDLModel.getServices().keySet())));
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new WebServiceException(ClientMessages.INVALID_ADDRESS(paramWSEndpointReference.getAddress()));
      }
    }
    QName localQName1 = localQName3;
    if ((localQName1 == null) && (paramQName != null))
    {
      localObject = wsdlService.getMatchingPort(paramQName);
      if (localObject == null) {
        throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(paramQName));
      }
      localQName1 = ((WSDLPort)localObject).getName();
    }
    if (localQName1 == null) {
      throw new WebServiceException(ProviderApiMessages.NULL_PORTNAME());
    }
    if (wsdlService.get(localQName1) == null) {
      throw new WebServiceException(ClientMessages.INVALID_EPR_PORT_NAME(localQName1, buildWsdlPortNames()));
    }
    return localQName1;
  }
  
  private <T> T createProxy(final Class<T> paramClass, final InvocationHandler paramInvocationHandler)
  {
    final ClassLoader localClassLoader = getDelegatingLoader(paramClass.getClassLoader(), WSServiceDelegate.class.getClassLoader());
    RuntimePermission localRuntimePermission = new RuntimePermission("accessClassInPackage.com.sun.xml.internal.*");
    PermissionCollection localPermissionCollection = localRuntimePermission.newPermissionCollection();
    localPermissionCollection.add(localRuntimePermission);
    (T)AccessController.doPrivileged(new PrivilegedAction()new AccessControlContextnew ProtectionDomain
    {
      public T run()
      {
        Object localObject = Proxy.newProxyInstance(localClassLoader, new Class[] { paramClass, WSBindingProvider.class, Closeable.class }, paramInvocationHandler);
        return (T)paramClass.cast(localObject);
      }
    }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, localPermissionCollection) }));
  }
  
  private WSDLService getWSDLModelfromSEI(final Class paramClass)
  {
    WebService localWebService = (WebService)AccessController.doPrivileged(new PrivilegedAction()
    {
      public WebService run()
      {
        return (WebService)paramClass.getAnnotation(WebService.class);
      }
    });
    if ((localWebService == null) || (localWebService.wsdlLocation().equals(""))) {
      return null;
    }
    String str = localWebService.wsdlLocation();
    str = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(str));
    StreamSource localStreamSource = new StreamSource(str);
    WSDLService localWSDLService = null;
    try
    {
      URL localURL = localStreamSource.getSystemId() == null ? null : new URL(localStreamSource.getSystemId());
      WSDLModel localWSDLModel = parseWSDL(localURL, localStreamSource, paramClass);
      localWSDLService = localWSDLModel.getService(serviceName);
      if (localWSDLService == null) {
        throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(serviceName, buildNameList(localWSDLModel.getServices().keySet())));
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(localStreamSource.getSystemId()));
    }
    return localWSDLService;
  }
  
  public QName getServiceName()
  {
    return serviceName;
  }
  
  public Class getServiceClass()
  {
    return serviceClass;
  }
  
  public Iterator<QName> getPorts()
    throws WebServiceException
  {
    return ports.keySet().iterator();
  }
  
  public URL getWSDLDocumentLocation()
  {
    if (wsdlService == null) {
      return null;
    }
    try
    {
      return new URL(wsdlService.getParent().getLocation().getSystemId());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new AssertionError(localMalformedURLException);
    }
  }
  
  private <T> T createEndpointIFBaseProxy(@Nullable WSEndpointReference paramWSEndpointReference, QName paramQName, Class<T> paramClass, WebServiceFeatureList paramWebServiceFeatureList, SEIPortInfo paramSEIPortInfo)
  {
    if (wsdlService == null) {
      throw new WebServiceException(ClientMessages.INVALID_SERVICE_NO_WSDL(serviceName));
    }
    if (wsdlService.get(paramQName) == null) {
      throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(paramQName, buildWsdlPortNames()));
    }
    BindingImpl localBindingImpl = paramSEIPortInfo.createBinding(paramWebServiceFeatureList, paramClass);
    InvocationHandler localInvocationHandler = getStubHandler(localBindingImpl, paramSEIPortInfo, paramWSEndpointReference);
    Object localObject = createProxy(paramClass, localInvocationHandler);
    if (serviceInterceptor != null) {
      serviceInterceptor.postCreateProxy((WSBindingProvider)localObject, paramClass);
    }
    return (T)localObject;
  }
  
  protected InvocationHandler getStubHandler(BindingImpl paramBindingImpl, SEIPortInfo paramSEIPortInfo, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    return new SEIStub(paramSEIPortInfo, paramBindingImpl, model, paramWSEndpointReference);
  }
  
  private StringBuilder buildWsdlPortNames()
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = wsdlService.getPorts().iterator();
    while (localIterator.hasNext())
    {
      WSDLPort localWSDLPort = (WSDLPort)localIterator.next();
      localHashSet.add(localWSDLPort.getName());
    }
    return buildNameList(localHashSet);
  }
  
  @NotNull
  public WSDLPort getPortModel(WSDLService paramWSDLService, QName paramQName)
  {
    WSDLPort localWSDLPort = paramWSDLService.get(paramQName);
    if (localWSDLPort == null) {
      throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(paramQName, buildWsdlPortNames()));
    }
    return localWSDLPort;
  }
  
  private SEIPortInfo addSEI(QName paramQName, Class paramClass, WebServiceFeatureList paramWebServiceFeatureList)
    throws WebServiceException
  {
    boolean bool = useOwnSEIModel(paramWebServiceFeatureList);
    if (bool) {
      return createSEIPortInfo(paramQName, paramClass, paramWebServiceFeatureList);
    }
    SEIPortInfo localSEIPortInfo = (SEIPortInfo)seiContext.get(paramQName);
    if (localSEIPortInfo == null)
    {
      localSEIPortInfo = createSEIPortInfo(paramQName, paramClass, paramWebServiceFeatureList);
      seiContext.put(portName, localSEIPortInfo);
      ports.put(portName, localSEIPortInfo);
    }
    return localSEIPortInfo;
  }
  
  public SEIModel buildRuntimeModel(QName paramQName1, QName paramQName2, Class paramClass, WSDLPort paramWSDLPort, WebServiceFeatureList paramWebServiceFeatureList)
  {
    DatabindingFactory localDatabindingFactory = DatabindingFactory.newInstance();
    DatabindingConfig localDatabindingConfig = new DatabindingConfig();
    localDatabindingConfig.setContractClass(paramClass);
    localDatabindingConfig.getMappingInfo().setServiceName(paramQName1);
    localDatabindingConfig.setWsdlPort(paramWSDLPort);
    localDatabindingConfig.setFeatures(paramWebServiceFeatureList);
    localDatabindingConfig.setClassLoader(paramClass.getClassLoader());
    localDatabindingConfig.getMappingInfo().setPortName(paramQName2);
    localDatabindingConfig.setWsdlURL(wsdlURL);
    localDatabindingConfig.setMetadataReader(getMetadadaReader(paramWebServiceFeatureList, paramClass.getClassLoader()));
    DatabindingImpl localDatabindingImpl = (DatabindingImpl)localDatabindingFactory.createRuntime(localDatabindingConfig);
    return localDatabindingImpl.getModel();
  }
  
  private MetadataReader getMetadadaReader(WebServiceFeatureList paramWebServiceFeatureList, ClassLoader paramClassLoader)
  {
    if (paramWebServiceFeatureList == null) {
      return null;
    }
    ExternalMetadataFeature localExternalMetadataFeature = (ExternalMetadataFeature)paramWebServiceFeatureList.get(ExternalMetadataFeature.class);
    if (localExternalMetadataFeature != null) {
      return localExternalMetadataFeature.getMetadataReader(paramClassLoader, false);
    }
    return null;
  }
  
  private SEIPortInfo createSEIPortInfo(QName paramQName, Class paramClass, WebServiceFeatureList paramWebServiceFeatureList)
  {
    WSDLPort localWSDLPort = getPortModel(wsdlService, paramQName);
    SEIModel localSEIModel = buildRuntimeModel(serviceName, paramQName, paramClass, localWSDLPort, paramWebServiceFeatureList);
    return new SEIPortInfo(this, paramClass, (SOAPSEIModel)localSEIModel, localWSDLPort);
  }
  
  private boolean useOwnSEIModel(WebServiceFeatureList paramWebServiceFeatureList)
  {
    return paramWebServiceFeatureList.contains(UsesJAXBContextFeature.class);
  }
  
  public WSDLService getWsdlService()
  {
    return wsdlService;
  }
  
  private static ClassLoader getDelegatingLoader(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
  {
    if (paramClassLoader1 == null) {
      return paramClassLoader2;
    }
    if (paramClassLoader2 == null) {
      return paramClassLoader1;
    }
    return new DelegatingLoader(paramClassLoader1, paramClassLoader2);
  }
  
  static class DaemonThreadFactory
    implements ThreadFactory
  {
    DaemonThreadFactory() {}
    
    public Thread newThread(Runnable paramRunnable)
    {
      Thread localThread = new Thread(paramRunnable);
      localThread.setDaemon(Boolean.TRUE.booleanValue());
      return localThread;
    }
  }
  
  private static final class DelegatingLoader
    extends ClassLoader
  {
    private final ClassLoader loader;
    
    public int hashCode()
    {
      int i = 31;
      int j = 1;
      j = 31 * j + (loader == null ? 0 : loader.hashCode());
      j = 31 * j + (getParent() == null ? 0 : getParent().hashCode());
      return j;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      DelegatingLoader localDelegatingLoader = (DelegatingLoader)paramObject;
      if (loader == null)
      {
        if (loader != null) {
          return false;
        }
      }
      else if (!loader.equals(loader)) {
        return false;
      }
      if (getParent() == null)
      {
        if (localDelegatingLoader.getParent() != null) {
          return false;
        }
      }
      else if (!getParent().equals(localDelegatingLoader.getParent())) {
        return false;
      }
      return true;
    }
    
    DelegatingLoader(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2)
    {
      super();
      loader = paramClassLoader1;
    }
    
    protected Class findClass(String paramString)
      throws ClassNotFoundException
    {
      return loader.loadClass(paramString);
    }
    
    protected URL findResource(String paramString)
    {
      return loader.getResource(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\WSServiceDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */