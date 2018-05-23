package com.sun.org.apache.xml.internal.resolver;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.BootstrapResolver;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.reflect.misc.ReflectUtil;

public class CatalogManager
{
  private static String pFiles = "xml.catalog.files";
  private static String pVerbosity = "xml.catalog.verbosity";
  private static String pPrefer = "xml.catalog.prefer";
  private static String pStatic = "xml.catalog.staticCatalog";
  private static String pAllowPI = "xml.catalog.allowPI";
  private static String pClassname = "xml.catalog.className";
  private static String pIgnoreMissing = "xml.catalog.ignoreMissing";
  private static CatalogManager staticManager = new CatalogManager();
  private BootstrapResolver bResolver = new BootstrapResolver();
  private boolean ignoreMissingProperties = (SecuritySupport.getSystemProperty(pIgnoreMissing) != null) || (SecuritySupport.getSystemProperty(pFiles) != null);
  private ResourceBundle resources;
  private String propertyFile = "CatalogManager.properties";
  private URL propertyFileURI = null;
  private String defaultCatalogFiles = "./xcatalog";
  private String catalogFiles = null;
  private boolean fromPropertiesFile = false;
  private int defaultVerbosity = 1;
  private Integer verbosity = null;
  private boolean defaultPreferPublic = true;
  private Boolean preferPublic = null;
  private boolean defaultUseStaticCatalog = true;
  private Boolean useStaticCatalog = null;
  private static Catalog staticCatalog = null;
  private boolean defaultOasisXMLCatalogPI = true;
  private Boolean oasisXMLCatalogPI = null;
  private boolean defaultRelativeCatalogs = true;
  private Boolean relativeCatalogs = null;
  private String catalogClassName = null;
  private boolean useServicesMechanism;
  public Debug debug = null;
  
  public CatalogManager()
  {
    init();
  }
  
  public CatalogManager(String paramString)
  {
    propertyFile = paramString;
    init();
  }
  
  private void init()
  {
    debug = new Debug();
    if (System.getSecurityManager() == null) {
      useServicesMechanism = true;
    }
  }
  
  public void setBootstrapResolver(BootstrapResolver paramBootstrapResolver)
  {
    bResolver = paramBootstrapResolver;
  }
  
  public BootstrapResolver getBootstrapResolver()
  {
    return bResolver;
  }
  
  private synchronized void readProperties()
  {
    try
    {
      propertyFileURI = CatalogManager.class.getResource("/" + propertyFile);
      InputStream localInputStream = CatalogManager.class.getResourceAsStream("/" + propertyFile);
      if (localInputStream == null)
      {
        if (!ignoreMissingProperties)
        {
          System.err.println("Cannot find " + propertyFile);
          ignoreMissingProperties = true;
        }
        return;
      }
      resources = new PropertyResourceBundle(localInputStream);
    }
    catch (MissingResourceException localMissingResourceException)
    {
      if (!ignoreMissingProperties) {
        System.err.println("Cannot read " + propertyFile);
      }
    }
    catch (IOException localIOException)
    {
      if (!ignoreMissingProperties) {
        System.err.println("Failure trying to read " + propertyFile);
      }
    }
    if (verbosity == null) {
      try
      {
        String str = resources.getString("verbosity");
        int i = Integer.parseInt(str.trim());
        debug.setDebug(i);
        verbosity = new Integer(i);
      }
      catch (Exception localException) {}
    }
  }
  
  public static CatalogManager getStaticManager()
  {
    return staticManager;
  }
  
  public boolean getIgnoreMissingProperties()
  {
    return ignoreMissingProperties;
  }
  
  public void setIgnoreMissingProperties(boolean paramBoolean)
  {
    ignoreMissingProperties = paramBoolean;
  }
  
  /**
   * @deprecated
   */
  public void ignoreMissingProperties(boolean paramBoolean)
  {
    setIgnoreMissingProperties(paramBoolean);
  }
  
  private int queryVerbosity()
  {
    String str1 = Integer.toString(defaultVerbosity);
    String str2 = SecuritySupport.getSystemProperty(pVerbosity);
    if (str2 == null)
    {
      if (resources == null) {
        readProperties();
      }
      if (resources != null) {
        try
        {
          str2 = resources.getString("verbosity");
        }
        catch (MissingResourceException localMissingResourceException)
        {
          str2 = str1;
        }
      } else {
        str2 = str1;
      }
    }
    int i = defaultVerbosity;
    try
    {
      i = Integer.parseInt(str2.trim());
    }
    catch (Exception localException)
    {
      System.err.println("Cannot parse verbosity: \"" + str2 + "\"");
    }
    if (verbosity == null)
    {
      debug.setDebug(i);
      verbosity = new Integer(i);
    }
    return i;
  }
  
  public int getVerbosity()
  {
    if (verbosity == null) {
      verbosity = new Integer(queryVerbosity());
    }
    return verbosity.intValue();
  }
  
  public void setVerbosity(int paramInt)
  {
    verbosity = new Integer(paramInt);
    debug.setDebug(paramInt);
  }
  
  /**
   * @deprecated
   */
  public int verbosity()
  {
    return getVerbosity();
  }
  
  private boolean queryRelativeCatalogs()
  {
    if (resources == null) {
      readProperties();
    }
    if (resources == null) {
      return defaultRelativeCatalogs;
    }
    try
    {
      String str = resources.getString("relative-catalogs");
      return (str.equalsIgnoreCase("true")) || (str.equalsIgnoreCase("yes")) || (str.equalsIgnoreCase("1"));
    }
    catch (MissingResourceException localMissingResourceException) {}
    return defaultRelativeCatalogs;
  }
  
  public boolean getRelativeCatalogs()
  {
    if (relativeCatalogs == null) {
      relativeCatalogs = new Boolean(queryRelativeCatalogs());
    }
    return relativeCatalogs.booleanValue();
  }
  
  public void setRelativeCatalogs(boolean paramBoolean)
  {
    relativeCatalogs = new Boolean(paramBoolean);
  }
  
  /**
   * @deprecated
   */
  public boolean relativeCatalogs()
  {
    return getRelativeCatalogs();
  }
  
  private String queryCatalogFiles()
  {
    String str = SecuritySupport.getSystemProperty(pFiles);
    fromPropertiesFile = false;
    if (str == null)
    {
      if (resources == null) {
        readProperties();
      }
      if (resources != null) {
        try
        {
          str = resources.getString("catalogs");
          fromPropertiesFile = true;
        }
        catch (MissingResourceException localMissingResourceException)
        {
          System.err.println(propertyFile + ": catalogs not found.");
          str = null;
        }
      }
    }
    if (str == null) {
      str = defaultCatalogFiles;
    }
    return str;
  }
  
  public Vector getCatalogFiles()
  {
    if (catalogFiles == null) {
      catalogFiles = queryCatalogFiles();
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(catalogFiles, ";");
    Vector localVector = new Vector();
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      URL localURL = null;
      if ((fromPropertiesFile) && (!relativeCatalogs())) {
        try
        {
          localURL = new URL(propertyFileURI, str);
          str = localURL.toString();
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localURL = null;
        }
      }
      localVector.add(str);
    }
    return localVector;
  }
  
  public void setCatalogFiles(String paramString)
  {
    catalogFiles = paramString;
    fromPropertiesFile = false;
  }
  
  /**
   * @deprecated
   */
  public Vector catalogFiles()
  {
    return getCatalogFiles();
  }
  
  private boolean queryPreferPublic()
  {
    String str = SecuritySupport.getSystemProperty(pPrefer);
    if (str == null)
    {
      if (resources == null) {
        readProperties();
      }
      if (resources == null) {
        return defaultPreferPublic;
      }
      try
      {
        str = resources.getString("prefer");
      }
      catch (MissingResourceException localMissingResourceException)
      {
        return defaultPreferPublic;
      }
    }
    if (str == null) {
      return defaultPreferPublic;
    }
    return str.equalsIgnoreCase("public");
  }
  
  public boolean getPreferPublic()
  {
    if (preferPublic == null) {
      preferPublic = new Boolean(queryPreferPublic());
    }
    return preferPublic.booleanValue();
  }
  
  public void setPreferPublic(boolean paramBoolean)
  {
    preferPublic = new Boolean(paramBoolean);
  }
  
  /**
   * @deprecated
   */
  public boolean preferPublic()
  {
    return getPreferPublic();
  }
  
  private boolean queryUseStaticCatalog()
  {
    String str = SecuritySupport.getSystemProperty(pStatic);
    if (str == null)
    {
      if (resources == null) {
        readProperties();
      }
      if (resources == null) {
        return defaultUseStaticCatalog;
      }
      try
      {
        str = resources.getString("static-catalog");
      }
      catch (MissingResourceException localMissingResourceException)
      {
        return defaultUseStaticCatalog;
      }
    }
    if (str == null) {
      return defaultUseStaticCatalog;
    }
    return (str.equalsIgnoreCase("true")) || (str.equalsIgnoreCase("yes")) || (str.equalsIgnoreCase("1"));
  }
  
  public boolean getUseStaticCatalog()
  {
    if (useStaticCatalog == null) {
      useStaticCatalog = new Boolean(queryUseStaticCatalog());
    }
    return useStaticCatalog.booleanValue();
  }
  
  public void setUseStaticCatalog(boolean paramBoolean)
  {
    useStaticCatalog = new Boolean(paramBoolean);
  }
  
  /**
   * @deprecated
   */
  public boolean staticCatalog()
  {
    return getUseStaticCatalog();
  }
  
  public Catalog getPrivateCatalog()
  {
    Catalog localCatalog = staticCatalog;
    if (useStaticCatalog == null) {
      useStaticCatalog = new Boolean(getUseStaticCatalog());
    }
    if ((localCatalog == null) || (!useStaticCatalog.booleanValue()))
    {
      try
      {
        String str = getCatalogClassName();
        if (str == null) {
          localCatalog = new Catalog();
        } else {
          try
          {
            localCatalog = (Catalog)ReflectUtil.forName(str).newInstance();
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            debug.message(1, "Catalog class named '" + str + "' could not be found. Using default.");
            localCatalog = new Catalog();
          }
          catch (ClassCastException localClassCastException)
          {
            debug.message(1, "Class named '" + str + "' is not a Catalog. Using default.");
            localCatalog = new Catalog();
          }
        }
        localCatalog.setCatalogManager(this);
        localCatalog.setupReaders();
        localCatalog.loadSystemCatalogs();
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      if (useStaticCatalog.booleanValue()) {
        staticCatalog = localCatalog;
      }
    }
    return localCatalog;
  }
  
  public Catalog getCatalog()
  {
    Catalog localCatalog = staticCatalog;
    if (useStaticCatalog == null) {
      useStaticCatalog = new Boolean(getUseStaticCatalog());
    }
    if ((localCatalog == null) || (!useStaticCatalog.booleanValue()))
    {
      localCatalog = getPrivateCatalog();
      if (useStaticCatalog.booleanValue()) {
        staticCatalog = localCatalog;
      }
    }
    return localCatalog;
  }
  
  public boolean queryAllowOasisXMLCatalogPI()
  {
    String str = SecuritySupport.getSystemProperty(pAllowPI);
    if (str == null)
    {
      if (resources == null) {
        readProperties();
      }
      if (resources == null) {
        return defaultOasisXMLCatalogPI;
      }
      try
      {
        str = resources.getString("allow-oasis-xml-catalog-pi");
      }
      catch (MissingResourceException localMissingResourceException)
      {
        return defaultOasisXMLCatalogPI;
      }
    }
    if (str == null) {
      return defaultOasisXMLCatalogPI;
    }
    return (str.equalsIgnoreCase("true")) || (str.equalsIgnoreCase("yes")) || (str.equalsIgnoreCase("1"));
  }
  
  public boolean getAllowOasisXMLCatalogPI()
  {
    if (oasisXMLCatalogPI == null) {
      oasisXMLCatalogPI = new Boolean(queryAllowOasisXMLCatalogPI());
    }
    return oasisXMLCatalogPI.booleanValue();
  }
  
  public boolean useServicesMechanism()
  {
    return useServicesMechanism;
  }
  
  public void setAllowOasisXMLCatalogPI(boolean paramBoolean)
  {
    oasisXMLCatalogPI = new Boolean(paramBoolean);
  }
  
  /**
   * @deprecated
   */
  public boolean allowOasisXMLCatalogPI()
  {
    return getAllowOasisXMLCatalogPI();
  }
  
  public String queryCatalogClassName()
  {
    String str = SecuritySupport.getSystemProperty(pClassname);
    if (str == null)
    {
      if (resources == null) {
        readProperties();
      }
      if (resources == null) {
        return null;
      }
      try
      {
        return resources.getString("catalog-class-name");
      }
      catch (MissingResourceException localMissingResourceException)
      {
        return null;
      }
    }
    return str;
  }
  
  public String getCatalogClassName()
  {
    if (catalogClassName == null) {
      catalogClassName = queryCatalogClassName();
    }
    return catalogClassName;
  }
  
  public void setCatalogClassName(String paramString)
  {
    catalogClassName = paramString;
  }
  
  /**
   * @deprecated
   */
  public String catalogClassName()
  {
    return getCatalogClassName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\CatalogManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */