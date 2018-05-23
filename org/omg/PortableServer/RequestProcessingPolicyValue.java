package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class RequestProcessingPolicyValue
  implements IDLEntity
{
  private int __value;
  private static int __size = 3;
  private static RequestProcessingPolicyValue[] __array = new RequestProcessingPolicyValue[__size];
  public static final int _USE_ACTIVE_OBJECT_MAP_ONLY = 0;
  public static final RequestProcessingPolicyValue USE_ACTIVE_OBJECT_MAP_ONLY = new RequestProcessingPolicyValue(0);
  public static final int _USE_DEFAULT_SERVANT = 1;
  public static final RequestProcessingPolicyValue USE_DEFAULT_SERVANT = new RequestProcessingPolicyValue(1);
  public static final int _USE_SERVANT_MANAGER = 2;
  public static final RequestProcessingPolicyValue USE_SERVANT_MANAGER = new RequestProcessingPolicyValue(2);
  
  public int value()
  {
    return __value;
  }
  
  public static RequestProcessingPolicyValue from_int(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < __size)) {
      return __array[paramInt];
    }
    throw new BAD_PARAM();
  }
  
  protected RequestProcessingPolicyValue(int paramInt)
  {
    __value = paramInt;
    __array[__value] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\RequestProcessingPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */