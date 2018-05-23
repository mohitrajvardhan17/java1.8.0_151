package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class LocalVariableAnnotationNode
  extends TypeAnnotationNode
{
  public List<LabelNode> start;
  public List<LabelNode> end;
  public List<Integer> index;
  
  public LocalVariableAnnotationNode(int paramInt, TypePath paramTypePath, LabelNode[] paramArrayOfLabelNode1, LabelNode[] paramArrayOfLabelNode2, int[] paramArrayOfInt, String paramString)
  {
    this(327680, paramInt, paramTypePath, paramArrayOfLabelNode1, paramArrayOfLabelNode2, paramArrayOfInt, paramString);
  }
  
  public LocalVariableAnnotationNode(int paramInt1, int paramInt2, TypePath paramTypePath, LabelNode[] paramArrayOfLabelNode1, LabelNode[] paramArrayOfLabelNode2, int[] paramArrayOfInt, String paramString)
  {
    super(paramInt1, paramInt2, paramTypePath, paramString);
    start = new ArrayList(paramArrayOfLabelNode1.length);
    start.addAll(Arrays.asList(paramArrayOfLabelNode1));
    end = new ArrayList(paramArrayOfLabelNode2.length);
    end.addAll(Arrays.asList(paramArrayOfLabelNode2));
    index = new ArrayList(paramArrayOfInt.length);
    for (int k : paramArrayOfInt) {
      index.add(Integer.valueOf(k));
    }
  }
  
  public void accept(MethodVisitor paramMethodVisitor, boolean paramBoolean)
  {
    Label[] arrayOfLabel1 = new Label[start.size()];
    Label[] arrayOfLabel2 = new Label[end.size()];
    int[] arrayOfInt = new int[index.size()];
    for (int i = 0; i < arrayOfLabel1.length; i++)
    {
      arrayOfLabel1[i] = ((LabelNode)start.get(i)).getLabel();
      arrayOfLabel2[i] = ((LabelNode)end.get(i)).getLabel();
      arrayOfInt[i] = ((Integer)index.get(i)).intValue();
    }
    accept(paramMethodVisitor.visitLocalVariableAnnotation(typeRef, typePath, arrayOfLabel1, arrayOfLabel2, arrayOfInt, desc, true));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\LocalVariableAnnotationNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */