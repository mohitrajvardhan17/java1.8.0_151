package com.sun.org.omg.CORBA;

import org.omg.CORBA.StructMember;
import org.omg.CORBA.portable.IDLEntity;

public final class Initializer
  implements IDLEntity
{
  public StructMember[] members = null;
  public String name = null;
  
  public Initializer() {}
  
  public Initializer(StructMember[] paramArrayOfStructMember, String paramString)
  {
    members = paramArrayOfStructMember;
    name = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\Initializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */