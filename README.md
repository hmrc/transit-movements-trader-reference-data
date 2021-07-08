
# transit-movements-trader-reference-data

This is a placeholder README.md for a new repository

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").


### Steps to run transit-movements-trader-reference-data against the real reference data route
•  Make sure customs-reference-data is running locally
•  And then run the seed jobs under customs-reference-data-postman repo, to seed data to local database
            o  POST Reference data - Seed job
            o  POST Customs Office - Seed job
•  Then run transit-movements-trader-reference-data (ETL will ping the customs-reference-data service every hour (every min locally) in environments to get the latest reference data to access FE

