package javax.xml.ws;

import java.util.List;
import javax.xml.ws.handler.Handler;

public abstract interface Binding
{
  public abstract List<Handler> getHandlerChain();
  
  public abstract void setHandlerChain(List<Handler> paramList);
  
  public abstract String getBindingID();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\Binding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */