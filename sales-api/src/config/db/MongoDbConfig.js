import mongoose from "mongoose";
import { MONGO_DB_URL } from "../constants/Secrets.js";

export function connectMongoDb() {
    console.log("connect Mongo " + MONGO_DB_URL)
    mongoose.connection.on('connected', function () {
        console.info("The application connected to MongoDB successfully!")
    });
    mongoose.connection.on('error', function () {
        console.error("Error to connect to MongoDB!")
    });
    mongoose.connect(MONGO_DB_URL, {
        useNewUrlParser: true
    });
}
