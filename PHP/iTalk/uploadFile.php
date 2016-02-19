<?php

/* 
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();
include 'db_chat.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST' || $_SERVER['REQUEST_METHOD'] == 'GET')
if(isset($_REQUEST['file']) && isset($_REQUEST['text']) && isset($_REQUEST['filetype']) && isset($_REQUEST['id']))
{
    $id=$_REQUEST['id'];
    
    DB_connect();
    $result=DB_uploadFile($id,$_REQUEST['file'],$_REQUEST['filetype'],$_REQUEST['text']);
    DB_close();
    
    //if($result==0)
    {
        http_response_code(200+$result);
        exit();
    }

    
 }
http_response_code(203);
?>

