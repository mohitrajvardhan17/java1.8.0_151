package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LookupSwitchInsnNode
  extends AbstractInsnNode
{
  public LabelNode dflt;
  public List<Integer> keys;
  public List<LabelNode> labels;
  
  public LookupSwitchInsnNode(LabelNode paramLabelNode, int[] paramArrayOfInt, LabelNode[] paramArrayOfLabelNode)
  {
    super(171);
    dflt = paramLabelNode;
    keys = new ArrayList(paramArrayOfInt == null ? 0 : paramArrayOfInt.length);
    labels = new ArrayList(paramArrayOfLabelNode == null ? 0 : paramArrayOfLabelNode.length);
    if (paramArrayOfInt != null) {
      for (int i = 0; i < paramArrayOfInt.length; i++) {
        keys.add(Integer.valueOf(paramArrayOfInt[i]));
      }
    }
    if (paramArrayOfLabelNode != null) {
      labels.addAll(Arrays.asList(paramArrayOfLabelNode));
    }
  }
  
  public int getType()
  {
    return 12;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    int[] arrayOfInt = new int[keys.size()];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = ((Integer)keys.get(i)).intValue();
    }
    Label[] arrayOfLabel = new Label[labels.size()];
    for (int j = 0; j < arrayOfLabel.length; j++) {
      arrayOfLabel[j] = ((LabelNode)labels.get(j)).getLabel();
    }
    paramMethodVisitor.visitLookupSwitchInsn(dflt.getLabel(), arrayOfInt, arrayOfLabel);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    LookupSwitchInsnNode localLookupSwitchInsnNode = new LookupSwitchInsnNode(clone(dflt, paramMap), null, clone(labels, paramMap));
    keys.addAll(keys);
    return localLookupSwitchInsnNode.cloneAnnotations(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\LookupSwitchInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */