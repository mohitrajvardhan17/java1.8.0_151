package com.sun.xml.internal.ws.message.source;

import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

final class SourceUtils
{
  int srcType;
  private static final int domSource = 1;
  private static final int streamSource = 2;
  private static final int saxSource = 4;
  
  public SourceUtils(Source paramSource)
  {
    if ((paramSource instanceof StreamSource)) {
      srcType = 2;
    } else if ((paramSource instanceof DOMSource)) {
      srcType = 1;
    } else if ((paramSource instanceof SAXSource)) {
      srcType = 4;
    }
  }
  
  public boolean isDOMSource()
  {
    return (srcType & 0x1) == 1;
  }
  
  public boolean isStreamSource()
  {
    return (srcType & 0x2) == 2;
  }
  
  public boolean isSaxSource()
  {
    return (srcType & 0x4) == 4;
  }
  
  public QName sniff(Source paramSource)
  {
    return sniff(paramSource, new RootElementSniffer());
  }
  
  public QName sniff(Source paramSource, RootElementSniffer paramRootElementSniffer)
  {
    String str1 = null;
    String str2 = null;
    Object localObject1;
    Object localObject2;
    if (isDOMSource())
    {
      localObject1 = (DOMSource)paramSource;
      localObject2 = ((DOMSource)localObject1).getNode();
      if (((Node)localObject2).getNodeType() == 9) {
        localObject2 = ((Document)localObject2).getDocumentElement();
      }
      str1 = ((Node)localObject2).getLocalName();
      str2 = ((Node)localObject2).getNamespaceURI();
    }
    else if (isSaxSource())
    {
      localObject1 = (SAXSource)paramSource;
      localObject2 = new SAXResult(paramRootElementSniffer);
      try
      {
        Transformer localTransformer = XmlUtil.newTransformer();
        localTransformer.transform((Source)localObject1, (Result)localObject2);
      }
      catch (TransformerConfigurationException localTransformerConfigurationException)
      {
        throw new WebServiceException(localTransformerConfigurationException);
      }
      catch (TransformerException localTransformerException)
      {
        str1 = paramRootElementSniffer.getLocalName();
        str2 = paramRootElementSniffer.getNsUri();
      }
    }
    return new QName(str2, str1);
  }
  
  public static void serializeSource(Source paramSource, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    XMLStreamReader localXMLStreamReader = SourceReaderFactory.createSourceReader(paramSource, true);
    int i;
    do
    {
      i = localXMLStreamReader.next();
      switch (i)
      {
      case 1: 
        String str1 = localXMLStreamReader.getNamespaceURI();
        String str2 = localXMLStreamReader.getPrefix();
        String str3 = localXMLStreamReader.getLocalName();
        if (str2 == null)
        {
          if (str1 == null) {
            paramXMLStreamWriter.writeStartElement(str3);
          } else {
            paramXMLStreamWriter.writeStartElement(str1, str3);
          }
        }
        else if (str2.length() > 0)
        {
          String str4 = null;
          if (paramXMLStreamWriter.getNamespaceContext() != null) {
            str4 = paramXMLStreamWriter.getNamespaceContext().getNamespaceURI(str2);
          }
          String str5 = paramXMLStreamWriter.getPrefix(str1);
          if (declarePrefix(str2, str1, str5, str4))
          {
            paramXMLStreamWriter.writeStartElement(str2, str3, str1);
            paramXMLStreamWriter.setPrefix(str2, str1 != null ? str1 : "");
            paramXMLStreamWriter.writeNamespace(str2, str1);
          }
          else
          {
            paramXMLStreamWriter.writeStartElement(str2, str3, str1);
          }
        }
        else
        {
          paramXMLStreamWriter.writeStartElement(str2, str3, str1);
        }
        int j = localXMLStreamReader.getNamespaceCount();
        String str6;
        String str7;
        for (int k = 0; k < j; k++)
        {
          str6 = localXMLStreamReader.getNamespacePrefix(k);
          if (str6 == null) {
            str6 = "";
          }
          str7 = null;
          if (paramXMLStreamWriter.getNamespaceContext() != null) {
            str7 = paramXMLStreamWriter.getNamespaceContext().getNamespaceURI(str6);
          }
          String str8 = localXMLStreamReader.getNamespaceURI(k);
          if ((str7 == null) || (str6.length() == 0) || (str2.length() == 0) || ((!str6.equals(str2)) && (!str7.equals(str8))))
          {
            paramXMLStreamWriter.setPrefix(str6, str8 != null ? str8 : "");
            paramXMLStreamWriter.writeNamespace(str6, str8 != null ? str8 : "");
          }
        }
        j = localXMLStreamReader.getAttributeCount();
        for (k = 0; k < j; k++)
        {
          str6 = localXMLStreamReader.getAttributePrefix(k);
          str7 = localXMLStreamReader.getAttributeNamespace(k);
          paramXMLStreamWriter.writeAttribute(str6 != null ? str6 : "", str7 != null ? str7 : "", localXMLStreamReader.getAttributeLocalName(k), localXMLStreamReader.getAttributeValue(k));
          setUndeclaredPrefix(str6, str7, paramXMLStreamWriter);
        }
        break;
      case 2: 
        paramXMLStreamWriter.writeEndElement();
        break;
      case 4: 
        paramXMLStreamWriter.writeCharacters(localXMLStreamReader.getText());
      }
    } while (i != 8);
    localXMLStreamReader.close();
  }
  
  private static void setUndeclaredPrefix(String paramString1, String paramString2, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    String str = null;
    if (paramXMLStreamWriter.getNamespaceContext() != null) {
      str = paramXMLStreamWriter.getNamespaceContext().getNamespaceURI(paramString1);
    }
    if (str == null)
    {
      paramXMLStreamWriter.setPrefix(paramString1, paramString2 != null ? paramString2 : "");
      paramXMLStreamWriter.writeNamespace(paramString1, paramString2 != null ? paramString2 : "");
    }
  }
  
  private static boolean declarePrefix(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    return (paramString4 == null) || ((paramString3 != null) && (!paramString1.equals(paramString3))) || ((paramString2 != null) && (!paramString4.equals(paramString2)));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\source\SourceUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */