package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.UtilException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.PortInfo;

public class HandlerChainsModel
{
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.util");
  private Class annotatedClass;
  private List<HandlerChainType> handlerChains;
  private String id;
  public static final String PROTOCOL_SOAP11_TOKEN = "##SOAP11_HTTP";
  public static final String PROTOCOL_SOAP12_TOKEN = "##SOAP12_HTTP";
  public static final String PROTOCOL_XML_TOKEN = "##XML_HTTP";
  public static final String NS_109 = "http://java.sun.com/xml/ns/javaee";
  public static final QName QNAME_CHAIN_PORT_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "port-name-pattern");
  public static final QName QNAME_CHAIN_PROTOCOL_BINDING = new QName("http://java.sun.com/xml/ns/javaee", "protocol-bindings");
  public static final QName QNAME_CHAIN_SERVICE_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "service-name-pattern");
  public static final QName QNAME_HANDLER_CHAIN = new QName("http://java.sun.com/xml/ns/javaee", "handler-chain");
  public static final QName QNAME_HANDLER_CHAINS = new QName("http://java.sun.com/xml/ns/javaee", "handler-chains");
  public static final QName QNAME_HANDLER = new QName("http://java.sun.com/xml/ns/javaee", "handler");
  public static final QName QNAME_HANDLER_NAME = new QName("http://java.sun.com/xml/ns/javaee", "handler-name");
  public static final QName QNAME_HANDLER_CLASS = new QName("http://java.sun.com/xml/ns/javaee", "handler-class");
  public static final QName QNAME_HANDLER_PARAM = new QName("http://java.sun.com/xml/ns/javaee", "init-param");
  public static final QName QNAME_HANDLER_PARAM_NAME = new QName("http://java.sun.com/xml/ns/javaee", "param-name");
  public static final QName QNAME_HANDLER_PARAM_VALUE = new QName("http://java.sun.com/xml/ns/javaee", "param-value");
  public static final QName QNAME_HANDLER_HEADER = new QName("http://java.sun.com/xml/ns/javaee", "soap-header");
  public static final QName QNAME_HANDLER_ROLE = new QName("http://java.sun.com/xml/ns/javaee", "soap-role");
  
  private HandlerChainsModel(Class paramClass)
  {
    annotatedClass = paramClass;
  }
  
  private List<HandlerChainType> getHandlerChain()
  {
    if (handlerChains == null) {
      handlerChains = new ArrayList();
    }
    return handlerChains;
  }
  
  public String getId()
  {
    return id;
  }
  
  public void setId(String paramString)
  {
    id = paramString;
  }
  
  public static HandlerChainsModel parseHandlerConfigFile(Class paramClass, XMLStreamReader paramXMLStreamReader)
  {
    ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAINS);
    HandlerChainsModel localHandlerChainsModel = new HandlerChainsModel(paramClass);
    List localList1 = localHandlerChainsModel.getHandlerChain();
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_CHAIN))
    {
      HandlerChainType localHandlerChainType = new HandlerChainType();
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      Object localObject2;
      String str;
      if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PORT_PATTERN))
      {
        localObject1 = XMLStreamReaderUtil.getElementQName(paramXMLStreamReader);
        localHandlerChainType.setPortNamePattern((QName)localObject1);
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      }
      else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING))
      {
        localObject1 = XMLStreamReaderUtil.getElementText(paramXMLStreamReader);
        localObject2 = new StringTokenizer((String)localObject1);
        while (((StringTokenizer)localObject2).hasMoreTokens())
        {
          str = ((StringTokenizer)localObject2).nextToken();
          localHandlerChainType.addProtocolBinding(str);
        }
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      }
      else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN))
      {
        localObject1 = XMLStreamReaderUtil.getElementQName(paramXMLStreamReader);
        localHandlerChainType.setServiceNamePattern((QName)localObject1);
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      }
      Object localObject1 = localHandlerChainType.getHandlers();
      while (paramXMLStreamReader.getName().equals(QNAME_HANDLER))
      {
        localObject2 = new HandlerType();
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        if (paramXMLStreamReader.getName().equals(QNAME_HANDLER_NAME))
        {
          str = XMLStreamReaderUtil.getElementText(paramXMLStreamReader).trim();
          ((HandlerType)localObject2).setHandlerName(str);
          XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        }
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CLASS);
        str = XMLStreamReaderUtil.getElementText(paramXMLStreamReader).trim();
        ((HandlerType)localObject2).setHandlerClass(str);
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_PARAM)) {
          skipInitParamElement(paramXMLStreamReader);
        }
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_HEADER)) {
          skipTextElement(paramXMLStreamReader);
        }
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_ROLE))
        {
          List localList2 = ((HandlerType)localObject2).getSoapRoles();
          localList2.add(XMLStreamReaderUtil.getElementText(paramXMLStreamReader));
          XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        }
        ((List)localObject1).add(localObject2);
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER);
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
      }
      ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAIN);
      localList1.add(localHandlerChainType);
      XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    }
    return localHandlerChainsModel;
  }
  
  public static HandlerAnnotationInfo parseHandlerFile(XMLStreamReader paramXMLStreamReader, ClassLoader paramClassLoader, QName paramQName1, QName paramQName2, WSBinding paramWSBinding)
  {
    ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAINS);
    String str1 = paramWSBinding.getBindingId().toString();
    HandlerAnnotationInfo localHandlerAnnotationInfo = new HandlerAnnotationInfo();
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    ArrayList localArrayList1 = new ArrayList();
    HashSet localHashSet = new HashSet();
    while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_CHAIN))
    {
      XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      Object localObject;
      if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PORT_PATTERN))
      {
        if (paramQName2 == null) {
          logger.warning("handler chain sepcified for port but port QName passed to parser is null");
        }
        boolean bool1 = JAXWSUtils.matchQNames(paramQName2, XMLStreamReaderUtil.getElementQName(paramXMLStreamReader));
        if (!bool1)
        {
          skipChain(paramXMLStreamReader);
          continue;
        }
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      }
      else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING))
      {
        if (str1 == null) {
          logger.warning("handler chain sepcified for bindingId but bindingId passed to parser is null");
        }
        String str2 = XMLStreamReaderUtil.getElementText(paramXMLStreamReader);
        int i = 1;
        StringTokenizer localStringTokenizer = new StringTokenizer(str2);
        ArrayList localArrayList2 = new ArrayList();
        while (localStringTokenizer.hasMoreTokens())
        {
          localObject = localStringTokenizer.nextToken();
          localObject = DeploymentDescriptorParser.getBindingIdForToken((String)localObject);
          String str3 = BindingID.parse((String)localObject).toString();
          localArrayList2.add(str3);
        }
        if (localArrayList2.contains(str1)) {
          i = 0;
        }
        if (i != 0)
        {
          skipChain(paramXMLStreamReader);
          continue;
        }
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      }
      else if (paramXMLStreamReader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN))
      {
        if (paramQName1 == null) {
          logger.warning("handler chain sepcified for service but service QName passed to parser is null");
        }
        boolean bool2 = JAXWSUtils.matchQNames(paramQName1, XMLStreamReaderUtil.getElementQName(paramXMLStreamReader));
        if (!bool2)
        {
          skipChain(paramXMLStreamReader);
          continue;
        }
        XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
      }
      while (paramXMLStreamReader.getName().equals(QNAME_HANDLER))
      {
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        if (paramXMLStreamReader.getName().equals(QNAME_HANDLER_NAME)) {
          skipTextElement(paramXMLStreamReader);
        }
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CLASS);
        Handler localHandler;
        try
        {
          localHandler = (Handler)loadClass(paramClassLoader, XMLStreamReaderUtil.getElementText(paramXMLStreamReader).trim()).newInstance();
        }
        catch (InstantiationException localInstantiationException)
        {
          throw new RuntimeException(localInstantiationException);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new RuntimeException(localIllegalAccessException);
        }
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_PARAM)) {
          skipInitParamElement(paramXMLStreamReader);
        }
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_HEADER)) {
          skipTextElement(paramXMLStreamReader);
        }
        while (paramXMLStreamReader.getName().equals(QNAME_HANDLER_ROLE))
        {
          localHashSet.add(XMLStreamReaderUtil.getElementText(paramXMLStreamReader));
          XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
        }
        for (localObject : localHandler.getClass().getMethods()) {
          if (((Method)localObject).getAnnotation(PostConstruct.class) != null) {
            try
            {
              ((Method)localObject).invoke(localHandler, new Object[0]);
            }
            catch (Exception localException)
            {
              throw new RuntimeException(localException);
            }
          }
        }
        localArrayList1.add(localHandler);
        ensureProperName(paramXMLStreamReader, QNAME_HANDLER);
        XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
      }
      ensureProperName(paramXMLStreamReader, QNAME_HANDLER_CHAIN);
      XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    }
    localHandlerAnnotationInfo.setHandlers(localArrayList1);
    localHandlerAnnotationInfo.setRoles(localHashSet);
    return localHandlerAnnotationInfo;
  }
  
  public HandlerAnnotationInfo getHandlersForPortInfo(PortInfo paramPortInfo)
  {
    HandlerAnnotationInfo localHandlerAnnotationInfo = new HandlerAnnotationInfo();
    ArrayList localArrayList = new ArrayList();
    HashSet localHashSet = new HashSet();
    Iterator localIterator1 = handlerChains.iterator();
    while (localIterator1.hasNext())
    {
      HandlerChainType localHandlerChainType = (HandlerChainType)localIterator1.next();
      int i = 0;
      if ((!localHandlerChainType.isConstraintSet()) || (JAXWSUtils.matchQNames(paramPortInfo.getServiceName(), localHandlerChainType.getServiceNamePattern())) || (JAXWSUtils.matchQNames(paramPortInfo.getPortName(), localHandlerChainType.getPortNamePattern())) || (localHandlerChainType.getProtocolBindings().contains(paramPortInfo.getBindingID()))) {
        i = 1;
      }
      if (i != 0)
      {
        Iterator localIterator2 = localHandlerChainType.getHandlers().iterator();
        while (localIterator2.hasNext())
        {
          HandlerType localHandlerType = (HandlerType)localIterator2.next();
          try
          {
            Handler localHandler = (Handler)loadClass(annotatedClass.getClassLoader(), localHandlerType.getHandlerClass()).newInstance();
            callHandlerPostConstruct(localHandler);
            localArrayList.add(localHandler);
          }
          catch (InstantiationException localInstantiationException)
          {
            throw new RuntimeException(localInstantiationException);
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new RuntimeException(localIllegalAccessException);
          }
          localHashSet.addAll(localHandlerType.getSoapRoles());
        }
      }
    }
    localHandlerAnnotationInfo.setHandlers(localArrayList);
    localHandlerAnnotationInfo.setRoles(localHashSet);
    return localHandlerAnnotationInfo;
  }
  
  private static Class loadClass(ClassLoader paramClassLoader, String paramString)
  {
    try
    {
      return Class.forName(paramString, true, paramClassLoader);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new UtilException("util.handler.class.not.found", new Object[] { paramString });
    }
  }
  
  private static void callHandlerPostConstruct(Object paramObject)
  {
    for (Method localMethod : paramObject.getClass().getMethods()) {
      if (localMethod.getAnnotation(PostConstruct.class) != null) {
        try
        {
          localMethod.invoke(paramObject, new Object[0]);
        }
        catch (Exception localException)
        {
          throw new RuntimeException(localException);
        }
      }
    }
  }
  
  private static void skipChain(XMLStreamReader paramXMLStreamReader)
  {
    while ((XMLStreamReaderUtil.nextContent(paramXMLStreamReader) != 2) || (!paramXMLStreamReader.getName().equals(QNAME_HANDLER_CHAIN))) {}
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
  }
  
  private static void skipTextElement(XMLStreamReader paramXMLStreamReader)
  {
    XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
  }
  
  private static void skipInitParamElement(XMLStreamReader paramXMLStreamReader)
  {
    int i;
    do
    {
      i = XMLStreamReaderUtil.nextContent(paramXMLStreamReader);
    } while ((i != 2) || (!paramXMLStreamReader.getName().equals(QNAME_HANDLER_PARAM)));
    XMLStreamReaderUtil.nextElementContent(paramXMLStreamReader);
  }
  
  private static void ensureProperName(XMLStreamReader paramXMLStreamReader, QName paramQName)
  {
    if (!paramXMLStreamReader.getName().equals(paramQName)) {
      failWithLocalName("util.parser.wrong.element", paramXMLStreamReader, paramQName.getLocalPart());
    }
  }
  
  static void ensureProperName(XMLStreamReader paramXMLStreamReader, String paramString)
  {
    if (!paramXMLStreamReader.getLocalName().equals(paramString)) {
      failWithLocalName("util.parser.wrong.element", paramXMLStreamReader, paramString);
    }
  }
  
  private static void failWithLocalName(String paramString1, XMLStreamReader paramXMLStreamReader, String paramString2)
  {
    throw new UtilException(paramString1, new Object[] { Integer.toString(paramXMLStreamReader.getLocation().getLineNumber()), paramXMLStreamReader.getLocalName(), paramString2 });
  }
  
  static class HandlerChainType
  {
    QName serviceNamePattern;
    QName portNamePattern;
    List<String> protocolBindings = new ArrayList();
    boolean constraintSet = false;
    List<HandlerChainsModel.HandlerType> handlers;
    String id;
    
    public HandlerChainType() {}
    
    public void setServiceNamePattern(QName paramQName)
    {
      serviceNamePattern = paramQName;
      constraintSet = true;
    }
    
    public QName getServiceNamePattern()
    {
      return serviceNamePattern;
    }
    
    public void setPortNamePattern(QName paramQName)
    {
      portNamePattern = paramQName;
      constraintSet = true;
    }
    
    public QName getPortNamePattern()
    {
      return portNamePattern;
    }
    
    public List<String> getProtocolBindings()
    {
      return protocolBindings;
    }
    
    public void addProtocolBinding(String paramString)
    {
      paramString = DeploymentDescriptorParser.getBindingIdForToken(paramString);
      String str = BindingID.parse(paramString).toString();
      protocolBindings.add(str);
      constraintSet = true;
    }
    
    public boolean isConstraintSet()
    {
      return (constraintSet) || (!protocolBindings.isEmpty());
    }
    
    public String getId()
    {
      return id;
    }
    
    public void setId(String paramString)
    {
      id = paramString;
    }
    
    public List<HandlerChainsModel.HandlerType> getHandlers()
    {
      if (handlers == null) {
        handlers = new ArrayList();
      }
      return handlers;
    }
  }
  
  static class HandlerType
  {
    String handlerName;
    String handlerClass;
    List<String> soapRoles;
    String id;
    
    public HandlerType() {}
    
    public String getHandlerName()
    {
      return handlerName;
    }
    
    public void setHandlerName(String paramString)
    {
      handlerName = paramString;
    }
    
    public String getHandlerClass()
    {
      return handlerClass;
    }
    
    public void setHandlerClass(String paramString)
    {
      handlerClass = paramString;
    }
    
    public String getId()
    {
      return id;
    }
    
    public void setId(String paramString)
    {
      id = paramString;
    }
    
    public List<String> getSoapRoles()
    {
      if (soapRoles == null) {
        soapRoles = new ArrayList();
      }
      return soapRoles;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\HandlerChainsModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */