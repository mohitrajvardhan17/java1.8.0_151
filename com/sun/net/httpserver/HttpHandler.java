package com.sun.net.httpserver;

import java.io.IOException;
import jdk.Exported;

@Exported
public abstract interface HttpHandler
{
  public abstract void handle(HttpExchange paramHttpExchange)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\HttpHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */