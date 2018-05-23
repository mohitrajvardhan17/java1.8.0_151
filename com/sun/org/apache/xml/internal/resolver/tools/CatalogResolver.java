package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CatalogResolver
  implements EntityResolver, URIResolver
{
  public boolean namespaceAware = true;
  public boolean validating = false;
  private Catalog catalog = null;
  private CatalogManager catalogManager = CatalogManager.getStaticManager();
  
  public CatalogResolver()
  {
    initializeCatalogs(false);
  }
  
  public CatalogResolver(boolean paramBoolean)
  {
    initializeCatalogs(paramBoolean);
  }
  
  public CatalogResolver(CatalogManager paramCatalogManager)
  {
    catalogManager = paramCatalogManager;
    initializeCatalogs(!catalogManager.getUseStaticCatalog());
  }
  
  private void initializeCatalogs(boolean paramBoolean)
  {
    catalog = catalogManager.getCatalog();
  }
  
  public Catalog getCatalog()
  {
    return catalog;
  }
  
  public String getResolvedEntity(String paramString1, String paramString2)
  {
    String str = null;
    if (catalog == null)
    {
      catalogManager.debug.message(1, "Catalog resolution attempted with null catalog; ignored");
      return null;
    }
    if (paramString2 != null) {
      try
      {
        str = catalog.resolveSystem(paramString2);
      }
      catch (MalformedURLException localMalformedURLException1)
      {
        catalogManager.debug.message(1, "Malformed URL exception trying to resolve", paramString1);
        str = null;
      }
      catch (IOException localIOException1)
      {
        catalogManager.debug.message(1, "I/O exception trying to resolve", paramString1);
        str = null;
      }
    }
    if (str == null)
    {
      if (paramString1 != null) {
        try
        {
          str = catalog.resolvePublic(paramString1, paramString2);
        }
        catch (MalformedURLException localMalformedURLException2)
        {
          catalogManager.debug.message(1, "Malformed URL exception trying to resolve", paramString1);
        }
        catch (IOException localIOException2)
        {
          catalogManager.debug.message(1, "I/O exception trying to resolve", paramString1);
        }
      }
      if (str != null) {
        catalogManager.debug.message(2, "Resolved public", paramString1, str);
      }
    }
    else
    {
      catalogManager.debug.message(2, "Resolved system", paramString2, str);
    }
    return str;
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
  {
    String str = getResolvedEntity(paramString1, paramString2);
    if (str != null) {
      try
      {
        InputSource localInputSource = new InputSource(str);
        localInputSource.setPublicId(paramString1);
        URL localURL = new URL(str);
        InputStream localInputStream = localURL.openStream();
        localInputSource.setByteStream(localInputStream);
        return localInputSource;
      }
      catch (Exception localException)
      {
        catalogManager.debug.message(1, "Failed to create InputSource", str);
        return null;
      }
    }
    return null;
  }
  
  public Source resolve(String paramString1, String paramString2)
    throws TransformerException
  {
    String str1 = paramString1;
    String str2 = null;
    int i = paramString1.indexOf("#");
    if (i >= 0)
    {
      str1 = paramString1.substring(0, i);
      str2 = paramString1.substring(i + 1);
    }
    String str3 = null;
    try
    {
      str3 = catalog.resolveURI(paramString1);
    }
    catch (Exception localException) {}
    if (str3 == null) {
      try
      {
        URL localURL = null;
        if (paramString2 == null)
        {
          localURL = new URL(str1);
          str3 = localURL.toString();
        }
        else
        {
          localObject = new URL(paramString2);
          localURL = paramString1.length() == 0 ? localObject : new URL((URL)localObject, str1);
          str3 = localURL.toString();
        }
      }
      catch (MalformedURLException localMalformedURLException)
      {
        Object localObject = makeAbsolute(paramString2);
        if (!((String)localObject).equals(paramString2)) {
          return resolve(paramString1, (String)localObject);
        }
        throw new TransformerException("Malformed URL " + paramString1 + "(base " + paramString2 + ")", localMalformedURLException);
      }
    }
    catalogManager.debug.message(2, "Resolved URI", paramString1, str3);
    SAXSource localSAXSource = new SAXSource();
    localSAXSource.setInputSource(new InputSource(str3));
    setEntityResolver(localSAXSource);
    return localSAXSource;
  }
  
  private void setEntityResolver(SAXSource paramSAXSource)
    throws TransformerException
  {
    XMLReader localXMLReader = paramSAXSource.getXMLReader();
    if (localXMLReader == null)
    {
      SAXParserFactoryImpl localSAXParserFactoryImpl = catalogManager.useServicesMechanism() ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
      localSAXParserFactoryImpl.setNamespaceAware(true);
      try
      {
        localXMLReader = localSAXParserFactoryImpl.newSAXParser().getXMLReader();
      }
      catch (ParserConfigurationException localParserConfigurationException)
      {
        throw new TransformerException(localParserConfigurationException);
      }
      catch (SAXException localSAXException)
      {
        throw new TransformerException(localSAXException);
      }
    }
    localXMLReader.setEntityResolver(this);
    paramSAXSource.setXMLReader(localXMLReader);
  }
  
  private String makeAbsolute(String paramString)
  {
    if (paramString == null) {
      paramString = "";
    }
    try
    {
      URL localURL1 = new URL(paramString);
      return localURL1.toString();
    }
    catch (MalformedURLException localMalformedURLException1)
    {
      try
      {
        URL localURL2 = FileURL.makeURL(paramString);
        return localURL2.toString();
      }
      catch (MalformedURLException localMalformedURLException2) {}
    }
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\tools\CatalogResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */