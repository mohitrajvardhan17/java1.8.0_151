package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.DataWriter;
import com.sun.xml.internal.bind.marshaller.DumbEscapeHandler;
import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.marshaller.NioEscapeHandler;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.bind.marshaller.XMLWriter;
import com.sun.xml.internal.bind.v2.runtime.output.C14nXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import com.sun.xml.internal.bind.v2.runtime.output.ForkXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.IndentingUTF8XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.NamespaceContextImpl;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XMLEventWriterOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import com.sun.xml.internal.bind.v2.util.FatalAdapter;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.helpers.AbstractMarshallerImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public final class MarshallerImpl
  extends AbstractMarshallerImpl
  implements ValidationEventHandler
{
  private String indent = "    ";
  private NamespacePrefixMapper prefixMapper = null;
  private CharacterEscapeHandler escapeHandler = null;
  private String header = null;
  final JAXBContextImpl context;
  protected final XMLSerializer serializer;
  private Schema schema;
  private Marshaller.Listener externalListener = null;
  private boolean c14nSupport;
  private Flushable toBeFlushed;
  private Closeable toBeClosed;
  protected static final String INDENT_STRING = "com.sun.xml.internal.bind.indentString";
  protected static final String PREFIX_MAPPER = "com.sun.xml.internal.bind.namespacePrefixMapper";
  protected static final String ENCODING_HANDLER = "com.sun.xml.internal.bind.characterEscapeHandler";
  protected static final String ENCODING_HANDLER2 = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";
  protected static final String XMLDECLARATION = "com.sun.xml.internal.bind.xmlDeclaration";
  protected static final String XML_HEADERS = "com.sun.xml.internal.bind.xmlHeaders";
  protected static final String C14N = "com.sun.xml.internal.bind.c14n";
  protected static final String OBJECT_IDENTITY_CYCLE_DETECTION = "com.sun.xml.internal.bind.objectIdentitityCycleDetection";
  
  public MarshallerImpl(JAXBContextImpl paramJAXBContextImpl, AssociationMap paramAssociationMap)
  {
    context = paramJAXBContextImpl;
    serializer = new XMLSerializer(this);
    c14nSupport = context.c14nSupport;
    try
    {
      setEventHandler(this);
    }
    catch (JAXBException localJAXBException)
    {
      throw new AssertionError(localJAXBException);
    }
  }
  
  public JAXBContextImpl getContext()
  {
    return context;
  }
  
  public void marshal(Object paramObject, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    write(paramObject, createWriter(paramOutputStream), new StAXPostInitAction(paramNamespaceContext, serializer));
  }
  
  public void marshal(Object paramObject, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    write(paramObject, XMLStreamWriterOutput.create(paramXMLStreamWriter, context), new StAXPostInitAction(paramXMLStreamWriter, serializer));
  }
  
  public void marshal(Object paramObject, XMLEventWriter paramXMLEventWriter)
    throws JAXBException
  {
    write(paramObject, new XMLEventWriterOutput(paramXMLEventWriter), new StAXPostInitAction(paramXMLEventWriter, serializer));
  }
  
  public void marshal(Object paramObject, XmlOutput paramXmlOutput)
    throws JAXBException
  {
    write(paramObject, paramXmlOutput, null);
  }
  
  final XmlOutput createXmlOutput(Result paramResult)
    throws JAXBException
  {
    if ((paramResult instanceof SAXResult)) {
      return new SAXOutput(((SAXResult)paramResult).getHandler());
    }
    Object localObject1;
    Object localObject2;
    if ((paramResult instanceof DOMResult))
    {
      localObject1 = ((DOMResult)paramResult).getNode();
      if (localObject1 == null)
      {
        localObject2 = JAXBContextImpl.createDom(getContextdisableSecurityProcessing);
        ((DOMResult)paramResult).setNode((Node)localObject2);
        return new SAXOutput(new SAX2DOMEx((Node)localObject2));
      }
      return new SAXOutput(new SAX2DOMEx((Node)localObject1));
    }
    if ((paramResult instanceof StreamResult))
    {
      localObject1 = (StreamResult)paramResult;
      if (((StreamResult)localObject1).getWriter() != null) {
        return createWriter(((StreamResult)localObject1).getWriter());
      }
      if (((StreamResult)localObject1).getOutputStream() != null) {
        return createWriter(((StreamResult)localObject1).getOutputStream());
      }
      if (((StreamResult)localObject1).getSystemId() != null)
      {
        localObject2 = ((StreamResult)localObject1).getSystemId();
        try
        {
          localObject2 = new URI((String)localObject2).getPath();
        }
        catch (URISyntaxException localURISyntaxException) {}
        try
        {
          FileOutputStream localFileOutputStream = new FileOutputStream((String)localObject2);
          assert (toBeClosed == null);
          toBeClosed = localFileOutputStream;
          return createWriter(localFileOutputStream);
        }
        catch (IOException localIOException)
        {
          throw new MarshalException(localIOException);
        }
      }
    }
    throw new MarshalException(Messages.UNSUPPORTED_RESULT.format(new Object[0]));
  }
  
  final Runnable createPostInitAction(Result paramResult)
  {
    if ((paramResult instanceof DOMResult))
    {
      Node localNode = ((DOMResult)paramResult).getNode();
      return new DomPostInitAction(localNode, serializer);
    }
    return null;
  }
  
  public void marshal(Object paramObject, Result paramResult)
    throws JAXBException
  {
    write(paramObject, createXmlOutput(paramResult), createPostInitAction(paramResult));
  }
  
  protected final <T> void write(Name paramName, JaxBeanInfo<T> paramJaxBeanInfo, T paramT, XmlOutput paramXmlOutput, Runnable paramRunnable)
    throws JAXBException
  {
    try
    {
      return;
    }
    catch (IOException localIOException) {}finally
    {
      cleanUp();
    }
  }
  
  private void write(Object paramObject, XmlOutput paramXmlOutput, Runnable paramRunnable)
    throws JAXBException
  {
    try
    {
      if (paramObject == null) {
        throw new IllegalArgumentException(Messages.NOT_MARSHALLABLE.format(new Object[0]));
      }
      if (schema != null)
      {
        ValidatorHandler localValidatorHandler = schema.newValidatorHandler();
        localValidatorHandler.setErrorHandler(new FatalAdapter(serializer));
        XMLFilterImpl local1 = new XMLFilterImpl()
        {
          public void startPrefixMapping(String paramAnonymousString1, String paramAnonymousString2)
            throws SAXException
          {
            super.startPrefixMapping(paramAnonymousString1.intern(), paramAnonymousString2.intern());
          }
        };
        local1.setContentHandler(localValidatorHandler);
        paramXmlOutput = new ForkXmlOutput(new SAXOutput(local1)
        {
          public void startDocument(XMLSerializer paramAnonymousXMLSerializer, boolean paramAnonymousBoolean, int[] paramAnonymousArrayOfInt, NamespaceContextImpl paramAnonymousNamespaceContextImpl)
            throws SAXException, IOException, XMLStreamException
          {
            super.startDocument(paramAnonymousXMLSerializer, false, paramAnonymousArrayOfInt, paramAnonymousNamespaceContextImpl);
          }
          
          public void endDocument(boolean paramAnonymousBoolean)
            throws SAXException, IOException, XMLStreamException
          {
            super.endDocument(false);
          }
        }, paramXmlOutput);
      }
      try
      {
        prewrite(paramXmlOutput, isFragment(), paramRunnable);
        serializer.childAsRoot(paramObject);
      }
      catch (SAXException localSAXException)
      {
        throw new MarshalException(localSAXException);
      }
      catch (IOException localIOException)
      {
        throw new MarshalException(localIOException);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new MarshalException(localXMLStreamException);
      }
      finally
      {
        serializer.close();
      }
    }
    finally
    {
      cleanUp();
    }
  }
  
  private void cleanUp()
  {
    if (toBeFlushed != null) {
      try
      {
        toBeFlushed.flush();
      }
      catch (IOException localIOException1) {}
    }
    if (toBeClosed != null) {
      try
      {
        toBeClosed.close();
      }
      catch (IOException localIOException2) {}
    }
    toBeFlushed = null;
    toBeClosed = null;
  }
  
  private void prewrite(XmlOutput paramXmlOutput, boolean paramBoolean, Runnable paramRunnable)
    throws IOException, SAXException, XMLStreamException
  {
    serializer.startDocument(paramXmlOutput, paramBoolean, getSchemaLocation(), getNoNSSchemaLocation());
    if (paramRunnable != null) {
      paramRunnable.run();
    }
    if (prefixMapper != null)
    {
      String[] arrayOfString = prefixMapper.getContextualNamespaceDecls();
      if (arrayOfString != null) {
        for (int i = 0; i < arrayOfString.length; i += 2)
        {
          String str1 = arrayOfString[i];
          String str2 = arrayOfString[(i + 1)];
          if ((str2 != null) && (str1 != null)) {
            serializer.addInscopeBinding(str2, str1);
          }
        }
      }
    }
    serializer.setPrefixMapper(prefixMapper);
  }
  
  private void postwrite()
    throws IOException, SAXException, XMLStreamException
  {
    serializer.endDocument();
    serializer.reconcileID();
  }
  
  protected CharacterEscapeHandler createEscapeHandler(String paramString)
  {
    if (escapeHandler != null) {
      return escapeHandler;
    }
    if (paramString.startsWith("UTF")) {
      return MinimumEscapeHandler.theInstance;
    }
    try
    {
      return new NioEscapeHandler(getJavaEncoding(paramString));
    }
    catch (Throwable localThrowable) {}
    return DumbEscapeHandler.theInstance;
  }
  
  public XmlOutput createWriter(Writer paramWriter, String paramString)
  {
    if (!(paramWriter instanceof BufferedWriter)) {
      paramWriter = new BufferedWriter(paramWriter);
    }
    assert (toBeFlushed == null);
    toBeFlushed = paramWriter;
    CharacterEscapeHandler localCharacterEscapeHandler = createEscapeHandler(paramString);
    Object localObject;
    if (isFormattedOutput())
    {
      DataWriter localDataWriter = new DataWriter(paramWriter, paramString, localCharacterEscapeHandler);
      localDataWriter.setIndentStep(indent);
      localObject = localDataWriter;
    }
    else
    {
      localObject = new XMLWriter(paramWriter, paramString, localCharacterEscapeHandler);
    }
    ((XMLWriter)localObject).setXmlDecl(!isFragment());
    ((XMLWriter)localObject).setHeader(header);
    return new SAXOutput((ContentHandler)localObject);
  }
  
  public XmlOutput createWriter(Writer paramWriter)
  {
    return createWriter(paramWriter, getEncoding());
  }
  
  public XmlOutput createWriter(OutputStream paramOutputStream)
    throws JAXBException
  {
    return createWriter(paramOutputStream, getEncoding());
  }
  
  public XmlOutput createWriter(OutputStream paramOutputStream, String paramString)
    throws JAXBException
  {
    if (paramString.equals("UTF-8"))
    {
      Encoded[] arrayOfEncoded = context.getUTF8NameTable();
      Object localObject;
      if (isFormattedOutput()) {
        localObject = new IndentingUTF8XmlOutput(paramOutputStream, indent, arrayOfEncoded, escapeHandler);
      } else if (c14nSupport) {
        localObject = new C14nXmlOutput(paramOutputStream, arrayOfEncoded, context.c14nSupport, escapeHandler);
      } else {
        localObject = new UTF8XmlOutput(paramOutputStream, arrayOfEncoded, escapeHandler);
      }
      if (header != null) {
        ((UTF8XmlOutput)localObject).setHeader(header);
      }
      return (XmlOutput)localObject;
    }
    try
    {
      return createWriter(new OutputStreamWriter(paramOutputStream, getJavaEncoding(paramString)), paramString);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new MarshalException(Messages.UNSUPPORTED_ENCODING.format(new Object[] { paramString }), localUnsupportedEncodingException);
    }
  }
  
  public Object getProperty(String paramString)
    throws PropertyException
  {
    if ("com.sun.xml.internal.bind.indentString".equals(paramString)) {
      return indent;
    }
    if (("com.sun.xml.internal.bind.characterEscapeHandler".equals(paramString)) || ("com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler".equals(paramString))) {
      return escapeHandler;
    }
    if ("com.sun.xml.internal.bind.namespacePrefixMapper".equals(paramString)) {
      return prefixMapper;
    }
    if ("com.sun.xml.internal.bind.xmlDeclaration".equals(paramString)) {
      return Boolean.valueOf(!isFragment());
    }
    if ("com.sun.xml.internal.bind.xmlHeaders".equals(paramString)) {
      return header;
    }
    if ("com.sun.xml.internal.bind.c14n".equals(paramString)) {
      return Boolean.valueOf(c14nSupport);
    }
    if ("com.sun.xml.internal.bind.objectIdentitityCycleDetection".equals(paramString)) {
      return Boolean.valueOf(serializer.getObjectIdentityCycleDetection());
    }
    return super.getProperty(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws PropertyException
  {
    if ("com.sun.xml.internal.bind.indentString".equals(paramString))
    {
      checkString(paramString, paramObject);
      indent = ((String)paramObject);
      return;
    }
    if (("com.sun.xml.internal.bind.characterEscapeHandler".equals(paramString)) || ("com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler".equals(paramString)))
    {
      if (!(paramObject instanceof CharacterEscapeHandler)) {
        throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, CharacterEscapeHandler.class.getName(), paramObject.getClass().getName() }));
      }
      escapeHandler = ((CharacterEscapeHandler)paramObject);
      return;
    }
    if ("com.sun.xml.internal.bind.namespacePrefixMapper".equals(paramString))
    {
      if (!(paramObject instanceof NamespacePrefixMapper)) {
        throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, NamespacePrefixMapper.class.getName(), paramObject.getClass().getName() }));
      }
      prefixMapper = ((NamespacePrefixMapper)paramObject);
      return;
    }
    if ("com.sun.xml.internal.bind.xmlDeclaration".equals(paramString))
    {
      checkBoolean(paramString, paramObject);
      super.setProperty("jaxb.fragment", Boolean.valueOf(!((Boolean)paramObject).booleanValue()));
      return;
    }
    if ("com.sun.xml.internal.bind.xmlHeaders".equals(paramString))
    {
      checkString(paramString, paramObject);
      header = ((String)paramObject);
      return;
    }
    if ("com.sun.xml.internal.bind.c14n".equals(paramString))
    {
      checkBoolean(paramString, paramObject);
      c14nSupport = ((Boolean)paramObject).booleanValue();
      return;
    }
    if ("com.sun.xml.internal.bind.objectIdentitityCycleDetection".equals(paramString))
    {
      checkBoolean(paramString, paramObject);
      serializer.setObjectIdentityCycleDetection(((Boolean)paramObject).booleanValue());
      return;
    }
    super.setProperty(paramString, paramObject);
  }
  
  private void checkBoolean(String paramString, Object paramObject)
    throws PropertyException
  {
    if (!(paramObject instanceof Boolean)) {
      throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, Boolean.class.getName(), paramObject.getClass().getName() }));
    }
  }
  
  private void checkString(String paramString, Object paramObject)
    throws PropertyException
  {
    if (!(paramObject instanceof String)) {
      throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, String.class.getName(), paramObject.getClass().getName() }));
    }
  }
  
  public <A extends XmlAdapter> void setAdapter(Class<A> paramClass, A paramA)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    serializer.putAdapter(paramClass, paramA);
  }
  
  public <A extends XmlAdapter> A getAdapter(Class<A> paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException();
    }
    if (serializer.containsAdapter(paramClass)) {
      return serializer.getAdapter(paramClass);
    }
    return null;
  }
  
  public void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller)
  {
    serializer.attachmentMarshaller = paramAttachmentMarshaller;
  }
  
  public AttachmentMarshaller getAttachmentMarshaller()
  {
    return serializer.attachmentMarshaller;
  }
  
  public Schema getSchema()
  {
    return schema;
  }
  
  public void setSchema(Schema paramSchema)
  {
    schema = paramSchema;
  }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent)
  {
    return false;
  }
  
  public Marshaller.Listener getListener()
  {
    return externalListener;
  }
  
  public void setListener(Marshaller.Listener paramListener)
  {
    externalListener = paramListener;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\MarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */