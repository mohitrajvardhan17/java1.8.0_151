package com.sun.org.apache.xml.internal.resolver.helpers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class BootstrapResolver
  implements EntityResolver, URIResolver
{
  public static final String xmlCatalogXSD = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd";
  public static final String xmlCatalogRNG = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng";
  public static final String xmlCatalogPubId = "-//OASIS//DTD XML Catalogs V1.0//EN";
  public static final String xmlCatalogSysId = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd";
  private final Map<String, String> publicMap = new HashMap();
  private final Map<String, String> systemMap = new HashMap();
  private final Map<String, String> uriMap = new HashMap();
  
  public BootstrapResolver()
  {
    URL localURL = getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.dtd");
    if (localURL != null)
    {
      publicMap.put("-//OASIS//DTD XML Catalogs V1.0//EN", localURL.toString());
      systemMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd", localURL.toString());
    }
    localURL = getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.rng");
    if (localURL != null) {
      uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng", localURL.toString());
    }
    localURL = getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.xsd");
    if (localURL != null) {
      uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd", localURL.toString());
    }
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
  {
    String str = null;
    if ((paramString2 != null) && (systemMap.containsKey(paramString2))) {
      str = (String)systemMap.get(paramString2);
    } else if ((paramString1 != null) && (publicMap.containsKey(paramString1))) {
      str = (String)publicMap.get(paramString1);
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
    if ((paramString1 != null) && (uriMap.containsKey(paramString1))) {
      str3 = (String)uriMap.get(paramString1);
    }
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
    SAXSource localSAXSource = new SAXSource();
    localSAXSource.setInputSource(new InputSource(str3));
    return localSAXSource;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\helpers\BootstrapResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */