package java.security.spec;

import java.security.GeneralSecurityException;

public class InvalidParameterSpecException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = -970468769593399342L;
  
  public InvalidParameterSpecException() {}
  
  public InvalidParameterSpecException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\spec\InvalidParameterSpecException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */