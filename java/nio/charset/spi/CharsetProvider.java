package java.nio.charset.spi;

import java.nio.charset.Charset;
import java.util.Iterator;

public abstract class CharsetProvider
{
  protected CharsetProvider()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("charsetProvider"));
    }
  }
  
  public abstract Iterator<Charset> charsets();
  
  public abstract Charset charsetForName(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\charset\spi\CharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */