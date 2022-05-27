import OrderService from "../services/OrderService.js";

class OrderController {

    async createOrder(req, res) {
        let order = await OrderService.createOrder(req)
        return res.status(order.status).json(order)
    }

    async findById(req, res) {
        let order = await OrderService.findById(req)
        return res.status(order.status).json(order)
    }

    async findByAll(req, res) {
        let order = await OrderService.findAll()
        return res.status(order.status).json(order)
    }

    async findByProductId(req, res) {
        let orders = await OrderService.findByProductId(req)
        return res.status(orders.status).json(orders)
    }
}

export default new OrderController();