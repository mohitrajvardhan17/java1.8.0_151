package java.net;

import java.io.IOException;

public class UnknownServiceException
  extends IOException
{
  private static final long serialVersionUID = -4169033248853639508L;
  
  public UnknownServiceException() {}
  
  public UnknownServiceException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\UnknownServiceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */