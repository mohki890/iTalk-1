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
   	if(isset($_REQUEST['id']))
   	{
   		if(DB_checkOnline($_REQUEST['id'])==TRUE) exit();
   	}
   	else
    {
    	$map=DB_showOnline();
    	echo $map;
    }
    DB_close();
}
else
{
    http_response_code(203);
}
?>


