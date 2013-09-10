!/bin/sh

DIR=files
CREATE_SCRIPTS="users.sql cru_crs_auth_provider_identities.sql conferences.sql cru_crs_registrations.sql pages.sql blocks.sql answers.sql cru_crs_authorizations.sql"

USER=crsuser
DB=crsdb

echo

for createScript in $CREATE_SCRIPTS
do
    echo "Running sql script ${createScript}"
    psql -f $DIR/$createScript -U $USER -d $DB > /dev/null
    echo "${createScript} complete"
    echo
done

