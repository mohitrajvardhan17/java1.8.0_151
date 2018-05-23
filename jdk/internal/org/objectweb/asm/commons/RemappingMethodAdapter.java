package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class RemappingMethodAdapter
  extends LocalVariablesSorter
{
  protected final Remapper remapper;
  
  public RemappingMethodAdapter(int paramInt, String paramString, MethodVisitor paramMethodVisitor, Remapper paramRemapper)
  {
    this(327680, paramInt, paramString, paramMethodVisitor, paramRemapper);
  }
  
  protected RemappingMethodAdapter(int paramInt1, int paramInt2, String paramString, MethodVisitor paramMethodVisitor, Remapper paramRemapper)
  {
    super(paramInt1, paramInt2, paramString, paramMethodVisitor);
    remapper = paramRemapper;
  }
  
  public AnnotationVisitor visitAnnotationDefault()
  {
    AnnotationVisitor localAnnotationVisitor = super.visitAnnotationDefault();
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitAnnotation(remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitTypeAnnotation(paramInt, paramTypePath, remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitParameterAnnotation(paramInt, remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    super.visitFrame(paramInt1, paramInt2, remapEntries(paramInt2, paramArrayOfObject1), paramInt3, remapEntries(paramInt3, paramArrayOfObject2));
  }
  
  private Object[] remapEntries(int paramInt, Object[] paramArrayOfObject)
  {
    for (int i = 0; i < paramInt; i++) {
      if ((paramArrayOfObject[i] instanceof String))
      {
        Object[] arrayOfObject = new Object[paramInt];
        if (i > 0) {
          System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, i);
        }
        do
        {
          Object localObject = paramArrayOfObject[i];
          arrayOfObject[(i++)] = ((localObject instanceof String) ? remapper.mapType((String)localObject) : localObject);
        } while (i < paramInt);
        return arrayOfObject;
      }
    }
    return paramArrayOfObject;
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    super.visitFieldInsn(paramInt, remapper.mapType(paramString1), remapper.mapFieldName(paramString1, paramString2, paramString3), remapper.mapDesc(paramString3));
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (api >= 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    }
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramInt == 185);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (api < 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    }
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  private void doVisitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (mv != null) {
      mv.visitMethodInsn(paramInt, remapper.mapType(paramString1), remapper.mapMethodName(paramString1, paramString2, paramString3), remapper.mapMethodDesc(paramString3), paramBoolean);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    for (int i = 0; i < paramVarArgs.length; i++) {
      paramVarArgs[i] = remapper.mapValue(paramVarArgs[i]);
    }
    super.visitInvokeDynamicInsn(remapper.mapInvokeDynamicMethodName(paramString1, paramString2), remapper.mapMethodDesc(paramString2), (Handle)remapper.mapValue(paramHandle), paramVarArgs);
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    super.visitTypeInsn(paramInt, remapper.mapType(paramString));
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    super.visitLdcInsn(remapper.mapValue(paramObject));
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    super.visitMultiANewArrayInsn(remapper.mapDesc(paramString), paramInt);
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitInsnAnnotation(paramInt, paramTypePath, remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    super.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString == null ? null : remapper.mapType(paramString));
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitTryCatchAnnotation(paramInt, paramTypePath, remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    super.visitLocalVariable(paramString1, remapper.mapDesc(paramString2), remapper.mapSignature(paramString3, true), paramLabel1, paramLabel2, paramInt);
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingMethodAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */