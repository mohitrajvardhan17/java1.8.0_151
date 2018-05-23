package sun.security.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract interface DerEncoder
{
  public abstract void derEncode(OutputStream paramOutputStream)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\DerEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */