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

  INSERT INTO payments(id, registration_id, authnet_transaction_id, cc_name_on_card, cc_expiration_month, cc_expiration_year, cc_last_four_digits, amount, transaction_timestamp) VALUES ('8492F4A8-C7DC-4C0A-BB9E-67E6DCB91957', 'A2BFF4A8-C7DC-4C0A-BB9E-67E6DCB982E7', 'fjkhdf9hf2jij09fafa', 'Joe User', '11', '2015', '1111', 50, '23-Aug-2013 05:17:55');
