/* USAGE: mysql -u root -p < create_databases.sql */

DROP DATABASE IF EXISTS petstore;
DROP DATABASE IF EXISTS petstore_dev;
DROP DATABASE IF EXISTS petstore_tests;

CREATE DATABASE petstore CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE DATABASE petstore_dev CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE DATABASE petstore_tests CHARACTER SET utf8 COLLATE utf8_general_ci;

GRANT ALL ON petstore.* TO petstore_admin@localhost IDENTIFIED BY "petstore_4dm1n";
GRANT ALL ON petstore_dev.* TO petstore_admin@localhost IDENTIFIED BY "petstore_4dm1n";
GRANT ALL ON petstore_tests.* TO petstore_admin@localhost IDENTIFIED BY "petstore_4dm1n";

FLUSH PRIVILEGES;
exit