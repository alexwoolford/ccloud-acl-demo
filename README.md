# ccloud-acl-demo

Login to Confluent Cloud:

    ➜  ~ ccloud login
    Enter your Confluent credentials:
    Email: alex.woolford+ccloud@confluent.io
    Password: ************
    Logged in as "alex.woolford+ccloud@confluent.io".
    Using environment "env-ygmko" ("woolford-env").

Use the cluster:

    ➜  ~ ccloud kafka cluster list
          Id      |     Name     |   Type   | Provider |  Region   | Availability | Status  
    +-------------+--------------+----------+----------+-----------+--------------+--------+
        lkc-pwn3y | woolford-aws | STANDARD | aws      | us-east-1 | HIGH         | UP      
    ➜  ~ ccloud kafka cluster use lkc-pwn3y
    Set Kafka cluster "lkc-pwn3y" as the active cluster for environment "env-ygmko".

Create the `walmart` and `target` topics:

    ccloud kafka topic create walmart
    ccloud kafka topic create target

Create Walmart and Target service accounts:

    ➜  ~ ccloud service-account create walmart --description "Walmart service account."
    +-------------+--------------------------+
    | Id          |                   103141 |
    | Name        | walmart                  |
    | Description | Walmart service account. |
    +-------------+--------------------------+

ccloud service-account create target --description "Target service account."

    ➜  ~ ccloud service-account create target --description "Target service account."
    +-------------+-------------------------+
    | Id          |                  103143 |
    | Name        | target                  |
    | Description | Target service account. |
    +-------------+-------------------------+

Note the service account ID's.

Allow the service accounts to read their respective topics

    ➜  ~ ccloud kafka acl create --allow --service-account "103141" --operation "READ" --topic "walmart"
      ServiceAccountId | Permission | Operation | Resource |  Name   |  Type    
    +------------------+------------+-----------+----------+---------+---------+
      User:103141      | ALLOW      | READ      | TOPIC    | walmart | LITERAL 
  
    ➜  ~ ccloud kafka acl create --allow --service-account "103143" --operation "READ" --topic "target"
      ServiceAccountId | Permission | Operation | Resource |  Name  |  Type    
    +------------------+------------+-----------+----------+--------+---------+
      User:103143      | ALLOW      | READ      | TOPIC    | target | LITERAL  

Create service account API keys:

    ➜  ~ ccloud api-key create --resource lkc-pwn3y --service-account 103141 --description "Walmart service account API key"
    It may take a couple of minutes for the API key to be ready.
    Save the API key and secret. The secret is not retrievable later.
    +---------+------------------------------------------------------------------+
    | API Key | HZL2D2L2BX2CXBHO                                                 |
    | Secret  | 5DWQaDBfC8xzj0qYSBq20xtdmdKBpBOZkU766YE1VBjw4qrUCI6TtdWxdS9qyGGA |
    +---------+------------------------------------------------------------------+

    ➜  ~ ccloud api-key create --resource lkc-pwn3y --service-account 103143 --description "Target service account API key"
    It may take a couple of minutes for the API key to be ready.
    Save the API key and secret. The secret is not retrievable later.
    +---------+------------------------------------------------------------------+
    | API Key | J33XBJ5XB3P3IMPG                                                 |
    | Secret  | fUIVGEgZwL/ssm6tw+GWcuk7RlzjGOua615RM+6y6Z4LWXWMeLmq6igBsglYTl3l |
    +---------+------------------------------------------------------------------+

Allow service account to read the consumer group:

    ➜  ~ ccloud kafka acl create --allow --service-account 103141 --operation READ --consumer-group foo
      ServiceAccountId | Permission | Operation | Resource | Name |  Type    
    +------------------+------------+-----------+----------+------+---------+
      User:103141      | ALLOW      | READ      | GROUP    | foo  | LITERAL 

    ➜  ~ ccloud kafka acl create --allow --service-account 103143 --operation READ --consumer-group foo
      ServiceAccountId | Permission | Operation | Resource | Name |  Type    
    +------------------+------------+-----------+----------+------+---------+
      User:103143      | ALLOW      | READ      | GROUP    | foo  | LITERAL  

The StreamSets pipeline writes sample data to the `walmart` and `target` topics.

The `ConsumerTarget` and `ConsumerWalmart` classes consume records from their respective ACL-protected topics. Once we've confirmed that, for example, the `ConsumerWalmart` class can consume messages from the `walmart` topic, we can change the topic name so the `ConsumeWalmart` class attempts to consume messages from the `target` topic. You'll see an error:

    Exception in thread "main" org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [target]
