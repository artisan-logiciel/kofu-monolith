-- noinspection SqlNoDataSourceInspectionForFile
CREATE TABLE IF NOT EXISTS users
(
    login     VARCHAR PRIMARY KEY,
    firstname VARCHAR,
    lastname  VARCHAR
);
MERGE INTO users VALUES ('smaldini', 'Stéphane', 'Maldini'),
                        ('sdeleuze', 'Sébastien', 'Deleuze'),
                        ('bclozel', 'Brian', 'Clozel');

CREATE TABLE IF NOT EXISTS authority
(
    `role` VARCHAR PRIMARY KEY
);
MERGE INTO authority VALUES ('ADMIN'),
                            ('USER'),
                            ('ANONYMOUS');
CREATE TABLE IF NOT EXISTS `user`
(
    id                 UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    login              VARCHAR,
    password           VARCHAR,
    first_name         VARCHAR,
    last_name          VARCHAR,
    created_by         UUID,
    created_date       datetime,
    last_modified_by   datetime,
    last_modified_date datetime,
    email              VARCHAR,
    activated          boolean,
    lang_key           VARCHAR,
    image_url          VARCHAR,
    activation_key     VARCHAR,
    reset_key          VARCHAR,
    reset_date         datetime
);
CREATE TABLE IF NOT EXISTS user_authority
(
    id     UUID,
    `role` VARCHAR,
    FOREIGN KEY (id) REFERENCES `user` (id)
        ON DELETE CASCADE,
    FOREIGN KEY (`role`) REFERENCES authority (`role`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id, `role`)
);