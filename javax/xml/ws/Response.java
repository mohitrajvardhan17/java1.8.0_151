package javax.xml.ws;

import java.util.Map;
import java.util.concurrent.Future;

public abstract interface Response<T>
  extends Future<T>
{
  public abstract Map<String, Object> getContext();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\Response.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */