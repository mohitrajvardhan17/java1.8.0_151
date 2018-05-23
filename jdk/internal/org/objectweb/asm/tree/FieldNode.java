package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class FieldNode
  extends FieldVisitor
{
  public int access;
  public String name;
  public String desc;
  public String signature;
  public Object value;
  public List<AnnotationNode> visibleAnnotations;
  public List<AnnotationNode> invisibleAnnotations;
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  public List<Attribute> attrs;
  
  public FieldNode(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    this(327680, paramInt, paramString1, paramString2, paramString3, paramObject);
    if (getClass() != FieldNode.class) {
      throw new IllegalStateException();
    }
  }
  
  public FieldNode(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    super(paramInt1);
    access = paramInt2;
    name = paramString1;
    desc = paramString2;
    signature = paramString3;
    value = paramObject;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    AnnotationNode localAnnotationNode = new AnnotationNode(paramString);
    if (paramBoolean)
    {
      if (visibleAnnotations == null) {
        visibleAnnotations = new ArrayList(1);
      }
      visibleAnnotations.add(localAnnotationNode);
    }
    else
    {
      if (invisibleAnnotations == null) {
        invisibleAnnotations = new ArrayList(1);
      }
      invisibleAnnotations.add(localAnnotationNode);
    }
    return localAnnotationNode;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    TypeAnnotationNode localTypeAnnotationNode = new TypeAnnotationNode(paramInt, paramTypePath, paramString);
    if (paramBoolean)
    {
      if (visibleTypeAnnotations == null) {
        visibleTypeAnnotations = new ArrayList(1);
      }
      visibleTypeAnnotations.add(localTypeAnnotationNode);
    }
    else
    {
      if (invisibleTypeAnnotations == null) {
        invisibleTypeAnnotations = new ArrayList(1);
      }
      invisibleTypeAnnotations.add(localTypeAnnotationNode);
    }
    return localTypeAnnotationNode;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    if (attrs == null) {
      attrs = new ArrayList(1);
    }
    attrs.add(paramAttribute);
  }
  
  public void visitEnd() {}
  
  public void check(int paramInt)
  {
    if (paramInt == 262144)
    {
      if ((visibleTypeAnnotations != null) && (visibleTypeAnnotations.size() > 0)) {
        throw new RuntimeException();
      }
      if ((invisibleTypeAnnotations != null) && (invisibleTypeAnnotations.size() > 0)) {
        throw new RuntimeException();
      }
    }
  }
  
  public void accept(ClassVisitor paramClassVisitor)
  {
    FieldVisitor localFieldVisitor = paramClassVisitor.visitField(access, name, desc, signature, value);
    if (localFieldVisitor == null) {
      return;
    }
    int j = visibleAnnotations == null ? 0 : visibleAnnotations.size();
    Object localObject;
    for (int i = 0; i < j; i++)
    {
      localObject = (AnnotationNode)visibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(localFieldVisitor.visitAnnotation(desc, true));
    }
    j = invisibleAnnotations == null ? 0 : invisibleAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (AnnotationNode)invisibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(localFieldVisitor.visitAnnotation(desc, false));
    }
    j = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (TypeAnnotationNode)visibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(localFieldVisitor.visitTypeAnnotation(typeRef, typePath, desc, true));
    }
    j = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (TypeAnnotationNode)invisibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(localFieldVisitor.visitTypeAnnotation(typeRef, typePath, desc, false));
    }
    j = attrs == null ? 0 : attrs.size();
    for (i = 0; i < j; i++) {
      localFieldVisitor.visitAttribute((Attribute)attrs.get(i));
    }
    localFieldVisitor.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\FieldNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */