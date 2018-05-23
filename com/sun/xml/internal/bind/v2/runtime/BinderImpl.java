package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.runtime.output.DOMOutput;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.InterningXmlVisitor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.SAXConnector;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BinderImpl<XmlNode>
  extends Binder<XmlNode>
{
  private final JAXBContextImpl context;
  private UnmarshallerImpl unmarshaller;
  private MarshallerImpl marshaller;
  private final InfosetScanner<XmlNode> scanner;
  private final AssociationMap<XmlNode> assoc = new AssociationMap();
  
  BinderImpl(JAXBContextImpl paramJAXBContextImpl, InfosetScanner<XmlNode> paramInfosetScanner)
  {
    context = paramJAXBContextImpl;
    scanner = paramInfosetScanner;
  }
  
  private UnmarshallerImpl getUnmarshaller()
  {
    if (unmarshaller == null) {
      unmarshaller = new UnmarshallerImpl(context, assoc);
    }
    return unmarshaller;
  }
  
  private MarshallerImpl getMarshaller()
  {
    if (marshaller == null) {
      marshaller = new MarshallerImpl(context, assoc);
    }
    return marshaller;
  }
  
  public void marshal(Object paramObject, XmlNode paramXmlNode)
    throws JAXBException
  {
    if ((paramXmlNode == null) || (paramObject == null)) {
      throw new IllegalArgumentException();
    }
    getMarshaller().marshal(paramObject, createOutput(paramXmlNode));
  }
  
  private DOMOutput createOutput(XmlNode paramXmlNode)
  {
    return new DOMOutput((Node)paramXmlNode, assoc);
  }
  
  public Object updateJAXB(XmlNode paramXmlNode)
    throws JAXBException
  {
    return associativeUnmarshal(paramXmlNode, true, null);
  }
  
  public Object unmarshal(XmlNode paramXmlNode)
    throws JAXBException
  {
    return associativeUnmarshal(paramXmlNode, false, null);
  }
  
  public <T> JAXBElement<T> unmarshal(XmlNode paramXmlNode, Class<T> paramClass)
    throws JAXBException
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (JAXBElement)associativeUnmarshal(paramXmlNode, true, paramClass);
  }
  
  public void setSchema(Schema paramSchema)
  {
    getMarshaller().setSchema(paramSchema);
    getUnmarshaller().setSchema(paramSchema);
  }
  
  public Schema getSchema()
  {
    return getUnmarshaller().getSchema();
  }
  
  private Object associativeUnmarshal(XmlNode paramXmlNode, boolean paramBoolean, Class paramClass)
    throws JAXBException
  {
    if (paramXmlNode == null) {
      throw new IllegalArgumentException();
    }
    JaxBeanInfo localJaxBeanInfo = null;
    if (paramClass != null) {
      localJaxBeanInfo = context.getBeanInfo(paramClass, true);
    }
    InterningXmlVisitor localInterningXmlVisitor = new InterningXmlVisitor(getUnmarshaller().createUnmarshallerHandler(scanner, paramBoolean, localJaxBeanInfo));
    scanner.setContentHandler(new SAXConnector(localInterningXmlVisitor, scanner.getLocator()));
    try
    {
      scanner.scan(paramXmlNode);
    }
    catch (SAXException localSAXException)
    {
      throw unmarshaller.createUnmarshalException(localSAXException);
    }
    return localInterningXmlVisitor.getContext().getResult();
  }
  
  public XmlNode getXMLNode(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException();
    }
    AssociationMap.Entry localEntry = assoc.byPeer(paramObject);
    if (localEntry == null) {
      return null;
    }
    return (XmlNode)localEntry.element();
  }
  
  public Object getJAXBNode(XmlNode paramXmlNode)
  {
    if (paramXmlNode == null) {
      throw new IllegalArgumentException();
    }
    AssociationMap.Entry localEntry = assoc.byElement(paramXmlNode);
    if (localEntry == null) {
      return null;
    }
    if (localEntry.outer() != null) {
      return localEntry.outer();
    }
    return localEntry.inner();
  }
  
  public XmlNode updateXML(Object paramObject)
    throws JAXBException
  {
    return (XmlNode)updateXML(paramObject, getXMLNode(paramObject));
  }
  
  public XmlNode updateXML(Object paramObject, XmlNode paramXmlNode)
    throws JAXBException
  {
    if ((paramObject == null) || (paramXmlNode == null)) {
      throw new IllegalArgumentException();
    }
    Element localElement = (Element)paramXmlNode;
    Node localNode1 = localElement.getNextSibling();
    Node localNode2 = localElement.getParentNode();
    localNode2.removeChild(localElement);
    JaxBeanInfo localJaxBeanInfo = context.getBeanInfo(paramObject, true);
    if (!localJaxBeanInfo.isElement()) {
      paramObject = new JAXBElement(new QName(localElement.getNamespaceURI(), localElement.getLocalName()), jaxbType, paramObject);
    }
    getMarshaller().marshal(paramObject, localNode2);
    Node localNode3 = localNode2.getLastChild();
    localNode2.removeChild(localNode3);
    localNode2.insertBefore(localNode3, localNode1);
    return localNode3;
  }
  
  public void setEventHandler(ValidationEventHandler paramValidationEventHandler)
    throws JAXBException
  {
    getUnmarshaller().setEventHandler(paramValidationEventHandler);
    getMarshaller().setEventHandler(paramValidationEventHandler);
  }
  
  public ValidationEventHandler getEventHandler()
  {
    return getUnmarshaller().getEventHandler();
  }
  
  public Object getProperty(String paramString)
    throws PropertyException
  {
    if (paramString == null) {
      throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format(new Object[0]));
    }
    if (excludeProperty(paramString)) {
      throw new PropertyException(paramString);
    }
    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      localObject1 = getMarshaller().getProperty(paramString);
      return localObject1;
    }
    catch (PropertyException localPropertyException1)
    {
      localObject2 = localPropertyException1;
      try
      {
        localObject1 = getUnmarshaller().getProperty(paramString);
        return localObject1;
      }
      catch (PropertyException localPropertyException2)
      {
        localObject2 = localPropertyException2;
        ((PropertyException)localObject2).setStackTrace(Thread.currentThread().getStackTrace());
        throw ((Throwable)localObject2);
      }
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws PropertyException
  {
    if (paramString == null) {
      throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format(new Object[0]));
    }
    if (excludeProperty(paramString)) {
      throw new PropertyException(paramString, paramObject);
    }
    Object localObject = null;
    try
    {
      getMarshaller().setProperty(paramString, paramObject);
      return;
    }
    catch (PropertyException localPropertyException1)
    {
      localObject = localPropertyException1;
      try
      {
        getUnmarshaller().setProperty(paramString, paramObject);
        return;
      }
      catch (PropertyException localPropertyException2)
      {
        localObject = localPropertyException2;
        ((PropertyException)localObject).setStackTrace(Thread.currentThread().getStackTrace());
        throw ((Throwable)localObject);
      }
    }
  }
  
  private boolean excludeProperty(String paramString)
  {
    return (paramString.equals("com.sun.xml.internal.bind.characterEscapeHandler")) || (paramString.equals("com.sun.xml.internal.bind.xmlDeclaration")) || (paramString.equals("com.sun.xml.internal.bind.xmlHeaders"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BinderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */