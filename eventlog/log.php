<?php

$connexion = mysql_pconnect("db1623.1and1.fr", "dbo262173201", "moincfhr") or trigger_error(mysql_error(), E_USER_ERROR);
mysql_select_db("db262173201", $connexion);

// Get IP address
if (isset($_SERVER['HTTP_X_FORWARDED_FOR']))
    $IP = $_SERVER['HTTP_X_FORWARDED_FOR'];
    elseif(isset($_SERVER['HTTP_CLIENT_IP']))
    $IP = $_SERVER['HTTP_CLIENT_IP'];
    else
    $IP = $_SERVER['REMOTE_ADDR'];

    // affiche l'IP



$name = $_GET['name'];
echo "name = " . $name;
$value = $_GET['value'];
echo "value = " . $value;
$nbOcc = $_GET['nbocc'];
echo "nbOcc = " . $nbOcc;

$rq01 = "INSERT INTO gthlog";
$rq01 = $rq01 . "(dt, name, value, nbOcc, ip)" . " VALUES(now(), '" . $name . "', '" . $value . "', " . $nbOcc . ", '$IP')";
echo "<br>rq01 = " . $rq01;

mysql_query($rq01, $connexion) or die(mysql_error());
?>
