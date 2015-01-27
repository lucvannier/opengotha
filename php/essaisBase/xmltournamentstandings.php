
<?php
    require_once('og_xmlrpc.inc');
    $servername = $_GET['servername'];
    $tournamentname = $_GET['tournamentname'];
    $stylesheet = $_GET['stylesheet'];
    alert("tournaments.php $ servername = " + $servername);
    $xml_result = og_xml_tournament_standings($servername, $tournamentname, $stylesheet);
//    alert("apres appel");
        
    print htmlentities($xml_result);
    $filename = $tournamentname . "_ts.xml";
    $fp = fopen($filename, "w");
    fputs($fp, $xml_result);

//    header('Location: ' . $filename);
    

?>
