package javax.lang.model.type;

import javax.lang.model.element.Element;

public abstract interface TypeVariable
  extends ReferenceType
{
  public abstract Element asElement();
  
  public abstract TypeMirror getUpperBound();
  
  public abstract TypeMirror getLowerBound();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\TypeVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */