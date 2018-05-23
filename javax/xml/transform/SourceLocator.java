package javax.xml.transform;

public abstract interface SourceLocator
{
  public abstract String getPublicId();
  
  public abstract String getSystemId();
  
  public abstract int getLineNumber();
  
  public abstract int getColumnNumber();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\transform\SourceLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */