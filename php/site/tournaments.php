<?php
require_once('og_xmlrpc.inc');
$serverName = $_GET["servername"];

$server = new xmlrpc_client('/', $serverName, 8099);
$message = new xmlrpcmsg('ogxmlrpcserver.tournamentsList');

$result = $server->send($message);
$value = $result->value();

if (!$result) {
    print "No tournament found";
} elseif ($result->faultcode()) {
    print "No tournament found";
} else {
    $nbElements = $value->arraysize();
    if ($nbElements == 0){
        print "No tournament on this server";
    }
    else{
        print "<SELECT name='tournamentsSelect' align='center' onchange='update()'>";
        for ($i = 0; $i < $nbElements; $i++) {
            $v = $value->arraymem($i)->scalarVal();
            print "<OPTION>" . $v . "</OPTION>";
        }
        print "</SELECT>";
    }
}
?>
