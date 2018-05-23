package java.net;

public final class PasswordAuthentication
{
  private String userName;
  private char[] password;
  
  public PasswordAuthentication(String paramString, char[] paramArrayOfChar)
  {
    userName = paramString;
    password = ((char[])paramArrayOfChar.clone());
  }
  
  public String getUserName()
  {
    return userName;
  }
  
  public char[] getPassword()
  {
    return password;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\PasswordAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */