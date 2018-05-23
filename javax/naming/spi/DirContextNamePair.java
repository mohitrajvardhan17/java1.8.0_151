package javax.naming.spi;

import javax.naming.Name;
import javax.naming.directory.DirContext;

class DirContextNamePair
{
  DirContext ctx;
  Name name;
  
  DirContextNamePair(DirContext paramDirContext, Name paramName)
  {
    ctx = paramDirContext;
    name = paramName;
  }
  
  DirContext getDirContext()
  {
    return ctx;
  }
  
  Name getName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\DirContextNamePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */