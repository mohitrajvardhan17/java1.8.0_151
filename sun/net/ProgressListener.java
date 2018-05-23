package sun.net;

import java.util.EventListener;

public abstract interface ProgressListener
  extends EventListener
{
  public abstract void progressStart(ProgressEvent paramProgressEvent);
  
  public abstract void progressUpdate(ProgressEvent paramProgressEvent);
  
  public abstract void progressFinish(ProgressEvent paramProgressEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ProgressListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */