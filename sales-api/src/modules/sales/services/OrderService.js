import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../product/rabbitmq/ProductStockUpdateSender.js";
import { BAD_REQUEST, INTERNAL_SERVER_ERROR, SUCCESS } from "../../../config/constants/HttpStatus.js";
import { PENDING } from "../status/OrderStatus.js";
import OrderException from "../exceptions/OrderExcption.js";
import ProductClient from "../../product/rabbitmq/client/ProductClient.js";

class OrderService {

    async createOrder(req) {
        try {
            let orderData = req.body;
            console.log(`Create order ${JSON.stringify(orderData)}`)
            this.validateOrderData(orderData);
            const { authUser } = req;
            const { authorization } = req.headers;
            let order = this.craeteInitialOrderData(orderData, authUser)
            await this.validiateProductStock(orderData, authorization);
            let createdOrder = await OrderRepository.save(order)
            this.sendMesasge(createdOrder);
            return {
                status: SUCCESS,
                createdOrder
            }
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    craeteInitialOrderData(orderData, authUser) {
        return {
            status: PENDING,
            user: authUser,
            createdAt: new Date(),
            updatedAt: new Date(),
            products: orderData.products
        }

    }

    sendMesasge(createdOrder) {
        const message = {
            salesId: createdOrder.id,
            products: createdOrder.products
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

    async validiateProductStock(order, token) {
        let stockIsOk = await ProductClient.checkProductStock(order, token);
        if (!stockIsOk) {
            throw new OrderException(BAD_REQUEST, "The stock is out for the products")
        }
    }

    async findById(req) {

        try {
            const { id } = req.params;
            this.validateInformedId(id);
            const existingOrder = await OrderRepository.findById(id);
            if (!existingOrder) {
                throw new OrderException(BAD_REQUEST, "The order was not found")
            }
            return {
                status: SUCCESS,
                existingOrder
            }
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async findAll() {
        try {
            const orders = await OrderRepository.findAll();
            return {
                status: SUCCESS,
                orders
            }
        } catch (error) {
            return {
                status: error.status ? error.status : INTERNAL_SERVER_ERROR,
                message: error.message
            }
        }
    }

    async findByProductId(req) {
        try {
            const { productId } = req.params;
            this.validateInformedProductId(productId);
            const orders = await OrderRepository.findByProductId(productId)
            return {
                status: SUCCESS,
                salesIds: orders.map(order => {
                    return order.id
                })
            }
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
