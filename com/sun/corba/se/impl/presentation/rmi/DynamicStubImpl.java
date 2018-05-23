package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.RepositoryIdCache;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class DynamicStubImpl
  extends ObjectImpl
  implements DynamicStub, Serializable
{
  private static final long serialVersionUID = 4852612040012087675L;
  private String[] typeIds;
  private StubIORImpl ior;
  private DynamicStub self = null;
  
  public void setSelf(DynamicStub paramDynamicStub)
  {
    self = paramDynamicStub;
  }
  
  public DynamicStub getSelf()
  {
    return self;
  }
  
  public DynamicStubImpl(String[] paramArrayOfString)
  {
    typeIds = paramArrayOfString;
    ior = null;
  }
  
  public void setDelegate(Delegate paramDelegate)
  {
    _set_delegate(paramDelegate);
  }
  
  public Delegate getDelegate()
  {
    return _get_delegate();
  }
  
  public org.omg.CORBA.ORB getORB()
  {
    return _orb();
  }
  
  public String[] _ids()
  {
    return typeIds;
  }
  
  public String[] getTypeIds()
  {
    return _ids();
  }
  
  public void connect(org.omg.CORBA.ORB paramORB)
    throws RemoteException
  {
    ior = StubConnectImpl.connect(ior, self, this, paramORB);
  }
  
  public boolean isLocal()
  {
    return _is_local();
  }
  
  public OutputStream request(String paramString, boolean paramBoolean)
  {
    return _request(paramString, paramBoolean);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ior = new StubIORImpl();
    ior.doRead(paramObjectInputStream);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (ior == null) {
      ior = new StubIORImpl(this);
    }
    ior.doWrite(paramObjectOutputStream);
  }
  
  public Object readResolve()
  {
    String str1 = ior.getRepositoryId();
    String str2 = RepositoryId.cache.getId(str1).getClassName();
    Class localClass = null;
    try
    {
      localClass = JDKBridge.loadClass(str2, null, null);
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    PresentationManager localPresentationManager = com.sun.corba.se.spi.orb.ORB.getPresentationManager();
    PresentationManager.ClassData localClassData = localPresentationManager.getClassData(localClass);
    InvocationHandlerFactoryImpl localInvocationHandlerFactoryImpl = (InvocationHandlerFactoryImpl)localClassData.getInvocationHandlerFactory();
    return localInvocationHandlerFactoryImpl.getInvocationHandler(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\DynamicStubImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */