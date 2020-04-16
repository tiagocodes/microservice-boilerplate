
Assumption:
- It is assumed that if account does not exist it should not be created otherwise cascade ALL could be implemented on field account of Transaction.java
- The service can scale. It can run on multiple simultaneous instances.   

Tested on Linux 5.0.0-32-generic #34-Ubuntu SMP Wed Oct 2 02:06:48 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux

Apache Maven 3.6.0
Maven home: /usr/share/maven
Java version: 1.8.0_222, vendor: Private Build, runtime: /usr/lib/jvm/java-8-openjdk-amd64/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.0.0-32-generic", arch: "amd64", family: "unix"

openjdk version "1.8.0_222"
OpenJDK Runtime Environment (build 1.8.0_222-8u222-b10-1ubuntu1~19.04.1-b10)
OpenJDK 64-Bit Server VM (build 25.222-b10, mixed mode)


    How to use:
    ----------

    mvn clean test
    mvn clean install

    CI/CD
    mvn clean verify -PIT1
    mvn clean verify -PIT2

    launch application
    java -jar target/prueba-1.0.0-SNAPSHOT.jar

![code-coverage](code-coverage.png)

    How to use Docker and run mega test:
    ------------------------------------
    1. First create an external database and change file Docker/application-prueba.yml with connection settings.
    2. Run the follwing SQL:

    CREATE TABLE `account` (
      `iban` varchar(255) NOT NULL,
      `balance` decimal(19,2) DEFAULT NULL,
      PRIMARY KEY (`iban`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

    CREATE TABLE `transaction` (
      `id` bigint(20) NOT NULL,
      `amount` decimal(7,2) NOT NULL,
      `date` datetime(6) DEFAULT NULL,
      `description` varchar(255) DEFAULT NULL,
      `fee` decimal(19,2) DEFAULT NULL,
      `reference` varchar(255) DEFAULT NULL,
      `iban` varchar(255) DEFAULT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `UK_ea0sj0oja3gyf9tj6v9n3ftjk` (`reference`),
      KEY `idx_reference` (`reference`),
      KEY `idx_iban` (`iban`),
      CONSTRAINT `FK7kr1ulmmiwwq1qr3xvb1vr9ls` FOREIGN KEY (`iban`) REFERENCES `account` (`iban`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

    insert into account (iban, balance) values ('ES7921000813610123456789', '0');

    3. cd Docker
    4. ./build.sh
    5. ./run-app.sh
    6. ./ps.sh
    7. ./log.sh and wait for #of instances defined on run-variables.sh (3) to boot.
    8. open another session and cd to Docker/Megatest/
    9. run mvn test
    10. select account and check balance is sum of all requests of test.
    11. ./destroy.sh to shutdown instances.
