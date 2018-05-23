package javax.imageio;

import java.io.IOException;

public class IIOException
  extends IOException
{
  public IIOException(String paramString)
  {
    super(paramString);
  }
  
  public IIOException(String paramString, Throwable paramThrowable)
  {
    super(paramString);
    initCause(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\IIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */