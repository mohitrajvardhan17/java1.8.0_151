package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public abstract interface AnnotationValueVisitor<R, P>
{
  public abstract R visit(AnnotationValue paramAnnotationValue, P paramP);
  
  public abstract R visit(AnnotationValue paramAnnotationValue);
  
  public abstract R visitBoolean(boolean paramBoolean, P paramP);
  
  public abstract R visitByte(byte paramByte, P paramP);
  
  public abstract R visitChar(char paramChar, P paramP);
  
  public abstract R visitDouble(double paramDouble, P paramP);
  
  public abstract R visitFloat(float paramFloat, P paramP);
  
  public abstract R visitInt(int paramInt, P paramP);
  
  public abstract R visitLong(long paramLong, P paramP);
  
  public abstract R visitShort(short paramShort, P paramP);
  
  public abstract R visitString(String paramString, P paramP);
  
  public abstract R visitType(TypeMirror paramTypeMirror, P paramP);
  
  public abstract R visitEnumConstant(VariableElement paramVariableElement, P paramP);
  
  public abstract R visitAnnotation(AnnotationMirror paramAnnotationMirror, P paramP);
  
  public abstract R visitArray(List<? extends AnnotationValue> paramList, P paramP);
  
  public abstract R visitUnknown(AnnotationValue paramAnnotationValue, P paramP);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\AnnotationValueVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */