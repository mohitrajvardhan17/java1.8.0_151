package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;

public class SourceInterpreter
  extends Interpreter<SourceValue>
  implements Opcodes
{
  public SourceInterpreter()
  {
    super(327680);
  }
  
  protected SourceInterpreter(int paramInt)
  {
    super(paramInt);
  }
  
  public SourceValue newValue(Type paramType)
  {
    if (paramType == Type.VOID_TYPE) {
      return null;
    }
    return new SourceValue(paramType == null ? 1 : paramType.getSize());
  }
  
  public SourceValue newOperation(AbstractInsnNode paramAbstractInsnNode)
  {
    int i;
    switch (paramAbstractInsnNode.getOpcode())
    {
    case 9: 
    case 10: 
    case 14: 
    case 15: 
      i = 2;
      break;
    case 18: 
      Object localObject = cst;
      i = ((localObject instanceof Long)) || ((localObject instanceof Double)) ? 2 : 1;
      break;
    case 178: 
      i = Type.getType(desc).getSize();
      break;
    default: 
      i = 1;
    }
    return new SourceValue(i, paramAbstractInsnNode);
  }
  
  public SourceValue copyOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue)
  {
    return new SourceValue(paramSourceValue.getSize(), paramAbstractInsnNode);
  }
  
  public SourceValue unaryOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue)
  {
    int i;
    switch (paramAbstractInsnNode.getOpcode())
    {
    case 117: 
    case 119: 
    case 133: 
    case 135: 
    case 138: 
    case 140: 
    case 141: 
    case 143: 
      i = 2;
      break;
    case 180: 
      i = Type.getType(desc).getSize();
      break;
    default: 
      i = 1;
    }
    return new SourceValue(i, paramAbstractInsnNode);
  }
  
  public SourceValue binaryOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue1, SourceValue paramSourceValue2)
  {
    int i;
    switch (paramAbstractInsnNode.getOpcode())
    {
    case 47: 
    case 49: 
    case 97: 
    case 99: 
    case 101: 
    case 103: 
    case 105: 
    case 107: 
    case 109: 
    case 111: 
    case 113: 
    case 115: 
    case 121: 
    case 123: 
    case 125: 
    case 127: 
    case 129: 
    case 131: 
      i = 2;
      break;
    default: 
      i = 1;
    }
    return new SourceValue(i, paramAbstractInsnNode);
  }
  
  public SourceValue ternaryOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue1, SourceValue paramSourceValue2, SourceValue paramSourceValue3)
  {
    return new SourceValue(1, paramAbstractInsnNode);
  }
  
  public SourceValue naryOperation(AbstractInsnNode paramAbstractInsnNode, List<? extends SourceValue> paramList)
  {
    int j = paramAbstractInsnNode.getOpcode();
    int i;
    if (j == 197)
    {
      i = 1;
    }
    else
    {
      String str = j == 186 ? desc : desc;
      i = Type.getReturnType(str).getSize();
    }
    return new SourceValue(i, paramAbstractInsnNode);
  }
  
  public void returnOperation(AbstractInsnNode paramAbstractInsnNode, SourceValue paramSourceValue1, SourceValue paramSourceValue2) {}
  
  public SourceValue merge(SourceValue paramSourceValue1, SourceValue paramSourceValue2)
  {
    Object localObject;
    if (((insns instanceof SmallSet)) && ((insns instanceof SmallSet)))
    {
      localObject = ((SmallSet)insns).union((SmallSet)insns);
      if ((localObject == insns) && (size == size)) {
        return paramSourceValue1;
      }
      return new SourceValue(Math.min(size, size), (Set)localObject);
    }
    if ((size != size) || (!insns.containsAll(insns)))
    {
      localObject = new HashSet();
      ((HashSet)localObject).addAll(insns);
      ((HashSet)localObject).addAll(insns);
      return new SourceValue(Math.min(size, size), (Set)localObject);
    }
    return paramSourceValue1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\SourceInterpreter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */