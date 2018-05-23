package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Stack;
import java.util.Vector;

public class TextCatalogReader
  implements CatalogReader
{
  protected InputStream catfile = null;
  protected int[] stack = new int[3];
  protected Stack tokenStack = new Stack();
  protected int top = -1;
  protected boolean caseSensitive = false;
  
  public TextCatalogReader() {}
  
  public void setCaseSensitive(boolean paramBoolean)
  {
    caseSensitive = paramBoolean;
  }
  
  public boolean getCaseSensitive()
  {
    return caseSensitive;
  }
  
  public void readCatalog(Catalog paramCatalog, String paramString)
    throws MalformedURLException, IOException
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
    URLConnection localURLConnection = localURL.openConnection();
    try
    {
      readCatalog(paramCatalog, localURLConnection.getInputStream());
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      getCatalogManagerdebug.message(1, "Failed to load catalog, file not found", localURL.toString());
    }
  }
  
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
  
  protected void finalize()
  {
    if (catfile != null) {
      try
      {
        catfile.close();
      }
      catch (IOException localIOException) {}
    }
    catfile = null;
  }
  
  protected String nextToken()
    throws IOException, CatalogException
  {
    String str1 = "";
    if (!tokenStack.empty()) {
      return (String)tokenStack.pop();
    }
    int j;
    do
    {
      i = catfile.read();
      while (i <= 32)
      {
        i = catfile.read();
        if (i < 0) {
          return null;
        }
      }
      j = catfile.read();
      if (j < 0) {
        return null;
      }
      if ((i != 45) || (j != 45)) {
        break;
      }
      i = 32;
      for (j = nextChar(); ((i != 45) || (j != 45)) && (j > 0); j = nextChar()) {
        i = j;
      }
    } while (j >= 0);
    throw new CatalogException(8, "Unterminated comment in catalog file; EOF treated as end-of-comment.");
    stack[(++top)] = j;
    stack[(++top)] = i;
    int i = nextChar();
    Object localObject;
    if ((i == 34) || (i == 39))
    {
      int k = i;
      while ((i = nextChar()) != k)
      {
        localObject = new char[1];
        localObject[0] = ((char)i);
        String str2 = new String((char[])localObject);
        str1 = str1.concat(str2);
      }
      return str1;
    }
    while (i > 32)
    {
      j = nextChar();
      if ((i == 45) && (j == 45))
      {
        stack[(++top)] = i;
        stack[(++top)] = j;
        return str1;
      }
      char[] arrayOfChar = new char[1];
      arrayOfChar[0] = ((char)i);
      localObject = new String(arrayOfChar);
      str1 = str1.concat((String)localObject);
      i = j;
    }
    return str1;
  }
  
  protected int nextChar()
    throws IOException
  {
    if (top < 0) {
      return catfile.read();
    }
    return stack[(top--)];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\readers\TextCatalogReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */