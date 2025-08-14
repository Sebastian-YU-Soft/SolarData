MaxxEnergy EDAP API
This is a simple Spring Boot application that provides a public REST API for retrieving solar energy data from a MongoDB database.

🚀 Getting Started
To get this application up and running on your local machine, follow these steps.

Prerequisites
Java Development Kit (JDK) 17+

Apache Maven

MongoDB running on localhost:27017

Running the Application
There are two ways to run this application: for development or as a downloadable JAR file.

Development Mode
Clone the repository or save the provided files into a new project.

Ensure a MongoDB instance is running on mongodb://localhost:27017 and that it contains a database named maxxenergy with a collection named solar_plants. This collection should have at least one document with an isPublic field set to true.

Build and run the application using Maven:

mvn spring-boot:run

The application will start on http://localhost:8080.

Running as a Downloadable JAR
Follow steps 1 and 2 from the Development Mode instructions above.

Package the application into a standalone JAR file using Maven:

mvn clean package

This command will create a file named something like edap-0.0.1-SNAPSHOT.jar in the target/ directory.

Run the JAR file:

java -jar target/edap-0.0.1-SNAPSHOT.jar

The application will now be running on http://localhost:8080 as a self-contained executable.

🖥️ API Endpoints
The application exposes one public endpoint for retrieving solar data.

GET /api/public/data
Description: Retrieves data for the first solar plant marked as public in the database.

Response: A JSON object containing the plant's name, current generation, and revenue.

Example Response:

{
  "plantName": "Example Solar Plant",
  "generation": 1500.5,
  "revenue": 750.25
}

Notes:

The service connects to a MongoDB database named maxxenergy and queries the solar_plants collection.

It specifically looks for a document where isPublic is set to true.

⚙️ Configuration
The application includes a WebConfig.java file to handle CORS (Cross-Origin Resource Sharing). This allows the API to be accessed from different origins, which is useful for front-end development.

Allowed Origins:

http://localhost:3000

http://127.0.0.1:5500

file://

📂 Project Structure
Main.java: The main entry point for the Spring Boot application.

SolarDataController.java: Defines the REST API endpoint /api/public/data.

SolarDataService.java: Contains the business logic to fetch data from MongoDB.

SolarData.java: The model class representing the solar data object.

WebConfig.java: Configures CORS settings for the application.
