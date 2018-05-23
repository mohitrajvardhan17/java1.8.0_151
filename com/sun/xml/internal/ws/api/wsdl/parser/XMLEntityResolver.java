package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.io.IOException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.SAXException;

public abstract interface XMLEntityResolver
{
  public abstract Parser resolveEntity(String paramString1, String paramString2)
    throws SAXException, IOException, XMLStreamException;
  
  public static final class Parser
  {
    public final URL systemId;
    public final XMLStreamReader parser;
    
    public Parser(URL paramURL, XMLStreamReader paramXMLStreamReader)
    {
      assert (paramXMLStreamReader != null);
      systemId = paramURL;
      parser = paramXMLStreamReader;
    }
    
    public Parser(SDDocumentSource paramSDDocumentSource)
      throws IOException, XMLStreamException
    {
      systemId = paramSDDocumentSource.getSystemId();
      parser = paramSDDocumentSource.read();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\XMLEntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */