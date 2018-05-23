package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import java.io.IOException;

public abstract class HttpMetadataPublisher
{
  public HttpMetadataPublisher() {}
  
  public abstract boolean handleMetadataRequest(@NotNull HttpAdapter paramHttpAdapter, @NotNull WSHTTPConnection paramWSHTTPConnection)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\HttpMetadataPublisher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */