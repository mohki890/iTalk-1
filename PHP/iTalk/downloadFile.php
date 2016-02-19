<?php

/* 
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();
include 'db_chat.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST' || $_SERVER['REQUEST_METHOD'] == 'GET')
if(isset($_REQUEST['id']) && isset($_REQUEST['fileid']))
{
    $id=$_REQUEST['id'];
    
    DB_connect();
    $result=  DB_downloadFile($id,$_REQUEST['fileid']);
    DB_close();
    
    //if(!($result==FALSE))
    {
	http_response_code(200+$result);
        exit();
    }
    
 }
http_response_code(205);
?>

