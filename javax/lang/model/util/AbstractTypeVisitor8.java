package javax.lang.model.util;

import javax.lang.model.type.IntersectionType;

public abstract class AbstractTypeVisitor8<R, P>
  extends AbstractTypeVisitor7<R, P>
{
  protected AbstractTypeVisitor8() {}
  
  public abstract R visitIntersection(IntersectionType paramIntersectionType, P paramP);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\AbstractTypeVisitor8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */