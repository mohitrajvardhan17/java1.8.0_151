package java.security;

public class InvalidKeyException
  extends KeyException
{
  private static final long serialVersionUID = 5698479920593359816L;
  
  public InvalidKeyException() {}
  
  public InvalidKeyException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidKeyException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public InvalidKeyException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\InvalidKeyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */