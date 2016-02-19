<?php

/*
 * Project	: iTalk
 * 		talking is life ....
 * 	(By gagan1kumar)
 */

session_start();

if (isset($_SESSION['id']) && is_numeric($_SESSION['id']) && isset($_REQUEST["image"]) && isset($_REQUEST["thumb"])) {

    if (sizeof($_REQUEST["thumb"]) <= 1024 * 50 && sizeof($_REQUEST["image"]) <= 1024 * 1024 * 5) {     //50 KB & 5MB
        if (!file_put_contents("t_" . $_SESSION['id'], $_REQUEST["thumb"])) {
            http_response_code(203);
            exit();
        }
        if (file_put_contents($_SESSION['id'], $_REQUEST["image"])) {
            exit();
        }
    }
};
http_response_code(205);

/*
  $allowedExts = array("gif", "jpeg", "jpg", "png");
  $temp = explode(".", $_FILES["file"]["name"]);
  $extension = end($temp);
  if ((($_FILES["file"]["type"] == "image/gif")
  || ($_FILES["file"]["type"] == "image/jpeg")
  || ($_FILES["file"]["type"] == "image/jpg")
  || ($_FILES["file"]["type"] == "image/pjpeg")
  || ($_FILES["file"]["type"] == "image/x-png")
  || ($_FILES["file"]["type"] == "image/png"))
  && ($_FILES["file"]["size"] < 1024*1024*10)	//10MB
  && in_array($extension, $allowedExts))
  {
  if ($_FILES["file"]["error"] > 0)
  {
  http_response_code(203);
  exit();
  }
  else
  {
  if(move_uploaded_file($_FILES["file"]["tmp_name"],
  $_SESSION['id']))
  {
  exit();
  }

  http_response_code(206);
  exit();

  }
  }
  else
  {
  http_response_code(208);
  exit();

  }
  }
  http_response_code(205);
  exit();
 */
?>
