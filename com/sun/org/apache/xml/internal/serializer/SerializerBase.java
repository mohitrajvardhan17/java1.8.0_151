package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Messages;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;

public abstract class SerializerBase
  implements SerializationHandler, SerializerConstants
{
  protected boolean m_needToCallStartDocument = true;
  protected boolean m_cdataTagOpen = false;
  protected AttributesImplSerializer m_attributes = new AttributesImplSerializer();
  protected boolean m_inEntityRef = false;
  protected boolean m_inExternalDTD = false;
  private String m_doctypeSystem;
  private String m_doctypePublic;
  boolean m_needToOutputDocTypeDecl = true;
  private String m_encoding = null;
  private boolean m_shouldNotWriteXMLHeader = false;
  private String m_standalone;
  protected boolean m_standaloneWasSpecified = false;
  protected boolean m_isStandalone = false;
  protected boolean m_doIndent = false;
  protected int m_indentAmount = 0;
  private String m_version = null;
  private String m_mediatype;
  private Transformer m_transformer;
  protected Vector m_cdataSectionElements = null;
  protected NamespaceMappings m_prefixMap;
  protected SerializerTrace m_tracer;
  protected SourceLocator m_sourceLocator;
  protected Writer m_writer = null;
  protected ElemContext m_elemContext = new ElemContext();
  protected char[] m_charsBuff = new char[60];
  protected char[] m_attrBuff = new char[30];
  private Locator m_locator = null;
  protected boolean m_needToCallSetDocumentInfo = true;
  
  public SerializerBase() {}
  
  protected void fireEndElem(String paramString)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(4, paramString, (Attributes)null);
    }
  }
  
  protected void fireCharEvent(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(5, paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void comment(String paramString)
    throws SAXException
  {
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    comment(m_charsBuff, 0, i);
  }
  
  protected String patchName(String paramString)
  {
    int i = paramString.lastIndexOf(':');
    if (i > 0)
    {
      int j = paramString.indexOf(':');
      String str1 = paramString.substring(0, j);
      String str2 = paramString.substring(i + 1);
      String str3 = m_prefixMap.lookupNamespace(str1);
      if ((str3 != null) && (str3.length() == 0)) {
        return str2;
      }
      if (j != i) {
        return str1 + ':' + str2;
      }
    }
    return paramString;
  }
  
  protected static String getLocalName(String paramString)
  {
    int i = paramString.lastIndexOf(':');
    return i > 0 ? paramString.substring(i + 1) : paramString;
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    m_locator = paramLocator;
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
    throws SAXException
  {
    if (m_elemContext.m_startTagOpen) {
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean);
    }
  }
  
  public boolean addAttributeAlways(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
  {
    int i;
    if ((paramString2 == null) || (paramString1 == null) || (paramString1.length() == 0)) {
      i = m_attributes.getIndex(paramString3);
    } else {
      i = m_attributes.getIndex(paramString1, paramString2);
    }
    boolean bool;
    if (i >= 0)
    {
      m_attributes.setValue(i, paramString5);
      bool = false;
    }
    else
    {
      m_attributes.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
      bool = true;
    }
    return bool;
  }
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (m_elemContext.m_startTagOpen)
    {
      String str1 = patchName(paramString1);
      String str2 = getLocalName(str1);
      String str3 = getNamespaceURI(str1, false);
      addAttributeAlways(str3, str2, str1, "CDATA", paramString2, false);
    }
  }
  
  public void addXSLAttribute(String paramString1, String paramString2, String paramString3)
  {
    if (m_elemContext.m_startTagOpen)
    {
      String str1 = patchName(paramString1);
      String str2 = getLocalName(str1);
      addAttributeAlways(paramString3, str2, str1, "CDATA", paramString2, true);
    }
  }
  
  public void addAttributes(Attributes paramAttributes)
    throws SAXException
  {
    int i = paramAttributes.getLength();
    for (int j = 0; j < i; j++)
    {
      String str = paramAttributes.getURI(j);
      if (null == str) {
        str = "";
      }
      addAttributeAlways(str, paramAttributes.getLocalName(j), paramAttributes.getQName(j), paramAttributes.getType(j), paramAttributes.getValue(j), false);
    }
  }
  
  public ContentHandler asContentHandler()
    throws IOException
  {
    return this;
  }
  
  public void endEntity(String paramString)
    throws SAXException
  {
    if (paramString.equals("[dtd]")) {
      m_inExternalDTD = false;
    }
    m_inEntityRef = false;
    if (m_tracer != null) {
      fireEndEntity(paramString);
    }
  }
  
  public void close() {}
  
  protected void initCDATA() {}
  
  public String getEncoding()
  {
    return m_encoding;
  }
  
  public void setEncoding(String paramString)
  {
    m_encoding = paramString;
  }
  
  public void setOmitXMLDeclaration(boolean paramBoolean)
  {
    m_shouldNotWriteXMLHeader = paramBoolean;
  }
  
  public boolean getOmitXMLDeclaration()
  {
    return m_shouldNotWriteXMLHeader;
  }
  
  public String getDoctypePublic()
  {
    return m_doctypePublic;
  }
  
  public void setDoctypePublic(String paramString)
  {
    m_doctypePublic = paramString;
  }
  
  public String getDoctypeSystem()
  {
    return m_doctypeSystem;
  }
  
  public void setDoctypeSystem(String paramString)
  {
    m_doctypeSystem = paramString;
  }
  
  public void setDoctype(String paramString1, String paramString2)
  {
    m_doctypeSystem = paramString1;
    m_doctypePublic = paramString2;
  }
  
  public void setStandalone(String paramString)
  {
    if (paramString != null)
    {
      m_standaloneWasSpecified = true;
      setStandaloneInternal(paramString);
    }
  }
  
  protected void setStandaloneInternal(String paramString)
  {
    if ("yes".equals(paramString)) {
      m_standalone = "yes";
    } else {
      m_standalone = "no";
    }
  }
  
  public String getStandalone()
  {
    return m_standalone;
  }
  
  public boolean getIndent()
  {
    return m_doIndent;
  }
  
  public String getMediaType()
  {
    return m_mediatype;
  }
  
  public String getVersion()
  {
    return m_version;
  }
  
  public void setVersion(String paramString)
  {
    m_version = paramString;
  }
  
  public void setMediaType(String paramString)
  {
    m_mediatype = paramString;
  }
  
  public int getIndentAmount()
  {
    return m_indentAmount;
  }
  
  public void setIndentAmount(int paramInt)
  {
    m_indentAmount = paramInt;
  }
  
  public void setIndent(boolean paramBoolean)
  {
    m_doIndent = paramBoolean;
  }
  
  public void setIsStandalone(boolean paramBoolean)
  {
    m_isStandalone = paramBoolean;
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public DOMSerializer asDOMSerializer()
    throws IOException
  {
    return this;
  }
  
  protected boolean isCdataSection()
  {
    boolean bool = false;
    if (null != m_cdataSectionElements)
    {
      if (m_elemContext.m_elementLocalName == null) {
        m_elemContext.m_elementLocalName = getLocalName(m_elemContext.m_elementName);
      }
      if (m_elemContext.m_elementURI == null)
      {
        String str1 = getPrefixPart(m_elemContext.m_elementName);
        if (str1 != null) {
          m_elemContext.m_elementURI = m_prefixMap.lookupNamespace(str1);
        }
      }
      if ((null != m_elemContext.m_elementURI) && (m_elemContext.m_elementURI.length() == 0)) {
        m_elemContext.m_elementURI = null;
      }
      int i = m_cdataSectionElements.size();
      for (int j = 0; j < i; j += 2)
      {
        String str2 = (String)m_cdataSectionElements.elementAt(j);
        String str3 = (String)m_cdataSectionElements.elementAt(j + 1);
        if ((str3.equals(m_elemContext.m_elementLocalName)) && (subPartMatch(m_elemContext.m_elementURI, str2)))
        {
          bool = true;
          break;
        }
      }
    }
    return bool;
  }
  
  private static final boolean subPartMatch(String paramString1, String paramString2)
  {
    return (paramString1 == paramString2) || ((null != paramString1) && (paramString1.equals(paramString2)));
  }
  
  protected static final String getPrefixPart(String paramString)
  {
    int i = paramString.indexOf(':');
    return i > 0 ? paramString.substring(0, i) : null;
  }
  
  public NamespaceMappings getNamespaceMappings()
  {
    return m_prefixMap;
  }
  
  public String getPrefix(String paramString)
  {
    String str = m_prefixMap.lookupPrefix(paramString);
    return str;
  }
  
  public String getNamespaceURI(String paramString, boolean paramBoolean)
  {
    String str1 = "";
    int i = paramString.lastIndexOf(':');
    String str2 = i > 0 ? paramString.substring(0, i) : "";
    if (((!"".equals(str2)) || (paramBoolean)) && (m_prefixMap != null))
    {
      str1 = m_prefixMap.lookupNamespace(str2);
      if ((str1 == null) && (!str2.equals("xmlns"))) {
        throw new RuntimeException(Utils.messages.createMessage("ER_NAMESPACE_PREFIX", new Object[] { paramString.substring(0, i) }));
      }
    }
    return str1;
  }
  
  public String getNamespaceURIFromPrefix(String paramString)
  {
    String str = null;
    if (m_prefixMap != null) {
      str = m_prefixMap.lookupNamespace(paramString);
    }
    return str;
  }
  
  public void entityReference(String paramString)
    throws SAXException
  {
    flushPending();
    startEntity(paramString);
    endEntity(paramString);
    if (m_tracer != null) {
      fireEntityReference(paramString);
    }
  }
  
  public void setTransformer(Transformer paramTransformer)
  {
    m_transformer = paramTransformer;
    if (((m_transformer instanceof SerializerTrace)) && (((SerializerTrace)m_transformer).hasTraceListeners())) {
      m_tracer = ((SerializerTrace)m_transformer);
    } else {
      m_tracer = null;
    }
  }
  
  public Transformer getTransformer()
  {
    return m_transformer;
  }
  
  public void characters(Node paramNode)
    throws SAXException
  {
    flushPending();
    String str = paramNode.getNodeValue();
    if (str != null)
    {
      int i = str.length();
      if (i > m_charsBuff.length) {
        m_charsBuff = new char[i * 2 + 1];
      }
      str.getChars(0, i, m_charsBuff, 0);
      characters(m_charsBuff, 0, i);
    }
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {}
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    m_elemContext.m_startTagOpen = false;
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {}
  
  protected void fireStartEntity(String paramString)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(9, paramString);
    }
  }
  
  private void flushMyWriter()
  {
    if (m_writer != null) {
      try
      {
        m_writer.flush();
      }
      catch (IOException localIOException) {}
    }
  }
  
  protected void fireCDATAEvent(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(10, paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  protected void fireCommentEvent(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(8, new String(paramArrayOfChar, paramInt1, paramInt2));
    }
  }
  
  public void fireEndEntity(String paramString)
    throws SAXException
  {
    if (m_tracer != null) {
      flushMyWriter();
    }
  }
  
  protected void fireStartDoc()
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(1);
    }
  }
  
  protected void fireEndDoc()
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(2);
    }
  }
  
  protected void fireStartElem(String paramString)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(3, paramString, m_attributes);
    }
  }
  
  protected void fireEscapingEvent(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(7, paramString1, paramString2);
    }
  }
  
  protected void fireEntityReference(String paramString)
    throws SAXException
  {
    if (m_tracer != null)
    {
      flushMyWriter();
      m_tracer.fireGenerateEvent(9, paramString, (Attributes)null);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    startDocumentInternal();
    m_needToCallStartDocument = false;
  }
  
  protected void startDocumentInternal()
    throws SAXException
  {
    if (m_tracer != null) {
      fireStartDoc();
    }
  }
  
  protected void setDocumentInfo()
  {
    if (m_locator == null) {
      return;
    }
    try
    {
      String str = ((Locator2)m_locator).getXMLVersion();
      if (str != null) {
        setVersion(str);
      }
    }
    catch (ClassCastException localClassCastException) {}
  }
  
  public void setSourceLocator(SourceLocator paramSourceLocator)
  {
    m_sourceLocator = paramSourceLocator;
  }
  
  public void setNamespaceMappings(NamespaceMappings paramNamespaceMappings)
  {
    m_prefixMap = paramNamespaceMappings;
  }
  
  public boolean reset()
  {
    resetSerializerBase();
    return true;
  }
  
  private void resetSerializerBase()
  {
    m_attributes.clear();
    m_cdataSectionElements = null;
    m_elemContext = new ElemContext();
    m_doctypePublic = null;
    m_doctypeSystem = null;
    m_doIndent = false;
    m_encoding = null;
    m_indentAmount = 0;
    m_inEntityRef = false;
    m_inExternalDTD = false;
    m_mediatype = null;
    m_needToCallStartDocument = true;
    m_needToOutputDocTypeDecl = false;
    if (m_prefixMap != null) {
      m_prefixMap.reset();
    }
    m_shouldNotWriteXMLHeader = false;
    m_sourceLocator = null;
    m_standalone = null;
    m_standaloneWasSpecified = false;
    m_tracer = null;
    m_transformer = null;
    m_version = null;
  }
  
  final boolean inTemporaryOutputState()
  {
    return getEncoding() == null;
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {
    if (m_elemContext.m_startTagOpen) {
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, false);
    }
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {}
  
  public void setDTDEntityExpansion(boolean paramBoolean) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\SerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */