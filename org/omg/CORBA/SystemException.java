package org.omg.CORBA;

public abstract class SystemException
  extends RuntimeException
{
  public int minor;
  public CompletionStatus completed;
  
  protected SystemException(String paramString, int paramInt, CompletionStatus paramCompletionStatus)
  {
    super(paramString);
    minor = paramInt;
    completed = paramCompletionStatus;
  }
  
  public String toString()
  {
    String str = super.toString();
    int i = minor & 0xF000;
    switch (i)
    {
    case 1330446336: 
      str = str + "  vmcid: OMG";
      break;
    case 1398079488: 
      str = str + "  vmcid: SUN";
      break;
    default: 
      str = str + "  vmcid: 0x" + Integer.toHexString(i);
    }
    int j = minor & 0xFFF;
    str = str + "  minor code: " + j;
    switch (completed.value())
    {
    case 0: 
      str = str + "  completed: Yes";
      break;
    case 1: 
      str = str + "  completed: No";
      break;
    case 2: 
    default: 
      str = str + " completed: Maybe";
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\SystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */