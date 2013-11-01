DROP TABLE conference_costs CASCADE;

CREATE TABLE conference_costs (
  id uuid NOT NULL PRIMARY KEY,
  base_cost decimal,
  minimum_deposit decimal default 0,
  early_registration_discount boolean default false,
  early_registration_amount decimal,
  early_registration_cutoff timestamp with time zone,
  accept_credit_cards boolean default true,
  authnet_id varchar(25),
  authnet_token varchar(25)
);

INSERT INTO conference_costs (id,base_cost,minimum_deposit) VALUES ('42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', '50.00', '10.0');
INSERT INTO conference_costs (id,base_cost,minimum_deposit,early_registration_discount,early_registration_cutoff,early_registration_amount) VALUES ('D5878EBA-9B3F-7F33-8355-3193BF4FB698', '75.00', '0.0', true, '31-Mar-2013 23:59:59', '15');