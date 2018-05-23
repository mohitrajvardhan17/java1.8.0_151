package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public abstract interface TypeParameterElement
  extends Element
{
  public abstract Element getGenericElement();
  
  public abstract List<? extends TypeMirror> getBounds();
  
  public abstract Element getEnclosingElement();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\TypeParameterElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */