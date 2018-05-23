package jdk.internal.org.objectweb.asm.util;

import java.io.PrintWriter;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public final class TraceClassVisitor
  extends ClassVisitor
{
  private final PrintWriter pw;
  public final Printer p;
  
  public TraceClassVisitor(PrintWriter paramPrintWriter)
  {
    this(null, paramPrintWriter);
  }
  
  public TraceClassVisitor(ClassVisitor paramClassVisitor, PrintWriter paramPrintWriter)
  {
    this(paramClassVisitor, new Textifier(), paramPrintWriter);
  }
  
  public TraceClassVisitor(ClassVisitor paramClassVisitor, Printer paramPrinter, PrintWriter paramPrintWriter)
  {
    super(327680, paramClassVisitor);
    pw = paramPrintWriter;
    p = paramPrinter;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    p.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public void visitSource(String paramString1, String paramString2)
  {
    p.visitSource(paramString1, paramString2);
    super.visitSource(paramString1, paramString2);
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    p.visitOuterClass(paramString1, paramString2, paramString3);
    super.visitOuterClass(paramString1, paramString2, paramString3);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitClassAnnotation(paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = cv == null ? null : cv.visitAnnotation(paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    Printer localPrinter = p.visitClassTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor localAnnotationVisitor = cv == null ? null : cv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(localAnnotationVisitor, localPrinter);
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    p.visitClassAttribute(paramAttribute);
    super.visitAttribute(paramAttribute);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    p.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
    super.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    Printer localPrinter = p.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
    FieldVisitor localFieldVisitor = cv == null ? null : cv.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
    return new TraceFieldVisitor(localFieldVisitor, localPrinter);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    Printer localPrinter = p.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    MethodVisitor localMethodVisitor = cv == null ? null : cv.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    return new TraceMethodVisitor(localMethodVisitor, localPrinter);
  }
  
  public void visitEnd()
  {
    p.visitClassEnd();
    if (pw != null)
    {
      p.print(pw);
      pw.flush();
    }
    super.visitEnd();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\TraceClassVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */