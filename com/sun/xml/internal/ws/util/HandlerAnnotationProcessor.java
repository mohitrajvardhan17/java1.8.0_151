package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPMessageHandlers;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;

public class HandlerAnnotationProcessor
{
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.util");
  
  public HandlerAnnotationProcessor() {}
  
  public static HandlerAnnotationInfo buildHandlerInfo(@NotNull Class<?> paramClass, QName paramQName1, QName paramQName2, WSBinding paramWSBinding)
  {
    Object localObject = EndpointFactory.getExternalMetadatReader(paramClass, paramWSBinding);
    if (localObject == null) {
      localObject = new ReflectAnnotationReader();
    }
    HandlerChain localHandlerChain = (HandlerChain)((MetadataReader)localObject).getAnnotation(HandlerChain.class, paramClass);
    if (localHandlerChain == null)
    {
      paramClass = getSEI(paramClass, (MetadataReader)localObject);
      if (paramClass != null) {
        localHandlerChain = (HandlerChain)((MetadataReader)localObject).getAnnotation(HandlerChain.class, paramClass);
      }
      if (localHandlerChain == null) {
        return null;
      }
    }
    if (paramClass.getAnnotation(SOAPMessageHandlers.class) != null) {
      throw new UtilException("util.handler.cannot.combine.soapmessagehandlers", new Object[0]);
    }
    InputStream localInputStream = getFileAsStream(paramClass, localHandlerChain);
    XMLStreamReader localXMLStreamReader = XMLStreamReaderFactory.create(null, localInputStream, true);
    XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
    HandlerAnnotationInfo localHandlerAnnotationInfo = HandlerChainsModel.parseHandlerFile(localXMLStreamReader, paramClass.getClassLoader(), paramQName1, paramQName2, paramWSBinding);
    try
    {
      localXMLStreamReader.close();
      localInputStream.close();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      localXMLStreamException.printStackTrace();
      throw new UtilException(localXMLStreamException.getMessage(), new Object[0]);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      throw new UtilException(localIOException.getMessage(), new Object[0]);
    }
    return localHandlerAnnotationInfo;
  }
  
  public static HandlerChainsModel buildHandlerChainsModel(Class<?> paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    HandlerChain localHandlerChain = (HandlerChain)paramClass.getAnnotation(HandlerChain.class);
    if (localHandlerChain == null) {
      return null;
    }
    InputStream localInputStream = getFileAsStream(paramClass, localHandlerChain);
    XMLStreamReader localXMLStreamReader = XMLStreamReaderFactory.create(null, localInputStream, true);
    XMLStreamReaderUtil.nextElementContent(localXMLStreamReader);
    HandlerChainsModel localHandlerChainsModel = HandlerChainsModel.parseHandlerConfigFile(paramClass, localXMLStreamReader);
    try
    {
      localXMLStreamReader.close();
      localInputStream.close();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      localXMLStreamException.printStackTrace();
      throw new UtilException(localXMLStreamException.getMessage(), new Object[0]);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
      throw new UtilException(localIOException.getMessage(), new Object[0]);
    }
    return localHandlerChainsModel;
  }
  
  static Class getClass(String paramString)
  {
    try
    {
      return Thread.currentThread().getContextClassLoader().loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new UtilException("util.handler.class.not.found", new Object[] { paramString });
    }
  }
  
  static Class getSEI(Class<?> paramClass, MetadataReader paramMetadataReader)
  {
    if (paramMetadataReader == null) {
      paramMetadataReader = new ReflectAnnotationReader();
    }
    if ((Provider.class.isAssignableFrom(paramClass)) || (AsyncProvider.class.isAssignableFrom(paramClass))) {
      return null;
    }
    if (Service.class.isAssignableFrom(paramClass)) {
      return null;
    }
    WebService localWebService1 = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
    if (localWebService1 == null) {
      throw new UtilException("util.handler.no.webservice.annotation", new Object[] { paramClass.getCanonicalName() });
    }
    String str = localWebService1.endpointInterface();
    if (str.length() > 0)
    {
      paramClass = getClass(localWebService1.endpointInterface());
      WebService localWebService2 = (WebService)paramMetadataReader.getAnnotation(WebService.class, paramClass);
      if (localWebService2 == null) {
        throw new UtilException("util.handler.endpoint.interface.no.webservice", new Object[] { localWebService1.endpointInterface() });
      }
      return paramClass;
    }
    return null;
  }
  
  static InputStream getFileAsStream(Class paramClass, HandlerChain paramHandlerChain)
  {
    URL localURL = paramClass.getResource(paramHandlerChain.file());
    if (localURL == null) {
      localURL = Thread.currentThread().getContextClassLoader().getResource(paramHandlerChain.file());
    }
    if (localURL == null)
    {
      String str = paramClass.getPackage().getName();
      str = str.replace('.', '/');
      str = str + "/" + paramHandlerChain.file();
      localURL = Thread.currentThread().getContextClassLoader().getResource(str);
    }
    if (localURL == null) {
      throw new UtilException("util.failed.to.find.handlerchain.file", new Object[] { paramClass.getName(), paramHandlerChain.file() });
    }
    try
    {
      return localURL.openStream();
    }
    catch (IOException localIOException)
    {
      throw new UtilException("util.failed.to.parse.handlerchain.file", new Object[] { paramClass.getName(), paramHandlerChain.file() });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\HandlerAnnotationProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */