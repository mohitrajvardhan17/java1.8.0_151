package sun.security.provider.certpath;

import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;

public class BuildStep
{
  private Vertex vertex;
  private X509Certificate cert;
  private Throwable throwable;
  private int result;
  public static final int POSSIBLE = 1;
  public static final int BACK = 2;
  public static final int FOLLOW = 3;
  public static final int FAIL = 4;
  public static final int SUCCEED = 5;
  
  public BuildStep(Vertex paramVertex, int paramInt)
  {
    vertex = paramVertex;
    if (vertex != null)
    {
      cert = vertex.getCertificate();
      throwable = vertex.getThrowable();
    }
    result = paramInt;
  }
  
  public Vertex getVertex()
  {
    return vertex;
  }
  
  public X509Certificate getCertificate()
  {
    return cert;
  }
  
  public String getIssuerName()
  {
    return getIssuerName(null);
  }
  
  public String getIssuerName(String paramString)
  {
    return cert == null ? paramString : cert.getIssuerX500Principal().toString();
  }
  
  public String getSubjectName()
  {
    return getSubjectName(null);
  }
  
  public String getSubjectName(String paramString)
  {
    return cert == null ? paramString : cert.getSubjectX500Principal().toString();
  }
  
  public Throwable getThrowable()
  {
    return throwable;
  }
  
  public int getResult()
  {
    return result;
  }
  
  public String resultToString(int paramInt)
  {
    String str = "";
    switch (paramInt)
    {
    case 1: 
      str = "Certificate to be tried.\n";
      break;
    case 2: 
      str = "Certificate backed out since path does not satisfy build requirements.\n";
      break;
    case 3: 
      str = "Certificate satisfies conditions.\n";
      break;
    case 4: 
      str = "Certificate backed out since path does not satisfy conditions.\n";
      break;
    case 5: 
      str = "Certificate satisfies conditions.\n";
      break;
    default: 
      str = "Internal error: Invalid step result value.\n";
    }
    return str;
  }
  
  public String toString()
  {
    String str = "Internal Error\n";
    switch (result)
    {
    case 2: 
    case 4: 
      str = resultToString(result);
      str = str + vertex.throwableToString();
      break;
    case 1: 
    case 3: 
    case 5: 
      str = resultToString(result);
      break;
    default: 
      str = "Internal Error: Invalid step result\n";
    }
    return str;
  }
  
  public String verboseToString()
  {
    String str = resultToString(getResult());
    switch (result)
    {
    case 2: 
    case 4: 
      str = str + vertex.throwableToString();
      break;
    case 3: 
    case 5: 
      str = str + vertex.moreToString();
      break;
    case 1: 
      break;
    }
    str = str + "Certificate contains:\n" + vertex.certToString();
    return str;
  }
  
  public String fullToString()
  {
    return resultToString(getResult()) + vertex.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\BuildStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */