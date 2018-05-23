package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Objects;

class VariableRefBase
  extends Expression
{
  protected VariableBase _variable;
  protected Closure _closure = null;
  
  public VariableRefBase(VariableBase paramVariableBase)
  {
    _variable = paramVariableBase;
    paramVariableBase.addReference(this);
  }
  
  public VariableRefBase()
  {
    _variable = null;
  }
  
  public VariableBase getVariable()
  {
    return _variable;
  }
  
  public void addParentDependency()
  {
    for (Object localObject1 = this; (localObject1 != null) && (!(localObject1 instanceof TopLevelElement)); localObject1 = ((SyntaxTreeNode)localObject1).getParent()) {}
    TopLevelElement localTopLevelElement = (TopLevelElement)localObject1;
    if (localTopLevelElement != null)
    {
      Object localObject2 = _variable;
      if (_variable._ignore) {
        if ((_variable instanceof Variable)) {
          localObject2 = localTopLevelElement.getSymbolTable().lookupVariable(_variable._name);
        } else if ((_variable instanceof Param)) {
          localObject2 = localTopLevelElement.getSymbolTable().lookupParam(_variable._name);
        }
      }
      localTopLevelElement.addDependency((TopLevelElement)localObject2);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject == this) || (((paramObject instanceof VariableRefBase)) && (_variable == _variable));
  }
  
  public int hashCode()
  {
    return Objects.hashCode(_variable);
  }
  
  public String toString()
  {
    return "variable-ref(" + _variable.getName() + '/' + _variable.getType() + ')';
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_type != null) {
      return _type;
    }
    if (_variable.isLocal())
    {
      SyntaxTreeNode localSyntaxTreeNode = getParent();
      do
      {
        if ((localSyntaxTreeNode instanceof Closure))
        {
          _closure = ((Closure)localSyntaxTreeNode);
          break;
        }
        if ((localSyntaxTreeNode instanceof TopLevelElement)) {
          break;
        }
        localSyntaxTreeNode = localSyntaxTreeNode.getParent();
      } while (localSyntaxTreeNode != null);
      if (_closure != null) {
        _closure.addVariable(this);
      }
    }
    _type = _variable.getType();
    if (_type == null)
    {
      _variable.typeCheck(paramSymbolTable);
      _type = _variable.getType();
    }
    addParentDependency();
    return _type;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\VariableRefBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */