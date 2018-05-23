package javax.management;

class AndQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = -1081892073854801359L;
  private QueryExp exp1;
  private QueryExp exp2;
  
  public AndQueryExp() {}
  
  public AndQueryExp(QueryExp paramQueryExp1, QueryExp paramQueryExp2)
  {
    exp1 = paramQueryExp1;
    exp2 = paramQueryExp2;
  }
  
  public QueryExp getLeftExp()
  {
    return exp1;
  }
  
  public QueryExp getRightExp()
  {
    return exp2;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    return (exp1.apply(paramObjectName)) && (exp2.apply(paramObjectName));
  }
  
  public String toString()
  {
    return "(" + exp1 + ") and (" + exp2 + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\AndQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */