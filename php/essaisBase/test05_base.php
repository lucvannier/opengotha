
<?php
    require_once('xmlrpc.inc');
    $server = new xmlrpc_client('/', 'localhost', 8099);
    $message = new xmlrpcmsg('ogxmlrpcserver.test05');

    $result = $server->send($message);
    $value = $result->value();

    print htmlentities($value->scalarval());
?>
