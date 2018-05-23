package javax.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public abstract interface RoundEnvironment
{
  public abstract boolean processingOver();
  
  public abstract boolean errorRaised();
  
  public abstract Set<? extends Element> getRootElements();
  
  public abstract Set<? extends Element> getElementsAnnotatedWith(TypeElement paramTypeElement);
  
  public abstract Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\annotation\processing\RoundEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */