package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class TypeInsnNode
  extends AbstractInsnNode
{
  public String desc;
  
  public TypeInsnNode(int paramInt, String paramString)
  {
    super(paramInt);
    desc = paramString;
  }
  
  public void setOpcode(int paramInt)
  {
    opcode = paramInt;
  }
  
  public int getType()
  {
    return 3;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitTypeInsn(opcode, desc);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new TypeInsnNode(opcode, desc).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\TypeInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */