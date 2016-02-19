<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */
$AutUser = "iTalk";
$AuthPwd = "just@dduser";
include 'db.php';


if ($_SERVER['REQUEST_METHOD'] == "POST") {
    
   
    if (isset($_POST['Auser']) && isset($_POST['Apwd']))
    if ($_POST['Auser'] == $AutUser && $_POST['Apwd'] == $AuthPwd)
    {
        if (isset($_POST['multiUser']))
        {
            $userlist=split(",", $_POST['multiUser']);
            
            foreach ($userlist as $key => $value) {
                $usr=  trim($value);
                if(strlen($usr)>$maxLengthUser)
                {
                    http_response_code(501);    //Not Accepeted
                    echo "Max 64 Character";
                    exit();
                }
            }
            $count=0;
            $is_error=FALSE;
            DB_connect();
            foreach ($userlist as $key => $value)
            { 
               $usr=  trim($value);
               if(!DB_addUser($usr,$usr,$usr))
                   $is_error=TRUE;
               else $count++;
            }
            DB_close();
            echo $count." Users Added!";
            if($is_error)
                http_response_code(202);     //Check It
            else
                http_response_code(200);
            exit();
             
            
        }
        /*
        if (isset($_POST['user']) && isset($_POST['pwd']))
        {
            
                if ($_POST['user'] == "")
                    if(addUser($_POST['user'], $_POST['user']))
                    {
                        http_response_code(200);
                        echo "Added";
                        exit();
                    }
                else
                    if(addUser($_POST['user'], $_POST['pwd']))
                    {
                        http_response_code(200);
                        echo "Added";
                        exit();
                    }
                
            }
            else
            {
                http_response_code(203);   //Change It Invalid Auth
                echo "Invalid Id";
                exit();
            }
        */
    }
   
}
 http_response_code(400);
?>
