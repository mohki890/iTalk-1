<?php

/* * *********NEED TO CHANGE SQL DATABASE USER **************** */

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */

/* * Need To Confirm Vulnerability due to this** */ //Check IT
$maxLengthUser = 64;
$maxLengthPwd = 64;


include 'DB_connection.php';

function DB_checkLogin($username, $pwd) {
    $username = santize($username);
    $hashpwd = hash_pwd($pwd);
    $q = "SELECT id,username,nickname FROM USERINFO WHERE username='" . $username . "' AND pwd='" . $hashpwd . "';";
    $rslt = mysqli_query($GLOBALS['con'], $q);
    if ($rslt != FALSE) {
        if (mysqli_num_rows($rslt) > 0) {
            $row = mysqli_fetch_array($rslt);
            $_SESSION['id'] = $row['id'];
            $_SESSION['username'] = $row['username'];
            $_SESSION['nickname'] = $row['nickname'];
            DB_ping(TRUE);
            return 1;
        } else
            return 0;
        
    }
    return -1;
}

function santize($str) {
    $str = mysql_real_escape_string($str);
    return $str;
}

function DB_addUser($username, $pwd) {
    if (strlen($username) > $GLOBALS['maxLengthUser'] || strlen($pwd) > $GLOBALS['maxLengthPwd'])
        return FALSE;

//Check It later SQL INJECTIONS --- ALL TYPES
    $username = santize($username);
    $q = "INSERT INTO USERINFO (username,pwd,nickname) VALUES ('" . $username . "' , '" . hash_pwd($pwd) . "','" . $username . "' );";

    if (mysqli_query($GLOBALS['con'], $q) == FALSE)
        return FALSE;


//Get User Id
    $id = NULL;
    $q = "SELECT id FROM USERINFO WHERE username='" . $username . "' AND pwd='" . hash_pwd($pwd) . "';";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return FALSE;
    else {
        $id = mysqli_fetch_array($result)['id'];
    }
/*
    $q = "CREATE TABLE IF NOT EXISTS `chat_" . $id . "` (" .
            " `id` INT(10) UNSIGNED NOT NULL," .    //  AUTO_INCREMENT
            "`type` TINYINT(1) NOT NULL,".
            " `text` text," .
            " `file` LONGBLOB, " .
            " `filetype` TINYINT, " .
            " `other` int(11) NOT NULL," .
            " `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP" .
            ");";// PRIMARY KEY (`id`)
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return FALSE;

 *  CREATE TABLE IF NOT EXISTS `iTalk`.`chat` ( 
    		`myid` INT(10) UNSIGNED NOT NULL,
            `id` INT(10) UNSIGNED NOT NULL,
            `type` TINYINT(1) NOT NULL,
            `text` text,
            `file` LONGBLOB, 
            `filetype` TINYINT, 
            `other` int(11) NOT NULL,
            `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP )            ;

 *  */

    echo $username . " Added!\n";
    return TRUE;
}

function DB_allUser() {
    $arr = array();
    $q = "SELECT id,username,nickname FROM USERINFO;";
    $rslt = mysqli_query($GLOBALS['con'], $q);
    if ($rslt != FALSE) {
        while (($row = mysqli_fetch_array($rslt))) {
            $arr[$row['id']] = array($row['username'], $row['nickname']);
        }
    }

    return json_encode($arr);
}

function DB_changeNName($nname)
{
    if(!is_numeric($_SESSION['id']))        return FALSE;
    $nname=  santize($nname);
    $q = "UPDATE `USERINFO` SET nickname='" . $nname . "' WHERE id=" . $_SESSION['id'] .";";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return FALSE;
    return TRUE;

}
function DB_changePwd($oldpwd,$pwd)
{
    if(!is_numeric($_SESSION['id']))        return FALSE;
    
    
    $q = "SELECT id FROM `USERINFO` WHERE id=" . $_SESSION['id'] ." AND pwd='" .  hash_pwd($oldpwd) . "';";
    $rslt = mysqli_query($GLOBALS['con'], $q);
    
    if ($rslt == FALSE) return FALSE;
    if(!mysqli_fetch_array($rslt)) return FALSE;
        
    $q = "UPDATE `USERINFO` SET pwd='" . hash_pwd($pwd) . "' WHERE id=" . $_SESSION['id'] ;
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return FALSE;
    return TRUE;

}
function DB_ping($currentTime)
{
    if(!is_numeric($_SESSION['id']))        return FALSE;
    $q = "";
    if($currentTime)
        $q = "UPDATE `USERINFO` SET lastlogin=CURRENT_TIMESTAMP WHERE id=" . $_SESSION['id'] ;
    else
        $q = "UPDATE `USERINFO` SET lastlogin=NULL WHERE id=" . $_SESSION['id'] ;
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return FALSE;
    
    return TRUE;
}


function hash_pwd($pwd) {
    return sha1($pwd);
}

?>
