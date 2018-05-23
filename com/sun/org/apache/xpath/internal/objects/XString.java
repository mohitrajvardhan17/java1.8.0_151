package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.util.Locale;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XString
  extends XObject
  implements XMLString
{
  static final long serialVersionUID = 2020470518395094525L;
  public static final XString EMPTYSTRING = new XString("");
  
  protected XString(Object paramObject)
  {
    super(paramObject);
  }
  
  public XString(String paramString)
  {
    super(paramString);
  }
  
  public int getType()
  {
    return 3;
  }
  
  public String getTypeString()
  {
    return "#STRING";
  }
  
  public boolean hasString()
  {
    return true;
  }
  
  public double num()
  {
    return toDouble();
  }
  
  public double toDouble()
  {
    XMLString localXMLString = trim();
    double d = NaN.0D;
    for (int i = 0; i < localXMLString.length(); i++)
    {
      int j = localXMLString.charAt(i);
      if ((j != 45) && (j != 46) && ((j < 48) || (j > 57))) {
        return d;
      }
    }
    try
    {
      d = Double.parseDouble(localXMLString.toString());
    }
    catch (NumberFormatException localNumberFormatException) {}
    return d;
  }
  
  public boolean bool()
  {
    return str().length() > 0;
  }
  
  public XMLString xstr()
  {
    return this;
  }
  
  public String str()
  {
    return null != m_obj ? (String)m_obj : "";
  }
  
  public int rtf(XPathContext paramXPathContext)
  {
    DTM localDTM = paramXPathContext.createDocumentFragment();
    localDTM.appendTextChild(str());
    return localDTM.getDocument();
  }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler)
    throws SAXException
  {
    String str = str();
    paramContentHandler.characters(str.toCharArray(), 0, str.length());
  }
  
  public void dispatchAsComment(LexicalHandler paramLexicalHandler)
    throws SAXException
  {
    String str = str();
    paramLexicalHandler.comment(str.toCharArray(), 0, str.length());
  }
  
  public int length()
  {
    return str().length();
  }
  
  public char charAt(int paramInt)
  {
    return str().charAt(paramInt);
  }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    str().getChars(paramInt1, paramInt2, paramArrayOfChar, paramInt3);
  }
  
  public boolean equals(XObject paramXObject)
  {
    int i = paramXObject.getType();
    try
    {
      if (4 == i) {
        return paramXObject.equals(this);
      }
      if (1 == i) {
        return paramXObject.bool() == bool();
      }
      if (2 == i) {
        return paramXObject.num() == num();
      }
    }
    catch (TransformerException localTransformerException)
    {
      throw new WrappedRuntimeException(localTransformerException);
    }
    return xstr().equals(paramXObject.xstr());
  }
  
  public boolean equals(String paramString)
  {
    return str().equals(paramString);
  }
  
  public boolean equals(XMLString paramXMLString)
  {
    if (paramXMLString != null)
    {
      if (!paramXMLString.hasString()) {
        return paramXMLString.equals(str());
      }
      return str().equals(paramXMLString.toString());
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (null == paramObject) {
      return false;
    }
    if ((paramObject instanceof XNodeSet)) {
      return paramObject.equals(this);
    }
    if ((paramObject instanceof XNumber)) {
      return paramObject.equals(this);
    }
    return str().equals(paramObject.toString());
  }
  
  public boolean equalsIgnoreCase(String paramString)
  {
    return str().equalsIgnoreCase(paramString);
  }
  
  public int compareTo(XMLString paramXMLString)
  {
    int i = length();
    int j = paramXMLString.length();
    int k = Math.min(i, j);
    int m = 0;
    for (int n = 0; k-- != 0; n++)
    {
      int i1 = charAt(m);
      int i2 = paramXMLString.charAt(n);
      if (i1 != i2) {
        return i1 - i2;
      }
      m++;
    }
    return i - j;
  }
  
  public int compareToIgnoreCase(XMLString paramXMLString)
  {
    throw new WrappedRuntimeException(new NoSuchMethodException("Java 1.2 method, not yet implemented"));
  }
  
  public boolean startsWith(String paramString, int paramInt)
  {
    return str().startsWith(paramString, paramInt);
  }
  
  public boolean startsWith(String paramString)
  {
    return startsWith(paramString, 0);
  }
  
  public boolean startsWith(XMLString paramXMLString, int paramInt)
  {
    int i = paramInt;
    int j = length();
    int k = 0;
    int m = paramXMLString.length();
    if ((paramInt < 0) || (paramInt > j - m)) {
      return false;
    }
    for (;;)
    {
      m--;
      if (m < 0) {
        break;
      }
      if (charAt(i) != paramXMLString.charAt(k)) {
        return false;
      }
      i++;
      k++;
    }
    return true;
  }
  
  public boolean startsWith(XMLString paramXMLString)
  {
    return startsWith(paramXMLString, 0);
  }
  
  public boolean endsWith(String paramString)
  {
    return str().endsWith(paramString);
  }
  
  public int hashCode()
  {
    return str().hashCode();
  }
  
  public int indexOf(int paramInt)
  {
    return str().indexOf(paramInt);
  }
  
  public int indexOf(int paramInt1, int paramInt2)
  {
    return str().indexOf(paramInt1, paramInt2);
  }
  
  public int lastIndexOf(int paramInt)
  {
    return str().lastIndexOf(paramInt);
  }
  
  public int lastIndexOf(int paramInt1, int paramInt2)
  {
    return str().lastIndexOf(paramInt1, paramInt2);
  }
  
  public int indexOf(String paramString)
  {
    return str().indexOf(paramString);
  }
  
  public int indexOf(XMLString paramXMLString)
  {
    return str().indexOf(paramXMLString.toString());
  }
  
  public int indexOf(String paramString, int paramInt)
  {
    return str().indexOf(paramString, paramInt);
  }
  
  public int lastIndexOf(String paramString)
  {
    return str().lastIndexOf(paramString);
  }
  
  public int lastIndexOf(String paramString, int paramInt)
  {
    return str().lastIndexOf(paramString, paramInt);
  }
  
  public XMLString substring(int paramInt)
  {
    return new XString(str().substring(paramInt));
  }
  
  public XMLString substring(int paramInt1, int paramInt2)
  {
    return new XString(str().substring(paramInt1, paramInt2));
  }
  
  public XMLString concat(String paramString)
  {
    return new XString(str().concat(paramString));
  }
  
  public XMLString toLowerCase(Locale paramLocale)
  {
    return new XString(str().toLowerCase(paramLocale));
  }
  
  public XMLString toLowerCase()
  {
    return new XString(str().toLowerCase());
  }
  
  public XMLString toUpperCase(Locale paramLocale)
  {
    return new XString(str().toUpperCase(paramLocale));
  }
  
  public XMLString toUpperCase()
  {
    return new XString(str().toUpperCase());
  }
  
  public XMLString trim()
  {
    return new XString(str().trim());
  }
  
  private static boolean isSpace(char paramChar)
  {
    return XMLCharacterRecognizer.isWhiteSpace(paramChar);
  }
  
  public XMLString fixWhiteSpace(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i = length();
    char[] arrayOfChar = new char[i];
    getChars(0, i, arrayOfChar, 0);
    int j = 0;
    for (int k = 0; (k < i) && (!isSpace(arrayOfChar[k])); k++) {}
    int m = k;
    int n = 0;
    while (k < i)
    {
      i1 = arrayOfChar[k];
      if (isSpace(i1))
      {
        if (n == 0)
        {
          if (32 != i1) {
            j = 1;
          }
          arrayOfChar[(m++)] = ' ';
          if ((paramBoolean3) && (k != 0))
          {
            int i2 = arrayOfChar[(k - 1)];
            if ((i2 != 46) && (i2 != 33) && (i2 != 63)) {
              n = 1;
            }
          }
          else
          {
            n = 1;
          }
        }
        else
        {
          j = 1;
          n = 1;
        }
      }
      else
      {
        arrayOfChar[(m++)] = i1;
        n = 0;
      }
      k++;
    }
    if ((paramBoolean2) && (1 <= m) && (' ' == arrayOfChar[(m - 1)]))
    {
      j = 1;
      m--;
    }
    int i1 = 0;
    if ((paramBoolean1) && (0 < m) && (' ' == arrayOfChar[0]))
    {
      j = 1;
      i1++;
    }
    XMLStringFactory localXMLStringFactory = XMLStringFactoryImpl.getFactory();
    return j != 0 ? localXMLStringFactory.newstr(new String(arrayOfChar, i1, m - i1)) : this;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    paramXPathVisitor.visitStringLiteral(paramExpressionOwner, this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */