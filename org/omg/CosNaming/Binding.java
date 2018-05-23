package org.omg.CosNaming;

import org.omg.CORBA.portable.IDLEntity;

public final class Binding
  implements IDLEntity
{
  public NameComponent[] binding_name = null;
  public BindingType binding_type = null;
  
  public Binding() {}
  
  public Binding(NameComponent[] paramArrayOfNameComponent, BindingType paramBindingType)
  {
    binding_name = paramArrayOfNameComponent;
    binding_type = paramBindingType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\Binding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */