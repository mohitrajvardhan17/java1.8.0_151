package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebModule;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public final class ServerAdapter
  extends HttpAdapter
  implements BoundEndpoint
{
  final String name;
  private static final Logger LOGGER = Logger.getLogger(ServerAdapter.class.getName());
  
  protected ServerAdapter(String paramString1, String paramString2, WSEndpoint paramWSEndpoint, ServerAdapterList paramServerAdapterList)
  {
    super(paramWSEndpoint, paramServerAdapterList, paramString2);
    name = paramString1;
    Module localModule = (Module)paramWSEndpoint.getContainer().getSPI(Module.class);
    if (localModule == null) {
      LOGGER.log(Level.WARNING, "Container {0} doesn''t support {1}", new Object[] { paramWSEndpoint.getContainer(), Module.class });
    } else {
      localModule.getBoundEndpoints().add(this);
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  @NotNull
  public URI getAddress()
  {
    WebModule localWebModule = (WebModule)endpoint.getContainer().getSPI(WebModule.class);
    if (localWebModule == null) {
      throw new WebServiceException("Container " + endpoint.getContainer() + " doesn't support " + WebModule.class);
    }
    return getAddress(localWebModule.getContextPath());
  }
  
  @NotNull
  public URI getAddress(String paramString)
  {
    String str = paramString + getValidPath();
    try
    {
      return new URI(str);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new WebServiceException("Unable to compute address for " + endpoint, localURISyntaxException);
    }
  }
  
  public void dispose()
  {
    endpoint.dispose();
  }
  
  public String getUrlPattern()
  {
    return urlPattern;
  }
  
  public String toString()
  {
    return super.toString() + "[name=" + name + ']';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\server\ServerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */