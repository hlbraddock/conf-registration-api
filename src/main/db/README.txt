These scripts assume the following:

-postgresql v9.2.x is installed and running on your system
-your system has a system (unix) user account named postgres
-the postgres user is running the postgres application

To create the CRS database and roles, run: 

create_crs_db_and_roles.sh

The following roles will be created and require a password to be entered
-dbadmin - role which has privleges to create other roles and database, but is not a postgresql "super-user"
-crsuser - role which has full admin privileges to the CRS database (crsdb). also is not a postgresql "super-user"

This script typically only needs to be run once to set up these roles and databases, but can
be re-run if necessary.

Once the roles and database are created, run:

populate_crs_db.sh

This script can be run and re-run as necessary to refresh the data in the local crsdb.  It will drop and recreate
all database objects within crsdb and populate them with data, though not all tables have data in them just yet.
