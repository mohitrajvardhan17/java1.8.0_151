package java.sql;

import java.util.Map;

public class SQLClientInfoException
  extends SQLException
{
  private Map<String, ClientInfoStatus> failedProperties;
  private static final long serialVersionUID = -4319604256824655880L;
  
  public SQLClientInfoException()
  {
    failedProperties = null;
  }
  
  public SQLClientInfoException(Map<String, ClientInfoStatus> paramMap)
  {
    failedProperties = paramMap;
  }
  
  public SQLClientInfoException(Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable)
  {
    super(paramThrowable != null ? paramThrowable.toString() : null);
    initCause(paramThrowable);
    failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString, Map<String, ClientInfoStatus> paramMap)
  {
    super(paramString);
    failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString, Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable)
  {
    super(paramString);
    initCause(paramThrowable);
    failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, Map<String, ClientInfoStatus> paramMap)
  {
    super(paramString1, paramString2);
    failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable)
  {
    super(paramString1, paramString2);
    initCause(paramThrowable);
    failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, int paramInt, Map<String, ClientInfoStatus> paramMap)
  {
    super(paramString1, paramString2, paramInt);
    failedProperties = paramMap;
  }
  
  public SQLClientInfoException(String paramString1, String paramString2, int paramInt, Map<String, ClientInfoStatus> paramMap, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt);
    initCause(paramThrowable);
    failedProperties = paramMap;
  }
  
  public Map<String, ClientInfoStatus> getFailedProperties()
  {
    return failedProperties;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\SQLClientInfoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */