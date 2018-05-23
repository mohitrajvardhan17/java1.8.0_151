package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public final class TraceAnnotationVisitor
  extends AnnotationVisitor
{
  private final Printer p;
  
  public TraceAnnotationVisitor(Printer paramPrinter)
  {
    this(null, paramPrinter);
  }
  
  public TraceAnnotationVisitor(AnnotationVisitor paramAnnotationVisitor, Printer paramPrinter)
  {
    super(327680, paramAnnotationVisitor);
    p = paramPrinter;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    p.visit(paramString, paramObject);
    super.visit(paramString, paramObject);
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    p.visitEnum(paramString1, paramString2, paramString3);
    super.visitEnum(paramString1, paramString2, paramString3);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    Printer localPrinter = p.visitAnnotation(paramString1, paramString2);
    AnnotationVisitor localAnnotationVisitor = av == null ? null : av.visitAnnotation(paramString1, paramString2);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public AnnotationVisitor visitArray(String paramString)
  {
    Printer localPrinter = p.visitArray(paramString);
    AnnotationVisitor localAnnotationVisitor = av == null ? null : av.visitArray(paramString);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitEnd()
  {
    p.visitAnnotationEnd();
    super.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\TraceAnnotationVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */