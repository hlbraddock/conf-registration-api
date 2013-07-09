echo 'WARNING!  This command will drop and recreate the entire crsdb database and roles'
printf '\n'
echo 'Do you want to continue? (Y/N) '
read USER_CHOICE
if [ "$USER_CHOICE" = "Y" ] || [ "$USER_CHOICE" = "y" ]; then
	printf '\n'
else
	echo 'Exiting script.'
	exit 1
fi

dropdb -U postgres crsdb
dropuser -U postgres crsuser
dropuser -U postgres dbadmin

printf '\n'
echo 'Creating database user: DBADMIN'
echo 'This user will be used to create the database and roles for this application'
createuser -U postgres -lrdP dbadmin
echo 'Success!'

printf '\n'
echo 'Creating database user: CRSUSER'
echo 'This user will be used to administer and access the CRSDB'
createuser -U dbadmin -ldP crsuser
echo 'Success!'

printf '\n'
echo 'Creating database: CRSDB'
createdb -U dbadmin crsdb
echo 'Success!'

printf '\n'
echo 'Granting all privileges on CRSDB to CRSUSER'
psql -U dbadmin -d crsdb -c "GRANT ALL PRIVILEGES ON DATABASE crsdb to crsuser"
echo 'Success!'

printf '\n'
echo 'Setting up database...'
./init_crs_mock_db.sh

printf '\n'
echo 'Finished creating database.. happy conferencing!'
