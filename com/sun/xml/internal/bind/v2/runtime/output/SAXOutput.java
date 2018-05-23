package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.util.AttributesImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public class SAXOutput
  extends XmlOutputAbstractImpl
{
  protected final ContentHandler out;
  private String elementNsUri;
  private String elementLocalName;
  private String elementQName;
  private char[] buf = new char['Ā'];
  private final AttributesImpl atts = new AttributesImpl();
  
  public SAXOutput(ContentHandler paramContentHandler)
  {
    out = paramContentHandler;
    paramContentHandler.setDocumentLocator(new LocatorImpl());
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws SAXException, IOException, XMLStreamException
  {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (!paramBoolean) {
      out.startDocument();
    }
  }
  
  public void endDocument(boolean paramBoolean)
    throws SAXException, IOException, XMLStreamException
  {
    if (!paramBoolean) {
      out.endDocument();
    }
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(int paramInt, String paramString)
  {
    elementNsUri = nsContext.getNamespaceURI(paramInt);
    elementLocalName = paramString;
    elementQName = getQName(paramInt, paramString);
    atts.clear();
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
  {
    String str2;
    String str1;
    if (paramInt == -1)
    {
      str2 = "";
      str1 = paramString1;
    }
    else
    {
      str2 = nsContext.getNamespaceURI(paramInt);
      String str3 = nsContext.getPrefix(paramInt);
      if (str3.length() == 0) {
        str1 = paramString1;
      } else {
        str1 = str3 + ':' + paramString1;
      }
    }
    atts.addAttribute(str2, paramString1, str1, "CDATA", paramString2);
  }
  
  public void endStartTag()
    throws SAXException
  {
    NamespaceContextImpl.Element localElement = nsContext.getCurrent();
    if (localElement != null)
    {
      int i = localElement.count();
      for (int j = 0; j < i; j++)
      {
        String str1 = localElement.getPrefix(j);
        String str2 = localElement.getNsUri(j);
        if ((str2.length() != 0) || (localElement.getBase() != 1)) {
          out.startPrefixMapping(str1, str2);
        }
      }
    }
    out.startElement(elementNsUri, elementLocalName, elementQName, atts);
  }
  
  public void endTag(int paramInt, String paramString)
    throws SAXException
  {
    out.endElement(nsContext.getNamespaceURI(paramInt), paramString, getQName(paramInt, paramString));
    NamespaceContextImpl.Element localElement = nsContext.getCurrent();
    if (localElement != null)
    {
      int i = localElement.count();
      for (int j = i - 1; j >= 0; j--)
      {
        String str1 = localElement.getPrefix(j);
        String str2 = localElement.getNsUri(j);
        if ((str2.length() != 0) || (localElement.getBase() != 1)) {
          out.endPrefixMapping(str1);
        }
      }
    }
  }
  
  private String getQName(int paramInt, String paramString)
  {
    String str2 = nsContext.getPrefix(paramInt);
    String str1;
    if (str2.length() == 0) {
      str1 = paramString;
    } else {
      str1 = str2 + ':' + paramString;
    }
    return str1;
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    int i = paramString.length();
    if (buf.length <= i) {
      buf = new char[Math.max(buf.length * 2, i + 1)];
    }
    if (paramBoolean)
    {
      paramString.getChars(0, i, buf, 1);
      buf[0] = ' ';
    }
    else
    {
      paramString.getChars(0, i, buf, 0);
    }
    out.characters(buf, 0, i + (paramBoolean ? 1 : 0));
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    int i = paramPcdata.length();
    if (buf.length <= i) {
      buf = new char[Math.max(buf.length * 2, i + 1)];
    }
    if (paramBoolean)
    {
      paramPcdata.writeTo(buf, 1);
      buf[0] = ' ';
    }
    else
    {
      paramPcdata.writeTo(buf, 0);
    }
    out.characters(buf, 0, i + (paramBoolean ? 1 : 0));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\SAXOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */