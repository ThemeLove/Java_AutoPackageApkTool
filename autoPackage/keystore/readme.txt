用户输入在pptv平台申请的游戏唯一标示gid

生成签名文件的默认参数如下：

签名文件名称：keystore:{gid}.keystore
签名文件密码：storepass:{gid}pptvas
别名：alias:{gid}.keystore
别名密码：keypass:{gid}pptvvas



pptv游戏签名生成规则，用户可根据需要自行修改
cmd命令如下：
String generateKeystoreCommand=String.format(getLocale(),"keytool -genkey -keyalg RSA -validity 36500 -alias %s -keystore %s -storepass %s -keypass %s -dname \"CN=pptvvas,OU=pptvvas,O=pptvvas,L=shanghai,ST=shanghai,C=CN\"",
						new Object[]{inputGid+".keystore",gameKeystorePath,inputGid+"pptvvas",inputGid+"pptvvas"});
