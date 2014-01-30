CREATE TYPE profile_type AS ENUM ( 'EMAIL', 'NAME', 'PHONE', 'ADDRESS', 'BIRTH_DATE', 'GENDER', 'CAMPUS', 'GRADUATION', 'DORMITORY' );

CREATE TYPE permission_levels AS ENUM ('VIEW', 'UPDATE', 'FULL', 'CREATOR');

CREATE TYPE payment_type AS ENUM ('CREDIT_CARD', 'CREDIT_CARD_REFUND', 'CHECK', 'CASH', 'SCHOLARSHIP');

CREATE TABLE users (
  id uuid NOT NULL PRIMARY KEY,
  first_name text,
  last_name text,
  phone_number text,
  email_address text
);

CREATE TABLE auth_provider_identities(
	id uuid NOT NULL PRIMARY KEY,
	crs_id uuid references users(id),
	auth_provider_name text NOT NULL,
	user_auth_provider_id text NOT NULL,
	auth_provider_user_access_token text,
	username text,
	email text,
	first_name text,
	last_name text
);

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

CREATE TABLE conferences (
  id uuid NOT NULL PRIMARY KEY,
  conference_costs_id uuid references conference_costs(id),
  name text,
  description text,
  event_start_time timestamp with time zone,
  event_end_time timestamp with time zone,
  registration_start_time timestamp with time zone,
  registration_end_time timestamp with time zone,
  total_slots integer default NULL,
  contact_person_name text,
  contact_person_email text,
  contact_person_phone text,
  location_name text,
  location_address text,
  location_city text,
  location_state text,
  location_zip_code varchar(5),
  require_login boolean
);

CREATE TABLE registrations (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid references users(id),
  conference_id uuid references conferences(id),
  total_due decimal default 0,
  completed boolean default false,
  completed_timestamp timestamp with time zone
);

CREATE TABLE payments (
  id uuid NOT NULL PRIMARY KEY,
  registration_id uuid references registrations(id),
  authnet_transaction_id bigint,
  cc_name_on_card text,
  cc_expiration_month text,
  cc_expiration_year varchar(4),
  cc_last_four_digits varchar(4),
  amount decimal,
  transaction_timestamp timestamp with time zone,
  payment_type payment_type,
  updated_by_user_id uuid references users(id),
  refunded_payment_id uuid references payments(id)
);

CREATE TABLE pages (
  id uuid NOT NULL PRIMARY KEY,
  title text,
  conference_id uuid NOT NULL references conferences(id),
  position integer
);

CREATE TABLE blocks (
  id uuid NOT NULL PRIMARY KEY,
  page_id uuid references pages(id),
  block_type text,
  admin_only boolean,
  required boolean,
  position integer,
  title text,
  content json,
  profile_type profile_type
);

CREATE TABLE registration_views(
	id uuid not null primary key,
	conference_id uuid references conferences(id),
	created_by_user_id uuid references users(id),
	name text,
	visible_block_ids json
);

CREATE TABLE answers (
  id uuid NOT NULL PRIMARY KEY,
  registration_id uuid references registrations(id),
  block_id uuid references blocks(id),
  answer json
);

CREATE TABLE permissions (
  id uuid NOT NULL PRIMARY KEY,
  conference_id uuid references conferences(id),
  user_id uuid references users(id),
  email_address text,
  permission_level permission_levels,
  activation_code varchar(41),
  given_by_user_id uuid references users(id),
  last_updated_timestamp timestamp with time zone
);

CREATE TABLE sessions(
	id uuid NOT NULL PRIMARY KEY,
	auth_code text NOT NULL,
    expiration timestamp with time zone,
	auth_provider_id uuid references auth_provider_identities(id)
);

CREATE TABLE profiles (
  id uuid NOT NULL PRIMARY KEY,
  user_id uuid references users(id),
  email text,
  firstName text,
  lastName text,
  phone text,
  address1 text,
  address2 text,
  city text,
  state text,
  zip varchar(5),
  birth_date date,
  gender varchar(1),
  campus text,
  graduation date,
  dormitory text
);