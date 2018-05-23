package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.AttributeList;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

/**
 * @deprecated
 */
public class ResolvingParser
  implements Parser, DTDHandler, DocumentHandler, EntityResolver
{
  public static boolean namespaceAware = true;
  public static boolean validating = false;
  public static boolean suppressExplanation = false;
  private SAXParser saxParser = null;
  private Parser parser = null;
  private DocumentHandler documentHandler = null;
  private DTDHandler dtdHandler = null;
  private CatalogManager catalogManager = CatalogManager.getStaticManager();
  private CatalogResolver catalogResolver = null;
  private CatalogResolver piCatalogResolver = null;
  private boolean allowXMLCatalogPI = false;
  private boolean oasisXMLCatalogPI = false;
  private URL baseURL = null;
  
  public ResolvingParser()
  {
    initParser();
  }
  
  public ResolvingParser(CatalogManager paramCatalogManager)
  {
    catalogManager = paramCatalogManager;
    initParser();
  }
  
  private void initParser()
  {
    catalogResolver = new CatalogResolver(catalogManager);
    SAXParserFactoryImpl localSAXParserFactoryImpl = catalogManager.useServicesMechanism() ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
    localSAXParserFactoryImpl.setNamespaceAware(namespaceAware);
    localSAXParserFactoryImpl.setValidating(validating);
    try
    {
      saxParser = localSAXParserFactoryImpl.newSAXParser();
      parser = saxParser.getParser();
      documentHandler = null;
      dtdHandler = null;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public Catalog getCatalog()
  {
    return catalogResolver.getCatalog();
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    setupParse(paramInputSource.getSystemId());
    try
    {
      parser.parse(paramInputSource);
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
    setupParse(paramString);
    try
    {
      parser.parse(paramString);
    }
    catch (InternalError localInternalError)
    {
      explain(paramString);
      throw localInternalError;
    }
  }
  
  public void setDocumentHandler(DocumentHandler paramDocumentHandler)
  {
    documentHandler = paramDocumentHandler;
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
  {
    dtdHandler = paramDTDHandler;
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) {}
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    parser.setErrorHandler(paramErrorHandler);
  }
  
  public void setLocale(Locale paramLocale)
    throws SAXException
  {
    parser.setLocale(paramLocale);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (documentHandler != null) {
      documentHandler.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    if (documentHandler != null) {
      documentHandler.endDocument();
    }
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    if (documentHandler != null) {
      documentHandler.endElement(paramString);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (documentHandler != null) {
      documentHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
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
    else if (documentHandler != null)
    {
      documentHandler.processingInstruction(paramString1, paramString2);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    if (documentHandler != null) {
      documentHandler.setDocumentLocator(paramLocator);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    if (documentHandler != null) {
      documentHandler.startDocument();
    }
  }
  
  public void startElement(String paramString, AttributeList paramAttributeList)
    throws SAXException
  {
    allowXMLCatalogPI = false;
    if (documentHandler != null) {
      documentHandler.startElement(paramString, paramAttributeList);
    }
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    allowXMLCatalogPI = false;
    if (dtdHandler != null) {
      dtdHandler.notationDecl(paramString1, paramString2, paramString3);
    }
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    allowXMLCatalogPI = false;
    if (dtdHandler != null) {
      dtdHandler.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4);
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
  
  private void setupParse(String paramString)
  {
    allowXMLCatalogPI = true;
    parser.setEntityResolver(this);
    parser.setDocumentHandler(this);
    parser.setDTDHandler(this);
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
      System.out.println("Parser probably encountered bad URI in " + paramString);
      System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\tools\ResolvingParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */