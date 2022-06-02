import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../product/rabbitmq/ProductStockUpdateSender.js";
import { BAD_REQUEST, INTERNAL_SERVER_ERROR, SUCCESS } from "../../../config/constants/HttpStatus.js";
import { PENDING } from "../status/OrderStatus.js";
import OrderException from "../exceptions/OrderExcption.js";
import ProductClient from "../../product/rabbitmq/client/ProductClient.js";

class OrderService {

    async createOrder(req) {
        try {
            console.log("LOOOOGGGCreate order " + JSON.stringify(req.headers));
            const { transactionid, serviceid } = req.headers;
            let orderData = req.body;
            console.info(`Request to POST order with data ${JSON.stringify(orderData)} | [transactionid: ${transactionid} | serviceid: ${serviceid}]`)
            this.validateOrderData(orderData);
            const { authUser } = req;
            const { authorization } = req.headers;
            let order = this.craeteInitialOrderData(orderData, authUser, transactionid, serviceid)
            await this.validiateProductStock(orderData, authorization, transactionid);
            let createdOrder = await OrderRepository.save(order)
            this.sendMesasge(createdOrder, transactionid);
            let response = {
                status: SUCCESS,
                createdOrder
            }
            console.info(`Response to POST order with data ${JSON.stringify(response)} | [transactionid: ${transactionid} | serviceid: ${serviceid}]`)
            return response;
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    craeteInitialOrderData(orderData, authUser, transactionid, serviceid) {
        return {
            status: PENDING,
            user: authUser,
            createdAt: new Date(),
            updatedAt: new Date(),
            products: orderData.products,
            transactionid,
            serviceid
        }

    }

    sendMesasge(createdOrder, transactionid) {
        const message = {
            salesId: createdOrder.id,
            products: createdOrder.products,
            transactionid
        }
        sendMessageToProductStockUpdateQueue(message);
    }

    async updateOrder(orderMessage) {
        try {
            const order = JSON.parse(orderMessage);
            if (!order.salesId || !order.status) {
                console.warn("The order message was not complete");
                return;
            }
            let existingOrder = await OrderRepository.findById(order.salesId);
            if (existingOrder && order.status !== existingOrder.status) {
                existingOrder.status = order.status;
                existingOrder.updatedAt = new Date();
                await OrderRepository.save(existingOrder)
            }
        } catch (error) {
            console.error(`Could not parse order message queue. Error: ${error.message}`)
        }
    }

    validateOrderData(data) {
        if (!data || !data.products) {
            throw new OrderException(BAD_REQUEST, "The products must be informed")
        }
    }

    async validiateProductStock(order, token, transactionid) {
        let stockIsOk = await ProductClient.checkProductStock(order, token, transactionid);
        if (!stockIsOk) {
            throw new OrderException(BAD_REQUEST, "The stock is out for the products")
        }
    }

    async findById(req) {

        try {
            const { transactionid, serviceid } = req.headers;
            const { id } = req.params;
            console.info(`Request to GET order by ID ${id}| [transactionid: ${transactionid} | serviceid ${serviceid}]`)
            this.validateInformedId(id);
            const existingOrder = await OrderRepository.findById(id);
            if (!existingOrder) {
                throw new OrderException(BAD_REQUEST, "The order was not found")
            }
            let response = {
                status: SUCCESS,
                existingOrder
            }
            console.info(`Response to GET order by ID ${id} | ${JSON.stringify(response)}| [transactionid: ${transactionid} | serviceid ${serviceid}`)
            return response;
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async findAll(req) {
        try {
            const { transactionid, serviceid } = req.headers;
            console.info(`Request to GET all orders [transactionid: ${transactionid} | serviceid ${serviceid}`)
            const orders = await OrderRepository.findAll();
            let response = {
                status: SUCCESS,
                orders
            }
            console.info(`Response to GET all orders ${JSON.stringify(response)} [transactionid: ${transactionid} | serviceid ${serviceid}`)
            return response;
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async findByProductId(req) {
        try {
            const { transactionid, serviceid } = req.headers;
            const { productId } = req.params;
            console.info(`Request to GET find by product id ${productId} [transactionid: ${transactionid} | serviceid ${serviceid}`)
            this.validateInformedProductId(productId);
            const orders = await OrderRepository.findByProductId(productId)
            let response = {
                status: SUCCESS,
                salesIds: orders.map(order => {
                    return order.id
                })
            }
            console.info(`Response to GET find by productid ${productId} | ${JSON.stringify(response)} id [transactionid: ${transactionid} | serviceid ${serviceid}`)
            return response;
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    validateInformedId(id) {
        if (!id) {
            throw new OrderException(BAD_REQUEST, "The order ID must be informed");
        }
    }

    validateInformedProductId(id) {
        if (!id) {
            throw new OrderException(BAD_REQUEST, "The product ID must be informed");
        }
    }
}

export default new OrderService();
