package javax.management;

public class BadStringOperationException
  extends Exception
{
  private static final long serialVersionUID = 7802201238441662100L;
  private String op;
  
  public BadStringOperationException(String paramString)
  {
    op = paramString;
  }
  
  public String toString()
  {
    return "BadStringOperationException: " + op;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\BadStringOperationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */