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
docker build -f .docker/Dockerfile -t nantaaditya/db-migration:0.0.1 .
```

Run Image
```shell
docker run --env-file .env/local.env --name db-migration -p 1000:1000 -m512m nantaaditya/db-migration:0.0.1
```

## Build & Deploy Native Image

Build Image
```shell
docker buildx build -f .docker/NativeDockerfile -t nantaaditya/db-migration:0.0.1-native .
```

Run Image
```shell
docker run --env-file .env/local.env --name db-migration-native -p 1000:1000 -m512m nantaaditya/db-migration:0.0.1-native
```