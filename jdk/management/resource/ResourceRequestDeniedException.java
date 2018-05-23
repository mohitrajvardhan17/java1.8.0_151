package jdk.management.resource;

public class ResourceRequestDeniedException
  extends RuntimeException
{
  private static final long serialVersionUID = 4861402271690587669L;
  
  public ResourceRequestDeniedException() {}
  
  public ResourceRequestDeniedException(String paramString)
  {
    super(paramString);
  }
  
  public ResourceRequestDeniedException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\ResourceRequestDeniedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */