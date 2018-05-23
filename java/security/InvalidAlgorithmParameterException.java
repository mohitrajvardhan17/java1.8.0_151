package java.security;

public class InvalidAlgorithmParameterException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = 2864672297499471472L;
  
  public InvalidAlgorithmParameterException() {}
  
  public InvalidAlgorithmParameterException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidAlgorithmParameterException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public InvalidAlgorithmParameterException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\InvalidAlgorithmParameterException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */