<html>

<body>
<?php
    print "test05.php";
    require_once('xmlrpc.inc');
    $server = new xmlrpc_client('/', 'localhost', 8099);
    $message = new xmlrpcmsg('ogxmlrpcserver.test05');
            
    $result = $server->send($message);
    $value = $result->value();
    if (!$result){
        print "<p>Could not connect to HTTP server.</p>";
    } elseif ($result->faultcode()){
        print "<p>XML-RPC Fault #" . $result->faultCode() . ": " .
            $result->faultString();
    } else{
        print "<BR>Answer = " . htmlentities($value->scalarval()) . "<BR>";
        print "<HR>I got this value back<BR><PRE>" .
        htmlentities($result->serialize()) . "</PRE><HR>\n";
    }

?>
</body>

</html>