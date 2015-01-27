<script type="text/javascript"  language='javascript'>
    var serverName = "";
    var tournamentName = "";
    var functionName = "Tournament Standings";

    function update(){
/*        var newServerName = document.getElementsByName("serversSelect").item(0).value;
        if (newServerName != serverName){
            serverName = newServerName;
            var tournamentsDiv =document.getElementsByName("tournamentsDiv").item(0);
            tournamentsDiv.innerHTML = "Questionning server ...<br>Please wait !";
            var requeteAJAX = new XMLHttpRequest();
            var url = 'tournaments.php?servername=' + serverName;
            requeteAJAX.open('GET', url, false);
            requeteAJAX.send(null);
            tournamentsDiv.innerHTML = requeteAJAX.responseText;
        }
        var newTournamentName = document.getElementsByName("tournamentsSelect").item(0).value;
        tournamentName = newTournamentName;
        var newFunctionName = document.getElementsByName("functionsSelect").item(0).value;
        functionName = newFunctionName;

        displayIframe = document.getElementsByName("displayIframe").item(0);
        displayIframe.src = "/og/" + "transition.html";

        var requeteAJAX = new XMLHttpRequest();
        var url = 'xmltournamentstandings.php?servername=' + serverName
                    + '&tournamentname=' + tournamentName
                    + '&stylesheet=TournamentStandings.xsl';
        requeteAJAX.open('GET', url, false);
        requeteAJAX.send(null);

        displayIframe.src = "/og/" + tournamentName + "_ts.xml";
  */
        displayIframe = document.getElementsByName("displayIframe").item(0);
        displayIframe.src = "/og/maquette.html";
  }
</script>

<?php
require_once('og_xmlrpc.inc');
?>

<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    </head>
    <body>
        <div style="position:absolute; left:190px; top:0px; width:700px">
        <h2 align="center">Remote access to OpenGotha Servers</h2>
        </div>

        <div style="width:170px; height:180px; position:absolute; left:10px; top:50px; background:yellow">
            <h4 align='center'>Servers</h4>
            <div align='center'>
                <SELECT name='serversSelect' onchange='update()'>
                <OPTION>localhost</OPTION>
                <OPTION>82.226.203.232</OPTION>
                </SELECT>
                <br><br><br>
                <button onclick="update()">Update</button>
            </div>
        </div>

        <div style="width:170px; height:180px; position:absolute; left:10px; top:240px; background:lightgreen">
            <h4 align='center'>Tournaments</h4>
            <div name='tournamentsDiv' align='center'>
                Tournaments list
            </div>
        </div>

        <div style="width:170px; height:180px; position:absolute; left:10px; top:430px; background:lightsalmon">
            <h4 align='center'>Functions</h4>       
            <div align='center'>       
               <SELECT name='functionsSelect' onchange='serverSelect(this.value)'>
               <OPTION>Tournament Standings</OPTION>
               </SELECT>       
           </div>
        </div>

        <iframe name="displayIframe" style="width:1100px; height:560px; position:absolute; left:190px; top:50px; background:lightcyan">
            <h1> Display iframe</h1>
        </iframe>

    </body>
</html>

<script type='text/javascript'>
    update();
</script>