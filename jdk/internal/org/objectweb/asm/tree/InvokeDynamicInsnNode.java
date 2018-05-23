package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class InvokeDynamicInsnNode
  extends AbstractInsnNode
{
  public String name;
  public String desc;
  public Handle bsm;
  public Object[] bsmArgs;
  
  public InvokeDynamicInsnNode(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    super(186);
    name = paramString1;
    desc = paramString2;
    bsm = paramHandle;
    bsmArgs = paramVarArgs;
  }
  
  public int getType()
  {
    return 6;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\InvokeDynamicInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */