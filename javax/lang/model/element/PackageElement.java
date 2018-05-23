package javax.lang.model.element;

import java.util.List;

public abstract interface PackageElement
  extends Element, QualifiedNameable
{
  public abstract Name getQualifiedName();
  
  public abstract Name getSimpleName();
  
  public abstract List<? extends Element> getEnclosedElements();
  
  public abstract boolean isUnnamed();
  
  public abstract Element getEnclosingElement();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\PackageElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */