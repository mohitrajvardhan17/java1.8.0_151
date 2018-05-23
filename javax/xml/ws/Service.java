package javax.xml.ws;

import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Executor;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;

public class Service
{
  private ServiceDelegate delegate;
  
  protected Service(URL paramURL, QName paramQName)
  {
    delegate = Provider.provider().createServiceDelegate(paramURL, paramQName, getClass());
  }
  
  protected Service(URL paramURL, QName paramQName, WebServiceFeature... paramVarArgs)
  {
    delegate = Provider.provider().createServiceDelegate(paramURL, paramQName, getClass(), paramVarArgs);
  }
  
  public <T> T getPort(QName paramQName, Class<T> paramClass)
  {
    return (T)delegate.getPort(paramQName, paramClass);
  }
  
  public <T> T getPort(QName paramQName, Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    return (T)delegate.getPort(paramQName, paramClass, paramVarArgs);
  }
  
  public <T> T getPort(Class<T> paramClass)
  {
    return (T)delegate.getPort(paramClass);
  }
  
  public <T> T getPort(Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    return (T)delegate.getPort(paramClass, paramVarArgs);
  }
  
  public <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    return (T)delegate.getPort(paramEndpointReference, paramClass, paramVarArgs);
  }
  
  public void addPort(QName paramQName, String paramString1, String paramString2)
  {
    delegate.addPort(paramQName, paramString1, paramString2);
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Mode paramMode)
  {
    return delegate.createDispatch(paramQName, paramClass, paramMode);
  }
  
  public <T> Dispatch<T> createDispatch(QName paramQName, Class<T> paramClass, Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return delegate.createDispatch(paramQName, paramClass, paramMode, paramVarArgs);
  }
  
  public <T> Dispatch<T> createDispatch(EndpointReference paramEndpointReference, Class<T> paramClass, Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return delegate.createDispatch(paramEndpointReference, paramClass, paramMode, paramVarArgs);
  }
  
  public Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Mode paramMode)
  {
    return delegate.createDispatch(paramQName, paramJAXBContext, paramMode);
  }
  
  public Dispatch<Object> createDispatch(QName paramQName, JAXBContext paramJAXBContext, Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return delegate.createDispatch(paramQName, paramJAXBContext, paramMode, paramVarArgs);
  }
  
  public Dispatch<Object> createDispatch(EndpointReference paramEndpointReference, JAXBContext paramJAXBContext, Mode paramMode, WebServiceFeature... paramVarArgs)
  {
    return delegate.createDispatch(paramEndpointReference, paramJAXBContext, paramMode, paramVarArgs);
  }
  
  public QName getServiceName()
  {
    return delegate.getServiceName();
  }
  
  public Iterator<QName> getPorts()
  {
    return delegate.getPorts();
  }
  
  public URL getWSDLDocumentLocation()
  {
    return delegate.getWSDLDocumentLocation();
  }
  
  public HandlerResolver getHandlerResolver()
  {
    return delegate.getHandlerResolver();
  }
  
  public void setHandlerResolver(HandlerResolver paramHandlerResolver)
  {
    delegate.setHandlerResolver(paramHandlerResolver);
  }
  
  public Executor getExecutor()
  {
    return delegate.getExecutor();
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    delegate.setExecutor(paramExecutor);
  }
  
  public static Service create(URL paramURL, QName paramQName)
  {
    return new Service(paramURL, paramQName);
  }
  
  public static Service create(URL paramURL, QName paramQName, WebServiceFeature... paramVarArgs)
  {
    return new Service(paramURL, paramQName, paramVarArgs);
  }
  
  public static Service create(QName paramQName)
  {
    return new Service(null, paramQName);
  }
  
  public static Service create(QName paramQName, WebServiceFeature... paramVarArgs)
  {
    return new Service(null, paramQName, paramVarArgs);
  }
  
  public static enum Mode
  {
    MESSAGE,  PAYLOAD;
    
    private Mode() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */