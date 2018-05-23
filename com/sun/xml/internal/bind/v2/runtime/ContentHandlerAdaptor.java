package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.SAXException2;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class ContentHandlerAdaptor
  extends DefaultHandler
{
  private final FinalArrayList<String> prefixMap = new FinalArrayList();
  private final XMLSerializer serializer;
  private final StringBuffer text = new StringBuffer();
  
  ContentHandlerAdaptor(XMLSerializer paramXMLSerializer)
  {
    serializer = paramXMLSerializer;
  }
  
  public void startDocument()
  {
    prefixMap.clear();
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
  {
    prefixMap.add(paramString1);
    prefixMap.add(paramString2);
  }
  
  private boolean containsPrefixMapping(String paramString1, String paramString2)
  {
    for (int i = 0; i < prefixMap.size(); i += 2) {
      if ((((String)prefixMap.get(i)).equals(paramString1)) && (((String)prefixMap.get(i + 1)).equals(paramString2))) {
        return true;
      }
    }
    return false;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    try
    {
      flushText();
      int i = paramAttributes.getLength();
      String str1 = getPrefix(paramString3);
      if (containsPrefixMapping(str1, paramString1)) {
        serializer.startElementForce(paramString1, paramString2, str1, null);
      } else {
        serializer.startElement(paramString1, paramString2, str1, null);
      }
      for (int j = 0; j < prefixMap.size(); j += 2) {
        serializer.getNamespaceContext().force((String)prefixMap.get(j + 1), (String)prefixMap.get(j));
      }
      for (j = 0; j < i; j++)
      {
        String str2 = paramAttributes.getQName(j);
        if ((!str2.startsWith("xmlns")) && (paramAttributes.getURI(j).length() != 0))
        {
          String str3 = getPrefix(str2);
          serializer.getNamespaceContext().declareNamespace(paramAttributes.getURI(j), str3, true);
        }
      }
      serializer.endNamespaceDecls(null);
      for (j = 0; j < i; j++) {
        if (!paramAttributes.getQName(j).startsWith("xmlns")) {
          serializer.attribute(paramAttributes.getURI(j), paramAttributes.getLocalName(j), paramAttributes.getValue(j));
        }
      }
      prefixMap.clear();
      serializer.endAttributes();
    }
    catch (IOException localIOException)
    {
      throw new SAXException2(localIOException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException2(localXMLStreamException);
    }
  }
  
  private String getPrefix(String paramString)
  {
    int i = paramString.indexOf(':');
    String str = i == -1 ? "" : paramString.substring(0, i);
    return str;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      flushText();
      serializer.endElement();
    }
    catch (IOException localIOException)
    {
      throw new SAXException2(localIOException);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new SAXException2(localXMLStreamException);
    }
  }
  
  private void flushText()
    throws SAXException, IOException, XMLStreamException
  {
    if (text.length() != 0)
    {
      serializer.text(text.toString(), null);
      text.setLength(0);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    text.append(paramArrayOfChar, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ContentHandlerAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */