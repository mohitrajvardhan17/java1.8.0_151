package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Vector;

public class TR9401CatalogReader
  extends TextCatalogReader
{
  public TR9401CatalogReader() {}
  
  public void readCatalog(Catalog paramCatalog, InputStream paramInputStream)
    throws MalformedURLException, IOException
  {
    catfile = paramInputStream;
    if (catfile == null) {
      return;
    }
    Vector localVector1 = null;
    try
    {
      for (;;)
      {
        String str1 = nextToken();
        if (str1 == null)
        {
          if (localVector1 != null)
          {
            paramCatalog.unknownEntry(localVector1);
            localVector1 = null;
          }
          catfile.close();
          catfile = null;
          return;
        }
        String str2 = null;
        if (caseSensitive) {
          str2 = str1;
        } else {
          str2 = str1.toUpperCase();
        }
        if (str2.equals("DELEGATE")) {
          str2 = "DELEGATE_PUBLIC";
        }
        try
        {
          int i = CatalogEntry.getEntryType(str2);
          int j = CatalogEntry.getEntryArgCount(i);
          Vector localVector2 = new Vector();
          if (localVector1 != null)
          {
            paramCatalog.unknownEntry(localVector1);
            localVector1 = null;
          }
          for (int k = 0; k < j; k++) {
            localVector2.addElement(nextToken());
          }
          paramCatalog.addEntry(new CatalogEntry(str2, localVector2));
        }
        catch (CatalogException localCatalogException2)
        {
          if (localCatalogException2.getExceptionType() == 3)
          {
            if (localVector1 == null) {
              localVector1 = new Vector();
            }
            localVector1.addElement(str1);
          }
          else if (localCatalogException2.getExceptionType() == 2)
          {
            getCatalogManagerdebug.message(1, "Invalid catalog entry", str1);
            localVector1 = null;
          }
          else if (localCatalogException2.getExceptionType() == 8)
          {
            getCatalogManagerdebug.message(1, localCatalogException2.getMessage());
          }
        }
      }
      return;
    }
    catch (CatalogException localCatalogException1)
    {
      if (localCatalogException1.getExceptionType() == 8) {
        getCatalogManagerdebug.message(1, localCatalogException1.getMessage());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\TR9401CatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */