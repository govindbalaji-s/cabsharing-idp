<?php
    include 'constants.php';

    set_headers();

    //The below statement decodes the json object from the received POST request
    /*json can contain keys : from - string stored in lowercase in DB,
                              to   - string stored in lowercase in DB,
                              rollno - string | roll number stored in lowercase in DB
                              starttime - datetime denotes the lowerbound time of start of ride
                              waittill(waittill) - datetime denotes the upperbound time of start of ride
                              remarks - string
     */
    $post_json = json_decode(file_get_contents('php://input'), true);
   // echo file_get_contents('php://input');
    //This success is set false if the catch block is reached, symbolising a database error.
    $success = true;
    $message = '';
    try{
      
      //Open database connection
      $db_connection = get_db_connection();

      //Parse query data from the JSON
      $query_from = strtolower($post_json['from']);
      $query_to = strtolower($post_json['to']);
      $query_creator = strtolower($post_json['rollno']);
      $query_starttime= $post_json["starttime"];
      $query_waittill = $post_json["waittill"];
      $query_remarks = $post_json["remarks"];
      
      //For each user, we are storing their rollno, starttime and waittill. Serialize into a string in order to save in database
      $array_creator = ['rollno' => $query_creator,
                        'starttime' => $query_starttime,
                        'waittill' => $query_waittill];
      $array_people = serialize(array($array_creator));
      //echo $array_creator;

      //Prepare and execute SQL
      $statement = $db_connection->prepare("INSERT INTO `rides` (`from`, `to`, `people`, `starttime`, `waittill`, `remarks`) VALUES (?, ?, ?, ?, ?, ?);");
      $statement->execute(array($query_from, $query_to, $array_people, $query_starttime, $query_waittill, $query_remarks));
    }catch(PDOException $e){
      $success = false;
      $message = $e->getMessage();
    }

    //Check if the INSERT was successful and respond accordingly
    echo_success($success, $message);
    exit();
  ?>
