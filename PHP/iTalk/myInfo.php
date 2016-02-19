<?php

/* 
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();
include 'myDB.php';
if(isset($_SESSION['id']))
{
    DB_connect();
    $map=DB_myInfo();
    echo $map;
    DB_close();
}
else
{
    http_response_code(203);
}
?>


