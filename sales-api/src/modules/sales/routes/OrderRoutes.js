import { Router } from "express";
import OrderController from "../controller/OrderController.js";

const router = new Router();

router.get('/api/order/:id', OrderController.findById);
router.get('/api/order', OrderController.findByAll);
router.get('/api/order/product/:productId', OrderController.findByProductId);
router.post('/api/order', OrderController.createOrder);


export default router;