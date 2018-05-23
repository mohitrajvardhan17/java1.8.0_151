package com.sun.xml.internal.stream.writers;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;

public final class XMLStreamWriterImpl
  extends AbstractMap
  implements XMLStreamWriter
{
  public static final String START_COMMENT = "<!--";
  public static final String END_COMMENT = "-->";
  public static final String DEFAULT_ENCODING = " encoding=\"utf-8\"";
  public static final String DEFAULT_XMLDECL = "<?xml version=\"1.0\" ?>";
  public static final String DEFAULT_XML_VERSION = "1.0";
  public static final char CLOSE_START_TAG = '>';
  public static final char OPEN_START_TAG = '<';
  public static final String OPEN_END_TAG = "</";
  public static final char CLOSE_END_TAG = '>';
  public static final String START_CDATA = "<![CDATA[";
  public static final String END_CDATA = "]]>";
  public static final String CLOSE_EMPTY_ELEMENT = "/>";
  public static final String SPACE = " ";
  public static final String UTF_8 = "UTF-8";
  public static final String OUTPUTSTREAM_PROPERTY = "sjsxp-outputstream";
  boolean fEscapeCharacters = true;
  private boolean fIsRepairingNamespace = false;
  private Writer fWriter;
  private OutputStream fOutputStream = null;
  private ArrayList fAttributeCache;
  private ArrayList fNamespaceDecls;
  private NamespaceContextImpl fNamespaceContext = null;
  private NamespaceSupport fInternalNamespaceContext = null;
  private Random fPrefixGen = null;
  private PropertyManager fPropertyManager = null;
  private boolean fStartTagOpened = false;
  private boolean fReuse;
  private SymbolTable fSymbolTable = new SymbolTable();
  private ElementStack fElementStack = new ElementStack();
  private final String DEFAULT_PREFIX = fSymbolTable.addSymbol("");
  private final ReadOnlyIterator fReadOnlyIterator = new ReadOnlyIterator();
  private CharsetEncoder fEncoder = null;
  HashMap fAttrNamespace = null;
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream, PropertyManager paramPropertyManager)
    throws IOException
  {
    this(new OutputStreamWriter(paramOutputStream), paramPropertyManager);
  }
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream, String paramString, PropertyManager paramPropertyManager)
    throws IOException
  {
    this(new StreamResult(paramOutputStream), paramString, paramPropertyManager);
  }
  
  public XMLStreamWriterImpl(Writer paramWriter, PropertyManager paramPropertyManager)
    throws IOException
  {
    this(new StreamResult(paramWriter), null, paramPropertyManager);
  }
  
  public XMLStreamWriterImpl(StreamResult paramStreamResult, String paramString, PropertyManager paramPropertyManager)
    throws IOException
  {
    setOutput(paramStreamResult, paramString);
    fPropertyManager = paramPropertyManager;
    init();
  }
  
  private void init()
  {
    fReuse = false;
    fNamespaceDecls = new ArrayList();
    fPrefixGen = new Random();
    fAttributeCache = new ArrayList();
    fInternalNamespaceContext = new NamespaceSupport();
    fInternalNamespaceContext.reset();
    fNamespaceContext = new NamespaceContextImpl();
    fNamespaceContext.internalContext = fInternalNamespaceContext;
    Boolean localBoolean = (Boolean)fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
    fIsRepairingNamespace = localBoolean.booleanValue();
    localBoolean = (Boolean)fPropertyManager.getProperty("escapeCharacters");
    setEscapeCharacters(localBoolean.booleanValue());
  }
  
  public void reset()
  {
    reset(false);
  }
  
  void reset(boolean paramBoolean)
  {
    if (!fReuse) {
      throw new IllegalStateException("close() Must be called before calling reset()");
    }
    fReuse = false;
    fNamespaceDecls.clear();
    fAttributeCache.clear();
    fElementStack.clear();
    fInternalNamespaceContext.reset();
    fStartTagOpened = false;
    fNamespaceContext.userContext = null;
    if (paramBoolean)
    {
      Boolean localBoolean = (Boolean)fPropertyManager.getProperty("javax.xml.stream.isRepairingNamespaces");
      fIsRepairingNamespace = localBoolean.booleanValue();
      localBoolean = (Boolean)fPropertyManager.getProperty("escapeCharacters");
      setEscapeCharacters(localBoolean.booleanValue());
    }
  }
  
  public void setOutput(StreamResult paramStreamResult, String paramString)
    throws IOException
  {
    if (paramStreamResult.getOutputStream() != null) {
      setOutputUsingStream(paramStreamResult.getOutputStream(), paramString);
    } else if (paramStreamResult.getWriter() != null) {
      setOutputUsingWriter(paramStreamResult.getWriter());
    } else if (paramStreamResult.getSystemId() != null) {
      setOutputUsingStream(new FileOutputStream(paramStreamResult.getSystemId()), paramString);
    }
  }
  
  private void setOutputUsingWriter(Writer paramWriter)
    throws IOException
  {
    fWriter = paramWriter;
    if ((paramWriter instanceof OutputStreamWriter))
    {
      String str = ((OutputStreamWriter)paramWriter).getEncoding();
      if ((str != null) && (!str.equalsIgnoreCase("utf-8"))) {
        fEncoder = Charset.forName(str).newEncoder();
      }
    }
  }
  
  private void setOutputUsingStream(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    fOutputStream = paramOutputStream;
    if (paramString != null)
    {
      if (paramString.equalsIgnoreCase("utf-8"))
      {
        fWriter = new UTF8OutputStreamWriter(paramOutputStream);
      }
      else
      {
        fWriter = new XMLWriter(new OutputStreamWriter(paramOutputStream, paramString));
        fEncoder = Charset.forName(paramString).newEncoder();
      }
    }
    else
    {
      paramString = SecuritySupport.getSystemProperty("file.encoding");
      if ((paramString != null) && (paramString.equalsIgnoreCase("utf-8"))) {
        fWriter = new UTF8OutputStreamWriter(paramOutputStream);
      } else {
        fWriter = new XMLWriter(new OutputStreamWriter(paramOutputStream));
      }
    }
  }
  
  public boolean canReuse()
  {
    return fReuse;
  }
  
  public void setEscapeCharacters(boolean paramBoolean)
  {
    fEscapeCharacters = paramBoolean;
  }
  
  public boolean getEscapeCharacters()
  {
    return fEscapeCharacters;
  }
  
  public void close()
    throws XMLStreamException
  {
    if (fWriter != null) {
      try
      {
        fWriter.flush();
      }
      catch (IOException localIOException)
      {
        throw new XMLStreamException(localIOException);
      }
    }
    fWriter = null;
    fOutputStream = null;
    fNamespaceDecls.clear();
    fAttributeCache.clear();
    fElementStack.clear();
    fInternalNamespaceContext.reset();
    fReuse = true;
    fStartTagOpened = false;
    fNamespaceContext.userContext = null;
  }
  
  public void flush()
    throws XMLStreamException
  {
    try
    {
      fWriter.flush();
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return fNamespaceContext;
  }
  
  public String getPrefix(String paramString)
    throws XMLStreamException
  {
    return fNamespaceContext.getPrefix(paramString);
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if (!fPropertyManager.containsProperty(paramString)) {
      throw new IllegalArgumentException("Property '" + paramString + "' is not supported");
    }
    return fPropertyManager.getProperty(paramString);
  }
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    if (paramString != null) {
      paramString = fSymbolTable.addSymbol(paramString);
    }
    if (fIsRepairingNamespace)
    {
      if (isDefaultNamespace(paramString)) {
        return;
      }
      QName localQName = new QName();
      localQName.setValues(DEFAULT_PREFIX, "xmlns", null, paramString);
      fNamespaceDecls.add(localQName);
    }
    else
    {
      fInternalNamespaceContext.declarePrefix(DEFAULT_PREFIX, paramString);
    }
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    fNamespaceContext.userContext = paramNamespaceContext;
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramString1 == null) {
      throw new XMLStreamException("Prefix cannot be null");
    }
    if (paramString2 == null) {
      throw new XMLStreamException("URI cannot be null");
    }
    paramString1 = fSymbolTable.addSymbol(paramString1);
    paramString2 = fSymbolTable.addSymbol(paramString2);
    if (fIsRepairingNamespace)
    {
      String str = fInternalNamespaceContext.getURI(paramString1);
      if ((str != null) && (str == paramString2)) {
        return;
      }
      if (checkUserNamespaceContext(paramString1, paramString2)) {
        return;
      }
      QName localQName = new QName();
      localQName.setValues(paramString1, "xmlns", null, paramString2);
      fNamespaceDecls.add(localQName);
      return;
    }
    fInternalNamespaceContext.declarePrefix(paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException
  {
    try
    {
      if (!fStartTagOpened) {
        throw new XMLStreamException("Attribute not associated with any element");
      }
      if (fIsRepairingNamespace)
      {
        Attribute localAttribute = new Attribute(paramString2);
        localAttribute.setValues(null, paramString1, null, null);
        fAttributeCache.add(localAttribute);
        return;
      }
      fWriter.write(" ");
      fWriter.write(paramString1);
      fWriter.write("=\"");
      writeXMLContent(paramString2, true, true);
      fWriter.write("\"");
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    try
    {
      if (!fStartTagOpened) {
        throw new XMLStreamException("Attribute not associated with any element");
      }
      if (paramString1 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      paramString1 = fSymbolTable.addSymbol(paramString1);
      String str = fInternalNamespaceContext.getPrefix(paramString1);
      if (!fIsRepairingNamespace)
      {
        if (str == null) {
          throw new XMLStreamException("Prefix cannot be null");
        }
        writeAttributeWithPrefix(str, paramString2, paramString3);
      }
      else
      {
        Attribute localAttribute = new Attribute(paramString3);
        localAttribute.setValues(null, paramString2, null, paramString1);
        fAttributeCache.add(localAttribute);
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  private void writeAttributeWithPrefix(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    fWriter.write(" ");
    if ((paramString1 != null) && (paramString1 != ""))
    {
      fWriter.write(paramString1);
      fWriter.write(":");
    }
    fWriter.write(paramString2);
    fWriter.write("=\"");
    writeXMLContent(paramString3, true, true);
    fWriter.write("\"");
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLStreamException
  {
    try
    {
      if (!fStartTagOpened) {
        throw new XMLStreamException("Attribute not associated with any element");
      }
      if (paramString2 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString3 == null) {
        throw new XMLStreamException("Local name cannot be null");
      }
      Object localObject;
      if (!fIsRepairingNamespace)
      {
        if ((paramString1 == null) || (paramString1.equals("")))
        {
          if (!paramString2.equals("")) {
            throw new XMLStreamException("prefix cannot be null or empty");
          }
          writeAttributeWithPrefix(null, paramString3, paramString4);
          return;
        }
        if ((!paramString1.equals("xml")) || (!paramString2.equals("http://www.w3.org/XML/1998/namespace")))
        {
          paramString1 = fSymbolTable.addSymbol(paramString1);
          paramString2 = fSymbolTable.addSymbol(paramString2);
          if (fInternalNamespaceContext.containsPrefixInCurrentContext(paramString1))
          {
            localObject = fInternalNamespaceContext.getURI(paramString1);
            if ((localObject != null) && (localObject != paramString2)) {
              throw new XMLStreamException("Prefix " + paramString1 + " is already bound to " + (String)localObject + ". Trying to rebind it to " + paramString2 + " is an error.");
            }
          }
          fInternalNamespaceContext.declarePrefix(paramString1, paramString2);
        }
        writeAttributeWithPrefix(paramString1, paramString3, paramString4);
      }
      else
      {
        if (paramString1 != null) {
          paramString1 = fSymbolTable.addSymbol(paramString1);
        }
        paramString2 = fSymbolTable.addSymbol(paramString2);
        localObject = new Attribute(paramString4);
        ((Attribute)localObject).setValues(paramString1, paramString3, null, paramString2);
        fAttributeCache.add(localObject);
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (paramString == null) {
        throw new XMLStreamException("cdata cannot be null");
      }
      if (fStartTagOpened) {
        closeStartTag();
      }
      fWriter.write("<![CDATA[");
      fWriter.write(paramString);
      fWriter.write("]]>");
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      writeXMLContent(paramString);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      writeXMLContent(paramArrayOfChar, paramInt1, paramInt2, fEscapeCharacters);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeComment(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      fWriter.write("<!--");
      if (paramString != null) {
        fWriter.write(paramString);
      }
      fWriter.write("-->");
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeDTD(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      fWriter.write(paramString);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    String str = null;
    if (paramString == null) {
      str = "";
    } else {
      str = paramString;
    }
    try
    {
      if (!fStartTagOpened) {
        throw new IllegalStateException("Namespace Attribute not associated with any element");
      }
      Object localObject;
      if (fIsRepairingNamespace)
      {
        localObject = new QName();
        ((QName)localObject).setValues("", "xmlns", null, str);
        fNamespaceDecls.add(localObject);
        return;
      }
      str = fSymbolTable.addSymbol(str);
      if (fInternalNamespaceContext.containsPrefixInCurrentContext(""))
      {
        localObject = fInternalNamespaceContext.getURI("");
        if ((localObject != null) && (localObject != str)) {
          throw new XMLStreamException("xmlns has been already bound to " + (String)localObject + ". Rebinding it to " + str + " is an error");
        }
      }
      fInternalNamespaceContext.declarePrefix("", str);
      writenamespace(null, str);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      openStartTag();
      fElementStack.push(null, paramString, null, null, true);
      fInternalNamespaceContext.pushContext();
      if (!fIsRepairingNamespace) {
        fWriter.write(paramString);
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeEmptyElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramString1 == null) {
      throw new XMLStreamException("NamespaceURI cannot be null");
    }
    paramString1 = fSymbolTable.addSymbol(paramString1);
    String str = fNamespaceContext.getPrefix(paramString1);
    writeEmptyElement(str, paramString2, paramString1);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    try
    {
      if (paramString2 == null) {
        throw new XMLStreamException("Local Name cannot be null");
      }
      if (paramString3 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString1 != null) {
        paramString1 = fSymbolTable.addSymbol(paramString1);
      }
      paramString3 = fSymbolTable.addSymbol(paramString3);
      if (fStartTagOpened) {
        closeStartTag();
      }
      openStartTag();
      fElementStack.push(paramString1, paramString2, null, paramString3, true);
      fInternalNamespaceContext.pushContext();
      if (!fIsRepairingNamespace)
      {
        if (paramString1 == null) {
          throw new XMLStreamException("NamespaceURI " + paramString3 + " has not been bound to any prefix");
        }
      }
      else {
        return;
      }
      if ((paramString1 != null) && (paramString1 != ""))
      {
        fWriter.write(paramString1);
        fWriter.write(":");
      }
      fWriter.write(paramString2);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeEndDocument()
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      ElementState localElementState = null;
      while (!fElementStack.empty())
      {
        localElementState = fElementStack.pop();
        fInternalNamespaceContext.popContext();
        if (!isEmpty)
        {
          fWriter.write("</");
          if ((prefix != null) && (!prefix.equals("")))
          {
            fWriter.write(prefix);
            fWriter.write(":");
          }
          fWriter.write(localpart);
          fWriter.write(62);
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new XMLStreamException("No more elements to write");
    }
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      ElementState localElementState = fElementStack.pop();
      if (localElementState == null) {
        throw new XMLStreamException("No element was found to write");
      }
      if (isEmpty) {
        return;
      }
      fWriter.write("</");
      if ((prefix != null) && (!prefix.equals("")))
      {
        fWriter.write(prefix);
        fWriter.write(":");
      }
      fWriter.write(localpart);
      fWriter.write(62);
      fInternalNamespaceContext.popContext();
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new XMLStreamException("No element was found to write: " + localArrayIndexOutOfBoundsException.toString(), localArrayIndexOutOfBoundsException);
    }
  }
  
  public void writeEntityRef(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      fWriter.write(38);
      fWriter.write(paramString);
      fWriter.write(59);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeNamespace(String paramString1, String paramString2)
    throws XMLStreamException
  {
    String str1 = null;
    if (paramString2 == null) {
      str1 = "";
    } else {
      str1 = paramString2;
    }
    try
    {
      QName localQName = null;
      if (!fStartTagOpened) {
        throw new IllegalStateException("Invalid state: start tag is not opened at writeNamespace(" + paramString1 + ", " + str1 + ")");
      }
      if ((paramString1 == null) || (paramString1.equals("")) || (paramString1.equals("xmlns")))
      {
        writeDefaultNamespace(str1);
        return;
      }
      if ((paramString1.equals("xml")) && (str1.equals("http://www.w3.org/XML/1998/namespace"))) {
        return;
      }
      paramString1 = fSymbolTable.addSymbol(paramString1);
      str1 = fSymbolTable.addSymbol(str1);
      String str2;
      if (fIsRepairingNamespace)
      {
        str2 = fInternalNamespaceContext.getURI(paramString1);
        if ((str2 != null) && (str2 == str1)) {
          return;
        }
        localQName = new QName();
        localQName.setValues(paramString1, "xmlns", null, str1);
        fNamespaceDecls.add(localQName);
        return;
      }
      if (fInternalNamespaceContext.containsPrefixInCurrentContext(paramString1))
      {
        str2 = fInternalNamespaceContext.getURI(paramString1);
        if ((str2 != null) && (str2 != str1)) {
          throw new XMLStreamException("prefix " + paramString1 + " has been already bound to " + str2 + ". Rebinding it to " + str1 + " is an error");
        }
      }
      fInternalNamespaceContext.declarePrefix(paramString1, str1);
      writenamespace(paramString1, str1);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  private void writenamespace(String paramString1, String paramString2)
    throws IOException
  {
    fWriter.write(" xmlns");
    if ((paramString1 != null) && (paramString1 != ""))
    {
      fWriter.write(":");
      fWriter.write(paramString1);
    }
    fWriter.write("=\"");
    writeXMLContent(paramString2, true, true);
    fWriter.write("\"");
  }
  
  public void writeProcessingInstruction(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      if (paramString != null)
      {
        fWriter.write("<?");
        fWriter.write(paramString);
        fWriter.write("?>");
        return;
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
    throw new XMLStreamException("PI target cannot be null");
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2)
    throws XMLStreamException
  {
    try
    {
      if (fStartTagOpened) {
        closeStartTag();
      }
      if ((paramString1 == null) || (paramString2 == null)) {
        throw new XMLStreamException("PI target cannot be null");
      }
      fWriter.write("<?");
      fWriter.write(paramString1);
      fWriter.write(" ");
      fWriter.write(paramString2);
      fWriter.write("?>");
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {
    try
    {
      fWriter.write("<?xml version=\"1.0\" ?>");
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    try
    {
      if ((paramString == null) || (paramString.equals("")))
      {
        writeStartDocument();
        return;
      }
      fWriter.write("<?xml version=\"");
      fWriter.write(paramString);
      fWriter.write("\"");
      fWriter.write("?>");
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    try
    {
      if ((paramString1 == null) && (paramString2 == null))
      {
        writeStartDocument();
        return;
      }
      if (paramString1 == null)
      {
        writeStartDocument(paramString2);
        return;
      }
      String str = null;
      if ((fWriter instanceof OutputStreamWriter)) {
        str = ((OutputStreamWriter)fWriter).getEncoding();
      } else if ((fWriter instanceof UTF8OutputStreamWriter)) {
        str = ((UTF8OutputStreamWriter)fWriter).getEncoding();
      } else if ((fWriter instanceof XMLWriter)) {
        str = ((OutputStreamWriter)((XMLWriter)fWriter).getWriter()).getEncoding();
      }
      if ((str != null) && (!str.equalsIgnoreCase(paramString1)))
      {
        int i = 0;
        Set localSet = Charset.forName(paramString1).aliases();
        Iterator localIterator = localSet.iterator();
        while ((i == 0) && (localIterator.hasNext())) {
          if (str.equalsIgnoreCase((String)localIterator.next())) {
            i = 1;
          }
        }
        if (i == 0) {
          throw new XMLStreamException("Underlying stream encoding '" + str + "' and input paramter for writeStartDocument() method '" + paramString1 + "' do not match.");
        }
      }
      fWriter.write("<?xml version=\"");
      if ((paramString2 == null) || (paramString2.equals(""))) {
        fWriter.write("1.0");
      } else {
        fWriter.write(paramString2);
      }
      if (!paramString1.equals(""))
      {
        fWriter.write("\" encoding=\"");
        fWriter.write(paramString1);
      }
      fWriter.write("\"?>");
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (paramString == null) {
        throw new XMLStreamException("Local Name cannot be null");
      }
      if (fStartTagOpened) {
        closeStartTag();
      }
      openStartTag();
      fElementStack.push(null, paramString, null, null, false);
      fInternalNamespaceContext.pushContext();
      if (fIsRepairingNamespace) {
        return;
      }
      fWriter.write(paramString);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeStartElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramString2 == null) {
      throw new XMLStreamException("Local Name cannot be null");
    }
    if (paramString1 == null) {
      throw new XMLStreamException("NamespaceURI cannot be null");
    }
    paramString1 = fSymbolTable.addSymbol(paramString1);
    String str = null;
    if (!fIsRepairingNamespace)
    {
      str = fNamespaceContext.getPrefix(paramString1);
      if (str != null) {
        str = fSymbolTable.addSymbol(str);
      }
    }
    writeStartElement(str, paramString2, paramString1);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    try
    {
      if (paramString2 == null) {
        throw new XMLStreamException("Local Name cannot be null");
      }
      if (paramString3 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if ((!fIsRepairingNamespace) && (paramString1 == null)) {
        throw new XMLStreamException("Prefix cannot be null");
      }
      if (fStartTagOpened) {
        closeStartTag();
      }
      openStartTag();
      paramString3 = fSymbolTable.addSymbol(paramString3);
      if (paramString1 != null) {
        paramString1 = fSymbolTable.addSymbol(paramString1);
      }
      fElementStack.push(paramString1, paramString2, null, paramString3, false);
      fInternalNamespaceContext.pushContext();
      String str = fNamespaceContext.getPrefix(paramString3);
      if ((paramString1 != null) && ((str == null) || (!paramString1.equals(str)))) {
        fInternalNamespaceContext.declarePrefix(paramString1, paramString3);
      }
      if (fIsRepairingNamespace)
      {
        if ((paramString1 == null) || ((str != null) && (paramString1.equals(str)))) {
          return;
        }
        QName localQName = new QName();
        localQName.setValues(paramString1, "xmlns", null, paramString3);
        fNamespaceDecls.add(localQName);
        return;
      }
      if ((paramString1 != null) && (paramString1 != ""))
      {
        fWriter.write(paramString1);
        fWriter.write(":");
      }
      fWriter.write(paramString2);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  private void writeCharRef(int paramInt)
    throws IOException
  {
    fWriter.write("&#x");
    fWriter.write(Integer.toHexString(paramInt));
    fWriter.write(59);
  }
  
  private void writeXMLContent(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean)
    {
      fWriter.write(paramArrayOfChar, paramInt1, paramInt2);
      return;
    }
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++)
    {
      char c = paramArrayOfChar[k];
      if ((fEncoder != null) && (!fEncoder.canEncode(c)))
      {
        fWriter.write(paramArrayOfChar, i, k - i);
        if ((k != j - 1) && (Character.isSurrogatePair(c, paramArrayOfChar[(k + 1)])))
        {
          writeCharRef(Character.toCodePoint(c, paramArrayOfChar[(k + 1)]));
          k++;
        }
        else
        {
          writeCharRef(c);
        }
        i = k + 1;
      }
      else
      {
        switch (c)
        {
        case '<': 
          fWriter.write(paramArrayOfChar, i, k - i);
          fWriter.write("&lt;");
          i = k + 1;
          break;
        case '&': 
          fWriter.write(paramArrayOfChar, i, k - i);
          fWriter.write("&amp;");
          i = k + 1;
          break;
        case '>': 
          fWriter.write(paramArrayOfChar, i, k - i);
          fWriter.write("&gt;");
          i = k + 1;
        }
      }
    }
    fWriter.write(paramArrayOfChar, i, j - i);
  }
  
  private void writeXMLContent(String paramString)
    throws IOException
  {
    if ((paramString != null) && (paramString.length() > 0)) {
      writeXMLContent(paramString, fEscapeCharacters, false);
    }
  }
  
  private void writeXMLContent(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    if (!paramBoolean1)
    {
      fWriter.write(paramString);
      return;
    }
    int i = 0;
    int j = paramString.length();
    for (int k = 0; k < j; k++)
    {
      char c = paramString.charAt(k);
      if ((fEncoder != null) && (!fEncoder.canEncode(c)))
      {
        fWriter.write(paramString, i, k - i);
        if ((k != j - 1) && (Character.isSurrogatePair(c, paramString.charAt(k + 1))))
        {
          writeCharRef(Character.toCodePoint(c, paramString.charAt(k + 1)));
          k++;
        }
        else
        {
          writeCharRef(c);
        }
        i = k + 1;
      }
      else
      {
        switch (c)
        {
        case '<': 
          fWriter.write(paramString, i, k - i);
          fWriter.write("&lt;");
          i = k + 1;
          break;
        case '&': 
          fWriter.write(paramString, i, k - i);
          fWriter.write("&amp;");
          i = k + 1;
          break;
        case '>': 
          fWriter.write(paramString, i, k - i);
          fWriter.write("&gt;");
          i = k + 1;
          break;
        case '"': 
          fWriter.write(paramString, i, k - i);
          if (paramBoolean2) {
            fWriter.write("&quot;");
          } else {
            fWriter.write(34);
          }
          i = k + 1;
        }
      }
    }
    fWriter.write(paramString, i, j - i);
  }
  
  private void closeStartTag()
    throws XMLStreamException
  {
    try
    {
      ElementState localElementState = fElementStack.peek();
      if (fIsRepairingNamespace)
      {
        repair();
        correctPrefix(localElementState, 1);
        if ((prefix != null) && (prefix != ""))
        {
          fWriter.write(prefix);
          fWriter.write(":");
        }
        fWriter.write(localpart);
        int i = fNamespaceDecls.size();
        QName localQName = null;
        for (int j = 0; j < i; j++)
        {
          localQName = (QName)fNamespaceDecls.get(j);
          if ((localQName != null) && (fInternalNamespaceContext.declarePrefix(prefix, uri))) {
            writenamespace(prefix, uri);
          }
        }
        fNamespaceDecls.clear();
        Attribute localAttribute = null;
        for (int k = 0; k < fAttributeCache.size(); k++)
        {
          localAttribute = (Attribute)fAttributeCache.get(k);
          if ((prefix != null) && (uri != null) && (!prefix.equals("")) && (!uri.equals("")))
          {
            String str = fInternalNamespaceContext.getPrefix(uri);
            if ((str == null) || (str != prefix))
            {
              str = getAttrPrefix(uri);
              if (str == null)
              {
                if (fInternalNamespaceContext.declarePrefix(prefix, uri)) {
                  writenamespace(prefix, uri);
                }
              }
              else {
                writenamespace(prefix, uri);
              }
            }
          }
          writeAttributeWithPrefix(prefix, localpart, value);
        }
        fAttrNamespace = null;
        fAttributeCache.clear();
      }
      if (isEmpty)
      {
        fElementStack.pop();
        fInternalNamespaceContext.popContext();
        fWriter.write("/>");
      }
      else
      {
        fWriter.write(62);
      }
      fStartTagOpened = false;
    }
    catch (IOException localIOException)
    {
      fStartTagOpened = false;
      throw new XMLStreamException(localIOException);
    }
  }
  
  private void openStartTag()
    throws IOException
  {
    fStartTagOpened = true;
    fWriter.write(60);
  }
  
  private void correctPrefix(QName paramQName, int paramInt)
  {
    String str1 = null;
    String str2 = prefix;
    String str3 = uri;
    int i = 0;
    if ((str2 == null) || (str2.equals("")))
    {
      if (str3 == null) {
        return;
      }
      if ((str2 == "") && (str3 == "")) {
        return;
      }
      str3 = fSymbolTable.addSymbol(str3);
      QName localQName = null;
      for (int j = 0; j < fNamespaceDecls.size(); j++)
      {
        localQName = (QName)fNamespaceDecls.get(j);
        if ((localQName != null) && (uri == uri))
        {
          prefix = prefix;
          return;
        }
      }
      str1 = fNamespaceContext.getPrefix(str3);
      if (str1 == "")
      {
        if (paramInt == 1) {
          return;
        }
        if (paramInt == 10)
        {
          str1 = getAttrPrefix(str3);
          i = 1;
        }
      }
      Object localObject;
      if (str1 == null)
      {
        localObject = new StringBuffer("zdef");
        for (int k = 0; k < 1; k++) {
          ((StringBuffer)localObject).append(fPrefixGen.nextInt());
        }
        str2 = ((StringBuffer)localObject).toString();
        str2 = fSymbolTable.addSymbol(str2);
      }
      else
      {
        str2 = fSymbolTable.addSymbol(str1);
      }
      if (str1 == null) {
        if (i != 0)
        {
          addAttrNamespace(str2, str3);
        }
        else
        {
          localObject = new QName();
          ((QName)localObject).setValues(str2, "xmlns", null, str3);
          fNamespaceDecls.add(localObject);
          fInternalNamespaceContext.declarePrefix(fSymbolTable.addSymbol(str2), str3);
        }
      }
    }
    prefix = str2;
  }
  
  private String getAttrPrefix(String paramString)
  {
    if (fAttrNamespace != null) {
      return (String)fAttrNamespace.get(paramString);
    }
    return null;
  }
  
  private void addAttrNamespace(String paramString1, String paramString2)
  {
    if (fAttrNamespace == null) {
      fAttrNamespace = new HashMap();
    }
    fAttrNamespace.put(paramString1, paramString2);
  }
  
  private boolean isDefaultNamespace(String paramString)
  {
    String str = fInternalNamespaceContext.getURI(DEFAULT_PREFIX);
    return paramString == str;
  }
  
  private boolean checkUserNamespaceContext(String paramString1, String paramString2)
  {
    if (fNamespaceContext.userContext != null)
    {
      String str = fNamespaceContext.userContext.getNamespaceURI(paramString1);
      if ((str != null) && (str.equals(paramString2))) {
        return true;
      }
    }
    return false;
  }
  
  protected void repair()
  {
    Attribute localAttribute1 = null;
    Attribute localAttribute2 = null;
    ElementState localElementState = fElementStack.peek();
    removeDuplicateDecls();
    for (int i = 0; i < fAttributeCache.size(); i++)
    {
      localAttribute1 = (Attribute)fAttributeCache.get(i);
      if (((prefix != null) && (!prefix.equals(""))) || ((uri != null) && (!uri.equals("")))) {
        correctPrefix(localElementState, localAttribute1);
      }
    }
    if ((!isDeclared(localElementState)) && (prefix != null) && (uri != null) && (!prefix.equals("")) && (!uri.equals(""))) {
      fNamespaceDecls.add(localElementState);
    }
    for (i = 0; i < fAttributeCache.size(); i++)
    {
      localAttribute1 = (Attribute)fAttributeCache.get(i);
      for (int j = i + 1; j < fAttributeCache.size(); j++)
      {
        localAttribute2 = (Attribute)fAttributeCache.get(j);
        if ((!"".equals(prefix)) && (!"".equals(prefix))) {
          correctPrefix(localAttribute1, localAttribute2);
        }
      }
    }
    repairNamespaceDecl(localElementState);
    i = 0;
    for (i = 0; i < fAttributeCache.size(); i++)
    {
      localAttribute1 = (Attribute)fAttributeCache.get(i);
      if ((prefix != null) && (prefix.equals("")) && (uri != null) && (uri.equals(""))) {
        repairNamespaceDecl(localAttribute1);
      }
    }
    QName localQName = null;
    for (i = 0; i < fNamespaceDecls.size(); i++)
    {
      localQName = (QName)fNamespaceDecls.get(i);
      if (localQName != null) {
        fInternalNamespaceContext.declarePrefix(prefix, uri);
      }
    }
    for (i = 0; i < fAttributeCache.size(); i++)
    {
      localAttribute1 = (Attribute)fAttributeCache.get(i);
      correctPrefix(localAttribute1, 10);
    }
  }
  
  void correctPrefix(QName paramQName1, QName paramQName2)
  {
    String str = null;
    QName localQName1 = null;
    int i = 0;
    checkForNull(paramQName1);
    checkForNull(paramQName2);
    if ((prefix.equals(prefix)) && (!uri.equals(uri)))
    {
      str = fNamespaceContext.getPrefix(uri);
      if (str != null)
      {
        prefix = fSymbolTable.addSymbol(str);
      }
      else
      {
        localQName1 = null;
        for (int j = 0; j < fNamespaceDecls.size(); j++)
        {
          localQName1 = (QName)fNamespaceDecls.get(j);
          if ((localQName1 != null) && (uri == uri))
          {
            prefix = prefix;
            return;
          }
        }
        StringBuffer localStringBuffer = new StringBuffer("zdef");
        for (int k = 0; k < 1; k++) {
          localStringBuffer.append(fPrefixGen.nextInt());
        }
        str = localStringBuffer.toString();
        str = fSymbolTable.addSymbol(str);
        prefix = str;
        QName localQName2 = new QName();
        localQName2.setValues(str, "xmlns", null, uri);
        fNamespaceDecls.add(localQName2);
      }
    }
  }
  
  void checkForNull(QName paramQName)
  {
    if (prefix == null) {
      prefix = "";
    }
    if (uri == null) {
      uri = "";
    }
  }
  
  void removeDuplicateDecls()
  {
    for (int i = 0; i < fNamespaceDecls.size(); i++)
    {
      QName localQName1 = (QName)fNamespaceDecls.get(i);
      if (localQName1 != null) {
        for (int j = i + 1; j < fNamespaceDecls.size(); j++)
        {
          QName localQName2 = (QName)fNamespaceDecls.get(j);
          if ((localQName2 != null) && (prefix.equals(prefix)) && (uri.equals(uri))) {
            fNamespaceDecls.remove(j);
          }
        }
      }
    }
  }
  
  void repairNamespaceDecl(QName paramQName)
  {
    QName localQName = null;
    for (int i = 0; i < fNamespaceDecls.size(); i++)
    {
      localQName = (QName)fNamespaceDecls.get(i);
      if ((localQName != null) && (prefix != null) && (prefix.equals(prefix)) && (!uri.equals(uri)))
      {
        String str = fNamespaceContext.getNamespaceURI(prefix);
        if (str != null) {
          if (str.equals(uri)) {
            fNamespaceDecls.set(i, null);
          } else {
            uri = uri;
          }
        }
      }
    }
  }
  
  boolean isDeclared(QName paramQName)
  {
    QName localQName = null;
    for (int i = 0; i < fNamespaceDecls.size(); i++)
    {
      localQName = (QName)fNamespaceDecls.get(i);
      if ((prefix != null) && (prefix == prefix) && (uri == uri)) {
        return true;
      }
    }
    return (uri != null) && (fNamespaceContext.getPrefix(uri) != null);
  }
  
  public int size()
  {
    return 1;
  }
  
  public boolean isEmpty()
  {
    return false;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return paramObject.equals("sjsxp-outputstream");
  }
  
  public Object get(Object paramObject)
  {
    if (paramObject.equals("sjsxp-outputstream")) {
      return fOutputStream;
    }
    return null;
  }
  
  public Set entrySet()
  {
    throw new UnsupportedOperationException();
  }
  
  public String toString()
  {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
  }
  
  public int hashCode()
  {
    return fElementStack.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return this == paramObject;
  }
  
  class Attribute
    extends QName
  {
    String value;
    
    Attribute(String paramString)
    {
      value = paramString;
    }
  }
  
  protected class ElementStack
  {
    protected XMLStreamWriterImpl.ElementState[] fElements = new XMLStreamWriterImpl.ElementState[10];
    protected short fDepth;
    
    public ElementStack()
    {
      for (int i = 0; i < fElements.length; i++) {
        fElements[i] = new XMLStreamWriterImpl.ElementState(XMLStreamWriterImpl.this);
      }
    }
    
    public XMLStreamWriterImpl.ElementState push(XMLStreamWriterImpl.ElementState paramElementState)
    {
      if (fDepth == fElements.length)
      {
        XMLStreamWriterImpl.ElementState[] arrayOfElementState = new XMLStreamWriterImpl.ElementState[fElements.length * 2];
        System.arraycopy(fElements, 0, arrayOfElementState, 0, fDepth);
        fElements = arrayOfElementState;
        for (int i = fDepth; i < fElements.length; i++) {
          fElements[i] = new XMLStreamWriterImpl.ElementState(XMLStreamWriterImpl.this);
        }
      }
      fElements[fDepth].setValues(paramElementState);
      return fElements[(fDepth++)];
    }
    
    public XMLStreamWriterImpl.ElementState push(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean)
    {
      if (fDepth == fElements.length)
      {
        XMLStreamWriterImpl.ElementState[] arrayOfElementState = new XMLStreamWriterImpl.ElementState[fElements.length * 2];
        System.arraycopy(fElements, 0, arrayOfElementState, 0, fDepth);
        fElements = arrayOfElementState;
        for (int i = fDepth; i < fElements.length; i++) {
          fElements[i] = new XMLStreamWriterImpl.ElementState(XMLStreamWriterImpl.this);
        }
      }
      fElements[fDepth].setValues(paramString1, paramString2, paramString3, paramString4, paramBoolean);
      return fElements[(fDepth++)];
    }
    
    public XMLStreamWriterImpl.ElementState pop()
    {
      return fElements[(fDepth = (short)(fDepth - 1))];
    }
    
    public void clear()
    {
      fDepth = 0;
    }
    
    public XMLStreamWriterImpl.ElementState peek()
    {
      return fElements[(fDepth - 1)];
    }
    
    public boolean empty()
    {
      return fDepth <= 0;
    }
  }
  
  class ElementState
    extends QName
  {
    public boolean isEmpty = false;
    
    public ElementState() {}
    
    public ElementState(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      super(paramString2, paramString3, paramString4);
    }
    
    public void setValues(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean)
    {
      super.setValues(paramString1, paramString2, paramString3, paramString4);
      isEmpty = paramBoolean;
    }
  }
  
  class NamespaceContextImpl
    implements NamespaceContext
  {
    NamespaceContext userContext = null;
    NamespaceSupport internalContext = null;
    
    NamespaceContextImpl() {}
    
    public String getNamespaceURI(String paramString)
    {
      String str = null;
      if (paramString != null) {
        paramString = fSymbolTable.addSymbol(paramString);
      }
      if (internalContext != null)
      {
        str = internalContext.getURI(paramString);
        if (str != null) {
          return str;
        }
      }
      if (userContext != null)
      {
        str = userContext.getNamespaceURI(paramString);
        return str;
      }
      return null;
    }
    
    public String getPrefix(String paramString)
    {
      String str = null;
      if (paramString != null) {
        paramString = fSymbolTable.addSymbol(paramString);
      }
      if (internalContext != null)
      {
        str = internalContext.getPrefix(paramString);
        if (str != null) {
          return str;
        }
      }
      if (userContext != null) {
        return userContext.getPrefix(paramString);
      }
      return null;
    }
    
    public Iterator getPrefixes(String paramString)
    {
      Vector localVector = null;
      Iterator localIterator = null;
      if (paramString != null) {
        paramString = fSymbolTable.addSymbol(paramString);
      }
      if (userContext != null) {
        localIterator = userContext.getPrefixes(paramString);
      }
      if (internalContext != null) {
        localVector = internalContext.getPrefixes(paramString);
      }
      if ((localVector == null) && (localIterator != null)) {
        return localIterator;
      }
      if ((localVector != null) && (localIterator == null)) {
        return new ReadOnlyIterator(localVector.iterator());
      }
      if ((localVector != null) && (localIterator != null))
      {
        String str = null;
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          if (str != null) {
            str = fSymbolTable.addSymbol(str);
          }
          if (!localVector.contains(str)) {
            localVector.add(str);
          }
        }
        return new ReadOnlyIterator(localVector.iterator());
      }
      return fReadOnlyIterator;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\writers\XMLStreamWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */