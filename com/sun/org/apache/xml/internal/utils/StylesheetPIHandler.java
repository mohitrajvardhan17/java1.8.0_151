package com.sun.org.apache.xml.internal.utils;

import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StylesheetPIHandler
  extends DefaultHandler
{
  String m_baseID;
  String m_media;
  String m_title;
  String m_charset;
  Vector m_stylesheets = new Vector();
  URIResolver m_uriResolver;
  
  public void setURIResolver(URIResolver paramURIResolver)
  {
    m_uriResolver = paramURIResolver;
  }
  
  public URIResolver getURIResolver()
  {
    return m_uriResolver;
  }
  
  public StylesheetPIHandler(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    m_baseID = paramString1;
    m_media = paramString2;
    m_title = paramString3;
    m_charset = paramString4;
  }
  
  public Source getAssociatedStylesheet()
  {
    int i = m_stylesheets.size();
    if (i > 0)
    {
      Source localSource = (Source)m_stylesheets.elementAt(i - 1);
      return localSource;
    }
    return null;
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (paramString1.equals("xml-stylesheet"))
    {
      Object localObject1 = null;
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      boolean bool = false;
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString2, " \t=\n", true);
      int i = 0;
      Object localObject2 = null;
      String str5 = "";
      while (localStringTokenizer.hasMoreTokens())
      {
        if (i == 0) {
          str5 = localStringTokenizer.nextToken();
        } else {
          i = 0;
        }
        if ((!localStringTokenizer.hasMoreTokens()) || ((!str5.equals(" ")) && (!str5.equals("\t")) && (!str5.equals("="))))
        {
          String str6 = str5;
          if (str6.equals("type"))
          {
            for (str5 = localStringTokenizer.nextToken(); (localStringTokenizer.hasMoreTokens()) && ((str5.equals(" ")) || (str5.equals("\t")) || (str5.equals("="))); str5 = localStringTokenizer.nextToken()) {}
            str1 = str5.substring(1, str5.length() - 1);
          }
          else if (str6.equals("href"))
          {
            for (str5 = localStringTokenizer.nextToken(); (localStringTokenizer.hasMoreTokens()) && ((str5.equals(" ")) || (str5.equals("\t")) || (str5.equals("="))); str5 = localStringTokenizer.nextToken()) {}
            localObject1 = str5;
            if (localStringTokenizer.hasMoreTokens())
            {
              str5 = localStringTokenizer.nextToken();
              while ((str5.equals("=")) && (localStringTokenizer.hasMoreTokens()))
              {
                localObject1 = (String)localObject1 + str5 + localStringTokenizer.nextToken();
                if (!localStringTokenizer.hasMoreTokens()) {
                  break;
                }
                str5 = localStringTokenizer.nextToken();
                i = 1;
              }
            }
            localObject1 = ((String)localObject1).substring(1, ((String)localObject1).length() - 1);
            try
            {
              if (m_uriResolver != null)
              {
                localObject2 = m_uriResolver.resolve((String)localObject1, m_baseID);
              }
              else
              {
                localObject1 = SystemIDResolver.getAbsoluteURI((String)localObject1, m_baseID);
                localObject2 = new SAXSource(new InputSource((String)localObject1));
              }
            }
            catch (TransformerException localTransformerException)
            {
              throw new SAXException(localTransformerException);
            }
          }
          else if (str6.equals("title"))
          {
            for (str5 = localStringTokenizer.nextToken(); (localStringTokenizer.hasMoreTokens()) && ((str5.equals(" ")) || (str5.equals("\t")) || (str5.equals("="))); str5 = localStringTokenizer.nextToken()) {}
            str2 = str5.substring(1, str5.length() - 1);
          }
          else if (str6.equals("media"))
          {
            for (str5 = localStringTokenizer.nextToken(); (localStringTokenizer.hasMoreTokens()) && ((str5.equals(" ")) || (str5.equals("\t")) || (str5.equals("="))); str5 = localStringTokenizer.nextToken()) {}
            str3 = str5.substring(1, str5.length() - 1);
          }
          else if (str6.equals("charset"))
          {
            for (str5 = localStringTokenizer.nextToken(); (localStringTokenizer.hasMoreTokens()) && ((str5.equals(" ")) || (str5.equals("\t")) || (str5.equals("="))); str5 = localStringTokenizer.nextToken()) {}
            str4 = str5.substring(1, str5.length() - 1);
          }
          else if (str6.equals("alternate"))
          {
            for (str5 = localStringTokenizer.nextToken(); (localStringTokenizer.hasMoreTokens()) && ((str5.equals(" ")) || (str5.equals("\t")) || (str5.equals("="))); str5 = localStringTokenizer.nextToken()) {}
            bool = str5.substring(1, str5.length() - 1).equals("yes");
          }
        }
      }
      if ((null != str1) && ((str1.equals("text/xsl")) || (str1.equals("text/xml")) || (str1.equals("application/xml+xslt"))) && (null != localObject1))
      {
        if (null != m_media) {
          if (null != str3)
          {
            if (str3.equals(m_media)) {}
          }
          else {
            return;
          }
        }
        if (null != m_charset) {
          if (null != str4)
          {
            if (str4.equals(m_charset)) {}
          }
          else {
            return;
          }
        }
        if (null != m_title) {
          if (null != str2)
          {
            if (str2.equals(m_title)) {}
          }
          else {
            return;
          }
        }
        m_stylesheets.addElement(localObject2);
      }
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    throw new StopParseException();
  }
  
  public void setBaseId(String paramString)
  {
    m_baseID = paramString;
  }
  
  public String getBaseId()
  {
    return m_baseID;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StylesheetPIHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */