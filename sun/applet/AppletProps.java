package sun.applet;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

class AppletProps
  extends Frame
{
  TextField proxyHost;
  TextField proxyPort;
  Choice accessMode;
  private static AppletMessageHandler amh = new AppletMessageHandler("appletprops");
  
  AppletProps()
  {
    setTitle(amh.getMessage("title"));
    Panel localPanel = new Panel();
    localPanel.setLayout(new GridLayout(0, 2));
    localPanel.add(new Label(amh.getMessage("label.http.server", "Http proxy server:")));
    localPanel.add(proxyHost = new TextField());
    localPanel.add(new Label(amh.getMessage("label.http.proxy")));
    localPanel.add(proxyPort = new TextField());
    localPanel.add(new Label(amh.getMessage("label.class")));
    localPanel.add(accessMode = new Choice());
    accessMode.addItem(amh.getMessage("choice.class.item.restricted"));
    accessMode.addItem(amh.getMessage("choice.class.item.unrestricted"));
    add("Center", localPanel);
    localPanel = new Panel();
    localPanel.add(new Button(amh.getMessage("button.apply")));
    localPanel.add(new Button(amh.getMessage("button.reset")));
    localPanel.add(new Button(amh.getMessage("button.cancel")));
    add("South", localPanel);
    move(200, 150);
    pack();
    reset();
  }
  
  void reset()
  {
    AppletSecurity localAppletSecurity = (AppletSecurity)System.getSecurityManager();
    if (localAppletSecurity != null) {
      localAppletSecurity.reset();
    }
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyHost"));
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyPort"));
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new GetBooleanAction("package.restrict.access.sun"));
    boolean bool = localBoolean.booleanValue();
    if (bool) {
      accessMode.select(amh.getMessage("choice.class.item.restricted"));
    } else {
      accessMode.select(amh.getMessage("choice.class.item.unrestricted"));
    }
    if (str1 != null)
    {
      proxyHost.setText(str1);
      proxyPort.setText(str2);
    }
    else
    {
      proxyHost.setText("");
      proxyPort.setText("");
    }
  }
  
  void apply()
  {
    String str1 = proxyHost.getText().trim();
    String str2 = proxyPort.getText().trim();
    final Properties localProperties = (Properties)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return System.getProperties();
      }
    });
    if (str1.length() != 0)
    {
      int i = 0;
      try
      {
        i = Integer.parseInt(str2);
      }
      catch (NumberFormatException localNumberFormatException) {}
      if (i <= 0)
      {
        proxyPort.selectAll();
        proxyPort.requestFocus();
        new AppletPropsErrorDialog(this, amh.getMessage("title.invalidproxy"), amh.getMessage("label.invalidproxy"), amh.getMessage("button.ok")).show();
        return;
      }
      localProperties.put("http.proxyHost", str1);
      localProperties.put("http.proxyPort", str2);
    }
    else
    {
      localProperties.put("http.proxyHost", "");
    }
    if (amh.getMessage("choice.class.item.restricted").equals(accessMode.getSelectedItem())) {
      localProperties.put("package.restrict.access.sun", "true");
    } else {
      localProperties.put("package.restrict.access.sun", "false");
    }
    try
    {
      reset();
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws IOException
        {
          File localFile = Main.theUserPropertiesFile;
          FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
          Properties localProperties = new Properties();
          for (int i = 0; i < Main.avDefaultUserProps.length; i++)
          {
            String str = Main.avDefaultUserProps[i][0];
            localProperties.setProperty(str, localProperties.getProperty(str));
          }
          localProperties.store(localFileOutputStream, AppletProps.amh.getMessage("prop.store"));
          localFileOutputStream.close();
          return null;
        }
      });
      hide();
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      System.out.println(amh.getMessage("apply.exception", localPrivilegedActionException.getException()));
      localPrivilegedActionException.printStackTrace();
      reset();
    }
  }
  
  public boolean action(Event paramEvent, Object paramObject)
  {
    if (amh.getMessage("button.apply").equals(paramObject))
    {
      apply();
      return true;
    }
    if (amh.getMessage("button.reset").equals(paramObject))
    {
      reset();
      return true;
    }
    if (amh.getMessage("button.cancel").equals(paramObject))
    {
      reset();
      hide();
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */