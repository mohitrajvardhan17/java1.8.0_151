package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LineNumberNode
  extends AbstractInsnNode
{
  public int line;
  public LabelNode start;
  
  public LineNumberNode(int paramInt, LabelNode paramLabelNode)
  {
    super(-1);
    line = paramInt;
    start = paramLabelNode;
  }
  
  public int getType()
  {
    return 15;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitLineNumber(line, start.getLabel());
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new LineNumberNode(line, clone(start, paramMap));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\LineNumberNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */