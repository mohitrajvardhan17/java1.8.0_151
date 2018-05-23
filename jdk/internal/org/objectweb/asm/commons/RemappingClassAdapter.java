package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class RemappingClassAdapter
  extends ClassVisitor
{
  protected final Remapper remapper;
  protected String className;
  
  public RemappingClassAdapter(ClassVisitor paramClassVisitor, Remapper paramRemapper)
  {
    this(327680, paramClassVisitor, paramRemapper);
  }
  
  protected RemappingClassAdapter(int paramInt, ClassVisitor paramClassVisitor, Remapper paramRemapper)
  {
    super(paramInt, paramClassVisitor);
    remapper = paramRemapper;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    className = paramString1;
    super.visit(paramInt1, paramInt2, remapper.mapType(paramString1), remapper.mapSignature(paramString2, false), remapper.mapType(paramString3), paramArrayOfString == null ? null : remapper.mapTypes(paramArrayOfString));
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitAnnotation(remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? null : createRemappingAnnotationAdapter(localAnnotationVisitor);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitTypeAnnotation(paramInt, paramTypePath, remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? null : createRemappingAnnotationAdapter(localAnnotationVisitor);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    FieldVisitor localFieldVisitor = super.visitField(paramInt, remapper.mapFieldName(className, paramString1, paramString2), remapper.mapDesc(paramString2), remapper.mapSignature(paramString3, true), remapper.mapValue(paramObject));
    return localFieldVisitor == null ? null : createRemappingFieldAdapter(localFieldVisitor);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    String str = remapper.mapMethodDesc(paramString2);
    MethodVisitor localMethodVisitor = super.visitMethod(paramInt, remapper.mapMethodName(className, paramString1, paramString2), str, remapper.mapSignature(paramString3, false), paramArrayOfString == null ? null : remapper.mapTypes(paramArrayOfString));
    return localMethodVisitor == null ? null : createRemappingMethodAdapter(paramInt, str, localMethodVisitor);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    super.visitInnerClass(remapper.mapType(paramString1), paramString2 == null ? null : remapper.mapType(paramString2), paramString3, paramInt);
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    super.visitOuterClass(remapper.mapType(paramString1), paramString2 == null ? null : remapper.mapMethodName(paramString1, paramString2, paramString3), paramString3 == null ? null : remapper.mapMethodDesc(paramString3));
  }
  
  protected FieldVisitor createRemappingFieldAdapter(FieldVisitor paramFieldVisitor)
  {
    return new RemappingFieldAdapter(paramFieldVisitor, remapper);
  }
  
  protected MethodVisitor createRemappingMethodAdapter(int paramInt, String paramString, MethodVisitor paramMethodVisitor)
  {
    return new RemappingMethodAdapter(paramInt, paramString, paramMethodVisitor, remapper);
  }
  
  protected AnnotationVisitor createRemappingAnnotationAdapter(AnnotationVisitor paramAnnotationVisitor)
  {
    return new RemappingAnnotationAdapter(paramAnnotationVisitor, remapper);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingClassAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */