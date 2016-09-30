-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema loggdatacollector
-- -----------------------------------------------------
-- Magnus kreation, v 0.1
DROP SCHEMA IF EXISTS `loggdatacollector` ;

-- -----------------------------------------------------
-- Schema loggdatacollector
--
-- Magnus kreation, v 0.1
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `loggdatacollector` ;
USE `loggdatacollector` ;

-- -----------------------------------------------------
-- Table `loggdatacollector`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `loggdatacollector`.`user` ;

CREATE TABLE IF NOT EXISTS `loggdatacollector`.`user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `firstName` VARCHAR(20) NOT NULL,
  `lastName` VARCHAR(20) NOT NULL,
  `email` VARCHAR(20) NOT NULL,
  `username` VARCHAR(20) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loggdatacollector`.`application`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `loggdatacollector`.`application` ;

CREATE TABLE IF NOT EXISTS `loggdatacollector`.`application` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `log_name` VARCHAR(45) NOT NULL,
  `log_type` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loggdatacollector`.`company`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `loggdatacollector`.`company` ;

CREATE TABLE IF NOT EXISTS `loggdatacollector`.`company` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `website` VARCHAR(45) NOT NULL,
  `extra_incase` VARCHAR(45) NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_company_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_company_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `loggdatacollector`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loggdatacollector`.`log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `loggdatacollector`.`log` ;

CREATE TABLE IF NOT EXISTS `loggdatacollector`.`log` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `dateTime` DATETIME NOT NULL,
  `event` VARCHAR(65000) NOT NULL,
  `special_event` VARCHAR(45) NOT NULL,
  `application_id` INT UNSIGNED NOT NULL,
  `company_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_log_application_idx` (`application_id` ASC),
  INDEX `fk_log_company_idx` (`company_id` ASC),
  CONSTRAINT `fk_log_application`
    FOREIGN KEY (`application_id`)
    REFERENCES `loggdatacollector`.`application` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_log_company`
    FOREIGN KEY (`company_id`)
    REFERENCES `loggdatacollector`.`company` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `loggdatacollector`.`config`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `loggdatacollector`.`config` ;

CREATE TABLE IF NOT EXISTS `loggdatacollector`.`config` (
  `id` INT UNSIGNED NOT NULL,
  `latest_update` DATETIME NOT NULL,
  `interval` INT NOT NULL,
  `company_id` INT UNSIGNED NOT NULL,
  `application_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_config_company_idx` (`company_id` ASC),
  INDEX `fk_config_application_idx` (`application_id` ASC),
  CONSTRAINT `fk_config_company`
    FOREIGN KEY (`company_id`)
    REFERENCES `loggdatacollector`.`company` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_config_application`
    FOREIGN KEY (`application_id`)
    REFERENCES `loggdatacollector`.`application` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

