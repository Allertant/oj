### nacos 相关
1. 启动： 
- 进入到 Nacos 的 bin 目录下，输入命令
```bash
sh startup.sh -m standalone # linux
bash startup.sh -m standalone # 特 unbuntu
startup.cmd -m standalone # windows
``` 
2. nacos web 的管理和服务端
```text
http://localhost:8848/nacos/
```

### redis 相关
启动命令
```bash
redis-server
```

### rabbitMq 相关
1. 配置
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    password: guest
    username: guest
```
2. web 管理端
自启动，不用关心
```text
http://localhost:15672/#/
```
