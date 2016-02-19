<?php

/* 
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();
include 'db_chat.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST' || $_SERVER['REQUEST_METHOD'] == 'GET')
if(isset($_REQUEST['msg']) && isset($_REQUEST['id']))
{
    $msg=$_REQUEST['msg'];
    $id=$_REQUEST['id'];
    
    DB_connect();
    $result=DB_sendMsg($id,$msg);
    DB_close();
    
    if($result == 0)
    {
        http_response_code(200);
        exit();
    }
 }
http_response_code(203);
?>

