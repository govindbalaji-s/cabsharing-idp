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

    //Check if such a trip exists
    $statement = $db_connection->prepare("SELECT * FROM `rides` WHERE `sno` = ?;");
    $statement->execute(array($query_sno));

    if($statement->rowCount() == 0){
      //No such trip exists
      $success = false;
      $message = "No such trip exists.";
    }
    else{
      $db_row = $statement->fetch(PDO::FETCH_ASSOC); 

      //Check if the new times dont further constrain the existing times
      if((new DateTime($query_starttime) > new DateTime($db_row['starttime']) )|| (new DateTime($query_waittill) < new DateTime($db_row['waittill']))){
        $success = false;
        $message = "Edit binds other people.";
      }
      else{
        //Search through $array_people till I reach the desired user
        $array_people = unserialize($db_row['people']);
        $no_of_people = count($array_people);
        $index = 0;
        for($index = 0; $index < $no_of_people; $index++){
          if(strcmp($array_people[$index]["rollno"], $query_rollno) == 0){
            break;
          }
        }
        if($index == $no_of_people){
          //This means the person is not there
          $success = false;
          $message = "User not part of the ride.";
        }
        else{
          $array_people[$index]['starttime'] = $query_starttime;
          $array_people[$index]['waittill'] = $query_waittill;

          //Update max starttime and min waittill
          $max_starttime = new DateTime($array_people[0]['starttime']);
          $min_waittill = new DateTime($array_people[0]['waittill']);
          for($i = 1; $i < $no_of_people - 1; $i++){
            $max_starttime = max($max_starttime, new DateTime($array_people[$i]['starttime']));
            $min_waittill = min($min_waittill, new DateTime($array_people[$i]['waittill']));
          }
          $max_starttime = $max_starttime->format($datetime_format);
          $min_waittill = $min_waittill->format($datetime_format);

          $array_people = serialize($array_people);

          $statement = $db_connection->prepare("UPDATE `rides` SET `people` = ?, `starttime` = ?, `waittill` = ? WHERE `sno` = ?;");
          $statement->execute(array($array_people, $max_starttime, $min_waittill, $query_sno));
        }
      }
    }
  }catch(PDOException $e){
    $success = false;
    $message = $e->getMessage();
  }
  //Check if the UPDATE was successful and respond accordingly
  echo_success($success,$message);
  exit();
?>