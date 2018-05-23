package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class InsnNode
  extends AbstractInsnNode
{
  public InsnNode(int paramInt)
  {
    super(paramInt);
  }
  
  public int getType()
  {
    return 0;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitInsn(opcode);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new InsnNode(opcode).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\InsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */