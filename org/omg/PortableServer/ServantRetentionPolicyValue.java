package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ServantRetentionPolicyValue
  implements IDLEntity
{
  private int __value;
  private static int __size = 2;
  private static ServantRetentionPolicyValue[] __array = new ServantRetentionPolicyValue[__size];
  public static final int _RETAIN = 0;
  public static final ServantRetentionPolicyValue RETAIN = new ServantRetentionPolicyValue(0);
  public static final int _NON_RETAIN = 1;
  public static final ServantRetentionPolicyValue NON_RETAIN = new ServantRetentionPolicyValue(1);
  
  public int value()
  {
    return __value;
  }
  
  public static ServantRetentionPolicyValue from_int(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < __size)) {
      return __array[paramInt];
    }
    throw new BAD_PARAM();
  }
  
  protected ServantRetentionPolicyValue(int paramInt)
  {
    __value = paramInt;
    __array[__value] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\ServantRetentionPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */