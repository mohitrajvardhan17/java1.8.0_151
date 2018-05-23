package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LocalVariableNode
{
  public String name;
  public String desc;
  public String signature;
  public LabelNode start;
  public LabelNode end;
  public int index;
  
  public LocalVariableNode(String paramString1, String paramString2, String paramString3, LabelNode paramLabelNode1, LabelNode paramLabelNode2, int paramInt)
  {
    name = paramString1;
    desc = paramString2;
    signature = paramString3;
    start = paramLabelNode1;
    end = paramLabelNode2;
    index = paramInt;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitLocalVariable(name, desc, signature, start.getLabel(), end.getLabel(), index);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\LocalVariableNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */