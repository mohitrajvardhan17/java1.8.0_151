package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public abstract interface ExecutableElement
  extends Element, Parameterizable
{
  public abstract List<? extends TypeParameterElement> getTypeParameters();
  
  public abstract TypeMirror getReturnType();
  
  public abstract List<? extends VariableElement> getParameters();
  
  public abstract TypeMirror getReceiverType();
  
  public abstract boolean isVarArgs();
  
  public abstract boolean isDefault();
  
  public abstract List<? extends TypeMirror> getThrownTypes();
  
  public abstract AnnotationValue getDefaultValue();
  
  public abstract Name getSimpleName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\ExecutableElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */