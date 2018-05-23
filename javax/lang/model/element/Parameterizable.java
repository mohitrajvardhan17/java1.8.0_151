package javax.lang.model.element;

import java.util.List;

public abstract interface Parameterizable
  extends Element
{
  public abstract List<? extends TypeParameterElement> getTypeParameters();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\Parameterizable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */