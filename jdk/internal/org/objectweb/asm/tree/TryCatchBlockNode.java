package jdk.internal.org.objectweb.asm.tree;

import java.util.Iterator;
import java.util.List;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class TryCatchBlockNode
{
  public LabelNode start;
  public LabelNode end;
  public LabelNode handler;
  public String type;
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public TryCatchBlockNode(LabelNode paramLabelNode1, LabelNode paramLabelNode2, LabelNode paramLabelNode3, String paramString)
  {
    start = paramLabelNode1;
    end = paramLabelNode2;
    handler = paramLabelNode3;
    type = paramString;
  }
  
  public void updateIndex(int paramInt)
  {
    int i = 0x42000000 | paramInt << 8;
    Iterator localIterator;
    TypeAnnotationNode localTypeAnnotationNode;
    if (visibleTypeAnnotations != null)
    {
      localIterator = visibleTypeAnnotations.iterator();
      while (localIterator.hasNext())
      {
        localTypeAnnotationNode = (TypeAnnotationNode)localIterator.next();
        typeRef = i;
      }
    }
    if (invisibleTypeAnnotations != null)
    {
      localIterator = invisibleTypeAnnotations.iterator();
      while (localIterator.hasNext())
      {
        localTypeAnnotationNode = (TypeAnnotationNode)localIterator.next();
        typeRef = i;
      }
    }
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    paramMethodVisitor.visitTryCatchBlock(start.getLabel(), end.getLabel(), handler == null ? null : handler.getLabel(), type);
    int i = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
    TypeAnnotationNode localTypeAnnotationNode;
    for (int j = 0; j < i; j++)
    {
      localTypeAnnotationNode = (TypeAnnotationNode)visibleTypeAnnotations.get(j);
      localTypeAnnotationNode.accept(paramMethodVisitor.visitTryCatchAnnotation(typeRef, typePath, desc, true));
    }
    i = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations.size();
    for (j = 0; j < i; j++)
    {
      localTypeAnnotationNode = (TypeAnnotationNode)invisibleTypeAnnotations.get(j);
      localTypeAnnotationNode.accept(paramMethodVisitor.visitTryCatchAnnotation(typeRef, typePath, desc, false));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\TryCatchBlockNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */