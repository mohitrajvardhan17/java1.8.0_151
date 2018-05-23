package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ElementKindVisitor7<R, P>
  extends ElementKindVisitor6<R, P>
{
  protected ElementKindVisitor7()
  {
    super(null);
  }
  
  protected ElementKindVisitor7(R paramR)
  {
    super(paramR);
  }
  
  public R visitVariableAsResourceVariable(VariableElement paramVariableElement, P paramP)
  {
    return (R)defaultAction(paramVariableElement, paramP);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\ElementKindVisitor7.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */