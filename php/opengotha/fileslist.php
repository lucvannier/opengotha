<?php
// fileslist.php scans the dir specified by strURL and echoes the file names
// separated by "\r\n"
// Only file names beginning by '2' are returned
// LV 14/01/2019
$dirName = $_GET['dirName'];
// $dir = opendir($dirName);
opendir($dirName);

while($file = readdir()) {
    echo $file . "\r\n";
}
closedir($dir);
?>

