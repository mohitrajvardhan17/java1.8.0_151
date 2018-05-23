package com.sun.net.httpserver;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import jdk.Exported;

@Exported
public abstract class Filter
{
  protected Filter() {}
  
  public abstract void doFilter(HttpExchange paramHttpExchange, Chain paramChain)
    throws IOException;
  
  public abstract String description();
  
  @Exported
  public static class Chain
  {
    private ListIterator<Filter> iter;
    private HttpHandler handler;
    
    public Chain(List<Filter> paramList, HttpHandler paramHttpHandler)
    {
      iter = paramList.listIterator();
      handler = paramHttpHandler;
    }
    
    public void doFilter(HttpExchange paramHttpExchange)
      throws IOException
    {
      if (!iter.hasNext())
      {
        handler.handle(paramHttpExchange);
      }
      else
      {
        Filter localFilter = (Filter)iter.next();
        localFilter.doFilter(paramHttpExchange, this);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */