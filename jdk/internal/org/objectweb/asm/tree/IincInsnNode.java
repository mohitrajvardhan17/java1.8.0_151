package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class IincInsnNode
  extends AbstractInsnNode
{
  public int var;
  public int incr;
  
  public IincInsnNode(int paramInt1, int paramInt2)
  {
    super(132);
    var = paramInt1;
    incr = paramInt2;
  }
  
  public int getType()
  {
    return 10;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitIincInsn(var, incr);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new IincInsnNode(var, incr).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\IincInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */