package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

@Deprecated
public abstract interface RemoteCall
{
  @Deprecated
  public abstract ObjectOutput getOutputStream()
    throws IOException;
  
  @Deprecated
  public abstract void releaseOutputStream()
    throws IOException;
  
  @Deprecated
  public abstract ObjectInput getInputStream()
    throws IOException;
  
  @Deprecated
  public abstract void releaseInputStream()
    throws IOException;
  
  @Deprecated
  public abstract ObjectOutput getResultStream(boolean paramBoolean)
    throws IOException, StreamCorruptedException;
  
  @Deprecated
  public abstract void executeCall()
    throws Exception;
  
  @Deprecated
  public abstract void done()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RemoteCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */