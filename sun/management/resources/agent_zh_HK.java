package sun.management.resources;

import java.util.ListResourceBundle;

public final class agent_zh_HK
  extends ListResourceBundle
{
  public agent_zh_HK() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "agent.err.access.file.not.readable", "存取檔案無法讀取" }, { "agent.err.access.file.notfound", "找不到存取檔案" }, { "agent.err.access.file.notset", "未指定存取檔案，但 com.sun.management.jmxremote.authenticate=true" }, { "agent.err.access.file.read.failed", "無法讀取存取檔案" }, { "agent.err.acl.file.access.notrestricted", "必須限制密碼檔案讀取存取" }, { "agent.err.acl.file.not.readable", "SNMP ACL 檔案無法讀取" }, { "agent.err.acl.file.notfound", "找不到 SNMP ACL 檔案" }, { "agent.err.acl.file.notset", "未指定 SNMP ACL 檔案，但 com.sun.management.snmp.acl=true" }, { "agent.err.acl.file.read.failed", "無法讀取 SNMP ACL 檔案" }, { "agent.err.agentclass.access.denied", "存取 premain(String) 遭到拒絕" }, { "agent.err.agentclass.failed", "管理代理程式類別失敗 " }, { "agent.err.agentclass.notfound", "找不到管理代理程式類別" }, { "agent.err.configfile.access.denied", "存取組態檔案遭到拒絕" }, { "agent.err.configfile.closed.failed", "無法關閉組態檔案" }, { "agent.err.configfile.failed", "無法讀取組態檔案" }, { "agent.err.configfile.notfound", "找不到組態檔案" }, { "agent.err.connector.server.io.error", "JMX 連接器伺服器通訊錯誤" }, { "agent.err.error", "錯誤" }, { "agent.err.exception", "代理程式發生異常 " }, { "agent.err.exportaddress.failed", "將 JMX 連接器位址匯出至設備緩衝區失敗" }, { "agent.err.file.access.not.restricted", "必須限制檔案讀取存取權" }, { "agent.err.file.not.found", "找不到檔案" }, { "agent.err.file.not.readable", "檔案無法讀取" }, { "agent.err.file.not.set", "未指定檔案" }, { "agent.err.file.read.failed", "無法讀取檔案" }, { "agent.err.invalid.agentclass", "com.sun.management.agent.class 屬性值無效" }, { "agent.err.invalid.jmxremote.port", "com.sun.management.jmxremote.port 號碼無效" }, { "agent.err.invalid.jmxremote.rmi.port", "com.sun.management.jmxremote.rmi.port 號碼無效" }, { "agent.err.invalid.option", "指定的選項無效" }, { "agent.err.invalid.snmp.port", "com.sun.management.snmp.port 號碼無效" }, { "agent.err.invalid.snmp.trap.port", "com.sun.management.snmp.trap 編號無效" }, { "agent.err.invalid.state", "無效的代理程式狀態" }, { "agent.err.password.file.access.notrestricted", "必須限制密碼檔案讀取存取" }, { "agent.err.password.file.not.readable", "密碼檔案無法讀取" }, { "agent.err.password.file.notfound", "找不到密碼檔案" }, { "agent.err.password.file.notset", "未指定密碼檔案，但 com.sun.management.jmxremote.authenticate=true" }, { "agent.err.password.file.read.failed", "無法讀取密碼檔案" }, { "agent.err.premain.notfound", "代理程式類別中不存在 premain(String)" }, { "agent.err.snmp.adaptor.start.failed", "無法使用位址啟動 SNMP 配接卡" }, { "agent.err.snmp.mib.init.failed", "無法初始化 SNMP MIB，出現錯誤" }, { "agent.err.unknown.snmp.interface", "不明的 SNMP 介面" }, { "agent.err.warning", "警告" }, { "jmxremote.AdaptorBootstrap.getTargetList.adding", "正在新增目標: {0}" }, { "jmxremote.AdaptorBootstrap.getTargetList.initialize1", "配接卡就緒。" }, { "jmxremote.AdaptorBootstrap.getTargetList.initialize2", "SNMP 配接卡就緒，位於: {0}:{1}" }, { "jmxremote.AdaptorBootstrap.getTargetList.processing", "正在處理 ACL" }, { "jmxremote.AdaptorBootstrap.getTargetList.starting", "正在啟動配接卡伺服器:" }, { "jmxremote.AdaptorBootstrap.getTargetList.terminate", "終止 {0}" }, { "jmxremote.ConnectorBootstrap.file.readonly", "必須限制檔案讀取存取權: {0}" }, { "jmxremote.ConnectorBootstrap.noAuthentication", "無認證" }, { "jmxremote.ConnectorBootstrap.password.readonly", "必須限制密碼檔案讀取存取: {0}" }, { "jmxremote.ConnectorBootstrap.ready", "JMX 連接器就緒，位於: {0}" }, { "jmxremote.ConnectorBootstrap.starting", "正在啟動 JMX 連接器伺服器:" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\resources\agent_zh_HK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */