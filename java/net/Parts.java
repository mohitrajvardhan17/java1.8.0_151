package java.net;

class Parts
{
  String path;
  String query;
  String ref;
  
  Parts(String paramString)
  {
    int i = paramString.indexOf('#');
    ref = (i < 0 ? null : paramString.substring(i + 1));
    paramString = i < 0 ? paramString : paramString.substring(0, i);
    int j = paramString.lastIndexOf('?');
    if (j != -1)
    {
      query = paramString.substring(j + 1);
      path = paramString.substring(0, j);
    }
    else
    {
      path = paramString;
    }
  }
  
  String getPath()
  {
    return path;
  }
  
  String getQuery()
  {
    return query;
  }
  
  String getRef()
  {
    return ref;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\Parts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */