package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.Component;
import java.net.URI;

public abstract interface BoundEndpoint
  extends Component
{
  @NotNull
  public abstract WSEndpoint getEndpoint();
  
  @NotNull
  public abstract URI getAddress();
  
  @NotNull
  public abstract URI getAddress(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\BoundEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */