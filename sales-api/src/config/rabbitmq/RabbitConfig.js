import amqp from 'amqplib/callback_api.js';

import { PRODUCT_TOPIC, PRODUCT_STOCK_UPDATE_QUEUE, PRODUCT_STOCK_UPDATE_ROUTING_KEY, SALES_CONFIRMATION_QUEUE, SALES_CONFIRMATION_ROUTING_KEY } from './Queue.js'
import { RABBIT_MQ_URL } from '../constants/Secrets.js';
import { listenToSalesConfirmationQueue } from '../../modules/sales/model/rabbitmq/SalesConfirmationListener.js';

const HALF_SECOND = 500;
const HALF_MINUTE = 30000;
const CONTAINER_ENV = "container";

export async function connectRabbitMq() {
    const ENV = process.env.NODE_ENV;

    console.log(`ENV: ${ENV}`)
    if (CONTAINER_ENV == ENV) {
        console.info("Waiting for RabbitMQ to start");
        setTimeout(() => {
            connectRabbitMqAndCreateQueues();
        }, HALF_MINUTE);
    } else {
        connectRabbitMqAndCreateQueues();
    }
}

function connectRabbitMqAndCreateQueues() {
    console.info("Connect to rabbit");
    amqp.connect(RABBIT_MQ_URL, { timeout: 180000 }, (error, connection) => {
        if (error) {
            throw error;
        }
        craeteQueue(
            connection,
            PRODUCT_STOCK_UPDATE_QUEUE,
            PRODUCT_STOCK_UPDATE_ROUTING_KEY,
            PRODUCT_TOPIC
        );
        craeteQueue(
            connection,
            SALES_CONFIRMATION_QUEUE,
            SALES_CONFIRMATION_ROUTING_KEY,
            PRODUCT_TOPIC
        );
        setTimeout(function () {
            connection.close();
            listenToSalesConfirmationQueue();
        }, HALF_SECOND)
    })
}

function craeteQueue(connection, queue, routingKey, topic) {
    connection.createChannel((error, chanel) => {
        if (error) {
            throw error;
        }
        chanel.assertExchange(topic, 'topic', { durable: true })
        chanel.assertQueue(queue, { durable: true })
        chanel.bindQueue(queue, topic, routingKey)
    })
}
