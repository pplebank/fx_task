# FX Task Application
FX Task Application is a Java application that processes market price data from a CSV file and saves it to a database. It provides functionality to parse the CSV data, adjust bid and ask prices, and store the market prices in the database.

# Features
- Parse CSV data: The application can read market price data from a CSV file and convert it into MarketPrice objects.
- Adjust bid and ask prices: The application can adjust the bid and ask prices of market records based on specific calculations.
- Save market prices: The application can save the adjusted market prices to a database using the MarketPriceRepository.
- 
# Technologies Used
- Java
- Spring Framework
- Spring Data
- Mockito (for testing)

# Build

In db folder, there is file compose.yaml. In this way, there is possible to run db with db panel on same machine as application. Also, in application properties, url to db is set. Application will not work without db.
Also, there is script database-init-schema.sql, which adds example data and put index on instrument name column. It's due that application often search through this column.

# Usage
To use the FX Task Application, follow these steps:

- Prepare a CSV file with market price data. Each row should contain the following information: instrument name, bid price, ask price, and time.
- Invoke the insertCSVRecords in Service, or send via POST method (CSV needs to be sent as a String)
- The application will parse the CSV file in String , adjust the bid and ask prices, and save the market prices to the database.
- Use endpoint to obtain the latest record, sending in GET request particular name like EURUSD, PLNUSD
- IMPORTANT! don't send with slash (/) in address, as it is not read properly
- You can also obtain all latest records with GET method (without specifying particular currency). It returns the latest record of every type of instrument.

# Testing
The FX Task Application includes automated tests to verify its functionality. The tests are written using the Mockito framework for mocking dependencies. Tests are independent of db. Also, manual tests with postman were done.

# Assumptions

As it's forex project, i put BigDecimal type for ask and bid prices. Therefore, values of prices are having bigger/longer precise (looks that after float point, there are more digits than default 2 )
As a commission, the values in db are real (unmodified), but as for the client, they are adjusted (-0,1%/+0,1%). I thought it is more logical, to store real values, and returning with commission included.
Normally, i would also consider implementing WebSocket, as usually in FX, there is a need for real time records. In this way, would be much faster than typical REST communication. Also, I would implement application more as microservices,
to isolate connection with DB and on the user endpoint application use reactive programming (webFlux).

For the test version, I put in properties line : spring.jpa.hibernate.ddl-auto=create, which causes that whenever application is running, it creates for new whole schema (tables), and also is dropping all existing records