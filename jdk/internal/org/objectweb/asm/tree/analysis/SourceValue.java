package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.Set;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

public class SourceValue
  implements Value
{
  public final int size;
  public final Set<AbstractInsnNode> insns;
  
  public SourceValue(int paramInt)
  {
    this(paramInt, SmallSet.emptySet());
  }
  
  public SourceValue(int paramInt, AbstractInsnNode paramAbstractInsnNode)
  {
    size = paramInt;
    insns = new SmallSet(paramAbstractInsnNode, null);
  }
  
  public SourceValue(int paramInt, Set<AbstractInsnNode> paramSet)
  {
    size = paramInt;
    insns = paramSet;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof SourceValue)) {
      return false;
    }
    SourceValue localSourceValue = (SourceValue)paramObject;
    return (size == size) && (insns.equals(insns));
  }
  
  public int hashCode()
  {
    return insns.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\SourceValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */