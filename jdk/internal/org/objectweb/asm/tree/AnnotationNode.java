package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public class AnnotationNode
  extends AnnotationVisitor
{
  public String desc;
  public List<Object> values;
  
  public AnnotationNode(String paramString)
  {
    this(327680, paramString);
    if (getClass() != AnnotationNode.class) {
      throw new IllegalStateException();
    }
  }
  
  public AnnotationNode(int paramInt, String paramString)
  {
    super(paramInt);
    desc = paramString;
  }
  
  AnnotationNode(List<Object> paramList)
  {
    super(327680);
    values = paramList;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    if (values == null) {
      values = new ArrayList(desc != null ? 2 : 1);
    }
    if (desc != null) {
      values.add(paramString);
    }
    values.add(paramObject);
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    if (values == null) {
      values = new ArrayList(desc != null ? 2 : 1);
    }
    if (desc != null) {
      values.add(paramString1);
    }
    values.add(new String[] { paramString2, paramString3 });
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    if (values == null) {
      values = new ArrayList(desc != null ? 2 : 1);
    }
    if (desc != null) {
      values.add(paramString1);
    }
    AnnotationNode localAnnotationNode = new AnnotationNode(paramString2);
    values.add(localAnnotationNode);
    return localAnnotationNode;
  }
  
  public AnnotationVisitor visitArray(String paramString)
  {
    if (values == null) {
      values = new ArrayList(desc != null ? 2 : 1);
    }
    if (desc != null) {
      values.add(paramString);
    }
    ArrayList localArrayList = new ArrayList();
    values.add(localArrayList);
    return new AnnotationNode(localArrayList);
  }
  
  public void visitEnd() {}
  
  public void check(int paramInt) {}
  
  public void accept(AnnotationVisitor paramAnnotationVisitor)
  {
    if (paramAnnotationVisitor != null)
    {
      if (values != null) {
        for (int i = 0; i < values.size(); i += 2)
        {
          String str = (String)values.get(i);
          Object localObject = values.get(i + 1);
          accept(paramAnnotationVisitor, str, localObject);
        }
      }
      paramAnnotationVisitor.visitEnd();
    }
  }
  
  static void accept(AnnotationVisitor paramAnnotationVisitor, String paramString, Object paramObject)
  {
    if (paramAnnotationVisitor != null)
    {
      Object localObject;
      if ((paramObject instanceof String[]))
      {
        localObject = (String[])paramObject;
        paramAnnotationVisitor.visitEnum(paramString, localObject[0], localObject[1]);
      }
      else if ((paramObject instanceof AnnotationNode))
      {
        localObject = (AnnotationNode)paramObject;
        ((AnnotationNode)localObject).accept(paramAnnotationVisitor.visitAnnotation(paramString, desc));
      }
      else if ((paramObject instanceof List))
      {
        localObject = paramAnnotationVisitor.visitArray(paramString);
        if (localObject != null)
        {
          List localList = (List)paramObject;
          for (int i = 0; i < localList.size(); i++) {
            accept((AnnotationVisitor)localObject, null, localList.get(i));
          }
          ((AnnotationVisitor)localObject).visitEnd();
        }
      }
      else
      {
        paramAnnotationVisitor.visit(paramString, paramObject);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\AnnotationNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */