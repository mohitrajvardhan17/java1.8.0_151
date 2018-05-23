package javax.management;

class BinaryRelQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = -5690656271650491000L;
  private int relOp;
  private ValueExp exp1;
  private ValueExp exp2;
  
  public BinaryRelQueryExp() {}
  
  public BinaryRelQueryExp(int paramInt, ValueExp paramValueExp1, ValueExp paramValueExp2)
  {
    relOp = paramInt;
    exp1 = paramValueExp1;
    exp2 = paramValueExp2;
  }
  
  public int getOperator()
  {
    return relOp;
  }
  
  public ValueExp getLeftValue()
  {
    return exp1;
  }
  
  public ValueExp getRightValue()
  {
    return exp2;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    ValueExp localValueExp1 = exp1.apply(paramObjectName);
    ValueExp localValueExp2 = exp2.apply(paramObjectName);
    boolean bool1 = localValueExp1 instanceof NumericValueExp;
    boolean bool2 = localValueExp1 instanceof BooleanValueExp;
    if (bool1)
    {
      if (((NumericValueExp)localValueExp1).isLong())
      {
        long l1 = ((NumericValueExp)localValueExp1).longValue();
        long l2 = ((NumericValueExp)localValueExp2).longValue();
        switch (relOp)
        {
        case 0: 
          return l1 > l2;
        case 1: 
          return l1 < l2;
        case 2: 
          return l1 >= l2;
        case 3: 
          return l1 <= l2;
        case 4: 
          return l1 == l2;
        }
      }
      else
      {
        double d1 = ((NumericValueExp)localValueExp1).doubleValue();
        double d2 = ((NumericValueExp)localValueExp2).doubleValue();
        switch (relOp)
        {
        case 0: 
          return d1 > d2;
        case 1: 
          return d1 < d2;
        case 2: 
          return d1 >= d2;
        case 3: 
          return d1 <= d2;
        case 4: 
          return d1 == d2;
        }
      }
    }
    else if (bool2)
    {
      boolean bool3 = ((BooleanValueExp)localValueExp1).getValue().booleanValue();
      boolean bool4 = ((BooleanValueExp)localValueExp2).getValue().booleanValue();
      switch (relOp)
      {
      case 0: 
        return (bool3) && (!bool4);
      case 1: 
        return (!bool3) && (bool4);
      case 2: 
        return (bool3) || (!bool4);
      case 3: 
        return (!bool3) || (bool4);
      case 4: 
        return bool3 == bool4;
      }
    }
    else
    {
      String str1 = ((StringValueExp)localValueExp1).getValue();
      String str2 = ((StringValueExp)localValueExp2).getValue();
      switch (relOp)
      {
      case 0: 
        return str1.compareTo(str2) > 0;
      case 1: 
        return str1.compareTo(str2) < 0;
      case 2: 
        return str1.compareTo(str2) >= 0;
      case 3: 
        return str1.compareTo(str2) <= 0;
      case 4: 
        return str1.compareTo(str2) == 0;
      }
    }
    return false;
  }
  
  public String toString()
  {
    return "(" + exp1 + ") " + relOpString() + " (" + exp2 + ")";
  }
  
  private String relOpString()
  {
    switch (relOp)
    {
    case 0: 
      return ">";
    case 1: 
      return "<";
    case 2: 
      return ">=";
    case 3: 
      return "<=";
    case 4: 
      return "=";
    }
    return "=";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\BinaryRelQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */