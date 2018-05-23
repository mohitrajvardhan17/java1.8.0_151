package org.omg.stub.java.rmi;

import java.rmi.Remote;
import javax.rmi.CORBA.Stub;

public final class _Remote_Stub
  extends Stub
  implements Remote
{
  private static final String[] _type_ids = { "" };
  
  public _Remote_Stub() {}
  
  public String[] _ids()
  {
    return (String[])_type_ids.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\stub\java\rmi\_Remote_Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */