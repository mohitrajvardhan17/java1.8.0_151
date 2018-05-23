package sun.misc;

import java.io.Console;
import java.nio.charset.Charset;

public abstract interface JavaIOAccess
{
  public abstract Console console();
  
  public abstract Charset charset();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JavaIOAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */