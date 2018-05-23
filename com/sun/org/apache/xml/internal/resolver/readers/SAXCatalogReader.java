package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.BootstrapResolver;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;

public class SAXCatalogReader
  implements CatalogReader, ContentHandler, DocumentHandler
{
  protected SAXParserFactory parserFactory = null;
  protected String parserClass = null;
  protected Map<String, String> namespaceMap = new HashMap();
  private SAXCatalogParser saxParser = null;
  private boolean abandonHope = false;
  private Catalog catalog;
  protected Debug debug = getStaticManagerdebug;
  
  public void setParserFactory(SAXParserFactory paramSAXParserFactory)
  {
    parserFactory = paramSAXParserFactory;
  }
  
  public void setParserClass(String paramString)
  {
    parserClass = paramString;
  }
  
  public SAXParserFactory getParserFactory()
  {
    return parserFactory;
  }
  
  public String getParserClass()
  {
    return parserClass;
  }
  
  public SAXCatalogReader()
  {
    parserFactory = null;
    parserClass = null;
  }
  
  public SAXCatalogReader(SAXParserFactory paramSAXParserFactory)
  {
    parserFactory = paramSAXParserFactory;
  }
  
  public SAXCatalogReader(String paramString)
  {
    parserClass = paramString;
  }
  
  public void setCatalogParser(String paramString1, String paramString2, String paramString3)
  {
    if (paramString1 == null) {
      namespaceMap.put(paramString2, paramString3);
    } else {
      namespaceMap.put("{" + paramString1 + "}" + paramString2, paramString3);
    }
  }
  
  public String getCatalogParser(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return (String)namespaceMap.get(paramString2);
    }
    return (String)namespaceMap.get("{" + paramString1 + "}" + paramString2);
  }
  
  public void readCatalog(Catalog paramCatalog, String paramString)
    throws MalformedURLException, IOException, CatalogException
  {
    URL localURL = null;
    try
    {
      localURL = new URL(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localURL = new URL("file:///" + paramString);
    }
    debug = getCatalogManagerdebug;
    try
    {
      URLConnection localURLConnection = localURL.openConnection();
      readCatalog(paramCatalog, localURLConnection.getInputStream());
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      getCatalogManagerdebug.message(1, "Failed to load catalog, file not found", localURL.toString());
    }
  }
  
  public void readCatalog(Catalog paramCatalog, InputStream paramInputStream)
    throws IOException, CatalogException
  {
    if ((parserFactory == null) && (parserClass == null))
    {
      debug.message(1, "Cannot read SAX catalog without a parser");
      throw new CatalogException(6);
    }
    debug = getCatalogManagerdebug;
    BootstrapResolver localBootstrapResolver = paramCatalog.getCatalogManager().getBootstrapResolver();
    catalog = paramCatalog;
    try
    {
      Object localObject1;
      if (parserFactory != null)
      {
        localObject1 = parserFactory.newSAXParser();
        localObject2 = new SAXParserHandler();
        ((SAXParserHandler)localObject2).setContentHandler(this);
        if (localBootstrapResolver != null) {
          ((SAXParserHandler)localObject2).setEntityResolver(localBootstrapResolver);
        }
        ((SAXParser)localObject1).parse(new InputSource(paramInputStream), (DefaultHandler)localObject2);
      }
      else
      {
        localObject1 = (Parser)ReflectUtil.forName(parserClass).newInstance();
        ((Parser)localObject1).setDocumentHandler(this);
        if (localBootstrapResolver != null) {
          ((Parser)localObject1).setEntityResolver(localBootstrapResolver);
        }
        ((Parser)localObject1).parse(new InputSource(paramInputStream));
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new CatalogException(6);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new CatalogException(6);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new CatalogException(6);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new CatalogException(5);
    }
    catch (SAXException localSAXException)
    {
      Object localObject2 = localSAXException.getException();
      UnknownHostException localUnknownHostException = new UnknownHostException();
      FileNotFoundException localFileNotFoundException = new FileNotFoundException();
      if (localObject2 != null)
      {
        if (localObject2.getClass() == localUnknownHostException.getClass()) {
          throw new CatalogException(7, ((Exception)localObject2).toString());
        }
        if (localObject2.getClass() == localFileNotFoundException.getClass()) {
          throw new CatalogException(7, ((Exception)localObject2).toString());
        }
      }
      throw new CatalogException(localSAXException);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    if (saxParser != null) {
      saxParser.setDocumentLocator(paramLocator);
    }
  }
  
  public void startDocument()
    throws SAXException
  {
    saxParser = null;
    abandonHope = false;
  }
  
  public void endDocument()
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.endDocument();
    }
  }
  
  public void startElement(String paramString, AttributeList paramAttributeList)
    throws SAXException
  {
    if (abandonHope) {
      return;
    }
    if (saxParser == null)
    {
      String str1 = "";
      if (paramString.indexOf(':') > 0) {
        str1 = paramString.substring(0, paramString.indexOf(':'));
      }
      String str2 = paramString;
      if (str2.indexOf(':') > 0) {
        str2 = str2.substring(str2.indexOf(':') + 1);
      }
      String str3 = null;
      if (str1.equals("")) {
        str3 = paramAttributeList.getValue("xmlns");
      } else {
        str3 = paramAttributeList.getValue("xmlns:" + str1);
      }
      String str4 = getCatalogParser(str3, str2);
      if (str4 == null)
      {
        abandonHope = true;
        if (str3 == null) {
          debug.message(2, "No Catalog parser for " + paramString);
        } else {
          debug.message(2, "No Catalog parser for {" + str3 + "}" + paramString);
        }
        return;
      }
      try
      {
        saxParser = ((SAXCatalogParser)ReflectUtil.forName(str4).newInstance());
        saxParser.setCatalog(catalog);
        saxParser.startDocument();
        saxParser.startElement(paramString, paramAttributeList);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localClassNotFoundException.toString());
      }
      catch (InstantiationException localInstantiationException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localInstantiationException.toString());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localIllegalAccessException.toString());
      }
      catch (ClassCastException localClassCastException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localClassCastException.toString());
      }
    }
    else
    {
      saxParser.startElement(paramString, paramAttributeList);
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (abandonHope) {
      return;
    }
    if (saxParser == null)
    {
      String str = getCatalogParser(paramString1, paramString2);
      if (str == null)
      {
        abandonHope = true;
        if (paramString1 == null) {
          debug.message(2, "No Catalog parser for " + paramString2);
        } else {
          debug.message(2, "No Catalog parser for {" + paramString1 + "}" + paramString2);
        }
        return;
      }
      try
      {
        saxParser = ((SAXCatalogParser)ReflectUtil.forName(str).newInstance());
        saxParser.setCatalog(catalog);
        saxParser.startDocument();
        saxParser.startElement(paramString1, paramString2, paramString3, paramAttributes);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localClassNotFoundException.toString());
      }
      catch (InstantiationException localInstantiationException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localInstantiationException.toString());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localIllegalAccessException.toString());
      }
      catch (ClassCastException localClassCastException)
      {
        saxParser = null;
        abandonHope = true;
        debug.message(2, localClassCastException.toString());
      }
    }
    else
    {
      saxParser.startElement(paramString1, paramString2, paramString3, paramAttributes);
    }
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.endElement(paramString);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.endElement(paramString1, paramString2, paramString3);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.processingInstruction(paramString1, paramString2);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.startPrefixMapping(paramString1, paramString2);
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.endPrefixMapping(paramString);
    }
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    if (saxParser != null) {
      saxParser.skippedEntity(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\SAXCatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */