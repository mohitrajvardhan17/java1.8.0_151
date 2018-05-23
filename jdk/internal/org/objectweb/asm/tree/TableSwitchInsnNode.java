package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class TableSwitchInsnNode
  extends AbstractInsnNode
{
  public int min;
  public int max;
  public LabelNode dflt;
  public List<LabelNode> labels;
  
  public TableSwitchInsnNode(int paramInt1, int paramInt2, LabelNode paramLabelNode, LabelNode... paramVarArgs)
  {
    super(170);
    min = paramInt1;
    max = paramInt2;
    dflt = paramLabelNode;
    labels = new ArrayList();
    if (paramVarArgs != null) {
      labels.addAll(Arrays.asList(paramVarArgs));
    }
  }
  
  public int getType()
  {
    return 11;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    Label[] arrayOfLabel = new Label[labels.size()];
    for (int i = 0; i < arrayOfLabel.length; i++) {
      arrayOfLabel[i] = ((LabelNode)labels.get(i)).getLabel();
    }
    paramMethodVisitor.visitTableSwitchInsn(min, max, dflt.getLabel(), arrayOfLabel);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    return new TableSwitchInsnNode(min, max, clone(dflt, paramMap), clone(labels, paramMap)).cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\TableSwitchInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */