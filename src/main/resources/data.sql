-- Sekvencie
create sequence user_id_seq start with 1 increment by 1;
create sequence project_id_seq start with 1 increment by 1;
create sequence task_id_seq start with 1 increment by 1;

-- Používatelia: id, name, email
INSERT INTO user VALUES
(next value for user_id_seq, 'Richard Mrkvicka', 'jozko.mrkvicka@gmail.com'),
(next value for user_id_seq, 'Maria Terezia', 'maria.terezia@gmail.com'),
(next value for user_id_seq, 'Albert Einstein', 'albert.einstein@gmail.com'),
(next value for user_id_seq, 'Ada Lovelace', 'ada.lovelace@gmail.com'),
(next value for user_id_seq, 'Nikola Tesla', 'nikola.tesla@gmail.com');

-- Projekty: id, user_id, name, description, created_at
INSERT INTO project VALUES
(next value for project_id_seq, 1, 'Jozkov projekt', 'Tasky v robote', CURRENT_TIMESTAMP),
(next value for project_id_seq, 2, 'Projekt Marie Terezie', 'Moj Todolist', CURRENT_TIMESTAMP),
(next value for project_id_seq, 3, 'Relativita', 'Výpočty ohýbania časopriestoru', CURRENT_TIMESTAMP),
(next value for project_id_seq, 4, 'Analýza algoritmov', 'Matematická analýza výpočtov', CURRENT_TIMESTAMP),
(next value for project_id_seq, 5, 'Elektrický prúd', 'Testovanie vysokofrekvenčných prúdov', CURRENT_TIMESTAMP);

-- Tasky: id, user_id, project_id, name, description, status, created_at
INSERT INTO task VALUES
(next value for task_id_seq, 1, 1, 'Spravit API', 'API ma byt pre noveho klienta', 'DONE', CURRENT_TIMESTAMP),
(next value for task_id_seq, 1, 1, 'Otestovat API', 'Unit testy + integracne testy', 'NEW', CURRENT_TIMESTAMP),
(next value for task_id_seq, 2, 2, 'Kupit mame darcek', null, 'NEW', CURRENT_TIMESTAMP),
(next value for task_id_seq, 3, 3, 'Spocitat casovy dilatacny efekt', 'Porovnat so simulaciou', 'IN_PROGRESS', CURRENT_TIMESTAMP),
(next value for task_id_seq, 4, 4, 'Napísať pseudokód', 'Preložiť do ASM', 'NEW', CURRENT_TIMESTAMP),
(next value for task_id_seq, 5, 5, 'Zmerať voltáž', 'Testovanie Tesla cievky', 'DONE', CURRENT_TIMESTAMP),
(next value for task_id_seq, 3, 3, 'Nakresliť diagramy', 'Vizualizácia časopriestoru', 'NEW', CURRENT_TIMESTAMP);
