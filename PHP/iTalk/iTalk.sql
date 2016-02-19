-- phpMyAdmin SQL Dump
-- version 4.0.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 03, 2015 at 10:09 AM
-- Server version: 5.6.14
-- PHP Version: 5.5.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `iTalk`
--

-- --------------------------------------------------------

--
-- Table structure for table `chat`
--

CREATE TABLE IF NOT EXISTS `chat` (
  `myid` int(10) unsigned NOT NULL,
  `id` int(10) unsigned NOT NULL,
  `type` tinyint(1) NOT NULL,
  `text` text,
  `file` longblob,
  `filetype` tinyint(4) DEFAULT NULL,
  `other` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Stand-in structure for view `LatestMsg`
--
CREATE TABLE IF NOT EXISTS `LatestMsg` (
`myid` int(10) unsigned
,`id` int(10) unsigned
,`type` tinyint(1)
,`text` text
,`file` longblob
,`filetype` tinyint(4)
,`other` int(11)
,`time` timestamp
);
-- --------------------------------------------------------

--
-- Table structure for table `USERINFO`
--

CREATE TABLE IF NOT EXISTS `USERINFO` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` char(64) NOT NULL,
  `pwd` char(64) NOT NULL,
  `nickname` varchar(64) DEFAULT NULL,
  `profilepic` blob,
  `lastlogin` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Structure for view `LatestMsg`
--
DROP TABLE IF EXISTS `LatestMsg`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `LatestMsg` AS select `o`.`myid` AS `myid`,`o`.`id` AS `id`,`o`.`type` AS `type`,`o`.`text` AS `text`,`o`.`file` AS `file`,`o`.`filetype` AS `filetype`,`o`.`other` AS `other`,`o`.`time` AS `time` from `chat` `o` where (`o`.`id` = (select max(`chat`.`id`) from `chat` where (`chat`.`other` = `o`.`other`))) group by `o`.`other`;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
