package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Messages;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public final class ToXMLStream
  extends ToStream
{
  boolean m_cdataTagOpen = false;
  private static CharInfo m_xmlcharInfo = CharInfo.getCharInfoInternal("com.sun.org.apache.xml.internal.serializer.XMLEntities", "xml");
  
  public ToXMLStream()
  {
    m_charInfo = m_xmlcharInfo;
    initCDATA();
    m_prefixMap = new NamespaceMappings();
  }
  
  public void CopyFrom(ToXMLStream paramToXMLStream)
  {
    m_writer = m_writer;
    String str = paramToXMLStream.getEncoding();
    setEncoding(str);
    setOmitXMLDeclaration(paramToXMLStream.getOmitXMLDeclaration());
    m_ispreserve = m_ispreserve;
    m_preserves = m_preserves;
    m_isprevtext = m_isprevtext;
    m_doIndent = m_doIndent;
    setIndentAmount(paramToXMLStream.getIndentAmount());
    m_startNewLine = m_startNewLine;
    m_needToOutputDocTypeDecl = m_needToOutputDocTypeDecl;
    setDoctypeSystem(paramToXMLStream.getDoctypeSystem());
    setDoctypePublic(paramToXMLStream.getDoctypePublic());
    setStandalone(paramToXMLStream.getStandalone());
    setMediaType(paramToXMLStream.getMediaType());
    m_maxCharacter = m_maxCharacter;
    m_encodingInfo = m_encodingInfo;
    m_spaceBeforeClose = m_spaceBeforeClose;
    m_cdataStartCalled = m_cdataStartCalled;
  }
  
  public void startDocumentInternal()
    throws SAXException
  {
    if (m_needToCallStartDocument)
    {
      super.startDocumentInternal();
      m_needToCallStartDocument = false;
      if (m_inEntityRef) {
        return;
      }
      m_needToOutputDocTypeDecl = true;
      m_startNewLine = false;
      if (!getOmitXMLDeclaration())
      {
        String str1 = Encodings.getMimeEncoding(getEncoding());
        String str2 = getVersion();
        if (str2 == null) {
          str2 = "1.0";
        }
        String str3;
        if (m_standaloneWasSpecified) {
          str3 = " standalone=\"" + getStandalone() + "\"";
        } else {
          str3 = "";
        }
        try
        {
          Writer localWriter = m_writer;
          localWriter.write("<?xml version=\"");
          localWriter.write(str2);
          localWriter.write("\" encoding=\"");
          localWriter.write(str1);
          localWriter.write(34);
          localWriter.write(str3);
          localWriter.write("?>");
          if ((m_doIndent) && ((m_standaloneWasSpecified) || (getDoctypePublic() != null) || (getDoctypeSystem() != null) || (m_isStandalone))) {
            localWriter.write(m_lineSep, 0, m_lineSepLen);
          }
        }
        catch (IOException localIOException)
        {
          throw new SAXException(localIOException);
        }
      }
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    flushPending();
    if ((m_doIndent) && (!m_isprevtext)) {
      try
      {
        outputLineSep();
      }
      catch (IOException localIOException)
      {
        throw new SAXException(localIOException);
      }
    }
    flushWriter();
    if (m_tracer != null) {
      super.fireEndDoc();
    }
  }
  
  public void startPreserving()
    throws SAXException
  {
    m_preserves.push(true);
    m_ispreserve = true;
  }
  
  public void endPreserving()
    throws SAXException
  {
    m_ispreserve = (m_preserves.isEmpty() ? false : m_preserves.pop());
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_inEntityRef) {
      return;
    }
    flushPending();
    if (paramString1.equals("javax.xml.transform.disable-output-escaping")) {
      startNonEscaping();
    } else if (paramString1.equals("javax.xml.transform.enable-output-escaping")) {
      endNonEscaping();
    } else {
      try
      {
        if (m_elemContext.m_startTagOpen)
        {
          closeStartTag();
          m_elemContext.m_startTagOpen = false;
        }
        else if (m_needToCallStartDocument)
        {
          startDocumentInternal();
        }
        if (shouldIndent()) {
          indent();
        }
        Writer localWriter = m_writer;
        localWriter.write("<?");
        localWriter.write(paramString1);
        if ((paramString2.length() > 0) && (!Character.isSpaceChar(paramString2.charAt(0)))) {
          localWriter.write(32);
        }
        int i = paramString2.indexOf("?>");
        if (i >= 0)
        {
          if (i > 0) {
            localWriter.write(paramString2.substring(0, i));
          }
          localWriter.write("? >");
          if (i + 2 < paramString2.length()) {
            localWriter.write(paramString2.substring(i + 2));
          }
        }
        else
        {
          localWriter.write(paramString2);
        }
        localWriter.write(63);
        localWriter.write(62);
        if ((m_elemContext.m_currentElemDepth <= 0) && (m_isStandalone)) {
          localWriter.write(m_lineSep, 0, m_lineSepLen);
        }
        m_startNewLine = true;
      }
      catch (IOException localIOException)
      {
        throw new SAXException(localIOException);
      }
    }
    if (m_tracer != null) {
      super.fireEscapingEvent(paramString1, paramString2);
    }
  }
  
  public void entityReference(String paramString)
    throws SAXException
  {
    if (m_elemContext.m_startTagOpen)
    {
      closeStartTag();
      m_elemContext.m_startTagOpen = false;
    }
    try
    {
      if (shouldIndent()) {
        indent();
      }
      Writer localWriter = m_writer;
      localWriter.write(38);
      localWriter.write(paramString);
      localWriter.write(59);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    if (m_tracer != null) {
      super.fireEntityReference(paramString);
    }
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt)
    throws SAXException
  {
    if (m_elemContext.m_startTagOpen) {
      try
      {
        String str = patchName(paramString1);
        Writer localWriter = m_writer;
        if (((paramInt & 0x1) > 0) && (m_xmlcharInfoonlyQuotAmpLtGt))
        {
          localWriter.write(32);
          localWriter.write(str);
          localWriter.write("=\"");
          localWriter.write(paramString2);
          localWriter.write(34);
        }
        else
        {
          localWriter.write(32);
          localWriter.write(str);
          localWriter.write("=\"");
          writeAttrString(localWriter, paramString2, getEncoding());
          localWriter.write(34);
        }
      }
      catch (IOException localIOException)
      {
        throw new SAXException(localIOException);
      }
    }
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
    throws SAXException
  {
    Object localObject;
    if (m_elemContext.m_startTagOpen)
    {
      boolean bool = addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean);
      if ((bool) && (!paramBoolean) && (!paramString3.startsWith("xmlns")))
      {
        localObject = ensureAttributesNamespaceIsDeclared(paramString1, paramString2, paramString3);
        if ((localObject != null) && (paramString3 != null) && (!paramString3.startsWith((String)localObject))) {
          paramString3 = (String)localObject + ":" + paramString2;
        }
      }
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean);
    }
    else
    {
      String str = Utils.messages.createMessage("ER_ILLEGAL_ATTRIBUTE_POSITION", new Object[] { paramString2 });
      try
      {
        localObject = super.getTransformer();
        ErrorListener localErrorListener = ((Transformer)localObject).getErrorListener();
        if ((null != localErrorListener) && (m_sourceLocator != null)) {
          localErrorListener.warning(new TransformerException(str, m_sourceLocator));
        } else {
          System.out.println(str);
        }
      }
      catch (Exception localException) {}
    }
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    endElement(null, null, paramString);
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_elemContext.m_elementURI == null)
    {
      String str = getPrefixPart(m_elemContext.m_elementName);
      if ((str == null) && ("".equals(paramString1))) {
        m_elemContext.m_elementURI = paramString2;
      }
    }
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  protected boolean pushNamespace(String paramString1, String paramString2)
  {
    try
    {
      if (m_prefixMap.pushNamespace(paramString1, paramString2, m_elemContext.m_currentElemDepth))
      {
        startPrefixMapping(paramString1, paramString2);
        return true;
      }
    }
    catch (SAXException localSAXException) {}
    return false;
  }
  
  public boolean reset()
  {
    boolean bool = false;
    if (super.reset())
    {
      resetToXMLStream();
      bool = true;
    }
    return bool;
  }
  
  private void resetToXMLStream()
  {
    m_cdataTagOpen = false;
  }
  
  private String getXMLVersion()
  {
    String str1 = getVersion();
    if ((str1 == null) || (str1.equals("1.0")))
    {
      str1 = "1.0";
    }
    else if (str1.equals("1.1"))
    {
      str1 = "1.1";
    }
    else
    {
      String str2 = Utils.messages.createMessage("ER_XML_VERSION_NOT_SUPPORTED", new Object[] { str1 });
      try
      {
        Transformer localTransformer = super.getTransformer();
        ErrorListener localErrorListener = localTransformer.getErrorListener();
        if ((null != localErrorListener) && (m_sourceLocator != null)) {
          localErrorListener.warning(new TransformerException(str2, m_sourceLocator));
        } else {
          System.out.println(str2);
        }
      }
      catch (Exception localException) {}
      str1 = "1.0";
    }
    return str1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToXMLStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */