package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.TypePath;

public class MethodNode
  extends MethodVisitor
{
  public int access;
  public String name;
  public String desc;
  public String signature;
  public List<String> exceptions;
  public List<ParameterNode> parameters;
  public List<AnnotationNode> visibleAnnotations;
  public List<AnnotationNode> invisibleAnnotations;
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  public List<Attribute> attrs;
  public Object annotationDefault;
  public List<AnnotationNode>[] visibleParameterAnnotations;
  public List<AnnotationNode>[] invisibleParameterAnnotations;
  public InsnList instructions;
  public List<TryCatchBlockNode> tryCatchBlocks;
  public int maxStack;
  public int maxLocals;
  public List<LocalVariableNode> localVariables;
  public List<LocalVariableAnnotationNode> visibleLocalVariableAnnotations;
  public List<LocalVariableAnnotationNode> invisibleLocalVariableAnnotations;
  private boolean visited;
  
  public MethodNode()
  {
    this(327680);
    if (getClass() != MethodNode.class) {
      throw new IllegalStateException();
    }
  }
  
  public MethodNode(int paramInt)
  {
    super(paramInt);
    instructions = new InsnList();
  }
  
  public MethodNode(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this(327680, paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    if (getClass() != MethodNode.class) {
      throw new IllegalStateException();
    }
  }
  
  public MethodNode(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    super(paramInt1);
    access = paramInt2;
    name = paramString1;
    desc = paramString2;
    signature = paramString3;
    exceptions = new ArrayList(paramArrayOfString == null ? 0 : paramArrayOfString.length);
    int i = (paramInt2 & 0x400) != 0 ? 1 : 0;
    if (i == 0) {
      localVariables = new ArrayList(5);
    }
    tryCatchBlocks = new ArrayList();
    if (paramArrayOfString != null) {
      exceptions.addAll(Arrays.asList(paramArrayOfString));
    }
    instructions = new InsnList();
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    if (parameters == null) {
      parameters = new ArrayList(5);
    }
    parameters.add(new ParameterNode(paramString, paramInt));
  }
  
  public AnnotationVisitor visitAnnotationDefault()
  {
    new AnnotationNode(new ArrayList(0)
    {
      public boolean add(Object paramAnonymousObject)
      {
        annotationDefault = paramAnonymousObject;
        return super.add(paramAnonymousObject);
      }
    });
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
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    AnnotationNode localAnnotationNode = new AnnotationNode(paramString);
    int i;
    if (paramBoolean)
    {
      if (visibleParameterAnnotations == null)
      {
        i = Type.getArgumentTypes(desc).length;
        visibleParameterAnnotations = ((List[])new List[i]);
      }
      if (visibleParameterAnnotations[paramInt] == null) {
        visibleParameterAnnotations[paramInt] = new ArrayList(1);
      }
      visibleParameterAnnotations[paramInt].add(localAnnotationNode);
    }
    else
    {
      if (invisibleParameterAnnotations == null)
      {
        i = Type.getArgumentTypes(desc).length;
        invisibleParameterAnnotations = ((List[])new List[i]);
      }
      if (invisibleParameterAnnotations[paramInt] == null) {
        invisibleParameterAnnotations[paramInt] = new ArrayList(1);
      }
      invisibleParameterAnnotations[paramInt].add(localAnnotationNode);
    }
    return localAnnotationNode;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    if (attrs == null) {
      attrs = new ArrayList(1);
    }
    attrs.add(paramAttribute);
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    instructions.add(new FrameNode(paramInt1, paramInt2, paramArrayOfObject1 == null ? null : getLabelNodes(paramArrayOfObject1), paramInt3, paramArrayOfObject2 == null ? null : getLabelNodes(paramArrayOfObject2)));
  }
  
  public void visitInsn(int paramInt)
  {
    instructions.add(new InsnNode(paramInt));
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    instructions.add(new IntInsnNode(paramInt1, paramInt2));
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    instructions.add(new VarInsnNode(paramInt1, paramInt2));
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    instructions.add(new TypeInsnNode(paramInt, paramString));
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    instructions.add(new FieldInsnNode(paramInt, paramString1, paramString2, paramString3));
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (api >= 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    }
    instructions.add(new MethodInsnNode(paramInt, paramString1, paramString2, paramString3));
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (api < 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    }
    instructions.add(new MethodInsnNode(paramInt, paramString1, paramString2, paramString3, paramBoolean));
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    instructions.add(new InvokeDynamicInsnNode(paramString1, paramString2, paramHandle, paramVarArgs));
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    instructions.add(new JumpInsnNode(paramInt, getLabelNode(paramLabel)));
  }
  
  public void visitLabel(Label paramLabel)
  {
    instructions.add(getLabelNode(paramLabel));
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    instructions.add(new LdcInsnNode(paramObject));
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    instructions.add(new IincInsnNode(paramInt1, paramInt2));
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    instructions.add(new TableSwitchInsnNode(paramInt1, paramInt2, getLabelNode(paramLabel), getLabelNodes(paramVarArgs)));
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    instructions.add(new LookupSwitchInsnNode(getLabelNode(paramLabel), paramArrayOfInt, getLabelNodes(paramArrayOfLabel)));
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    instructions.add(new MultiANewArrayInsnNode(paramString, paramInt));
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    for (AbstractInsnNode localAbstractInsnNode = instructions.getLast(); localAbstractInsnNode.getOpcode() == -1; localAbstractInsnNode = localAbstractInsnNode.getPrevious()) {}
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
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    tryCatchBlocks.add(new TryCatchBlockNode(getLabelNode(paramLabel1), getLabelNode(paramLabel2), getLabelNode(paramLabel3), paramString));
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    TryCatchBlockNode localTryCatchBlockNode = (TryCatchBlockNode)tryCatchBlocks.get((paramInt & 0xFFFF00) >> 8);
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
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    localVariables.add(new LocalVariableNode(paramString1, paramString2, paramString3, getLabelNode(paramLabel1), getLabelNode(paramLabel2), paramInt));
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    LocalVariableAnnotationNode localLocalVariableAnnotationNode = new LocalVariableAnnotationNode(paramInt, paramTypePath, getLabelNodes(paramArrayOfLabel1), getLabelNodes(paramArrayOfLabel2), paramArrayOfInt, paramString);
    if (paramBoolean)
    {
      if (visibleLocalVariableAnnotations == null) {
        visibleLocalVariableAnnotations = new ArrayList(1);
      }
      visibleLocalVariableAnnotations.add(localLocalVariableAnnotationNode);
    }
    else
    {
      if (invisibleLocalVariableAnnotations == null) {
        invisibleLocalVariableAnnotations = new ArrayList(1);
      }
      invisibleLocalVariableAnnotations.add(localLocalVariableAnnotationNode);
    }
    return localLocalVariableAnnotationNode;
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    instructions.add(new LineNumberNode(paramInt, getLabelNode(paramLabel)));
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    maxStack = paramInt1;
    maxLocals = paramInt2;
  }
  
  public void visitEnd() {}
  
  protected LabelNode getLabelNode(Label paramLabel)
  {
    if (!(info instanceof LabelNode)) {
      info = new LabelNode();
    }
    return (LabelNode)info;
  }
  
  private LabelNode[] getLabelNodes(Label[] paramArrayOfLabel)
  {
    LabelNode[] arrayOfLabelNode = new LabelNode[paramArrayOfLabel.length];
    for (int i = 0; i < paramArrayOfLabel.length; i++) {
      arrayOfLabelNode[i] = getLabelNode(paramArrayOfLabel[i]);
    }
    return arrayOfLabelNode;
  }
  
  private Object[] getLabelNodes(Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      Object localObject = paramArrayOfObject[i];
      if ((localObject instanceof Label)) {
        localObject = getLabelNode((Label)localObject);
      }
      arrayOfObject[i] = localObject;
    }
    return arrayOfObject;
  }
  
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
      int i = tryCatchBlocks == null ? 0 : tryCatchBlocks.size();
      Object localObject;
      for (int j = 0; j < i; j++)
      {
        localObject = (TryCatchBlockNode)tryCatchBlocks.get(j);
        if ((visibleTypeAnnotations != null) && (visibleTypeAnnotations.size() > 0)) {
          throw new RuntimeException();
        }
        if ((invisibleTypeAnnotations != null) && (invisibleTypeAnnotations.size() > 0)) {
          throw new RuntimeException();
        }
      }
      for (j = 0; j < instructions.size(); j++)
      {
        localObject = instructions.get(j);
        if ((visibleTypeAnnotations != null) && (visibleTypeAnnotations.size() > 0)) {
          throw new RuntimeException();
        }
        if ((invisibleTypeAnnotations != null) && (invisibleTypeAnnotations.size() > 0)) {
          throw new RuntimeException();
        }
        if ((localObject instanceof MethodInsnNode))
        {
          boolean bool = itf;
          if (bool != (opcode == 185)) {
            throw new RuntimeException();
          }
        }
      }
      if ((visibleLocalVariableAnnotations != null) && (visibleLocalVariableAnnotations.size() > 0)) {
        throw new RuntimeException();
      }
      if ((invisibleLocalVariableAnnotations != null) && (invisibleLocalVariableAnnotations.size() > 0)) {
        throw new RuntimeException();
      }
    }
  }
  
  public void accept(ClassVisitor paramClassVisitor)
  {
    String[] arrayOfString = new String[exceptions.size()];
    exceptions.toArray(arrayOfString);
    MethodVisitor localMethodVisitor = paramClassVisitor.visitMethod(access, name, desc, signature, arrayOfString);
    if (localMethodVisitor != null) {
      accept(localMethodVisitor);
    }
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    int k = parameters == null ? 0 : parameters.size();
    Object localObject;
    for (int i = 0; i < k; i++)
    {
      localObject = (ParameterNode)parameters.get(i);
      paramMethodVisitor.visitParameter(name, access);
    }
    if (annotationDefault != null)
    {
      localObject = paramMethodVisitor.visitAnnotationDefault();
      AnnotationNode.accept((AnnotationVisitor)localObject, null, annotationDefault);
      if (localObject != null) {
        ((AnnotationVisitor)localObject).visitEnd();
      }
    }
    k = visibleAnnotations == null ? 0 : visibleAnnotations.size();
    for (i = 0; i < k; i++)
    {
      localObject = (AnnotationNode)visibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(paramMethodVisitor.visitAnnotation(desc, true));
    }
    k = invisibleAnnotations == null ? 0 : invisibleAnnotations.size();
    for (i = 0; i < k; i++)
    {
      localObject = (AnnotationNode)invisibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(paramMethodVisitor.visitAnnotation(desc, false));
    }
    k = visibleTypeAnnotations == null ? 0 : visibleTypeAnnotations.size();
    for (i = 0; i < k; i++)
    {
      localObject = (TypeAnnotationNode)visibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(paramMethodVisitor.visitTypeAnnotation(typeRef, typePath, desc, true));
    }
    k = invisibleTypeAnnotations == null ? 0 : invisibleTypeAnnotations.size();
    for (i = 0; i < k; i++)
    {
      localObject = (TypeAnnotationNode)invisibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(paramMethodVisitor.visitTypeAnnotation(typeRef, typePath, desc, false));
    }
    k = visibleParameterAnnotations == null ? 0 : visibleParameterAnnotations.length;
    int j;
    AnnotationNode localAnnotationNode;
    for (i = 0; i < k; i++)
    {
      localObject = visibleParameterAnnotations[i];
      if (localObject != null) {
        for (j = 0; j < ((List)localObject).size(); j++)
        {
          localAnnotationNode = (AnnotationNode)((List)localObject).get(j);
          localAnnotationNode.accept(paramMethodVisitor.visitParameterAnnotation(i, desc, true));
        }
      }
    }
    k = invisibleParameterAnnotations == null ? 0 : invisibleParameterAnnotations.length;
    for (i = 0; i < k; i++)
    {
      localObject = invisibleParameterAnnotations[i];
      if (localObject != null) {
        for (j = 0; j < ((List)localObject).size(); j++)
        {
          localAnnotationNode = (AnnotationNode)((List)localObject).get(j);
          localAnnotationNode.accept(paramMethodVisitor.visitParameterAnnotation(i, desc, false));
        }
      }
    }
    if (visited) {
      instructions.resetLabels();
    }
    k = attrs == null ? 0 : attrs.size();
    for (i = 0; i < k; i++) {
      paramMethodVisitor.visitAttribute((Attribute)attrs.get(i));
    }
    if (instructions.size() > 0)
    {
      paramMethodVisitor.visitCode();
      k = tryCatchBlocks == null ? 0 : tryCatchBlocks.size();
      for (i = 0; i < k; i++)
      {
        ((TryCatchBlockNode)tryCatchBlocks.get(i)).updateIndex(i);
        ((TryCatchBlockNode)tryCatchBlocks.get(i)).accept(paramMethodVisitor);
      }
      instructions.accept(paramMethodVisitor);
      k = localVariables == null ? 0 : localVariables.size();
      for (i = 0; i < k; i++) {
        ((LocalVariableNode)localVariables.get(i)).accept(paramMethodVisitor);
      }
      k = visibleLocalVariableAnnotations == null ? 0 : visibleLocalVariableAnnotations.size();
      for (i = 0; i < k; i++) {
        ((LocalVariableAnnotationNode)visibleLocalVariableAnnotations.get(i)).accept(paramMethodVisitor, true);
      }
      k = invisibleLocalVariableAnnotations == null ? 0 : invisibleLocalVariableAnnotations.size();
      for (i = 0; i < k; i++) {
        ((LocalVariableAnnotationNode)invisibleLocalVariableAnnotations.get(i)).accept(paramMethodVisitor, false);
      }
      paramMethodVisitor.visitMaxs(maxStack, maxLocals);
      visited = true;
    }
    paramMethodVisitor.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\MethodNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */