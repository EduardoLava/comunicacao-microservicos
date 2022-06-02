import express from "express";
import * as db from './src/config/db/InitialData.js'
import Tracing from "./src/config/Tracing.js";
import UserRoutes from './src/modules/user/routes/UserRoutes.js'


const app = express();
const env = process.env;
const PORT = env.PORT || 8080;
const CONTAINER_ENV = "container";

app.use(Tracing)

app.get('/api/status', (req, res) => {
    return res.status(200).json({
        service: "Auth-API",
        status: "up"
    })
});


app.use(express.json())
app.use(UserRoutes)

db.createInitialData();

/*function startApplication() {
    if (env.NODE_ENV !== CONTAINER_ENV) {
        db.createInitialData();
    }
}*/

app.listen(PORT, () => {
    console.log(`Server started successfully as port ${PORT}`);
})