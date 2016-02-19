<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
include 'DB_connection.php';
/*
 *  type    = 0 ;Send
 *  type    = 1 ;Received
 */


/* * **** Insted of Null Try 
 * 
 * mysqli_num_rows
 */

function DB_sendMsg($other_id, $msg) {

    if (!(isset($_SESSION['id']) && is_numeric($_SESSION['id'])))
        return 6;
    if (!is_numeric($other_id))
        return 5;
    $msg = mysql_real_escape_string($msg);
    $id_mytable = -1;
    $id_othertable = -1;

    /*     * **Obtaining ID for my table******* */
    $q = "SELECT max(id) FROM `chat` WHERE `myid`=" .$_SESSION['id'] ." AND `other`=" . $other_id . ";";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 4;

    $row = mysqli_fetch_array($result);
    if ($row == NULL)
        $id_mytable = 0;
    else
        $id_mytable = 1 + $row['max(id)'];

    /*     * **Obtaining ID for other table******* */
    $q = "SELECT max(id) FROM `chat` WHERE `myid`=" . $other_id ." AND `other`=" . $_SESSION['id'] . ";";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 3;
    $row = mysqli_fetch_array($result);
    if ($row == NULL)
        $id_othertable = 0;
    else
        $id_othertable = 1 + $row['max(id)'];


    /*     * **Should I check is_numeric for both id,, or is it a waste* */

    $q = "INSERT INTO `chat` (`myid`,`id`,`type`,`text`, `other`,`filetype`) VALUES (" . $_SESSION['id'] . " , " . $id_mytable . ",0,'" . $msg . "', '" . $other_id . "',0);";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 2;
    $q = "INSERT INTO `chat` (`myid`,`id`,`type`,`text`, `other`,`filetype`) VALUES (" . $other_id . " , " . $id_othertable . ",1,'" . $msg . "', '" . $_SESSION['id'] . "',0);";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 1;
    return 0;
}

function DB_getMsg($id, $minID, $maxID) {
    if (!is_numeric($minID))
        return FALSE;
    if (!is_numeric($maxID))
        return FALSE;

    if (!is_numeric($id))
        return FALSE;
    if (!(isset($_SESSION['id']) && is_numeric($_SESSION['id'])))
        return FALSE;

    /*     * **Obtaining ID for my table******* */



    $q = "SELECT max(id)  FROM `chat` WHERE `myid`=" . $_SESSION['id'] . " AND `other`=" . $id . ";";
    $lastest_msg = '0';
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return FALSE;
    $row = mysqli_fetch_array($result);
    if ($row != NULL)
        $lastest_msg = $row['max(id)'];

    $q = "SELECT `id`,`text`,`time`,`type`,`filetype`  FROM `chat` WHERE `myid`=" . $_SESSION['id'] . " AND `other`=" . $id . " AND ";
    if ($minID < 0)
        $minID = $lastest_msg - 10;
    if ($maxID >= 0) {
        $q = $q . "`id` BETWEEN " . ($minID) . "  AND " . $maxID . " ";
    } else {
        $q = $q . "`id` >= " . ($minID) . " ";
    }
    $q = $q . "ORDER BY `id`";
    $result = mysqli_query($GLOBALS['con'], $q);
    $arr = array();
    $c = 0;
    if ($result != FALSE) {
        while (($row = mysqli_fetch_array($result))) {
            $s = array();
            $s['id'] = $row['id'];
            $s['text'] = $row['text'];
            $s['time'] = $row['time'];
            $s['type'] = $row['type'];
            $s['filetype'] = $row['filetype'];
            $arr[$c++] = $s;
        }
    } else
        return FALSE;
    return $arr;
}

function DB_uploadFile($other_id, $file, $fileType, $fileName) {

    if (!(isset($_SESSION['id']) && is_numeric($_SESSION['id'])))
        return 1;
    if (!is_numeric($other_id))
        return 2;
    if (!is_numeric($fileType))
        return 3;

    /*     * *************Vulnerable For SQL Injection************* */
    //$file
    $fileName = mysql_real_escape_string($fileName);
    $id_mytable = -1;
    $id_othertable = -1;

    /*     * **Obtaining ID for my table******* */
    $q = "SELECT max(id) FROM `chat` WHERE `myid`=" . $_SESSION['id'] . " AND `other`=" . $other_id . ";";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 4;

    $row = mysqli_fetch_array($result);
    if ($row == NULL)
        $id_mytable = 0;
    else
        $id_mytable = 1 + $row['max(id)'];

    /*     * **Obtaining ID for other table******* */
    $q = "SELECT max(id) FROM `chat` WHERE `myid`=" . $other_id . " AND `other`=" . $_SESSION['id'] . ";";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 5;
    $row = mysqli_fetch_array($result);
    if ($row == NULL)
        $id_othertable = 0;
    else
        $id_othertable = 1 + $row['max(id)'];

    $q = "INSERT INTO `chat` (`myid`,`id`,`type`,`file`, `other`,`filetype`,`text`) VALUES (" . $_SESSION['id'] . " , " . $id_mytable . ",0,'" . base64_encode($file) . "', '" . $other_id . "'," . $fileType . ",'" . $fileName . "');";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 6;
    $q = "INSERT INTO `chat` (`myid`,`id`,`type`,`file`, `other`,`filetype`,`text`) VALUES (" . $other_id . " , " . $id_othertable . ",1,'" . base64_encode($file) . "', '" . $_SESSION['id'] . "'," . $fileType . ",'" . $fileName . "');";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE)
        return 7;
    return 0;
}

function DB_downloadFile($id, $fileID) {
    if (!is_numeric($fileID))
        return 1;
    if (!is_numeric($id))
        return 2;
    if (!(isset($_SESSION['id']) && is_numeric($_SESSION['id'])))
        return 3;

    $q = "SELECT `filetype`, `file`  FROM `chat` WHERE `myid`=" . $_SESSION['id'] . " AND `other`=" . $id . " AND `id` = " . ($fileID) . ";";
    $result = mysqli_query($GLOBALS['con'], $q);
    $fileData = "";
    if ($result != FALSE) {
        if (($row = mysqli_fetch_array($result))) {
            if ($row['filetype'] > 0) {
                $fileData = base64_decode($row['file']);
                if($fileData==FALSE)
                    return 4;
                echo $fileData;
                return 0;
            }
        }
    }
    return 4;
}

function DB_getLatestMsg() {
    if (!(isset($_SESSION['id']) && is_numeric($_SESSION['id'])))
        return FALSE;

    /*     * **Obtaining ID for my table******* */



    $q = "SELECT `id`,`other`,`text`,`time`,`type`,`filetype`  FROM `LatestMsg` WHERE `myid`=" . $_SESSION['id'] . ";";
    $result = mysqli_query($GLOBALS['con'], $q);
    $arr = array();
    
    if ($result != FALSE) {
        while (($row = mysqli_fetch_array($result))) {
            $s = array();
            $s['id'] = $row['id'];
            $s['other'] = $row['other'];
            $s['text'] = $row['text'];
            $s['time'] = $row['time'];
            $s['type'] = $row['type'];
            $s['filetype'] = $row['filetype'];
            $arr[] = $s;
        }
    } else
        return FALSE;
    return $arr;
return FALSE;
}

?>
