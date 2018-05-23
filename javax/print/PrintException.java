package javax.print;

public class PrintException
  extends Exception
{
  public PrintException() {}
  
  public PrintException(String paramString)
  {
    super(paramString);
  }
  
  public PrintException(Exception paramException)
  {
    super(paramException);
  }
  
  public PrintException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\PrintException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */