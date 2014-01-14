DROP TABLE registration_views CASCADE;

CREATE TABLE registration_views(
	id uuid,
	conference_id uuid references conferences(id),
	created_by_user_id uuid references users(id),
	name text,
	visible_block_ids json
);

INSERT INTO registration_views(id, conference_id, created_by_user_id, name, visible_block_ids) VALUES('11cfdedf-febc-4011-9b48-44d36bf94997', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'No cats', '["AF60D878-4741-4F21-9D25-231D-B86E43EE","DDA45720-DE87-C419-933A-0187-12B152D2"]');
INSERT INTO registration_views(id, conference_id, created_by_user_id, name, visible_block_ids) VALUES('70daba1c-c252-4c3f-a76c-f7c3d97941ab', '42E4C1B2-0CC1-89F7-9F4B-6BC3E0DB5309', 'dbc6a808-d7bc-4d92-967c-d82d9d312898', 'Name and email', '["AF60D878-4741-4F21-9D25-231D-B86E43EB","A229C854-6989-F658-7C29-B3DD-034F6FDB"]');
