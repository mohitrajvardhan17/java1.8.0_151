package sun.misc.resources;

import java.util.ListResourceBundle;

public final class Messages_ja
  extends ListResourceBundle
{
  public Messages_ja() {}
  
  protected Object[][] getContents()
  {
    return new Object[][] { { "optpkg.versionerror", "エラー: JARファイル{0}で無効なバージョン形式が使用されています。サポートされるバージョン形式についてのドキュメントを参照してください。" }, { "optpkg.attributeerror", "エラー: 必要なJARマニフェスト属性{0}がJARファイル{1}に設定されていません。" }, { "optpkg.attributeserror", "エラー: 複数の必要なJARマニフェスト属性がJARファイル{0}に設定されていません。" } };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\resources\Messages_ja.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */