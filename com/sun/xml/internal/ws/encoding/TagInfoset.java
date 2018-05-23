package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public final class TagInfoset
{
  @NotNull
  public final String[] ns;
  @NotNull
  public final AttributesImpl atts;
  @Nullable
  public final String prefix;
  @Nullable
  public final String nsUri;
  @NotNull
  public final String localName;
  @Nullable
  private String qname;
  private static final String[] EMPTY_ARRAY = new String[0];
  private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
  
  public TagInfoset(String paramString1, String paramString2, String paramString3, AttributesImpl paramAttributesImpl, String... paramVarArgs)
  {
    nsUri = paramString1;
    prefix = paramString3;
    localName = paramString2;
    atts = paramAttributesImpl;
    ns = paramVarArgs;
  }
  
  public TagInfoset(XMLStreamReader paramXMLStreamReader)
  {
    prefix = paramXMLStreamReader.getPrefix();
    nsUri = paramXMLStreamReader.getNamespaceURI();
    localName = paramXMLStreamReader.getLocalName();
    int i = paramXMLStreamReader.getNamespaceCount();
    if (i > 0)
    {
      ns = new String[i * 2];
      for (j = 0; j < i; j++)
      {
        ns[(j * 2)] = fixNull(paramXMLStreamReader.getNamespacePrefix(j));
        ns[(j * 2 + 1)] = fixNull(paramXMLStreamReader.getNamespaceURI(j));
      }
    }
    else
    {
      ns = EMPTY_ARRAY;
    }
    int j = paramXMLStreamReader.getAttributeCount();
    if (j > 0)
    {
      atts = new AttributesImpl();
      StringBuilder localStringBuilder = new StringBuilder();
      for (int k = 0; k < j; k++)
      {
        localStringBuilder.setLength(0);
        String str1 = paramXMLStreamReader.getAttributePrefix(k);
        String str2 = paramXMLStreamReader.getAttributeLocalName(k);
        String str3;
        if ((str1 != null) && (str1.length() != 0))
        {
          localStringBuilder.append(str1);
          localStringBuilder.append(":");
          localStringBuilder.append(str2);
          str3 = localStringBuilder.toString();
        }
        else
        {
          str3 = str2;
        }
        atts.addAttribute(fixNull(paramXMLStreamReader.getAttributeNamespace(k)), str2, str3, paramXMLStreamReader.getAttributeType(k), paramXMLStreamReader.getAttributeValue(k));
      }
    }
    else
    {
      atts = EMPTY_ATTRIBUTES;
    }
  }
  
  public void writeStart(ContentHandler paramContentHandler)
    throws SAXException
  {
    for (int i = 0; i < ns.length; i += 2) {
      paramContentHandler.startPrefixMapping(fixNull(ns[i]), fixNull(ns[(i + 1)]));
    }
    paramContentHandler.startElement(fixNull(nsUri), localName, getQName(), atts);
  }
  
  public void writeEnd(ContentHandler paramContentHandler)
    throws SAXException
  {
    paramContentHandler.endElement(fixNull(nsUri), localName, getQName());
    for (int i = ns.length - 2; i >= 0; i -= 2) {
      paramContentHandler.endPrefixMapping(fixNull(ns[i]));
    }
  }
  
  public void writeStart(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (prefix == null)
    {
      if (nsUri == null) {
        paramXMLStreamWriter.writeStartElement(localName);
      } else {
        paramXMLStreamWriter.writeStartElement("", localName, nsUri);
      }
    }
    else {
      paramXMLStreamWriter.writeStartElement(prefix, localName, nsUri);
    }
    for (int i = 0; i < ns.length; i += 2) {
      paramXMLStreamWriter.writeNamespace(ns[i], ns[(i + 1)]);
    }
    for (i = 0; i < atts.getLength(); i++)
    {
      String str1 = atts.getURI(i);
      if ((str1 == null) || (str1.length() == 0))
      {
        paramXMLStreamWriter.writeAttribute(atts.getLocalName(i), atts.getValue(i));
      }
      else
      {
        String str2 = atts.getQName(i);
        String str3 = str2.substring(0, str2.indexOf(':'));
        paramXMLStreamWriter.writeAttribute(str3, str1, atts.getLocalName(i), atts.getValue(i));
      }
    }
  }
  
  private String getQName()
  {
    if (qname != null) {
      return qname;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (prefix != null)
    {
      localStringBuilder.append(prefix);
      localStringBuilder.append(':');
      localStringBuilder.append(localName);
      qname = localStringBuilder.toString();
    }
    else
    {
      qname = localName;
    }
    return qname;
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  public String getNamespaceURI(String paramString)
  {
    int i = ns.length / 2;
    for (int j = 0; j < i; j++)
    {
      String str1 = ns[(j * 2)];
      String str2 = ns[(j * 2 + 1)];
      if (paramString.equals(str1)) {
        return str2;
      }
    }
    return null;
  }
  
  public String getPrefix(String paramString)
  {
    int i = ns.length / 2;
    for (int j = 0; j < i; j++)
    {
      String str1 = ns[(j * 2)];
      String str2 = ns[(j * 2 + 1)];
      if (paramString.equals(str2)) {
        return str1;
      }
    }
    return null;
  }
  
  public List<String> allPrefixes(String paramString)
  {
    int i = ns.length / 2;
    ArrayList localArrayList = new ArrayList();
    for (int j = 0; j < i; j++)
    {
      String str1 = ns[(j * 2)];
      String str2 = ns[(j * 2 + 1)];
      if (paramString.equals(str2)) {
        localArrayList.add(str1);
      }
    }
    return localArrayList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\TagInfoset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */