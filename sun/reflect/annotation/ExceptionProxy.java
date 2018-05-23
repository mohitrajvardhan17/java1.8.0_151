package sun.reflect.annotation;

import java.io.Serializable;

public abstract class ExceptionProxy
  implements Serializable
{
  public ExceptionProxy() {}
  
  protected abstract RuntimeException generateException();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\annotation\ExceptionProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */