CREATE TABLE IF NOT EXISTS user
(
    id    int auto_increment PRIMARY KEY,
    email varchar(255) NOT NULL,
    CONSTRAINT user_pk UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS email
(
    subject  text NULL,
    sender   int  NULL,
    receiver int  NULL,
    content  text NULL,
    id       int auto_increment PRIMARY KEY,
    CONSTRAINT email_user_id_fk FOREIGN KEY (sender) REFERENCES user (id),
    CONSTRAINT email_user_id_fk2 FOREIGN KEY (receiver) REFERENCES user (id)
);

INSERT INTO user (email) VALUES ('janedoe@example.com');
INSERT INTO user (email) VALUES ('johndoe@example.com');

INSERT INTO email (subject, sender, receiver, content)
VALUES ('Get Rich Quick', 1, 2, 'You have been selected to win a large sum of money! Click here.');

INSERT INTO email (subject, sender, receiver, content) VALUES 
('Project Update', 2, 1, 'The project is on track for delivery at the end of the month.');

