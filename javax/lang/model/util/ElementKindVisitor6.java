package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ElementKindVisitor6<R, P>
  extends SimpleElementVisitor6<R, P>
{
  protected ElementKindVisitor6()
  {
    super(null);
  }
  
  protected ElementKindVisitor6(R paramR)
  {
    super(paramR);
  }
  
  public R visitPackage(PackageElement paramPackageElement, P paramP)
  {
    assert (paramPackageElement.getKind() == ElementKind.PACKAGE) : "Bad kind on PackageElement";
    return (R)defaultAction(paramPackageElement, paramP);
  }
  
  public R visitType(TypeElement paramTypeElement, P paramP)
  {
    ElementKind localElementKind = paramTypeElement.getKind();
    switch (localElementKind)
    {
    case ANNOTATION_TYPE: 
      return (R)visitTypeAsAnnotationType(paramTypeElement, paramP);
    case CLASS: 
      return (R)visitTypeAsClass(paramTypeElement, paramP);
    case ENUM: 
      return (R)visitTypeAsEnum(paramTypeElement, paramP);
    case INTERFACE: 
      return (R)visitTypeAsInterface(paramTypeElement, paramP);
    }
    throw new AssertionError("Bad kind " + localElementKind + " for TypeElement" + paramTypeElement);
  }
  
  public R visitTypeAsAnnotationType(TypeElement paramTypeElement, P paramP)
  {
    return (R)defaultAction(paramTypeElement, paramP);
  }
  
  public R visitTypeAsClass(TypeElement paramTypeElement, P paramP)
  {
    return (R)defaultAction(paramTypeElement, paramP);
  }
  
  public R visitTypeAsEnum(TypeElement paramTypeElement, P paramP)
  {
    return (R)defaultAction(paramTypeElement, paramP);
  }
  
  public R visitTypeAsInterface(TypeElement paramTypeElement, P paramP)
  {
    return (R)defaultAction(paramTypeElement, paramP);
  }
  
  public R visitVariable(VariableElement paramVariableElement, P paramP)
  {
    ElementKind localElementKind = paramVariableElement.getKind();
    switch (localElementKind)
    {
    case ENUM_CONSTANT: 
      return (R)visitVariableAsEnumConstant(paramVariableElement, paramP);
    case EXCEPTION_PARAMETER: 
      return (R)visitVariableAsExceptionParameter(paramVariableElement, paramP);
    case FIELD: 
      return (R)visitVariableAsField(paramVariableElement, paramP);
    case LOCAL_VARIABLE: 
      return (R)visitVariableAsLocalVariable(paramVariableElement, paramP);
    case PARAMETER: 
      return (R)visitVariableAsParameter(paramVariableElement, paramP);
    case RESOURCE_VARIABLE: 
      return (R)visitVariableAsResourceVariable(paramVariableElement, paramP);
    }
    throw new AssertionError("Bad kind " + localElementKind + " for VariableElement" + paramVariableElement);
  }
  
  public R visitVariableAsEnumConstant(VariableElement paramVariableElement, P paramP)
  {
    return (R)defaultAction(paramVariableElement, paramP);
  }
  
  public R visitVariableAsExceptionParameter(VariableElement paramVariableElement, P paramP)
  {
    return (R)defaultAction(paramVariableElement, paramP);
  }
  
  public R visitVariableAsField(VariableElement paramVariableElement, P paramP)
  {
    return (R)defaultAction(paramVariableElement, paramP);
  }
  
  public R visitVariableAsLocalVariable(VariableElement paramVariableElement, P paramP)
  {
    return (R)defaultAction(paramVariableElement, paramP);
  }
  
  public R visitVariableAsParameter(VariableElement paramVariableElement, P paramP)
  {
    return (R)defaultAction(paramVariableElement, paramP);
  }
  
  public R visitVariableAsResourceVariable(VariableElement paramVariableElement, P paramP)
  {
    return (R)visitUnknown(paramVariableElement, paramP);
  }
  
  public R visitExecutable(ExecutableElement paramExecutableElement, P paramP)
  {
    ElementKind localElementKind = paramExecutableElement.getKind();
    switch (localElementKind)
    {
    case CONSTRUCTOR: 
      return (R)visitExecutableAsConstructor(paramExecutableElement, paramP);
    case INSTANCE_INIT: 
      return (R)visitExecutableAsInstanceInit(paramExecutableElement, paramP);
    case METHOD: 
      return (R)visitExecutableAsMethod(paramExecutableElement, paramP);
    case STATIC_INIT: 
      return (R)visitExecutableAsStaticInit(paramExecutableElement, paramP);
    }
    throw new AssertionError("Bad kind " + localElementKind + " for ExecutableElement" + paramExecutableElement);
  }
  
  public R visitExecutableAsConstructor(ExecutableElement paramExecutableElement, P paramP)
  {
    return (R)defaultAction(paramExecutableElement, paramP);
  }
  
  public R visitExecutableAsInstanceInit(ExecutableElement paramExecutableElement, P paramP)
  {
    return (R)defaultAction(paramExecutableElement, paramP);
  }
  
  public R visitExecutableAsMethod(ExecutableElement paramExecutableElement, P paramP)
  {
    return (R)defaultAction(paramExecutableElement, paramP);
  }
  
  public R visitExecutableAsStaticInit(ExecutableElement paramExecutableElement, P paramP)
  {
    return (R)defaultAction(paramExecutableElement, paramP);
  }
  
  public R visitTypeParameter(TypeParameterElement paramTypeParameterElement, P paramP)
  {
    assert (paramTypeParameterElement.getKind() == ElementKind.TYPE_PARAMETER) : "Bad kind on TypeParameterElement";
    return (R)defaultAction(paramTypeParameterElement, paramP);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\util\ElementKindVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */