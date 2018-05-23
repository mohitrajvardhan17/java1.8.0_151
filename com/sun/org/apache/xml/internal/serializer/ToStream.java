package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.Messages;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class ToStream
  extends SerializerBase
{
  private static final String COMMENT_BEGIN = "<!--";
  private static final String COMMENT_END = "-->";
  protected BoolStack m_disableOutputEscapingStates = new BoolStack();
  EncodingInfo m_encodingInfo = new EncodingInfo(null, null);
  Method m_canConvertMeth;
  boolean m_triedToGetConverter = false;
  Object m_charToByteConverter = null;
  protected BoolStack m_preserves = new BoolStack();
  protected boolean m_ispreserve = false;
  protected boolean m_isprevtext = false;
  protected int m_maxCharacter = Encodings.getLastPrintable();
  protected char[] m_lineSep = SecuritySupport.getSystemProperty("line.separator").toCharArray();
  protected boolean m_lineSepUse = true;
  protected int m_lineSepLen = m_lineSep.length;
  protected CharInfo m_charInfo;
  boolean m_shouldFlush = true;
  protected boolean m_spaceBeforeClose = false;
  boolean m_startNewLine;
  protected boolean m_inDoctype = false;
  boolean m_isUTF8 = false;
  protected Properties m_format;
  protected boolean m_cdataStartCalled = false;
  private boolean m_expandDTDEntities = true;
  private boolean m_escaping = true;
  
  public ToStream() {}
  
  protected void closeCDATA()
    throws SAXException
  {
    try
    {
      m_writer.write("]]>");
      m_cdataTagOpen = false;
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void serialize(Node paramNode)
    throws IOException
  {
    try
    {
      TreeWalker localTreeWalker = new TreeWalker(this);
      localTreeWalker.traverse(paramNode);
    }
    catch (SAXException localSAXException)
    {
      throw new WrappedRuntimeException(localSAXException);
    }
  }
  
  static final boolean isUTF16Surrogate(char paramChar)
  {
    return (paramChar & 0xFC00) == 55296;
  }
  
  protected final void flushWriter()
    throws SAXException
  {
    Writer localWriter = m_writer;
    if (null != localWriter) {
      try
      {
        if ((localWriter instanceof WriterToUTF8Buffered)) {
          if (m_shouldFlush) {
            ((WriterToUTF8Buffered)localWriter).flush();
          } else {
            ((WriterToUTF8Buffered)localWriter).flushBuffer();
          }
        }
        if ((localWriter instanceof WriterToASCI))
        {
          if (m_shouldFlush) {
            localWriter.flush();
          }
        }
        else {
          localWriter.flush();
        }
      }
      catch (IOException localIOException)
      {
        throw new SAXException(localIOException);
      }
    }
  }
  
  public OutputStream getOutputStream()
  {
    if ((m_writer instanceof WriterToUTF8Buffered)) {
      return ((WriterToUTF8Buffered)m_writer).getOutputStream();
    }
    if ((m_writer instanceof WriterToASCI)) {
      return ((WriterToASCI)m_writer).getOutputStream();
    }
    return null;
  }
  
  public void elementDecl(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_inExternalDTD) {
      return;
    }
    try
    {
      Writer localWriter = m_writer;
      DTDprolog();
      localWriter.write("<!ELEMENT ");
      localWriter.write(paramString1);
      localWriter.write(32);
      localWriter.write(paramString2);
      localWriter.write(62);
      localWriter.write(m_lineSep, 0, m_lineSepLen);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void internalEntityDecl(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_inExternalDTD) {
      return;
    }
    try
    {
      DTDprolog();
      outputEntityDecl(paramString1, paramString2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  void outputEntityDecl(String paramString1, String paramString2)
    throws IOException
  {
    Writer localWriter = m_writer;
    localWriter.write("<!ENTITY ");
    localWriter.write(paramString1);
    localWriter.write(" \"");
    localWriter.write(paramString2);
    localWriter.write("\">");
    localWriter.write(m_lineSep, 0, m_lineSepLen);
  }
  
  protected final void outputLineSep()
    throws IOException
  {
    m_writer.write(m_lineSep, 0, m_lineSepLen);
  }
  
  public void setOutputFormat(Properties paramProperties)
  {
    boolean bool = m_shouldFlush;
    init(m_writer, paramProperties, false, false);
    m_shouldFlush = bool;
  }
  
  private synchronized void init(Writer paramWriter, Properties paramProperties, boolean paramBoolean1, boolean paramBoolean2)
  {
    m_shouldFlush = paramBoolean2;
    if ((m_tracer != null) && (!(paramWriter instanceof SerializerTraceWriter))) {
      m_writer = new SerializerTraceWriter(paramWriter, m_tracer);
    } else {
      m_writer = paramWriter;
    }
    m_format = paramProperties;
    setCdataSectionElements("cdata-section-elements", paramProperties);
    setIndentAmount(OutputPropertyUtils.getIntProperty("{http://xml.apache.org/xalan}indent-amount", paramProperties));
    setIndent(OutputPropertyUtils.getBooleanProperty("indent", paramProperties));
    String str1 = paramProperties.getProperty("{http://xml.apache.org/xalan}line-separator");
    if (str1 != null)
    {
      m_lineSep = str1.toCharArray();
      m_lineSepLen = str1.length();
    }
    boolean bool = OutputPropertyUtils.getBooleanProperty("omit-xml-declaration", paramProperties);
    setOmitXMLDeclaration(bool);
    setDoctypeSystem(paramProperties.getProperty("doctype-system"));
    String str2 = paramProperties.getProperty("doctype-public");
    setDoctypePublic(str2);
    if (paramProperties.get("standalone") != null)
    {
      str3 = paramProperties.getProperty("standalone");
      if (paramBoolean1) {
        setStandaloneInternal(str3);
      } else {
        setStandalone(str3);
      }
    }
    setMediaType(paramProperties.getProperty("media-type"));
    if ((null != str2) && (str2.startsWith("-//W3C//DTD XHTML"))) {
      m_spaceBeforeClose = true;
    }
    String str3 = getVersion();
    if (null == str3)
    {
      str3 = paramProperties.getProperty("version");
      setVersion(str3);
    }
    String str4 = getEncoding();
    if (null == str4)
    {
      str4 = Encodings.getMimeEncoding(paramProperties.getProperty("encoding"));
      setEncoding(str4);
    }
    m_isUTF8 = str4.equals("UTF-8");
    String str5 = (String)paramProperties.get("{http://xml.apache.org/xalan}entities");
    if (null != str5)
    {
      String str6 = (String)paramProperties.get("method");
      m_charInfo = CharInfo.getCharInfo(str5, str6);
    }
  }
  
  private synchronized void init(Writer paramWriter, Properties paramProperties)
  {
    init(paramWriter, paramProperties, false, false);
  }
  
  protected synchronized void init(OutputStream paramOutputStream, Properties paramProperties, boolean paramBoolean)
    throws UnsupportedEncodingException
  {
    String str = getEncoding();
    if (str == null)
    {
      str = Encodings.getMimeEncoding(paramProperties.getProperty("encoding"));
      setEncoding(str);
    }
    if (str.equalsIgnoreCase("UTF-8"))
    {
      m_isUTF8 = true;
      init(new WriterToUTF8Buffered(paramOutputStream), paramProperties, paramBoolean, true);
    }
    else if ((str.equals("WINDOWS-1250")) || (str.equals("US-ASCII")) || (str.equals("ASCII")))
    {
      init(new WriterToASCI(paramOutputStream), paramProperties, paramBoolean, true);
    }
    else
    {
      Writer localWriter;
      try
      {
        localWriter = Encodings.getWriter(paramOutputStream, str);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        System.out.println("Warning: encoding \"" + str + "\" not supported, using " + "UTF-8");
        str = "UTF-8";
        setEncoding(str);
        localWriter = Encodings.getWriter(paramOutputStream, str);
      }
      init(localWriter, paramProperties, paramBoolean, true);
    }
  }
  
  public Properties getOutputFormat()
  {
    return m_format;
  }
  
  public void setWriter(Writer paramWriter)
  {
    if ((m_tracer != null) && (!(paramWriter instanceof SerializerTraceWriter))) {
      m_writer = new SerializerTraceWriter(paramWriter, m_tracer);
    } else {
      m_writer = paramWriter;
    }
  }
  
  public boolean setLineSepUse(boolean paramBoolean)
  {
    boolean bool = m_lineSepUse;
    m_lineSepUse = paramBoolean;
    return bool;
  }
  
  public void setOutputStream(OutputStream paramOutputStream)
  {
    try
    {
      Properties localProperties;
      if (null == m_format) {
        localProperties = OutputPropertiesFactory.getDefaultMethodProperties("xml");
      } else {
        localProperties = m_format;
      }
      init(paramOutputStream, localProperties, true);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
  }
  
  public boolean setEscaping(boolean paramBoolean)
  {
    boolean bool = m_escaping;
    m_escaping = paramBoolean;
    return bool;
  }
  
  protected void indent(int paramInt)
    throws IOException
  {
    if (m_startNewLine) {
      outputLineSep();
    }
    if (m_indentAmount > 0) {
      printSpace(paramInt * m_indentAmount);
    }
  }
  
  protected void indent()
    throws IOException
  {
    indent(m_elemContext.m_currentElemDepth);
  }
  
  private void printSpace(int paramInt)
    throws IOException
  {
    Writer localWriter = m_writer;
    for (int i = 0; i < paramInt; i++) {
      localWriter.write(32);
    }
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {
    if (m_inExternalDTD) {
      return;
    }
    try
    {
      Writer localWriter = m_writer;
      DTDprolog();
      localWriter.write("<!ATTLIST ");
      localWriter.write(paramString1);
      localWriter.write(32);
      localWriter.write(paramString2);
      localWriter.write(32);
      localWriter.write(paramString3);
      if (paramString4 != null)
      {
        localWriter.write(32);
        localWriter.write(paramString4);
      }
      localWriter.write(62);
      localWriter.write(m_lineSep, 0, m_lineSepLen);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public Writer getWriter()
  {
    return m_writer;
  }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      DTDprolog();
      m_writer.write("<!ENTITY ");
      m_writer.write(paramString1);
      if (paramString2 != null)
      {
        m_writer.write(" PUBLIC \"");
        m_writer.write(paramString2);
      }
      else
      {
        m_writer.write(" SYSTEM \"");
        m_writer.write(paramString3);
      }
      m_writer.write("\" >");
      m_writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  
  protected boolean escapingNotNeeded(char paramChar)
  {
    boolean bool;
    if (paramChar < '')
    {
      if ((paramChar >= ' ') || ('\n' == paramChar) || ('\r' == paramChar) || ('\t' == paramChar)) {
        bool = true;
      } else {
        bool = false;
      }
    }
    else {
      bool = m_encodingInfo.isInEncoding(paramChar);
    }
    return bool;
  }
  
  protected int writeUTF16Surrogate(char paramChar, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    if (paramInt1 + 1 >= paramInt2) {
      throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) }));
    }
    char c1 = paramChar;
    char c2 = paramArrayOfChar[(paramInt1 + 1)];
    if (!Encodings.isLowUTF16Surrogate(c2)) {
      throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) + " " + Integer.toHexString(c2) }));
    }
    Writer localWriter = m_writer;
    if (m_encodingInfo.isInEncoding(paramChar, c2))
    {
      localWriter.write(paramArrayOfChar, paramInt1, 2);
    }
    else
    {
      String str = getEncoding();
      if (str != null)
      {
        i = Encodings.toCodePoint(c1, c2);
        localWriter.write(38);
        localWriter.write(35);
        localWriter.write(Integer.toString(i));
        localWriter.write(59);
      }
      else
      {
        localWriter.write(paramArrayOfChar, paramInt1, 2);
      }
    }
    return i;
  }
  
  protected int accumDefaultEntity(Writer paramWriter, char paramChar, int paramInt1, char[] paramArrayOfChar, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    if ((!paramBoolean2) && ('\n' == paramChar))
    {
      paramWriter.write(m_lineSep, 0, m_lineSepLen);
    }
    else if (((paramBoolean1) && (m_charInfo.isSpecialTextChar(paramChar))) || ((!paramBoolean1) && (m_charInfo.isSpecialAttrChar(paramChar))))
    {
      String str = m_charInfo.getOutputStringForChar(paramChar);
      if (null != str) {
        paramWriter.write(str);
      } else {
        return paramInt1;
      }
    }
    else
    {
      return paramInt1;
    }
    return paramInt1 + 1;
  }
  
  void writeNormalizedChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, SAXException
  {
    Writer localWriter = m_writer;
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
    {
      char c = paramArrayOfChar[j];
      if (('\n' == c) && (paramBoolean2))
      {
        localWriter.write(m_lineSep, 0, m_lineSepLen);
      }
      else
      {
        String str;
        if ((paramBoolean1) && (!escapingNotNeeded(c)))
        {
          if (m_cdataTagOpen) {
            closeCDATA();
          }
          if (Encodings.isHighUTF16Surrogate(c))
          {
            writeUTF16Surrogate(c, paramArrayOfChar, j, i);
            j++;
          }
          else
          {
            localWriter.write("&#");
            str = Integer.toString(c);
            localWriter.write(str);
            localWriter.write(59);
          }
        }
        else if ((paramBoolean1) && (j < i - 2) && (']' == c) && (']' == paramArrayOfChar[(j + 1)]) && ('>' == paramArrayOfChar[(j + 2)]))
        {
          localWriter.write("]]]]><![CDATA[>");
          j += 2;
        }
        else if (escapingNotNeeded(c))
        {
          if ((paramBoolean1) && (!m_cdataTagOpen))
          {
            localWriter.write("<![CDATA[");
            m_cdataTagOpen = true;
          }
          localWriter.write(c);
        }
        else if (Encodings.isHighUTF16Surrogate(c))
        {
          if (m_cdataTagOpen) {
            closeCDATA();
          }
          writeUTF16Surrogate(c, paramArrayOfChar, j, i);
          j++;
        }
        else
        {
          if (m_cdataTagOpen) {
            closeCDATA();
          }
          localWriter.write("&#");
          str = Integer.toString(c);
          localWriter.write(str);
          localWriter.write(59);
        }
      }
    }
  }
  
  public void endNonEscaping()
    throws SAXException
  {
    m_disableOutputEscapingStates.pop();
  }
  
  public void startNonEscaping()
    throws SAXException
  {
    m_disableOutputEscapingStates.push(true);
  }
  
  protected void cdata(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      int i = paramInt1;
      if (m_elemContext.m_startTagOpen)
      {
        closeStartTag();
        m_elemContext.m_startTagOpen = false;
      }
      m_ispreserve = true;
      if (shouldIndent()) {
        indent();
      }
      int j = (paramInt2 >= 1) && (escapingNotNeeded(paramArrayOfChar[paramInt1])) ? 1 : 0;
      if ((j != 0) && (!m_cdataTagOpen))
      {
        m_writer.write("<![CDATA[");
        m_cdataTagOpen = true;
      }
      if (isEscapingDisabled()) {
        charactersRaw(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        writeNormalizedChars(paramArrayOfChar, paramInt1, paramInt2, true, m_lineSepUse);
      }
      if ((j != 0) && (paramArrayOfChar[(paramInt1 + paramInt2 - 1)] == ']')) {
        closeCDATA();
      }
      if (m_tracer != null) {
        super.fireCDATAEvent(paramArrayOfChar, i, paramInt2);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(Utils.messages.createMessage("ER_OIERROR", null), localIOException);
    }
  }
  
  private boolean isEscapingDisabled()
  {
    return m_disableOutputEscapingStates.peekOrFalse();
  }
  
  protected void charactersRaw(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_inEntityRef) {
      return;
    }
    try
    {
      if (m_elemContext.m_startTagOpen)
      {
        closeStartTag();
        m_elemContext.m_startTagOpen = false;
      }
      m_ispreserve = true;
      m_writer.write(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if ((paramInt2 == 0) || ((m_inEntityRef) && (!m_expandDTDEntities))) {
      return;
    }
    if (m_elemContext.m_startTagOpen)
    {
      closeStartTag();
      m_elemContext.m_startTagOpen = false;
    }
    else if (m_needToCallStartDocument)
    {
      startDocumentInternal();
    }
    if ((m_cdataStartCalled) || (m_elemContext.m_isCdataSection))
    {
      cdata(paramArrayOfChar, paramInt1, paramInt2);
      return;
    }
    if (m_cdataTagOpen) {
      closeCDATA();
    }
    if ((m_disableOutputEscapingStates.peekOrFalse()) || (!m_escaping))
    {
      charactersRaw(paramArrayOfChar, paramInt1, paramInt2);
      if (m_tracer != null) {
        super.fireCharEvent(paramArrayOfChar, paramInt1, paramInt2);
      }
      return;
    }
    if (m_elemContext.m_startTagOpen)
    {
      closeStartTag();
      m_elemContext.m_startTagOpen = false;
    }
    try
    {
      int k = paramInt1 + paramInt2;
      int m = paramInt1 - 1;
      char c1;
      for (int i = paramInt1; (i < k) && (((c1 = paramArrayOfChar[i]) == ' ') || ((c1 == '\n') && (m_lineSepUse)) || (c1 == '\r') || (c1 == '\t')); i++) {
        if (!m_charInfo.isTextASCIIClean(c1))
        {
          m = processDirty(paramArrayOfChar, k, i, c1, m, true);
          i = m;
        }
      }
      if (i < k) {
        m_ispreserve = true;
      }
      boolean bool = "1.0".equals(getVersion());
      while (i < k)
      {
        while ((i < k) && ((c2 = paramArrayOfChar[i]) < '') && (m_charInfo.isTextASCIIClean(c2))) {
          i++;
        }
        if (i == k) {
          break;
        }
        char c2 = paramArrayOfChar[i];
        if (((isCharacterInC0orC1Range(c2)) || ((!bool) && (isNELorLSEPCharacter(c2))) || (!escapingNotNeeded(c2)) || (m_charInfo.isSpecialTextChar(c2))) && ('"' != c2))
        {
          m = processDirty(paramArrayOfChar, k, i, c2, m, true);
          i = m;
        }
        i++;
      }
      int j = m + 1;
      if (i > j)
      {
        int n = i - j;
        m_writer.write(paramArrayOfChar, j, n);
      }
      m_isprevtext = true;
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    if (m_tracer != null) {
      super.fireCharEvent(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  private static boolean isCharacterInC0orC1Range(char paramChar)
  {
    if ((paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r')) {
      return false;
    }
    return ((paramChar >= '') && (paramChar <= '')) || ((paramChar >= '\001') && (paramChar <= '\037'));
  }
  
  private static boolean isNELorLSEPCharacter(char paramChar)
  {
    return (paramChar == '') || (paramChar == ' ');
  }
  
  private int processDirty(char[] paramArrayOfChar, int paramInt1, int paramInt2, char paramChar, int paramInt3, boolean paramBoolean)
    throws IOException
  {
    int i = paramInt3 + 1;
    if (paramInt2 > i)
    {
      int j = paramInt2 - i;
      m_writer.write(paramArrayOfChar, i, j);
    }
    if (('\n' == paramChar) && (paramBoolean))
    {
      m_writer.write(m_lineSep, 0, m_lineSepLen);
    }
    else
    {
      i = accumDefaultEscape(m_writer, paramChar, paramInt2, paramArrayOfChar, paramInt1, paramBoolean, false);
      paramInt2 = i - 1;
    }
    return paramInt2;
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    if ((m_inEntityRef) && (!m_expandDTDEntities)) {
      return;
    }
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    characters(m_charsBuff, 0, i);
  }
  
  protected int accumDefaultEscape(Writer paramWriter, char paramChar, int paramInt1, char[] paramArrayOfChar, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    int i = accumDefaultEntity(paramWriter, paramChar, paramInt1, paramArrayOfChar, paramInt2, paramBoolean1, paramBoolean2);
    if (paramInt1 == i) {
      if (Encodings.isHighUTF16Surrogate(paramChar))
      {
        int j = 0;
        if (paramInt1 + 1 >= paramInt2) {
          throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) }));
        }
        char c = paramArrayOfChar[(++paramInt1)];
        if (!Encodings.isLowUTF16Surrogate(c)) {
          throw new IOException(Utils.messages.createMessage("ER_INVALID_UTF16_SURROGATE", new Object[] { Integer.toHexString(paramChar) + " " + Integer.toHexString(c) }));
        }
        j = Encodings.toCodePoint(paramChar, c);
        paramWriter.write("&#");
        paramWriter.write(Integer.toString(j));
        paramWriter.write(59);
        i += 2;
      }
      else
      {
        if ((isCharacterInC0orC1Range(paramChar)) || (("1.1".equals(getVersion())) && (isNELorLSEPCharacter(paramChar))))
        {
          paramWriter.write("&#");
          paramWriter.write(Integer.toString(paramChar));
          paramWriter.write(59);
        }
        else if (((!escapingNotNeeded(paramChar)) || ((paramBoolean1) && (m_charInfo.isSpecialTextChar(paramChar))) || ((!paramBoolean1) && (m_charInfo.isSpecialAttrChar(paramChar)))) && (m_elemContext.m_currentElemDepth > 0))
        {
          paramWriter.write("&#");
          paramWriter.write(Integer.toString(paramChar));
          paramWriter.write(59);
        }
        else
        {
          paramWriter.write(paramChar);
        }
        i++;
      }
    }
    return i;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (m_inEntityRef) {
      return;
    }
    if (m_needToCallStartDocument)
    {
      startDocumentInternal();
      m_needToCallStartDocument = false;
    }
    else if (m_cdataTagOpen)
    {
      closeCDATA();
    }
    try
    {
      if ((true == m_needToOutputDocTypeDecl) && (null != getDoctypeSystem())) {
        outputDocTypeDecl(paramString3, true);
      }
      m_needToOutputDocTypeDecl = false;
      if (m_elemContext.m_startTagOpen)
      {
        closeStartTag();
        m_elemContext.m_startTagOpen = false;
      }
      if (paramString1 != null) {
        ensurePrefixIsDeclared(paramString1, paramString3);
      }
      m_ispreserve = false;
      if ((shouldIndent()) && (m_startNewLine)) {
        indent();
      }
      m_startNewLine = true;
      Writer localWriter = m_writer;
      localWriter.write(60);
      localWriter.write(paramString3);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    if (paramAttributes != null) {
      addAttributes(paramAttributes);
    }
    m_elemContext = m_elemContext.push(paramString1, paramString2, paramString3);
    m_isprevtext = false;
    if (m_tracer != null) {
      firePseudoAttributes();
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    startElement(paramString1, paramString2, paramString3, null);
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    startElement(null, null, paramString, null);
  }
  
  void outputDocTypeDecl(String paramString, boolean paramBoolean)
    throws SAXException
  {
    if (m_cdataTagOpen) {
      closeCDATA();
    }
    try
    {
      Writer localWriter = m_writer;
      localWriter.write("<!DOCTYPE ");
      localWriter.write(paramString);
      String str1 = getDoctypePublic();
      if (null != str1)
      {
        localWriter.write(" PUBLIC \"");
        localWriter.write(str1);
        localWriter.write(34);
      }
      String str2 = getDoctypeSystem();
      if (null != str2)
      {
        if (null == str1) {
          localWriter.write(" SYSTEM \"");
        } else {
          localWriter.write(" \"");
        }
        localWriter.write(str2);
        if (paramBoolean)
        {
          localWriter.write("\">");
          localWriter.write(m_lineSep, 0, m_lineSepLen);
          paramBoolean = false;
        }
        else
        {
          localWriter.write(34);
        }
      }
      int i = 0;
      if ((i != 0) && (paramBoolean))
      {
        localWriter.write(62);
        localWriter.write(m_lineSep, 0, m_lineSepLen);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void processAttributes(Writer paramWriter, int paramInt)
    throws IOException, SAXException
  {
    String str1 = getEncoding();
    for (int i = 0; i < paramInt; i++)
    {
      String str2 = m_attributes.getQName(i);
      String str3 = m_attributes.getValue(i);
      paramWriter.write(32);
      paramWriter.write(str2);
      paramWriter.write("=\"");
      writeAttrString(paramWriter, str3, str1);
      paramWriter.write(34);
    }
  }
  
  public void writeAttrString(Writer paramWriter, String paramString1, String paramString2)
    throws IOException
  {
    int i = paramString1.length();
    if (i > m_attrBuff.length) {
      m_attrBuff = new char[i * 2 + 1];
    }
    paramString1.getChars(0, i, m_attrBuff, 0);
    char[] arrayOfChar = m_attrBuff;
    int j = 0;
    while (j < i)
    {
      char c = arrayOfChar[j];
      if ((escapingNotNeeded(c)) && (!m_charInfo.isSpecialAttrChar(c)))
      {
        paramWriter.write(c);
        j++;
      }
      else
      {
        j = accumDefaultEscape(paramWriter, c, j, arrayOfChar, i, false, true);
      }
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (m_inEntityRef) {
      return;
    }
    m_prefixMap.popNamespaces(m_elemContext.m_currentElemDepth, null);
    try
    {
      Writer localWriter = m_writer;
      if (m_elemContext.m_startTagOpen)
      {
        if (m_tracer != null) {
          super.fireStartElem(m_elemContext.m_elementName);
        }
        int i = m_attributes.getLength();
        if (i > 0)
        {
          processAttributes(m_writer, i);
          m_attributes.clear();
        }
        if (m_spaceBeforeClose) {
          localWriter.write(" />");
        } else {
          localWriter.write("/>");
        }
      }
      else
      {
        if (m_cdataTagOpen) {
          closeCDATA();
        }
        if (shouldIndent()) {
          indent(m_elemContext.m_currentElemDepth - 1);
        }
        localWriter.write(60);
        localWriter.write(47);
        localWriter.write(paramString3);
        localWriter.write(62);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    if ((!m_elemContext.m_startTagOpen) && (m_doIndent)) {
      m_ispreserve = (m_preserves.isEmpty() ? false : m_preserves.pop());
    }
    m_isprevtext = false;
    if (m_tracer != null) {
      super.fireEndElem(paramString3);
    }
    m_elemContext = m_elemContext.m_prev;
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    endElement(null, null, paramString);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    startPrefixMapping(paramString1, paramString2, true);
  }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean)
    throws SAXException
  {
    int i;
    if (paramBoolean)
    {
      flushPending();
      i = m_elemContext.m_currentElemDepth + 1;
    }
    else
    {
      i = m_elemContext.m_currentElemDepth;
    }
    boolean bool = m_prefixMap.pushNamespace(paramString1, paramString2, i);
    if (bool)
    {
      String str;
      if ("".equals(paramString1))
      {
        str = "xmlns";
        addAttributeAlways("http://www.w3.org/2000/xmlns/", str, str, "CDATA", paramString2, false);
      }
      else if (!"".equals(paramString2))
      {
        str = "xmlns:" + paramString1;
        addAttributeAlways("http://www.w3.org/2000/xmlns/", paramString1, str, "CDATA", paramString2, false);
      }
    }
    return bool;
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    int i = paramInt1;
    if (m_inEntityRef) {
      return;
    }
    if (m_elemContext.m_startTagOpen)
    {
      closeStartTag();
      m_elemContext.m_startTagOpen = false;
    }
    else if (m_needToCallStartDocument)
    {
      startDocumentInternal();
      m_needToCallStartDocument = false;
    }
    try
    {
      if ((shouldIndent()) && (m_isStandalone)) {
        indent();
      }
      int j = paramInt1 + paramInt2;
      int k = 0;
      if (m_cdataTagOpen) {
        closeCDATA();
      }
      if ((shouldIndent()) && (!m_isStandalone)) {
        indent();
      }
      Writer localWriter = m_writer;
      localWriter.write("<!--");
      for (int m = paramInt1; m < j; m++)
      {
        if ((k != 0) && (paramArrayOfChar[m] == '-'))
        {
          localWriter.write(paramArrayOfChar, paramInt1, m - paramInt1);
          localWriter.write(" -");
          paramInt1 = m + 1;
        }
        k = paramArrayOfChar[m] == '-' ? 1 : 0;
      }
      if (paramInt2 > 0)
      {
        m = j - paramInt1;
        if (m > 0) {
          localWriter.write(paramArrayOfChar, paramInt1, m);
        }
        if (paramArrayOfChar[(j - 1)] == '-') {
          localWriter.write(32);
        }
      }
      localWriter.write("-->");
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    m_startNewLine = true;
    if (m_tracer != null) {
      super.fireCommentEvent(paramArrayOfChar, i, paramInt2);
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    if (m_cdataTagOpen) {
      closeCDATA();
    }
    m_cdataStartCalled = false;
  }
  
  public void endDTD()
    throws SAXException
  {
    try
    {
      if (m_needToCallStartDocument) {
        return;
      }
      if (m_needToOutputDocTypeDecl)
      {
        outputDocTypeDecl(m_elemContext.m_elementName, false);
        m_needToOutputDocTypeDecl = false;
      }
      Writer localWriter = m_writer;
      if (!m_inDoctype) {
        localWriter.write("]>");
      } else {
        localWriter.write(62);
      }
      localWriter.write(m_lineSep, 0, m_lineSepLen);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (0 == paramInt2) {
      return;
    }
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {
    m_cdataStartCalled = true;
  }
  
  public void startEntity(String paramString)
    throws SAXException
  {
    if (paramString.equals("[dtd]")) {
      m_inExternalDTD = true;
    }
    if ((!m_expandDTDEntities) && (!m_inExternalDTD))
    {
      startNonEscaping();
      characters("&" + paramString + ';');
      endNonEscaping();
    }
    m_inEntityRef = true;
  }
  
  protected void closeStartTag()
    throws SAXException
  {
    if (m_elemContext.m_startTagOpen)
    {
      try
      {
        if (m_tracer != null) {
          super.fireStartElem(m_elemContext.m_elementName);
        }
        int i = m_attributes.getLength();
        if (i > 0)
        {
          processAttributes(m_writer, i);
          m_attributes.clear();
        }
        m_writer.write(62);
      }
      catch (IOException localIOException)
      {
        throw new SAXException(localIOException);
      }
      if (m_cdataSectionElements != null) {
        m_elemContext.m_isCdataSection = isCdataSection();
      }
      if (m_doIndent)
      {
        m_isprevtext = false;
        m_preserves.push(m_ispreserve);
      }
    }
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    setDoctypeSystem(paramString3);
    setDoctypePublic(paramString2);
    m_elemContext.m_elementName = paramString1;
    m_inDoctype = true;
  }
  
  public int getIndentAmount()
  {
    return m_indentAmount;
  }
  
  public void setIndentAmount(int paramInt)
  {
    m_indentAmount = paramInt;
  }
  
  protected boolean shouldIndent()
  {
    return (m_doIndent) && (!m_ispreserve) && (!m_isprevtext) && ((m_elemContext.m_currentElemDepth > 0) || (m_isStandalone));
  }
  
  private void setCdataSectionElements(String paramString, Properties paramProperties)
  {
    String str = paramProperties.getProperty(paramString);
    if (null != str)
    {
      Vector localVector = new Vector();
      int i = str.length();
      int j = 0;
      StringBuffer localStringBuffer = new StringBuffer();
      for (int k = 0; k < i; k++)
      {
        char c = str.charAt(k);
        if (Character.isWhitespace(c))
        {
          if (j == 0)
          {
            if (localStringBuffer.length() <= 0) {
              continue;
            }
            addCdataSectionElement(localStringBuffer.toString(), localVector);
            localStringBuffer.setLength(0);
            continue;
          }
        }
        else if ('{' == c) {
          j = 1;
        } else if ('}' == c) {
          j = 0;
        }
        localStringBuffer.append(c);
      }
      if (localStringBuffer.length() > 0)
      {
        addCdataSectionElement(localStringBuffer.toString(), localVector);
        localStringBuffer.setLength(0);
      }
      setCdataSectionElements(localVector);
    }
  }
  
  private void addCdataSectionElement(String paramString, Vector paramVector)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "{}", false);
    String str = localStringTokenizer.nextToken();
    Object localObject = localStringTokenizer.hasMoreTokens() ? localStringTokenizer.nextToken() : null;
    if (null == localObject)
    {
      paramVector.addElement(null);
      paramVector.addElement(str);
    }
    else
    {
      paramVector.addElement(str);
      paramVector.addElement(localObject);
    }
  }
  
  public void setCdataSectionElements(Vector paramVector)
  {
    m_cdataSectionElements = paramVector;
  }
  
  protected String ensureAttributesNamespaceIsDeclared(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if ((paramString1 != null) && (paramString1.length() > 0))
    {
      int i = 0;
      String str1 = (i = paramString3.indexOf(":")) < 0 ? "" : paramString3.substring(0, i);
      if (i > 0)
      {
        str2 = m_prefixMap.lookupNamespace(str1);
        if ((str2 != null) && (str2.equals(paramString1))) {
          return null;
        }
        startPrefixMapping(str1, paramString1, false);
        addAttribute("http://www.w3.org/2000/xmlns/", str1, "xmlns:" + str1, "CDATA", paramString1, false);
        return str1;
      }
      String str2 = m_prefixMap.lookupPrefix(paramString1);
      if (str2 == null)
      {
        str2 = m_prefixMap.generateNextPrefix();
        startPrefixMapping(str2, paramString1, false);
        addAttribute("http://www.w3.org/2000/xmlns/", str2, "xmlns:" + str2, "CDATA", paramString1, false);
      }
      return str2;
    }
    return null;
  }
  
  void ensurePrefixIsDeclared(String paramString1, String paramString2)
    throws SAXException
  {
    if ((paramString1 != null) && (paramString1.length() > 0))
    {
      int i;
      int j = (i = paramString2.indexOf(":")) < 0 ? 1 : 0;
      String str1 = j != 0 ? "" : paramString2.substring(0, i);
      if (null != str1)
      {
        String str2 = m_prefixMap.lookupNamespace(str1);
        if ((null == str2) || (!str2.equals(paramString1)))
        {
          startPrefixMapping(str1, paramString1);
          addAttributeAlways("http://www.w3.org/2000/xmlns/", j != 0 ? "xmlns" : str1, "xmlns:" + str1, "CDATA", paramString1, false);
        }
      }
    }
  }
  
  public void flushPending()
    throws SAXException
  {
    if (m_needToCallStartDocument)
    {
      startDocumentInternal();
      m_needToCallStartDocument = false;
    }
    if (m_elemContext.m_startTagOpen)
    {
      closeStartTag();
      m_elemContext.m_startTagOpen = false;
    }
    if (m_cdataTagOpen)
    {
      closeCDATA();
      m_cdataTagOpen = false;
    }
  }
  
  public void setContentHandler(ContentHandler paramContentHandler) {}
  
  public boolean addAttributeAlways(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
  {
    int i = m_attributes.getIndex(paramString3);
    boolean bool;
    if (i >= 0)
    {
      String str1 = null;
      if (m_tracer != null)
      {
        str1 = m_attributes.getValue(i);
        if (paramString5.equals(str1)) {
          str1 = null;
        }
      }
      m_attributes.setValue(i, paramString5);
      bool = false;
      if (str1 != null) {
        firePseudoAttributes();
      }
    }
    else
    {
      if (paramBoolean)
      {
        int j = paramString3.indexOf(':');
        String str2;
        if (j > 0)
        {
          str2 = paramString3.substring(0, j);
          NamespaceMappings.MappingRecord localMappingRecord = m_prefixMap.getMappingFromPrefix(str2);
          if ((localMappingRecord != null) && (m_declarationDepth == m_elemContext.m_currentElemDepth) && (!m_uri.equals(paramString1)))
          {
            str2 = m_prefixMap.lookupPrefix(paramString1);
            if (str2 == null) {
              str2 = m_prefixMap.generateNextPrefix();
            }
            paramString3 = str2 + ':' + paramString2;
          }
        }
        try
        {
          str2 = ensureAttributesNamespaceIsDeclared(paramString1, paramString2, paramString3);
        }
        catch (SAXException localSAXException)
        {
          localSAXException.printStackTrace();
        }
      }
      m_attributes.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
      bool = true;
      if (m_tracer != null) {
        firePseudoAttributes();
      }
    }
    return bool;
  }
  
  protected void firePseudoAttributes()
  {
    if (m_tracer != null) {
      try
      {
        m_writer.flush();
        StringBuffer localStringBuffer = new StringBuffer();
        int i = m_attributes.getLength();
        if (i > 0)
        {
          localObject = new WritertoStringBuffer(localStringBuffer);
          processAttributes((Writer)localObject, i);
        }
        localStringBuffer.append('>');
        Object localObject = localStringBuffer.toString().toCharArray();
        m_tracer.fireGenerateEvent(11, (char[])localObject, 0, localObject.length);
      }
      catch (IOException localIOException) {}catch (SAXException localSAXException) {}
    }
  }
  
  public void setTransformer(Transformer paramTransformer)
  {
    super.setTransformer(paramTransformer);
    if ((m_tracer != null) && (!(m_writer instanceof SerializerTraceWriter))) {
      m_writer = new SerializerTraceWriter(m_writer, m_tracer);
    }
  }
  
  public boolean reset()
  {
    boolean bool = false;
    if (super.reset())
    {
      resetToStream();
      bool = true;
    }
    return bool;
  }
  
  private void resetToStream()
  {
    m_cdataStartCalled = false;
    m_disableOutputEscapingStates.clear();
    m_escaping = true;
    m_inDoctype = false;
    m_ispreserve = false;
    m_ispreserve = false;
    m_isprevtext = false;
    m_isUTF8 = false;
    m_preserves.clear();
    m_shouldFlush = true;
    m_spaceBeforeClose = false;
    m_startNewLine = false;
    m_lineSepUse = true;
    m_expandDTDEntities = true;
  }
  
  public void setEncoding(String paramString)
  {
    String str1 = getEncoding();
    super.setEncoding(paramString);
    if ((str1 == null) || (!str1.equals(paramString)))
    {
      m_encodingInfo = Encodings.getEncodingInfo(paramString);
      if ((paramString != null) && (m_encodingInfo.name == null))
      {
        String str2 = Utils.messages.createMessage("ER_ENCODING_NOT_SUPPORTED", new Object[] { paramString });
        try
        {
          Transformer localTransformer = super.getTransformer();
          if (localTransformer != null)
          {
            ErrorListener localErrorListener = localTransformer.getErrorListener();
            if ((null != localErrorListener) && (m_sourceLocator != null)) {
              localErrorListener.warning(new TransformerException(str2, m_sourceLocator));
            } else {
              System.out.println(str2);
            }
          }
          else
          {
            System.out.println(str2);
          }
        }
        catch (Exception localException) {}
      }
    }
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      DTDprolog();
      m_writer.write("<!NOTATION ");
      m_writer.write(paramString1);
      if (paramString2 != null)
      {
        m_writer.write(" PUBLIC \"");
        m_writer.write(paramString2);
      }
      else
      {
        m_writer.write(" SYSTEM \"");
        m_writer.write(paramString3);
      }
      m_writer.write("\" >");
      m_writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    try
    {
      DTDprolog();
      m_writer.write("<!ENTITY ");
      m_writer.write(paramString1);
      if (paramString2 != null)
      {
        m_writer.write(" PUBLIC \"");
        m_writer.write(paramString2);
      }
      else
      {
        m_writer.write(" SYSTEM \"");
        m_writer.write(paramString3);
      }
      m_writer.write("\" NDATA ");
      m_writer.write(paramString4);
      m_writer.write(" >");
      m_writer.write(m_lineSep, 0, m_lineSepLen);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  
  private void DTDprolog()
    throws SAXException, IOException
  {
    Writer localWriter = m_writer;
    if (m_needToOutputDocTypeDecl)
    {
      outputDocTypeDecl(m_elemContext.m_elementName, false);
      m_needToOutputDocTypeDecl = false;
    }
    if (m_inDoctype)
    {
      localWriter.write(" [");
      localWriter.write(m_lineSep, 0, m_lineSepLen);
      m_inDoctype = false;
    }
  }
  
  public void setDTDEntityExpansion(boolean paramBoolean)
  {
    m_expandDTDEntities = paramBoolean;
  }
  
  static final class BoolStack
  {
    private boolean[] m_values;
    private int m_allocatedSize;
    private int m_index;
    
    public BoolStack()
    {
      this(32);
    }
    
    public BoolStack(int paramInt)
    {
      m_allocatedSize = paramInt;
      m_values = new boolean[paramInt];
      m_index = -1;
    }
    
    public final int size()
    {
      return m_index + 1;
    }
    
    public final void clear()
    {
      m_index = -1;
    }
    
    public final boolean push(boolean paramBoolean)
    {
      if (m_index == m_allocatedSize - 1) {
        grow();
      }
      return m_values[(++m_index)] = paramBoolean;
    }
    
    public final boolean pop()
    {
      return m_values[(m_index--)];
    }
    
    public final boolean popAndTop()
    {
      m_index -= 1;
      return m_index >= 0 ? m_values[m_index] : false;
    }
    
    public final void setTop(boolean paramBoolean)
    {
      m_values[m_index] = paramBoolean;
    }
    
    public final boolean peek()
    {
      return m_values[m_index];
    }
    
    public final boolean peekOrFalse()
    {
      return m_index > -1 ? m_values[m_index] : false;
    }
    
    public final boolean peekOrTrue()
    {
      return m_index > -1 ? m_values[m_index] : true;
    }
    
    public boolean isEmpty()
    {
      return m_index == -1;
    }
    
    private void grow()
    {
      m_allocatedSize *= 2;
      boolean[] arrayOfBoolean = new boolean[m_allocatedSize];
      System.arraycopy(m_values, 0, arrayOfBoolean, 0, m_index + 1);
      m_values = arrayOfBoolean;
    }
  }
  
  private class WritertoStringBuffer
    extends Writer
  {
    private final StringBuffer m_stringbuf;
    
    WritertoStringBuffer(StringBuffer paramStringBuffer)
    {
      m_stringbuf = paramStringBuffer;
    }
    
    public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      m_stringbuf.append(paramArrayOfChar, paramInt1, paramInt2);
    }
    
    public void flush()
      throws IOException
    {}
    
    public void close()
      throws IOException
    {}
    
    public void write(int paramInt)
    {
      m_stringbuf.append((char)paramInt);
    }
    
    public void write(String paramString)
    {
      m_stringbuf.append(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */