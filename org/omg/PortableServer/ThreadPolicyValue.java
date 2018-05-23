package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ThreadPolicyValue
  implements IDLEntity
{
  private int __value;
  private static int __size = 2;
  private static ThreadPolicyValue[] __array = new ThreadPolicyValue[__size];
  public static final int _ORB_CTRL_MODEL = 0;
  public static final ThreadPolicyValue ORB_CTRL_MODEL = new ThreadPolicyValue(0);
  public static final int _SINGLE_THREAD_MODEL = 1;
  public static final ThreadPolicyValue SINGLE_THREAD_MODEL = new ThreadPolicyValue(1);
  
  public int value()
  {
    return __value;
  }
  
  public static ThreadPolicyValue from_int(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < __size)) {
      return __array[paramInt];
    }
    throw new BAD_PARAM();
  }
  
  protected ThreadPolicyValue(int paramInt)
  {
    __value = paramInt;
    __array[__value] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\ThreadPolicyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */