package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class MethodInsnNode
  extends AbstractInsnNode
{
  public String owner;
  public String name;
  public String desc;
  public boolean itf;
  
  @Deprecated
  public MethodInsnNode(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this(paramInt, paramString1, paramString2, paramString3, paramInt == 185);
  }
  
  public MethodInsnNode(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    super(paramInt);
    owner = paramString1;
    name = paramString2;
    desc = paramString3;
    itf = paramBoolean;
  }
  
  public void setOpcode(int paramInt)
  {
    opcode = paramInt;
  }
  
  public int getType()
  {
    return 5;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitMethodInsn(opcode, owner, name, desc, itf);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new MethodInsnNode(opcode, owner, name, desc, itf);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\MethodInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */