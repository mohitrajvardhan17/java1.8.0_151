package org.omg.Dynamic;

import org.omg.CORBA.Any;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.portable.IDLEntity;

public final class Parameter
  implements IDLEntity
{
  public Any argument = null;
  public ParameterMode mode = null;
  
  public Parameter() {}
  
  public Parameter(Any paramAny, ParameterMode paramParameterMode)
  {
    argument = paramAny;
    mode = paramParameterMode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\Dynamic\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */