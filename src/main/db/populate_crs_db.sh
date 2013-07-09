printf '\n'
echo 'Creating and populating table CRU_CRS_CONFERENCES'
psql -f files/cru_crs_conferences.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'

printf '\n'
echo 'Creating and populating table CRU_CRS_BLOCKS'
psql -f files/cru_crs_blocks.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'

printf '\n'
echo 'Creating and populating table CRU_CRS_PAGES'
psql -f files/cru_crs_pages.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'

printf '\n'
echo 'Creating and populating table CRU_CRS_USERS'
psql -f files/cru_crs_users.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'

printf '\n'
echo 'Creating and populating table CRU_CRS_IDENTITIES'
psql -f files/cru_crs_identities.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'

printf '\n'
echo 'Creating and populating table CRU_CRS_REGISTRATIONS'
psql -f files/cru_crs_registrations.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'

printf '\n'
echo 'Creating and populating table CRU_CRS_ANSWERS'
psql -f files/cru_crs_answers.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'

printf '\n'
echo 'Creating and populating table CRU_CRS_AUTHORIZATIONS'
psql -f files/cru_crs_authorizations.sql -U crsuser -d crsdb -L logs/populate-db-`eval date +%F`.log > /dev/null 2> /dev/null
echo 'Success!'
