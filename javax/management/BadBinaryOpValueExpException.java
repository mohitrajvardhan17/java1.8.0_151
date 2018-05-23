package javax.management;

public class BadBinaryOpValueExpException
  extends Exception
{
  private static final long serialVersionUID = 5068475589449021227L;
  private ValueExp exp;
  
  public BadBinaryOpValueExpException(ValueExp paramValueExp)
  {
    exp = paramValueExp;
  }
  
  public ValueExp getExp()
  {
    return exp;
  }
  
  public String toString()
  {
    return "BadBinaryOpValueExpException: " + exp;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\BadBinaryOpValueExpException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */