package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XStringForChars
  extends XString
{
  static final long serialVersionUID = -2235248887220850467L;
  int m_start;
  int m_length;
  protected String m_strCache = null;
  
  public XStringForChars(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    super(paramArrayOfChar);
    m_start = paramInt1;
    m_length = paramInt2;
    if (null == paramArrayOfChar) {
      throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null));
    }
  }
  
  private XStringForChars(String paramString)
  {
    super(paramString);
    throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING", null));
  }
  
  public FastStringBuffer fsb()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS", null));
  }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer)
  {
    paramFastStringBuffer.append((char[])m_obj, m_start, m_length);
  }
  
  public boolean hasString()
  {
    return null != m_strCache;
  }
  
  public String str()
  {
    if (null == m_strCache) {
      m_strCache = new String((char[])m_obj, m_start, m_length);
    }
    return m_strCache;
  }
  
  public Object object()
  {
    return str();
  }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler)
    throws SAXException
  {
    paramContentHandler.characters((char[])m_obj, m_start, m_length);
  }
  
  public void dispatchAsComment(LexicalHandler paramLexicalHandler)
    throws SAXException
  {
    paramLexicalHandler.comment((char[])m_obj, m_start, m_length);
  }
  
  public int length()
  {
    return m_length;
  }
  
  public char charAt(int paramInt)
  {
    return ((char[])(char[])m_obj)[(paramInt + m_start)];
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    System.arraycopy((char[])m_obj, m_start + paramInt1, paramArrayOfChar, paramInt3, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XStringForChars.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */