package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xml.internal.serializer.EmptySerializer;
import org.xml.sax.SAXException;

public final class StringValueHandler
  extends EmptySerializer
{
  private StringBuilder _buffer = new StringBuilder();
  private String _str = null;
  private static final String EMPTY_STR = "";
  private boolean m_escaping = false;
  private int _nestedLevel = 0;
  
  public StringValueHandler() {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (_nestedLevel > 0) {
      return;
    }
    if (_str != null)
    {
      _buffer.append(_str);
      _str = null;
    }
    _buffer.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public String getValue()
  {
    if (_buffer.length() != 0)
    {
      str = _buffer.toString();
      _buffer.setLength(0);
      return str;
    }
    String str = _str;
    _str = null;
    return str != null ? str : "";
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    if (_nestedLevel > 0) {
      return;
    }
    if ((_str == null) && (_buffer.length() == 0))
    {
      _str = paramString;
    }
    else
    {
      if (_str != null)
      {
        _buffer.append(_str);
        _str = null;
      }
      _buffer.append(paramString);
    }
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    _nestedLevel += 1;
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    _nestedLevel -= 1;
  }
  
  public boolean setEscaping(boolean paramBoolean)
  {
    boolean bool = m_escaping;
    m_escaping = paramBoolean;
    return paramBoolean;
  }
  
  public String getValueOfPI()
  {
    String str = getValue();
    if (str.indexOf("?>") > 0)
    {
      int i = str.length();
      StringBuilder localStringBuilder = new StringBuilder();
      int j = 0;
      while (j < i)
      {
        char c = str.charAt(j++);
        if ((c == '?') && (str.charAt(j) == '>'))
        {
          localStringBuilder.append("? >");
          j++;
        }
        else
        {
          localStringBuilder.append(c);
        }
      }
      return localStringBuilder.toString();
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\StringValueHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */