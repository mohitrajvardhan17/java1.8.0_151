package javax.management.loading;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

public class PrivateMLet
  extends MLet
  implements PrivateClassLoader
{
  private static final long serialVersionUID = 2503458973393711979L;
  
  public PrivateMLet(URL[] paramArrayOfURL, boolean paramBoolean)
  {
    super(paramArrayOfURL, paramBoolean);
  }
  
  public PrivateMLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, boolean paramBoolean)
  {
    super(paramArrayOfURL, paramClassLoader, paramBoolean);
  }
  
  public PrivateMLet(URL[] paramArrayOfURL, ClassLoader paramClassLoader, URLStreamHandlerFactory paramURLStreamHandlerFactory, boolean paramBoolean)
  {
    super(paramArrayOfURL, paramClassLoader, paramURLStreamHandlerFactory, paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\PrivateMLet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */