package java.net;

public abstract interface CookiePolicy
{
  public static final CookiePolicy ACCEPT_ALL = new CookiePolicy()
  {
    public boolean shouldAccept(URI paramAnonymousURI, HttpCookie paramAnonymousHttpCookie)
    {
      return true;
    }
  };
  public static final CookiePolicy ACCEPT_NONE = new CookiePolicy()
  {
    public boolean shouldAccept(URI paramAnonymousURI, HttpCookie paramAnonymousHttpCookie)
    {
      return false;
    }
  };
  public static final CookiePolicy ACCEPT_ORIGINAL_SERVER = new CookiePolicy()
  {
    public boolean shouldAccept(URI paramAnonymousURI, HttpCookie paramAnonymousHttpCookie)
    {
      if ((paramAnonymousURI == null) || (paramAnonymousHttpCookie == null)) {
        return false;
      }
      return HttpCookie.domainMatches(paramAnonymousHttpCookie.getDomain(), paramAnonymousURI.getHost());
    }
  };
  
  public abstract boolean shouldAccept(URI paramURI, HttpCookie paramHttpCookie);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\CookiePolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */