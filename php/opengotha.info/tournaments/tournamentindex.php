<?php      
$dirName = getcwd();  
$tournamentName = substr(strrchr($dirName, "/"), 1);
header('Location:../htmlfileslist.php?tournament=' . $tournamentName);      
?>