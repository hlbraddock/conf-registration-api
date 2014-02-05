conf-registration-api

Current project status: https://trello.com/b/i7500iKQ/new-crs

To run the API in your development environment, the following steps need to be taken:

* Clone this project.
* Download and Install application server: wildfly-8.0.0.Beta1 (http://wildfly.org/downloads/)
* Download and Install local database: postgresql v9.3.x (http://www.postgresql.org/download/)
* Create and populate database.  Run: `src/main/db/create_crs_db_and_roles.sh`
* Build project. Run: `mvn clean package`
* Deploy project Copy target/crs_http_json_api.war to {wildfly_home}/standalone/deployments/
* Start application server {wildfly_home}/bin/standalone.sh
* 

## License

The Conference Registration API (conf-registration-api) is released under the MIT License (http://opensource.org/licenses/MIT)

=====================
