package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class CompletionStatus
  implements IDLEntity
{
  public static final int _COMPLETED_YES = 0;
  public static final int _COMPLETED_NO = 1;
  public static final int _COMPLETED_MAYBE = 2;
  public static final CompletionStatus COMPLETED_YES = new CompletionStatus(0);
  public static final CompletionStatus COMPLETED_NO = new CompletionStatus(1);
  public static final CompletionStatus COMPLETED_MAYBE = new CompletionStatus(2);
  private int _value;
  
  public int value()
  {
    return _value;
  }
  
  public static CompletionStatus from_int(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return COMPLETED_YES;
    case 1: 
      return COMPLETED_NO;
    case 2: 
      return COMPLETED_MAYBE;
    }
    throw new BAD_PARAM();
  }
  
  private CompletionStatus(int paramInt)
  {
    _value = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\CompletionStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */