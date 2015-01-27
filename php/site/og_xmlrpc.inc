<?php
// by Luc Vannier (C) 2011
// <luc.vannier@gmail.com>
// $Id: og_xmlrpc.inc,v 1.01 $

// Copyright (c) 2011 Luc Vannier.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//
//    * Redistributions in binary form must reproduce the above
//      copyright notice.
//
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
// STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
// OF THE POSSIBILITY OF SUCH DAMAGE.

require_once('xmlrpc.inc');

function og_xml_tournament_standings($server_name, $tournament_name, $stylesheet=""){
    $server = new xmlrpc_client('/', $server_name, 8099);
    $message = new xmlrpcmsg('ogxmlrpcserver.xmlTournamentStandings',
            array(new xmlrpcval($tournament_name, "string"),
                  new xmlrpcval($stylesheet, "string")));
    $result = $server->send($message);
    $value = $result->value();
    $ret_value = $value->scalarval();
    if (!$result){
        print "<p>Could not connect to HTTP server.</p>";
        $ret_value = "Error";
    } elseif ($result->faultcode()){
        print "<p>XML-RPC Fault #" . $result->faultCode() . ": " .
            $result->faultString();
        $ret_value = "Error";
    }
    return $ret_value;
}

?>
