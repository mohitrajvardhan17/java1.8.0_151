package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public class RemappingAnnotationAdapter
  extends AnnotationVisitor
{
  protected final Remapper remapper;
  
  public RemappingAnnotationAdapter(AnnotationVisitor paramAnnotationVisitor, Remapper paramRemapper)
  {
    this(327680, paramAnnotationVisitor, paramRemapper);
  }
  
  protected RemappingAnnotationAdapter(int paramInt, AnnotationVisitor paramAnnotationVisitor, Remapper paramRemapper)
  {
    super(paramInt, paramAnnotationVisitor);
    remapper = paramRemapper;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    av.visit(paramString, remapper.mapValue(paramObject));
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    av.visitEnum(paramString1, remapper.mapDesc(paramString2), paramString3);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    AnnotationVisitor localAnnotationVisitor = av.visitAnnotation(paramString1, remapper.mapDesc(paramString2));
    return localAnnotationVisitor == av ? this : localAnnotationVisitor == null ? null : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
  
  public AnnotationVisitor visitArray(String paramString)
  {
    AnnotationVisitor localAnnotationVisitor = av.visitArray(paramString);
    return localAnnotationVisitor == av ? this : localAnnotationVisitor == null ? null : new RemappingAnnotationAdapter(localAnnotationVisitor, remapper);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingAnnotationAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */