package javax.lang.model.util;

import java.util.Iterator;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ElementScanner6<R, P>
  extends AbstractElementVisitor6<R, P>
{
  protected final R DEFAULT_VALUE;
  
  protected ElementScanner6()
  {
    DEFAULT_VALUE = null;
  }
  
  protected ElementScanner6(R paramR)
  {
    DEFAULT_VALUE = paramR;
  }
  
  public final R scan(Iterable<? extends Element> paramIterable, P paramP)
  {
    Object localObject = DEFAULT_VALUE;
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      localObject = scan(localElement, paramP);
    }
    return (R)localObject;
  }
  
  public R scan(Element paramElement, P paramP)
  {
    return (R)paramElement.accept(this, paramP);
  }
  
  public final R scan(Element paramElement)
  {
    return (R)scan(paramElement, null);
  }
  
  public R visitPackage(PackageElement paramPackageElement, P paramP)
  {
    return (R)scan(paramPackageElement.getEnclosedElements(), paramP);
  }
  
  public R visitType(TypeElement paramTypeElement, P paramP)
  {
    return (R)scan(paramTypeElement.getEnclosedElements(), paramP);
  }
  
  public R visitVariable(VariableElement paramVariableElement, P paramP)
  {
    if (paramVariableElement.getKind() != ElementKind.RESOURCE_VARIABLE) {
      return (R)scan(paramVariableElement.getEnclosedElements(), paramP);
    }
    return (R)visitUnknown(paramVariableElement, paramP);
  }
  
  public R visitExecutable(ExecutableElement paramExecutableElement, P paramP)
  {
    return (R)scan(paramExecutableElement.getParameters(), paramP);
  }
  
  public R visitTypeParameter(TypeParameterElement paramTypeParameterElement, P paramP)
  {
    return (R)scan(paramTypeParameterElement.getEnclosedElements(), paramP);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\ElementScanner6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */