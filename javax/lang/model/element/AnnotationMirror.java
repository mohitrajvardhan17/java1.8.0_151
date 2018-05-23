package javax.lang.model.element;

import java.util.Map;
import javax.lang.model.type.DeclaredType;

public abstract interface AnnotationMirror
{
  public abstract DeclaredType getAnnotationType();
  
  public abstract Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\AnnotationMirror.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */