# DB Migration

## API

1. Run Migration

| Path        | /api/migrate                   |
|-------------|:-------------------------------|
| Http Method | POST                           |
| Headers     | Content-Type: application/json |

Request Body
```json
[
  {
    "id": 1,
    "migration": "alter table account add column test varchar(10);",
    "rollback": "alter table account drop column test;"
  }, {
    "id": 2,
    "migration": "alter table xxx add column test2 boolean;",
    "rollback": "alter table xxx drop column test2;"
  }, {
    "id": 3,
    "migration": "alter table account add column success boolean;",
    "rollback": "alter table account drop column success;"
  }
]
```

Response Body
```json
[
    {
        "id": 1,
        "migration": "alter table account add column test varchar(10);",
        "rollback": "alter table account drop column test;",
        "status": 1
    },
    {
        "id": 2,
        "migration": "alter table xxx add column test2 boolean;",
        "rollback": "alter table xxx drop column test2;",
        "status": 2
    },
    {
        "id": 3,
        "migration": "alter table account add column success boolean;",
        "rollback": "alter table account drop column success;",
        "status": 1
    }
]
```

```text
Status Legend:
- 0 = migration script created, but not executed.
- 1 = migration script successfully executed.
- 2 = migration script failed executed.
- 3 = rollback script successfully executed.
- 4 = rollback script failed executed.
```

```shell
curl --location 'http://localhost:1000/api/migrate' \
--header 'Content-Type: application/json' \
--data '[
    {
        "id": 1,
        "migration": "alter table account add column test varchar(10);",
        "rollback": "alter table account drop column test;" 
    }, {
        "id": 2,
        "migration": "alter table xxx add column test2 boolean;",
        "rollback": "alter table xxx drop column test2;"
    }, {
        "id": 3,
        "migration": "alter table account add column success boolean;",
        "rollback": "alter table account drop column success;"
    }
]'
```

2. Rollback API

| Path          | /api/rollback/{id}             |
|---------------|:-------------------------------|
| Http Method   | DELETE                         |
| Path Variable | id                             |


Response Body
```json
[
  {
    "id": 1,
    "migration": "alter table account add column test varchar(10);",
    "rollback": "alter table account drop column test;",
    "status": 3
  },
  {
    "id": 3,
    "migration": "alter table account add column success boolean;",
    "rollback": "alter table account drop column success;",
    "status": 3
  }
]
```

```shell
curl --location --request DELETE 'http://localhost:1000/api/rollback/0'
```

3. Get Schema

| Path          | /api/schema                    |
|---------------|:-------------------------------|
| Http Method   | GET                            |


Response Body
```json
{
  "migrations": [
    "alter table account add column test varchar(10);",
    "alter table xxx add column test2 boolean;",
    "alter table account add column success boolean;"
  ],
  "rollbacks": [
    "alter table xxx drop column test2;",
    "alter table account drop column success;",
    "alter table account drop column test;"
  ]
}
```

```shell
curl --location 'http://localhost:1000/api/schema'
```

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
docker run --env-file .env/local.env --name db-migration -p 1000:1000 nantaaditya/db-migration:0.0.1
```

## Build & Deploy Native Image
Build Jar
```shell
mvn install -DskipTests
```

Build Image
```shell
docker build -f .docker/NativeDockerfile -t nantaaditya/db-migration:0.0.1-native .
```

Run Image
```shell
docker run --env-file .env/local.env --name db-migration-native -p 1000:1000 nantaaditya/db-migration:0.0.1-native
```