# Use zigbee Get Temperature

使用zigbee创建终端节点，获取温度，并通过路由传到协调器或者直接传到协调器，协调器将数据进行处理并打印到PC或Android上。

 <br>

 本文分为几个部分，分别如下

|文件|说明|
|--|--|
|Android|使用安卓控制|
|C#|串口|
|Zigbee_coordinator|协调器|
|Zigbee_endpointsr|终端节点|

<br>

Zigbee的功能如下：
- 当组件网络成功后或节点连接入网络，蓝灯持续点亮
- 当收到 network_topology 时协调器发送拓扑结构到串口上，格式如下：
		Coordinator：网络地址
		Router:网络地址，parent:网络地址
		Endpoint:网络地址，parent:网络地址

- 当收到 get_temperature 时协调器将终端获取到的温度和湿度打印到串口上，格式如下：
		Temperature:30, humidity:60
- 当收到 on 时终端节点将自己的灯点亮。（黄灯）
- 当收到 off时终端节点将自己的灯熄灭。 （黄灯）
- 当收到 toggle时终端节点周期性5000ms闪烁灯 （黄灯）
