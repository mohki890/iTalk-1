<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
include 'DB_connection.php';
/*
 *  type    = 0 ;Send
 *  type    = 1 ;Received
 */


/* * **** Insted of Null Try 
 * 
 * mysqli_num_rows
 */
DB_connect();
    
$file=file_get_contents("Ticket.pdf");
   $fileType=1;
    $my_id=39;
    $other_id=41;
    $fileName = "Ticket.pdf";
    $id_mytable = 7;
    $id_othertable = 7;

    $q = "INSERT INTO `chat_" . $my_id . "` (`id`,`type`,`file`, `other`,`filetype`,`text`) VALUES ('" . $id_mytable . "',0,'" . base64_encode($file) . "', '" . $other_id . "'," . $fileType . ",'" . $fileName . "');";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE);
        
    $q = "INSERT INTO `chat_" . $other_id . "` (`id`,`type`,`file`, `other`,`filetype`,`text`) VALUES ('" . $id_othertable . "',1,'" . base64_encode($file) . "', '" . $my_id . "'," . $fileType . ",'" . $fileName . "');";
    if (($result = mysqli_query($GLOBALS['con'], $q)) == FALSE);
DB_close();
echo "DONE";
?>
