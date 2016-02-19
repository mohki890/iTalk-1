<?php
/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
include 'DB_connection.php';

function DB_myInfo() {
    $arr = array();
    $q = "SELECT id,username,nickname FROM USERINFO WHERE id=" . $_SESSION['id'] .";";
    
    $rslt = mysqli_query($GLOBALS['con'], $q);
    if ($rslt != FALSE) {
        while (($row = mysqli_fetch_array($rslt))) {
            $arr['id'] = $row['id'];
            $arr['uname'] = $row['username'];
            $arr['nname'] = $row['nickname'];
        }
    }
    return json_encode($arr);
     
}


function DB_allUser() {
    $main=array();
    
    $q = "SELECT id,username,nickname FROM USERINFO;";
    $rslt = mysqli_query($GLOBALS['con'], $q);
    if ($rslt != FALSE) {
        while (($row = mysqli_fetch_array($rslt))) {
            $arr = array();
            $arr['id'] = $row['id'];
            $arr['uname'] = $row['username'];
            $arr['nname'] = $row['nickname'];
            $main[]=$arr;
            
        }
    }

    return json_encode($main);
}
function DB_showOnline() {
    $main=array();
    $ONLINEGAP_SEC=60;
    $q = "SELECT id FROM USERINFO WHERE now()-lastlogin<=" . $ONLINEGAP_SEC . ";";
    $rslt = mysqli_query($GLOBALS['con'], $q);
    if ($rslt != FALSE) {
        while (($row = mysqli_fetch_array($rslt))) {
            $main[]=$row['id'];
        }
    }

    return json_encode($main);
    
}
function DB_checkOnline($id)
{
    if(!is_numeric($id))        return FALSE;
    $q = "SELECT now(),lastlogin FROM USERINFO WHERE id=" . $id . " and lastlogin IS NOT NULL;";
    $rslt = mysqli_query($GLOBALS['con'], $q);
    if ($rslt != FALSE) {
        if (mysqli_num_rows($rslt) > 0) {
            $row = mysqli_fetch_array($rslt);
            echo json_encode(array($row['now()'],$row['lastlogin']));
        }
        else        http_response_code(201);
 
        return TRUE;
    }
    
    return FALSE;
}


?>