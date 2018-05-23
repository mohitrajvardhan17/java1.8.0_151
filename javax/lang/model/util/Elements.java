package javax.lang.model.util;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public abstract interface Elements
{
  public abstract PackageElement getPackageElement(CharSequence paramCharSequence);
  
  public abstract TypeElement getTypeElement(CharSequence paramCharSequence);
  
  public abstract Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror paramAnnotationMirror);
  
  public abstract String getDocComment(Element paramElement);
  
  public abstract boolean isDeprecated(Element paramElement);
  
  public abstract Name getBinaryName(TypeElement paramTypeElement);
  
  public abstract PackageElement getPackageOf(Element paramElement);
  
  public abstract List<? extends Element> getAllMembers(TypeElement paramTypeElement);
  
  public abstract List<? extends AnnotationMirror> getAllAnnotationMirrors(Element paramElement);
  
  public abstract boolean hides(Element paramElement1, Element paramElement2);
  
  public abstract boolean overrides(ExecutableElement paramExecutableElement1, ExecutableElement paramExecutableElement2, TypeElement paramTypeElement);
  
  public abstract String getConstantExpression(Object paramObject);
  
  public abstract void printElements(Writer paramWriter, Element... paramVarArgs);
  
  public abstract Name getName(CharSequence paramCharSequence);
  
  public abstract boolean isFunctionalInterface(TypeElement paramTypeElement);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\Elements.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */