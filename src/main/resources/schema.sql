CREATE TABLE Account(
	ID INTEGER PRIMARY KEY,
-- Contraine d'intégrité : découvert interdit !
	Total INTEGER CHECK (Total >= 0)
);
