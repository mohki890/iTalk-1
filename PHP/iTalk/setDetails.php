<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();
include 'db.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST' || $_SERVER['REQUEST_METHOD'] == 'GET') {
    if (isset($_REQUEST['nname']) && isset($_SESSION['id'])) {

        DB_connect();
        $result = DB_changeNName($_REQUEST['nname']);
        DB_close();
        
        if ($result) {
            http_response_code(200);
            exit();
        }
    }
    if (isset($_REQUEST['pwd']) && isset($_REQUEST['oldpwd']) && isset($_SESSION['id'])) {

        DB_connect();
        $result = DB_changePwd($_REQUEST['oldpwd'],$_REQUEST['pwd']);
        DB_close();

        if ($result) {
            http_response_code(200);
            exit();
        }
    }
}
http_response_code(203);
?>

