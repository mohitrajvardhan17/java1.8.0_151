package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Deprecated
public abstract interface Certificate
{
  public abstract Principal getGuarantor();
  
  public abstract Principal getPrincipal();
  
  public abstract PublicKey getPublicKey();
  
  public abstract void encode(OutputStream paramOutputStream)
    throws KeyException, IOException;
  
  public abstract void decode(InputStream paramInputStream)
    throws KeyException, IOException;
  
  public abstract String getFormat();
  
  public abstract String toString(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */