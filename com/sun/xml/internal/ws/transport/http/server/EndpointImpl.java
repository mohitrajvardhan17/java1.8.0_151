package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.ws.Binding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointContext;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServicePermission;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

public class EndpointImpl
  extends Endpoint
{
  private static final WebServicePermission ENDPOINT_PUBLISH_PERMISSION = new WebServicePermission("publishEndpoint");
  private Object actualEndpoint;
  private final WSBinding binding;
  @Nullable
  private final Object implementor;
  private List<Source> metadata;
  private Executor executor;
  private Map<String, Object> properties = Collections.emptyMap();
  private boolean stopped;
  @Nullable
  private EndpointContext endpointContext;
  @NotNull
  private final Class<?> implClass;
  private final com.sun.xml.internal.ws.api.server.Invoker invoker;
  private Container container;
  
  public EndpointImpl(@NotNull BindingID paramBindingID, @NotNull Object paramObject, WebServiceFeature... paramVarArgs)
  {
    this(paramBindingID, paramObject, paramObject.getClass(), InstanceResolver.createSingleton(paramObject).createInvoker(), paramVarArgs);
  }
  
  public EndpointImpl(@NotNull BindingID paramBindingID, @NotNull Class paramClass, javax.xml.ws.spi.Invoker paramInvoker, WebServiceFeature... paramVarArgs)
  {
    this(paramBindingID, null, paramClass, new InvokerImpl(paramInvoker), paramVarArgs);
  }
  
  private EndpointImpl(@NotNull BindingID paramBindingID, Object paramObject, @NotNull Class paramClass, com.sun.xml.internal.ws.api.server.Invoker paramInvoker, WebServiceFeature... paramVarArgs)
  {
    binding = BindingImpl.create(paramBindingID, paramVarArgs);
    implClass = paramClass;
    invoker = paramInvoker;
    implementor = paramObject;
  }
  
  /**
   * @deprecated
   */
  public EndpointImpl(WSEndpoint paramWSEndpoint, Object paramObject)
  {
    this(paramWSEndpoint, paramObject, null);
  }
  
  /**
   * @deprecated
   */
  public EndpointImpl(WSEndpoint paramWSEndpoint, Object paramObject, EndpointContext paramEndpointContext)
  {
    endpointContext = paramEndpointContext;
    actualEndpoint = new HttpEndpoint(null, getAdapter(paramWSEndpoint, ""));
    ((HttpEndpoint)actualEndpoint).publish(paramObject);
    binding = paramWSEndpoint.getBinding();
    implementor = null;
    implClass = null;
    invoker = null;
  }
  
  /**
   * @deprecated
   */
  public EndpointImpl(WSEndpoint paramWSEndpoint, String paramString)
  {
    this(paramWSEndpoint, paramString, null);
  }
  
  /**
   * @deprecated
   */
  public EndpointImpl(WSEndpoint paramWSEndpoint, String paramString, EndpointContext paramEndpointContext)
  {
    URL localURL;
    try
    {
      localURL = new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new IllegalArgumentException("Cannot create URL for this address " + paramString);
    }
    if (!localURL.getProtocol().equals("http")) {
      throw new IllegalArgumentException(localURL.getProtocol() + " protocol based address is not supported");
    }
    if (!localURL.getPath().startsWith("/")) {
      throw new IllegalArgumentException("Incorrect WebService address=" + paramString + ". The address's path should start with /");
    }
    endpointContext = paramEndpointContext;
    actualEndpoint = new HttpEndpoint(null, getAdapter(paramWSEndpoint, localURL.getPath()));
    ((HttpEndpoint)actualEndpoint).publish(paramString);
    binding = paramWSEndpoint.getBinding();
    implementor = null;
    implClass = null;
    invoker = null;
  }
  
  public Binding getBinding()
  {
    return binding;
  }
  
  public Object getImplementor()
  {
    return implementor;
  }
  
  public void publish(String paramString)
  {
    canPublish();
    URL localURL;
    try
    {
      localURL = new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new IllegalArgumentException("Cannot create URL for this address " + paramString);
    }
    if (!localURL.getProtocol().equals("http")) {
      throw new IllegalArgumentException(localURL.getProtocol() + " protocol based address is not supported");
    }
    if (!localURL.getPath().startsWith("/")) {
      throw new IllegalArgumentException("Incorrect WebService address=" + paramString + ". The address's path should start with /");
    }
    createEndpoint(localURL.getPath());
    ((HttpEndpoint)actualEndpoint).publish(paramString);
  }
  
  public void publish(Object paramObject)
  {
    canPublish();
    if (!com.sun.net.httpserver.HttpContext.class.isAssignableFrom(paramObject.getClass())) {
      throw new IllegalArgumentException(paramObject.getClass() + " is not a supported context.");
    }
    createEndpoint(((com.sun.net.httpserver.HttpContext)paramObject).getPath());
    ((HttpEndpoint)actualEndpoint).publish(paramObject);
  }
  
  public void publish(javax.xml.ws.spi.http.HttpContext paramHttpContext)
  {
    canPublish();
    createEndpoint(paramHttpContext.getPath());
    ((HttpEndpoint)actualEndpoint).publish(paramHttpContext);
  }
  
  public void stop()
  {
    if (isPublished())
    {
      ((HttpEndpoint)actualEndpoint).stop();
      actualEndpoint = null;
      stopped = true;
    }
  }
  
  public boolean isPublished()
  {
    return actualEndpoint != null;
  }
  
  public List<Source> getMetadata()
  {
    return metadata;
  }
  
  public void setMetadata(List<Source> paramList)
  {
    if (isPublished()) {
      throw new IllegalStateException("Cannot set Metadata. Endpoint is already published");
    }
    metadata = paramList;
  }
  
  public Executor getExecutor()
  {
    return executor;
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    executor = paramExecutor;
  }
  
  public Map<String, Object> getProperties()
  {
    return new HashMap(properties);
  }
  
  public void setProperties(Map<String, Object> paramMap)
  {
    properties = new HashMap(paramMap);
  }
  
  private void createEndpoint(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(ENDPOINT_PUBLISH_PERMISSION);
    }
    try
    {
      Class.forName("com.sun.net.httpserver.HttpServer");
    }
    catch (Exception localException)
    {
      throw new UnsupportedOperationException("Couldn't load light weight http server", localException);
    }
    container = getContainer();
    MetadataReader localMetadataReader = EndpointFactory.getExternalMetadatReader(implClass, binding);
    WSEndpoint localWSEndpoint = WSEndpoint.create(implClass, true, invoker, (QName)getProperty(QName.class, "javax.xml.ws.wsdl.service"), (QName)getProperty(QName.class, "javax.xml.ws.wsdl.port"), container, binding, getPrimaryWsdl(localMetadataReader), buildDocList(), (EntityResolver)null, false);
    actualEndpoint = new HttpEndpoint(executor, getAdapter(localWSEndpoint, paramString));
  }
  
  private <T> T getProperty(Class<T> paramClass, String paramString)
  {
    Object localObject = properties.get(paramString);
    if (localObject == null) {
      return null;
    }
    if (paramClass.isInstance(localObject)) {
      return (T)paramClass.cast(localObject);
    }
    throw new IllegalArgumentException("Property " + paramString + " has to be of type " + paramClass);
  }
  
  private List<SDDocumentSource> buildDocList()
  {
    ArrayList localArrayList = new ArrayList();
    if (metadata != null)
    {
      Iterator localIterator = metadata.iterator();
      while (localIterator.hasNext())
      {
        Source localSource = (Source)localIterator.next();
        try
        {
          XMLStreamBufferResult localXMLStreamBufferResult = (XMLStreamBufferResult)XmlUtil.identityTransform(localSource, new XMLStreamBufferResult());
          String str = localSource.getSystemId();
          localArrayList.add(SDDocumentSource.create(new URL(str), localXMLStreamBufferResult.getXMLStreamBuffer()));
        }
        catch (TransformerException localTransformerException)
        {
          throw new ServerRtException("server.rt.err", new Object[] { localTransformerException });
        }
        catch (IOException localIOException)
        {
          throw new ServerRtException("server.rt.err", new Object[] { localIOException });
        }
        catch (SAXException localSAXException)
        {
          throw new ServerRtException("server.rt.err", new Object[] { localSAXException });
        }
        catch (ParserConfigurationException localParserConfigurationException)
        {
          throw new ServerRtException("server.rt.err", new Object[] { localParserConfigurationException });
        }
      }
    }
    return localArrayList;
  }
  
  @Nullable
  private SDDocumentSource getPrimaryWsdl(MetadataReader paramMetadataReader)
  {
    EndpointFactory.verifyImplementorClass(implClass, paramMetadataReader);
    String str = EndpointFactory.getWsdlLocation(implClass, paramMetadataReader);
    if (str != null)
    {
      ClassLoader localClassLoader = implClass.getClassLoader();
      URL localURL = localClassLoader.getResource(str);
      if (localURL != null) {
        return SDDocumentSource.create(localURL);
      }
      throw new ServerRtException("cannot.load.wsdl", new Object[] { str });
    }
    return null;
  }
  
  private void canPublish()
  {
    if (isPublished()) {
      throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already published.");
    }
    if (stopped) {
      throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already stopped.");
    }
  }
  
  public EndpointReference getEndpointReference(Element... paramVarArgs)
  {
    return getEndpointReference(W3CEndpointReference.class, paramVarArgs);
  }
  
  public <T extends EndpointReference> T getEndpointReference(Class<T> paramClass, Element... paramVarArgs)
  {
    if (!isPublished()) {
      throw new WebServiceException("Endpoint is not published yet");
    }
    return ((HttpEndpoint)actualEndpoint).getEndpointReference(paramClass, paramVarArgs);
  }
  
  public void setEndpointContext(EndpointContext paramEndpointContext)
  {
    endpointContext = paramEndpointContext;
  }
  
  private HttpAdapter getAdapter(WSEndpoint paramWSEndpoint, String paramString)
  {
    Object localObject = null;
    if (endpointContext != null)
    {
      if ((endpointContext instanceof Component)) {
        localObject = (HttpAdapterList)((Component)endpointContext).getSPI(HttpAdapterList.class);
      }
      if (localObject == null)
      {
        Iterator localIterator = endpointContext.getEndpoints().iterator();
        while (localIterator.hasNext())
        {
          Endpoint localEndpoint = (Endpoint)localIterator.next();
          if ((localEndpoint.isPublished()) && (localEndpoint != this))
          {
            localObject = ((HttpEndpoint)actualEndpoint).getAdapterOwner();
            if (($assertionsDisabled) || (localObject != null)) {
              break;
            }
            throw new AssertionError();
          }
        }
      }
    }
    if (localObject == null) {
      localObject = new ServerAdapterList();
    }
    return ((HttpAdapterList)localObject).createAdapter("", paramString, paramWSEndpoint);
  }
  
  private Container getContainer()
  {
    if (endpointContext != null)
    {
      if ((endpointContext instanceof Component))
      {
        localObject = (Container)((Component)endpointContext).getSPI(Container.class);
        if (localObject != null) {
          return (Container)localObject;
        }
      }
      Object localObject = endpointContext.getEndpoints().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Endpoint localEndpoint = (Endpoint)((Iterator)localObject).next();
        if ((localEndpoint.isPublished()) && (localEndpoint != this)) {
          return container;
        }
      }
    }
    return new ServerContainer();
  }
  
  private static class InvokerImpl
    extends com.sun.xml.internal.ws.api.server.Invoker
  {
    private javax.xml.ws.spi.Invoker spiInvoker;
    
    InvokerImpl(javax.xml.ws.spi.Invoker paramInvoker)
    {
      spiInvoker = paramInvoker;
    }
    
    public void start(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull WSEndpoint paramWSEndpoint)
    {
      try
      {
        spiInvoker.inject(paramWSWebServiceContext);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new WebServiceException(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new WebServiceException(localInvocationTargetException);
      }
    }
    
    public Object invoke(@NotNull Packet paramPacket, @NotNull Method paramMethod, @NotNull Object... paramVarArgs)
      throws InvocationTargetException, IllegalAccessException
    {
      return spiInvoker.invoke(paramMethod, paramVarArgs);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\EndpointImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */