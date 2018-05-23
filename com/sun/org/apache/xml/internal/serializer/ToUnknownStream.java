package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class ToUnknownStream
  extends SerializerBase
{
  private SerializationHandler m_handler = new ToXMLStream();
  private static final String EMPTYSTRING = "";
  private boolean m_wrapped_handler_not_initialized = false;
  private String m_firstElementPrefix;
  private String m_firstElementName;
  private String m_firstElementURI;
  private String m_firstElementLocalName = null;
  private boolean m_firstTagNotEmitted = true;
  private Vector m_namespaceURI = null;
  private Vector m_namespacePrefix = null;
  private boolean m_needToCallStartDocument = false;
  private boolean m_setVersion_called = false;
  private boolean m_setDoctypeSystem_called = false;
  private boolean m_setDoctypePublic_called = false;
  private boolean m_setMediaType_called = false;
  
  public ToUnknownStream() {}
  
  public ContentHandler asContentHandler()
    throws IOException
  {
    return this;
  }
  
  public void close()
  {
    m_handler.close();
  }
  
  public Properties getOutputFormat()
  {
    return m_handler.getOutputFormat();
  }
  
  public OutputStream getOutputStream()
  {
    return m_handler.getOutputStream();
  }
  
  public Writer getWriter()
  {
    return m_handler.getWriter();
  }
  
  public boolean reset()
  {
    return m_handler.reset();
  }
  
  public void serialize(Node paramNode)
    throws IOException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.serialize(paramNode);
  }
  
  public boolean setEscaping(boolean paramBoolean)
    throws SAXException
  {
    return m_handler.setEscaping(paramBoolean);
  }
  
  public void setOutputFormat(Properties paramProperties)
  {
    m_handler.setOutputFormat(paramProperties);
  }
  
  public void setOutputStream(OutputStream paramOutputStream)
  {
    m_handler.setOutputStream(paramOutputStream);
  }
  
  public void setWriter(Writer paramWriter)
  {
    m_handler.setWriter(paramWriter);
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {
    addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5, false);
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean);
  }
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.addAttribute(paramString1, paramString2);
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.addUniqueAttribute(paramString1, paramString2, paramInt);
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    characters(m_charsBuff, 0, i);
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.endElement(paramString);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    startPrefixMapping(paramString1, paramString2, true);
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {
    if ((m_firstTagNotEmitted) && (m_firstElementURI == null) && (m_firstElementName != null))
    {
      String str = getPrefixPart(m_firstElementName);
      if ((str == null) && ("".equals(paramString1))) {
        m_firstElementURI = paramString2;
      }
    }
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean)
    throws SAXException
  {
    boolean bool = false;
    if (m_firstTagNotEmitted)
    {
      if ((m_firstElementName != null) && (paramBoolean))
      {
        flush();
        bool = m_handler.startPrefixMapping(paramString1, paramString2, paramBoolean);
      }
      else
      {
        if (m_namespacePrefix == null)
        {
          m_namespacePrefix = new Vector();
          m_namespaceURI = new Vector();
        }
        m_namespacePrefix.addElement(paramString1);
        m_namespaceURI.addElement(paramString2);
        if ((m_firstElementURI == null) && (paramString1.equals(m_firstElementPrefix))) {
          m_firstElementURI = paramString2;
        }
      }
    }
    else {
      bool = m_handler.startPrefixMapping(paramString1, paramString2, paramBoolean);
    }
    return bool;
  }
  
  public void setVersion(String paramString)
  {
    m_handler.setVersion(paramString);
    m_setVersion_called = true;
  }
  
  public void startDocument()
    throws SAXException
  {
    m_needToCallStartDocument = true;
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    startElement(null, null, paramString, null);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    startElement(paramString1, paramString2, paramString3, null);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (m_needToCallSetDocumentInfo)
    {
      super.setDocumentInfo();
      m_needToCallSetDocumentInfo = false;
    }
    if (m_firstTagNotEmitted)
    {
      if (m_firstElementName != null)
      {
        flush();
        m_handler.startElement(paramString1, paramString2, paramString3, paramAttributes);
      }
      else
      {
        m_wrapped_handler_not_initialized = true;
        m_firstElementName = paramString3;
        m_firstElementPrefix = getPrefixPartUnknown(paramString3);
        m_firstElementURI = paramString1;
        m_firstElementLocalName = paramString2;
        if (m_tracer != null) {
          firePseudoElement(paramString3);
        }
        if (paramAttributes != null) {
          super.addAttributes(paramAttributes);
        }
        if (paramAttributes != null) {
          flush();
        }
      }
    }
    else {
      m_handler.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void comment(String paramString)
    throws SAXException
  {
    if ((m_firstTagNotEmitted) && (m_firstElementName != null))
    {
      emitFirstTag();
    }
    else if (m_needToCallStartDocument)
    {
      m_handler.startDocument();
      m_needToCallStartDocument = false;
    }
    m_handler.comment(paramString);
  }
  
  public String getDoctypePublic()
  {
    return m_handler.getDoctypePublic();
  }
  
  public String getDoctypeSystem()
  {
    return m_handler.getDoctypeSystem();
  }
  
  public String getEncoding()
  {
    return m_handler.getEncoding();
  }
  
  public boolean getIndent()
  {
    return m_handler.getIndent();
  }
  
  public int getIndentAmount()
  {
    return m_handler.getIndentAmount();
  }
  
  public String getMediaType()
  {
    return m_handler.getMediaType();
  }
  
  public boolean getOmitXMLDeclaration()
  {
    return m_handler.getOmitXMLDeclaration();
  }
  
  public String getStandalone()
  {
    return m_handler.getStandalone();
  }
  
  public String getVersion()
  {
    return m_handler.getVersion();
  }
  
  public void setDoctype(String paramString1, String paramString2)
  {
    m_handler.setDoctypePublic(paramString2);
    m_handler.setDoctypeSystem(paramString1);
  }
  
  public void setDoctypePublic(String paramString)
  {
    m_handler.setDoctypePublic(paramString);
    m_setDoctypePublic_called = true;
  }
  
  public void setDoctypeSystem(String paramString)
  {
    m_handler.setDoctypeSystem(paramString);
    m_setDoctypeSystem_called = true;
  }
  
  public void setEncoding(String paramString)
  {
    m_handler.setEncoding(paramString);
  }
  
  public void setIndent(boolean paramBoolean)
  {
    m_handler.setIndent(paramBoolean);
  }
  
  public void setIndentAmount(int paramInt)
  {
    m_handler.setIndentAmount(paramInt);
  }
  
  public void setMediaType(String paramString)
  {
    m_handler.setMediaType(paramString);
    m_setMediaType_called = true;
  }
  
  public void setOmitXMLDeclaration(boolean paramBoolean)
  {
    m_handler.setOmitXMLDeclaration(paramBoolean);
  }
  
  public void setStandalone(String paramString)
  {
    m_handler.setStandalone(paramString);
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {
    m_handler.attributeDecl(paramString1, paramString2, paramString3, paramString4, paramString5);
  }
  
  public void elementDecl(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      emitFirstTag();
    }
    m_handler.elementDecl(paramString1, paramString2);
  }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.externalEntityDecl(paramString1, paramString2, paramString3);
  }
  
  public void internalEntityDecl(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.internalEntityDecl(paramString1, paramString2);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void endDocument()
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.endDocument();
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (m_firstTagNotEmitted)
    {
      flush();
      if ((paramString1 == null) && (m_firstElementURI != null)) {
        paramString1 = m_firstElementURI;
      }
      if ((paramString2 == null) && (m_firstElementLocalName != null)) {
        paramString2 = m_firstElementLocalName;
      }
    }
    m_handler.endElement(paramString1, paramString2, paramString3);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    m_handler.endPrefixMapping(paramString);
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.processingInstruction(paramString1, paramString2);
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    super.setDocumentLocator(paramLocator);
    m_handler.setDocumentLocator(paramLocator);
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    m_handler.skippedEntity(paramString);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      flush();
    }
    m_handler.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void endCDATA()
    throws SAXException
  {
    m_handler.endCDATA();
  }
  
  public void endDTD()
    throws SAXException
  {
    m_handler.endDTD();
  }
  
  public void endEntity(String paramString)
    throws SAXException
  {
    if (m_firstTagNotEmitted) {
      emitFirstTag();
    }
    m_handler.endEntity(paramString);
  }
  
  public void startCDATA()
    throws SAXException
  {
    m_handler.startCDATA();
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    m_handler.startDTD(paramString1, paramString2, paramString3);
  }
  
  public void startEntity(String paramString)
    throws SAXException
  {
    m_handler.startEntity(paramString);
  }
  
  private void initStreamOutput()
    throws SAXException
  {
    boolean bool = isFirstElemHTML();
    if (bool)
    {
      SerializationHandler localSerializationHandler = m_handler;
      Properties localProperties = OutputPropertiesFactory.getDefaultMethodProperties("html");
      Serializer localSerializer = SerializerFactory.getSerializer(localProperties);
      m_handler = ((SerializationHandler)localSerializer);
      Writer localWriter = localSerializationHandler.getWriter();
      if (null != localWriter)
      {
        m_handler.setWriter(localWriter);
      }
      else
      {
        OutputStream localOutputStream = localSerializationHandler.getOutputStream();
        if (null != localOutputStream) {
          m_handler.setOutputStream(localOutputStream);
        }
      }
      m_handler.setVersion(localSerializationHandler.getVersion());
      m_handler.setDoctypeSystem(localSerializationHandler.getDoctypeSystem());
      m_handler.setDoctypePublic(localSerializationHandler.getDoctypePublic());
      m_handler.setMediaType(localSerializationHandler.getMediaType());
      m_handler.setTransformer(localSerializationHandler.getTransformer());
    }
    if (m_needToCallStartDocument)
    {
      m_handler.startDocument();
      m_needToCallStartDocument = false;
    }
    m_wrapped_handler_not_initialized = false;
  }
  
  private void emitFirstTag()
    throws SAXException
  {
    if (m_firstElementName != null)
    {
      if (m_wrapped_handler_not_initialized)
      {
        initStreamOutput();
        m_wrapped_handler_not_initialized = false;
      }
      m_handler.startElement(m_firstElementURI, null, m_firstElementName, m_attributes);
      m_attributes = null;
      if (m_namespacePrefix != null)
      {
        int i = m_namespacePrefix.size();
        for (int j = 0; j < i; j++)
        {
          String str1 = (String)m_namespacePrefix.elementAt(j);
          String str2 = (String)m_namespaceURI.elementAt(j);
          m_handler.startPrefixMapping(str1, str2, false);
        }
        m_namespacePrefix = null;
        m_namespaceURI = null;
      }
      m_firstTagNotEmitted = false;
    }
  }
  
  private String getLocalNameUnknown(String paramString)
  {
    int i = paramString.lastIndexOf(':');
    if (i >= 0) {
      paramString = paramString.substring(i + 1);
    }
    i = paramString.lastIndexOf('@');
    if (i >= 0) {
      paramString = paramString.substring(i + 1);
    }
    return paramString;
  }
  
  private String getPrefixPartUnknown(String paramString)
  {
    int i = paramString.indexOf(':');
    return i > 0 ? paramString.substring(0, i) : "";
  }
  
  private boolean isFirstElemHTML()
  {
    boolean bool = getLocalNameUnknown(m_firstElementName).equalsIgnoreCase("html");
    if ((bool) && (m_firstElementURI != null) && (!"".equals(m_firstElementURI))) {
      bool = false;
    }
    if ((bool) && (m_namespacePrefix != null))
    {
      int i = m_namespacePrefix.size();
      for (int j = 0; j < i; j++)
      {
        String str1 = (String)m_namespacePrefix.elementAt(j);
        String str2 = (String)m_namespaceURI.elementAt(j);
        if ((m_firstElementPrefix != null) && (m_firstElementPrefix.equals(str1)) && (!"".equals(str2)))
        {
          bool = false;
          break;
        }
      }
    }
    return bool;
  }
  
  public DOMSerializer asDOMSerializer()
    throws IOException
  {
    return m_handler.asDOMSerializer();
  }
  
  public void setCdataSectionElements(Vector paramVector)
  {
    m_handler.setCdataSectionElements(paramVector);
  }
  
  public void addAttributes(Attributes paramAttributes)
    throws SAXException
  {
    m_handler.addAttributes(paramAttributes);
  }
  
  public NamespaceMappings getNamespaceMappings()
  {
    NamespaceMappings localNamespaceMappings = null;
    if (m_handler != null) {
      localNamespaceMappings = m_handler.getNamespaceMappings();
    }
    return localNamespaceMappings;
  }
  
  public void flushPending()
    throws SAXException
  {
    flush();
    m_handler.flushPending();
  }
  
  private void flush()
  {
    try
    {
      if (m_firstTagNotEmitted) {
        emitFirstTag();
      }
      if (m_needToCallStartDocument)
      {
        m_handler.startDocument();
        m_needToCallStartDocument = false;
      }
    }
    catch (SAXException localSAXException)
    {
      throw new RuntimeException(localSAXException.toString());
    }
  }
  
  public String getPrefix(String paramString)
  {
    return m_handler.getPrefix(paramString);
  }
  
  public void entityReference(String paramString)
    throws SAXException
  {
    m_handler.entityReference(paramString);
  }
  
  public String getNamespaceURI(String paramString, boolean paramBoolean)
  {
    return m_handler.getNamespaceURI(paramString, paramBoolean);
  }
  
  public String getNamespaceURIFromPrefix(String paramString)
  {
    return m_handler.getNamespaceURIFromPrefix(paramString);
  }
  
  public void setTransformer(Transformer paramTransformer)
  {
    m_handler.setTransformer(paramTransformer);
    if (((paramTransformer instanceof SerializerTrace)) && (((SerializerTrace)paramTransformer).hasTraceListeners())) {
      m_tracer = ((SerializerTrace)paramTransformer);
    } else {
      m_tracer = null;
    }
  }
  
  public Transformer getTransformer()
  {
    return m_handler.getTransformer();
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    m_handler.setContentHandler(paramContentHandler);
  }
  
  public void setSourceLocator(SourceLocator paramSourceLocator)
  {
    m_handler.setSourceLocator(paramSourceLocator);
  }
  
  protected void firePseudoElement(String paramString)
  {
    if (m_tracer != null)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append('<');
      localStringBuffer.append(paramString);
      char[] arrayOfChar = localStringBuffer.toString().toCharArray();
      m_tracer.fireGenerateEvent(11, arrayOfChar, 0, arrayOfChar.length);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToUnknownStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */