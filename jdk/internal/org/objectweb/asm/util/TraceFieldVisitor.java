package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public final class TraceFieldVisitor
  extends FieldVisitor
{
  public final Printer p;
  
  public TraceFieldVisitor(Printer paramPrinter)
  {
    this(null, paramPrinter);
  }
  
  public TraceFieldVisitor(FieldVisitor paramFieldVisitor, Printer paramPrinter)
  {
    super(327680, paramFieldVisitor);
    p = paramPrinter;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitFieldAnnotation(paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = fv == null ? null : fv.visitAnnotation(paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitFieldTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = fv == null ? null : fv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    p.visitFieldAttribute(paramAttribute);
    super.visitAttribute(paramAttribute);
  }
  
  public void visitEnd()
  {
    p.visitFieldEnd();
    super.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\TraceFieldVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */