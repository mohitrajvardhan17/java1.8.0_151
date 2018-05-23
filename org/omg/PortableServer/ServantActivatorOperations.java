package org.omg.PortableServer;

public abstract interface ServantActivatorOperations
  extends ServantManagerOperations
{
  public abstract Servant incarnate(byte[] paramArrayOfByte, POA paramPOA)
    throws ForwardRequest;
  
  public abstract void etherealize(byte[] paramArrayOfByte, POA paramPOA, Servant paramServant, boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\ServantActivatorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */