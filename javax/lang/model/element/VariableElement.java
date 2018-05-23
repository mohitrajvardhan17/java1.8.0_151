package javax.lang.model.element;

public abstract interface VariableElement
  extends Element
{
  public abstract Object getConstantValue();
  
  public abstract Name getSimpleName();
  
  public abstract Element getEnclosingElement();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\VariableElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */