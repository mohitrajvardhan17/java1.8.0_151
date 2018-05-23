package javax.xml.ws.handler;

import java.util.List;

public abstract interface HandlerResolver
{
  public abstract List<Handler> getHandlerChain(PortInfo paramPortInfo);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\handler\HandlerResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */