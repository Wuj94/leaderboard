# Leaderboard App

A player's total score increment everytime he plays a game.
The leaderboard is made of the following fields:

 - User
 - Rank
 - Score

And the Score is **cumulative**.
Two APIs are needed, the public API and the administrator API.


## Public api (draft)

 1. #### Get my current score
    Request headerline: GET /public/players/{id}/score
    Response status code: 200 OK or 404 NOT FOUND
 2. #### Increment/Decrement my current score by delta points
    Request headerline: GET /public/players/{id}/score?increment=delta
    Response status code: 200 NO CONTENT or 404 NOT FOUND or 400 BAD REQUEST
 3. #### Get score and rank of the 10 players with higher rank and the 10 players with lower rank
     Request headerline: GET /public/players/{id}?slack=10
     Response status code: 200 OK or 404 NOT FOUND or 400 BAD REQUEST

## Private api (draft)

 1. #### Get score and rank for a user  
    Request headerline: GET /private/players/{id}/score
    Response status code: 200 OK (success) or 404 NOT FOUND (player not found)
 2. #### Increment/decrement score for a user
    Request headerline: GET /private/players/{id}/score?increment=delta
    Response status code: 200 OK or 404 NOT FOUND (player not found) or 400 BAD REQUEST (invalid query parameter) 
 3. #### Set absolute score for a user by delta points
     Request headerline: PUT /private/players/{id}/score
     Response status code: 204 NO CONTENT or 404 NOT FOUND or 400 BAD REQUEST
 4. #### Delete a user
     Request headerline: DELETE /private/players/{id}
     Response status code: 204 NO CONTENT or 404 NOT FOUND
 5. #### Get score and rank of a section of the leaderboard
     Request headerline: GET /admin/players?fromRank=low&toRank=high
     Response status code: 200 OK or 400 BAD REQUEST (invalid query parameters).

## Database design

To the extent of implementing a working proof-of-concept, following an iterative approach, the functional requirement 
Private Api.5 (Get score and rank of a section of the leaderboard) is investigated as deemed at highest risk.
The reason is the choice of the DB technology.

Reading the specification, we have the single domain entity Player and the access pattern is the following:

 1. Read score and rank of users inside the rank window given in input.

The data model doesn't include relationships between entities and a NoSQL DB technology is chosen,
given we'd like to gain availability giving in strict consistency in a partitioned/distributed landscape.

Note: There is no uniqueness constraint on the rank. We accept that two different users,
with the same score, share the same rank position.

Amazon DynamoDb is used as document/key-value DB technology.

The attribute we must to store for a player are: id, username, game, rank.
Players DynamoDb table is created with the following design:

**Partition Key**: id
**GSI1 Partition Key**: game
**GSI1 Sort Key**: rank
**Other attribute(s)**: username, score

The queries to satisfy the access pattern are:

 2. On GSI1: partition key = alwaysMe and sort key IS BETWEEN(low, high)

### DB Validation Design

#### Default dataset

Assuming the system runs the following default dataset:

|id  | game | rank | username | score |
|--|--|--|--|--|
| 3a4a1ed5-5785-480e-b9a4-72b45dace2d5 | alwaysMe | 1 | wuj | 22
| d3fbc880-b1a7-47f1-8de8-d51c8be9d1e0 | alwaysMe | 2 | bob | 18
| 9e441c44-e4cb-459d-ab98-c193dc2eff49 | alwaysMe | 3 | mic | 17

The DynamoDb query that let us satisfy the functional requirement is

***CLI command***

    aws dynamodb query \
    --table-name players \
    --index-name between_ranks_index \
    --key-condition-expression "game = :game AND pos BETWEEN :low AND :high" \
    --expression-attribute-values file:///Users/gcalabrese/values.json \
    --select ALL_PROJECTED_ATTRIBUTES \
    --return-consumed-capacity INDEXES \
    --endpoint=http://localhost:8000

**values.json**

    {
        ":game": { "S": "alwaysMe" },
        ":low": { "N": "1" },
        ":high": { "N": "2" }
    }
*Note*: The test was carrier out against a container running local-dynamo and exposing the port mapping 8000:8000.

### The PoC

To run the application, you DynamoDb running in background and mapping guest port 8000 to host port 8000.
You can easily spin up a Docker container for this:

    docker run -p 8000:8000 amazon/dynamodb-local

To run the actual PoC, execute the following bash command from the root folder of the project

    ./gradlew bootRun

The application is automatically loaded with the default dataset (as in previous section).
To check the actual successful bootstrap of the components, the following manual end-to-end(smoke) test can be 
ran from any web browser.

    http://localhost:8080/admin/players?fromRank=2&toRank=5

**Expected HTTP Response**

    {"scoreAndRanks":[{"score":18,"rank":2},{"score":17,"rank":3}]}

### Discussion

During the 2nd iteration, we decide to further validate the technologies chosen 
for the implementation of the app.

We assume that a user knows its identifier (It's generated by the system on creation and 
given back to the client as part of the HTTP response body).
The requirement Public API.3 can be implemented reusing (part of) the solution 
for the requirement Private API.5.

When the user issues a GET request providing its identifier and the slack at the endpoint
    
    GET /public/players/{id}?slack={slack}  

the server is able to translate the request to the logical equivalent of the following request

    GET /private/players?fromRank=userRankMinus10&toRank=userRankPlus10

Notice that the server is able to retrieve the user rank since it knows it's identifier {id}.
