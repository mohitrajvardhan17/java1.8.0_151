package java.rmi.server;

@Deprecated
public abstract class RemoteStub
  extends RemoteObject
{
  private static final long serialVersionUID = -1585587260594494182L;
  
  protected RemoteStub() {}
  
  protected RemoteStub(RemoteRef paramRemoteRef)
  {
    super(paramRemoteRef);
  }
  
  @Deprecated
  protected static void setRef(RemoteStub paramRemoteStub, RemoteRef paramRemoteRef)
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RemoteStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */