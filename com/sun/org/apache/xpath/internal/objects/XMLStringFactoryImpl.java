package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;

public class XMLStringFactoryImpl
  extends XMLStringFactory
{
  private static XMLStringFactory m_xstringfactory = new XMLStringFactoryImpl();
  
  public XMLStringFactoryImpl() {}
  
  public static XMLStringFactory getFactory()
  {
    return m_xstringfactory;
  }
  
  public XMLString newstr(String paramString)
  {
    return new XString(paramString);
  }
  
  public XMLString newstr(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2)
  {
    return new XStringForFSB(paramFastStringBuffer, paramInt1, paramInt2);
  }
  
  public XMLString newstr(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return new XStringForChars(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public XMLString emptystr()
  {
    return XString.EMPTYSTRING;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XMLStringFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */