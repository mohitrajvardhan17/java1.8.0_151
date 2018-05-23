package javax.xml.ws.handler;

public abstract interface Handler<C extends MessageContext>
{
  public abstract boolean handleMessage(C paramC);
  
  public abstract boolean handleFault(C paramC);
  
  public abstract void close(MessageContext paramMessageContext);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\handler\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */