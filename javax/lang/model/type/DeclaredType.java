package javax.lang.model.type;

import java.util.List;
import javax.lang.model.element.Element;

public abstract interface DeclaredType
  extends ReferenceType
{
  public abstract Element asElement();
  
  public abstract TypeMirror getEnclosingType();
  
  public abstract List<? extends TypeMirror> getTypeArguments();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\DeclaredType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */