package java.io;

public class InterruptedIOException
  extends IOException
{
  private static final long serialVersionUID = 4020568460727500567L;
  public int bytesTransferred = 0;
  
  public InterruptedIOException() {}
  
  public InterruptedIOException(String paramString)
  {
    super(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\InterruptedIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */