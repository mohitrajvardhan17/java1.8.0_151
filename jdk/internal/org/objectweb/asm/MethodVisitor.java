package jdk.internal.org.objectweb.asm;

public abstract class MethodVisitor
{
  protected final int api;
  protected MethodVisitor mv;
  
  public MethodVisitor(int paramInt)
  {
    this(paramInt, null);
  }
  
  public MethodVisitor(int paramInt, MethodVisitor paramMethodVisitor)
  {
    if ((paramInt != 262144) && (paramInt != 327680)) {
      throw new IllegalArgumentException();
    }
    api = paramInt;
    mv = paramMethodVisitor;
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    if (api < 327680) {
      throw new RuntimeException();
    }
    if (mv != null) {
      mv.visitParameter(paramString, paramInt);
    }
  }
  
  public AnnotationVisitor visitAnnotationDefault()
  {
    if (mv != null) {
      return mv.visitAnnotationDefault();
    }
    return null;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    if (mv != null) {
      return mv.visitAnnotation(paramString, paramBoolean);
    }
    return null;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (api < 327680) {
      throw new RuntimeException();
    }
    if (mv != null) {
      return mv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    if (mv != null) {
      return mv.visitParameterAnnotation(paramInt, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    if (mv != null) {
      mv.visitAttribute(paramAttribute);
    }
  }
  
  public void visitCode()
  {
    if (mv != null) {
      mv.visitCode();
    }
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    if (mv != null) {
      mv.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
    }
  }
  
  public void visitInsn(int paramInt)
  {
    if (mv != null) {
      mv.visitInsn(paramInt);
    }
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    if (mv != null) {
      mv.visitIntInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    if (mv != null) {
      mv.visitVarInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    if (mv != null) {
      mv.visitTypeInsn(paramInt, paramString);
    }
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (mv != null) {
      mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
    }
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (api >= 327680)
    {
      boolean bool = paramInt == 185;
      visitMethodInsn(paramInt, paramString1, paramString2, paramString3, bool);
      return;
    }
    if (mv != null) {
      mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
    }
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (api < 327680)
    {
      if (paramBoolean != (paramInt == 185)) {
        throw new IllegalArgumentException("INVOKESPECIAL/STATIC on interfaces require ASM 5");
      }
      visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    }
    if (mv != null) {
      mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    if (mv != null) {
      mv.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
    }
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    if (mv != null) {
      mv.visitJumpInsn(paramInt, paramLabel);
    }
  }
  
  public void visitLabel(Label paramLabel)
  {
    if (mv != null) {
      mv.visitLabel(paramLabel);
    }
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    if (mv != null) {
      mv.visitLdcInsn(paramObject);
    }
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    if (mv != null) {
      mv.visitIincInsn(paramInt1, paramInt2);
    }
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    if (mv != null) {
      mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
    }
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    if (mv != null) {
      mv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
    }
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    if (mv != null) {
      mv.visitMultiANewArrayInsn(paramString, paramInt);
    }
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (api < 327680) {
      throw new RuntimeException();
    }
    if (mv != null) {
      return mv.visitInsnAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    if (mv != null) {
      mv.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
    }
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    if (api < 327680) {
      throw new RuntimeException();
    }
    if (mv != null) {
      return mv.visitTryCatchAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    if (mv != null) {
      mv.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
    }
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    if (api < 327680) {
      throw new RuntimeException();
    }
    if (mv != null) {
      return mv.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, paramString, paramBoolean);
    }
    return null;
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    if (mv != null) {
      mv.visitLineNumber(paramInt, paramLabel);
    }
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    if (mv != null) {
      mv.visitMaxs(paramInt1, paramInt2);
    }
  }
  
  public void visitEnd()
  {
    if (mv != null) {
      mv.visitEnd();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\MethodVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */