package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EntityResolverWrapper
  implements XMLEntityResolver
{
  protected EntityResolver fEntityResolver;
  
  public EntityResolverWrapper() {}
  
  public EntityResolverWrapper(EntityResolver paramEntityResolver)
  {
    setEntityResolver(paramEntityResolver);
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    fEntityResolver = paramEntityResolver;
  }
  
  public EntityResolver getEntityResolver()
  {
    return fEntityResolver;
  }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier)
    throws XNIException, IOException
  {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getExpandedSystemId();
    if ((str1 == null) && (str2 == null)) {
      return null;
    }
    if ((fEntityResolver != null) && (paramXMLResourceIdentifier != null)) {
      try
      {
        InputSource localInputSource = fEntityResolver.resolveEntity(str1, str2);
        if (localInputSource != null)
        {
          localObject = localInputSource.getPublicId();
          String str3 = localInputSource.getSystemId();
          String str4 = paramXMLResourceIdentifier.getBaseSystemId();
          InputStream localInputStream = localInputSource.getByteStream();
          Reader localReader = localInputSource.getCharacterStream();
          String str5 = localInputSource.getEncoding();
          XMLInputSource localXMLInputSource = new XMLInputSource((String)localObject, str3, str4);
          localXMLInputSource.setByteStream(localInputStream);
          localXMLInputSource.setCharacterStream(localReader);
          localXMLInputSource.setEncoding(str5);
          return localXMLInputSource;
        }
      }
      catch (SAXException localSAXException)
      {
        Object localObject = localSAXException.getException();
        if (localObject == null) {
          localObject = localSAXException;
        }
        throw new XNIException((Exception)localObject);
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\EntityResolverWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */