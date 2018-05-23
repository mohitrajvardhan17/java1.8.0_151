package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class ResolvingXMLFilter
  extends XMLFilterImpl
{
  public static boolean suppressExplanation = false;
  CatalogManager catalogManager = CatalogManager.getStaticManager();
  private CatalogResolver catalogResolver = null;
  private CatalogResolver piCatalogResolver = null;
  private boolean allowXMLCatalogPI = false;
  private boolean oasisXMLCatalogPI = false;
  private URL baseURL = null;
  
  public ResolvingXMLFilter()
  {
    catalogResolver = new CatalogResolver(catalogManager);
  }
  
  public ResolvingXMLFilter(XMLReader paramXMLReader)
  {
    super(paramXMLReader);
    catalogResolver = new CatalogResolver(catalogManager);
  }
  
  public ResolvingXMLFilter(CatalogManager paramCatalogManager)
  {
    catalogManager = paramCatalogManager;
    catalogResolver = new CatalogResolver(catalogManager);
  }
  
  public ResolvingXMLFilter(XMLReader paramXMLReader, CatalogManager paramCatalogManager)
  {
    super(paramXMLReader);
    catalogManager = paramCatalogManager;
    catalogResolver = new CatalogResolver(catalogManager);
  }
  
  public Catalog getCatalog()
  {
    return catalogResolver.getCatalog();
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    allowXMLCatalogPI = true;
    setupBaseURI(paramInputSource.getSystemId());
    try
    {
      super.parse(paramInputSource);
    }
    catch (InternalError localInternalError)
    {
      explain(paramInputSource.getSystemId());
      throw localInternalError;
    }
  }
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    allowXMLCatalogPI = true;
    setupBaseURI(paramString);
    try
    {
      super.parse(paramString);
    }
    catch (InternalError localInternalError)
    {
      explain(paramString);
      throw localInternalError;
    }
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
  {
    allowXMLCatalogPI = false;
    String str = catalogResolver.getResolvedEntity(paramString1, paramString2);
    if ((str == null) && (piCatalogResolver != null)) {
      str = piCatalogResolver.getResolvedEntity(paramString1, paramString2);
    }
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
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    allowXMLCatalogPI = false;
    super.notationDecl(paramString1, paramString2, paramString3);
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    allowXMLCatalogPI = false;
    super.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    allowXMLCatalogPI = false;
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (paramString1.equals("oasis-xml-catalog"))
    {
      URL localURL = null;
      String str1 = paramString2;
      int i = str1.indexOf("catalog=");
      if (i >= 0)
      {
        str1 = str1.substring(i + 8);
        if (str1.length() > 1)
        {
          String str2 = str1.substring(0, 1);
          str1 = str1.substring(1);
          i = str1.indexOf(str2);
          if (i >= 0)
          {
            str1 = str1.substring(0, i);
            try
            {
              if (baseURL != null) {
                localURL = new URL(baseURL, str1);
              } else {
                localURL = new URL(str1);
              }
            }
            catch (MalformedURLException localMalformedURLException) {}
          }
        }
      }
      if (allowXMLCatalogPI)
      {
        if (catalogManager.getAllowOasisXMLCatalogPI())
        {
          catalogManager.debug.message(4, "oasis-xml-catalog PI", paramString2);
          if (localURL != null) {
            try
            {
              catalogManager.debug.message(4, "oasis-xml-catalog", localURL.toString());
              oasisXMLCatalogPI = true;
              if (piCatalogResolver == null) {
                piCatalogResolver = new CatalogResolver(true);
              }
              piCatalogResolver.getCatalog().parseCatalog(localURL.toString());
            }
            catch (Exception localException)
            {
              catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: " + localURL.toString());
            }
          } else {
            catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + paramString2);
          }
        }
        else
        {
          catalogManager.debug.message(4, "PI oasis-xml-catalog ignored: " + paramString2);
        }
      }
      else {
        catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: " + paramString2);
      }
    }
    else
    {
      super.processingInstruction(paramString1, paramString2);
    }
  }
  
  private void setupBaseURI(String paramString)
  {
    URL localURL = null;
    try
    {
      localURL = FileURL.makeURL("basename");
    }
    catch (MalformedURLException localMalformedURLException1)
    {
      localURL = null;
    }
    try
    {
      baseURL = new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException2)
    {
      if (localURL != null) {
        try
        {
          baseURL = new URL(localURL, paramString);
        }
        catch (MalformedURLException localMalformedURLException3)
        {
          baseURL = null;
        }
      } else {
        baseURL = null;
      }
    }
  }
  
  private void explain(String paramString)
  {
    if (!suppressExplanation)
    {
      System.out.println("XMLReader probably encountered bad URI in " + paramString);
      System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
    }
    suppressExplanation = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\tools\ResolvingXMLFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */