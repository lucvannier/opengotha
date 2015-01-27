<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>HTML files list</title>
    <link href="current.css" rel="stylesheet" type="text/css"></head>
<body>
    <h1 align="center">HTML files list </h1>

<?php
    $newLine = "\r\n";
    $dirName = $_GET['tournament'];
    echo '<h1 align="center">' . $dirName . '</h1>';
    
    $dir = @opendir('./' . $dirName); 

    function displayFiles($strType){
        
        $newLine = "\r\n";
        $listTitle = "";
        switch($strType){
            case "PlayersList"      : $listTitle = "Players list"; break;
            case "TeamsList"        : $listTitle = "Teams list";  break;
            case "GamesList"        : $listTitle = "Games list"; break;
            case "MatchesList"      : $listTitle = "Matches list"; break;
            case "Standings"        : $listTitle = "Standings "; break;
            case "TeamsStandings"   : $listTitle = "Teams standings"; break;
        }
        $nbFL = 0;
        $dirName = $_GET['tournament'];       
        $dir = opendir('./' . $dirName); 

        while($file = readdir($dir)) {
            if($file == '.' || $file == '..' || end(explode('.', $file)) != 'html') continue;
            $fn = strstr($file, '_' . $strType );
            if ($fn == "") continue;
            $files[$nbFL] = $file;
            $nbFL++;
        }
                   
        for ($n = 0; $n < $nbFL - 1; $n++){
            for ($i = 0;  $i < $nbFL - 1; $i++){
                $file1 = $files[$i];
                $fn1 = strstr($file1, '_' . $strType );
                $posR1 = strpos($fn1, "R");
                $posPoint1 = strpos($fn1, ".");
                $rn1 = substr($fn1, $posR1 + 1, $posPoint1 - $posR1 - 1);
                $r1 = intval($rn1);
                $file2 = $files[$i + 1];
                $fn2 = strstr($file2, '_' . $strType );
                $posR2 = strpos($fn2, "R");
                $posPoint2 = strpos($fn2, ".");
                $rn2 = substr($fn2, $posR2 + 1, $posPoint2 - $posR2 - 1);
                $r2 = intval($rn2);
                if ($r1 > $r2){
                    $files[$i + 1] = $file1;
                    $files[$i] = $file2;
                }
            
            }
        }
        
        for($i = 0; $i < $nbFL; $i++){
            $file = $files[$i];
            $fn = strstr($file, '_' . $strType );
            $col0 = "";
            if ($i == 0) $col0 = $listTitle;

            $posR = strpos($fn, "R");
            $posPoint = strpos($fn, ".");
            $rn = substr($fn, $posR + 1, $posPoint - $posR - 1);
            $col1 = $rn;

            $colDT = date ("Y-m-d H:i:s", filemtime($dirName . '/' . $file));

            $strPar = 'pair';
            if ($i % 2 == 0 ) $strPar = 'impair';

            echo $newLine .'<tr>';
            echo $newLine .'<td class=' . $strPar . ' align=middle>' . $col0 . '&nbsp;</a></td>';
            echo $newLine .'<td class=' . $strPar . ' align=middle>' . $col1 . '&nbsp;</a></td>';   
            echo $newLine .'<td class=' . $strPar . ' align=middle>' . '<a href="./' . $dirName . '/' . $file . '"> ' . $file . '&nbsp;</a></td>';
            echo $newLine .'<td class=' . $strPar . ' align=middle>' . $colDT . '&nbsp;</a></td>';
            echo $newLine .'</tr>'; 
        }
        if ($nbFL > 0)  echo $newLine .'<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>';
    }
?>
    <table align="center" class="simple">
        <th class="middle"></th>
        <th class="middle">Round</th>
        <th class="middle">HTML file</th>
        <th class="middle">Last modified</th>

<?php
    if ($dir == FALSE){
        echo '</table>';
        echo '<h1 align="center">The tournament does not exist</h1>';
     }
    else{
        displayFiles("PlayersList");
        displayFiles("TeamsList");
        displayFiles("GamesList");
        displayFiles("MatchesList");
        displayFiles("Standings");
        displayFiles("TeamsStandings");
    
        echo '</table>';
        
        closedir($dir);
    }
      
    echo $newLine . '<br><br><br><h1 align="center">Go to <a href = "http://opengotha.info">OpenGotha site main page</a></h1>';
    
?>
</body>
</html>