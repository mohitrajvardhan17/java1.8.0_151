package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public abstract class AbstractInsnNode
{
  public static final int INSN = 0;
  public static final int INT_INSN = 1;
  public static final int VAR_INSN = 2;
  public static final int TYPE_INSN = 3;
  public static final int FIELD_INSN = 4;
  public static final int METHOD_INSN = 5;
  public static final int INVOKE_DYNAMIC_INSN = 6;
  public static final int JUMP_INSN = 7;
  public static final int LABEL = 8;
  public static final int LDC_INSN = 9;
  public static final int IINC_INSN = 10;
  public static final int TABLESWITCH_INSN = 11;
  public static final int LOOKUPSWITCH_INSN = 12;
  public static final int MULTIANEWARRAY_INSN = 13;
  public static final int FRAME = 14;
  public static final int LINE = 15;
  protected int opcode;
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  AbstractInsnNode prev;
  AbstractInsnNode next;
  int index;
  
  protected AbstractInsnNode(int paramInt)
  {
    opcode = paramInt;
    index = -1;
  }
  
  public int getOpcode()
  {
    return opcode;
  }
  
  public abstract int getType();
  
  public AbstractInsnNode getPrevious()
  {
    return prev;
  }
  
  public AbstractInsnNode getNext()
  {
    return next;
  }
  
  public abstract void accept(MethodVisitor paramMethodVisitor);
  
  protected final void acceptAnnotations(MethodVisitor paramMethodVisitor)
  {
    int i = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
    TypeAnnotationNode localTypeAnnotationNode;
    for (int j = 0; j < i; j++)
    {
      localTypeAnnotationNode = (TypeAnnotationNode)visibleTypeAnnotations.get(j);
      localTypeAnnotationNode.accept(paramMethodVisitor.visitInsnAnnotation(typeRef, typePath, desc, true));
    }
    i = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations.size();
    for (j = 0; j < i; j++)
    {
      localTypeAnnotationNode = (TypeAnnotationNode)invisibleTypeAnnotations.get(j);
      localTypeAnnotationNode.accept(paramMethodVisitor.visitInsnAnnotation(typeRef, typePath, desc, false));
    }
  }
  
  public abstract AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap);
  
  static LabelNode clone(LabelNode paramLabelNode, Map<LabelNode, LabelNode> paramMap)
  {
    return (LabelNode)paramMap.get(paramLabelNode);
  }
  
  static LabelNode[] clone(List<LabelNode> paramList, Map<LabelNode, LabelNode> paramMap)
  {
    LabelNode[] arrayOfLabelNode = new LabelNode[paramList.size()];
    for (int i = 0; i < arrayOfLabelNode.length; i++) {
      arrayOfLabelNode[i] = ((LabelNode)paramMap.get(paramList.get(i)));
    }
    return arrayOfLabelNode;
  }
  
  protected final AbstractInsnNode cloneAnnotations(AbstractInsnNode paramAbstractInsnNode)
  {
    int i;
    TypeAnnotationNode localTypeAnnotationNode1;
    TypeAnnotationNode localTypeAnnotationNode2;
    if (visibleTypeAnnotations != null)
    {
      visibleTypeAnnotations = new ArrayList();
      for (i = 0; i < visibleTypeAnnotations.size(); i++)
      {
        localTypeAnnotationNode1 = (TypeAnnotationNode)visibleTypeAnnotations.get(i);
        localTypeAnnotationNode2 = new TypeAnnotationNode(typeRef, typePath, desc);
        localTypeAnnotationNode1.accept(localTypeAnnotationNode2);
        visibleTypeAnnotations.add(localTypeAnnotationNode2);
      }
    }
    if (invisibleTypeAnnotations != null)
    {
      invisibleTypeAnnotations = new ArrayList();
      for (i = 0; i < invisibleTypeAnnotations.size(); i++)
      {
        localTypeAnnotationNode1 = (TypeAnnotationNode)invisibleTypeAnnotations.get(i);
        localTypeAnnotationNode2 = new TypeAnnotationNode(typeRef, typePath, desc);
        localTypeAnnotationNode1.accept(localTypeAnnotationNode2);
        invisibleTypeAnnotations.add(localTypeAnnotationNode2);
      }
    }
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\AbstractInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */