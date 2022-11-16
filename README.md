# TNT aggregator assignment
Author: Ion Bazavan

# Design decision
- Solution was implemented using an imperative multithreaded solution, using completable futures and thread safe data structures. I considered using reactor for a more concise solution, but lack of expertise with the technology and lack of times as well prevented me.
- Solution has maintainability due to the use of interface and generics, implementing a new service should be relatively easy, and if required methods can be overwritten
- Due to a bug on the OpenAPI generator that was used to generate the API and classes, validation is not working on query parameters that are arrays

# Left to do (due to lack of time/bugs)
- Further validation of content of query arrays
- Add unit tests and integration tests. These were left out due to a lack of time (8 hours for the complete solution from start to finish, with manual testing for all cases would not be enough to also add extensive automated testing)
- Create configurable values for URLs, number of retries, etc

# Prerequisite

- Docker installed

# How to run
```bash
./start.sh
```

# How to stop
If you run the application with docker then simply run following command:
 ```bash
 ./stop.sh
 ```
