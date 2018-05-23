package javax.lang.model.element;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.type.TypeMirror;

public abstract interface Element
  extends AnnotatedConstruct
{
  public abstract TypeMirror asType();
  
  public abstract ElementKind getKind();
  
  public abstract Set<Modifier> getModifiers();
  
  public abstract Name getSimpleName();
  
  public abstract Element getEnclosingElement();
  
  public abstract List<? extends Element> getEnclosedElements();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract List<? extends AnnotationMirror> getAnnotationMirrors();
  
  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
  
  public abstract <R, P> R accept(ElementVisitor<R, P> paramElementVisitor, P paramP);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */