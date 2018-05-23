package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.IincInsnNode;
import jdk.internal.org.objectweb.asm.tree.InvokeDynamicInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MultiANewArrayInsnNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

public class Frame<V extends Value>
{
  private V returnValue;
  private V[] values;
  private int locals;
  private int top;
  
  public Frame(int paramInt1, int paramInt2)
  {
    values = ((Value[])new Value[paramInt1 + paramInt2]);
    locals = paramInt1;
  }
  
  public Frame(Frame<? extends V> paramFrame)
  {
    this(locals, values.length - locals);
    init(paramFrame);
  }
  
  public Frame<V> init(Frame<? extends V> paramFrame)
  {
    returnValue = returnValue;
    System.arraycopy(values, 0, values, 0, values.length);
    top = top;
    return this;
  }
  
  public void setReturn(V paramV)
  {
    returnValue = paramV;
  }
  
  public int getLocals()
  {
    return locals;
  }
  
  public int getMaxStackSize()
  {
    return values.length - locals;
  }
  
  public V getLocal(int paramInt)
    throws IndexOutOfBoundsException
  {
    if (paramInt >= locals) {
      throw new IndexOutOfBoundsException("Trying to access an inexistant local variable");
    }
    return values[paramInt];
  }
  
  public void setLocal(int paramInt, V paramV)
    throws IndexOutOfBoundsException
  {
    if (paramInt >= locals) {
      throw new IndexOutOfBoundsException("Trying to access an inexistant local variable " + paramInt);
    }
    values[paramInt] = paramV;
  }
  
  public int getStackSize()
  {
    return top;
  }
  
  public V getStack(int paramInt)
    throws IndexOutOfBoundsException
  {
    return values[(paramInt + locals)];
  }
  
  public void clearStack()
  {
    top = 0;
  }
  
  public V pop()
    throws IndexOutOfBoundsException
  {
    if (top == 0) {
      throw new IndexOutOfBoundsException("Cannot pop operand off an empty stack.");
    }
    return values[(--top + locals)];
  }
  
  public void push(V paramV)
    throws IndexOutOfBoundsException
  {
    if (top + locals >= values.length) {
      throw new IndexOutOfBoundsException("Insufficient maximum stack size.");
    }
    values[(top++ + locals)] = paramV;
  }
  
  public void execute(AbstractInsnNode paramAbstractInsnNode, Interpreter<V> paramInterpreter)
    throws AnalyzerException
  {
    Value localValue2;
    Value localValue1;
    int i;
    Object localObject;
    Value localValue3;
    ArrayList localArrayList;
    int k;
    switch (paramAbstractInsnNode.getOpcode())
    {
    case 0: 
      break;
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
      push(paramInterpreter.newOperation(paramAbstractInsnNode));
      break;
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      push(paramInterpreter.copyOperation(paramAbstractInsnNode, getLocal(var)));
      break;
    case 46: 
    case 47: 
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
      localValue2 = pop();
      localValue1 = pop();
      push(paramInterpreter.binaryOperation(paramAbstractInsnNode, localValue1, localValue2));
      break;
    case 54: 
    case 55: 
    case 56: 
    case 57: 
    case 58: 
      localValue1 = paramInterpreter.copyOperation(paramAbstractInsnNode, pop());
      i = var;
      setLocal(i, localValue1);
      if (localValue1.getSize() == 2) {
        setLocal(i + 1, paramInterpreter.newValue(null));
      }
      if (i > 0)
      {
        localObject = getLocal(i - 1);
        if ((localObject != null) && (((Value)localObject).getSize() == 2)) {
          setLocal(i - 1, paramInterpreter.newValue(null));
        }
      }
      break;
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
      localValue3 = pop();
      localValue2 = pop();
      localValue1 = pop();
      paramInterpreter.ternaryOperation(paramAbstractInsnNode, localValue1, localValue2, localValue3);
      break;
    case 87: 
      if (pop().getSize() == 2) {
        throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of POP");
      }
      break;
    case 88: 
      if ((pop().getSize() == 1) && (pop().getSize() != 1)) {
        throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of POP2");
      }
      break;
    case 89: 
      localValue1 = pop();
      if (localValue1.getSize() != 1) {
        throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP");
      }
      push(localValue1);
      push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
      break;
    case 90: 
      localValue1 = pop();
      localValue2 = pop();
      if ((localValue1.getSize() != 1) || (localValue2.getSize() != 1)) {
        throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP_X1");
      }
      push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
      push(localValue2);
      push(localValue1);
      break;
    case 91: 
      localValue1 = pop();
      if (localValue1.getSize() == 1)
      {
        localValue2 = pop();
        if (localValue2.getSize() == 1)
        {
          localValue3 = pop();
          if (localValue3.getSize() == 1)
          {
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
            push(localValue3);
            push(localValue2);
            push(localValue1);
            break;
          }
        }
        else
        {
          push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
          push(localValue2);
          push(localValue1);
          break;
        }
      }
      throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP_X2");
    case 92: 
      localValue1 = pop();
      if (localValue1.getSize() == 1)
      {
        localValue2 = pop();
        if (localValue2.getSize() == 1)
        {
          push(localValue2);
          push(localValue1);
          push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue2));
          push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
          break;
        }
      }
      else
      {
        push(localValue1);
        push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
        break;
      }
      throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2");
    case 93: 
      localValue1 = pop();
      if (localValue1.getSize() == 1)
      {
        localValue2 = pop();
        if (localValue2.getSize() == 1)
        {
          localValue3 = pop();
          if (localValue3.getSize() == 1)
          {
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue2));
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
            push(localValue3);
            push(localValue2);
            push(localValue1);
            break;
          }
        }
      }
      else
      {
        localValue2 = pop();
        if (localValue2.getSize() == 1)
        {
          push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
          push(localValue2);
          push(localValue1);
          break;
        }
      }
      throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X1");
    case 94: 
      localValue1 = pop();
      if (localValue1.getSize() == 1)
      {
        localValue2 = pop();
        if (localValue2.getSize() == 1)
        {
          localValue3 = pop();
          if (localValue3.getSize() == 1)
          {
            Value localValue4 = pop();
            if (localValue4.getSize() == 1)
            {
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue2));
              push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
              push(localValue4);
              push(localValue3);
              push(localValue2);
              push(localValue1);
              break;
            }
          }
          else
          {
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue2));
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
            push(localValue3);
            push(localValue2);
            push(localValue1);
            break;
          }
        }
      }
      else
      {
        localValue2 = pop();
        if (localValue2.getSize() == 1)
        {
          localValue3 = pop();
          if (localValue3.getSize() == 1)
          {
            push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
            push(localValue3);
            push(localValue2);
            push(localValue1);
            break;
          }
        }
        else
        {
          push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
          push(localValue2);
          push(localValue1);
          break;
        }
      }
      throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of DUP2_X2");
    case 95: 
      localValue2 = pop();
      localValue1 = pop();
      if ((localValue1.getSize() != 1) || (localValue2.getSize() != 1)) {
        throw new AnalyzerException(paramAbstractInsnNode, "Illegal use of SWAP");
      }
      push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue2));
      push(paramInterpreter.copyOperation(paramAbstractInsnNode, localValue1));
      break;
    case 96: 
    case 97: 
    case 98: 
    case 99: 
    case 100: 
    case 101: 
    case 102: 
    case 103: 
    case 104: 
    case 105: 
    case 106: 
    case 107: 
    case 108: 
    case 109: 
    case 110: 
    case 111: 
    case 112: 
    case 113: 
    case 114: 
    case 115: 
      localValue2 = pop();
      localValue1 = pop();
      push(paramInterpreter.binaryOperation(paramAbstractInsnNode, localValue1, localValue2));
      break;
    case 116: 
    case 117: 
    case 118: 
    case 119: 
      push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      break;
    case 120: 
    case 121: 
    case 122: 
    case 123: 
    case 124: 
    case 125: 
    case 126: 
    case 127: 
    case 128: 
    case 129: 
    case 130: 
    case 131: 
      localValue2 = pop();
      localValue1 = pop();
      push(paramInterpreter.binaryOperation(paramAbstractInsnNode, localValue1, localValue2));
      break;
    case 132: 
      i = var;
      setLocal(i, paramInterpreter.unaryOperation(paramAbstractInsnNode, getLocal(i)));
      break;
    case 133: 
    case 134: 
    case 135: 
    case 136: 
    case 137: 
    case 138: 
    case 139: 
    case 140: 
    case 141: 
    case 142: 
    case 143: 
    case 144: 
    case 145: 
    case 146: 
    case 147: 
      push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      break;
    case 148: 
    case 149: 
    case 150: 
    case 151: 
    case 152: 
      localValue2 = pop();
      localValue1 = pop();
      push(paramInterpreter.binaryOperation(paramAbstractInsnNode, localValue1, localValue2));
      break;
    case 153: 
    case 154: 
    case 155: 
    case 156: 
    case 157: 
    case 158: 
      paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      break;
    case 159: 
    case 160: 
    case 161: 
    case 162: 
    case 163: 
    case 164: 
    case 165: 
    case 166: 
      localValue2 = pop();
      localValue1 = pop();
      paramInterpreter.binaryOperation(paramAbstractInsnNode, localValue1, localValue2);
      break;
    case 167: 
      break;
    case 168: 
      push(paramInterpreter.newOperation(paramAbstractInsnNode));
      break;
    case 169: 
      break;
    case 170: 
    case 171: 
      paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      break;
    case 172: 
    case 173: 
    case 174: 
    case 175: 
    case 176: 
      localValue1 = pop();
      paramInterpreter.unaryOperation(paramAbstractInsnNode, localValue1);
      paramInterpreter.returnOperation(paramAbstractInsnNode, localValue1, returnValue);
      break;
    case 177: 
      if (returnValue != null) {
        throw new AnalyzerException(paramAbstractInsnNode, "Incompatible return type");
      }
      break;
    case 178: 
      push(paramInterpreter.newOperation(paramAbstractInsnNode));
      break;
    case 179: 
      paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      break;
    case 180: 
      push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      break;
    case 181: 
      localValue2 = pop();
      localValue1 = pop();
      paramInterpreter.binaryOperation(paramAbstractInsnNode, localValue1, localValue2);
      break;
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      localArrayList = new ArrayList();
      localObject = desc;
      for (k = Type.getArgumentTypes((String)localObject).length; k > 0; k--) {
        localArrayList.add(0, pop());
      }
      if (paramAbstractInsnNode.getOpcode() != 184) {
        localArrayList.add(0, pop());
      }
      if (Type.getReturnType((String)localObject) == Type.VOID_TYPE) {
        paramInterpreter.naryOperation(paramAbstractInsnNode, localArrayList);
      } else {
        push(paramInterpreter.naryOperation(paramAbstractInsnNode, localArrayList));
      }
      break;
    case 186: 
      localArrayList = new ArrayList();
      localObject = desc;
      for (k = Type.getArgumentTypes((String)localObject).length; k > 0; k--) {
        localArrayList.add(0, pop());
      }
      if (Type.getReturnType((String)localObject) == Type.VOID_TYPE) {
        paramInterpreter.naryOperation(paramAbstractInsnNode, localArrayList);
      } else {
        push(paramInterpreter.naryOperation(paramAbstractInsnNode, localArrayList));
      }
      break;
    case 187: 
      push(paramInterpreter.newOperation(paramAbstractInsnNode));
      break;
    case 188: 
    case 189: 
    case 190: 
      push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      break;
    case 191: 
      paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      break;
    case 192: 
    case 193: 
      push(paramInterpreter.unaryOperation(paramAbstractInsnNode, pop()));
      break;
    case 194: 
    case 195: 
      paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      break;
    case 197: 
      localArrayList = new ArrayList();
      for (int j = dims; j > 0; j--) {
        localArrayList.add(0, pop());
      }
      push(paramInterpreter.naryOperation(paramAbstractInsnNode, localArrayList));
      break;
    case 198: 
    case 199: 
      paramInterpreter.unaryOperation(paramAbstractInsnNode, pop());
      break;
    case 19: 
    case 20: 
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 37: 
    case 38: 
    case 39: 
    case 40: 
    case 41: 
    case 42: 
    case 43: 
    case 44: 
    case 45: 
    case 59: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 64: 
    case 65: 
    case 66: 
    case 67: 
    case 68: 
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 196: 
    default: 
      throw new RuntimeException("Illegal opcode " + paramAbstractInsnNode.getOpcode());
    }
  }
  
  public boolean merge(Frame<? extends V> paramFrame, Interpreter<V> paramInterpreter)
    throws AnalyzerException
  {
    if (top != top) {
      throw new AnalyzerException(null, "Incompatible stack heights");
    }
    boolean bool = false;
    for (int i = 0; i < locals + top; i++)
    {
      Value localValue = paramInterpreter.merge(values[i], values[i]);
      if (!localValue.equals(values[i]))
      {
        values[i] = localValue;
        bool = true;
      }
    }
    return bool;
  }
  
  public boolean merge(Frame<? extends V> paramFrame, boolean[] paramArrayOfBoolean)
  {
    boolean bool = false;
    for (int i = 0; i < locals; i++) {
      if ((paramArrayOfBoolean[i] == 0) && (!values[i].equals(values[i])))
      {
        values[i] = values[i];
        bool = true;
      }
    }
    return bool;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < getLocals(); i++) {
      localStringBuilder.append(getLocal(i));
    }
    localStringBuilder.append(' ');
    for (i = 0; i < getStackSize(); i++) {
      localStringBuilder.append(getStack(i).toString());
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\Frame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */