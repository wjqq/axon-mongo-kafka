###### Axon Framework without Axon-server
> 这个POC参考axon-mongo-demo的git repo，在原来的基础上进行了改进，使用了kafka作为event bus,不再使用axon-server,框架流程图我就不画了.
1. 如果只使用mongo-extension，似乎也可以，但是内部代码应该是long poll方式吧.有待调查
2. Mongo不是很推荐作为Event Store,官方说的很含糊(1 - tracking Processor性能 2 - Mongo没有sequeuece)，后台有必要还是需要使用Axon Server.

> 知識點包括
- Spring Boot
- Axon
- Kafka
- Mongo

> 代碼結構
```
src
  -api
    -command: Command = POJO对象 + @TargetAggregateIdentifier
    -event: Axon Event = POJO对象
    -query: Axon Query = POJO对象
  -config
    MongoConfig: 连接MongoDB，Mongo同时做Event Store也做了View Store,数据库的结构如下
                  > use db-axon0
                    switched to db db-axon0
                  > show collections
                    GiftCard  = View Collection
                    domainevents = event store(Sourcing)
                    snapshotevents = 业务对象的快照
                    trackingtokens = event process(publish && receive)的token
    PublicationConfiguration: Kafka publish 配置
    TrackingConfiguration: Kafka Tracking配置
                          40~52: 初始化GiftCardHandler processor的其实token，从mongodb的trackingtokens collection获取，作为kafka的andInitialTrackingToken;
                          @Autowired
                          public void configureProcessor(Configurer configurer,
                              StreamableKafkaMessageSource<String, byte[]> streamableKafkaMessageSource,
                              TokenStore tokenStore) {

                            TrackingToken giftCardHandlerTrackingToken = tokenStore.fetchToken("GiftCardHandler", 0);
                            configurer.eventProcessing().usingSubscribingEventProcessors()
                                .registerTrackingEventProcessor("GiftCardHandler",
                                    c->streamableKafkaMessageSource,
                                    c -> TrackingEventProcessorConfiguration.forParallelProcessing(1)
                                        .andInitialTrackingToken(sms -> giftCardHandlerTrackingToken)
                                        .andTokenClaimInterval(10000, TimeUnit.SECONDS));
                          }
                          Token就是记录上次处理到的位置，如果使用kafka作为event middleware，token就是这样的,总之这个看你使用什么作为event bus了
                          <org.axonframework.extensions.kafka.eventhandling.consumer.streamable.KafkaTrackingToken>
                             <positions class="java.util.Collections$UnmodifiableMap">
                               <m>
                                 <entry>
                                   <org.apache.kafka.common.TopicPartition>
                                     <hash>525393588</hash>
                                     <partition>0</partition>
                                     <topic>Topic-1</topic>
                                   </org.apache.kafka.common.TopicPartition>
                                   <long>17</long>
                                  </entry>
                                  <entry>
                                   <org.apache.kafka.common.TopicPartition>
                                     <hash>4606</hash>
                                     <partition>0</partition>
                                     <topic>t1</topic>
                                    </org.apache.kafka.common.TopicPartition>
                                    <long>511</long>
                                   </entry>
                                  </m>
                                </positions>
                            </org.axonframework.extensions.kafka.eventhandling.consumer.streamable.KafkaTrackingToken>
  -query
    -entity: VIEW model的entity
    -repo: JPA Repository OR Mongo Repository
    GiftCardProjector用来接受event，保存数据到view model,对于分布式，@ProcessingGroup("GiftCardHandler") 通过Group来考虑并发处理，Axon FrameWork 已经考虑这点了，详细参考官方文档
  -web 传统代码的的web层
    -req: 请求的DTO
    -res: 返回的DTO
    CardController 标准controller,只是查询和更新需要借助bus
          @Autowired
          private CommandGateway commandGateway;
          @Autowired
          private QueryGateway queryGateway;
   App 启动类啦-_-!!
```

> 流程, 启动前保证kafka和mongo已经启动
```
docker run --network=kafka -d --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:4.1.0
docker run --net=kafka -d -p 9092:9092 --name=kafka -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:4.1.0
Mongo我是安装到本地的，就不提供docker启动方式了，自己google吧
```
1. 創建card
```
POST http://localhost:8082/cards
{
   "value": 9
}
```

3. 增加卡的餘額
```
PATCH http://localhost:8082/cards
{
    "id": "75b8a744-5f1a-40e9-8d93-b69fc7935ed9",
    "value": 9
}
```
4. 查看卡片
```
GET http://localhost:8082/cards/75b8a744-5f1a-40e9-8d93-b69fc7935ed9
```
