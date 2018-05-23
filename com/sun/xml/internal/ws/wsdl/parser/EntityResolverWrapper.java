package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver.Parser;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class EntityResolverWrapper
  implements XMLEntityResolver
{
  private final EntityResolver core;
  private boolean useStreamFromEntityResolver = false;
  
  public EntityResolverWrapper(EntityResolver paramEntityResolver)
  {
    core = paramEntityResolver;
  }
  
  public EntityResolverWrapper(EntityResolver paramEntityResolver, boolean paramBoolean)
  {
    core = paramEntityResolver;
    useStreamFromEntityResolver = paramBoolean;
  }
  
  public XMLEntityResolver.Parser resolveEntity(String paramString1, String paramString2)
    throws SAXException, IOException
  {
    InputSource localInputSource = core.resolveEntity(paramString1, paramString2);
    if (localInputSource == null) {
      return null;
    }
    if (localInputSource.getSystemId() != null) {
      paramString2 = localInputSource.getSystemId();
    }
    URL localURL = new URL(paramString2);
    InputStream localInputStream;
    if (useStreamFromEntityResolver) {
      localInputStream = localInputSource.getByteStream();
    } else {
      localInputStream = localURL.openStream();
    }
    return new XMLEntityResolver.Parser(localURL, new TidyXMLStreamReader(XMLStreamReaderFactory.create(localURL.toExternalForm(), localInputStream, true), localInputStream));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\EntityResolverWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */