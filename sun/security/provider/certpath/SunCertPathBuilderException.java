package sun.security.provider.certpath;

import java.security.cert.CertPathBuilderException;

public class SunCertPathBuilderException
  extends CertPathBuilderException
{
  private static final long serialVersionUID = -7814288414129264709L;
  private transient AdjacencyList adjList;
  
  public SunCertPathBuilderException() {}
  
  public SunCertPathBuilderException(String paramString)
  {
    super(paramString);
  }
  
  public SunCertPathBuilderException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public SunCertPathBuilderException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
  
  SunCertPathBuilderException(String paramString, AdjacencyList paramAdjacencyList)
  {
    this(paramString);
    adjList = paramAdjacencyList;
  }
  
  SunCertPathBuilderException(String paramString, Throwable paramThrowable, AdjacencyList paramAdjacencyList)
  {
    this(paramString, paramThrowable);
    adjList = paramAdjacencyList;
  }
  
  public AdjacencyList getAdjacencyList()
  {
    return adjList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\SunCertPathBuilderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */