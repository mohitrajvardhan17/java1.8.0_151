package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class XCatalogReader
  extends SAXCatalogReader
  implements SAXCatalogParser
{
  protected Catalog catalog = null;
  
  public void setCatalog(Catalog paramCatalog)
  {
    catalog = paramCatalog;
  }
  
  public Catalog getCatalog()
  {
    return catalog;
  }
  
  public XCatalogReader(SAXParserFactory paramSAXParserFactory)
  {
    super(paramSAXParserFactory);
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void startDocument()
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    int i = -1;
    Vector localVector = new Vector();
    if (paramString2.equals("Base"))
    {
      i = Catalog.BASE;
      localVector.add(paramAttributes.getValue("HRef"));
      catalog.getCatalogManager().debug.message(4, "Base", paramAttributes.getValue("HRef"));
    }
    else if (paramString2.equals("Delegate"))
    {
      i = Catalog.DELEGATE_PUBLIC;
      localVector.add(paramAttributes.getValue("PublicId"));
      localVector.add(paramAttributes.getValue("HRef"));
      catalog.getCatalogManager().debug.message(4, "Delegate", PublicId.normalize(paramAttributes.getValue("PublicId")), paramAttributes.getValue("HRef"));
    }
    else if (paramString2.equals("Extend"))
    {
      i = Catalog.CATALOG;
      localVector.add(paramAttributes.getValue("HRef"));
      catalog.getCatalogManager().debug.message(4, "Extend", paramAttributes.getValue("HRef"));
    }
    else if (paramString2.equals("Map"))
    {
      i = Catalog.PUBLIC;
      localVector.add(paramAttributes.getValue("PublicId"));
      localVector.add(paramAttributes.getValue("HRef"));
      catalog.getCatalogManager().debug.message(4, "Map", PublicId.normalize(paramAttributes.getValue("PublicId")), paramAttributes.getValue("HRef"));
    }
    else if (paramString2.equals("Remap"))
    {
      i = Catalog.SYSTEM;
      localVector.add(paramAttributes.getValue("SystemId"));
      localVector.add(paramAttributes.getValue("HRef"));
      catalog.getCatalogManager().debug.message(4, "Remap", paramAttributes.getValue("SystemId"), paramAttributes.getValue("HRef"));
    }
    else if (!paramString2.equals("XMLCatalog"))
    {
      catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", paramString2);
    }
    if (i >= 0) {
      try
      {
        CatalogEntry localCatalogEntry = new CatalogEntry(i, localVector);
        catalog.addEntry(localCatalogEntry);
      }
      catch (CatalogException localCatalogException)
      {
        if (localCatalogException.getExceptionType() == 3) {
          catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", paramString2);
        } else if (localCatalogException.getExceptionType() == 2) {
          catalog.getCatalogManager().debug.message(1, "Invalid catalog entry", paramString2);
        }
      }
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\XCatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */