package sun.security.tools.policytool;

class RuntimePerm
  extends Perm
{
  public RuntimePerm()
  {
    super("RuntimePermission", "java.lang.RuntimePermission", new String[] { "createClassLoader", "getClassLoader", "setContextClassLoader", "enableContextClassLoaderOverride", "setSecurityManager", "createSecurityManager", "getenv.<" + PolicyTool.getMessage("environment.variable.name") + ">", "exitVM", "shutdownHooks", "setFactory", "setIO", "modifyThread", "stopThread", "modifyThreadGroup", "getProtectionDomain", "readFileDescriptor", "writeFileDescriptor", "loadLibrary.<" + PolicyTool.getMessage("library.name") + ">", "accessClassInPackage.<" + PolicyTool.getMessage("package.name") + ">", "defineClassInPackage.<" + PolicyTool.getMessage("package.name") + ">", "accessDeclaredMembers", "queuePrintJob", "getStackTrace", "setDefaultUncaughtExceptionHandler", "preferences", "usePolicy" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\RuntimePerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */