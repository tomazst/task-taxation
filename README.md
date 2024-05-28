# Tasks to setup the project

## Get task-taxation project
git clone https://github.com/tomazst/task-taxation.git

## Create tables mysql db

```sql
-- create trader table to test taxation service
CREATE TABLE IF NOT EXISTS `trader` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tax_value_type` varchar(255) DEFAULT NULL,
  `tax_value` decimal(38,2) DEFAULT NULL,
  `trader_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- insert some dummy data
INSERT INTO `trader` (`id`, `tax_value_type`, `tax_value`, `trader_id`) VALUES
	(1, 'RATE', 10.00, 1),
	(2, 'AMOUNT', 20.00, 2),
	(3, 'RATE', 22.00, 3),
	(4, 'AMOUNT', 5.00, 4);


-- create fa_random table to test fa_random.txt data insert
CREATE TABLE IF NOT EXISTS `fo_random` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `match_id` varchar(50) NOT NULL DEFAULT '0',
  `market_id` int(11) NOT NULL DEFAULT '0',
  `outcome_id` varchar(50) NOT NULL DEFAULT '0',
  `specifiers` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `date_insert` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=630212 DEFAULT CHARSET=utf8;
```

## Build project with maven
mvn clean install

## Start application
mvn spring-boot:run

## Execute request to services with postman
### Call regular taxation endpoint
```
curl --location 'http://127.0.0.1:8080/api/tax/general' \
--header 'Content-Type: application/json' \
--data '{
    "traderId": 2,
    "playedAmount": 100,
    "odd": 3.2
}'
```
### Call winings taxation endpoint
```
curl --location 'http://127.0.0.1:8080/api/tax/winings' \
--header 'Content-Type: application/json' \
--data '{
    "traderId": 2,
    "playedAmount": 100,
    "odd": 3.2
}'
```

## Test data insert performance
Project has two unit tests methods in DataImportTest. Uncomment Test annotations to enable tests.

First is bulkInsertPerformanceTest that insert data with generating multi value inserts. (Execution duration in milis: 11245)

Second one is fileInsertPerformanceTest that prpares data in a tmp file and uses LOAD DATA command to import to database from a file. (Execution duration in milis: 7330)
