package javax.naming.directory;

import javax.naming.Binding;

public class SearchResult
  extends Binding
{
  private Attributes attrs;
  private static final long serialVersionUID = -9158063327699723172L;
  
  public SearchResult(String paramString, Object paramObject, Attributes paramAttributes)
  {
    super(paramString, paramObject);
    attrs = paramAttributes;
  }
  
  public SearchResult(String paramString, Object paramObject, Attributes paramAttributes, boolean paramBoolean)
  {
    super(paramString, paramObject, paramBoolean);
    attrs = paramAttributes;
  }
  
  public SearchResult(String paramString1, String paramString2, Object paramObject, Attributes paramAttributes)
  {
    super(paramString1, paramString2, paramObject);
    attrs = paramAttributes;
  }
  
  public SearchResult(String paramString1, String paramString2, Object paramObject, Attributes paramAttributes, boolean paramBoolean)
  {
    super(paramString1, paramString2, paramObject, paramBoolean);
    attrs = paramAttributes;
  }
  
  public Attributes getAttributes()
  {
    return attrs;
  }
  
  public void setAttributes(Attributes paramAttributes)
  {
    attrs = paramAttributes;
  }
  
  public String toString()
  {
    return super.toString() + ":" + getAttributes();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\directory\SearchResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */