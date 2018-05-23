package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class SDDocumentSource
{
  public SDDocumentSource() {}
  
  public abstract XMLStreamReader read(XMLInputFactory paramXMLInputFactory)
    throws IOException, XMLStreamException;
  
  public abstract XMLStreamReader read()
    throws IOException, XMLStreamException;
  
  public abstract URL getSystemId();
  
  public static SDDocumentSource create(URL paramURL)
  {
    new SDDocumentSource()
    {
      private final URL systemId = val$url;
      
      public XMLStreamReader read(XMLInputFactory paramAnonymousXMLInputFactory)
        throws IOException, XMLStreamException
      {
        InputStream localInputStream = val$url.openStream();
        return new TidyXMLStreamReader(paramAnonymousXMLInputFactory.createXMLStreamReader(systemId.toExternalForm(), localInputStream), localInputStream);
      }
      
      public XMLStreamReader read()
        throws IOException, XMLStreamException
      {
        InputStream localInputStream = val$url.openStream();
        return new TidyXMLStreamReader(XMLStreamReaderFactory.create(systemId.toExternalForm(), localInputStream, false), localInputStream);
      }
      
      public URL getSystemId()
      {
        return systemId;
      }
    };
  }
  
  public static SDDocumentSource create(final URL paramURL, XMLStreamBuffer paramXMLStreamBuffer)
  {
    new SDDocumentSource()
    {
      public XMLStreamReader read(XMLInputFactory paramAnonymousXMLInputFactory)
        throws XMLStreamException
      {
        return val$xsb.readAsXMLStreamReader();
      }
      
      public XMLStreamReader read()
        throws XMLStreamException
      {
        return val$xsb.readAsXMLStreamReader();
      }
      
      public URL getSystemId()
      {
        return paramURL;
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\SDDocumentSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */