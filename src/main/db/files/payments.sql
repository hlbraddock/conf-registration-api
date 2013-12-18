DROP TABLE payments CASCADE;

CREATE TABLE payments (
  id uuid NOT NULL PRIMARY KEY,
  registration_id uuid references registrations(id),
  authnet_transaction_id bigint,
  cc_name_on_card text,
  cc_expiration_month text,
  cc_expiration_year varchar(4),
  cc_last_four_digits varchar(4),
  amount decimal,
  transaction_timestamp timestamp with time zone
  
);

INSERT INTO payments(id, registration_id, authnet_transaction_id, cc_name_on_card, cc_expiration_month, cc_expiration_year, cc_last_four_digits, amount, transaction_timestamp) VALUES ('8492F4A8-C7DC-4C0A-BB9E-67E6DCB22222', 'AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111', '2313987492387498248', 'Billy User', '04', '2014', '1111', 20.00, '21-Aug-2013 19:22:07');
INSERT INTO payments(id, registration_id, authnet_transaction_id, cc_name_on_card, cc_expiration_month, cc_expiration_year, cc_last_four_digits, amount, transaction_timestamp) VALUES ('8492F4A8-C7DC-4C0A-BB9E-67E6DCB33333', 'AAAAF4A8-C7DC-4C0A-BB9E-67E6DCB91111', '2313987492387498259', 'Billy User', '04', '2014', '1111', 55.00, '1-Oct-2013 11:02:11');
