package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class FieldInsnNode
  extends AbstractInsnNode
{
  public String owner;
  public String name;
  public String desc;
  
  public FieldInsnNode(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    super(paramInt);
    owner = paramString1;
    name = paramString2;
    desc = paramString3;
  }
  
  public void setOpcode(int paramInt)
  {
    opcode = paramInt;
  }
  
  public int getType()
  {
    return 4;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitFieldInsn(opcode, owner, name, desc);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new FieldInsnNode(opcode, owner, name, desc).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\FieldInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */