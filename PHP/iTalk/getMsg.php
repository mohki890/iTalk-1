<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
session_start();
include 'db_chat.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST' || $_SERVER['REQUEST_METHOD'] == 'GET')
    if (isset($_REQUEST['id'])) {
        $id = $_REQUEST['id'];
        if ($id == -1)
        {
             DB_connect();
                $result = DB_getLatestMsg();
            DB_close();

            if (!($result == FALSE)) {
                echo json_encode($result);
                exit();
            }
        }
        else
        if (isset($_REQUEST['last_msgid_min']) && isset($_REQUEST['last_msgid_max'])) {

            DB_connect();
                $result = DB_getMsg($id, $_REQUEST['last_msgid_min'], $_REQUEST['last_msgid_max']);
            DB_close();

            if (!($result == FALSE)) {
                echo json_encode($result);
                exit();
            }
        }
    }
http_response_code(203);
?>

