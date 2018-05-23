package javax.lang.model.type;

import java.util.List;

public abstract interface UnionType
  extends TypeMirror
{
  public abstract List<? extends TypeMirror> getAlternatives();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\type\UnionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */