package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
import com.sun.xml.internal.messaging.saaj.util.MimeHeadersUtil;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.InputSource;

public abstract class SOAPPartImpl
  extends SOAPPart
  implements SOAPDocument
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  protected MimeHeaders headers = new MimeHeaders();
  protected Envelope envelope;
  protected Source source;
  protected SOAPDocumentImpl document = new SOAPDocumentImpl(this);
  private boolean sourceWasSet = false;
  protected boolean omitXmlDecl = true;
  protected String sourceCharsetEncoding = null;
  protected MessageImpl message;
  static final boolean lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");
  
  protected SOAPPartImpl()
  {
    this(null);
  }
  
  protected SOAPPartImpl(MessageImpl paramMessageImpl)
  {
    message = paramMessageImpl;
    headers.setHeader("Content-Type", getContentType());
  }
  
  protected abstract String getContentType();
  
  protected abstract Envelope createEnvelopeFromSource()
    throws SOAPException;
  
  protected abstract Envelope createEmptyEnvelope(String paramString)
    throws SOAPException;
  
  protected abstract SOAPPartImpl duplicateType();
  
  protected String getContentTypeString()
  {
    return getContentType();
  }
  
  public boolean isFastInfoset()
  {
    return message != null ? message.isFastInfoset() : false;
  }
  
  public SOAPEnvelope getEnvelope()
    throws SOAPException
  {
    if (sourceWasSet) {
      sourceWasSet = false;
    }
    lookForEnvelope();
    if (envelope != null)
    {
      if (source != null)
      {
        document.removeChild(envelope);
        envelope = createEnvelopeFromSource();
      }
    }
    else if (source != null)
    {
      envelope = createEnvelopeFromSource();
    }
    else
    {
      envelope = createEmptyEnvelope(null);
      document.insertBefore(envelope, null);
    }
    return envelope;
  }
  
  protected void lookForEnvelope()
    throws SOAPException
  {
    Element localElement = document.doGetDocumentElement();
    if ((localElement == null) || ((localElement instanceof Envelope)))
    {
      envelope = ((EnvelopeImpl)localElement);
    }
    else
    {
      if (!(localElement instanceof ElementImpl))
      {
        log.severe("SAAJ0512.soap.incorrect.factory.used");
        throw new SOAPExceptionImpl("Unable to create envelope: incorrect factory used during tree construction");
      }
      ElementImpl localElementImpl = (ElementImpl)localElement;
      if (localElementImpl.getLocalName().equalsIgnoreCase("Envelope"))
      {
        String str1 = localElementImpl.getPrefix();
        String str2 = str1 == null ? localElementImpl.getNamespaceURI() : localElementImpl.getNamespaceURI(str1);
        if ((!str2.equals("http://schemas.xmlsoap.org/soap/envelope/")) && (!str2.equals("http://www.w3.org/2003/05/soap-envelope")))
        {
          log.severe("SAAJ0513.soap.unknown.ns");
          throw new SOAPVersionMismatchException("Unable to create envelope from given source because the namespace was not recognized");
        }
      }
      else
      {
        log.severe("SAAJ0514.soap.root.elem.not.named.envelope");
        throw new SOAPExceptionImpl("Unable to create envelope from given source because the root element is not named \"Envelope\"");
      }
    }
  }
  
  public void removeAllMimeHeaders()
  {
    headers.removeAllHeaders();
  }
  
  public void removeMimeHeader(String paramString)
  {
    headers.removeHeader(paramString);
  }
  
  public String[] getMimeHeader(String paramString)
  {
    return headers.getHeader(paramString);
  }
  
  public void setMimeHeader(String paramString1, String paramString2)
  {
    headers.setHeader(paramString1, paramString2);
  }
  
  public void addMimeHeader(String paramString1, String paramString2)
  {
    headers.addHeader(paramString1, paramString2);
  }
  
  public Iterator getAllMimeHeaders()
  {
    return headers.getAllHeaders();
  }
  
  public Iterator getMatchingMimeHeaders(String[] paramArrayOfString)
  {
    return headers.getMatchingHeaders(paramArrayOfString);
  }
  
  public Iterator getNonMatchingMimeHeaders(String[] paramArrayOfString)
  {
    return headers.getNonMatchingHeaders(paramArrayOfString);
  }
  
  public Source getContent()
    throws SOAPException
  {
    if (source != null)
    {
      InputStream localInputStream = null;
      Object localObject;
      if ((source instanceof JAXMStreamSource))
      {
        localObject = (StreamSource)source;
        localInputStream = ((StreamSource)localObject).getInputStream();
      }
      else if (FastInfosetReflection.isFastInfosetSource(source))
      {
        localObject = (SAXSource)source;
        localInputStream = ((SAXSource)localObject).getInputSource().getByteStream();
      }
      if (localInputStream != null) {
        try
        {
          localInputStream.reset();
        }
        catch (IOException localIOException) {}
      }
      return source;
    }
    return ((Envelope)getEnvelope()).getContent();
  }
  
  public void setContent(Source paramSource)
    throws SOAPException
  {
    try
    {
      InputStream localInputStream;
      Object localObject;
      if ((paramSource instanceof StreamSource))
      {
        localInputStream = ((StreamSource)paramSource).getInputStream();
        localObject = ((StreamSource)paramSource).getReader();
        if (localInputStream != null)
        {
          source = new JAXMStreamSource(localInputStream);
        }
        else if (localObject != null)
        {
          source = new JAXMStreamSource((Reader)localObject);
        }
        else
        {
          log.severe("SAAJ0544.soap.no.valid.reader.for.src");
          throw new SOAPExceptionImpl("Source does not have a valid Reader or InputStream");
        }
      }
      else if (FastInfosetReflection.isFastInfosetSource(paramSource))
      {
        localInputStream = FastInfosetReflection.FastInfosetSource_getInputStream(paramSource);
        if (!(localInputStream instanceof ByteInputStream))
        {
          localObject = new ByteOutputStream();
          ((ByteOutputStream)localObject).write(localInputStream);
          FastInfosetReflection.FastInfosetSource_setInputStream(paramSource, ((ByteOutputStream)localObject).newInputStream());
        }
        source = paramSource;
      }
      else
      {
        source = paramSource;
      }
      sourceWasSet = true;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      log.severe("SAAJ0545.soap.cannot.set.src.for.part");
      throw new SOAPExceptionImpl("Error setting the source for SOAPPart: " + localException.getMessage());
    }
  }
  
  public InputStream getContentAsStream()
    throws IOException
  {
    if (source != null)
    {
      localObject = null;
      if (((source instanceof StreamSource)) && (!isFastInfoset())) {
        localObject = ((StreamSource)source).getInputStream();
      } else if ((FastInfosetReflection.isFastInfosetSource(source)) && (isFastInfoset())) {
        try
        {
          localObject = FastInfosetReflection.FastInfosetSource_getInputStream(source);
        }
        catch (Exception localException)
        {
          throw new IOException(localException.toString());
        }
      }
      if (localObject != null)
      {
        if (lazyContentLength) {
          return (InputStream)localObject;
        }
        if (!(localObject instanceof ByteInputStream))
        {
          log.severe("SAAJ0546.soap.stream.incorrect.type");
          throw new IOException("Internal error: stream not of the right type");
        }
        return (ByteInputStream)localObject;
      }
    }
    Object localObject = new ByteOutputStream();
    Envelope localEnvelope = null;
    try
    {
      localEnvelope = (Envelope)getEnvelope();
      localEnvelope.output((OutputStream)localObject, isFastInfoset());
    }
    catch (SOAPException localSOAPException)
    {
      log.severe("SAAJ0547.soap.cannot.externalize");
      throw new SOAPIOException("SOAP exception while trying to externalize: ", localSOAPException);
    }
    return ((ByteOutputStream)localObject).newInputStream();
  }
  
  MimeBodyPart getMimePart()
    throws SOAPException
  {
    try
    {
      MimeBodyPart localMimeBodyPart = new MimeBodyPart();
      localMimeBodyPart.setDataHandler(getDataHandler());
      AttachmentPartImpl.copyMimeHeaders(headers, localMimeBodyPart);
      return localMimeBodyPart;
    }
    catch (SOAPException localSOAPException)
    {
      throw localSOAPException;
    }
    catch (Exception localException)
    {
      log.severe("SAAJ0548.soap.cannot.externalize.hdr");
      throw new SOAPExceptionImpl("Unable to externalize header", localException);
    }
  }
  
  MimeHeaders getMimeHeaders()
  {
    return headers;
  }
  
  DataHandler getDataHandler()
  {
    DataSource local1 = new DataSource()
    {
      public OutputStream getOutputStream()
        throws IOException
      {
        throw new IOException("Illegal Operation");
      }
      
      public String getContentType()
      {
        return getContentTypeString();
      }
      
      public String getName()
      {
        return getContentId();
      }
      
      public InputStream getInputStream()
        throws IOException
      {
        return getContentAsStream();
      }
    };
    return new DataHandler(local1);
  }
  
  public SOAPDocumentImpl getDocument()
  {
    handleNewSource();
    return document;
  }
  
  public SOAPPartImpl getSOAPPart()
  {
    return this;
  }
  
  public DocumentType getDoctype()
  {
    return document.getDoctype();
  }
  
  public DOMImplementation getImplementation()
  {
    return document.getImplementation();
  }
  
  public Element getDocumentElement()
  {
    try
    {
      getEnvelope();
    }
    catch (SOAPException localSOAPException) {}
    return document.getDocumentElement();
  }
  
  protected void doGetDocumentElement()
  {
    handleNewSource();
    try
    {
      lookForEnvelope();
    }
    catch (SOAPException localSOAPException) {}
  }
  
  public Element createElement(String paramString)
    throws DOMException
  {
    return document.createElement(paramString);
  }
  
  public DocumentFragment createDocumentFragment()
  {
    return document.createDocumentFragment();
  }
  
  public Text createTextNode(String paramString)
  {
    return document.createTextNode(paramString);
  }
  
  public Comment createComment(String paramString)
  {
    return document.createComment(paramString);
  }
  
  public CDATASection createCDATASection(String paramString)
    throws DOMException
  {
    return document.createCDATASection(paramString);
  }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2)
    throws DOMException
  {
    return document.createProcessingInstruction(paramString1, paramString2);
  }
  
  public Attr createAttribute(String paramString)
    throws DOMException
  {
    return document.createAttribute(paramString);
  }
  
  public EntityReference createEntityReference(String paramString)
    throws DOMException
  {
    return document.createEntityReference(paramString);
  }
  
  public NodeList getElementsByTagName(String paramString)
  {
    handleNewSource();
    return document.getElementsByTagName(paramString);
  }
  
  public Node importNode(Node paramNode, boolean paramBoolean)
    throws DOMException
  {
    handleNewSource();
    return document.importNode(paramNode, paramBoolean);
  }
  
  public Element createElementNS(String paramString1, String paramString2)
    throws DOMException
  {
    return document.createElementNS(paramString1, paramString2);
  }
  
  public Attr createAttributeNS(String paramString1, String paramString2)
    throws DOMException
  {
    return document.createAttributeNS(paramString1, paramString2);
  }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2)
  {
    handleNewSource();
    return document.getElementsByTagNameNS(paramString1, paramString2);
  }
  
  public Element getElementById(String paramString)
  {
    handleNewSource();
    return document.getElementById(paramString);
  }
  
  public Node appendChild(Node paramNode)
    throws DOMException
  {
    handleNewSource();
    return document.appendChild(paramNode);
  }
  
  public Node cloneNode(boolean paramBoolean)
  {
    handleNewSource();
    return document.cloneNode(paramBoolean);
  }
  
  protected SOAPPartImpl doCloneNode()
  {
    handleNewSource();
    SOAPPartImpl localSOAPPartImpl = duplicateType();
    headers = MimeHeadersUtil.copy(headers);
    source = source;
    return localSOAPPartImpl;
  }
  
  public NamedNodeMap getAttributes()
  {
    return document.getAttributes();
  }
  
  public NodeList getChildNodes()
  {
    handleNewSource();
    return document.getChildNodes();
  }
  
  public Node getFirstChild()
  {
    handleNewSource();
    return document.getFirstChild();
  }
  
  public Node getLastChild()
  {
    handleNewSource();
    return document.getLastChild();
  }
  
  public String getLocalName()
  {
    return document.getLocalName();
  }
  
  public String getNamespaceURI()
  {
    return document.getNamespaceURI();
  }
  
  public Node getNextSibling()
  {
    handleNewSource();
    return document.getNextSibling();
  }
  
  public String getNodeName()
  {
    return document.getNodeName();
  }
  
  public short getNodeType()
  {
    return document.getNodeType();
  }
  
  public String getNodeValue()
    throws DOMException
  {
    return document.getNodeValue();
  }
  
  public Document getOwnerDocument()
  {
    return document.getOwnerDocument();
  }
  
  public Node getParentNode()
  {
    return document.getParentNode();
  }
  
  public String getPrefix()
  {
    return document.getPrefix();
  }
  
  public Node getPreviousSibling()
  {
    return document.getPreviousSibling();
  }
  
  public boolean hasAttributes()
  {
    return document.hasAttributes();
  }
  
  public boolean hasChildNodes()
  {
    handleNewSource();
    return document.hasChildNodes();
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    handleNewSource();
    return document.insertBefore(paramNode1, paramNode2);
  }
  
  public boolean isSupported(String paramString1, String paramString2)
  {
    return document.isSupported(paramString1, paramString2);
  }
  
  public void normalize()
  {
    handleNewSource();
    document.normalize();
  }
  
  public Node removeChild(Node paramNode)
    throws DOMException
  {
    handleNewSource();
    return document.removeChild(paramNode);
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    handleNewSource();
    return document.replaceChild(paramNode1, paramNode2);
  }
  
  public void setNodeValue(String paramString)
    throws DOMException
  {
    document.setNodeValue(paramString);
  }
  
  public void setPrefix(String paramString)
    throws DOMException
  {
    document.setPrefix(paramString);
  }
  
  private void handleNewSource()
  {
    if (sourceWasSet) {
      try
      {
        getEnvelope();
      }
      catch (SOAPException localSOAPException) {}
    }
  }
  
  protected XMLDeclarationParser lookForXmlDecl()
    throws SOAPException
  {
    if ((source != null) && ((source instanceof StreamSource)))
    {
      Object localObject = null;
      InputStream localInputStream = ((StreamSource)source).getInputStream();
      if (localInputStream != null)
      {
        if (getSourceCharsetEncoding() == null) {
          localObject = new InputStreamReader(localInputStream);
        } else {
          try
          {
            localObject = new InputStreamReader(localInputStream, getSourceCharsetEncoding());
          }
          catch (UnsupportedEncodingException localUnsupportedEncodingException)
          {
            log.log(Level.SEVERE, "SAAJ0551.soap.unsupported.encoding", new Object[] { getSourceCharsetEncoding() });
            throw new SOAPExceptionImpl("Unsupported encoding " + getSourceCharsetEncoding(), localUnsupportedEncodingException);
          }
        }
      }
      else {
        localObject = ((StreamSource)source).getReader();
      }
      if (localObject != null)
      {
        PushbackReader localPushbackReader = new PushbackReader((Reader)localObject, 4096);
        XMLDeclarationParser localXMLDeclarationParser = new XMLDeclarationParser(localPushbackReader);
        try
        {
          localXMLDeclarationParser.parse();
        }
        catch (Exception localException)
        {
          log.log(Level.SEVERE, "SAAJ0552.soap.xml.decl.parsing.failed");
          throw new SOAPExceptionImpl("XML declaration parsing failed", localException);
        }
        String str = localXMLDeclarationParser.getXmlDeclaration();
        if ((str != null) && (str.length() > 0)) {
          omitXmlDecl = false;
        }
        if (lazyContentLength) {
          source = new StreamSource(localPushbackReader);
        }
        return localXMLDeclarationParser;
      }
    }
    else if ((source == null) || (!(source instanceof DOMSource))) {}
    return null;
  }
  
  public void setSourceCharsetEncoding(String paramString)
  {
    sourceCharsetEncoding = paramString;
  }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2)
    throws DOMException
  {
    handleNewSource();
    return document.renameNode(paramNode, paramString1, paramString2);
  }
  
  public void normalizeDocument()
  {
    document.normalizeDocument();
  }
  
  public DOMConfiguration getDomConfig()
  {
    return document.getDomConfig();
  }
  
  public Node adoptNode(Node paramNode)
    throws DOMException
  {
    handleNewSource();
    return document.adoptNode(paramNode);
  }
  
  public void setDocumentURI(String paramString)
  {
    document.setDocumentURI(paramString);
  }
  
  public String getDocumentURI()
  {
    return document.getDocumentURI();
  }
  
  public void setStrictErrorChecking(boolean paramBoolean)
  {
    document.setStrictErrorChecking(paramBoolean);
  }
  
  public String getInputEncoding()
  {
    return document.getInputEncoding();
  }
  
  public String getXmlEncoding()
  {
    return document.getXmlEncoding();
  }
  
  public boolean getXmlStandalone()
  {
    return document.getXmlStandalone();
  }
  
  public void setXmlStandalone(boolean paramBoolean)
    throws DOMException
  {
    document.setXmlStandalone(paramBoolean);
  }
  
  public String getXmlVersion()
  {
    return document.getXmlVersion();
  }
  
  public void setXmlVersion(String paramString)
    throws DOMException
  {
    document.setXmlVersion(paramString);
  }
  
  public boolean getStrictErrorChecking()
  {
    return document.getStrictErrorChecking();
  }
  
  public String getBaseURI()
  {
    return document.getBaseURI();
  }
  
  public short compareDocumentPosition(Node paramNode)
    throws DOMException
  {
    return document.compareDocumentPosition(paramNode);
  }
  
  public String getTextContent()
    throws DOMException
  {
    return document.getTextContent();
  }
  
  public void setTextContent(String paramString)
    throws DOMException
  {
    document.setTextContent(paramString);
  }
  
  public boolean isSameNode(Node paramNode)
  {
    return document.isSameNode(paramNode);
  }
  
  public String lookupPrefix(String paramString)
  {
    return document.lookupPrefix(paramString);
  }
  
  public boolean isDefaultNamespace(String paramString)
  {
    return document.isDefaultNamespace(paramString);
  }
  
  public String lookupNamespaceURI(String paramString)
  {
    return document.lookupNamespaceURI(paramString);
  }
  
  public boolean isEqualNode(Node paramNode)
  {
    return document.isEqualNode(paramNode);
  }
  
  public Object getFeature(String paramString1, String paramString2)
  {
    return document.getFeature(paramString1, paramString2);
  }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler)
  {
    return document.setUserData(paramString, paramObject, paramUserDataHandler);
  }
  
  public Object getUserData(String paramString)
  {
    return document.getUserData(paramString);
  }
  
  public void recycleNode() {}
  
  public String getValue()
  {
    return null;
  }
  
  public void setValue(String paramString)
  {
    log.severe("SAAJ0571.soappart.setValue.not.defined");
    throw new IllegalStateException("Setting value of a soap part is not defined");
  }
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    log.severe("SAAJ0570.soappart.parent.element.not.defined");
    throw new SOAPExceptionImpl("The parent element of a soap part is not defined");
  }
  
  public SOAPElement getParentElement()
  {
    return null;
  }
  
  public void detachNode() {}
  
  public String getSourceCharsetEncoding()
  {
    return sourceCharsetEncoding;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\SOAPPartImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */