package com.sun.net.httpserver;

import jdk.Exported;

@Exported
public abstract class Authenticator
{
  public Authenticator() {}
  
  public abstract Result authenticate(HttpExchange paramHttpExchange);
  
  @Exported
  public static class Failure
    extends Authenticator.Result
  {
    private int responseCode;
    
    public Failure(int paramInt)
    {
      responseCode = paramInt;
    }
    
    public int getResponseCode()
    {
      return responseCode;
    }
  }
  
  public static abstract class Result
  {
    public Result() {}
  }
  
  @Exported
  public static class Retry
    extends Authenticator.Result
  {
    private int responseCode;
    
    public Retry(int paramInt)
    {
      responseCode = paramInt;
    }
    
    public int getResponseCode()
    {
      return responseCode;
    }
  }
  
  @Exported
  public static class Success
    extends Authenticator.Result
  {
    private HttpPrincipal principal;
    
    public Success(HttpPrincipal paramHttpPrincipal)
    {
      principal = paramHttpPrincipal;
    }
    
    public HttpPrincipal getPrincipal()
    {
      return principal;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */