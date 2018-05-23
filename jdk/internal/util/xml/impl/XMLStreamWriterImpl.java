package jdk.internal.util.xml.impl;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import jdk.internal.util.xml.XMLStreamException;
import jdk.internal.util.xml.XMLStreamWriter;

public class XMLStreamWriterImpl
  implements XMLStreamWriter
{
  static final int STATE_XML_DECL = 1;
  static final int STATE_PROLOG = 2;
  static final int STATE_DTD_DECL = 3;
  static final int STATE_ELEMENT = 4;
  static final int ELEMENT_STARTTAG_OPEN = 10;
  static final int ELEMENT_STARTTAG_CLOSE = 11;
  static final int ELEMENT_ENDTAG_OPEN = 12;
  static final int ELEMENT_ENDTAG_CLOSE = 13;
  public static final char CLOSE_START_TAG = '>';
  public static final char OPEN_START_TAG = '<';
  public static final String OPEN_END_TAG = "</";
  public static final char CLOSE_END_TAG = '>';
  public static final String START_CDATA = "<![CDATA[";
  public static final String END_CDATA = "]]>";
  public static final String CLOSE_EMPTY_ELEMENT = "/>";
  public static final String ENCODING_PREFIX = "&#x";
  public static final char SPACE = ' ';
  public static final char AMPERSAND = '&';
  public static final char DOUBLEQUOT = '"';
  public static final char SEMICOLON = ';';
  private int _state = 0;
  private Element _currentEle;
  private XMLWriter _writer;
  private String _encoding;
  boolean _escapeCharacters = true;
  private boolean _doIndent = true;
  private char[] _lineSep = System.getProperty("line.separator").toCharArray();
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream)
    throws XMLStreamException
  {
    this(paramOutputStream, "UTF-8");
  }
  
  public XMLStreamWriterImpl(OutputStream paramOutputStream, String paramString)
    throws XMLStreamException
  {
    Charset localCharset = null;
    if (paramString == null)
    {
      _encoding = "UTF-8";
    }
    else
    {
      try
      {
        localCharset = getCharset(paramString);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new XMLStreamException(localUnsupportedEncodingException);
      }
      _encoding = paramString;
    }
    _writer = new XMLWriter(paramOutputStream, paramString, localCharset);
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {
    writeStartDocument(_encoding, "1.0");
  }
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    writeStartDocument(_encoding, paramString, null);
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writeStartDocument(paramString1, paramString2, null);
  }
  
  public void writeStartDocument(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    if (_state > 0) {
      throw new XMLStreamException("XML declaration must be as the first line in the XML document.");
    }
    _state = 1;
    String str = paramString1;
    if (str == null) {
      str = _encoding;
    } else {
      try
      {
        getCharset(paramString1);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new XMLStreamException(localUnsupportedEncodingException);
      }
    }
    if (paramString2 == null) {
      paramString2 = "1.0";
    }
    _writer.write("<?xml version=\"");
    _writer.write(paramString2);
    _writer.write(34);
    if (str != null)
    {
      _writer.write(" encoding=\"");
      _writer.write(str);
      _writer.write(34);
    }
    if (paramString3 != null)
    {
      _writer.write(" standalone=\"");
      _writer.write(paramString3);
      _writer.write(34);
    }
    _writer.write("?>");
    writeLineSeparator();
  }
  
  public void writeDTD(String paramString)
    throws XMLStreamException
  {
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    _writer.write(paramString);
    writeLineSeparator();
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new XMLStreamException("Local Name cannot be null or empty");
    }
    _state = 4;
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    _currentEle = new Element(_currentEle, paramString, false);
    openStartTag();
    _writer.write(paramString);
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    _currentEle = new Element(_currentEle, paramString, true);
    openStartTag();
    _writer.write(paramString);
  }
  
  public void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (_currentEle.getState() != 10) {
      throw new XMLStreamException("Attribute not associated with any element");
    }
    _writer.write(32);
    _writer.write(paramString1);
    _writer.write("=\"");
    writeXMLContent(paramString2, true, true);
    _writer.write(34);
  }
  
  public void writeEndDocument()
    throws XMLStreamException
  {
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    while (_currentEle != null)
    {
      if (!_currentEle.isEmpty())
      {
        _writer.write("</");
        _writer.write(_currentEle.getLocalName());
        _writer.write(62);
      }
      _currentEle = _currentEle.getParent();
    }
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    if (_currentEle == null) {
      throw new XMLStreamException("No element was found to write");
    }
    if (_currentEle.isEmpty()) {
      return;
    }
    _writer.write("</");
    _writer.write(_currentEle.getLocalName());
    _writer.write(62);
    writeLineSeparator();
    _currentEle = _currentEle.getParent();
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    if (paramString == null) {
      throw new XMLStreamException("cdata cannot be null");
    }
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    _writer.write("<![CDATA[");
    _writer.write(paramString);
    _writer.write("]]>");
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    writeXMLContent(paramString);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    if ((_currentEle != null) && (_currentEle.getState() == 10)) {
      closeStartTag();
    }
    writeXMLContent(paramArrayOfChar, paramInt1, paramInt2, _escapeCharacters);
  }
  
  public void close()
    throws XMLStreamException
  {
    if (_writer != null) {
      _writer.close();
    }
    _writer = null;
    _currentEle = null;
    _state = 0;
  }
  
  public void flush()
    throws XMLStreamException
  {
    if (_writer != null) {
      _writer.flush();
    }
  }
  
  public void setDoIndent(boolean paramBoolean)
  {
    _doIndent = paramBoolean;
  }
  
  private void writeXMLContent(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws XMLStreamException
  {
    if (!paramBoolean)
    {
      _writer.write(paramArrayOfChar, paramInt1, paramInt2);
      return;
    }
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++)
    {
      char c = paramArrayOfChar[k];
      if (!_writer.canEncode(c))
      {
        _writer.write(paramArrayOfChar, i, k - i);
        _writer.write("&#x");
        _writer.write(Integer.toHexString(c));
        _writer.write(59);
        i = k + 1;
      }
      else
      {
        switch (c)
        {
        case '<': 
          _writer.write(paramArrayOfChar, i, k - i);
          _writer.write("&lt;");
          i = k + 1;
          break;
        case '&': 
          _writer.write(paramArrayOfChar, i, k - i);
          _writer.write("&amp;");
          i = k + 1;
          break;
        case '>': 
          _writer.write(paramArrayOfChar, i, k - i);
          _writer.write("&gt;");
          i = k + 1;
        }
      }
    }
    _writer.write(paramArrayOfChar, i, j - i);
  }
  
  private void writeXMLContent(String paramString)
    throws XMLStreamException
  {
    if ((paramString != null) && (paramString.length() > 0)) {
      writeXMLContent(paramString, _escapeCharacters, false);
    }
  }
  
  private void writeXMLContent(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws XMLStreamException
  {
    if (!paramBoolean1)
    {
      _writer.write(paramString);
      return;
    }
    int i = 0;
    int j = paramString.length();
    for (int k = 0; k < j; k++)
    {
      char c = paramString.charAt(k);
      if (!_writer.canEncode(c))
      {
        _writer.write(paramString, i, k - i);
        _writer.write("&#x");
        _writer.write(Integer.toHexString(c));
        _writer.write(59);
        i = k + 1;
      }
      else
      {
        switch (c)
        {
        case '<': 
          _writer.write(paramString, i, k - i);
          _writer.write("&lt;");
          i = k + 1;
          break;
        case '&': 
          _writer.write(paramString, i, k - i);
          _writer.write("&amp;");
          i = k + 1;
          break;
        case '>': 
          _writer.write(paramString, i, k - i);
          _writer.write("&gt;");
          i = k + 1;
          break;
        case '"': 
          _writer.write(paramString, i, k - i);
          if (paramBoolean2) {
            _writer.write("&quot;");
          } else {
            _writer.write(34);
          }
          i = k + 1;
        }
      }
    }
    _writer.write(paramString, i, j - i);
  }
  
  private void openStartTag()
    throws XMLStreamException
  {
    _currentEle.setState(10);
    _writer.write(60);
  }
  
  private void closeStartTag()
    throws XMLStreamException
  {
    if (_currentEle.isEmpty()) {
      _writer.write("/>");
    } else {
      _writer.write(62);
    }
    if (_currentEle.getParent() == null) {
      writeLineSeparator();
    }
    _currentEle.setState(11);
  }
  
  private void writeLineSeparator()
    throws XMLStreamException
  {
    if (_doIndent) {
      _writer.write(_lineSep, 0, _lineSep.length);
    }
  }
  
  private Charset getCharset(String paramString)
    throws UnsupportedEncodingException
  {
    if (paramString.equalsIgnoreCase("UTF-32")) {
      throw new UnsupportedEncodingException("The basic XMLWriter does not support " + paramString);
    }
    Charset localCharset;
    try
    {
      localCharset = Charset.forName(paramString);
    }
    catch (IllegalCharsetNameException|UnsupportedCharsetException localIllegalCharsetNameException)
    {
      throw new UnsupportedEncodingException(paramString);
    }
    return localCharset;
  }
  
  protected class Element
  {
    protected Element _parent;
    protected short _Depth;
    boolean _isEmptyElement = false;
    String _localpart;
    int _state;
    
    public Element() {}
    
    public Element(Element paramElement, String paramString, boolean paramBoolean)
    {
      _parent = paramElement;
      _localpart = paramString;
      _isEmptyElement = paramBoolean;
    }
    
    public Element getParent()
    {
      return _parent;
    }
    
    public String getLocalName()
    {
      return _localpart;
    }
    
    public int getState()
    {
      return _state;
    }
    
    public void setState(int paramInt)
    {
      _state = paramInt;
    }
    
    public boolean isEmpty()
    {
      return _isEmptyElement;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\XMLStreamWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */