<?php

$con = NULL;

function DB_connect() {
    $DB_user = "server";
    $DB_pwd = "LQ8VDywFctRyzNcY";
    $DB_host = "localhost";
    $DB_database = "iTalk";
    $GLOBALS['con'] = mysqli_connect($DB_host, $DB_user, $DB_pwd, $DB_database);
    if (mysqli_connect_errno()) {
        echo "Cannot Connect To Database";
        http_response_code(503);
        exit();
    }
    return TRUE;
}

function DB_close() {
    if ($GLOBALS['con'] != NULL) {
        mysqli_close($GLOBALS['con']);
    }
}

?>
