ALTER TABLE `study`.`study` ADD COLUMN `AUTO_GENERATE_BIOSPECIMENUID` TINYINT NOT NULL DEFAULT 0  AFTER `PARENT_ID` , ADD COLUMN `AUTO_GENERATE_BIOCOLLECTIONUID` TINYINT NOT NULL DEFAULT 0  AFTER `AUTO_GENERATE_BIOSPECIMENUID` ;

