<?php
    $apiKey = "91789073dc6e489988f2629602cfcae4";
    $ip = "77.128.107.78";
    $location = get_geolocation($apiKey, $ip);
    $decodedLocation = json_decode($location, true);
    
    echo "<pre>";
    print_r($decodedLocation);
    echo "</pre>";

    function get_geolocation($apiKey, $ip, $lang = "en", $fields = "*", $excludes = "") {
        $url = "https://api.ipgeolocation.io/ipgeo?apiKey=".$apiKey."&ip=".$ip."&lang=".$lang."&fields=".$fields."&excludes=".$excludes;
        $cURL = curl_init();

        curl_setopt($cURL, CURLOPT_URL, $url);
        curl_setopt($cURL, CURLOPT_HTTPGET, true);
        curl_setopt($cURL, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($cURL, CURLOPT_HTTPHEADER, array(
            'Content-Type: application/json',
            'Accept: application/json'
        ));
        return curl_exec($cURL);
    }
?>