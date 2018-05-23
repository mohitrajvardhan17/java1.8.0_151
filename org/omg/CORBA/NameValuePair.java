package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class NameValuePair
  implements IDLEntity
{
  public String id;
  public Any value;
  
  public NameValuePair() {}
  
  public NameValuePair(String paramString, Any paramAny)
  {
    id = paramString;
    value = paramAny;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\NameValuePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */