package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.IntersectionType;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleTypeVisitor8<R, P>
  extends SimpleTypeVisitor7<R, P>
{
  protected SimpleTypeVisitor8()
  {
    super(null);
  }
  
  protected SimpleTypeVisitor8(R paramR)
  {
    super(paramR);
  }
  
  public R visitIntersection(IntersectionType paramIntersectionType, P paramP)
  {
    return (R)defaultAction(paramIntersectionType, paramP);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\SimpleTypeVisitor8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */