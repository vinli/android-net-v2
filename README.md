Vinli Android Net SDK V2
=====================

Coming soon...

VinliRequest.Builder builder = new VinliRequest.Builder()
  .stack(VinliRequest.STACK_DEV) // hidden API, gated off with build flags
  .logLevel(VinliRequest.LOG_LEVEL_FULL)
  .getDevices()
  .since(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3))
  .until(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1))
  .limit(20);

// ...

builder.build()
  .asObservable()
  .subscribe(/*...*/);

builder.build()
  .asCallback(/*...*/)

