package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class JumpInsnNode
  extends AbstractInsnNode
{
  public LabelNode label;
  
  public JumpInsnNode(int paramInt, LabelNode paramLabelNode)
  {
    super(paramInt);
    label = paramLabelNode;
  }
  
  public void setOpcode(int paramInt)
  {
    opcode = paramInt;
  }
  
  public int getType()
  {
    return 7;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitJumpInsn(opcode, label.getLabel());
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new JumpInsnNode(opcode, clone(label, paramMap)).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\JumpInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */