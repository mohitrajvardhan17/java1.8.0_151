package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class RemappingFieldAdapter
  extends FieldVisitor
{
  private final Remapper remapper;
  
  public RemappingFieldAdapter(FieldVisitor paramFieldVisitor, Remapper paramRemapper)
  {
    this(327680, paramFieldVisitor, paramRemapper);
  }
  
  protected RemappingFieldAdapter(int paramInt, FieldVisitor paramFieldVisitor, Remapper paramRemapper)
  {
    super(paramInt, paramFieldVisitor);
    remapper = paramRemapper;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = fv.visitAnnotation(remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? null : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = super.visitTypeAnnotation(paramInt, paramTypePath, remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? null : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingFieldAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */