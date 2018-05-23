package javax.lang.model;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;

public abstract interface AnnotatedConstruct
{
  public abstract List<? extends AnnotationMirror> getAnnotationMirrors();
  
  public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
  
  public abstract <A extends Annotation> A[] getAnnotationsByType(Class<A> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\AnnotatedConstruct.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */