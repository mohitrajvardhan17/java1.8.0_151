package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.ResourceLoader;
import com.sun.xml.internal.ws.api.server.Container;
import java.net.MalformedURLException;
import java.net.URL;

final class ClientContainer
  extends Container
{
  private final ResourceLoader loader = new ResourceLoader()
  {
    public URL getResource(String paramAnonymousString)
      throws MalformedURLException
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = getClass().getClassLoader();
      }
      return localClassLoader.getResource("META-INF/" + paramAnonymousString);
    }
  };
  
  ClientContainer() {}
  
  public <T> T getSPI(Class<T> paramClass)
  {
    Object localObject = super.getSPI(paramClass);
    if (localObject != null) {
      return (T)localObject;
    }
    if (paramClass == ResourceLoader.class) {
      return (T)paramClass.cast(loader);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\ClientContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */