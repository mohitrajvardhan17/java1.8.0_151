package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class IdAssignmentPolicyValue
  implements IDLEntity
{
  private int __value;
  private static int __size = 2;
  private static IdAssignmentPolicyValue[] __array = new IdAssignmentPolicyValue[__size];
  public static final int _USER_ID = 0;
  public static final IdAssignmentPolicyValue USER_ID = new IdAssignmentPolicyValue(0);
  public static final int _SYSTEM_ID = 1;
  public static final IdAssignmentPolicyValue SYSTEM_ID = new IdAssignmentPolicyValue(1);
  
  public int value()
  {
    return __value;
  }
  
  public static IdAssignmentPolicyValue from_int(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < __size)) {
      return __array[paramInt];
    }
    throw new BAD_PARAM();
  }
  
  protected IdAssignmentPolicyValue(int paramInt)
  {
    __value = paramInt;
    __array[__value] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\IdAssignmentPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */