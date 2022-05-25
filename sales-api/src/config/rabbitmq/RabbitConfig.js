import amqp from 'amqplib/callback_api.js';

import { PRODUCT_TOPIC, PRODUCT_STOCK_UPDATE_QUEUE, PRODUCT_STOCK_UPDATE_ROUTING_KEY, SALES_CONFIRMATION_QUEUE, SALES_CONFIRMATION_ROUTING_KEY } from './Queue.js'
import { RABBIT_MQ_URL } from '../constants/Secrets.js';

const HALF_SECOND = 500;

export async function connectRabbitMq() {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if (error) {
            throw error;
        }
        craeteQueue(connection, PRODUCT_STOCK_UPDATE_QUEUE, PRODUCT_STOCK_UPDATE_ROUTING_KEY, PRODUCT_TOPIC)
        craeteQueue(connection, SALES_CONFIRMATION_QUEUE, SALES_CONFIRMATION_ROUTING_KEY, PRODUCT_TOPIC)
        setTimeout(function () {
            connection.close();
        }, HALF_SECOND)
    })

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
}