package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Type;

public class CheckAnnotationAdapter
  extends AnnotationVisitor
{
  private final boolean named;
  private boolean end;
  
  public CheckAnnotationAdapter(AnnotationVisitor paramAnnotationVisitor)
  {
    this(paramAnnotationVisitor, true);
  }
  
  CheckAnnotationAdapter(AnnotationVisitor paramAnnotationVisitor, boolean paramBoolean)
  {
    super(327680, paramAnnotationVisitor);
    named = paramBoolean;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    checkEnd();
    checkName(paramString);
    if ((!(paramObject instanceof Byte)) && (!(paramObject instanceof Boolean)) && (!(paramObject instanceof Character)) && (!(paramObject instanceof Short)) && (!(paramObject instanceof Integer)) && (!(paramObject instanceof Long)) && (!(paramObject instanceof Float)) && (!(paramObject instanceof Double)) && (!(paramObject instanceof String)) && (!(paramObject instanceof Type)) && (!(paramObject instanceof byte[])) && (!(paramObject instanceof boolean[])) && (!(paramObject instanceof char[])) && (!(paramObject instanceof short[])) && (!(paramObject instanceof int[])) && (!(paramObject instanceof long[])) && (!(paramObject instanceof float[])) && (!(paramObject instanceof double[]))) {
      throw new IllegalArgumentException("Invalid annotation value");
    }
    if ((paramObject instanceof Type))
    {
      int i = ((Type)paramObject).getSort();
      if (i == 11) {
        throw new IllegalArgumentException("Invalid annotation value");
      }
    }
    if (av != null) {
      av.visit(paramString, paramObject);
    }
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    checkEnd();
    checkName(paramString1);
    CheckMethodAdapter.checkDesc(paramString2, false);
    if (paramString3 == null) {
      throw new IllegalArgumentException("Invalid enum value");
    }
    if (av != null) {
      av.visitEnum(paramString1, paramString2, paramString3);
    }
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    checkEnd();
    checkName(paramString1);
    CheckMethodAdapter.checkDesc(paramString2, false);
    return new CheckAnnotationAdapter(av == null ? null : av.visitAnnotation(paramString1, paramString2));
  }
  
  public AnnotationVisitor visitArray(String paramString)
  {
    checkEnd();
    checkName(paramString);
    return new CheckAnnotationAdapter(av == null ? null : av.visitArray(paramString), false);
  }
  
  public void visitEnd()
  {
    checkEnd();
    end = true;
    if (av != null) {
      av.visitEnd();
    }
  }
  
  private void checkEnd()
  {
    if (end) {
      throw new IllegalStateException("Cannot call a visit method after visitEnd has been called");
    }
  }
  
  private void checkName(String paramString)
  {
    if ((named) && (paramString == null)) {
      throw new IllegalArgumentException("Annotation value name must not be null");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\CheckAnnotationAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */