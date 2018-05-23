package java.rmi.dgc;

import java.io.Serializable;

public final class Lease
  implements Serializable
{
  private VMID vmid;
  private long value;
  private static final long serialVersionUID = -5713411624328831948L;
  
  public Lease(VMID paramVMID, long paramLong)
  {
    vmid = paramVMID;
    value = paramLong;
  }
  
  public VMID getVMID()
  {
    return vmid;
  }
  
  public long getValue()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\dgc\Lease.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */