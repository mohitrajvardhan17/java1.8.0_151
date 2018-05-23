package java.util.jar;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.List;
import sun.misc.JavaUtilJarAccess;

class JavaUtilJarAccessImpl
  implements JavaUtilJarAccess
{
  JavaUtilJarAccessImpl() {}
  
  public boolean jarFileHasClassPathAttribute(JarFile paramJarFile)
    throws IOException
  {
    return paramJarFile.hasClassPathAttribute();
  }
  
  public CodeSource[] getCodeSources(JarFile paramJarFile, URL paramURL)
  {
    return paramJarFile.getCodeSources(paramURL);
  }
  
  public CodeSource getCodeSource(JarFile paramJarFile, URL paramURL, String paramString)
  {
    return paramJarFile.getCodeSource(paramURL, paramString);
  }
  
  public Enumeration<String> entryNames(JarFile paramJarFile, CodeSource[] paramArrayOfCodeSource)
  {
    return paramJarFile.entryNames(paramArrayOfCodeSource);
  }
  
  public Enumeration<JarEntry> entries2(JarFile paramJarFile)
  {
    return paramJarFile.entries2();
  }
  
  public void setEagerValidation(JarFile paramJarFile, boolean paramBoolean)
  {
    paramJarFile.setEagerValidation(paramBoolean);
  }
  
  public List<Object> getManifestDigests(JarFile paramJarFile)
  {
    return paramJarFile.getManifestDigests();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\JavaUtilJarAccessImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */