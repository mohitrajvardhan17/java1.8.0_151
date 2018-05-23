package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.Resolver;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ExtendedXMLCatalogReader
  extends OASISXMLCatalogReader
{
  public static final String extendedNamespaceName = "http://nwalsh.com/xcatalog/1.0";
  
  public ExtendedXMLCatalogReader() {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    boolean bool = inExtensionNamespace();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    int i = -1;
    Vector localVector = new Vector();
    if ((paramString1 != null) && ("http://nwalsh.com/xcatalog/1.0".equals(paramString1)) && (!bool))
    {
      Object localObject;
      if (paramAttributes.getValue("xml:base") != null)
      {
        localObject = paramAttributes.getValue("xml:base");
        i = Catalog.BASE;
        localVector.add(localObject);
        baseURIStack.push(localObject);
        debug.message(4, "xml:base", (String)localObject);
        try
        {
          CatalogEntry localCatalogEntry = new CatalogEntry(i, localVector);
          catalog.addEntry(localCatalogEntry);
        }
        catch (CatalogException localCatalogException2)
        {
          if (localCatalogException2.getExceptionType() == 3) {
            debug.message(1, "Invalid catalog entry type", paramString2);
          } else if (localCatalogException2.getExceptionType() == 2) {
            debug.message(1, "Invalid catalog entry (base)", paramString2);
          }
        }
        i = -1;
        localVector = new Vector();
      }
      else
      {
        baseURIStack.push(baseURIStack.peek());
      }
      if (paramString2.equals("uriSuffix"))
      {
        if (checkAttributes(paramAttributes, "suffix", "uri"))
        {
          i = Resolver.URISUFFIX;
          localVector.add(paramAttributes.getValue("suffix"));
          localVector.add(paramAttributes.getValue("uri"));
          debug.message(4, "uriSuffix", paramAttributes.getValue("suffix"), paramAttributes.getValue("uri"));
        }
      }
      else if (paramString2.equals("systemSuffix"))
      {
        if (checkAttributes(paramAttributes, "suffix", "uri"))
        {
          i = Resolver.SYSTEMSUFFIX;
          localVector.add(paramAttributes.getValue("suffix"));
          localVector.add(paramAttributes.getValue("uri"));
          debug.message(4, "systemSuffix", paramAttributes.getValue("suffix"), paramAttributes.getValue("uri"));
        }
      }
      else {
        debug.message(1, "Invalid catalog entry type", paramString2);
      }
      if (i >= 0) {
        try
        {
          localObject = new CatalogEntry(i, localVector);
          catalog.addEntry((CatalogEntry)localObject);
        }
        catch (CatalogException localCatalogException1)
        {
          if (localCatalogException1.getExceptionType() == 3) {
            debug.message(1, "Invalid catalog entry type", paramString2);
          } else if (localCatalogException1.getExceptionType() == 2) {
            debug.message(1, "Invalid catalog entry", paramString2);
          }
        }
      }
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    super.endElement(paramString1, paramString2, paramString3);
    boolean bool = inExtensionNamespace();
    int i = -1;
    Vector localVector = new Vector();
    if ((paramString1 != null) && ("http://nwalsh.com/xcatalog/1.0".equals(paramString1)) && (!bool))
    {
      String str1 = (String)baseURIStack.pop();
      String str2 = (String)baseURIStack.peek();
      if (!str2.equals(str1))
      {
        i = Catalog.BASE;
        localVector.add(str2);
        debug.message(4, "(reset) xml:base", str2);
        try
        {
          CatalogEntry localCatalogEntry = new CatalogEntry(i, localVector);
          catalog.addEntry(localCatalogEntry);
        }
        catch (CatalogException localCatalogException)
        {
          if (localCatalogException.getExceptionType() == 3) {
            debug.message(1, "Invalid catalog entry type", paramString2);
          } else if (localCatalogException.getExceptionType() == 2) {
            debug.message(1, "Invalid catalog entry (rbase)", paramString2);
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\ExtendedXMLCatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */