!/bin/sh

DIR=files
CREATE_SCRIPTS="users.sql auth_provider_identities.sql conference_costs.sql conferences.sql registrations.sql pages.sql blocks.sql answers.sql authorizations.sql"

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

