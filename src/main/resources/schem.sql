CREATE TABLE Account(
	ID INTEGER PRIMARY KEY,
	Total INTEGER 
);

-- Contrainte d'intégrité : découvert interdit !
ALTER TABLE Account
ADD CONSTRAINT DECOUVERT_INTERDIT
	CHECK (Total >= 0);