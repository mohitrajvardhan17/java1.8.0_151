package com.sun.xml.internal.ws.wsdl.writer;

import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.bind.v2.schemagen.Util;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Element;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.txw2.output.TXWResult;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLGeneratorExtension;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.writer.document.Binding;
import com.sun.xml.internal.ws.wsdl.writer.document.BindingOperationType;
import com.sun.xml.internal.ws.wsdl.writer.document.Definitions;
import com.sun.xml.internal.ws.wsdl.writer.document.Fault;
import com.sun.xml.internal.ws.wsdl.writer.document.FaultType;
import com.sun.xml.internal.ws.wsdl.writer.document.Message;
import com.sun.xml.internal.ws.wsdl.writer.document.Operation;
import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import com.sun.xml.internal.ws.wsdl.writer.document.Port;
import com.sun.xml.internal.ws.wsdl.writer.document.PortType;
import com.sun.xml.internal.ws.wsdl.writer.document.Service;
import com.sun.xml.internal.ws.wsdl.writer.document.StartWithExtensionsType;
import com.sun.xml.internal.ws.wsdl.writer.document.Types;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;

public class WSDLGenerator
{
  private JAXWSOutputSchemaResolver resolver;
  private WSDLResolver wsdlResolver = null;
  private AbstractSEIModelImpl model;
  private Definitions serviceDefinitions;
  private Definitions portDefinitions;
  private Types types;
  private static final String DOT_WSDL = ".wsdl";
  private static final String RESPONSE = "Response";
  private static final String PARAMETERS = "parameters";
  private static final String RESULT = "parameters";
  private static final String UNWRAPPABLE_RESULT = "result";
  private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
  private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
  private static final String XSD_PREFIX = "xsd";
  private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
  private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";
  private static final String SOAP_PREFIX = "soap";
  private static final String SOAP12_PREFIX = "soap12";
  private static final String TNS_PREFIX = "tns";
  private static final String DOCUMENT = "document";
  private static final String RPC = "rpc";
  private static final String LITERAL = "literal";
  private static final String REPLACE_WITH_ACTUAL_URL = "REPLACE_WITH_ACTUAL_URL";
  private Set<QName> processedExceptions = new HashSet();
  private WSBinding binding;
  private String wsdlLocation;
  private String portWSDLID;
  private String schemaPrefix;
  private WSDLGeneratorExtension extension;
  List<WSDLGeneratorExtension> extensionHandlers;
  private String endpointAddress = "REPLACE_WITH_ACTUAL_URL";
  private Container container;
  private final Class implType;
  private boolean inlineSchemas;
  private final boolean disableXmlSecurity;
  
  public WSDLGenerator(AbstractSEIModelImpl paramAbstractSEIModelImpl, WSDLResolver paramWSDLResolver, WSBinding paramWSBinding, Container paramContainer, Class paramClass, boolean paramBoolean, WSDLGeneratorExtension... paramVarArgs)
  {
    this(paramAbstractSEIModelImpl, paramWSDLResolver, paramWSBinding, paramContainer, paramClass, paramBoolean, false, paramVarArgs);
  }
  
  public WSDLGenerator(AbstractSEIModelImpl paramAbstractSEIModelImpl, WSDLResolver paramWSDLResolver, WSBinding paramWSBinding, Container paramContainer, Class paramClass, boolean paramBoolean1, boolean paramBoolean2, WSDLGeneratorExtension... paramVarArgs)
  {
    model = paramAbstractSEIModelImpl;
    resolver = new JAXWSOutputSchemaResolver();
    wsdlResolver = paramWSDLResolver;
    binding = paramWSBinding;
    container = paramContainer;
    implType = paramClass;
    extensionHandlers = new ArrayList();
    inlineSchemas = paramBoolean1;
    disableXmlSecurity = paramBoolean2;
    register(new W3CAddressingWSDLGeneratorExtension());
    register(new W3CAddressingMetadataWSDLGeneratorExtension());
    register(new PolicyWSDLGeneratorExtension());
    WSDLGeneratorExtension[] arrayOfWSDLGeneratorExtension1;
    if (paramContainer != null)
    {
      arrayOfWSDLGeneratorExtension1 = (WSDLGeneratorExtension[])paramContainer.getSPI(WSDLGeneratorExtension[].class);
      if (arrayOfWSDLGeneratorExtension1 != null) {
        for (WSDLGeneratorExtension localWSDLGeneratorExtension2 : arrayOfWSDLGeneratorExtension1) {
          register(localWSDLGeneratorExtension2);
        }
      }
    }
    for (WSDLGeneratorExtension localWSDLGeneratorExtension1 : paramVarArgs) {
      register(localWSDLGeneratorExtension1);
    }
    extension = new WSDLGeneratorExtensionFacade((WSDLGeneratorExtension[])extensionHandlers.toArray(new WSDLGeneratorExtension[0]));
  }
  
  public void setEndpointAddress(String paramString)
  {
    endpointAddress = paramString;
  }
  
  protected String mangleName(String paramString)
  {
    return BindingHelper.mangleNameToClassName(paramString);
  }
  
  public void doGeneration()
  {
    Object localObject = null;
    String str1 = mangleName(model.getServiceQName().getLocalPart());
    Result localResult = wsdlResolver.getWSDL(str1 + ".wsdl");
    wsdlLocation = localResult.getSystemId();
    CommentFilter localCommentFilter = new CommentFilter(ResultFactory.createSerializer(localResult));
    if (model.getServiceQName().getNamespaceURI().equals(model.getTargetNamespace()))
    {
      localObject = localCommentFilter;
      schemaPrefix = (str1 + "_");
    }
    else
    {
      String str2 = mangleName(model.getPortTypeName().getLocalPart());
      if (str2.equals(str1)) {
        str2 = str2 + "PortType";
      }
      Holder localHolder = new Holder();
      value = (str2 + ".wsdl");
      localResult = wsdlResolver.getAbstractWSDL(localHolder);
      if (localResult != null)
      {
        portWSDLID = localResult.getSystemId();
        if (portWSDLID.equals(wsdlLocation)) {
          localObject = localCommentFilter;
        } else {
          localObject = new CommentFilter(ResultFactory.createSerializer(localResult));
        }
      }
      else
      {
        portWSDLID = ((String)value);
      }
      schemaPrefix = new File(portWSDLID).getName();
      int i = schemaPrefix.lastIndexOf('.');
      if (i > 0) {
        schemaPrefix = schemaPrefix.substring(0, i);
      }
      schemaPrefix = (mangleName(schemaPrefix) + "_");
    }
    generateDocument(localCommentFilter, (XmlSerializer)localObject);
  }
  
  private void generateDocument(XmlSerializer paramXmlSerializer1, XmlSerializer paramXmlSerializer2)
  {
    serviceDefinitions = ((Definitions)TXW.create(Definitions.class, paramXmlSerializer1));
    serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/", "");
    serviceDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
    serviceDefinitions.targetNamespace(model.getServiceQName().getNamespaceURI());
    serviceDefinitions._namespace(model.getServiceQName().getNamespaceURI(), "tns");
    if (binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
      serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap12/", "soap12");
    } else {
      serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap/", "soap");
    }
    serviceDefinitions.name(model.getServiceQName().getLocalPart());
    WSDLGenExtnContext localWSDLGenExtnContext = new WSDLGenExtnContext(serviceDefinitions, model, binding, container, implType);
    extension.start(localWSDLGenExtnContext);
    String str;
    com.sun.xml.internal.ws.wsdl.writer.document.Import localImport;
    if ((paramXmlSerializer1 != paramXmlSerializer2) && (paramXmlSerializer2 != null))
    {
      portDefinitions = ((Definitions)TXW.create(Definitions.class, paramXmlSerializer2));
      portDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/", "");
      portDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
      if (model.getTargetNamespace() != null)
      {
        portDefinitions.targetNamespace(model.getTargetNamespace());
        portDefinitions._namespace(model.getTargetNamespace(), "tns");
      }
      str = relativize(portWSDLID, wsdlLocation);
      localImport = serviceDefinitions._import().namespace(model.getTargetNamespace());
      localImport.location(str);
    }
    else if (paramXmlSerializer2 != null)
    {
      portDefinitions = serviceDefinitions;
    }
    else
    {
      str = relativize(portWSDLID, wsdlLocation);
      localImport = serviceDefinitions._import().namespace(model.getTargetNamespace());
      localImport.location(str);
    }
    extension.addDefinitionsExtension(serviceDefinitions);
    if (portDefinitions != null)
    {
      generateTypes();
      generateMessages();
      generatePortType();
    }
    generateBinding();
    generateService();
    extension.end(localWSDLGenExtnContext);
    serviceDefinitions.commit();
    if ((portDefinitions != null) && (portDefinitions != serviceDefinitions)) {
      portDefinitions.commit();
    }
  }
  
  protected void generateTypes()
  {
    types = portDefinitions.types();
    if (model.getBindingContext() != null)
    {
      if ((inlineSchemas) && (model.getBindingContext().getClass().getName().indexOf("glassfish") == -1)) {
        resolver.nonGlassfishSchemas = new ArrayList();
      }
      try
      {
        model.getBindingContext().generateSchema(resolver);
      }
      catch (IOException localIOException)
      {
        throw new WebServiceException(localIOException.getMessage());
      }
    }
    if (resolver.nonGlassfishSchemas != null)
    {
      TransformerFactory localTransformerFactory = XmlUtil.newTransformerFactory(!disableXmlSecurity);
      try
      {
        Transformer localTransformer = localTransformerFactory.newTransformer();
        Iterator localIterator = resolver.nonGlassfishSchemas.iterator();
        while (localIterator.hasNext())
        {
          DOMResult localDOMResult = (DOMResult)localIterator.next();
          Document localDocument = (Document)localDOMResult.getNode();
          SAXResult localSAXResult = new SAXResult(new TXWContentHandler(types));
          localTransformer.transform(new DOMSource(localDocument.getDocumentElement()), localSAXResult);
        }
      }
      catch (TransformerConfigurationException localTransformerConfigurationException)
      {
        throw new WebServiceException(localTransformerConfigurationException.getMessage(), localTransformerConfigurationException);
      }
      catch (TransformerException localTransformerException)
      {
        throw new WebServiceException(localTransformerException.getMessage(), localTransformerException);
      }
    }
    generateWrappers();
  }
  
  void generateWrappers()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = model.getJavaMethods().iterator();
    Object localObject3;
    Object localObject4;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (JavaMethodImpl)((Iterator)localObject1).next();
      if (!((JavaMethodImpl)localObject2).getBinding().isRpcLit())
      {
        localObject3 = ((JavaMethodImpl)localObject2).getRequestParameters().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (ParameterImpl)((Iterator)localObject3).next();
          if (((localObject4 instanceof WrapperParameter)) && (WrapperComposite.class.equals(getTypeInfotype))) {
            localArrayList.add((WrapperParameter)localObject4);
          }
        }
        localObject3 = ((JavaMethodImpl)localObject2).getResponseParameters().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (ParameterImpl)((Iterator)localObject3).next();
          if (((localObject4 instanceof WrapperParameter)) && (WrapperComposite.class.equals(getTypeInfotype))) {
            localArrayList.add((WrapperParameter)localObject4);
          }
        }
      }
    }
    if (localArrayList.isEmpty()) {
      return;
    }
    localObject1 = new HashMap();
    Object localObject2 = localArrayList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (WrapperParameter)((Iterator)localObject2).next();
      localObject4 = ((WrapperParameter)localObject3).getName().getNamespaceURI();
      Schema localSchema = (Schema)((HashMap)localObject1).get(localObject4);
      if (localSchema == null)
      {
        localSchema = types.schema();
        localSchema.targetNamespace((String)localObject4);
        ((HashMap)localObject1).put(localObject4, localSchema);
      }
      Element localElement = (Element)localSchema._element(Element.class);
      localElement._attribute("name", ((WrapperParameter)localObject3).getName().getLocalPart());
      localElement.type(((WrapperParameter)localObject3).getName());
      ComplexType localComplexType = (ComplexType)localSchema._element(ComplexType.class);
      localComplexType._attribute("name", ((WrapperParameter)localObject3).getName().getLocalPart());
      ExplicitGroup localExplicitGroup = localComplexType.sequence();
      Iterator localIterator = ((WrapperParameter)localObject3).getWrapperChildren().iterator();
      while (localIterator.hasNext())
      {
        ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
        if (localParameterImpl.getBinding().isBody())
        {
          LocalElement localLocalElement = localExplicitGroup.element();
          localLocalElement._attribute("name", localParameterImpl.getName().getLocalPart());
          TypeInfo localTypeInfo = localParameterImpl.getItemType();
          int i = 0;
          if (localTypeInfo == null) {
            localTypeInfo = localParameterImpl.getTypeInfo();
          } else {
            i = 1;
          }
          QName localQName = model.getBindingContext().getTypeName(localTypeInfo);
          localLocalElement.type(localQName);
          if (i != 0)
          {
            localLocalElement.minOccurs(0);
            localLocalElement.maxOccurs("unbounded");
          }
        }
      }
    }
  }
  
  protected void generateMessages()
  {
    Iterator localIterator = model.getJavaMethods().iterator();
    while (localIterator.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      generateSOAPMessages(localJavaMethodImpl, localJavaMethodImpl.getBinding());
    }
  }
  
  protected void generateSOAPMessages(JavaMethodImpl paramJavaMethodImpl, com.sun.xml.internal.ws.api.model.soap.SOAPBinding paramSOAPBinding)
  {
    boolean bool = paramSOAPBinding.isDocLit();
    Message localMessage = portDefinitions.message().name(paramJavaMethodImpl.getRequestMessageName());
    extension.addInputMessageExtension(localMessage, paramJavaMethodImpl);
    BindingContext localBindingContext = model.getBindingContext();
    int i = 1;
    Iterator localIterator = paramJavaMethodImpl.getRequestParameters().iterator();
    Object localObject1;
    Part localPart;
    Object localObject2;
    Object localObject3;
    while (localIterator.hasNext())
    {
      localObject1 = (ParameterImpl)localIterator.next();
      if (bool)
      {
        if (isHeaderParameter((ParameterImpl)localObject1)) {
          i = 0;
        }
        localPart = localMessage.part().name(((ParameterImpl)localObject1).getPartName());
        localPart.element(((ParameterImpl)localObject1).getName());
      }
      else if (((ParameterImpl)localObject1).isWrapperStyle())
      {
        localObject2 = ((WrapperParameter)localObject1).getWrapperChildren().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (ParameterImpl)((Iterator)localObject2).next();
          localPart = localMessage.part().name(((ParameterImpl)localObject3).getPartName());
          localPart.type(localBindingContext.getTypeName(((ParameterImpl)localObject3).getXMLBridge().getTypeInfo()));
        }
      }
      else
      {
        localPart = localMessage.part().name(((ParameterImpl)localObject1).getPartName());
        localPart.element(((ParameterImpl)localObject1).getName());
      }
    }
    if (paramJavaMethodImpl.getMEP() != MEP.ONE_WAY)
    {
      localMessage = portDefinitions.message().name(paramJavaMethodImpl.getResponseMessageName());
      extension.addOutputMessageExtension(localMessage, paramJavaMethodImpl);
      localIterator = paramJavaMethodImpl.getResponseParameters().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (ParameterImpl)localIterator.next();
        if (bool)
        {
          localPart = localMessage.part().name(((ParameterImpl)localObject1).getPartName());
          localPart.element(((ParameterImpl)localObject1).getName());
        }
        else if (((ParameterImpl)localObject1).isWrapperStyle())
        {
          localObject2 = ((WrapperParameter)localObject1).getWrapperChildren().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (ParameterImpl)((Iterator)localObject2).next();
            localPart = localMessage.part().name(((ParameterImpl)localObject3).getPartName());
            localPart.type(localBindingContext.getTypeName(((ParameterImpl)localObject3).getXMLBridge().getTypeInfo()));
          }
        }
        else
        {
          localPart = localMessage.part().name(((ParameterImpl)localObject1).getPartName());
          localPart.element(((ParameterImpl)localObject1).getName());
        }
      }
    }
    localIterator = paramJavaMethodImpl.getCheckedExceptions().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (CheckedExceptionImpl)localIterator.next();
      localObject2 = getDetailTypetagName;
      localObject3 = ((CheckedExceptionImpl)localObject1).getMessageName();
      QName localQName = new QName(model.getTargetNamespace(), (String)localObject3);
      if (!processedExceptions.contains(localQName))
      {
        localMessage = portDefinitions.message().name((String)localObject3);
        extension.addFaultMessageExtension(localMessage, paramJavaMethodImpl, (CheckedException)localObject1);
        localPart = localMessage.part().name("fault");
        localPart.element((QName)localObject2);
        processedExceptions.add(localQName);
      }
    }
  }
  
  protected void generatePortType()
  {
    PortType localPortType = portDefinitions.portType().name(model.getPortTypeName().getLocalPart());
    extension.addPortTypeExtension(localPortType);
    Iterator localIterator1 = model.getJavaMethods().iterator();
    while (localIterator1.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator1.next();
      Operation localOperation = localPortType.operation().name(localJavaMethodImpl.getOperationName());
      generateParameterOrder(localOperation, localJavaMethodImpl);
      extension.addOperationExtension(localOperation, localJavaMethodImpl);
      switch (localJavaMethodImpl.getMEP())
      {
      case REQUEST_RESPONSE: 
        generateInputMessage(localOperation, localJavaMethodImpl);
        generateOutputMessage(localOperation, localJavaMethodImpl);
        break;
      case ONE_WAY: 
        generateInputMessage(localOperation, localJavaMethodImpl);
        break;
      }
      Iterator localIterator2 = localJavaMethodImpl.getCheckedExceptions().iterator();
      while (localIterator2.hasNext())
      {
        CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)localIterator2.next();
        QName localQName = new QName(model.getTargetNamespace(), localCheckedExceptionImpl.getMessageName());
        FaultType localFaultType = localOperation.fault().message(localQName).name(localCheckedExceptionImpl.getMessageName());
        extension.addOperationFaultExtension(localFaultType, localJavaMethodImpl, localCheckedExceptionImpl);
      }
    }
  }
  
  protected boolean isWrapperStyle(JavaMethodImpl paramJavaMethodImpl)
  {
    if (paramJavaMethodImpl.getRequestParameters().size() > 0)
    {
      ParameterImpl localParameterImpl = (ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next();
      return localParameterImpl.isWrapperStyle();
    }
    return false;
  }
  
  protected boolean isRpcLit(JavaMethodImpl paramJavaMethodImpl)
  {
    return paramJavaMethodImpl.getBinding().getStyle() == SOAPBinding.Style.RPC;
  }
  
  protected void generateParameterOrder(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl)
  {
    if (paramJavaMethodImpl.getMEP() == MEP.ONE_WAY) {
      return;
    }
    if (isRpcLit(paramJavaMethodImpl)) {
      generateRpcParameterOrder(paramOperation, paramJavaMethodImpl);
    } else {
      generateDocumentParameterOrder(paramOperation, paramJavaMethodImpl);
    }
  }
  
  protected void generateRpcParameterOrder(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    HashSet localHashSet = new HashSet();
    List localList = sortMethodParameters(paramJavaMethodImpl);
    int i = 0;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      if (localParameterImpl.getIndex() >= 0)
      {
        String str = localParameterImpl.getPartName();
        if (!localHashSet.contains(str))
        {
          if (i++ > 0) {
            localStringBuilder.append(' ');
          }
          localStringBuilder.append(str);
          localHashSet.add(str);
        }
      }
    }
    if (i > 1) {
      paramOperation.parameterOrder(localStringBuilder.toString());
    }
  }
  
  protected void generateDocumentParameterOrder(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    HashSet localHashSet = new HashSet();
    List localList = sortMethodParameters(paramJavaMethodImpl);
    int i = 0;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      if (localParameterImpl.getIndex() >= 0)
      {
        String str = localParameterImpl.getPartName();
        if (!localHashSet.contains(str))
        {
          if (i++ > 0) {
            localStringBuilder.append(' ');
          }
          localStringBuilder.append(str);
          localHashSet.add(str);
        }
      }
    }
    if (i > 1) {
      paramOperation.parameterOrder(localStringBuilder.toString());
    }
  }
  
  protected List<ParameterImpl> sortMethodParameters(JavaMethodImpl paramJavaMethodImpl)
  {
    HashSet localHashSet = new HashSet();
    ArrayList localArrayList = new ArrayList();
    if (isRpcLit(paramJavaMethodImpl))
    {
      localIterator = paramJavaMethodImpl.getRequestParameters().iterator();
      while (localIterator.hasNext())
      {
        localParameterImpl1 = (ParameterImpl)localIterator.next();
        if ((localParameterImpl1 instanceof WrapperParameter)) {
          localHashSet.addAll(((WrapperParameter)localParameterImpl1).getWrapperChildren());
        } else {
          localHashSet.add(localParameterImpl1);
        }
      }
      localIterator = paramJavaMethodImpl.getResponseParameters().iterator();
      while (localIterator.hasNext())
      {
        localParameterImpl1 = (ParameterImpl)localIterator.next();
        if ((localParameterImpl1 instanceof WrapperParameter)) {
          localHashSet.addAll(((WrapperParameter)localParameterImpl1).getWrapperChildren());
        } else {
          localHashSet.add(localParameterImpl1);
        }
      }
    }
    else
    {
      localHashSet.addAll(paramJavaMethodImpl.getRequestParameters());
      localHashSet.addAll(paramJavaMethodImpl.getResponseParameters());
    }
    Iterator localIterator = localHashSet.iterator();
    if (localHashSet.isEmpty()) {
      return localArrayList;
    }
    ParameterImpl localParameterImpl1 = (ParameterImpl)localIterator.next();
    localArrayList.add(localParameterImpl1);
    for (int j = 1; j < localHashSet.size(); j++)
    {
      localParameterImpl1 = (ParameterImpl)localIterator.next();
      for (int i = 0; i < j; i++)
      {
        ParameterImpl localParameterImpl2 = (ParameterImpl)localArrayList.get(i);
        if (((localParameterImpl1.getIndex() == localParameterImpl2.getIndex()) && ((localParameterImpl1 instanceof WrapperParameter))) || (localParameterImpl1.getIndex() < localParameterImpl2.getIndex())) {
          break;
        }
      }
      localArrayList.add(i, localParameterImpl1);
    }
    return localArrayList;
  }
  
  protected boolean isBodyParameter(ParameterImpl paramParameterImpl)
  {
    ParameterBinding localParameterBinding = paramParameterImpl.getBinding();
    return localParameterBinding.isBody();
  }
  
  protected boolean isHeaderParameter(ParameterImpl paramParameterImpl)
  {
    ParameterBinding localParameterBinding = paramParameterImpl.getBinding();
    return localParameterBinding.isHeader();
  }
  
  protected boolean isAttachmentParameter(ParameterImpl paramParameterImpl)
  {
    ParameterBinding localParameterBinding = paramParameterImpl.getBinding();
    return localParameterBinding.isAttachment();
  }
  
  protected void generateBinding()
  {
    Binding localBinding = serviceDefinitions.binding().name(model.getBoundPortTypeName().getLocalPart());
    extension.addBindingExtension(localBinding);
    localBinding.type(model.getPortTypeName());
    int i = 1;
    Iterator localIterator = model.getJavaMethods().iterator();
    while (localIterator.hasNext())
    {
      JavaMethodImpl localJavaMethodImpl = (JavaMethodImpl)localIterator.next();
      if (i != 0)
      {
        com.sun.xml.internal.ws.api.model.soap.SOAPBinding localSOAPBinding = localJavaMethodImpl.getBinding();
        SOAPVersion localSOAPVersion = localSOAPBinding.getSOAPVersion();
        Object localObject;
        if (localSOAPVersion == SOAPVersion.SOAP_12)
        {
          localObject = localBinding.soap12Binding();
          ((com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding)localObject).transport(binding.getBindingId().getTransport());
          if (localSOAPBinding.getStyle().equals(SOAPBinding.Style.DOCUMENT)) {
            ((com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding)localObject).style("document");
          } else {
            ((com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding)localObject).style("rpc");
          }
        }
        else
        {
          localObject = localBinding.soapBinding();
          ((com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding)localObject).transport(binding.getBindingId().getTransport());
          if (localSOAPBinding.getStyle().equals(SOAPBinding.Style.DOCUMENT)) {
            ((com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding)localObject).style("document");
          } else {
            ((com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding)localObject).style("rpc");
          }
        }
        i = 0;
      }
      if (binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
        generateSOAP12BindingOperation(localJavaMethodImpl, localBinding);
      } else {
        generateBindingOperation(localJavaMethodImpl, localBinding);
      }
    }
  }
  
  protected void generateBindingOperation(JavaMethodImpl paramJavaMethodImpl, Binding paramBinding)
  {
    BindingOperationType localBindingOperationType = paramBinding.operation().name(paramJavaMethodImpl.getOperationName());
    extension.addBindingOperationExtension(localBindingOperationType, paramJavaMethodImpl);
    String str = model.getTargetNamespace();
    QName localQName = new QName(str, paramJavaMethodImpl.getOperationName());
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    splitParameters(localArrayList1, localArrayList2, paramJavaMethodImpl.getRequestParameters());
    com.sun.xml.internal.ws.api.model.soap.SOAPBinding localSOAPBinding = paramJavaMethodImpl.getBinding();
    localBindingOperationType.soapOperation().soapAction(localSOAPBinding.getSOAPAction());
    StartWithExtensionsType localStartWithExtensionsType = localBindingOperationType.input();
    extension.addBindingOperationInputExtension(localStartWithExtensionsType, paramJavaMethodImpl);
    com.sun.xml.internal.ws.wsdl.writer.document.soap.BodyType localBodyType = (com.sun.xml.internal.ws.wsdl.writer.document.soap.BodyType)localStartWithExtensionsType._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.Body.class);
    boolean bool = localSOAPBinding.getStyle().equals(SOAPBinding.Style.RPC);
    Object localObject2;
    Object localObject4;
    if (localSOAPBinding.getUse() == SOAPBinding.Use.LITERAL)
    {
      localBodyType.use("literal");
      if (localArrayList2.size() > 0)
      {
        if (localArrayList1.size() > 0)
        {
          localObject1 = (ParameterImpl)localArrayList1.iterator().next();
          if (bool)
          {
            localObject2 = new StringBuilder();
            int i = 0;
            Iterator localIterator = ((WrapperParameter)localObject1).getWrapperChildren().iterator();
            while (localIterator.hasNext())
            {
              localObject4 = (ParameterImpl)localIterator.next();
              if (i++ > 0) {
                ((StringBuilder)localObject2).append(' ');
              }
              ((StringBuilder)localObject2).append(((ParameterImpl)localObject4).getPartName());
            }
            localBodyType.parts(((StringBuilder)localObject2).toString());
          }
          else
          {
            localBodyType.parts(((ParameterImpl)localObject1).getPartName());
          }
        }
        else
        {
          localBodyType.parts("");
        }
        generateSOAPHeaders(localStartWithExtensionsType, localArrayList2, localQName);
      }
      if (bool) {
        localBodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI());
      }
    }
    else
    {
      throw new WebServiceException("encoded use is not supported");
    }
    Object localObject3;
    if (paramJavaMethodImpl.getMEP() != MEP.ONE_WAY)
    {
      localArrayList1.clear();
      localArrayList2.clear();
      splitParameters(localArrayList1, localArrayList2, paramJavaMethodImpl.getResponseParameters());
      localObject1 = localBindingOperationType.output();
      extension.addBindingOperationOutputExtension((TypedXmlWriter)localObject1, paramJavaMethodImpl);
      localBodyType = (com.sun.xml.internal.ws.wsdl.writer.document.soap.BodyType)((TypedXmlWriter)localObject1)._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.Body.class);
      localBodyType.use("literal");
      if (localArrayList2.size() > 0)
      {
        localObject2 = new StringBuilder();
        if (localArrayList1.size() > 0)
        {
          localObject3 = localArrayList1.iterator().hasNext() ? (ParameterImpl)localArrayList1.iterator().next() : null;
          if (localObject3 != null) {
            if (bool)
            {
              int j = 0;
              localObject4 = ((WrapperParameter)localObject3).getWrapperChildren().iterator();
              while (((Iterator)localObject4).hasNext())
              {
                ParameterImpl localParameterImpl = (ParameterImpl)((Iterator)localObject4).next();
                if (j++ > 0) {
                  ((StringBuilder)localObject2).append(" ");
                }
                ((StringBuilder)localObject2).append(localParameterImpl.getPartName());
              }
            }
            else
            {
              localObject2 = new StringBuilder(((ParameterImpl)localObject3).getPartName());
            }
          }
        }
        localBodyType.parts(((StringBuilder)localObject2).toString());
        localObject3 = new QName(str, paramJavaMethodImpl.getResponseMessageName());
        generateSOAPHeaders((TypedXmlWriter)localObject1, localArrayList2, (QName)localObject3);
      }
      if (bool) {
        localBodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI());
      }
    }
    Object localObject1 = paramJavaMethodImpl.getCheckedExceptions().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (CheckedExceptionImpl)((Iterator)localObject1).next();
      localObject3 = localBindingOperationType.fault().name(((CheckedExceptionImpl)localObject2).getMessageName());
      extension.addBindingOperationFaultExtension((TypedXmlWriter)localObject3, paramJavaMethodImpl, (CheckedException)localObject2);
      com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPFault localSOAPFault = ((com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPFault)((Fault)localObject3)._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPFault.class)).name(((CheckedExceptionImpl)localObject2).getMessageName());
      localSOAPFault.use("literal");
    }
  }
  
  protected void generateSOAP12BindingOperation(JavaMethodImpl paramJavaMethodImpl, Binding paramBinding)
  {
    BindingOperationType localBindingOperationType = paramBinding.operation().name(paramJavaMethodImpl.getOperationName());
    extension.addBindingOperationExtension(localBindingOperationType, paramJavaMethodImpl);
    String str1 = model.getTargetNamespace();
    QName localQName = new QName(str1, paramJavaMethodImpl.getOperationName());
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    splitParameters(localArrayList1, localArrayList2, paramJavaMethodImpl.getRequestParameters());
    com.sun.xml.internal.ws.api.model.soap.SOAPBinding localSOAPBinding = paramJavaMethodImpl.getBinding();
    String str2 = localSOAPBinding.getSOAPAction();
    if (str2 != null) {
      localBindingOperationType.soap12Operation().soapAction(str2);
    }
    StartWithExtensionsType localStartWithExtensionsType = localBindingOperationType.input();
    extension.addBindingOperationInputExtension(localStartWithExtensionsType, paramJavaMethodImpl);
    com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType localBodyType = (com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType)localStartWithExtensionsType._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
    boolean bool = localSOAPBinding.getStyle().equals(SOAPBinding.Style.RPC);
    Object localObject2;
    Object localObject4;
    if (localSOAPBinding.getUse().equals(SOAPBinding.Use.LITERAL))
    {
      localBodyType.use("literal");
      if (localArrayList2.size() > 0)
      {
        if (localArrayList1.size() > 0)
        {
          localObject1 = (ParameterImpl)localArrayList1.iterator().next();
          if (bool)
          {
            localObject2 = new StringBuilder();
            int i = 0;
            Iterator localIterator = ((WrapperParameter)localObject1).getWrapperChildren().iterator();
            while (localIterator.hasNext())
            {
              localObject4 = (ParameterImpl)localIterator.next();
              if (i++ > 0) {
                ((StringBuilder)localObject2).append(' ');
              }
              ((StringBuilder)localObject2).append(((ParameterImpl)localObject4).getPartName());
            }
            localBodyType.parts(((StringBuilder)localObject2).toString());
          }
          else
          {
            localBodyType.parts(((ParameterImpl)localObject1).getPartName());
          }
        }
        else
        {
          localBodyType.parts("");
        }
        generateSOAP12Headers(localStartWithExtensionsType, localArrayList2, localQName);
      }
      if (bool) {
        localBodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI());
      }
    }
    else
    {
      throw new WebServiceException("encoded use is not supported");
    }
    Object localObject3;
    if (paramJavaMethodImpl.getMEP() != MEP.ONE_WAY)
    {
      localArrayList1.clear();
      localArrayList2.clear();
      splitParameters(localArrayList1, localArrayList2, paramJavaMethodImpl.getResponseParameters());
      localObject1 = localBindingOperationType.output();
      extension.addBindingOperationOutputExtension((TypedXmlWriter)localObject1, paramJavaMethodImpl);
      localBodyType = (com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType)((TypedXmlWriter)localObject1)._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
      localBodyType.use("literal");
      if (localArrayList2.size() > 0)
      {
        if (localArrayList1.size() > 0)
        {
          localObject2 = (ParameterImpl)localArrayList1.iterator().next();
          if (bool)
          {
            localObject3 = new StringBuilder();
            int j = 0;
            localObject4 = ((WrapperParameter)localObject2).getWrapperChildren().iterator();
            while (((Iterator)localObject4).hasNext())
            {
              ParameterImpl localParameterImpl = (ParameterImpl)((Iterator)localObject4).next();
              if (j++ > 0) {
                ((StringBuilder)localObject3).append(" ");
              }
              ((StringBuilder)localObject3).append(localParameterImpl.getPartName());
            }
            localBodyType.parts(((StringBuilder)localObject3).toString());
          }
          else
          {
            localBodyType.parts(((ParameterImpl)localObject2).getPartName());
          }
        }
        else
        {
          localBodyType.parts("");
        }
        localObject2 = new QName(str1, paramJavaMethodImpl.getResponseMessageName());
        generateSOAP12Headers((TypedXmlWriter)localObject1, localArrayList2, (QName)localObject2);
      }
      if (bool) {
        localBodyType.namespace(((ParameterImpl)paramJavaMethodImpl.getRequestParameters().iterator().next()).getName().getNamespaceURI());
      }
    }
    Object localObject1 = paramJavaMethodImpl.getCheckedExceptions().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (CheckedExceptionImpl)((Iterator)localObject1).next();
      localObject3 = localBindingOperationType.fault().name(((CheckedExceptionImpl)localObject2).getMessageName());
      extension.addBindingOperationFaultExtension((TypedXmlWriter)localObject3, paramJavaMethodImpl, (CheckedException)localObject2);
      com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault localSOAPFault = ((com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault)((Fault)localObject3)._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault.class)).name(((CheckedExceptionImpl)localObject2).getMessageName());
      localSOAPFault.use("literal");
    }
  }
  
  protected void splitParameters(List<ParameterImpl> paramList1, List<ParameterImpl> paramList2, List<ParameterImpl> paramList3)
  {
    Iterator localIterator = paramList3.iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      if (isBodyParameter(localParameterImpl)) {
        paramList1.add(localParameterImpl);
      } else {
        paramList2.add(localParameterImpl);
      }
    }
  }
  
  protected void generateSOAPHeaders(TypedXmlWriter paramTypedXmlWriter, List<ParameterImpl> paramList, QName paramQName)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      com.sun.xml.internal.ws.wsdl.writer.document.soap.Header localHeader = (com.sun.xml.internal.ws.wsdl.writer.document.soap.Header)paramTypedXmlWriter._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.Header.class);
      localHeader.message(paramQName);
      localHeader.part(localParameterImpl.getPartName());
      localHeader.use("literal");
    }
  }
  
  protected void generateSOAP12Headers(TypedXmlWriter paramTypedXmlWriter, List<ParameterImpl> paramList, QName paramQName)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
      com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header localHeader = (com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header)paramTypedXmlWriter._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header.class);
      localHeader.message(paramQName);
      localHeader.part(localParameterImpl.getPartName());
      localHeader.use("literal");
    }
  }
  
  protected void generateService()
  {
    QName localQName1 = model.getPortName();
    QName localQName2 = model.getServiceQName();
    Service localService = serviceDefinitions.service().name(localQName2.getLocalPart());
    extension.addServiceExtension(localService);
    Port localPort = localService.port().name(localQName1.getLocalPart());
    localPort.binding(model.getBoundPortTypeName());
    extension.addPortExtension(localPort);
    if (model.getJavaMethods().isEmpty()) {
      return;
    }
    Object localObject;
    if (binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12)
    {
      localObject = (com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPAddress)localPort._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPAddress.class);
      ((com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPAddress)localObject).location(endpointAddress);
    }
    else
    {
      localObject = (com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress)localPort._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress.class);
      ((com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress)localObject).location(endpointAddress);
    }
  }
  
  protected void generateInputMessage(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl)
  {
    ParamType localParamType = paramOperation.input();
    extension.addOperationInputExtension(localParamType, paramJavaMethodImpl);
    localParamType.message(new QName(model.getTargetNamespace(), paramJavaMethodImpl.getRequestMessageName()));
  }
  
  protected void generateOutputMessage(Operation paramOperation, JavaMethodImpl paramJavaMethodImpl)
  {
    ParamType localParamType = paramOperation.output();
    extension.addOperationOutputExtension(localParamType, paramJavaMethodImpl);
    localParamType.message(new QName(model.getTargetNamespace(), paramJavaMethodImpl.getResponseMessageName()));
  }
  
  public Result createOutputFile(String paramString1, String paramString2)
    throws IOException
  {
    if (paramString1 == null) {
      return null;
    }
    Holder localHolder = new Holder();
    value = (schemaPrefix + paramString2);
    Result localResult = wsdlResolver.getSchemaOutput(paramString1, localHolder);
    String str;
    if (localResult == null) {
      str = (String)value;
    } else {
      str = relativize(localResult.getSystemId(), wsdlLocation);
    }
    boolean bool = paramString1.trim().equals("");
    if (!bool)
    {
      com.sun.xml.internal.ws.wsdl.writer.document.xsd.Import localImport = types.schema()._import();
      localImport.namespace(paramString1);
      localImport.schemaLocation(str);
    }
    return localResult;
  }
  
  private Result createInlineSchema(String paramString1, String paramString2)
    throws IOException
  {
    if (paramString1.equals("")) {
      return null;
    }
    TXWResult localTXWResult = new TXWResult(types);
    localTXWResult.setSystemId("");
    return localTXWResult;
  }
  
  protected static String relativize(String paramString1, String paramString2)
  {
    try
    {
      assert (paramString1 != null);
      if (paramString2 == null) {
        return paramString1;
      }
      URI localURI1 = new URI(Util.escapeURI(paramString1));
      URI localURI2 = new URI(Util.escapeURI(paramString2));
      if ((localURI1.isOpaque()) || (localURI2.isOpaque())) {
        return paramString1;
      }
      if ((!Util.equalsIgnoreCase(localURI1.getScheme(), localURI2.getScheme())) || (!Util.equal(localURI1.getAuthority(), localURI2.getAuthority()))) {
        return paramString1;
      }
      String str1 = localURI1.getPath();
      String str2 = localURI2.getPath();
      if (!str2.endsWith("/")) {
        str2 = Util.normalizeUriPath(str2);
      }
      if (str1.equals(str2)) {
        return ".";
      }
      String str3 = calculateRelativePath(str1, str2);
      if (str3 == null) {
        return paramString1;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(str3);
      if (localURI1.getQuery() != null) {
        localStringBuilder.append('?').append(localURI1.getQuery());
      }
      if (localURI1.getFragment() != null) {
        localStringBuilder.append('#').append(localURI1.getFragment());
      }
      return localStringBuilder.toString();
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new InternalError("Error escaping one of these uris:\n\t" + paramString1 + "\n\t" + paramString2);
    }
  }
  
  private static String calculateRelativePath(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return null;
    }
    if (paramString1.startsWith(paramString2)) {
      return paramString1.substring(paramString2.length());
    }
    return "../" + calculateRelativePath(paramString1, Util.getParentUriPath(paramString2));
  }
  
  private void register(WSDLGeneratorExtension paramWSDLGeneratorExtension)
  {
    extensionHandlers.add(paramWSDLGeneratorExtension);
  }
  
  private static class CommentFilter
    implements XmlSerializer
  {
    final XmlSerializer serializer;
    private static final String VERSION_COMMENT = " Generated by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
    
    CommentFilter(XmlSerializer paramXmlSerializer)
    {
      serializer = paramXmlSerializer;
    }
    
    public void startDocument()
    {
      serializer.startDocument();
      comment(new StringBuilder(VERSION_COMMENT));
      text(new StringBuilder("\n"));
    }
    
    public void beginStartTag(String paramString1, String paramString2, String paramString3)
    {
      serializer.beginStartTag(paramString1, paramString2, paramString3);
    }
    
    public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder)
    {
      serializer.writeAttribute(paramString1, paramString2, paramString3, paramStringBuilder);
    }
    
    public void writeXmlns(String paramString1, String paramString2)
    {
      serializer.writeXmlns(paramString1, paramString2);
    }
    
    public void endStartTag(String paramString1, String paramString2, String paramString3)
    {
      serializer.endStartTag(paramString1, paramString2, paramString3);
    }
    
    public void endTag()
    {
      serializer.endTag();
    }
    
    public void text(StringBuilder paramStringBuilder)
    {
      serializer.text(paramStringBuilder);
    }
    
    public void cdata(StringBuilder paramStringBuilder)
    {
      serializer.cdata(paramStringBuilder);
    }
    
    public void comment(StringBuilder paramStringBuilder)
    {
      serializer.comment(paramStringBuilder);
    }
    
    public void endDocument()
    {
      serializer.endDocument();
    }
    
    public void flush()
    {
      serializer.flush();
    }
  }
  
  protected class JAXWSOutputSchemaResolver
    extends SchemaOutputResolver
  {
    ArrayList<DOMResult> nonGlassfishSchemas = null;
    
    protected JAXWSOutputSchemaResolver() {}
    
    public Result createOutput(String paramString1, String paramString2)
      throws IOException
    {
      return inlineSchemas ? WSDLGenerator.this.createInlineSchema(paramString1, paramString2) : nonGlassfishSchemas != null ? nonGlassfishSchemaResult(paramString1, paramString2) : createOutputFile(paramString1, paramString2);
    }
    
    private Result nonGlassfishSchemaResult(String paramString1, String paramString2)
      throws IOException
    {
      DOMResult localDOMResult = new DOMResult();
      localDOMResult.setSystemId("");
      nonGlassfishSchemas.add(localDOMResult);
      return localDOMResult;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\WSDLGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */