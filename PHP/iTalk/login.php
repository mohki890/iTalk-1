<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();
include 'db.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST' || $_SERVER['REQUEST_METHOD'] == 'GET')
 {
    if (isset($_REQUEST['user']) && isset($_REQUEST['pwd'])) {
    session_unset();
        DB_connect();
        $result=DB_checkLogin($_REQUEST['user'], $_REQUEST['pwd']);
        DB_close();
          
       if($result==1)
            {
            http_response_code(200);
            exit();
        }
        else if($result==0)
        {
            http_response_code(204);
            exit();
        }
    }
       
}
http_response_code(203);   //Change It
?>
