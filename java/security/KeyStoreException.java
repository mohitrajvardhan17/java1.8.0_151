package java.security;

public class KeyStoreException
  extends GeneralSecurityException
{
  private static final long serialVersionUID = -1119353179322377262L;
  
  public KeyStoreException() {}
  
  public KeyStoreException(String paramString)
  {
    super(paramString);
  }
  
  public KeyStoreException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  public KeyStoreException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\KeyStoreException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */