<html>

<body>
<?php
    print "numberofplayers.php";
    require_once('xmlrpc.inc');
    $server = new xmlrpc_client('/', 'localhost', 8099);
    $tournamentName = $_GET["tournamentname"];
    $message = new xmlrpcmsg('ogxmlrpcserver.numberOfPlayers',
            array(new xmlrpcval($tournamentName, "string")));
    $result = $server->send($message);
    $value = $result->value();
    if (!$result){
        print "<p>Could not connect to HTTP server.</p>";
    } elseif ($result->faultcode()){
        print "<p>XML-RPC Fault #" . $result->faultCode() . ": " .
            $result->faultString();
    } else{
        print "<HR>";
        $nbPlayers = $value->scalarval();
        print "<BR>In " . $tournamentName . ", " . $nbPlayers . " players are registered";

        print "<HR>I got this value back<BR><PRE>" .
        htmlentities($result->serialize()) . "</PRE><HR>\n";
    }

?>
</body>