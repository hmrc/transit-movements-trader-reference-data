
# transit-movements-trader-reference-data

This service provides a series of endpoints for fetching reference data.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

### Steps to run transit-movements-trader-reference-data against the real reference data route
1. Make sure customs-reference-data is running locally
2. And then run the seed jobs under customs-reference-data-postman repo, to seed data to local database
    1. POST Reference data - Seed job
    2. POST Customs Office - Seed job
3. Then run transit-movements-trader-reference-data (ETL will ping the customs-reference-data service every hour (every min locally) in environments to get the latest reference data to access FE

