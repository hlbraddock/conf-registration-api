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

INSERT INTO conference_costs (id,base_cost,minimum_deposit) VALUES ('d3be03f0-198f-11e3-8ffd-0800200c9a66', '50.00', '10.0');
INSERT INTO conference_costs (id,base_cost,minimum_deposit,early_registration_discount,early_registration_cutoff,early_registration_amount) VALUES ('abcd03f0-198f-11e3-8ffd-0800200c5555', '75.00', '0.0', true, '31-Mar-2013 23:59:59', '15');