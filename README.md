conf-registration-api

To run the API in your development environment, the following steps need to be taken:

* Download and Install jboss v7.1.x.FINAL or v7.2.x.FINAL
* Download and Install postgresql v9.2.x
* Configure postgresql datasource in jboss installation by adding snippets from src/main/db/postgres-ds.xml to standalone.xml
* Install postgresql driver as a module in jboss
* Use the driver version 9.2.  The correct module.xml is provided as src/main/db/postgres-module.xml
* See http://kousikraj.me/2011/11/25/datasource-configuration-setup-for-jboss-as-7-with-example-of-postgresql/
* Create and populate sample database.  See: src/main/db/README.txt

=====================
