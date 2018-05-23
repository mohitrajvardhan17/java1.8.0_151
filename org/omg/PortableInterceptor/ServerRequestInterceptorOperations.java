package org.omg.PortableInterceptor;

public abstract interface ServerRequestInterceptorOperations
  extends InterceptorOperations
{
  public abstract void receive_request_service_contexts(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
  
  public abstract void receive_request(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
  
  public abstract void send_reply(ServerRequestInfo paramServerRequestInfo);
  
  public abstract void send_exception(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
  
  public abstract void send_other(ServerRequestInfo paramServerRequestInfo)
    throws ForwardRequest;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ServerRequestInterceptorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */