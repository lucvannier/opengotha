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



$logtext = $_GET['logtext'];
echo "logtext = " . $logtext;
$rq01 = "INSERT INTO gthlog";
// $rq01 = $rq01 . "(dt, logtext)" . " VALUES(now(), '" . $logtext . "')";
$rq01 = $rq01 . "(dt, logtext, ip)" . " VALUES(now(), '$logtext', '$IP')";
echo "<br>rq01 = " . $rq01;

mysql_query($rq01, $connexion) or die(mysql_error());
?>
