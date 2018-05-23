package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import javax.xml.ws.WebServiceException;

public final class EndpointAddress
{
  @Nullable
  private URL url;
  private final URI uri;
  private final String stringForm;
  private volatile boolean dontUseProxyMethod;
  private Proxy proxy;
  
  public EndpointAddress(URI paramURI)
  {
    uri = paramURI;
    stringForm = paramURI.toString();
    try
    {
      initURL();
      proxy = chooseProxy();
    }
    catch (MalformedURLException localMalformedURLException) {}
  }
  
  public EndpointAddress(String paramString)
    throws URISyntaxException
  {
    uri = new URI(paramString);
    stringForm = paramString;
    try
    {
      initURL();
      proxy = chooseProxy();
    }
    catch (MalformedURLException localMalformedURLException) {}
  }
  
  private void initURL()
    throws MalformedURLException
  {
    String str = uri.getScheme();
    if (str == null)
    {
      url = new URL(uri.toString());
      return;
    }
    str = str.toLowerCase();
    if (("http".equals(str)) || ("https".equals(str))) {
      url = new URL(uri.toASCIIString());
    } else {
      url = uri.toURL();
    }
  }
  
  public static EndpointAddress create(String paramString)
  {
    try
    {
      return new EndpointAddress(paramString);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new WebServiceException("Illegal endpoint address: " + paramString, localURISyntaxException);
    }
  }
  
  private Proxy chooseProxy()
  {
    ProxySelector localProxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ProxySelector run()
      {
        return ProxySelector.getDefault();
      }
    });
    if (localProxySelector == null) {
      return Proxy.NO_PROXY;
    }
    if (!localProxySelector.getClass().getName().equals("sun.net.spi.DefaultProxySelector")) {
      return null;
    }
    Iterator localIterator = localProxySelector.select(uri).iterator();
    if (localIterator.hasNext()) {
      return (Proxy)localIterator.next();
    }
    return Proxy.NO_PROXY;
  }
  
  public URL getURL()
  {
    return url;
  }
  
  public URI getURI()
  {
    return uri;
  }
  
  public URLConnection openConnection()
    throws IOException
  {
    if (url == null) {
      throw new WebServiceException("URI=" + uri + " doesn't have the corresponding URL");
    }
    if ((proxy != null) && (!dontUseProxyMethod)) {
      try
      {
        return url.openConnection(proxy);
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        dontUseProxyMethod = true;
      }
    }
    return url.openConnection();
  }
  
  public String toString()
  {
    return stringForm;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\EndpointAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */