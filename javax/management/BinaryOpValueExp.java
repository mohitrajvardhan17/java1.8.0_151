package javax.management;

class BinaryOpValueExp
  extends QueryEval
  implements ValueExp
{
  private static final long serialVersionUID = 1216286847881456786L;
  private int op;
  private ValueExp exp1;
  private ValueExp exp2;
  
  public BinaryOpValueExp() {}
  
  public BinaryOpValueExp(int paramInt, ValueExp paramValueExp1, ValueExp paramValueExp2)
  {
    op = paramInt;
    exp1 = paramValueExp1;
    exp2 = paramValueExp2;
  }
  
  public int getOperator()
  {
    return op;
  }
  
  public ValueExp getLeftValue()
  {
    return exp1;
  }
  
  public ValueExp getRightValue()
  {
    return exp2;
  }
  
  public ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    ValueExp localValueExp1 = exp1.apply(paramObjectName);
    ValueExp localValueExp2 = exp2.apply(paramObjectName);
    boolean bool = localValueExp1 instanceof NumericValueExp;
    if (bool)
    {
      if (((NumericValueExp)localValueExp1).isLong())
      {
        long l1 = ((NumericValueExp)localValueExp1).longValue();
        long l2 = ((NumericValueExp)localValueExp2).longValue();
        switch (op)
        {
        case 0: 
          return Query.value(l1 + l2);
        case 2: 
          return Query.value(l1 * l2);
        case 1: 
          return Query.value(l1 - l2);
        case 3: 
          return Query.value(l1 / l2);
        }
      }
      else
      {
        double d1 = ((NumericValueExp)localValueExp1).doubleValue();
        double d2 = ((NumericValueExp)localValueExp2).doubleValue();
        switch (op)
        {
        case 0: 
          return Query.value(d1 + d2);
        case 2: 
          return Query.value(d1 * d2);
        case 1: 
          return Query.value(d1 - d2);
        case 3: 
          return Query.value(d1 / d2);
        }
      }
    }
    else
    {
      String str1 = ((StringValueExp)localValueExp1).getValue();
      String str2 = ((StringValueExp)localValueExp2).getValue();
      switch (op)
      {
      case 0: 
        return new StringValueExp(str1 + str2);
      }
      throw new BadStringOperationException(opString());
    }
    throw new BadBinaryOpValueExpException(this);
  }
  
  public String toString()
  {
    try
    {
      return parens(exp1, true) + " " + opString() + " " + parens(exp2, false);
    }
    catch (BadBinaryOpValueExpException localBadBinaryOpValueExpException) {}
    return "invalid expression";
  }
  
  private String parens(ValueExp paramValueExp, boolean paramBoolean)
    throws BadBinaryOpValueExpException
  {
    int i;
    if ((paramValueExp instanceof BinaryOpValueExp))
    {
      int j = op;
      if (paramBoolean) {
        i = precedence(j) >= precedence(op) ? 1 : 0;
      } else {
        i = precedence(j) > precedence(op) ? 1 : 0;
      }
    }
    else
    {
      i = 1;
    }
    if (i != 0) {
      return paramValueExp.toString();
    }
    return "(" + paramValueExp + ")";
  }
  
  private int precedence(int paramInt)
    throws BadBinaryOpValueExpException
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
      return 0;
    case 2: 
    case 3: 
      return 1;
    }
    throw new BadBinaryOpValueExpException(this);
  }
  
  private String opString()
    throws BadBinaryOpValueExpException
  {
    switch (op)
    {
    case 0: 
      return "+";
    case 2: 
      return "*";
    case 1: 
      return "-";
    case 3: 
      return "/";
    }
    throw new BadBinaryOpValueExpException(this);
  }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer)
  {
    super.setMBeanServer(paramMBeanServer);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\BinaryOpValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */