package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.Binding;

public class InternalBindingValue
{
  public Binding theBinding;
  public String strObjectRef;
  public org.omg.CORBA.Object theObjectRef;
  
  public InternalBindingValue() {}
  
  public InternalBindingValue(Binding paramBinding, String paramString)
  {
    theBinding = paramBinding;
    strObjectRef = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\InternalBindingValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */