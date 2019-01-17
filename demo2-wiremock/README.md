# Demo2: WireMock


### Docker Compose

With Docker Compose you can easily create containers with predefined states. 

In the `demo2-wiremock` folder you can see a `docker-compose.yml` file, what will create:
- a MySQL database instance with predefined schema and inserted default dataset
- an Adminer DB client running on port 8080
- 2 WireMock servers running on ports 9000 and 9001

Start the containers by running `docker-compose up` command. When it is running in another terminal you can list 
the running containers:
```
$ docker ps
CONTAINER ID        IMAGE                                COMMAND                  CREATED             STATUS              PORTS                              NAMES
91fca5e5f821        rodolpheche/wiremock:2.19.0-alpine   "/docker-entrypoint.…"   48 seconds ago      Up 26 seconds       8443/tcp, 0.0.0.0:9001->8080/tcp   test-chargingstation-mock
79c5b285bf28        adminer                              "entrypoint.sh docke…"   48 seconds ago      Up 27 seconds       0.0.0.0:8080->8080/tcp             test-adminer
8597654c997b        rodolpheche/wiremock:2.19.0-alpine   "/docker-entrypoint.…"   48 seconds ago      Up 27 seconds       8443/tcp, 0.0.0.0:9000->8080/tcp   test-wordservice-mock
e69efb1a14d0        mysql:5.7.16                         "docker-entrypoint.s…"   48 seconds ago      Up 27 seconds       0.0.0.0:3306->3306/tcp             test-db
```

Now you can login to the database on `http://localhost:8080`:

|              |               |
|--------------|---------------|
| **System**   | MySQL         |
| **Server**   | test-db       |
| **Username** | root          |
| **Password** | pass          |
| **Databse**  | test-database |

You can see the `test_table` already existing with some predefined data:

| column1 | column2 |
|---------|---------|
| val01   | val02   |
| val11   | val12   |

The Docker container also mounted the database files to an external folder, so your data changes will be persisted 
across container restarts or recreations. You can test this by changing any values in the database and running the 
following commands:
1. `docker-compose down`
2. `docker container prune`
3. `docker-compose up`


### WireMock: Word service

On port 9000 we have a MockServer for word service. As you can see the file contents under `demo2-wiremock\wordservice\mappings`
the following operations are mocked:

**Filter valid words**

*operation:*
will return `true` for every word except of `nonexistent`
  
*endpoint:*  
GET /words?wordExist={word}  

*request:*

*response:*  
status: 200  
body: true

*command:*
```
curl -v http://localhost:9000/words\?wordExist=anything
```

**Filter invalid words**

*operation:*
will return `false` for word `nonexistent`
  
*endpoint:*  
GET /words?wordExist=nonexistent  

*request:*

*response:*  
status: 404
header: Content-Type: application/json  
body: true

*command:*
```
curl -v http://localhost:9000/words\?wordExist=nonexistent
```

**Create word**

*operation:*
simulates creating a new word
  
*endpoint:*  
POST /words  

*request:*  
header: Content-Type: application/json  
body: { "word": "string", "createdBy": "string" }

*response:*  
status: 201  
header: Content-Type: application/json

*command:*
```
curl -v http://localhost:9000/words -X POST \
-H "Content-Type: application/json" \
-d "{\"word\":\"anyWord\", \"createdBy\":\"anyOne\"}"
```

**Delayed response**

*operation:*
simulates response delay between 1-5 seconds
  
*endpoint:*  
GET /words/delay  

*request:*  

*response:*  
status: 200  

*command:*
```
echo "started $(date +%s)" && \
curl -v http://localhost:9000/words/delay && \
echo "finished $(date +%s)"
```

**Faulty response**

*operation:*
simulates faulty response
  
*endpoint:*  
GET /words/fault  

*request:*  

*response:*  

*command:*
```
curl -v http://localhost:9000/words/fault
```


### WireMock: Charging station service

This WireMock server on poprt 9001 has response templating enabled by using the flag `--global-response-templating`.
The stub under `demo2-wiremock\chargingstation\mappings\get_station_by_evseId.json` is using the built-in 
`response-template` transformer, and the response comes from the `demo2-wiremock\chargingstation\single_station.json` file.

In the response you will see values replaced by Handlerbars templates. Some simple templates are used directly like `{{randomValue type='UUID'}}`.
Other templates are used multiple times, or they are transformed many times before the usage, so this is why they are assigned
to a variable before usage, like `"{{#assign 'stationId'}}{{randomValue length=10 type='ALPHANUMERIC'}}{{/assign}}"`

*command:*
`curl http://localhost:9001/charging-stations\?evseId=HU\*NKM\*ID01\*A`



### WireMock: Admin API

It is possible to manage or query WireMock stubs using the REST API under `/__admin`.

Examples:

1. Mappings: `curl http://localhost:9000/__admin/mappings`
2. Requests: `curl http://localhost:9000/__admin/requests`
3. Unmatched requests: `curl http://localhost:9000/__admin/requests/unmatched`
4. Near missed requests: `curl http://localhost:9000/__admin/requests/unmatched/near-misses`
5. Find or count requests:  
`curl -X POST http://localhost:9000/__admin/requests/count -d "{\"url\":\"/words/delay\"}"`  
`curl http://localhost:9000/__admin/requests\?since=2019-01-16T05:42:00Z\&limit=2`
6. Shut down: `curl -X POST http://localhost:9000/__admin/shutdown`  

Check the full Admin documentation at `http://wiremock.org/docs/api/`