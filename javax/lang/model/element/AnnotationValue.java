package javax.lang.model.element;

public abstract interface AnnotationValue
{
  public abstract Object getValue();
  
  public abstract String toString();
  
  public abstract <R, P> R accept(AnnotationValueVisitor<R, P> paramAnnotationValueVisitor, P paramP);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\AnnotationValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */