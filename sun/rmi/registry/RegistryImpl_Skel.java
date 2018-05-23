package sun.rmi.registry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.UnmarshalException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonMismatchException;

public final class RegistryImpl_Skel
  implements Skeleton
{
  private static final Operation[] operations = { new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)") };
  private static final long interfaceHash = 4905912898345647071L;
  
  public RegistryImpl_Skel() {}
  
  public Operation[] getOperations()
  {
    return (Operation[])operations.clone();
  }
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt, long paramLong)
    throws Exception
  {
    if (paramLong != 4905912898345647071L) {
      throw new SkeletonMismatchException("interface hash mismatch");
    }
    RegistryImpl localRegistryImpl = (RegistryImpl)paramRemote;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    switch (paramInt)
    {
    case 0: 
      RegistryImpl.checkAccess("Registry.bind");
      try
      {
        ObjectInput localObjectInput2 = paramRemoteCall.getInputStream();
        localObject1 = (String)localObjectInput2.readObject();
        localObject2 = (Remote)localObjectInput2.readObject();
      }
      catch (IOException|ClassNotFoundException localIOException5)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException5);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localRegistryImpl.bind((String)localObject1, (Remote)localObject2);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException6)
      {
        throw new MarshalException("error marshalling return", localIOException6);
      }
    case 1: 
      paramRemoteCall.releaseInputStream();
      localObject1 = localRegistryImpl.list();
      try
      {
        localObject2 = paramRemoteCall.getResultStream(true);
        ((ObjectOutput)localObject2).writeObject(localObject1);
      }
      catch (IOException localIOException1)
      {
        throw new MarshalException("error marshalling return", localIOException1);
      }
    case 2: 
      try
      {
        ObjectInput localObjectInput1 = paramRemoteCall.getInputStream();
        localObject1 = (String)localObjectInput1.readObject();
      }
      catch (IOException|ClassNotFoundException localIOException2)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException2);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localObject3 = localRegistryImpl.lookup((String)localObject1);
      try
      {
        ObjectOutput localObjectOutput = paramRemoteCall.getResultStream(true);
        localObjectOutput.writeObject(localObject3);
      }
      catch (IOException localIOException7)
      {
        throw new MarshalException("error marshalling return", localIOException7);
      }
    case 3: 
      RegistryImpl.checkAccess("Registry.rebind");
      try
      {
        ObjectInput localObjectInput3 = paramRemoteCall.getInputStream();
        localObject1 = (String)localObjectInput3.readObject();
        localObject3 = (Remote)localObjectInput3.readObject();
      }
      catch (IOException|ClassNotFoundException localIOException8)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException8);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localRegistryImpl.rebind((String)localObject1, (Remote)localObject3);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException9)
      {
        throw new MarshalException("error marshalling return", localIOException9);
      }
    case 4: 
      RegistryImpl.checkAccess("Registry.unbind");
      try
      {
        localObject3 = paramRemoteCall.getInputStream();
        localObject1 = (String)((ObjectInput)localObject3).readObject();
      }
      catch (IOException|ClassNotFoundException localIOException3)
      {
        throw new UnmarshalException("error unmarshalling arguments", localIOException3);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      localRegistryImpl.unbind((String)localObject1);
      try
      {
        paramRemoteCall.getResultStream(true);
      }
      catch (IOException localIOException4)
      {
        throw new MarshalException("error marshalling return", localIOException4);
      }
    default: 
      throw new UnmarshalException("invalid method number");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\registry\RegistryImpl_Skel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */