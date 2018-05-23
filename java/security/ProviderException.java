package java.security;

public class ProviderException
  extends RuntimeException
{
  private static final long serialVersionUID = 5256023526693665674L;
  
  public ProviderException() {}
  
  public ProviderException(String paramString)
  {
    super(paramString);
  }
  
  public ProviderException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public ProviderException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\ProviderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */