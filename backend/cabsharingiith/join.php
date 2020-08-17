<?php
  include 'constants.php';

  set_headers();

  //Decode the json object from POST data.
  /*JSON object must contain:
          sno - sno of the row in rides database
          rollno - roll number of the user who wants to join the trip
          starttime - the preferred starttime of the user
          waittill - the preferred waittill of the user
  */
  $post_json = json_decode(file_get_contents('php://input'), true);

  //This success is set false if the catch block is reached, symbolising a database error.
  $success = true;
  $message = '';
  try{
    
    //Open database connection
    $db_connection = get_db_connection();


    //Get query data from json
    $query_sno = $post_json['sno'];
    $query_rollno = $post_json['rollno'];
    $query_starttime = $post_json['starttime'];
    $query_waittill = $post_json['waittill'];

    //Prepare the user array
    $array_user = ["rollno" => $query_rollno,
                    "starttime" => $query_starttime,
                    "waittill" => $query_waittill];

    //First Check if such a trip exists
    $statement = $db_connection->prepare("SELECT * FROM `rides` WHERE `sno` = ?;");
    $statement->execute(array($query_sno));

    if($statement->rowCount() == 0){
      //No such trip exists
      $success = false;
      $message = 'No such trip exists.';
    }
    else{
      $db_row = $statement->fetch(PDO::FETCH_ASSOC); 


      //First append the array 'people' with $array_user
      $array_people = unserialize($db_row['people']);
      if($array_people)
        array_push($array_people, $array_user);
      else
        $array_people = array($array_user);
      
      $db_row['people'] = serialize($array_people);
      

      //New starttime is the maximum of existing and the new guy.
      //And new waittill is the minimum
      $qsdt = new DateTime($query_starttime);
      $dsdt = new DateTime($db_row['starttime']);
      $db_row['starttime'] = max($qsdt, $dsdt)->format($datetime_format);
      $db_row['waittill'] = min(new DateTime($query_waittill), new DateTime($db_row['waittill']))->format($datetime_format);

      //Update the ride details in the database
      $statement = $db_connection->prepare("UPDATE `rides` SET `people` = ?, `starttime` = ?, `waittill` = ? WHERE `sno` = ?;");
      $statement->execute(array($db_row['people'], $db_row['starttime'], $db_row['waittill'], $query_sno));

    }
  }catch(PDOException $e){
    $success = false;
    $message = $e->getMessage();
  }
  //Check if the UPDATE was successful and respond accordingly
  echo_success($success, $message);
  exit();
?>