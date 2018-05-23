package javax.management;

public class InvalidApplicationException
  extends Exception
{
  private static final long serialVersionUID = -3048022274675537269L;
  private Object val;
  
  public InvalidApplicationException(Object paramObject)
  {
    val = paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\InvalidApplicationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */