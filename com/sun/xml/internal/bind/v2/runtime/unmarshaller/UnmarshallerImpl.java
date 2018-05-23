package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.IDResolver;
import com.sun.xml.internal.bind.api.ClassResolver;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.unmarshaller.Messages;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.helpers.AbstractUnmarshallerImpl;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public final class UnmarshallerImpl
  extends AbstractUnmarshallerImpl
  implements ValidationEventHandler, Closeable
{
  protected final JAXBContextImpl context;
  private Schema schema;
  public final UnmarshallingContext coordinator;
  private Unmarshaller.Listener externalListener;
  private AttachmentUnmarshaller attachmentUnmarshaller;
  private IDResolver idResolver = new DefaultIDResolver();
  private XMLReader reader = null;
  private static final DefaultHandler dummyHandler = new DefaultHandler();
  public static final String FACTORY = "com.sun.xml.internal.bind.ObjectFactory";
  
  public UnmarshallerImpl(JAXBContextImpl paramJAXBContextImpl, AssociationMap paramAssociationMap)
  {
    context = paramJAXBContextImpl;
    coordinator = new UnmarshallingContext(this, paramAssociationMap);
    try
    {
      setEventHandler(this);
    }
    catch (JAXBException localJAXBException)
    {
      throw new AssertionError(localJAXBException);
    }
  }
  
  public UnmarshallerHandler getUnmarshallerHandler()
  {
    return getUnmarshallerHandler(true, null);
  }
  
  protected XMLReader getXMLReader()
    throws JAXBException
  {
    if (reader == null) {
      try
      {
        SAXParserFactory localSAXParserFactory = XmlFactory.createParserFactory(context.disableSecurityProcessing);
        localSAXParserFactory.setValidating(false);
        reader = localSAXParserFactory.newSAXParser().getXMLReader();
      }
      catch (ParserConfigurationException localParserConfigurationException)
      {
        throw new JAXBException(localParserConfigurationException);
      }
      catch (SAXException localSAXException)
      {
        throw new JAXBException(localSAXException);
      }
    }
    return reader;
  }
  
  private SAXConnector getUnmarshallerHandler(boolean paramBoolean, JaxBeanInfo paramJaxBeanInfo)
  {
    Object localObject = createUnmarshallerHandler(null, false, paramJaxBeanInfo);
    if (paramBoolean) {
      localObject = new InterningXmlVisitor((XmlVisitor)localObject);
    }
    return new SAXConnector((XmlVisitor)localObject, null);
  }
  
  public final XmlVisitor createUnmarshallerHandler(InfosetScanner paramInfosetScanner, boolean paramBoolean, JaxBeanInfo paramJaxBeanInfo)
  {
    coordinator.reset(paramInfosetScanner, paramBoolean, paramJaxBeanInfo, idResolver);
    Object localObject = coordinator;
    if (schema != null) {
      localObject = new ValidatingUnmarshaller(schema, (XmlVisitor)localObject);
    }
    if ((attachmentUnmarshaller != null) && (attachmentUnmarshaller.isXOPPackage())) {
      localObject = new MTOMDecorator(this, (XmlVisitor)localObject, attachmentUnmarshaller);
    }
    return (XmlVisitor)localObject;
  }
  
  public static boolean needsInterning(XMLReader paramXMLReader)
  {
    try
    {
      paramXMLReader.setFeature("http://xml.org/sax/features/string-interning", true);
    }
    catch (SAXException localSAXException1) {}
    try
    {
      if (paramXMLReader.getFeature("http://xml.org/sax/features/string-interning")) {
        return false;
      }
    }
    catch (SAXException localSAXException2) {}
    return true;
  }
  
  protected Object unmarshal(XMLReader paramXMLReader, InputSource paramInputSource)
    throws JAXBException
  {
    return unmarshal0(paramXMLReader, paramInputSource, null);
  }
  
  protected <T> JAXBElement<T> unmarshal(XMLReader paramXMLReader, InputSource paramInputSource, Class<T> paramClass)
    throws JAXBException
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (JAXBElement)unmarshal0(paramXMLReader, paramInputSource, getBeanInfo(paramClass));
  }
  
  private Object unmarshal0(XMLReader paramXMLReader, InputSource paramInputSource, JaxBeanInfo paramJaxBeanInfo)
    throws JAXBException
  {
    SAXConnector localSAXConnector = getUnmarshallerHandler(needsInterning(paramXMLReader), paramJaxBeanInfo);
    paramXMLReader.setContentHandler(localSAXConnector);
    paramXMLReader.setErrorHandler(coordinator);
    try
    {
      paramXMLReader.parse(paramInputSource);
    }
    catch (IOException localIOException)
    {
      coordinator.clearStates();
      throw new UnmarshalException(localIOException);
    }
    catch (SAXException localSAXException)
    {
      coordinator.clearStates();
      throw createUnmarshalException(localSAXException);
    }
    Object localObject = localSAXConnector.getResult();
    paramXMLReader.setContentHandler(dummyHandler);
    paramXMLReader.setErrorHandler(dummyHandler);
    return localObject;
  }
  
  public <T> JAXBElement<T> unmarshal(Source paramSource, Class<T> paramClass)
    throws JAXBException
  {
    if ((paramSource instanceof SAXSource))
    {
      SAXSource localSAXSource = (SAXSource)paramSource;
      XMLReader localXMLReader = localSAXSource.getXMLReader();
      if (localXMLReader == null) {
        localXMLReader = getXMLReader();
      }
      return unmarshal(localXMLReader, localSAXSource.getInputSource(), paramClass);
    }
    if ((paramSource instanceof StreamSource)) {
      return unmarshal(getXMLReader(), streamSourceToInputSource((StreamSource)paramSource), paramClass);
    }
    if ((paramSource instanceof DOMSource)) {
      return unmarshal(((DOMSource)paramSource).getNode(), paramClass);
    }
    throw new IllegalArgumentException();
  }
  
  public Object unmarshal0(Source paramSource, JaxBeanInfo paramJaxBeanInfo)
    throws JAXBException
  {
    if ((paramSource instanceof SAXSource))
    {
      SAXSource localSAXSource = (SAXSource)paramSource;
      XMLReader localXMLReader = localSAXSource.getXMLReader();
      if (localXMLReader == null) {
        localXMLReader = getXMLReader();
      }
      return unmarshal0(localXMLReader, localSAXSource.getInputSource(), paramJaxBeanInfo);
    }
    if ((paramSource instanceof StreamSource)) {
      return unmarshal0(getXMLReader(), streamSourceToInputSource((StreamSource)paramSource), paramJaxBeanInfo);
    }
    if ((paramSource instanceof DOMSource)) {
      return unmarshal0(((DOMSource)paramSource).getNode(), paramJaxBeanInfo);
    }
    throw new IllegalArgumentException();
  }
  
  public final ValidationEventHandler getEventHandler()
  {
    try
    {
      return super.getEventHandler();
    }
    catch (JAXBException localJAXBException)
    {
      throw new AssertionError();
    }
  }
  
  public final boolean hasEventHandler()
  {
    return getEventHandler() != this;
  }
  
  public <T> JAXBElement<T> unmarshal(Node paramNode, Class<T> paramClass)
    throws JAXBException
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (JAXBElement)unmarshal0(paramNode, getBeanInfo(paramClass));
  }
  
  public final Object unmarshal(Node paramNode)
    throws JAXBException
  {
    return unmarshal0(paramNode, null);
  }
  
  @Deprecated
  public final Object unmarshal(SAXSource paramSAXSource)
    throws JAXBException
  {
    return super.unmarshal(paramSAXSource);
  }
  
  public final Object unmarshal0(Node paramNode, JaxBeanInfo paramJaxBeanInfo)
    throws JAXBException
  {
    try
    {
      DOMScanner localDOMScanner = new DOMScanner();
      InterningXmlVisitor localInterningXmlVisitor = new InterningXmlVisitor(createUnmarshallerHandler(null, false, paramJaxBeanInfo));
      localDOMScanner.setContentHandler(new SAXConnector(localInterningXmlVisitor, localDOMScanner));
      if (paramNode.getNodeType() == 1) {
        localDOMScanner.scan((Element)paramNode);
      } else if (paramNode.getNodeType() == 9) {
        localDOMScanner.scan((Document)paramNode);
      } else {
        throw new IllegalArgumentException("Unexpected node type: " + paramNode);
      }
      Object localObject = localInterningXmlVisitor.getContext().getResult();
      localInterningXmlVisitor.getContext().clearResult();
      return localObject;
    }
    catch (SAXException localSAXException)
    {
      throw createUnmarshalException(localSAXException);
    }
  }
  
  public Object unmarshal(XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return unmarshal0(paramXMLStreamReader, null);
  }
  
  public <T> JAXBElement<T> unmarshal(XMLStreamReader paramXMLStreamReader, Class<T> paramClass)
    throws JAXBException
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (JAXBElement)unmarshal0(paramXMLStreamReader, getBeanInfo(paramClass));
  }
  
  public Object unmarshal0(XMLStreamReader paramXMLStreamReader, JaxBeanInfo paramJaxBeanInfo)
    throws JAXBException
  {
    if (paramXMLStreamReader == null) {
      throw new IllegalArgumentException(Messages.format("Unmarshaller.NullReader"));
    }
    int i = paramXMLStreamReader.getEventType();
    if ((i != 1) && (i != 7)) {
      throw new IllegalStateException(Messages.format("Unmarshaller.IllegalReaderState", Integer.valueOf(i)));
    }
    XmlVisitor localXmlVisitor = createUnmarshallerHandler(null, false, paramJaxBeanInfo);
    StAXConnector localStAXConnector = StAXStreamConnector.create(paramXMLStreamReader, localXmlVisitor);
    try
    {
      localStAXConnector.bridge();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw handleStreamException(localXMLStreamException);
    }
    Object localObject = localXmlVisitor.getContext().getResult();
    localXmlVisitor.getContext().clearResult();
    return localObject;
  }
  
  public <T> JAXBElement<T> unmarshal(XMLEventReader paramXMLEventReader, Class<T> paramClass)
    throws JAXBException
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    return (JAXBElement)unmarshal0(paramXMLEventReader, getBeanInfo(paramClass));
  }
  
  public Object unmarshal(XMLEventReader paramXMLEventReader)
    throws JAXBException
  {
    return unmarshal0(paramXMLEventReader, null);
  }
  
  private Object unmarshal0(XMLEventReader paramXMLEventReader, JaxBeanInfo paramJaxBeanInfo)
    throws JAXBException
  {
    if (paramXMLEventReader == null) {
      throw new IllegalArgumentException(Messages.format("Unmarshaller.NullReader"));
    }
    try
    {
      XMLEvent localXMLEvent = paramXMLEventReader.peek();
      if ((!localXMLEvent.isStartElement()) && (!localXMLEvent.isStartDocument())) {
        throw new IllegalStateException(Messages.format("Unmarshaller.IllegalReaderState", Integer.valueOf(localXMLEvent.getEventType())));
      }
      boolean bool = paramXMLEventReader.getClass().getName().equals("com.sun.xml.internal.stream.XMLReaderImpl");
      Object localObject = createUnmarshallerHandler(null, false, paramJaxBeanInfo);
      if (!bool) {
        localObject = new InterningXmlVisitor((XmlVisitor)localObject);
      }
      new StAXEventConnector(paramXMLEventReader, (XmlVisitor)localObject).bridge();
      return ((XmlVisitor)localObject).getContext().getResult();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw handleStreamException(localXMLStreamException);
    }
  }
  
  public Object unmarshal0(InputStream paramInputStream, JaxBeanInfo paramJaxBeanInfo)
    throws JAXBException
  {
    return unmarshal0(getXMLReader(), new InputSource(paramInputStream), paramJaxBeanInfo);
  }
  
  private static JAXBException handleStreamException(XMLStreamException paramXMLStreamException)
  {
    Throwable localThrowable = paramXMLStreamException.getNestedException();
    if ((localThrowable instanceof JAXBException)) {
      return (JAXBException)localThrowable;
    }
    if ((localThrowable instanceof SAXException)) {
      return new UnmarshalException(localThrowable);
    }
    return new UnmarshalException(paramXMLStreamException);
  }
  
  public Object getProperty(String paramString)
    throws PropertyException
  {
    if (paramString.equals(IDResolver.class.getName())) {
      return idResolver;
    }
    return super.getProperty(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws PropertyException
  {
    if (paramString.equals("com.sun.xml.internal.bind.ObjectFactory"))
    {
      coordinator.setFactories(paramObject);
      return;
    }
    if (paramString.equals(IDResolver.class.getName()))
    {
      idResolver = ((IDResolver)paramObject);
      return;
    }
    if (paramString.equals(ClassResolver.class.getName()))
    {
      coordinator.classResolver = ((ClassResolver)paramObject);
      return;
    }
    if (paramString.equals(ClassLoader.class.getName()))
    {
      coordinator.classLoader = ((ClassLoader)paramObject);
      return;
    }
    super.setProperty(paramString, paramObject);
  }
  
  public void setSchema(Schema paramSchema)
  {
    schema = paramSchema;
  }
  
  public Schema getSchema()
  {
    return schema;
  }
  
  public AttachmentUnmarshaller getAttachmentUnmarshaller()
  {
    return attachmentUnmarshaller;
  }
  
  public void setAttachmentUnmarshaller(AttachmentUnmarshaller paramAttachmentUnmarshaller)
  {
    attachmentUnmarshaller = paramAttachmentUnmarshaller;
  }
  
  /**
   * @deprecated
   */
  public boolean isValidating()
  {
    throw new UnsupportedOperationException();
  }
  
  /**
   * @deprecated
   */
  public void setValidating(boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public <A extends XmlAdapter> void setAdapter(Class<A> paramClass, A paramA)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    coordinator.putAdapter(paramClass, paramA);
  }
  
  public <A extends XmlAdapter> A getAdapter(Class<A> paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    if (coordinator.containsAdapter(paramClass)) {
      return coordinator.getAdapter(paramClass);
    }
    return null;
  }
  
  public UnmarshalException createUnmarshalException(SAXException paramSAXException)
  {
    return super.createUnmarshalException(paramSAXException);
  }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent)
  {
    return paramValidationEvent.getSeverity() != 2;
  }
  
  private static InputSource streamSourceToInputSource(StreamSource paramStreamSource)
  {
    InputSource localInputSource = new InputSource();
    localInputSource.setSystemId(paramStreamSource.getSystemId());
    localInputSource.setByteStream(paramStreamSource.getInputStream());
    localInputSource.setCharacterStream(paramStreamSource.getReader());
    return localInputSource;
  }
  
  public <T> JaxBeanInfo<T> getBeanInfo(Class<T> paramClass)
    throws JAXBException
  {
    return context.getBeanInfo(paramClass, true);
  }
  
  public Unmarshaller.Listener getListener()
  {
    return externalListener;
  }
  
  public void setListener(Unmarshaller.Listener paramListener)
  {
    externalListener = paramListener;
  }
  
  public UnmarshallingContext getContext()
  {
    return coordinator;
  }
  
  /* Error */
  protected void finalize()
    throws Throwable
  {
    // Byte code:
    //   0: invokestatic 440	com/sun/xml/internal/bind/v2/ClassFactory:cleanCache	()V
    //   3: aload_0
    //   4: invokespecial 486	java/lang/Object:finalize	()V
    //   7: goto +10 -> 17
    //   10: astore_1
    //   11: aload_0
    //   12: invokespecial 486	java/lang/Object:finalize	()V
    //   15: aload_1
    //   16: athrow
    //   17: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	18	0	this	UnmarshallerImpl
    //   10	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	3	10	finally
  }
  
  public void close()
    throws IOException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\UnmarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */