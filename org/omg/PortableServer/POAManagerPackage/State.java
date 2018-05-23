package org.omg.PortableServer.POAManagerPackage;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class State
  implements IDLEntity
{
  private int __value;
  private static int __size = 4;
  private static State[] __array = new State[__size];
  public static final int _HOLDING = 0;
  public static final State HOLDING = new State(0);
  public static final int _ACTIVE = 1;
  public static final State ACTIVE = new State(1);
  public static final int _DISCARDING = 2;
  public static final State DISCARDING = new State(2);
  public static final int _INACTIVE = 3;
  public static final State INACTIVE = new State(3);
  
  public int value()
  {
    return __value;
  }
  
  public static State from_int(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < __size)) {
      return __array[paramInt];
    }
    throw new BAD_PARAM();
  }
  
  protected State(int paramInt)
  {
    __value = paramInt;
    __array[__value] = this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAManagerPackage\State.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */