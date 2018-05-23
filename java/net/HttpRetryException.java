package java.net;

import java.io.IOException;

public class HttpRetryException
  extends IOException
{
  private static final long serialVersionUID = -9186022286469111381L;
  private int responseCode;
  private String location;
  
  public HttpRetryException(String paramString, int paramInt)
  {
    super(paramString);
    responseCode = paramInt;
  }
  
  public HttpRetryException(String paramString1, int paramInt, String paramString2)
  {
    super(paramString1);
    responseCode = paramInt;
    location = paramString2;
  }
  
  public int responseCode()
  {
    return responseCode;
  }
  
  public String getReason()
  {
    return super.getMessage();
  }
  
  public String getLocation()
  {
    return location;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\HttpRetryException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */