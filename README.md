Vinli Android Net SDK V2
=====================

It's this simple:

```
final VinliRequest.Builder requestBuilder = VinliRequest.builder()
        .logLevel(Level.BODY)
        .readTimeout(30, SECONDS)
        .writeTimeout(30, SECONDS)
        .connectTimeout(30, SECONDS)
        .overallTimeout(60, SECONDS)
        .retryPolicy(exponential(2, SECONDS));
```

[Grab it on Bintray!](https://bintray.com/vinli)
