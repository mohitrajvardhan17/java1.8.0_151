package javax.lang.model.type;

import java.util.List;

public abstract interface ExecutableType
  extends TypeMirror
{
  public abstract List<? extends TypeVariable> getTypeVariables();
  
  public abstract TypeMirror getReturnType();
  
  public abstract List<? extends TypeMirror> getParameterTypes();
  
  public abstract TypeMirror getReceiverType();
  
  public abstract List<? extends TypeMirror> getThrownTypes();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\ExecutableType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */