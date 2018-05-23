package java.rmi;

public class UnexpectedException
  extends RemoteException
{
  private static final long serialVersionUID = 1800467484195073863L;
  
  public UnexpectedException(String paramString)
  {
    super(paramString);
  }
  
  public UnexpectedException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\UnexpectedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */