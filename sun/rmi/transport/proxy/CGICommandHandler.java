package sun.rmi.transport.proxy;

abstract interface CGICommandHandler
{
  public abstract String getName();
  
  public abstract void execute(String paramString)
    throws CGIClientException, CGIServerException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\CGICommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */