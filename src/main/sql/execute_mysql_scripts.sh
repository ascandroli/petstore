mysql -u root -proot < create_databases.sql
mysql petstore -u root -proot < create_changelog_table.sql
mysql petstore_dev -u root -proot < create_changelog_table.sql
mysql petstore_tests -u root -proot < create_changelog_table.sql

