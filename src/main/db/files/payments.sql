DROP TABLE payments CASCADE;

CREATE TABLE payments (
  id uuid NOT NULL PRIMARY KEY,
  registration_id uuid references registrations(id),
  authnet_transaction_id text,
  cc_name_on_card text,
  cc_expiration_month text,
  cc_expiration_year varchar(4),
  cc_last_four_digits varchar(4),

  amount decimal,
  transaction_timestamp timestamp with time zone
);