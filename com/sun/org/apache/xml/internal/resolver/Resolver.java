package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;

public class Resolver
  extends Catalog
{
  public static final int URISUFFIX = CatalogEntry.addEntryType("URISUFFIX", 2);
  public static final int SYSTEMSUFFIX = CatalogEntry.addEntryType("SYSTEMSUFFIX", 2);
  public static final int RESOLVER = CatalogEntry.addEntryType("RESOLVER", 1);
  public static final int SYSTEMREVERSE = CatalogEntry.addEntryType("SYSTEMREVERSE", 1);
  
  public Resolver() {}
  
  public void setupReaders()
  {
    SAXParserFactoryImpl localSAXParserFactoryImpl = catalogManager.useServicesMechanism() ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
    localSAXParserFactoryImpl.setNamespaceAware(true);
    localSAXParserFactoryImpl.setValidating(false);
    SAXCatalogReader localSAXCatalogReader = new SAXCatalogReader(localSAXParserFactoryImpl);
    localSAXCatalogReader.setCatalogParser(null, "XMLCatalog", "com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader");
    localSAXCatalogReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader");
    addReader("application/xml", localSAXCatalogReader);
    TR9401CatalogReader localTR9401CatalogReader = new TR9401CatalogReader();
    addReader("text/plain", localTR9401CatalogReader);
  }
  
  public void addEntry(CatalogEntry paramCatalogEntry)
  {
    int i = paramCatalogEntry.getEntryType();
    String str1;
    String str2;
    if (i == URISUFFIX)
    {
      str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str2);
      catalogManager.debug.message(4, "URISUFFIX", str1, str2);
    }
    else if (i == SYSTEMSUFFIX)
    {
      str1 = normalizeURI(paramCatalogEntry.getEntryArg(0));
      str2 = makeAbsolute(normalizeURI(paramCatalogEntry.getEntryArg(1)));
      paramCatalogEntry.setEntryArg(1, str2);
      catalogManager.debug.message(4, "SYSTEMSUFFIX", str1, str2);
    }
    super.addEntry(paramCatalogEntry);
  }
  
  public String resolveURI(String paramString)
    throws MalformedURLException, IOException
  {
    String str1 = super.resolveURI(paramString);
    if (str1 != null) {
      return str1;
    }
    Enumeration localEnumeration = catalogEntries.elements();
    while (localEnumeration.hasMoreElements())
    {
      CatalogEntry localCatalogEntry = (CatalogEntry)localEnumeration.nextElement();
      if (localCatalogEntry.getEntryType() == RESOLVER)
      {
        str1 = resolveExternalSystem(paramString, localCatalogEntry.getEntryArg(0));
        if (str1 != null) {
          return str1;
        }
      }
      else if (localCatalogEntry.getEntryType() == URISUFFIX)
      {
        String str2 = localCatalogEntry.getEntryArg(0);
        String str3 = localCatalogEntry.getEntryArg(1);
        if ((str2.length() <= paramString.length()) && (paramString.substring(paramString.length() - str2.length()).equals(str2))) {
          return str3;
        }
      }
    }
    return resolveSubordinateCatalogs(Catalog.URI, null, null, paramString);
  }
  
  public String resolveSystem(String paramString)
    throws MalformedURLException, IOException
  {
    String str1 = super.resolveSystem(paramString);
    if (str1 != null) {
      return str1;
    }
    Enumeration localEnumeration = catalogEntries.elements();
    while (localEnumeration.hasMoreElements())
    {
      CatalogEntry localCatalogEntry = (CatalogEntry)localEnumeration.nextElement();
      if (localCatalogEntry.getEntryType() == RESOLVER)
      {
        str1 = resolveExternalSystem(paramString, localCatalogEntry.getEntryArg(0));
        if (str1 != null) {
          return str1;
        }
      }
      else if (localCatalogEntry.getEntryType() == SYSTEMSUFFIX)
      {
        String str2 = localCatalogEntry.getEntryArg(0);
        String str3 = localCatalogEntry.getEntryArg(1);
        if ((str2.length() <= paramString.length()) && (paramString.substring(paramString.length() - str2.length()).equals(str2))) {
          return str3;
        }
      }
    }
    return resolveSubordinateCatalogs(Catalog.SYSTEM, null, null, paramString);
  }
  
  public String resolvePublic(String paramString1, String paramString2)
    throws MalformedURLException, IOException
  {
    String str = super.resolvePublic(paramString1, paramString2);
    if (str != null) {
      return str;
    }
    Enumeration localEnumeration = catalogEntries.elements();
    while (localEnumeration.hasMoreElements())
    {
      CatalogEntry localCatalogEntry = (CatalogEntry)localEnumeration.nextElement();
      if (localCatalogEntry.getEntryType() == RESOLVER)
      {
        if (paramString2 != null)
        {
          str = resolveExternalSystem(paramString2, localCatalogEntry.getEntryArg(0));
          if (str != null) {
            return str;
          }
        }
        str = resolveExternalPublic(paramString1, localCatalogEntry.getEntryArg(0));
        if (str != null) {
          return str;
        }
      }
    }
    return resolveSubordinateCatalogs(Catalog.PUBLIC, null, paramString1, paramString2);
  }
  
  protected String resolveExternalSystem(String paramString1, String paramString2)
    throws MalformedURLException, IOException
  {
    Resolver localResolver = queryResolver(paramString2, "i2l", paramString1, null);
    if (localResolver != null) {
      return localResolver.resolveSystem(paramString1);
    }
    return null;
  }
  
  protected String resolveExternalPublic(String paramString1, String paramString2)
    throws MalformedURLException, IOException
  {
    Resolver localResolver = queryResolver(paramString2, "fpi2l", paramString1, null);
    if (localResolver != null) {
      return localResolver.resolvePublic(paramString1, null);
    }
    return null;
  }
  
  protected Resolver queryResolver(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Object localObject1 = null;
    String str1 = paramString1 + "?command=" + paramString2 + "&format=tr9401&uri=" + paramString3 + "&uri2=" + paramString4;
    Object localObject2 = null;
    try
    {
      URL localURL = new URL(str1);
      URLConnection localURLConnection = localURL.openConnection();
      localURLConnection.setUseCaches(false);
      Resolver localResolver = (Resolver)newCatalog();
      String str2 = localURLConnection.getContentType();
      if (str2.indexOf(";") > 0) {
        str2 = str2.substring(0, str2.indexOf(";"));
      }
      localResolver.parseCatalog(str2, localURLConnection.getInputStream());
      return localResolver;
    }
    catch (CatalogException localCatalogException)
    {
      if (localCatalogException.getExceptionType() == 6) {
        catalogManager.debug.message(1, "Unparseable catalog: " + str1);
      } else if (localCatalogException.getExceptionType() == 5) {
        catalogManager.debug.message(1, "Unknown catalog format: " + str1);
      }
      return null;
    }
    catch (MalformedURLException localMalformedURLException)
    {
      catalogManager.debug.message(1, "Malformed resolver URL: " + str1);
      return null;
    }
    catch (IOException localIOException)
    {
      catalogManager.debug.message(1, "I/O Exception opening resolver: " + str1);
    }
    return null;
  }
  
  private Vector appendVector(Vector paramVector1, Vector paramVector2)
  {
    if (paramVector2 != null) {
      for (int i = 0; i < paramVector2.size(); i++) {
        paramVector1.addElement(paramVector2.elementAt(i));
      }
    }
    return paramVector1;
  }
  
  public Vector resolveAllSystemReverse(String paramString)
    throws MalformedURLException, IOException
  {
    Vector localVector1 = new Vector();
    if (paramString != null)
    {
      localVector2 = resolveLocalSystemReverse(paramString);
      localVector1 = appendVector(localVector1, localVector2);
    }
    Vector localVector2 = resolveAllSubordinateCatalogs(SYSTEMREVERSE, null, null, paramString);
    return appendVector(localVector1, localVector2);
  }
  
  public String resolveSystemReverse(String paramString)
    throws MalformedURLException, IOException
  {
    Vector localVector = resolveAllSystemReverse(paramString);
    if ((localVector != null) && (localVector.size() > 0)) {
      return (String)localVector.elementAt(0);
    }
    return null;
  }
  
  public Vector resolveAllSystem(String paramString)
    throws MalformedURLException, IOException
  {
    Vector localVector1 = new Vector();
    if (paramString != null)
    {
      localVector2 = resolveAllLocalSystem(paramString);
      localVector1 = appendVector(localVector1, localVector2);
    }
    Vector localVector2 = resolveAllSubordinateCatalogs(SYSTEM, null, null, paramString);
    localVector1 = appendVector(localVector1, localVector2);
    if (localVector1.size() > 0) {
      return localVector1;
    }
    return null;
  }
  
  private Vector resolveAllLocalSystem(String paramString)
  {
    Vector localVector = new Vector();
    String str = SecuritySupport.getSystemProperty("os.name");
    int i = str.indexOf("Windows") >= 0 ? 1 : 0;
    Enumeration localEnumeration = catalogEntries.elements();
    while (localEnumeration.hasMoreElements())
    {
      CatalogEntry localCatalogEntry = (CatalogEntry)localEnumeration.nextElement();
      if ((localCatalogEntry.getEntryType() == SYSTEM) && ((localCatalogEntry.getEntryArg(0).equals(paramString)) || ((i != 0) && (localCatalogEntry.getEntryArg(0).equalsIgnoreCase(paramString))))) {
        localVector.addElement(localCatalogEntry.getEntryArg(1));
      }
    }
    if (localVector.size() == 0) {
      return null;
    }
    return localVector;
  }
  
  private Vector resolveLocalSystemReverse(String paramString)
  {
    Vector localVector = new Vector();
    String str = SecuritySupport.getSystemProperty("os.name");
    int i = str.indexOf("Windows") >= 0 ? 1 : 0;
    Enumeration localEnumeration = catalogEntries.elements();
    while (localEnumeration.hasMoreElements())
    {
      CatalogEntry localCatalogEntry = (CatalogEntry)localEnumeration.nextElement();
      if ((localCatalogEntry.getEntryType() == SYSTEM) && ((localCatalogEntry.getEntryArg(1).equals(paramString)) || ((i != 0) && (localCatalogEntry.getEntryArg(1).equalsIgnoreCase(paramString))))) {
        localVector.addElement(localCatalogEntry.getEntryArg(0));
      }
    }
    if (localVector.size() == 0) {
      return null;
    }
    return localVector;
  }
  
  private synchronized Vector resolveAllSubordinateCatalogs(int paramInt, String paramString1, String paramString2, String paramString3)
    throws MalformedURLException, IOException
  {
    Vector localVector = new Vector();
    for (int i = 0; i < catalogs.size(); i++)
    {
      Resolver localResolver = null;
      Object localObject;
      try
      {
        localResolver = (Resolver)catalogs.elementAt(i);
      }
      catch (ClassCastException localClassCastException)
      {
        localObject = (String)catalogs.elementAt(i);
        localResolver = (Resolver)newCatalog();
        try
        {
          localResolver.parseCatalog((String)localObject);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          catalogManager.debug.message(1, "Malformed Catalog URL", (String)localObject);
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          catalogManager.debug.message(1, "Failed to load catalog, file not found", (String)localObject);
        }
        catch (IOException localIOException)
        {
          catalogManager.debug.message(1, "Failed to load catalog, I/O error", (String)localObject);
        }
        catalogs.setElementAt(localResolver, i);
      }
      String str = null;
      if (paramInt == DOCTYPE)
      {
        str = localResolver.resolveDoctype(paramString1, paramString2, paramString3);
        if (str != null)
        {
          localVector.addElement(str);
          return localVector;
        }
      }
      else if (paramInt == DOCUMENT)
      {
        str = localResolver.resolveDocument();
        if (str != null)
        {
          localVector.addElement(str);
          return localVector;
        }
      }
      else if (paramInt == ENTITY)
      {
        str = localResolver.resolveEntity(paramString1, paramString2, paramString3);
        if (str != null)
        {
          localVector.addElement(str);
          return localVector;
        }
      }
      else if (paramInt == NOTATION)
      {
        str = localResolver.resolveNotation(paramString1, paramString2, paramString3);
        if (str != null)
        {
          localVector.addElement(str);
          return localVector;
        }
      }
      else if (paramInt == PUBLIC)
      {
        str = localResolver.resolvePublic(paramString2, paramString3);
        if (str != null)
        {
          localVector.addElement(str);
          return localVector;
        }
      }
      else
      {
        if (paramInt == SYSTEM)
        {
          localObject = localResolver.resolveAllSystem(paramString3);
          localVector = appendVector(localVector, (Vector)localObject);
          break;
        }
        if (paramInt == SYSTEMREVERSE)
        {
          localObject = localResolver.resolveAllSystemReverse(paramString3);
          localVector = appendVector(localVector, (Vector)localObject);
        }
      }
    }
    if (localVector != null) {
      return localVector;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */