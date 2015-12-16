# WeMeep Session Service
### Description
This service handles all the user session info, like all their logged devices, session tokens and so on. It's built over Docker containers and uses Java Spark, Gradle and PostgreSql.

### Setup
#### Docker
Simply:
```
docker-compose up -d
```
#### Environment variables
Set:
```
- PG_USER
- PG_PASS
```

#### Exposed ports
```
- PhpPgAdmin: 49161/phppgadmin
- API: 4567
```

### WebService
The web service exposes the following methods:

- Validate a token with `POST`:

```
http://host:4567/validatetoken/

Data: { body: { token:<someUsername>, userId:<someEmail>, ... } }
Returns { "valid": true|false }
```
- Generate a token with `POST`:

```
http://host:4567/generatetoken/

Data: { body: { token:<someUsername>, userId:<someEmail>, ... } }
Returns { "token": <someToken> }
```
### Data model
#### Objects
##### Session
|  Field      |  Values   |
| :---------- | :-------- |
| userId      | String    |
| username      | String    |
| id          | Number    |
| deviceId    | String    |
| token       | String    |
| tokenExpiration | Timestamp|

## TODO
- Add authentication to the database
- Protect the API
- Check for SQLInjection
