package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LabelNode
  extends AbstractInsnNode
{
  private Label label;
  
  public LabelNode()
  {
    super(-1);
  }
  
  public LabelNode(Label paramLabel)
  {
    super(-1);
    label = paramLabel;
  }
  
  public int getType()
  {
    return 8;
  }
  
  public Label getLabel()
  {
    if (label == null) {
      label = new Label();
    }
    return label;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitLabel(getLabel());
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return (AbstractInsnNode)paramMap.get(this);
  }
  
  public void resetLabel()
  {
    label = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\LabelNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */