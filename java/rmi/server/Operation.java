package java.rmi.server;

@Deprecated
public class Operation
{
  private String operation;
  
  @Deprecated
  public Operation(String paramString)
  {
    operation = paramString;
  }
  
  @Deprecated
  public String getOperation()
  {
    return operation;
  }
  
  @Deprecated
  public String toString()
  {
    return operation;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\Operation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */