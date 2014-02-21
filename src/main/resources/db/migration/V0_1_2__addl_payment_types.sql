ALTER TABLE payments ADD COLUMN check_number varchar(10), ADD COLUMN description text, ADD COLUMN transfer_source text;

ALTER TYPE payment_type RENAME TO payment_type_x_type;
CREATE TYPE payment_type AS ENUM('CASH', 'CHECK', 'CREDIT_CARD', 'CREDIT_CARD_REFUND', 'SCHOLARSHIP', 'TRANSFER');

ALTER TABLE payments RENAME COLUMN payment_type to payment_type_x_col;
ALTER TABLE payments ADD COLUMN payment_type payment_type;
UPDATE payments SET payment_type = payment_type_x_col::text::payment_type;

ALTER TABLE payments DROP COLUMN payment_type_x_col;
DROP TYPE payment_type_x_type;