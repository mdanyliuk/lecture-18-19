Create file `.env` and put email settings to it (look for example at `.env.example` file) <br>
Run <br>
`./mvnw clean package` <br>
`docker-compose up` <br>
<br>
### Endpoint
POST `http://localhost:8080/email` <br>
example of correct request body: <br>
`{
"subject": "subject",
"to": "some.mail@gmail.com",
"body": "Hello!"
}` <br>