package javax.naming.spi;

import javax.naming.directory.DirContext;

class DirContextStringPair
{
  DirContext ctx;
  String str;
  
  DirContextStringPair(DirContext paramDirContext, String paramString)
  {
    ctx = paramDirContext;
    str = paramString;
  }
  
  DirContext getDirContext()
  {
    return ctx;
  }
  
  String getString()
  {
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\DirContextStringPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */