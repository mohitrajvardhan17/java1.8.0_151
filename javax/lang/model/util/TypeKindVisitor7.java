package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.UnionType;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TypeKindVisitor7<R, P>
  extends TypeKindVisitor6<R, P>
{
  protected TypeKindVisitor7()
  {
    super(null);
  }
  
  protected TypeKindVisitor7(R paramR)
  {
    super(paramR);
  }
  
  public R visitUnion(UnionType paramUnionType, P paramP)
  {
    return (R)defaultAction(paramUnionType, paramP);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\TypeKindVisitor7.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */