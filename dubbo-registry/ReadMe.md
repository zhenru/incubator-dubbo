注册中心主要是用来抽象注册中心和服务发现的接口。
其中关键是 `registry-api`定义了接口，其中具体的实现有具体的几种实现。重点看下registry-zookeeper实现。