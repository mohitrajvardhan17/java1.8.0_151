package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class MultiANewArrayInsnNode
  extends AbstractInsnNode
{
  public String desc;
  public int dims;
  
  public MultiANewArrayInsnNode(String paramString, int paramInt)
  {
    super(197);
    desc = paramString;
    dims = paramInt;
  }
  
  public int getType()
  {
    return 13;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitMultiANewArrayInsn(desc, dims);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new MultiANewArrayInsnNode(desc, dims).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\MultiANewArrayInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */