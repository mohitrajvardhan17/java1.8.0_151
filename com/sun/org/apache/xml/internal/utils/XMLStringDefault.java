package com.sun.org.apache.xml.internal.utils;

import java.util.Locale;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XMLStringDefault
  implements XMLString
{
  private String m_str;
  
  public XMLStringDefault(String paramString)
  {
    m_str = paramString;
  }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler)
    throws SAXException
  {}
  
  public void dispatchAsComment(LexicalHandler paramLexicalHandler)
    throws SAXException
  {}
  
  public XMLString fixWhiteSpace(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    return new XMLStringDefault(m_str.trim());
  }
  
  public int length()
  {
    return m_str.length();
  }
  
  public char charAt(int paramInt)
  {
    return m_str.charAt(paramInt);
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    int i = paramInt3;
    for (int j = paramInt1; j < paramInt2; j++) {
      paramArrayOfChar[(i++)] = m_str.charAt(j);
    }
  }
  
  public boolean equals(String paramString)
  {
    return m_str.equals(paramString);
  }
  
  public boolean equals(XMLString paramXMLString)
  {
    return m_str.equals(paramXMLString.toString());
  }
  
  public boolean equals(Object paramObject)
  {
    return m_str.equals(paramObject);
  }
  
  public boolean equalsIgnoreCase(String paramString)
  {
    return m_str.equalsIgnoreCase(paramString);
  }
  
  public int compareTo(XMLString paramXMLString)
  {
    return m_str.compareTo(paramXMLString.toString());
  }
  
  public int compareToIgnoreCase(XMLString paramXMLString)
  {
    return m_str.compareToIgnoreCase(paramXMLString.toString());
  }
  
  public boolean startsWith(String paramString, int paramInt)
  {
    return m_str.startsWith(paramString, paramInt);
  }
  
  public boolean startsWith(XMLString paramXMLString, int paramInt)
  {
    return m_str.startsWith(paramXMLString.toString(), paramInt);
  }
  
  public boolean startsWith(String paramString)
  {
    return m_str.startsWith(paramString);
  }
  
  public boolean startsWith(XMLString paramXMLString)
  {
    return m_str.startsWith(paramXMLString.toString());
  }
  
  public boolean endsWith(String paramString)
  {
    return m_str.endsWith(paramString);
  }
  
  public int hashCode()
  {
    return m_str.hashCode();
  }
  
  public int indexOf(int paramInt)
  {
    return m_str.indexOf(paramInt);
  }
  
  public int indexOf(int paramInt1, int paramInt2)
  {
    return m_str.indexOf(paramInt1, paramInt2);
  }
  
  public int lastIndexOf(int paramInt)
  {
    return m_str.lastIndexOf(paramInt);
  }
  
  public int lastIndexOf(int paramInt1, int paramInt2)
  {
    return m_str.lastIndexOf(paramInt1, paramInt2);
  }
  
  public int indexOf(String paramString)
  {
    return m_str.indexOf(paramString);
  }
  
  public int indexOf(XMLString paramXMLString)
  {
    return m_str.indexOf(paramXMLString.toString());
  }
  
  public int indexOf(String paramString, int paramInt)
  {
    return m_str.indexOf(paramString, paramInt);
  }
  
  public int lastIndexOf(String paramString)
  {
    return m_str.lastIndexOf(paramString);
  }
  
  public int lastIndexOf(String paramString, int paramInt)
  {
    return m_str.lastIndexOf(paramString, paramInt);
  }
  
  public XMLString substring(int paramInt)
  {
    return new XMLStringDefault(m_str.substring(paramInt));
  }
  
  public XMLString substring(int paramInt1, int paramInt2)
  {
    return new XMLStringDefault(m_str.substring(paramInt1, paramInt2));
  }
  
  public XMLString concat(String paramString)
  {
    return new XMLStringDefault(m_str.concat(paramString));
  }
  
  public XMLString toLowerCase(Locale paramLocale)
  {
    return new XMLStringDefault(m_str.toLowerCase(paramLocale));
  }
  
  public XMLString toLowerCase()
  {
    return new XMLStringDefault(m_str.toLowerCase());
  }
  
  public XMLString toUpperCase(Locale paramLocale)
  {
    return new XMLStringDefault(m_str.toUpperCase(paramLocale));
  }
  
  public XMLString toUpperCase()
  {
    return new XMLStringDefault(m_str.toUpperCase());
  }
  
  public XMLString trim()
  {
    return new XMLStringDefault(m_str.trim());
  }
  
  public String toString()
  {
    return m_str;
  }
  
  public boolean hasString()
  {
    return true;
  }
  
  public double toDouble()
  {
    try
    {
      return Double.valueOf(m_str).doubleValue();
    }
    catch (NumberFormatException localNumberFormatException) {}
    return NaN.0D;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\XMLStringDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */