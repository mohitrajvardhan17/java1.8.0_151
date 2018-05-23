package javax.naming;

public class LinkRef
  extends Reference
{
  static final String linkClassName = LinkRef.class.getName();
  static final String linkAddrType = "LinkAddress";
  private static final long serialVersionUID = -5386290613498931298L;
  
  public LinkRef(Name paramName)
  {
    super(linkClassName, new StringRefAddr("LinkAddress", paramName.toString()));
  }
  
  public LinkRef(String paramString)
  {
    super(linkClassName, new StringRefAddr("LinkAddress", paramString));
  }
  
  public String getLinkName()
    throws NamingException
  {
    if ((className != null) && (className.equals(linkClassName)))
    {
      RefAddr localRefAddr = get("LinkAddress");
      if ((localRefAddr != null) && ((localRefAddr instanceof StringRefAddr))) {
        return (String)((StringRefAddr)localRefAddr).getContent();
      }
    }
    throw new MalformedLinkException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\LinkRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */