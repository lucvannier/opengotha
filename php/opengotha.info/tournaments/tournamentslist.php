<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Tournaments list</title>
    <link href="current.css" rel="stylesheet" type="text/css"></head>
<body>
    <h1 align="center">Tournaments list</h1>
    <table align="center" class="simple">
        <th class="left">Begin date</th>
        <th class="middle">Name</th>

<?php
$dirName = '.';
$dir = opendir($dirName); 
$nbTournaments = 0;
$newLine = "\r\n";

while($file = readdir($dir)) {
    if($file != '.' && $file != '..' && is_dir($file))
    {
        $nbTournaments++;
        $tournamentDir = $file;
        $beginDate = substr($tournamentDir, 0, 8);
        $strBeginDate = substr($beginDate, 6, 2).'/'.substr($beginDate, 4, 2).'/'.substr($beginDate, 0, 4);
        $link = 'htmlfileslist.php?tournament='.$tournamentDir;
        
        $shortName = substr($tournamentDir, 8);
        
        echo $newLine .'<tr>';
        $strPar = 'pair';
        if ($nbTournaments % 2 == 1 ) $strPar = 'impair';
        
        echo $newLine . '<td class=' . $strPar . ' align=left>&nbsp;' . $strBeginDate. '&nbsp; </td>';
        echo $newLine .'<td class=' . $strPar . ' align=middle>' . '<a href="' . $link . '"> &nbsp;' .$shortName. '&nbsp;</a></td>';
        echo $newLine .'</tr>';
    }
}
echo '</table>';
    
echo $newLine . '<br><br><br><h1 align="center">Go to <a href = "http://opengotha.info">OpenGotha site main page</a></h1>';

if ($nbTournaments == 0) echo '<br><p align=center> No tournament</p>';
closedir($dir);
?>
</body>
</html>