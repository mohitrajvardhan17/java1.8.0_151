package com.sun.xml.internal.ws.spi;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.ServiceSharedFeatureMarker;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.Metadata;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import com.sun.xml.internal.ws.transport.http.server.EndpointImpl;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public class ProviderImpl
  extends Provider
{
  private static final ContextClassloaderLocal<JAXBContext> eprjc = new ContextClassloaderLocal()
  {
    protected JAXBContext initialValue()
      throws Exception
    {
      return ProviderImpl.access$000();
    }
  };
  public static final ProviderImpl INSTANCE = new ProviderImpl();
  
  public ProviderImpl() {}
  
  public Endpoint createEndpoint(String paramString, Object paramObject)
  {
    return new EndpointImpl(paramString != null ? BindingID.parse(paramString) : BindingID.parse(paramObject.getClass()), paramObject, new WebServiceFeature[0]);
  }
  
  public ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class paramClass)
  {
    return new WSServiceDelegate(paramURL, paramQName, paramClass, new WebServiceFeature[0]);
  }
  
  public ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class paramClass, WebServiceFeature... paramVarArgs)
  {
    for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
      if (!(localWebServiceFeature instanceof ServiceSharedFeatureMarker)) {
        throw new WebServiceException("Doesn't support any Service specific features");
      }
    }
    return new WSServiceDelegate(paramURL, paramQName, paramClass, paramVarArgs);
  }
  
  public ServiceDelegate createServiceDelegate(Source paramSource, QName paramQName, Class paramClass)
  {
    return new WSServiceDelegate(paramSource, paramQName, paramClass, new WebServiceFeature[0]);
  }
  
  public Endpoint createAndPublishEndpoint(String paramString, Object paramObject)
  {
    EndpointImpl localEndpointImpl = new EndpointImpl(BindingID.parse(paramObject.getClass()), paramObject, new WebServiceFeature[0]);
    localEndpointImpl.publish(paramString);
    return localEndpointImpl;
  }
  
  public Endpoint createEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs)
  {
    return new EndpointImpl(paramString != null ? BindingID.parse(paramString) : BindingID.parse(paramObject.getClass()), paramObject, paramVarArgs);
  }
  
  public Endpoint createAndPublishEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs)
  {
    EndpointImpl localEndpointImpl = new EndpointImpl(BindingID.parse(paramObject.getClass()), paramObject, paramVarArgs);
    localEndpointImpl.publish(paramString);
    return localEndpointImpl;
  }
  
  public Endpoint createEndpoint(String paramString, Class paramClass, Invoker paramInvoker, WebServiceFeature... paramVarArgs)
  {
    return new EndpointImpl(paramString != null ? BindingID.parse(paramString) : BindingID.parse(paramClass), paramClass, paramInvoker, paramVarArgs);
  }
  
  public EndpointReference readEndpointReference(Source paramSource)
  {
    try
    {
      Unmarshaller localUnmarshaller = ((JAXBContext)eprjc.get()).createUnmarshaller();
      return (EndpointReference)localUnmarshaller.unmarshal(paramSource);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException("Error creating Marshaller or marshalling.", localJAXBException);
    }
  }
  
  public <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs)
  {
    if (paramEndpointReference == null) {
      throw new WebServiceException(ProviderApiMessages.NULL_EPR());
    }
    WSEndpointReference localWSEndpointReference = new WSEndpointReference(paramEndpointReference);
    WSEndpointReference.Metadata localMetadata = localWSEndpointReference.getMetaData();
    WSService localWSService;
    if (localMetadata.getWsdlSource() != null) {
      localWSService = (WSService)createServiceDelegate(localMetadata.getWsdlSource(), localMetadata.getServiceName(), Service.class);
    } else {
      throw new WebServiceException("WSDL metadata is missing in EPR");
    }
    return (T)localWSService.getPort(localWSEndpointReference, paramClass, paramVarArgs);
  }
  
  public W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, List<Element> paramList1, String paramString2, List<Element> paramList2)
  {
    return createW3CEndpointReference(paramString1, null, paramQName1, paramQName2, paramList1, paramString2, paramList2, null, null);
  }
  
  public W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList1, String paramString2, List<Element> paramList2, List<Element> paramList3, Map<QName, String> paramMap)
  {
    Container localContainer = ContainerResolver.getInstance().getContainer();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    if (paramString1 == null)
    {
      if ((paramQName2 == null) || (paramQName3 == null)) {
        throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS_SERVICE_ENDPOINT());
      }
      localObject1 = (Module)localContainer.getSPI(Module.class);
      if (localObject1 != null)
      {
        localObject2 = ((Module)localObject1).getBoundEndpoints();
        localObject3 = ((List)localObject2).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (BoundEndpoint)((Iterator)localObject3).next();
          localObject5 = ((BoundEndpoint)localObject4).getEndpoint();
          if ((((WSEndpoint)localObject5).getServiceName().equals(paramQName2)) && (((WSEndpoint)localObject5).getPortName().equals(paramQName3))) {
            try
            {
              paramString1 = ((BoundEndpoint)localObject4).getAddress().toString();
            }
            catch (WebServiceException localWebServiceException) {}
          }
        }
      }
      if (paramString1 == null) {
        throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS());
      }
    }
    if ((paramQName2 == null) && (paramQName3 != null)) {
      throw new IllegalStateException(ProviderApiMessages.NULL_SERVICE());
    }
    Object localObject1 = null;
    if (paramString2 != null) {
      try
      {
        localObject2 = XmlUtil.createDefaultCatalogResolver();
        localObject3 = new URL(paramString2);
        localObject4 = RuntimeWSDLParser.parse((URL)localObject3, new StreamSource(((URL)localObject3).toExternalForm()), (EntityResolver)localObject2, true, localContainer, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
        if (paramQName2 != null)
        {
          localObject5 = ((WSDLModel)localObject4).getService(paramQName2);
          if (localObject5 == null) {
            throw new IllegalStateException(ProviderApiMessages.NOTFOUND_SERVICE_IN_WSDL(paramQName2, paramString2));
          }
          if (paramQName3 != null)
          {
            WSDLPort localWSDLPort = ((WSDLService)localObject5).get(paramQName3);
            if (localWSDLPort == null) {
              throw new IllegalStateException(ProviderApiMessages.NOTFOUND_PORT_IN_WSDL(paramQName3, paramQName2, paramString2));
            }
          }
          localObject1 = paramQName2.getNamespaceURI();
        }
        else
        {
          localObject5 = ((WSDLModel)localObject4).getFirstServiceName();
          localObject1 = ((QName)localObject5).getNamespaceURI();
        }
      }
      catch (Exception localException)
      {
        throw new IllegalStateException(ProviderApiMessages.ERROR_WSDL(paramString2), localException);
      }
    }
    if ((paramList1 != null) && (paramList1.size() == 0)) {
      paramList1 = null;
    }
    return (W3CEndpointReference)new WSEndpointReference(AddressingVersion.fromSpecClass(W3CEndpointReference.class), paramString1, paramQName2, paramQName3, paramQName1, paramList1, paramString2, (String)localObject1, paramList2, paramList3, paramMap).toSpec(W3CEndpointReference.class);
  }
  
  private static JAXBContext getEPRJaxbContext()
  {
    (JAXBContext)AccessController.doPrivileged(new PrivilegedAction()
    {
      public JAXBContext run()
      {
        try
        {
          return JAXBContext.newInstance(new Class[] { MemberSubmissionEndpointReference.class, W3CEndpointReference.class });
        }
        catch (JAXBException localJAXBException)
        {
          throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", localJAXBException);
        }
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\ProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */