package javax.management;

class InQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = -5801329450358952434L;
  private ValueExp val;
  private ValueExp[] valueList;
  
  public InQueryExp() {}
  
  public InQueryExp(ValueExp paramValueExp, ValueExp[] paramArrayOfValueExp)
  {
    val = paramValueExp;
    valueList = paramArrayOfValueExp;
  }
  
  public ValueExp getCheckedValue()
  {
    return val;
  }
  
  public ValueExp[] getExplicitValues()
  {
    return valueList;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    if (valueList != null)
    {
      ValueExp localValueExp1 = val.apply(paramObjectName);
      boolean bool = localValueExp1 instanceof NumericValueExp;
      for (ValueExp localValueExp2 : valueList)
      {
        localValueExp2 = localValueExp2.apply(paramObjectName);
        if (bool)
        {
          if (((NumericValueExp)localValueExp2).doubleValue() == ((NumericValueExp)localValueExp1).doubleValue()) {
            return true;
          }
        }
        else if (((StringValueExp)localValueExp2).getValue().equals(((StringValueExp)localValueExp1).getValue())) {
          return true;
        }
      }
    }
    return false;
  }
  
  public String toString()
  {
    return val + " in (" + generateValueList() + ")";
  }
  
  private String generateValueList()
  {
    if ((valueList == null) || (valueList.length == 0)) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(valueList[0].toString());
    for (int i = 1; i < valueList.length; i++)
    {
      localStringBuilder.append(", ");
      localStringBuilder.append(valueList[i]);
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\InQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */