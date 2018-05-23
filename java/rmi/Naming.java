package java.rmi;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class Naming
{
  private Naming() {}
  
  public static Remote lookup(String paramString)
    throws NotBoundException, MalformedURLException, RemoteException
  {
    ParsedNamingURL localParsedNamingURL = parseURL(paramString);
    Registry localRegistry = getRegistry(localParsedNamingURL);
    if (name == null) {
      return localRegistry;
    }
    return localRegistry.lookup(name);
  }
  
  public static void bind(String paramString, Remote paramRemote)
    throws AlreadyBoundException, MalformedURLException, RemoteException
  {
    ParsedNamingURL localParsedNamingURL = parseURL(paramString);
    Registry localRegistry = getRegistry(localParsedNamingURL);
    if (paramRemote == null) {
      throw new NullPointerException("cannot bind to null");
    }
    localRegistry.bind(name, paramRemote);
  }
  
  public static void unbind(String paramString)
    throws RemoteException, NotBoundException, MalformedURLException
  {
    ParsedNamingURL localParsedNamingURL = parseURL(paramString);
    Registry localRegistry = getRegistry(localParsedNamingURL);
    localRegistry.unbind(name);
  }
  
  public static void rebind(String paramString, Remote paramRemote)
    throws RemoteException, MalformedURLException
  {
    ParsedNamingURL localParsedNamingURL = parseURL(paramString);
    Registry localRegistry = getRegistry(localParsedNamingURL);
    if (paramRemote == null) {
      throw new NullPointerException("cannot bind to null");
    }
    localRegistry.rebind(name, paramRemote);
  }
  
  public static String[] list(String paramString)
    throws RemoteException, MalformedURLException
  {
    ParsedNamingURL localParsedNamingURL = parseURL(paramString);
    Registry localRegistry = getRegistry(localParsedNamingURL);
    String str = "";
    if ((port > 0) || (!host.equals(""))) {
      str = str + "//" + host;
    }
    if (port > 0) {
      str = str + ":" + port;
    }
    str = str + "/";
    String[] arrayOfString = localRegistry.list();
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = (str + arrayOfString[i]);
    }
    return arrayOfString;
  }
  
  private static Registry getRegistry(ParsedNamingURL paramParsedNamingURL)
    throws RemoteException
  {
    return LocateRegistry.getRegistry(host, port);
  }
  
  private static ParsedNamingURL parseURL(String paramString)
    throws MalformedURLException
  {
    try
    {
      return intParseURL(paramString);
    }
    catch (URISyntaxException localURISyntaxException1)
    {
      MalformedURLException localMalformedURLException1 = new MalformedURLException("invalid URL String: " + paramString);
      localMalformedURLException1.initCause(localURISyntaxException1);
      int i = paramString.indexOf(':');
      int j = paramString.indexOf("//:");
      if (j < 0) {
        throw localMalformedURLException1;
      }
      if ((j == 0) || ((i > 0) && (j == i + 1)))
      {
        int k = j + 2;
        String str = paramString.substring(0, k) + "localhost" + paramString.substring(k);
        try
        {
          return intParseURL(str);
        }
        catch (URISyntaxException localURISyntaxException2)
        {
          throw localMalformedURLException1;
        }
        catch (MalformedURLException localMalformedURLException2)
        {
          throw localMalformedURLException2;
        }
      }
      throw localMalformedURLException1;
    }
  }
  
  private static ParsedNamingURL intParseURL(String paramString)
    throws MalformedURLException, URISyntaxException
  {
    URI localURI = new URI(paramString);
    if (localURI.isOpaque()) {
      throw new MalformedURLException("not a hierarchical URL: " + paramString);
    }
    if (localURI.getFragment() != null) {
      throw new MalformedURLException("invalid character, '#', in URL name: " + paramString);
    }
    if (localURI.getQuery() != null) {
      throw new MalformedURLException("invalid character, '?', in URL name: " + paramString);
    }
    if (localURI.getUserInfo() != null) {
      throw new MalformedURLException("invalid character, '@', in URL host: " + paramString);
    }
    String str1 = localURI.getScheme();
    if ((str1 != null) && (!str1.equals("rmi"))) {
      throw new MalformedURLException("invalid URL scheme: " + paramString);
    }
    String str2 = localURI.getPath();
    if (str2 != null)
    {
      if (str2.startsWith("/")) {
        str2 = str2.substring(1);
      }
      if (str2.length() == 0) {
        str2 = null;
      }
    }
    String str3 = localURI.getHost();
    if (str3 == null)
    {
      str3 = "";
      try
      {
        localURI.parseServerAuthority();
      }
      catch (URISyntaxException localURISyntaxException1)
      {
        String str4 = localURI.getAuthority();
        if ((str4 != null) && (str4.startsWith(":")))
        {
          str4 = "localhost" + str4;
          try
          {
            localURI = new URI(null, str4, null, null, null);
            localURI.parseServerAuthority();
          }
          catch (URISyntaxException localURISyntaxException2)
          {
            throw new MalformedURLException("invalid authority: " + paramString);
          }
        }
        else
        {
          throw new MalformedURLException("invalid authority: " + paramString);
        }
      }
    }
    int i = localURI.getPort();
    if (i == -1) {
      i = 1099;
    }
    return new ParsedNamingURL(str3, i, str2);
  }
  
  private static class ParsedNamingURL
  {
    String host;
    int port;
    String name;
    
    ParsedNamingURL(String paramString1, int paramInt, String paramString2)
    {
      host = paramString1;
      port = paramInt;
      name = paramString2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\Naming.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */