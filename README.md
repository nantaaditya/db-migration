# DB Migration

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
docker run --env-file .env/local.env --name db-migration-native -p 1000:1000 nantaaditya/db-migration:0.0.1-native
```