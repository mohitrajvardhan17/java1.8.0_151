package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.Vector;

public final class MethodType
  extends Type
{
  private final Type _resultType;
  private final Vector _argsType;
  
  public MethodType(Type paramType)
  {
    _argsType = null;
    _resultType = paramType;
  }
  
  public MethodType(Type paramType1, Type paramType2)
  {
    if (paramType2 != Type.Void)
    {
      _argsType = new Vector();
      _argsType.addElement(paramType2);
    }
    else
    {
      _argsType = null;
    }
    _resultType = paramType1;
  }
  
  public MethodType(Type paramType1, Type paramType2, Type paramType3)
  {
    _argsType = new Vector(2);
    _argsType.addElement(paramType2);
    _argsType.addElement(paramType3);
    _resultType = paramType1;
  }
  
  public MethodType(Type paramType1, Type paramType2, Type paramType3, Type paramType4)
  {
    _argsType = new Vector(3);
    _argsType.addElement(paramType2);
    _argsType.addElement(paramType3);
    _argsType.addElement(paramType4);
    _resultType = paramType1;
  }
  
  public MethodType(Type paramType, Vector paramVector)
  {
    _resultType = paramType;
    _argsType = (paramVector.size() > 0 ? paramVector : null);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("method{");
    if (_argsType != null)
    {
      int i = _argsType.size();
      for (int j = 0; j < i; j++)
      {
        localStringBuffer.append(_argsType.elementAt(j));
        if (j != i - 1) {
          localStringBuffer.append(',');
        }
      }
    }
    else
    {
      localStringBuffer.append("void");
    }
    localStringBuffer.append('}');
    return localStringBuffer.toString();
  }
  
  public String toSignature()
  {
    return toSignature("");
  }
  
  public String toSignature(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    if (_argsType != null)
    {
      int i = _argsType.size();
      for (int j = 0; j < i; j++) {
        localStringBuffer.append(((Type)_argsType.elementAt(j)).toSignature());
      }
    }
    return paramString + ')' + _resultType.toSignature();
  }
  
  public com.sun.org.apache.bcel.internal.generic.Type toJCType()
  {
    return null;
  }
  
  public boolean identicalTo(Type paramType)
  {
    boolean bool = false;
    if ((paramType instanceof MethodType))
    {
      MethodType localMethodType = (MethodType)paramType;
      if (_resultType.identicalTo(_resultType))
      {
        int i = argsCount();
        bool = i == localMethodType.argsCount();
        for (int j = 0; (j < i) && (bool); j++)
        {
          Type localType1 = (Type)_argsType.elementAt(j);
          Type localType2 = (Type)_argsType.elementAt(j);
          bool = localType1.identicalTo(localType2);
        }
      }
    }
    return bool;
  }
  
  public int distanceTo(Type paramType)
  {
    int i = Integer.MAX_VALUE;
    if ((paramType instanceof MethodType))
    {
      MethodType localMethodType = (MethodType)paramType;
      if (_argsType != null)
      {
        int j = _argsType.size();
        if (j == _argsType.size())
        {
          i = 0;
          for (int k = 0; k < j; k++)
          {
            Type localType1 = (Type)_argsType.elementAt(k);
            Type localType2 = (Type)_argsType.elementAt(k);
            int m = localType1.distanceTo(localType2);
            if (m == Integer.MAX_VALUE)
            {
              i = m;
              break;
            }
            i += localType1.distanceTo(localType2);
          }
        }
      }
      else if (_argsType == null)
      {
        i = 0;
      }
    }
    return i;
  }
  
  public Type resultType()
  {
    return _resultType;
  }
  
  public Vector argsType()
  {
    return _argsType;
  }
  
  public int argsCount()
  {
    return _argsType == null ? 0 : _argsType.size();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\MethodType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */