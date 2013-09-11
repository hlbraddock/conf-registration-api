DROP TABLE conference_costs_blocks CASCADE;

CREATE TABLE conference_costs_blocks (
  id uuid PRIMARY KEY,
  conf_costs_id uuid NOT NULL REFERENCES conference_costs(id),
  description text,
  amount decimal,
  enabling_answer json
);

INSERT INTO conference_costs_blocks VALUES ('c173ed10-198d-11e3-8ffd-0800200c9a66', 'd3be03f0-198f-11e3-8ffd-0800200c9a66', 'Answer Meow Mix to raise your cost 10 dollars', '10.00', '"Meow Mix"');
INSERT INTO conference_costs_blocks VALUES ('c4b54db0-1993-11e3-8ffd-0800200c9a66', 'd3be03f0-198f-11e3-8ffd-0800200c9a66', 'Wilderness Survival adds $15.00', '15.00', '["Wilderness Survival"]');

