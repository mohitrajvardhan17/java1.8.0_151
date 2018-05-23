package javax.print;

import java.io.OutputStream;

public abstract class StreamPrintService
  implements PrintService
{
  private OutputStream outStream;
  private boolean disposed = false;
  
  private StreamPrintService() {}
  
  protected StreamPrintService(OutputStream paramOutputStream)
  {
    outStream = paramOutputStream;
  }
  
  public OutputStream getOutputStream()
  {
    return outStream;
  }
  
  public abstract String getOutputFormat();
  
  public void dispose()
  {
    disposed = true;
  }
  
  public boolean isDisposed()
  {
    return disposed;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\StreamPrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */