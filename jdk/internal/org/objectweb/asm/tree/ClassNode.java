package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class ClassNode
  extends ClassVisitor
{
  public int version;
  public int access;
  public String name;
  public String signature;
  public String superName;
  public List<String> interfaces = new ArrayList();
  public String sourceFile;
  public String sourceDebug;
  public String outerClass;
  public String outerMethod;
  public String outerMethodDesc;
  public List<AnnotationNode> visibleAnnotations;
  public List<AnnotationNode> invisibleAnnotations;
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  public List<Attribute> attrs;
  public List<InnerClassNode> innerClasses = new ArrayList();
  public List<FieldNode> fields = new ArrayList();
  public List<MethodNode> methods = new ArrayList();
  
  public ClassNode()
  {
    this(327680);
    if (getClass() != ClassNode.class) {
      throw new IllegalStateException();
    }
  }
  
  public ClassNode(int paramInt)
  {
    super(paramInt);
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    version = paramInt1;
    access = paramInt2;
    name = paramString1;
    signature = paramString2;
    superName = paramString3;
    if (paramArrayOfString != null) {
      interfaces.addAll(Arrays.asList(paramArrayOfString));
    }
  }
  
  public void visitSource(String paramString1, String paramString2)
  {
    sourceFile = paramString1;
    sourceDebug = paramString2;
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    outerClass = paramString1;
    outerMethod = paramString2;
    outerMethodDesc = paramString3;
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
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    InnerClassNode localInnerClassNode = new InnerClassNode(paramString1, paramString2, paramString3, paramInt);
    innerClasses.add(localInnerClassNode);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    FieldNode localFieldNode = new FieldNode(paramInt, paramString1, paramString2, paramString3, paramObject);
    fields.add(localFieldNode);
    return localFieldNode;
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    MethodNode localMethodNode = new MethodNode(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    methods.add(localMethodNode);
    return localMethodNode;
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
      Iterator localIterator = fields.iterator();
      Object localObject;
      while (localIterator.hasNext())
      {
        localObject = (FieldNode)localIterator.next();
        ((FieldNode)localObject).check(paramInt);
      }
      localIterator = methods.iterator();
      while (localIterator.hasNext())
      {
        localObject = (MethodNode)localIterator.next();
        ((MethodNode)localObject).check(paramInt);
      }
    }
  }
  
  public void accept(ClassVisitor paramClassVisitor)
  {
    String[] arrayOfString = new String[interfaces.size()];
    interfaces.toArray(arrayOfString);
    paramClassVisitor.visit(version, access, name, signature, superName, arrayOfString);
    if ((sourceFile != null) || (sourceDebug != null)) {
      paramClassVisitor.visitSource(sourceFile, sourceDebug);
    }
    if (outerClass != null) {
      paramClassVisitor.visitOuterClass(outerClass, outerMethod, outerMethodDesc);
    }
    int j = visibleAnnotations == null ? 0 : visibleAnnotations.size();
    Object localObject;
    for (int i = 0; i < j; i++)
    {
      localObject = (AnnotationNode)visibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(paramClassVisitor.visitAnnotation(desc, true));
    }
    j = invisibleAnnotations == null ? 0 : invisibleAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (AnnotationNode)invisibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(paramClassVisitor.visitAnnotation(desc, false));
    }
    j = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (TypeAnnotationNode)visibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(paramClassVisitor.visitTypeAnnotation(typeRef, typePath, desc, true));
    }
    j = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (TypeAnnotationNode)invisibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(paramClassVisitor.visitTypeAnnotation(typeRef, typePath, desc, false));
    }
    j = attrs == null ? 0 : attrs.size();
    for (i = 0; i < j; i++) {
      paramClassVisitor.visitAttribute((Attribute)attrs.get(i));
    }
    for (i = 0; i < innerClasses.size(); i++) {
      ((InnerClassNode)innerClasses.get(i)).accept(paramClassVisitor);
    }
    for (i = 0; i < fields.size(); i++) {
      ((FieldNode)fields.get(i)).accept(paramClassVisitor);
    }
    for (i = 0; i < methods.size(); i++) {
      ((MethodNode)methods.get(i)).accept(paramClassVisitor);
    }
    paramClassVisitor.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\ClassNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */