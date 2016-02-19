<?php

/* 
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */

include 'db.php';

if (isset($_REQUEST['id'])) {
        DB_connect();
        $result = FALSE;
        $result = DB_lastLogin($_REQUEST['id']);
        DB_close();
        if($result == TRUE)
        {
            exit();
        }
        else
            echo "Error!";
}
http_response_code(203);


?>