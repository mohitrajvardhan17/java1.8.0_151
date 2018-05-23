package org.omg.PortableInterceptor;

public abstract interface ClientRequestInterceptorOperations
  extends InterceptorOperations
{
  public abstract void send_request(ClientRequestInfo paramClientRequestInfo)
    throws ForwardRequest;
  
  public abstract void send_poll(ClientRequestInfo paramClientRequestInfo);
  
  public abstract void receive_reply(ClientRequestInfo paramClientRequestInfo);
  
  public abstract void receive_exception(ClientRequestInfo paramClientRequestInfo)
    throws ForwardRequest;
  
  public abstract void receive_other(ClientRequestInfo paramClientRequestInfo)
    throws ForwardRequest;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ClientRequestInterceptorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */