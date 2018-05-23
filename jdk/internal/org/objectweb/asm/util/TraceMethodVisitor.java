package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public final class TraceMethodVisitor
  extends MethodVisitor
{
  public final Printer p;
  
  public TraceMethodVisitor(Printer paramPrinter)
  {
    this(null, paramPrinter);
  }
  
  public TraceMethodVisitor(MethodVisitor paramMethodVisitor, Printer paramPrinter)
  {
    super(327680, paramMethodVisitor);
    p = paramPrinter;
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    p.visitParameter(paramString, paramInt);
    super.visitParameter(paramString, paramInt);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitMethodAnnotation(paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = mv == null ? null : mv.visitAnnotation(paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitMethodTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = mv == null ? null : mv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    p.visitMethodAttribute(paramAttribute);
    super.visitAttribute(paramAttribute);
  }
  
  public AnnotationVisitor visitAnnotationDefault()
  {
    Printer localPrinter = p.visitAnnotationDefault();
    AnnotationVisitor localAnnotationVisitor = mv == null ? null : mv.visitAnnotationDefault();
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitParameterAnnotation(paramInt, paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = mv == null ? null : mv.visitParameterAnnotation(paramInt, paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitCode()
  {
    p.visitCode();
    super.visitCode();
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    p.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
    super.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
  }
  
  public void visitInsn(int paramInt)
  {
    p.visitInsn(paramInt);
    super.visitInsn(paramInt);
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    p.visitIntInsn(paramInt1, paramInt2);
    super.visitIntInsn(paramInt1, paramInt2);
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    p.visitVarInsn(paramInt1, paramInt2);
    super.visitVarInsn(paramInt1, paramInt2);
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    p.visitTypeInsn(paramInt, paramString);
    super.visitTypeInsn(paramInt, paramString);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    p.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
    super.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (api >= 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    }
    p.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
    if (mv != null) {
      mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
    }
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (api < 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    }
    p.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
    if (mv != null) {
      mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    p.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
    super.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    p.visitJumpInsn(paramInt, paramLabel);
    super.visitJumpInsn(paramInt, paramLabel);
  }
  
  public void visitLabel(Label paramLabel)
  {
    p.visitLabel(paramLabel);
    super.visitLabel(paramLabel);
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    p.visitLdcInsn(paramObject);
    super.visitLdcInsn(paramObject);
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    p.visitIincInsn(paramInt1, paramInt2);
    super.visitIincInsn(paramInt1, paramInt2);
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    p.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
    super.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    p.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
    super.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    p.visitMultiANewArrayInsn(paramString, paramInt);
    super.visitMultiANewArrayInsn(paramString, paramInt);
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitInsnAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = mv == null ? null : mv.visitInsnAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    p.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
    super.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitTryCatchAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = mv == null ? null : mv.visitTryCatchAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    p.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
    super.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = mv == null ? null : mv.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    p.visitLineNumber(paramInt, paramLabel);
    super.visitLineNumber(paramInt, paramLabel);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    p.visitMaxs(paramInt1, paramInt2);
    super.visitMaxs(paramInt1, paramInt2);
  }
  
  public void visitEnd()
  {
    p.visitMethodEnd();
    super.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\TraceMethodVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */