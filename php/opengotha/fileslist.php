<?php
// fileslist.php scans the dir specified by strURL and echoes the file names
// separated by "\r\n"
// Only file names beginning by '2' are returned
// LV 14/01/2019
$dirName = $_GET['dirName'];
$dir = opendir($dirName);

while($file = readdir($dir)) {
    $firstChar = substr($file, 0, 1);
    if($firstChar == '2')
    {
        echo "\n".$file;
    }
}
closedir($dir);
?>

