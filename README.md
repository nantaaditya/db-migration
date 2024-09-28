# DB Migration
A Java - PostgreSQL Schema Migrator

## Environment Variables

| Env Name           | Type   | Default Value                             | Description                                                                     |
|--------------------|--------|-------------------------------------------|---------------------------------------------------------------------------------|
| SERVER_PORT        | int    | 1000                                      | port of application                                                             |
| DB_URL             | String | jdbc:postgresql://localhost:5432/local_db | spring datasource url of application                                            |
| DB_USER            | String | user                                      | username of datasource                                                          |
| DB_PASS            | String | password                                  | password of datasource                                                          |
| LOG_NAME           | String | db-migration.log                          | log name of application                                                         |
| MAX_LOG_HISTORY    | int    | 14                                        | log retention to be kept on disk                                                |
| PRIVATE_KEY        | String | -                                         | RSA Private Key to decrypt encrypted value                                      |
| PUBLIC_KEY         | String | -                                         | RSA Public Key to encrypt username, password, passphrase of database credential |
| CREDENTIAL_PREFIX  | String | thisisprefix                              | Prefix to be add on password before doing encryption                            |
| CREDENTIAL_POSTFIX | String | thisispostfix                             | Postfix to be add on password before doing encryption                           |
| UPLOAD_FILE_PATH   | String | files/                                    | Upload file path for sql ddl file                                               |
| MAX_FILE_SIZE      | String | 5MB                                       | max file size                                                                   |
| MAX_UPLOAD_SIZE    | String | 5MB                                       | max upload size                                                                 |


## OpenAPI Swagger
```shell
http://localhost:1000/swagger-ui/index.html
```

## Build & Deploy Image
Build Jar
```shell
mvn install -DskipTests
```

Build Image
```shell
docker build \
  --build-arg APP_VERSION=1.0.0 
  --build-arg APP_CREATED_DATE=202409281619 \
  --build-arg APP_PORT=1000 \
  -f .docker/Dockerfile \
  -t nantaaditya/db-migration:1.0.0 .
```

Build Multi Stage Image
```shell
docker build \
  --build-arg APP_VERSION=1.0.0 \
  --build-arg APP_CREATED_DATE=202409281636 \
  --build-arg APP_PORT=1000 \
  -f .docker/Dockerfile_MultiStage \
  -t nantaaditya/db-migration:1.0.0 .
```

Build Native Image
```shell
docker build \
  --build-arg APP_VERSION=1.0.0 \
  --build-arg APP_CREATED_DATE=202409281712 \
  --build-arg APP_PORT=1000 \
  -f .docker/Dockerfile_Native \
  -t db-migration:1.0.0 .

```

Run Image
```shell
docker run --env-file .env/local.env --name db-migration -p 1000:1000 -m512m nantaaditya/db-migration:0.0.1
```