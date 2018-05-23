package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;

final class ParameterRef
  extends VariableRefBase
{
  QName _name = null;
  
  public ParameterRef(Param paramParam)
  {
    super(paramParam);
    _name = _name;
  }
  
  public String toString()
  {
    return "parameter-ref(" + _variable.getName() + '/' + _variable.getType() + ')';
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    String str1 = BasisLibrary.mapQNameToJavaName(_name.toString());
    String str2 = _type.toSignature();
    Object localObject;
    if (_variable.isLocal())
    {
      if (paramClassGenerator.isExternal())
      {
        for (localObject = _closure; (localObject != null) && (!((Closure)localObject).inInnerClass()); localObject = ((Closure)localObject).getParentClosure()) {}
        if (localObject != null)
        {
          localInstructionList.append(ALOAD_0);
          localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref(((Closure)localObject).getInnerClassName(), str1, str2)));
        }
        else
        {
          localInstructionList.append(_variable.loadInstruction());
        }
      }
      else
      {
        localInstructionList.append(_variable.loadInstruction());
      }
    }
    else
    {
      localObject = paramClassGenerator.getClassName();
      localInstructionList.append(paramClassGenerator.loadTranslet());
      if (paramClassGenerator.isExternal()) {
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass((String)localObject)));
      }
      localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref((String)localObject, str1, str2)));
    }
    if ((_variable.getType() instanceof NodeSetType))
    {
      int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "cloneIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(new INVOKEINTERFACE(i, 1));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\ParameterRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */