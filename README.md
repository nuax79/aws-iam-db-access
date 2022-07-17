# aws-iam-db-access

Apache Common DBCP 연결 풀을 사용한 AWS IAM 데이터베이스 인증

## Installation

```
git clone https://github.com/nuax79/aws-iam-db-access-dbcp.git
```

## Overview

AWS RDS, Redshift IAM Access 인증 방식을 Apache Common DBCP에 적용하여 연결 풀 생성 시 토큰을 생성하고 데이터베이스에 연결합니다.

이 소스 코드는 Apache Common DBCP 버전 v1, v2와 호환됩니다.

## Usage

예제는 연결을 생성하기 위한 Junit 테스트 케이스로 구현됩니다.

jdbc 연결 속성을 수정해야 합니다.

`test/resources/application-test.profiles`

```properties
# Region
datasource.rds.region=ap-northeast-2
# AWS RDS Service Endpoint
datasource.rds.endpoint=
# AWS RDS Service Port
datasource.rds.port=
# AWS RDS Username
datasource.rds.username=
datasource.rds.driverClassName=com.mysql.cj.jdbc.Driver
# AWS RDS JDBC URL
datasource.rds.url=

# Region
datasource.redshift.region=ap-northeast-2
# AWS Redshift Cluster ID
datasource.redshift.clusterId=
# AWS Redshift Username
datasource.redshift.username=
datasource.redshift.driverClassName=com.amazon.redshift.jdbc.Driver
# AWS Redshift JDBC URL
datasource.redshift.url=
```


환경에 대한 AWS CLI 인증 정보를 설정합니다.

Edit `$USER_HOME/.aws/config`.
```
[default]
output = json
region = ap-northeast-2

[my-profile]
output = json
region = ap-northeast-2
```

Edit `$USER_HOME/.aws/credentials`.
```
[default]
aws_access_key_id = ...
aws_secret_access_key = ...

[my-profile]
aws_access_key_id = ...
aws_secret_access_key = ...
```

프로젝트 작업 경로로 이동합니다.
Maven Junit 테스트 케이스를 실행합니다.

```cmd
$ mvn clean test -Daws.profile=If omitted, the default profile is used.
```
