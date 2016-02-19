<?php

/* 
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */

session_start();
include 'db.php';

if (isset($_SESSION['id'])) {
        DB_connect();
        $result = FALSE;
        if(isset($_REQUEST["makeNull"]))
            $result = DB_ping(FALSE);
        else    
            $result = DB_ping(TRUE);
        DB_close();
        if($result==TRUE)
        {
            exit();
        }
        
}
http_response_code(203);

?>