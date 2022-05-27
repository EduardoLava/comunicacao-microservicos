import express from "express";
import { connectMongoDb } from "./src/config/db/MongoDbConfig.js";
import { createInitialData } from "./src/config/db/InitialData.js";
import { connectRabbitMq } from "./src/config/rabbitmq/RabbitConfig.js";
import CheckToken from "./src/config/auth/CheckToken.js";
import router from "./src/modules/sales/routes/OrderRoutes.js";

const app = express();
const env = process.env;
const PORT = env.PORT || 8082;

connectMongoDb();
createInitialData();
connectRabbitMq();

app.get('/api/status', (req, res) => {
    return res.status(200).json({
        service: "Sales-api",
        status: "Up"
    })
})

app.use(express.json())
app.use(CheckToken)
app.use(router);

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`)
});