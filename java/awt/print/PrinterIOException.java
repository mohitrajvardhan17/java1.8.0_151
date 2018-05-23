package java.awt.print;

import java.io.IOException;

public class PrinterIOException
  extends PrinterException
{
  static final long serialVersionUID = 5850870712125932846L;
  private IOException mException;
  
  public PrinterIOException(IOException paramIOException)
  {
    initCause(null);
    mException = paramIOException;
  }
  
  public IOException getIOException()
  {
    return mException;
  }
  
  public Throwable getCause()
  {
    return mException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\print\PrinterIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */