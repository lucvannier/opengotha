<?php
// createdir.php creates the directory specified by dirName
// LV 15/01/2019
$dirname = $_GET['dirname'];
echo "createdir.php\n";
echo "dirname = $dirname";
if (mkdir("./tournaments/$dirname")) echo "Directory created";
else echo "Directory not created";
?>
