<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();

if (isset($_SESSION['id']) && isset($_SESSION['username']) && isset($_SESSION['nickname'])) {
    if (is_numeric($_SESSION['id']) && $_SESSION['username'] != "") {
        http_response_code(200);
        exit();
    }
}
http_response_code(203);    //Check It
?>
