## Running the Application
### Pre-Requisites
- **The only requirement for running the application in Production mode is that you have Docker installed.**
- If you wish to run in Developer mode, you must have the following installed as well to be able to build and serve the application yourself:
	- JDK 21
	- NodeJs 20
### Running in Production Mode
1. Open up a terminal.
2. Change directory into the repository's root ("nory-inventory-manager-challenge").
3. Open up the "**compose.yaml**" file and look for the **ACTIVE_LOCATION_ID** env variable defined in the backend container. Set this to whatever location ID you want (from the available data) to run the app/load data for. It is set to "1" by default. Also make sure you do not have anything currently running on port 80 or 5432 (for Postgres).
4. Run "**docker compose up**" to spin up the containers and wait for them to start up.
5. The data for your chosen location should be loaded now, but note that if you wish to change the location after spinning up the containers, you will need to shut down the app, delete the storage volume that Postgres is using, then start the containers back up. This is because the data migration on start-up won't run if there is any existing data.
6. Access the application @ http://localhost
### Running in Developer Mode
1. Run "**docker compose up**" on the "compose-db.yaml" file within the "api" directory, this will  spin up a container for the Developer database.
2. Start up the API project by running "" within the project directory. Ensure the ACTIVE_LOCATION_ID env variable has been set.
3. Start up the Client project by running "" within the project directory.
4. Access the application @ http://localhost:4200
### Verifying the Behaviour of the Product
You should be able to validate all the capabilities by interacting with the product directly as every change to the inventory is tracked as audit logs which are visible to the manager in the reporting page. Log in and make some sales, accept deliveries, and do some stock corrections then take a look at the audit logs and current inventory through the app. I recommend logging in as someone with the Manager role (Set the location Id as 1 and, StaffId: 115) as they would have access to all functionality. 
#### Connecting to the DB separately
If you would like to connect to the Postgres DB directly, you can use the details in the "compose.yaml" file to connect to the "Production" DB while the application is running. If you want to connect to the Developer database, use the details in the "compose-db.yaml" file in the "api" directory.
#### Using the API separately
If you'd like to hit any of the API endpoints on their own without the frontend, I've provided a postman collection in the root of the repository. Just remember that you need to pass the location ID and the staff member ID as headers for the request to be successful.
## Developer Commentary
### Assumptions
- The network that the application is running on is secure and only accessible by employees of the brand.
- There are no malicious actors among the employees that might for instance log in as someone else to cause chaos (i.e. password authentication is not important right now).
- Any member of staff at a particular location can perform a stocktake.
- Modifiers are applied by the staff member during the sale, adding an extra ingredient will increase the cost and expend one additional unit of the ingredient, whereas adding an allergen doesn't really do anything as they don't have a cost, its just for tracking purposes.
- "Waste" occurs when a staff member performs a stocktake and counts less of an ingredient than what we have in our system (due to spillage, spoiled goods). If for some reason the counted stock is above what we have then this will be treated as a "Correction" as far as auditing goes.
- The locations all use the Euro as their currency (for display purposes only).
### Prioritisation
I started this project with the assumption that I wouldn't be able to accomplish every single goal within the timeframe, and that this would be only the first iteration of a fictional product. In the end my priorities list of capabilities looked like this:
1. Data modelling & loading existing location data. (ACHIEVED)
2. Ability for any staff to log in to the app using their staff ID. (ACHIEVED)
3. Ability for any staff to view current stock and make stock corrections. (ACHIEVED)
4. Ability for managers to view the monthly audit logs for their inventory. (ACHIEVED)
5. Ability for chefs/BOH staff to accept deliveries. (ACHIEVED)
6. Ability to FOH staff to make a sale. (ACHIEVED)
7. Ability for FOH staff to add modifiers to the menu items they sell. (PARTIALLY ACHIEVED)
8. Ability for managers to view the monthly summary report on costs and sales. (PARTIALLY ACHIEVED)
I prioritised the data modelling/loading so highly because it is the foundation to all the other systems and the application itself is supposed to be data-driven, so it made sense to prioritise that first and spend a decent amount of time on it. I knew from experience that by designing a strong and accurate data model early on you can speed up development down the line as developers won't need to spend as much time figuring out DB relationships or the ORM that you may be using, they can instead focus on developing features for the customer since the groundwork has been laid. I think this ended up being the correct decision as I was able to develop a first iteration of all of the required capabilities.

Once the data model was in place and the sample data was loaded, the next big feature was some way to identify the user making the requests, in lieu of a proper username/password authentication system. This was an important capability as each action taken by the staff that could affect the inventory needed to be audited and tracked in the DB, with the staff's id and name attached to it. I settled on developing an interceptor that would look for the location id and staff id of the user making the request and use that to identify them. This was much simpler than implementing a proper secure authentication system, but when taking this app to production on the cloud, you certainly wouldn't do this without some form of auth.

Once all that was in place, I felt that the next most important thing was designing an auditable inventory system that could be corrected to account for any waste/miscounts and that the staff could actually view the up-to-date inventory. I considered this to be foundational piece of the product and I explain my decision in the next section, but this was what I worked on next. Following closely after that I needed a way for managers to actually make use of this data, which is why I prioritised the auditing log portion of the reporting page, to allow them to actually see the changes in their inventory.

From there I moved on to Deliveries as much of the logic from stocktaking could be re-used to allow staff to accept deliveries of ingredients too. I thought the benefit to the user was a good value proposition given the reduced  time investment. 

With my remaining time, I focused on getting as much done with sales as I could as well as finishing up the reporting page to give managers a monthly summary of the revenue/costs. The reason I left these to last is that these capabilities could theoretically be achieved already with existing functionality, but requiring more manual work from the user's end. For example, without the sales feature, the staff member could have used the existing stocktaking feature to correct the stock for sold ingredients. Similarly, managers could use the existing audit log functionality on the reporting page to tally up their costs and revenue, as well as the stocktaking feature to see their current inventory value (albeit with a lot of manual work or running queries directly in the DB which isn't exactly user friendly).
### Challenges Encountered & Decisions Made
While making the following decisions, I tried placing myself in the shoes of the staff member or manager that would end up using this product. This was helpful as it constantly reminded me that the product should be mobile-friendly (as it would be used on a mobile device), easy to use (as the staff aren't technical people), and accurate (there's no point buying a software product to manage your inventory if it can't accurately audit and apply changes to your stock!). 

With that in mind, here are some of the challenges I encountered and how I overcame them:
- Needed to figure out how to actually get the sample data into the app. Decided on a programmatic approach where data is loaded on start-up based on a supplied location id. I decided this approach over a manual one (or migration script) as that would eat up too much time and a programmatic approach can be extended later on into some kind of file upload for managers if they want to upload more ingredients for example.
- Had to break down the relationships of the data model more than I expected and had to handle a lot of many-to-many relationships. The context of the data necessitated using different ways of handling many-to-many relationships in my ORM (JPA/Hibernate) too, most could be handled with a simple link table but others (such as LocationIngredient) required a dedicated entity as there was information tracked about the relationship (ie. the quantity of the ingredient in stock at a given location).
- Figuring out how to track and apply changes to the inventory was the most interesting challenge and was of course a core feature of the product as well. I settled on an approach where I would use an InventoryAuditLog entity to track a single change to a particular ingredient. This entity would have fields for tracking who made the change, what time it happened at, what kind of change, as well as the difference it made to the quantity in stock of that ingredient (as well as the cost of that change). I then decided that a good way to make modifications to the inventory was to have all changes to the inventory be driven by these audit logs. This helps handle cases where multiple people are making changes to the stock at the same time, as the audit logs tell the system to change the quantity in stock by a given amount in a certain direction, rather than setting it directly to a new value that was computed by either the frontend or some other service. For example, if you create an audit log for the delivery of 2 Apples, then the system will apply a positive change of 2 units to the current quantity. Then I built in a safety mechanism to ensure any action that would bring an ingredient below 0 was rejected. 
- Spent a lot of time thinking we needed a proper authentication system, but then decided against it as one of the core assumptions was that it wasn't public facing and it was going to be hosted on a secure network, so I stook to the simple identity management system that I explained in the previous section.
- I noticed there  was no quantity in stock assigned to ingredients in the sample data so I had to create a new table to represent an Ingredient at a given Location (LocationIngredient), in which I assigned a default value for the quantity when loading the data.
- Found myself wanting to do a lot more with this (especially the reporting) but had to challenge myself to prioritise things appropriately instead of getting bogged down by any one feature. I described some of what I hoped to do in my "Magic Wand" section.
- When tallying up the revenue from sales and the cost of deliveries for reporting purposes, it was important to know what the cost/price was at that point in time, because the price of items on a menu or ingredients can change over time. That's why I decided to rely on the audit logs and SaleItems to track the cost/price of these items at the time the delivery/sale was made.
- My previous experience with stock taking is that you would be assigned to cover sections at a time, not the whole thing, so instead of presenting the staff member with a massive list of ingredients they need to check off, I opted for a solution where the staff member could search for a particular item at a time, the server would return the most up-to-date quantity info for that item, then they could make the required stock correction.
- Modifiers were very challenging to get over the line as sales was the last on my priorities list, I was able to add modifier support on the API but not on the frontend as it was taking too much time. I included this as one of my "Magic Wand" items.
- I initially thought to make the data loader load all data for all locations, but later on I decided to only load as the data is supposed to be kept completely separate according to the requirements. In a cloud-based production-scenario we would rely on an authentication mechanism to ensure staff cant access data that they're not supposed to, but this will suffice for a first iteration of the product.
### Tech Stack
#### Frontend
- **Angular:** easily extendable and scalable with modules, good ecosystem and talent pool, typescript support out of the box.
- **Bootstrap:** I prioritized capabilities and features far above ,  bootstrap allows me to keep the app looking simple while being responsive and mobile friendly (without writing a load of CSS).
#### Backend
- **Java w/ Spring Boot:** great ecosystem, battle-tested, wide talent pool, statically typed.
- **PostgreSQL:** The location data is relational so NoSQL is not really appropriate, if document storage later became necessary for something then this is already supports. Initially considered a SQLlite DB for a straightforward implementation, but decided on Postgres because with Docker the development process is , will also be easier to migrate this to production.
#### Other Technologies
- **Docker:** Containerization to allow the app to run on nearly any OS and also help spin up dependencies (like the DB) during development.
### Magic Wand Musings
These are the things I would have liked to have implemented if time was not an issue and I had a magic wand to bring them into existence:
- Implement password-based auth or use an identity manager like Auth0 or AWS Cognito for Staff authentication instead of allowing staff to login with just their staff ID.
- Finish building the capability for modifiers, currently it is partially implemented (API only) but would have been great to get it working end to end.
- Support for other brands w/ self service file upload for locations, staff, etc.
- Allow managers to view reports from previous months via a date-picker.
- Use a cache like Redis to cache reports from the previous month to reduce load on the API.
- Allow monthly reports to be exported as a PDF for record-keeping purposes. 
- I initially wanted to add components for tracking historical deliveries/sales/stocktakes but time did not permit. It would have been nice to have as relevant staff would be able to access a portion of the audit logs that apply to their role. In the current iteration this info is still technically available but only to the managers when viewing the monthly audit logs.
- The design/style of the product was kept simple to get as much functionality in as possible while maintaining mobile-friendliness. With a lot more time I would have applied a more personalized styling approach.
### Bringing to Production
These are just a couple of my errant thoughts around bringing this product to production that occurred to me while working on this first iteration, happy to discuss this further on the follow up call.
- Utilize Amazon S3 + Cloudfront to host the static frontend content in multiple locations for quick worldwide access.
- Use AWS Elastic Beanstalk for managing the scaling and load balancing of the backend API.
- Setup a blue-green deployment strategy on EB to prevent any downtime for the customers.
- The above approach using S3 + EB would allow us to keep our client and backend deployments separate, which is very useful if we end up having multiple client applications especially (e.g. a mobile app).
- Setup a reverse-proxy such as nginx to ensure the product is accessible through HTTPS.
- Purchase and set up a domain name for the product through a provider like AWS Route 53.
- Migrate to a cloud-managed instance of PostgreSQL using a service like AWS RDS for scalable infrastructure.
- Create a CI/CD pipeline with Github Actions or an equivalent to automatically run tests and deploy to EB/S3.
- When migrating to a cloud-based provider we would need to implement some core changes to the application such as:
	- Deciding and implementing an authentication strategy (could manage it ourself or offload to a managed provider like AWS Cognito/Auth0, etc).
