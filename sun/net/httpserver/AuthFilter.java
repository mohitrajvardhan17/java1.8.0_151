package sun.net.httpserver;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Authenticator.Failure;
import com.sun.net.httpserver.Authenticator.Result;
import com.sun.net.httpserver.Authenticator.Retry;
import com.sun.net.httpserver.Authenticator.Success;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Filter.Chain;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;

public class AuthFilter
  extends Filter
{
  private Authenticator authenticator;
  
  public AuthFilter(Authenticator paramAuthenticator)
  {
    authenticator = paramAuthenticator;
  }
  
  public String description()
  {
    return "Authentication filter";
  }
  
  public void setAuthenticator(Authenticator paramAuthenticator)
  {
    authenticator = paramAuthenticator;
  }
  
  public void consumeInput(HttpExchange paramHttpExchange)
    throws IOException
  {
    InputStream localInputStream = paramHttpExchange.getRequestBody();
    byte[] arrayOfByte = new byte['က'];
    while (localInputStream.read(arrayOfByte) != -1) {}
    localInputStream.close();
  }
  
  public void doFilter(HttpExchange paramHttpExchange, Filter.Chain paramChain)
    throws IOException
  {
    if (authenticator != null)
    {
      Authenticator.Result localResult = authenticator.authenticate(paramHttpExchange);
      Object localObject;
      if ((localResult instanceof Authenticator.Success))
      {
        localObject = (Authenticator.Success)localResult;
        ExchangeImpl localExchangeImpl = ExchangeImpl.get(paramHttpExchange);
        localExchangeImpl.setPrincipal(((Authenticator.Success)localObject).getPrincipal());
        paramChain.doFilter(paramHttpExchange);
      }
      else if ((localResult instanceof Authenticator.Retry))
      {
        localObject = (Authenticator.Retry)localResult;
        consumeInput(paramHttpExchange);
        paramHttpExchange.sendResponseHeaders(((Authenticator.Retry)localObject).getResponseCode(), -1L);
      }
      else if ((localResult instanceof Authenticator.Failure))
      {
        localObject = (Authenticator.Failure)localResult;
        consumeInput(paramHttpExchange);
        paramHttpExchange.sendResponseHeaders(((Authenticator.Failure)localObject).getResponseCode(), -1L);
      }
    }
    else
    {
      paramChain.doFilter(paramHttpExchange);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\AuthFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */