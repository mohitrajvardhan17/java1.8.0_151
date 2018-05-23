package javax.xml.ws.http;

import javax.xml.ws.ProtocolException;

public class HTTPException
  extends ProtocolException
{
  private int statusCode;
  
  public HTTPException(int paramInt)
  {
    statusCode = paramInt;
  }
  
  public int getStatusCode()
  {
    return statusCode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\http\HTTPException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */