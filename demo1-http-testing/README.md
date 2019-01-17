# Demo1: HTTP Testing


### Simple application calling an HTTP endpoint

We have a simple Java application what sends HTTP requests to `http://127.0.0.1:9000/echo/{value}` url. 
It sends GET requests with values 42 and 21 using an API key for authentication.

Try to run the application from IDE by running the `com.example.httptesting.Application` class, or from command line 
by running `./gradlew run` command. You will see an error stacktrace with `Connection refused` message,  what means it 
can't connect to the server:

```
Server responded with:
Exception in thread "main" java.net.ConnectException: Failed to connect to /127.0.0.1:9000
        at okhttp3.internal.connection.RealConnection.connectSocket(RealConnection.java:248)
...
Caused by: java.net.ConnectException: Connection refused (Connection refused)
        at java.base/java.net.PlainSocketImpl.socketConnect(Native Method)
...
```

So, how do we test if the application works fine?


### Test application with mocked server

In the `com.example.httptesting.ApplicationTest` test application we created a MockServer to stub the requests with the given API key.
The server needs to be started before calling the `Application` object, and it needs to be stopped after the application run.

Right now the MockServer is using WireMock for simplicity, but it could be any real or mocked server. 

Run the test application by running the class from the IDE or by running `./gradlew runTestApp` command what will respond with:

```
Server responded with:
42
21
```

Now we could test our implementation, but this is not a proper way of testing.


### Use MockServer in component tests

Tests should validate single behaviour. We can write separate component tests using the same MockServer and we can verify 
if the endpoint was called with the expected parameters.

In `com.example.httptesting.ApplicationComponentTest` you can see how to prepare, call and verify the mocked endpoint.

You can run the tests from you IDE, or by running `./gradlew test` command.

You can see the results by opening `build\reports\tests\test\index.html` file, or on the console:

```
$ ./gradlew clean test

> Task :test
Executing test test21Echoed [com.example.httptesting.ApplicationComponentTest] with result: SUCCESS
Executing test test42Echoed [com.example.httptesting.ApplicationComponentTest] with result: SUCCESS

BUILD SUCCESSFUL in 3s
4 actionable tasks: 4 executed
```
