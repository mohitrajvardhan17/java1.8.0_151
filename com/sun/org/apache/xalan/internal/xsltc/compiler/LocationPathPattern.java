package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

public abstract class LocationPathPattern
  extends Pattern
{
  private Template _template;
  private int _importPrecedence;
  private double _priority = NaN.0D;
  private int _position = 0;
  
  public LocationPathPattern() {}
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public void setTemplate(Template paramTemplate)
  {
    _template = paramTemplate;
    _priority = paramTemplate.getPriority();
    _importPrecedence = paramTemplate.getImportPrecedence();
    _position = paramTemplate.getPosition();
  }
  
  public Template getTemplate()
  {
    return _template;
  }
  
  public final double getPriority()
  {
    return Double.isNaN(_priority) ? getDefaultPriority() : _priority;
  }
  
  public double getDefaultPriority()
  {
    return 0.5D;
  }
  
  public boolean noSmallerThan(LocationPathPattern paramLocationPathPattern)
  {
    if (_importPrecedence > _importPrecedence) {
      return true;
    }
    if (_importPrecedence == _importPrecedence)
    {
      if (_priority > _priority) {
        return true;
      }
      if ((_priority == _priority) && (_position > _position)) {
        return true;
      }
    }
    return false;
  }
  
  public abstract StepPattern getKernelPattern();
  
  public abstract void reduceKernelPattern();
  
  public abstract boolean isWildcard();
  
  public int getAxis()
  {
    StepPattern localStepPattern = getKernelPattern();
    return localStepPattern != null ? localStepPattern.getAxis() : 3;
  }
  
  public String toString()
  {
    return "root()";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LocationPathPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */