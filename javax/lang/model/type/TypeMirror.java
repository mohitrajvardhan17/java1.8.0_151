package javax.lang.model.type;

import javax.lang.model.AnnotatedConstruct;

public abstract interface TypeMirror
  extends AnnotatedConstruct
{
  public abstract TypeKind getKind();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
  
  public abstract <R, P> R accept(TypeVisitor<R, P> paramTypeVisitor, P paramP);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\TypeMirror.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */