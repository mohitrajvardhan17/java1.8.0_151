package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Module;
import java.util.ArrayList;
import java.util.List;

class ServerContainer
  extends Container
{
  private final Module module = new Module()
  {
    private final List<BoundEndpoint> endpoints = new ArrayList();
    
    @NotNull
    public List<BoundEndpoint> getBoundEndpoints()
    {
      return endpoints;
    }
  };
  
  ServerContainer() {}
  
  public <T> T getSPI(Class<T> paramClass)
  {
    Object localObject = super.getSPI(paramClass);
    if (localObject != null) {
      return (T)localObject;
    }
    if (paramClass == Module.class) {
      return (T)paramClass.cast(module);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */