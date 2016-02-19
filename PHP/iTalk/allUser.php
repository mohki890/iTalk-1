<?php

/* 
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
include 'myDB.php';
DB_connect();
$var=DB_allUser();
DB_close();
echo $var;
?>


