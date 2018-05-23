package javax.xml.soap;

import java.util.Iterator;
import java.util.Vector;

public class MimeHeaders
{
  private Vector headers = new Vector();
  
  public MimeHeaders() {}
  
  public String[] getHeader(String paramString)
  {
    Vector localVector = new Vector();
    for (int i = 0; i < headers.size(); i++)
    {
      MimeHeader localMimeHeader = (MimeHeader)headers.elementAt(i);
      if ((localMimeHeader.getName().equalsIgnoreCase(paramString)) && (localMimeHeader.getValue() != null)) {
        localVector.addElement(localMimeHeader.getValue());
      }
    }
    if (localVector.size() == 0) {
      return null;
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public void setHeader(String paramString1, String paramString2)
  {
    int i = 0;
    if ((paramString1 == null) || (paramString1.equals(""))) {
      throw new IllegalArgumentException("Illegal MimeHeader name");
    }
    for (int j = 0; j < headers.size(); j++)
    {
      MimeHeader localMimeHeader = (MimeHeader)headers.elementAt(j);
      if (localMimeHeader.getName().equalsIgnoreCase(paramString1)) {
        if (i == 0)
        {
          headers.setElementAt(new MimeHeader(localMimeHeader.getName(), paramString2), j);
          i = 1;
        }
        else
        {
          headers.removeElementAt(j--);
        }
      }
    }
    if (i == 0) {
      addHeader(paramString1, paramString2);
    }
  }
  
  public void addHeader(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.equals(""))) {
      throw new IllegalArgumentException("Illegal MimeHeader name");
    }
    int i = headers.size();
    for (int j = i - 1; j >= 0; j--)
    {
      MimeHeader localMimeHeader = (MimeHeader)headers.elementAt(j);
      if (localMimeHeader.getName().equalsIgnoreCase(paramString1))
      {
        headers.insertElementAt(new MimeHeader(paramString1, paramString2), j + 1);
        return;
      }
    }
    headers.addElement(new MimeHeader(paramString1, paramString2));
  }
  
  public void removeHeader(String paramString)
  {
    for (int i = 0; i < headers.size(); i++)
    {
      MimeHeader localMimeHeader = (MimeHeader)headers.elementAt(i);
      if (localMimeHeader.getName().equalsIgnoreCase(paramString)) {
        headers.removeElementAt(i--);
      }
    }
  }
  
  public void removeAllHeaders()
  {
    headers.removeAllElements();
  }
  
  public Iterator getAllHeaders()
  {
    return headers.iterator();
  }
  
  public Iterator getMatchingHeaders(String[] paramArrayOfString)
  {
    return new MatchingIterator(paramArrayOfString, true);
  }
  
  public Iterator getNonMatchingHeaders(String[] paramArrayOfString)
  {
    return new MatchingIterator(paramArrayOfString, false);
  }
  
  class MatchingIterator
    implements Iterator
  {
    private boolean match;
    private Iterator iterator;
    private String[] names;
    private Object nextHeader;
    
    MatchingIterator(String[] paramArrayOfString, boolean paramBoolean)
    {
      match = paramBoolean;
      names = paramArrayOfString;
      iterator = headers.iterator();
    }
    
    private Object nextMatch()
    {
      while (iterator.hasNext())
      {
        MimeHeader localMimeHeader = (MimeHeader)iterator.next();
        if (names == null) {
          return match ? null : localMimeHeader;
        }
        for (int i = 0;; i++)
        {
          if (i >= names.length) {
            break label87;
          }
          if (localMimeHeader.getName().equalsIgnoreCase(names[i]))
          {
            if (!match) {
              break;
            }
            return localMimeHeader;
          }
        }
        label87:
        if (!match) {
          return localMimeHeader;
        }
      }
      return null;
    }
    
    public boolean hasNext()
    {
      if (nextHeader == null) {
        nextHeader = nextMatch();
      }
      return nextHeader != null;
    }
    
    public Object next()
    {
      if (nextHeader != null)
      {
        Object localObject = nextHeader;
        nextHeader = null;
        return localObject;
      }
      if (hasNext()) {
        return nextHeader;
      }
      return null;
    }
    
    public void remove()
    {
      iterator.remove();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\MimeHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */