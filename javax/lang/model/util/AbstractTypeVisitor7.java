package javax.lang.model.util;

import javax.lang.model.type.UnionType;

public abstract class AbstractTypeVisitor7<R, P>
  extends AbstractTypeVisitor6<R, P>
{
  protected AbstractTypeVisitor7() {}
  
  public abstract R visitUnion(UnionType paramUnionType, P paramP);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\AbstractTypeVisitor7.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */