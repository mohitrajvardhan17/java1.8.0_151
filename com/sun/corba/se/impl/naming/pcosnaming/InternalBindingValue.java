package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.Serializable;
import org.omg.CosNaming.BindingType;

public class InternalBindingValue
  implements Serializable
{
  public BindingType theBindingType;
  public String strObjectRef;
  private transient org.omg.CORBA.Object theObjectRef;
  
  public InternalBindingValue() {}
  
  public InternalBindingValue(BindingType paramBindingType, String paramString)
  {
    theBindingType = paramBindingType;
    strObjectRef = paramString;
  }
  
  public org.omg.CORBA.Object getObjectRef()
  {
    return theObjectRef;
  }
  
  public void setObjectRef(org.omg.CORBA.Object paramObject)
  {
    theObjectRef = paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\InternalBindingValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */