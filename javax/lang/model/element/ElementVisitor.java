package javax.lang.model.element;

public abstract interface ElementVisitor<R, P>
{
  public abstract R visit(Element paramElement, P paramP);
  
  public abstract R visit(Element paramElement);
  
  public abstract R visitPackage(PackageElement paramPackageElement, P paramP);
  
  public abstract R visitType(TypeElement paramTypeElement, P paramP);
  
  public abstract R visitVariable(VariableElement paramVariableElement, P paramP);
  
  public abstract R visitExecutable(ExecutableElement paramExecutableElement, P paramP);
  
  public abstract R visitTypeParameter(TypeParameterElement paramTypeParameterElement, P paramP);
  
  public abstract R visitUnknown(Element paramElement, P paramP);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\lang\model\element\ElementVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */