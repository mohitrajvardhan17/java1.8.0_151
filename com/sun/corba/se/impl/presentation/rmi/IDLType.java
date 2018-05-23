package com.sun.corba.se.impl.presentation.rmi;

public class IDLType
{
  private Class cl_;
  private String[] modules_;
  private String memberName_;
  
  public IDLType(Class paramClass, String[] paramArrayOfString, String paramString)
  {
    cl_ = paramClass;
    modules_ = paramArrayOfString;
    memberName_ = paramString;
  }
  
  public IDLType(Class paramClass, String paramString)
  {
    this(paramClass, new String[0], paramString);
  }
  
  public Class getJavaClass()
  {
    return cl_;
  }
  
  public String[] getModules()
  {
    return modules_;
  }
  
  public String makeConcatenatedName(char paramChar, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < modules_.length; i++)
    {
      String str = modules_[i];
      if (i > 0) {
        localStringBuffer.append(paramChar);
      }
      if ((paramBoolean) && (IDLNameTranslatorImpl.isIDLKeyword(str))) {
        str = IDLNameTranslatorImpl.mangleIDLKeywordClash(str);
      }
      localStringBuffer.append(str);
    }
    return localStringBuffer.toString();
  }
  
  public String getModuleName()
  {
    return makeConcatenatedName('_', false);
  }
  
  public String getExceptionName()
  {
    String str1 = makeConcatenatedName('/', true);
    String str2 = "Exception";
    String str3 = memberName_;
    if (str3.endsWith(str2))
    {
      int i = str3.length() - str2.length();
      str3 = str3.substring(0, i);
    }
    str3 = str3 + "Ex";
    if (str1.length() == 0) {
      return "IDL:" + str3 + ":1.0";
    }
    return "IDL:" + str1 + '/' + str3 + ":1.0";
  }
  
  public String getMemberName()
  {
    return memberName_;
  }
  
  public boolean hasModule()
  {
    return modules_.length > 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\IDLType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */