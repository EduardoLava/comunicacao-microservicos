import Order from "../../modules/sales/model/Order.js";
import { v4 as uuidv4 } from "uuid";

export async function createInitialData() {
    try {
        await Order.collection.drop();
    } catch (err) {
    }
    await Order.create({
        products: [
            {
                productId: 1,
                quantity: 3
            },
            {
                productId: 2,
                quantity: 1
            },
            {
                productId: 3,
                quantity: 1
            }
        ],
        user: {
            id: "sfddf156sadf165dasf561",
            name: "User Test",
            email: "usertest@gmail.com"
        },
        status: 'APPROVED',
        createdAt: new Date(),
        updatedAt: new Date(),
        transactionid: uuidv4(),
        serviceid: uuidv4()
    })

    await Order.create({
        products: [
            {
                productId: 1,
                quantity: 1
            },
            {
                productId: 2,
                quantity: 4
            }
        ],
        user: {
            id: "1sdf561a56fd1as",
            name: "User Test 2",
            email: "usertest2@gmail.com"
        },
        status: 'REJECTED',
        createdAt: new Date(),
        updatedAt: new Date(),
        transactionid: uuidv4(),
        serviceid: uuidv4()
    })

    let test = await Order.find();
    console.info("Info data was created: " + JSON.stringify(test, undefined, 4));
}