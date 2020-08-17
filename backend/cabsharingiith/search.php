<?php
   //Include the database credentials
    include 'constants.php';

    set_headers();   

		//The below statement decodes the json object from the received POST request
		/*json can contain keys : from - string stored in lowercase in DB,
                              to   - string stored in lowercase in DB,
                              starttime - datetime denotes the lowerbound time of start of ride
                              waittill(waittill) - datetime denotes the upperbound time of start of ride
     */
		$post_json = json_decode(file_get_contents('php://input'), true);

        $post_json = $post_json[0];
		
		//We need this to be a global variable so that we can access them even after db_connection is closed.
		$query_result = NULL;
		try{
			
      //Open database connection
		  $db_connection = get_db_connection();

			//The from and to fields are made all lowercase so that 'IITH' and 'iith' are not considered different
			$query_from = strtolower($post_json['from']);
			$query_to = strtolower($post_json['to']);
      $query_starttime= $post_json['starttime'];
      $query_waittill = $post_json['waittill'];
			/* ******************* Time matching ********************************
				Let a database row have starttime as x and waittill as y
				Let the search query have starttime as u and starttime end as v
				A row can be selected if and only if following is FALSE:  y<u or v < x
				ie if and only if not (y<u or v < x) is true
			*/	

      //Prepare and execute SQL and get the result		
			$statement = $db_connection->prepare("SELECT * FROM rides WHERE `from` = ? AND `to` = ? AND NOT (`waittill` < ? OR `starttime` > ?);");
			$statement->execute(array($query_from, $query_to, $query_starttime, $query_waittill));
      $query_result = $statement->fetchAll(PDO::FETCH_ASSOC);
      foreach($query_result as $i => $row){
        $query_result[$i]['people'] = unserialize($query_result[$i]['people']);
      }

		}catch(PDOException $ex){
			echo $ex->getMessage();
		}


		//Prepare a json object for responding to the POST request
    $json_output = json_encode($query_result); 
    //Output the json object
    echo $json_output;
	?>
