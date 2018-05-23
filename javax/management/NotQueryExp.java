package javax.management;

class NotQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = 5269643775896723397L;
  private QueryExp exp;
  
  public NotQueryExp() {}
  
  public NotQueryExp(QueryExp paramQueryExp)
  {
    exp = paramQueryExp;
  }
  
  public QueryExp getNegatedExp()
  {
    return exp;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    return !exp.apply(paramObjectName);
  }
  
  public String toString()
  {
    return "not (" + exp + ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\NotQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */