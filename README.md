# lightning
 &#160;&#160; &#160;&#160;LightNing是一款跨平台，零流量，传输速度远超蓝牙的极速文件传输工具。  
 &#160;&#160; &#160;&#160;当你在打印店打印文件，没带u盘、数据线？手机流量不够？拒绝尴尬！打开LightNing，无线连接打印机。  
 &#160;&#160; &#160;&#160;会议中传输会议资料，用微信的面对面加群？添加好友效率太低。用百度云的文件分享？一个个下载太麻烦。打开LightNing声波通讯，只需用 lightning放一段音乐，参会的每个人就可以拿到会议所需的材料。  
 &#160;&#160; &#160;&#160;电脑手机互传，使用 QQ的步骤是这样的：
A 机上复制文字→ 启动 QQ → 找到自己的帐号 → 粘贴 → 发送 → 换到 B 机启动 QQ → 接收消息 → 选中文字并复制 → 粘贴使用。   而Lightning的手机电脑隔空传功能，可以在android端复制任意内容，pc端便可直接粘该内容，反之亦然，「一键式」分享操作。操作步骤是：
A 机复制文字 → 换到 B 机→ 粘贴使用。  你可以看到，使用Lightning你所需要做的操作大大的减少了。  
  &#160;&#160; &#160;&#160;还在为电脑出现网络故障而烦恼吗？还在为高昂的流量费而痛心吗？LightNing的wifi管理，只需单击、一键修复，即可解决网络故障。  
 &#160;&#160; &#160;&#160;还在为与陌生人互传文件而头疼，还在为隐私泄露而担忧，LightNing微信公众号隐私分享是你很好的选择，无需添加好友，即可快速分享，支持下载即焚、超大附件。  

 &#160;&#160; &#160;&#160;LightNing的产品特色：  
1. 快速——局域网传输最高可达20MB/s  
2. 无线——无线连接打印机，方便快捷  
3. 安全——DES加密  
4. 多类——支持音乐、视频、图片、应用等任意文件类型。  
5. 声波——只需播放一段音乐、文件随广播传送  
6. 跨屏——Android、iOS、Windows，Linux、mac跨平台随心传。  
7. 简易——一键创建wifi，一键连接，智能匹配密码。   
8. 方便——支持离线下载，用户随时接收，人性化   
9. 智能——智能检测网络状态，智能选择压缩算法  
10. 分享——二维码分享文件，无需连接或下载应用，扫码即可  
11. 应急——网络故障、一键修复  

本产品分为四端（服务器端，PC端，Android端和IOS端）。  
接下来是Android端的功能：
1.	首页是手机的内存信息和文件统计。安装包、电子书、压缩包和大文件的个数，一目了然。还可快速搜索文件。
2.	智能检索周边使用LightNing开启热点的用户，点击用户头像，即可连接。连接后，如果电脑端已经授权，则可以浏览电脑端所有文件目录。
3.	若设备不支持WiFi功能，可以使用热点模式或是声波模式。或是打开扫码功能，生成自己独特的二维码，供小伙伴扫描。
4.	LightNing会智能判断当前的网络状态，如果无网络，使用局域网传输，如果有网，自动使用P2P加速模式。可自动从周边活跃的服务端传输文件，有效提高下载速度。
5.	我们可以清空全部的记录，也可以选择删除单条记录，支持重新发送、查看文件属性、打开文件等快捷操作。
6.	发送文件，文件支持图片形式或列表形式展示。选好后，可点击已选按钮，对待发送文件进行更改，确认无误后，点击发送即可。    
  
    
![](https://github.com/feelschaotic/lightning/blob/master/video/lightning_android.gif)    

![](https://github.com/feelschaotic/lightning/blob/master/video/wireless_printer.gif)    

![](https://github.com/feelschaotic/lightning/blob/master/video/sound_wave_transmission.gif)  

  
![](https://github.com/feelschaotic/lightning/blob/master/video/sound_wave_transmission1.gif)  
  
	    
		
性能测试：


测试用例 | 输入参数和数据 | 文件大小 | 运行场景设置 | 期望结果 | 实际情况 | 测试状态(P/F) |	测试点
---|---|---|---|---|---|---|---
发送文件 | Mp3文件路径 | 1.03MB | 单文件发送，无网状态 |   | 时间：90ms=0.09s | P |  无网状态下传输速度
发送文件 |	Doc文档路径	 |2.80MB |	单文件发送，无网状态 |	 |	时间：133ms=0.133s |	P |	无网状态下传输速度
发送文件 |	文件夹路径 |	3.83MB	 |多文件发送，无网状态	 |1s |	时间：186ms=0.186s |	P |	传输速度
发送文件|	电影文件路径|	1.05GB	|大文件发送，无网状态|	1min|	时间：63371ms=63.371s=1.05min|	P|	传输速度
发送文件|	Mp3文件路径|	1.03MB |	有网状态，普通模式，通过服务器中转【加密】|	2s|	时间：1684ms=1.68s|	P|	无使用P2P的传输速度
发送文件|	Doc文档路径|	2.80MB|有网状态，普通模式，通过服务器中转【加密】|	2s|	时间：1930ms=1.93s|	P|	无使用P2P的传输速度
发送文件|	文件夹路径|	3.83MB	|有网状态，普通模式，通过服务器中转【加密】|	3s|	时间：3148ms=3.14s|	F	|无使用P2P的传输速度
发送文件|	Peer节点地址|	3.83MB	|有网状态，P2P下载|	1s|	时间：137ms=0.137s|	P	|使用P2P的传输速度
压缩文件|	压缩的文件夹路径|	压缩前文件夹大小：12GB|	仅压缩，文件夹中文件类型超15种：包含：txt、pdf、xls、doc、c、cpp、mp3、exe、log、ppt、rar、dmp等，共有884个文件，91个子文件夹|	80%压缩比|	压缩后文件夹大小：4110MB=4.0136GB，压缩比：33.45%|	P	|压缩率
压缩文件|	图片路径|	压缩图片大小：1.08MB|	仅压缩	|80%压缩比|	压缩后：1.05MB  ，耗时：1.07s  ，压缩比：97%|	F|	压缩率
压缩文件|	文本路径|	压缩前大小：1.27MB	|	仅压缩|	80%压缩比|	压缩后：529KB  ，耗时：1.3s  ，压缩比：40.6%|	P|	压缩率








