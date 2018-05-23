package javax.naming;

public class LinkException
  extends NamingException
{
  protected Name linkResolvedName = null;
  protected Object linkResolvedObj = null;
  protected Name linkRemainingName = null;
  protected String linkExplanation = null;
  private static final long serialVersionUID = -7967662604076777712L;
  
  public LinkException(String paramString)
  {
    super(paramString);
  }
  
  public LinkException() {}
  
  public Name getLinkResolvedName()
  {
    return linkResolvedName;
  }
  
  public Name getLinkRemainingName()
  {
    return linkRemainingName;
  }
  
  public Object getLinkResolvedObj()
  {
    return linkResolvedObj;
  }
  
  public String getLinkExplanation()
  {
    return linkExplanation;
  }
  
  public void setLinkExplanation(String paramString)
  {
    linkExplanation = paramString;
  }
  
  public void setLinkResolvedName(Name paramName)
  {
    if (paramName != null) {
      linkResolvedName = ((Name)paramName.clone());
    } else {
      linkResolvedName = null;
    }
  }
  
  public void setLinkRemainingName(Name paramName)
  {
    if (paramName != null) {
      linkRemainingName = ((Name)paramName.clone());
    } else {
      linkRemainingName = null;
    }
  }
  
  public void setLinkResolvedObj(Object paramObject)
  {
    linkResolvedObj = paramObject;
  }
  
  public String toString()
  {
    return super.toString() + "; Link Remaining Name: '" + linkRemainingName + "'";
  }
  
  public String toString(boolean paramBoolean)
  {
    if ((!paramBoolean) || (linkResolvedObj == null)) {
      return toString();
    }
    return toString() + "; Link Resolved Object: " + linkResolvedObj;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\LinkException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */