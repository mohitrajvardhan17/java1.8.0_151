package javax.xml.ws;

import java.io.Serializable;

public final class Holder<T>
  implements Serializable
{
  private static final long serialVersionUID = 2623699057546497185L;
  public T value;
  
  public Holder() {}
  
  public Holder(T paramT)
  {
    value = paramT;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\Holder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */