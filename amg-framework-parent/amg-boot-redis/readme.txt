spring:
  redis:
    timeout: 6000
    database: 0
    #    host: localhost #单实例redis用这个配置
    #    password: #单实例redis用这个配置
    #    port: 6379 #单实例redis用这个配置
    mode: cluster # singl单列 cluster集群  sentinel哨兵(单列，集群  待实现   目前只实现了集群)
    password:
    cluster: #集群用这个配置
      nodes:
        - 192.168.1.188:7000
        - 192.168.1.188:7001
        - 192.168.1.188:7002
        - 192.168.1.188:8001
        - 192.168.1.188:8002
        - 192.168.1.188:8003
      max-redirects: 3 #获取槽不在当前节点时最大重定向次数
    lettuce:
      pool:
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
