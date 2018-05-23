package javax.lang.model.util;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

public abstract interface Types
{
  public abstract Element asElement(TypeMirror paramTypeMirror);
  
  public abstract boolean isSameType(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  public abstract boolean isSubtype(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  public abstract boolean isAssignable(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  public abstract boolean contains(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  public abstract boolean isSubsignature(ExecutableType paramExecutableType1, ExecutableType paramExecutableType2);
  
  public abstract List<? extends TypeMirror> directSupertypes(TypeMirror paramTypeMirror);
  
  public abstract TypeMirror erasure(TypeMirror paramTypeMirror);
  
  public abstract TypeElement boxedClass(PrimitiveType paramPrimitiveType);
  
  public abstract PrimitiveType unboxedType(TypeMirror paramTypeMirror);
  
  public abstract TypeMirror capture(TypeMirror paramTypeMirror);
  
  public abstract PrimitiveType getPrimitiveType(TypeKind paramTypeKind);
  
  public abstract NullType getNullType();
  
  public abstract NoType getNoType(TypeKind paramTypeKind);
  
  public abstract ArrayType getArrayType(TypeMirror paramTypeMirror);
  
  public abstract WildcardType getWildcardType(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  public abstract DeclaredType getDeclaredType(TypeElement paramTypeElement, TypeMirror... paramVarArgs);
  
  public abstract DeclaredType getDeclaredType(DeclaredType paramDeclaredType, TypeElement paramTypeElement, TypeMirror... paramVarArgs);
  
  public abstract TypeMirror asMemberOf(DeclaredType paramDeclaredType, Element paramElement);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\Types.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */