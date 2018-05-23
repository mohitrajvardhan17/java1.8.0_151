package sun.security.timestamp;

import java.io.IOException;

public abstract interface Timestamper
{
  public abstract TSResponse generateTimestamp(TSRequest paramTSRequest)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\timestamp\Timestamper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */