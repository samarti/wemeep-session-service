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

Data: { body: { token:<someToken>, userId:<someId>, ... } }
Returns { "valid": true|false }
```
- Generate a token with `POST`:

```
http://host:4567/generatetoken/

Data: { body: { token:<someToken>, userId:<someId>, ... } }
Returns { "token": <someToken> }

```
- Get a user last known position with `GET`:

```
http://host:4567/position/<id>

Data: { body: { token:<someToken>, userId:<someId> } }
Returns { "token": <someToken> }
```
- Update a user position with `PUT`:

```
http://host:4567/position/

Data: { body: { token:<someToken>, deviceid:<someId>, userId:<someId>, latitude:<someDecimal>, longitude:<someDecimal> } }
Returns { "token": <someToken> }
```
- Get a user session with `GET`:

```
http://host:4567/session/deviceid

Returns { <sessionJson> }
```
- Delete a user session with `DELETE`:

```
http://host:4567/session/deviceid

Returns { "Success": true or false}
```
- Get users within a given radius from a position with `GET`:

```
http://host:4567/closeusers?lat=<some number>&longi=<some number>&radius=<some number>

Returns [{<sessionJson>}, {<sessionJson>}, ...]
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
| updatedAt   | Timestamp|
| latitude    | Decimal |
| longitude   | Decimal |
| gcmid       | String  |

## TODO
- Add authentication to the database
- Protect the API
- Check for SQLInjection
