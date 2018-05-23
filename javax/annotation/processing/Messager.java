package javax.annotation.processing;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public abstract interface Messager
{
  public abstract void printMessage(Diagnostic.Kind paramKind, CharSequence paramCharSequence);
  
  public abstract void printMessage(Diagnostic.Kind paramKind, CharSequence paramCharSequence, Element paramElement);
  
  public abstract void printMessage(Diagnostic.Kind paramKind, CharSequence paramCharSequence, Element paramElement, AnnotationMirror paramAnnotationMirror);
  
  public abstract void printMessage(Diagnostic.Kind paramKind, CharSequence paramCharSequence, Element paramElement, AnnotationMirror paramAnnotationMirror, AnnotationValue paramAnnotationValue);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\annotation\processing\Messager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */