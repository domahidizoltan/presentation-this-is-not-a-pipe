# Demo5: Swagger bank

[Swagger bank](https://github.com/Tzinov15/swagger-bank) is a library to bridge Swagger API specifications and the 
Mountebank Response Stubbing Tool.

Under `demo5-swagger-bank\demo` there is a `demoApi.yaml` Swagger specification what you can see by copy-pasting it's content
into a [Swagger editor](https://editor.swagger.io/)

Swagger bank will generate Mountebank imposters from this specifications.

To try this out you need to build a custom Docker image what installs Swagger bank on NodeJS runtime.
```
docker build -t swaggerbank-demo .
```

The Docker Compose file creates a container from this image and attaches the Swagger specification with the Swagger bank configuration.
```
docker-compose up
```

Now you can see the stubbed responses by visiting the urls on port 3000, i.e. http://127.0.0.1:3000/v1/areas  
You can see that the values and UUID-s are not the ones defined in the specification examples.  
Swagger bank can run with three property generating mode:

* PROP_GEN=example: in this case the field values will use the given examples
* PROP_GEN=static: the values will be copied from the `config/options.js` file (like in the GitHub repo)
* PROP_GEN=random: generates random data from yaml description

Random mode will generate values as they are defined in the `type` and `format` descriptions. I.e. if you have `type: string`
and `format: uuid` it will generate a random UUID. These values are randomly generated after each restart.  

As you can see there are some limitations, i.e. resource relationships are not following the generated data, so you will 
get the same response for these links:
* http://127.0.0.1:3000/v1/areas/{anyAreaId}
* http://127.0.0.1:3000/v1/areas/{anyAreaId}/trails
* http://127.0.0.1:3000/v1/areas/{anyAreaId}/trails/{anyTrailId}
It gives empty response if the id contains `-`.


### Swagger generated server stub

In case you can use your defined examples, and you would use Swagger bank in static or example mode I suggest to try out 
the Swagger generated server stub.

* From the Swagger editor download the link for your desired server stub. In this example we will use a NodeJS stub. 
Download the zip file from:
```
editor -> Generate server -> nodejs-server
```

* Extract the zipfile to `demo5-swagger-bank\swagger-nodejs-server` 
* Comment out the container specification in the `docker-compose.yml` on lines 19-26.   
This will add the server stub to the image where we already have a NodeJS runtime.  
When you start the container you can see the responses on port 9080, i.e. http://127.0.0.1:9080/v1/areas
The Swagger specification is also viewable at http://127.0.0.1:9080/docs/
