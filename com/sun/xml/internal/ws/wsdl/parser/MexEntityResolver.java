package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver.Parser;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public final class MexEntityResolver
  implements XMLEntityResolver
{
  private final Map<String, SDDocumentSource> wsdls = new HashMap();
  
  public MexEntityResolver(List<? extends Source> paramList)
    throws IOException
  {
    Transformer localTransformer = XmlUtil.newTransformer();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Source localSource = (Source)localIterator.next();
      XMLStreamBufferResult localXMLStreamBufferResult = new XMLStreamBufferResult();
      try
      {
        localTransformer.transform(localSource, localXMLStreamBufferResult);
      }
      catch (TransformerException localTransformerException)
      {
        throw new WebServiceException(localTransformerException);
      }
      String str = localSource.getSystemId();
      if (str != null)
      {
        SDDocumentSource localSDDocumentSource = SDDocumentSource.create(JAXWSUtils.getFileOrURL(str), localXMLStreamBufferResult.getXMLStreamBuffer());
        wsdls.put(str, localSDDocumentSource);
      }
    }
  }
  
  public XMLEntityResolver.Parser resolveEntity(String paramString1, String paramString2)
    throws SAXException, IOException, XMLStreamException
  {
    if (paramString2 != null)
    {
      SDDocumentSource localSDDocumentSource = (SDDocumentSource)wsdls.get(paramString2);
      if (localSDDocumentSource != null) {
        return new XMLEntityResolver.Parser(localSDDocumentSource);
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\MexEntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */