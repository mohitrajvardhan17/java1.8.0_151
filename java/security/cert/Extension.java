package java.security.cert;

import java.io.IOException;
import java.io.OutputStream;

public abstract interface Extension
{
  public abstract String getId();
  
  public abstract boolean isCritical();
  
  public abstract byte[] getValue();
  
  public abstract void encode(OutputStream paramOutputStream)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\Extension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */