package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class VarInsnNode
  extends AbstractInsnNode
{
  public int var;
  
  public VarInsnNode(int paramInt1, int paramInt2)
  {
    super(paramInt1);
    var = paramInt2;
  }
  
  public void setOpcode(int paramInt)
  {
    opcode = paramInt;
  }
  
  public int getType()
  {
    return 2;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitVarInsn(opcode, var);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new VarInsnNode(opcode, var).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\VarInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */