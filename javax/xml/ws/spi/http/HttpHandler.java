package javax.xml.ws.spi.http;

import java.io.IOException;

public abstract class HttpHandler
{
  public HttpHandler() {}
  
  public abstract void handle(HttpExchange paramHttpExchange)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\spi\http\HttpHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */