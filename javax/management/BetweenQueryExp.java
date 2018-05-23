package javax.management;

class BetweenQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = -2933597532866307444L;
  private ValueExp exp1;
  private ValueExp exp2;
  private ValueExp exp3;
  
  public BetweenQueryExp() {}
  
  public BetweenQueryExp(ValueExp paramValueExp1, ValueExp paramValueExp2, ValueExp paramValueExp3)
  {
    exp1 = paramValueExp1;
    exp2 = paramValueExp2;
    exp3 = paramValueExp3;
  }
  
  public ValueExp getCheckedValue()
  {
    return exp1;
  }
  
  public ValueExp getLowerBound()
  {
    return exp2;
  }
  
  public ValueExp getUpperBound()
  {
    return exp3;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    ValueExp localValueExp1 = exp1.apply(paramObjectName);
    ValueExp localValueExp2 = exp2.apply(paramObjectName);
    ValueExp localValueExp3 = exp3.apply(paramObjectName);
    boolean bool = localValueExp1 instanceof NumericValueExp;
    if (bool)
    {
      if (((NumericValueExp)localValueExp1).isLong())
      {
        long l1 = ((NumericValueExp)localValueExp1).longValue();
        long l2 = ((NumericValueExp)localValueExp2).longValue();
        long l3 = ((NumericValueExp)localValueExp3).longValue();
        return (l2 <= l1) && (l1 <= l3);
      }
      double d1 = ((NumericValueExp)localValueExp1).doubleValue();
      double d2 = ((NumericValueExp)localValueExp2).doubleValue();
      double d3 = ((NumericValueExp)localValueExp3).doubleValue();
      return (d2 <= d1) && (d1 <= d3);
    }
    String str1 = ((StringValueExp)localValueExp1).getValue();
    String str2 = ((StringValueExp)localValueExp2).getValue();
    String str3 = ((StringValueExp)localValueExp3).getValue();
    return (str2.compareTo(str1) <= 0) && (str1.compareTo(str3) <= 0);
  }
  
  public String toString()
  {
    return "(" + exp1 + ") between (" + exp2 + ") and (" + exp3 + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\BetweenQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */