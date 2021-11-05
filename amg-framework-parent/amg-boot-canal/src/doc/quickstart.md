#### 快速开始
pom
~~~ 

        <dependency>
			<groupId>com.amg</groupId>
			<artifactId>amg-boot-canal</artifactId>
			<version>${revision}</version>
		</dependency>

   <dependencyManagement>
		<dependencies>
			<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-dependencies -->
			<dependency>
				<groupId>com.amg</groupId>
				<artifactId>amg-framework-parent</artifactId>
				<version>${revision}</version>
				<!-- 所有依赖包的版本控制统一 -->
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
~~~
##### 步骤
1. 加入 配置

~~~
canal.client.host=192.168.254.102
canal.client.port=11111
canal.client.batchSize=1000
canal.client.destination=example
canal.client.userName=canal
canal.client.password=canal
#订阅对应的库
#canal.client.filter=goods\\..*
~~~

2. 启动类 开启注解
~~~
@EnableCanalClient
~~~

3.使用 demo

  1. @CanalListener 监听表变化
  2. @CanalEventPoint 事件点 方法上
~~~
@CanalListener(table = {"goods"})
public class CanalListener111 {

    @CanalEventPoint(eventType=EventTypeEnum.DELETE)
    public void delete(Canal canal){
        System.out.println(canal);
    }


    @CanalEventPoint(eventType=EventTypeEnum.UPDATE_AFTER)
    public void updateAfter(Canal canal){
        System.out.println(canal);
    }


    @CanalEventPoint(eventType=EventTypeEnum.UPDATE_BEFORE)
    public void updateBefore(Canal canal){
        System.out.println(canal);
    }

    @CanalEventPoint(eventType=EventTypeEnum.INSERT)
    public void insert(Canal canal){
        System.out.println(canal);
    }
}
~~~